package com.example.selenium.tests;

import com.example.selenium.base.BaseTest;
import com.example.selenium.pages.LoginPage;
import com.example.selenium.pages.UserManagementPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Delete User Tests — OrangeHRM")
class DeleteUserTest extends BaseTest {

    private LoginPage loginPage;
    private UserManagementPage userManagementPage;

    private static final String EXISTING_EMPLOYEE = "Tim";

    @BeforeEach
    void login() {
        loginPage = new LoginPage(driver, wait);
        userManagementPage = new UserManagementPage(driver, wait);

        loginPage.navigateTo(BASE_URL);
        loginPage.loginAs(ADMIN_USERNAME, ADMIN_PASSWORD);
        userManagementPage.navigateTo(BASE_URL);
    }

    @Test
    @DisplayName("Successfully delete an existing user")
    void testDeleteUserSuccess() {
        String username = "todelete_" + System.currentTimeMillis();
        userManagementPage.addUser("ESS", "Enabled", EXISTING_EMPLOYEE,
                username, "Test@12345");
        assertTrue(userManagementPage.isSuccessToastDisplayed());

        userManagementPage.navigateTo(BASE_URL);
        userManagementPage.deleteUserByUsername(username);

        assertTrue(userManagementPage.isNoRecordsDisplayed()
                        || !userManagementPage.isUserVisible(username),
                "User should no longer be present after deletion");
    }

    @Test
    @DisplayName("Deleted user does not appear in search results")
    void testDeletedUserNotSearchable() {
        String username = "searchcheck_" + System.currentTimeMillis();

        userManagementPage.addUser("ESS", "Enabled", EXISTING_EMPLOYEE,
                username, "Test@12345");
        assertTrue(userManagementPage.isSuccessToastDisplayed());

        userManagementPage.navigateTo(BASE_URL);
        userManagementPage.deleteUserByUsername(username);

        userManagementPage.navigateTo(BASE_URL);
        assertFalse(userManagementPage.isUserVisible(username),
                "Deleted user should not appear in search");
    }

    @Test
    @DisplayName("Delete multiple users one by one")
    void testDeleteMultipleUsers() {
        String username1 = "multi1_" + System.currentTimeMillis();
        String username2 = "multi2_" + System.currentTimeMillis();

        userManagementPage.addUser("ESS", "Enabled", EXISTING_EMPLOYEE,
                username1, "Test@12345");
        assertTrue(userManagementPage.isSuccessToastDisplayed());

        userManagementPage.navigateTo(BASE_URL);

        userManagementPage.addUser("ESS", "Enabled", EXISTING_EMPLOYEE,
                username2, "Test@12345");
        assertTrue(userManagementPage.isSuccessToastDisplayed());

        userManagementPage.navigateTo(BASE_URL);
        userManagementPage.deleteUserByUsername(username1);

        userManagementPage.navigateTo(BASE_URL);
        userManagementPage.deleteUserByUsername(username2);

        assertFalse(userManagementPage.isUserVisible(username1));
        assertFalse(userManagementPage.isUserVisible(username2));
    }
}