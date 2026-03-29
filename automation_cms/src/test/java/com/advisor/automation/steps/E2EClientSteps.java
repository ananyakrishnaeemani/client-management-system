package com.advisor.automation.steps;

import com.advisor.automation.pages.ClientDetailsPage;
import com.advisor.automation.pages.ClientFormPage;
import com.advisor.automation.pages.DashboardPage;
import com.advisor.automation.pages.HeaderPage;
import com.advisor.automation.pages.LoginPage;
import com.advisor.automation.utils.DriverManager;
import com.advisor.automation.utils.AuthApiUtil;
import com.advisor.automation.utils.ExcelReader;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.Map;

public class E2EClientSteps {
    private WebDriver driver;
    private Map<String, String> currentTestData;

    private DashboardPage dashboardPage;
    private LoginPage loginPage;
    private ClientFormPage clientFormPage;
    private ClientDetailsPage clientDetailsPage;
    private HeaderPage headerPage;

    private String name() {
        return currentTestData.get("Name");
    }

    @Given("the user opens the application")
    public void the_user_opens_the_application() {
        driver = DriverManager.getDriver();
        driver.get("http://localhost:4200");
        dashboardPage = new DashboardPage(driver);
        loginPage = new LoginPage(driver);
        headerPage = new HeaderPage(driver);
        clientFormPage = new ClientFormPage(driver);
        clientDetailsPage = new ClientDetailsPage(driver);
    }

    @When("the user logs in using Excel data for {string}")
    public void the_user_logs_in_using_excel_data_for(String testCaseId) {
        currentTestData = ExcelReader.getTestData(testCaseId);

        String email = currentTestData.get("LoginEmail");
        String password = currentTestData.get("LoginPassword");
        Assert.assertNotNull(email, "Missing LoginEmail in Excel for " + testCaseId);
        Assert.assertNotNull(password, "Missing LoginPassword in Excel for " + testCaseId);

        // Ensure the advisor account exists. This keeps tests self-contained.
        String uniqueEmail = AuthApiUtil.uniquifyEmail(email, testCaseId);
        String agentName = currentTestData.getOrDefault("AgentName", "AutoAdvisor_" + testCaseId);
        AuthApiUtil.signupAdvisorIfNeeded(agentName, uniqueEmail, password);

        loginPage.login(uniqueEmail, password);

        // Wait for dashboard to load by checking the add button.
        new WebDriverWait(driver, Duration.ofSeconds(20)).until(
                ExpectedConditions.visibilityOfElementLocated(org.openqa.selenium.By.id("addClientBtn"))
        );
    }

    @When("the user navigates to the dashboard table")
    public void the_user_navigates_to_the_dashboard_table() {
        driver.get("http://localhost:4200");
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                ExpectedConditions.visibilityOfElementLocated(org.openqa.selenium.By.id("addClientBtn"))
        );
    }

    @When("the user adds a client using Excel data for {string}")
    public void the_user_adds_a_client_using_excel_data_for(String testCaseId) {
        // Load data for this test case (keeps view/update/delete aligned).
        currentTestData = ExcelReader.getTestData(testCaseId);
        String name = currentTestData.get("Name");

        Assert.assertNotNull(currentTestData.get("DOB"), "DOB is missing in Excel for " + testCaseId);
        Assert.assertNotNull(currentTestData.get("Phone"), "Phone is missing in Excel for " + testCaseId);
        Assert.assertNotNull(currentTestData.get("Email"), "Email is missing in Excel for " + testCaseId);

        Assert.assertNotNull(currentTestData.get("Gender"), "Gender is missing in Excel for " + testCaseId);
        Assert.assertNotNull(currentTestData.get("BloodGroup"), "BloodGroup is missing in Excel for " + testCaseId);
        Assert.assertNotNull(currentTestData.get("IdType"), "IdType is missing in Excel for " + testCaseId);
        Assert.assertNotNull(currentTestData.get("IdNumber"), "IdNumber is missing in Excel for " + testCaseId);
        Assert.assertNotNull(currentTestData.get("AnnualIncome"), "AnnualIncome is missing in Excel for " + testCaseId);
        Assert.assertNotNull(currentTestData.get("EmploymentStatus"), "EmploymentStatus is missing in Excel for " + testCaseId);

        driver.get("http://localhost:4200/add");

        clientFormPage.fillPersonalDetails(
                name,
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

        clientFormPage.fillFinancialDetails(
                currentTestData.get("InvAmount"),
                currentTestData.get("InvDuration")
        );

        // Required risk assessment + documents (driven from Excel).
        clientFormPage.completeFinancialRiskAssessment(
                currentTestData.get("RiskTolerance"),
                currentTestData.get("MarketDropReaction"),
                currentTestData.get("InvestmentExperience"),
                Boolean.parseBoolean(currentTestData.getOrDefault("PolicyOptIn", "false")),
                Boolean.parseBoolean(currentTestData.getOrDefault("KycCompleted", "false")),
                currentTestData.getOrDefault("PoliticallyExposed", "No")
        );

        clientFormPage.uploadDocument(currentTestData.get("DocumentPath"));
        clientFormPage.setAcceptTerms(Boolean.parseBoolean(currentTestData.getOrDefault("AcceptTerms", "true")));

        clientFormPage.submit();

        // Wait for dashboard to load and client to appear in table
        new WebDriverWait(driver, Duration.ofSeconds(20)).until(
                ExpectedConditions.visibilityOfElementLocated(org.openqa.selenium.By.id("addClientBtn"))
        );
        new WebDriverWait(driver, Duration.ofSeconds(20)).until(d ->
                new DashboardPage(driver).isClientInTable(name)
        );
        Assert.assertTrue(new DashboardPage(driver).isClientInTable(name), "Client was not found in dashboard after add.");
    }

    @Then("the user can view the added client")
    public void the_user_can_view_the_added_client() {
        String clientName = name();
        dashboardPage = new DashboardPage(driver);
        dashboardPage.clickViewForClient(clientName);

        Assert.assertTrue(clientDetailsPage.isClientNameDisplayed(clientName),
                "Client Details page did not display expected client name: " + clientName);
    }

    @When("the user updates the client using Excel data for {string}")
    public void the_user_updates_the_client_using_excel_data_for(String testCaseId) {
        currentTestData = ExcelReader.getTestData(testCaseId);
        String clientName = currentTestData.get("Name");

        // Go back to dashboard and open edit.
        driver.get("http://localhost:4200");
        dashboardPage = new DashboardPage(driver);
        dashboardPage.clickEditForClient(clientName);

        clientFormPage.updatePhone(currentTestData.get("UpdatedPhone"));
        clientFormPage.updateFinancialDetails(
                currentTestData.get("UpdatedInvAmount"),
                currentTestData.get("UpdatedInvDuration")
        );

        // acceptTerms is requiredTrue and edit-mode loadClientData() does not set it.
        clientFormPage.setAcceptTerms(Boolean.parseBoolean(currentTestData.getOrDefault("AcceptTerms", "true")));

        clientFormPage.submit();

        // Validate updated phone is present in table.
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> dashboardPage.isClientInTable(clientName));
        Assert.assertEquals(dashboardPage.getPhoneForClient(clientName), currentTestData.get("UpdatedPhone"),
                "Phone was not updated in dashboard table.");
    }

    @Then("the user can delete the client and verify it is removed")
    public void the_user_can_delete_the_client_and_verify_it_is_removed() {
        String clientName = name();

        driver.get("http://localhost:4200");
        dashboardPage = new DashboardPage(driver);
        dashboardPage.clickDeleteForClient(clientName);

        // Handle the confirm() dialog from deleteClient().
        Alert alert = new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.alertIsPresent());
        alert.accept();

        new WebDriverWait(driver, Duration.ofSeconds(10)).until(d ->
                !dashboardPage.isClientInTable(clientName)
        );
        Assert.assertFalse(dashboardPage.isClientInTable(clientName), "Client still exists in dashboard after delete.");
    }

    @And("the user logs out")
    public void the_user_logs_out() {
        headerPage = new HeaderPage(driver);
        headerPage.clickLogout();

        // Verify login page by checking presence of email input (reactive form).
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(org.openqa.selenium.By.cssSelector("input[formcontrolname='email']")),
                ExpectedConditions.visibilityOfElementLocated(org.openqa.selenium.By.cssSelector("input[formControlName='email']"))
        ));
    }
}

