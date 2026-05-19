package com.example.selenium.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class UserManagementPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By addButton           = By.cssSelector("button.oxd-button--secondary");
    private final By userRoleDropdown    = By.xpath("(//div[@class='oxd-select-text-input'])[1]");
    private final By statusDropdown      = By.xpath("(//div[@class='oxd-select-text-input'])[2]");
    private final By employeeNameInput   = By.cssSelector("input.oxd-input[placeholder='Type for hints...']");
    private final By usernameInput       = By.xpath("(//input[@class='oxd-input oxd-input--active'])[2]");
    private final By passwordInput       = By.xpath("(//input[@type='password'])[1]");
    private final By confirmPassInput    = By.xpath("(//input[@type='password'])[2]");
    private final By saveButton          = By.cssSelector("button[type='submit']");
    private final By successToast        = By.cssSelector(".oxd-toast--success");
    private final By tableRows           = By.cssSelector(".oxd-table-body .oxd-table-row");
    private final By deleteConfirmBtn    = By.xpath("//button[normalize-space()='Yes, Delete']");
    private final By searchUsernameInput = By.xpath("(//input[@class='oxd-input oxd-input--active'])[1]");
    private final By searchButton        = By.cssSelector("button[type='submit']");
    private final By noRecordsFound      = By.xpath("//span[contains(text(),'No Records Found')]");

    public UserManagementPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait   = wait;
    }

    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/web/index.php/admin/viewSystemUsers");
        wait.until(ExpectedConditions.visibilityOfElementLocated(addButton));
    }

    public void clickAdd() {
        wait.until(ExpectedConditions.elementToBeClickable(addButton)).click();
    }

    public void selectUserRole(String role) {
        wait.until(ExpectedConditions.elementToBeClickable(userRoleDropdown)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='option']//span[text()='" + role + "']")
        )).click();
    }

    public void selectStatus(String status) {
        wait.until(ExpectedConditions.elementToBeClickable(statusDropdown)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='option']//span[text()='" + status + "']")
        )).click();
    }

    public void setEmployeeName(String name) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(employeeNameInput));
        input.sendKeys(name);
        By suggestion = By.cssSelector(".oxd-autocomplete-option span");
        wait.until(ExpectedConditions.visibilityOfElementLocated(suggestion)).click();
    }

    public void setUsername(String username) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(usernameInput));
        input.clear();
        input.sendKeys(username);
    }

    public void setPassword(String password) {
        driver.findElement(passwordInput).sendKeys(password);
    }

    public void setConfirmPassword(String password) {
        driver.findElement(confirmPassInput).sendKeys(password);
    }

    public void clickSave() {
        driver.findElement(saveButton).click();
    }

    public boolean isSuccessToastDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(successToast));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

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

    public void searchByUsername(String username) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(searchUsernameInput));
        input.clear();
        input.sendKeys(username);
        driver.findElement(searchButton).click();
    }

    public void deleteUserByUsername(String username) {
        searchByUsername(username);
        wait.until(ExpectedConditions.visibilityOfElementLocated(tableRows));
        By deleteIcon = By.xpath("//div[@class='oxd-table-cell-actions']/button[2]");
        wait.until(ExpectedConditions.elementToBeClickable(deleteIcon)).click();
        wait.until(ExpectedConditions.elementToBeClickable(deleteConfirmBtn)).click();
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
        return driver.findElements(By.cssSelector(".oxd-input-field-error-message"));
    }
}