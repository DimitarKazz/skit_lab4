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

    // Confirm delete dialog
    private final By deleteConfirmBtn = By.xpath(
            "//button[normalize-space()='Yes, Delete']");

    // ---- Add User form ----
    // User Role — 1st oxd-select on the form
    private final By userRoleDropdown = By.xpath(
            "(//div[contains(@class,'oxd-select-text')])[1]");

    // Status — 2nd oxd-select on the form
    private final By statusDropdown = By.xpath(
            "(//div[contains(@class,'oxd-select-text')])[2]");

    // Employee Name autocomplete — has unique placeholder
    private final By employeeNameInput = By.xpath(
            "//input[@placeholder='Type for hints...']");

    // Username — the only plain oxd-input--active that is NOT the autocomplete
    // On the Add User form the autocomplete uses a different wrapper, so index [1] is username
    private final By usernameInput = By.xpath(
            "//label[normalize-space()='Username']/following::input[1]");

    // Password fields
    private final By passwordInput = By.xpath(
            "(//input[@type='password'])[1]");

    private final By confirmPassInput = By.xpath(
            "(//input[@type='password'])[2]");

    // Save button on the form
    private final By saveButton = By.xpath(
            "//button[normalize-space()='Save']");

    // Success toast
    private final By successToast = By.cssSelector(".oxd-toast--success");

    // Validation errors — OrangeHRM renders these as <span>
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
        // Wait for the Add-User form URL
        wait.until(ExpectedConditions.urlContains("saveSystemUser"));
        // Wait for the employee autocomplete input to be visible (form is fully rendered)
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
        // Make sure we are on the list page
        wait.until(ExpectedConditions.urlContains("viewSystemUsers"));
        WebElement input = wait.until(
                ExpectedConditions.elementToBeClickable(searchUsernameInput));
        input.clear();
        input.sendKeys(username);
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
    }

    public void deleteUserByUsername(String username) {
        searchByUsername(username);
        wait.until(ExpectedConditions.visibilityOfElementLocated(tableRows));

        // button[1] = trash/delete icon  |  button[2] = pencil/edit icon
        By deleteBtn = By.xpath(
                "//div[contains(@class,'oxd-table-row')]" +
                "[.//div[normalize-space()='" + username + "']]" +
                "//button[1]");
        wait.until(ExpectedConditions.elementToBeClickable(deleteBtn)).click();

        // Confirm the modal dialog
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
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        try {
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(validationErrors));
        } catch (TimeoutException ignored) { }
        return driver.findElements(validationErrors);
    }
}
