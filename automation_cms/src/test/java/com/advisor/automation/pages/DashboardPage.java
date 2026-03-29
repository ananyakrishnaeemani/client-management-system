package com.advisor.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class DashboardPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
    }

    public void clickAddClient() {
        WebElement addClientButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("addClientBtn"))
        );
        addClientButton.click();
    }

    public boolean isClientInTable(String clientName) {
        return getClientRow(clientName) != null;
    }

    private WebElement getClientRow(String clientName) {
        // Re-query everything fresh each call — Angular re-renders the table after mutations
        // which causes StaleElementReferenceException if we hold old references.
        try {
            for (WebElement table : driver.findElements(By.cssSelector("table"))) {
                for (WebElement row : table.findElements(By.cssSelector("tbody tr"))) {
                    try {
                        String text = row.getText();
                        if (text != null && text.contains(clientName)) {
                            return row;
                        }
                    } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
                        // Row was removed mid-iteration (e.g. after delete) — treat as not found
                    }
                }
            }
        } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
            // Table itself was re-rendered — treat as not found, caller will retry
        }
        return null;
    }

    public void clickViewForClient(String clientName) {
        WebElement row = requireClientRow(clientName);
        WebElement btn = row.findElement(By.cssSelector("button[aria-label='View client']"));
        wait.until(ExpectedConditions.elementToBeClickable(btn)).click();
    }

    public void clickEditForClient(String clientName) {
        WebElement row = requireClientRow(clientName);
        WebElement btn = row.findElement(By.cssSelector("button[aria-label='Edit client']"));
        wait.until(ExpectedConditions.elementToBeClickable(btn)).click();
    }

    public void clickDeleteForClient(String clientName) {
        WebElement row = requireClientRow(clientName);
        WebElement btn = row.findElement(By.cssSelector("button[aria-label='Delete client']"));
        wait.until(ExpectedConditions.elementToBeClickable(btn)).click();
    }

    private WebElement requireClientRow(String clientName) {
        WebElement row = getClientRow(clientName);
        if (row == null) throw new AssertionError("Client row not found for name: " + clientName);
        return row;
    }

    public String getPhoneForClient(String clientName) {
        WebElement row = requireClientRow(clientName);
        WebElement[] cells = row.findElements(By.cssSelector("td")).toArray(new WebElement[0]);
        if (cells.length >= 3) {
            // Columns: ID (0), Name (1), Phone (2) in this table.
            return cells[2].getText().trim();
        }
        // Fallback: attempt to find any phone-looking text in the row.
        throw new AssertionError("Could not locate Phone cell for name: " + clientName);
    }
}
