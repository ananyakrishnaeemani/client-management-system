package com.advisor.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void login(String email, String password) {
        // Inputs are bound via reactive forms (no stable HTML ids).
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[formcontrolname='email'], input[formControlName='email']")
        ));
        WebElement passwordInput = driver.findElement(
                By.cssSelector("input[formcontrolname='password'], input[formControlName='password']")
        );

        emailInput.clear();
        emailInput.sendKeys(email);

        passwordInput.clear();
        passwordInput.sendKeys(password);

        WebElement signInBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit' and (contains(.,'Sign In') or contains(@class,'auth-btn'))]")
        ));
        signInBtn.click();
    }
}

