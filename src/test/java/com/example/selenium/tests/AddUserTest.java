package com.example.selenium.tests;

import com.example.selenium.base.BaseTest;
import com.example.selenium.pages.LoginPage;
import com.example.selenium.pages.UserManagementPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Add User Tests — OrangeHRM")
class AddUserTest extends BaseTest {

    private LoginPage loginPage;
    private UserManagementPage userManagementPage;

    // Real employee from the demo database
    private static final String EXISTING_EMPLOYEE = "Sam";

    @BeforeEach
    void login() {
        loginPage = new LoginPage(driver, wait);
        userManagementPage = new UserManagementPage(driver, wait);

        loginPage.navigateTo(BASE_URL);
        loginPage.loginAs(ADMIN_USERNAME, ADMIN_PASSWORD);
        userManagementPage.navigateTo(BASE_URL);
    }

    @Test
    @DisplayName("Successfully add a new ESS user")
    void testAddUserSuccess() {
        String username = "testuser_" + System.currentTimeMillis();

        userManagementPage.addUser("ESS", "Enabled", EXISTING_EMPLOYEE,
                username, "Test@12345");

        assertTrue(userManagementPage.isSuccessToastDisplayed(),
                "Success toast should appear after adding a user");
    }

    @Test
    @DisplayName("Successfully add a new Admin user")
    void testAddAdminUserSuccess() {
        String adminUsername = "admintest_" + System.currentTimeMillis();

        userManagementPage.addUser("Admin", "Enabled", EXISTING_EMPLOYEE,
                adminUsername, "Admin@12345");

        assertTrue(userManagementPage.isSuccessToastDisplayed(),
                "Success toast should appear after adding an admin user");
    }

    @Test
    @DisplayName("Add user with Disabled status")
    void testAddDisabledUser() {
        String disabledUsername = "disabled_" + System.currentTimeMillis();

        userManagementPage.addUser("ESS", "Disabled", EXISTING_EMPLOYEE,
                disabledUsername, "Test@12345");

        assertTrue(userManagementPage.isSuccessToastDisplayed(),
                "Success toast should appear for a disabled user");
    }

    @Test
    @DisplayName("Validation errors appear when saving empty form")
    void testAddUserEmptyFormShowsErrors() {
        userManagementPage.clickAdd();
        userManagementPage.clickSave();

        assertFalse(userManagementPage.getValidationErrors().isEmpty(),
                "Validation errors should be shown for an empty form");
    }

    @Test
    @DisplayName("Added user is visible in the user list")
    void testAddedUserAppearsInList() {
        String username = "visible_" + System.currentTimeMillis();

        userManagementPage.addUser("ESS", "Enabled", EXISTING_EMPLOYEE,
                username, "Test@12345");
        assertTrue(userManagementPage.isSuccessToastDisplayed());

        userManagementPage.navigateTo(BASE_URL);

        assertTrue(userManagementPage.isUserVisible(username),
                "Newly created user should be searchable in the list");
    }
}
