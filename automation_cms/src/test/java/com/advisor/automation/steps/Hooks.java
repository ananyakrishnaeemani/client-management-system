package com.advisor.automation.steps;

import com.advisor.automation.utils.DriverManager;
import com.advisor.automation.utils.ExtentReportManager;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;

public class Hooks {

    // ── Before scenario ───────────────────────────────────────────────────────

    @Before(order = 0)
    public void beforeScenario(Scenario scenario) {
        DriverManager.initDriver();

        // Derive feature name from the URI  e.g. "client_e2e_crud.feature" → "Client E2E Crud"
        String featureName = extractFeatureName(scenario.getUri().toString());
        ExtentReportManager.startTest(scenario.getName(), featureName);

        ExtentReportManager.logInfo(
            "<b>Feature:</b> " + featureName +
            " &nbsp;|&nbsp; <b>Tags:</b> " + scenario.getSourceTagNames()
        );
    }

    // ── Before each step ─────────────────────────────────────────────────────

    @BeforeStep
    public void beforeStep(Scenario scenario) {
        // Nothing to do here — step name is not available in Cucumber-JVM BeforeStep
    }

    // ── After each step ──────────────────────────────────────────────────────

    @AfterStep
    public void afterStep(Scenario scenario) {
        // Screenshots are captured inline in ClientFormPage for validation errors only
    }

    // ── After scenario ────────────────────────────────────────────────────────

    @After(order = 0)
    public void afterScenario(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                ExtentReportManager.logFail("<b>Scenario FAILED:</b> " + scenario.getName());
            } else {
                ExtentReportManager.logPass("<b>Scenario PASSED:</b> " + scenario.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ExtentReportManager.flush();
            DriverManager.quitDriver();
            ExtentReportManager.endTest();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String extractFeatureName(String uri) {
        if (uri == null) return "Unknown Feature";
        String fileName = uri.substring(uri.lastIndexOf('/') + 1).replace(".feature", "");
        String[] parts = fileName.split("[_\\-]");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isBlank()) {
                sb.append(Character.toUpperCase(p.charAt(0)))
                  .append(p.substring(1).toLowerCase())
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }
}
