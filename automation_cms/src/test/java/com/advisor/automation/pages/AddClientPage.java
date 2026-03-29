package com.advisor.automation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class AddClientPage {
    private WebDriver driver;

    public AddClientPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(id = "name")
    private WebElement nameInput;

    @FindBy(id = "dob")
    private WebElement dobInput;

    @FindBy(id = "phone")
    private WebElement phoneInput;

    @FindBy(id = "email")
    private WebElement emailInput;

    @FindBy(id = "investmentAmount")
    private WebElement amountInput;

    @FindBy(id = "investmentDuration")
    private WebElement durationInput;

    @FindBy(id = "fileUpload")
    private WebElement documentUploadInput;

    @FindBy(id = "submitBtn")
    private WebElement submitButton;

    // We assume Radio and Dropdown selection logic will be handled specifically
    
    public void fillPersonalDetails(String name, String dob, String phone, String email) {
        nameInput.sendKeys(name);
        dobInput.sendKeys(dob);
        phoneInput.sendKeys(phone);
        emailInput.sendKeys(email);
    }

    public void fillFinancialDetails(String amount, String duration) {
        amountInput.sendKeys(amount);
        durationInput.sendKeys(duration);
    }

    public void uploadDocument(String path) {
        documentUploadInput.sendKeys(path);
    }

    public void submitForm() {
        submitButton.click();
    }
}
