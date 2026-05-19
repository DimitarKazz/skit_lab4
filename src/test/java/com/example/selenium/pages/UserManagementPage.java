package com.example.selenium.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class UserManagementPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ---- List page ----
    private final By addButton = By.xpath(
            "//button[normalize-space()='Add']");

    private final By searchUsernameInput = By.xpath(
            "(//input[contains(@class,'oxd-input--active')])[1]");

    private final By searchButton = By.xpath(
            "//button[normalize-space()='Search']");

    private final By tableRows = By.cssSelector(
            ".oxd-table-body .oxd-table-row");

    private final By noRecordsFound = By.xpath(
            "//*[contains(text(),'No Records Found')]");

    private final By deleteConfirmBtn = By.xpath(
            "//button[normalize-space()='Yes, Delete']");

    // ---- Add User form ----
    // User Role — first oxd-select on the form page
    private final By userRoleDropdown = By.xpath(
            "(//div[contains(@class,'oxd-select-text')])[1]");

    // Status — second oxd-select on the form page
    private final By statusDropdown = By.xpath(
            "(//div[contains(@class,'oxd-select-text')])[2]");

    // Employee Name autocomplete — wait for form URL before using this
    private final By employeeNameInput = By.xpath(
            "//input[@placeholder='Type for hints...']");

    // Username — 3rd active input on the Add User form
    private final By usernameInput = By.xpath(
            "(//input[contains(@class,'oxd-input--active')])[3]");

    // Password fields
    private final By passwordInput = By.xpath(
            "(//input[@type='password'])[1]");

    private final By confirmPassInput = By.xpath(
            "(//input[@type='password'])[2]");

    // Save button on form
    private final By saveButton = By.xpath(
            "//button[normalize-space()='Save']");

    // Success toast
    private final By successToast = By.cssSelector(".oxd-toast--success");

    // Validation error messages  — OrangeHRM uses <span> with this class
    private final By validationErrors = By.cssSelector(
            "span.oxd-input-field-error-message");

    // -----------------------------------------------------------------------

    public UserManagementPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait   = wait;
    }

    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/web/index.php/admin/viewSystemUsers");
        wait.until(ExpectedConditions.visibilityOfElementLocated(addButton));
    }

    // -----------------------------------------------------------------------
    // Add User
    // -----------------------------------------------------------------------

    public void clickAdd() {
        wait.until(ExpectedConditions.elementToBeClickable(addButton)).click();
        // Wait until the Add-User form has loaded (URL changes to saveSystemUser)
        wait.until(ExpectedConditions.urlContains("saveSystemUser"));
        // Extra pause for Vue components to render
        wait.until(ExpectedConditions.visibilityOfElementLocated(employeeNameInput));
    }

    public void selectUserRole(String role) {
        wait.until(ExpectedConditions.elementToBeClickable(userRoleDropdown)).click();
        By option = By.xpath(
                "//div[@role='listbox']//span[normalize-space()='" + role + "']");
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
    }

    public void selectStatus(String status) {
        wait.until(ExpectedConditions.elementToBeClickable(statusDropdown)).click();
        By option = By.xpath(
                "//div[@role='listbox']//span[normalize-space()='" + status + "']");
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
    }

    public void setEmployeeName(String hint) {
        WebElement input = wait.until(
                ExpectedConditions.elementToBeClickable(employeeNameInput));
        input.clear();
        input.sendKeys(hint);
        // Wait for autocomplete dropdown
        By suggestion = By.cssSelector(
                ".oxd-autocomplete-dropdown .oxd-autocomplete-option");
        wait.until(ExpectedConditions.visibilityOfElementLocated(suggestion));
        driver.findElements(suggestion).get(0).click();
    }

    public void setUsername(String username) {
        WebElement input = wait.until(
                ExpectedConditions.elementToBeClickable(usernameInput));
        input.clear();
        input.sendKeys(username);
    }

    public void setPassword(String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput))
                .sendKeys(password);
    }

    public void setConfirmPassword(String password) {
        driver.findElement(confirmPassInput).sendKeys(password);
    }

    public void clickSave() {
        wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();
    }

    /** Full add-user flow. */
    public void addUser(String role, String status, String employeeHint,
                        String username, String password) {
        clickAdd();
        selectUserRole(role);
        selectStatus(status);
        setEmployeeName(employeeHint);
        setUsername(username);
        setPassword(password);
        setConfirmPassword(password);
        clickSave();
    }

    public boolean isSuccessToastDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(successToast));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    // -----------------------------------------------------------------------
    // Delete User
    // -----------------------------------------------------------------------

    public void searchByUsername(String username) {
        WebElement input = wait.until(
                ExpectedConditions.elementToBeClickable(searchUsernameInput));
        input.clear();
        input.sendKeys(username);
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
    }

    public void deleteUserByUsername(String username) {
        searchByUsername(username);
        wait.until(ExpectedConditions.visibilityOfElementLocated(tableRows));

        // Delete button is the second button in the actions cell of the matching row
        By deleteBtn = By.xpath(
                "//div[contains(@class,'oxd-table-row')]" +
                "[.//div[normalize-space()='" + username + "']]" +
                "//button[2]");
        wait.until(ExpectedConditions.elementToBeClickable(deleteBtn)).click();
        wait.until(ExpectedConditions.elementToBeClickable(deleteConfirmBtn)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(deleteConfirmBtn));
    }

    public boolean isUserVisible(String username) {
        searchByUsername(username);
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(tableRows));
            return driver.findElements(tableRows).stream()
                    .anyMatch(r -> r.getText().contains(username));
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isNoRecordsDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(noRecordsFound));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public List<WebElement> getValidationErrors() {
        // Give the form a moment to show errors after clicking Save
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        try {
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(validationErrors));
        } catch (TimeoutException ignored) { }
        return driver.findElements(validationErrors);
    }
}
