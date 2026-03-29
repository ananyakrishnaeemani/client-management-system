package com.advisor.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ClientDetailsPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public ClientDetailsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public boolean isClientNameDisplayed(String clientName) {
        try {
            WebElement h2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(.,'Client Profile:') or contains(.,'Client Profile')]")));
            return h2.getText().contains(clientName);
        } catch (Exception e) {
            return false;
        }
    }
}

