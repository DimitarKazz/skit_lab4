package com.example.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By usernameInput   = By.name("username");
    private final By passwordInput   = By.name("password");
    private final By loginButton     = By.cssSelector("button[type='submit']");
    private final By errorMessage    = By.cssSelector(".oxd-alert-content-text");
    private final By dashboardHeader = By.cssSelector(".oxd-topbar-header-breadcrumb");

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait   = wait;
    }

    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/web/index.php/auth/login");
    }

    public void enterUsername(String username) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
        field.clear();
        field.sendKeys(username);
    }

    public void enterPassword(String password) {
        driver.findElement(passwordInput).sendKeys(password);
    }

    public void clickLogin() {
        driver.findElement(loginButton).click();
    }

    public void loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardHeader));
    }

    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).getText();
    }
}