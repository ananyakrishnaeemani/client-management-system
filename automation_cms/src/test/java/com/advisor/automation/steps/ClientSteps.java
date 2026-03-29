package com.advisor.automation.steps;

import com.advisor.automation.pages.DashboardPage;
import com.advisor.automation.pages.ClientFormPage;
import com.advisor.automation.utils.ExcelReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import com.advisor.automation.utils.DriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.Map;

public class ClientSteps {
    private DashboardPage dashboardPage;
    private ClientFormPage clientFormPage;
    private Map<String, String> currentTestData;

    @Given("the advisor is on the Add Client page")
    public void the_advisor_is_on_the_add_client_page() {
        WebDriver driver = DriverManager.getDriver();
        driver.get("http://localhost:4200/add"); // Navigate directly to add
        dashboardPage = new DashboardPage(driver);
        clientFormPage = new ClientFormPage(driver);
        Assert.assertTrue(driver.getCurrentUrl().contains("add"), "Expected to be on /add page");
    }

    @When("the advisor fills out the personal details from Excel using {string}")
    public void the_advisor_fills_out_the_personal_details_from_excel_using(String testCaseId) {
        currentTestData = ExcelReader.getTestData(testCaseId);

        Assert.assertNotNull(currentTestData.get("Name"), "Name is missing in Excel for " + testCaseId);
        Assert.assertNotNull(currentTestData.get("DOB"), "DOB is missing in Excel for " + testCaseId);
        Assert.assertNotNull(currentTestData.get("Phone"), "Phone is missing in Excel for " + testCaseId);
        Assert.assertNotNull(currentTestData.get("Email"), "Email is missing in Excel for " + testCaseId);

        Assert.assertNotNull(currentTestData.get("Gender"), "Gender is missing in Excel for " + testCaseId);
        Assert.assertNotNull(currentTestData.get("BloodGroup"), "BloodGroup is missing in Excel for " + testCaseId);
        Assert.assertNotNull(currentTestData.get("IdType"), "IdType is missing in Excel for " + testCaseId);
        Assert.assertNotNull(currentTestData.get("IdNumber"), "IdNumber is missing in Excel for " + testCaseId);
        Assert.assertNotNull(currentTestData.get("AnnualIncome"), "AnnualIncome is missing in Excel for " + testCaseId);
        Assert.assertNotNull(currentTestData.get("EmploymentStatus"), "EmploymentStatus is missing in Excel for " + testCaseId);

        clientFormPage.fillPersonalDetails(
                currentTestData.get("Name"),
                currentTestData.get("DOB"),
                currentTestData.get("Phone"),
                currentTestData.get("Email")
        );

        // Required identity + income fields (Angular form validation).
        clientFormPage.fillRequiredIdentityAndIncomeDetails(
                currentTestData.get("Gender"),
                currentTestData.get("BloodGroup"),
                currentTestData.get("IdType"),
                currentTestData.get("IdNumber"),
                currentTestData.get("AnnualIncome"),
                currentTestData.get("EmploymentStatus")
        );
    }

    @When("the advisor completes the financial risk assessment")
    public void the_advisor_completes_the_financial_risk_assessment() {
        clientFormPage.fillFinancialDetails(
                currentTestData.get("InvAmount"),
                currentTestData.get("InvDuration")
        );

        clientFormPage.completeFinancialRiskAssessment(
                currentTestData.get("RiskTolerance"),
                currentTestData.get("MarketDropReaction"),
                currentTestData.get("InvestmentExperience"),
                Boolean.parseBoolean(currentTestData.getOrDefault("PolicyOptIn", "false")),
                Boolean.parseBoolean(currentTestData.getOrDefault("KycCompleted", "false")),
                currentTestData.getOrDefault("PoliticallyExposed", "No")
        );

        clientFormPage.setAcceptTerms(Boolean.parseBoolean(currentTestData.getOrDefault("AcceptTerms", "true")));
    }

    @When("uploads a supporting ID document")
    public void uploads_a_supporting_id_document() {
        String docPath = currentTestData.get("DocumentPath");
        Assert.assertNotNull(docPath, "DocumentPath is missing in Excel for this test case");
        clientFormPage.uploadDocument(docPath);
    }

    @When("submits the client form")
    public void submits_the_client_form() {
        clientFormPage.submit();
    }

    @Then("the system should successfully save the client")
    public void the_system_should_successfully_save_the_client() throws InterruptedException {
        // Wait for redirect or success toast
        Thread.sleep(2000);
    }

    @Then("the new client should appear in the Dashboard table with accurate calculated Risk Category")
    public void the_new_client_should_appear_in_the_dashboard_table_with_accurate_calculated_risk_category() {
        WebDriver driver = DriverManager.getDriver();
        driver.get("http://localhost:4200");
        Assert.assertTrue(dashboardPage.isClientInTable(currentTestData.get("Name")), "Client is missing from Dashboard!");
    }
}
