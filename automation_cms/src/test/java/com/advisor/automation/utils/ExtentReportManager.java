package com.advisor.automation.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Thread-safe ExtentReports manager for parallel Cucumber+TestNG execution.
 * Produces a rich Spark HTML report with system info, environment details,
 * and per-scenario step logging.
 */
public class ExtentReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> EXTENT_TEST = new ThreadLocal<>();

    private static final String REPORT_DIR  = "target/extent-report/";
    private static final String REPORT_FILE = REPORT_DIR + "FinancialAdvisorPortal_AutomationReport.html";

    // ── Initialise once ──────────────────────────────────────────────────────

    private static synchronized ExtentReports getExtent() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_FILE);
            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle("Financial Advisor Portal – Automation Report");
            spark.config().setReportName("Financial Advisor Portal – E2E Test Report");
            spark.config().setEncoding("UTF-8");
            spark.config().setTimeStampFormat("MMM dd, yyyy  HH:mm:ss");
            spark.config().setCss(
                ".brand-name { font-size: 16px; font-weight: 700; color: #42a5f5; }" +
                ".report-name { color: #90caf9; }" +
                "td.status-col .badge { border-radius: 4px; }"
            );

            extent = new ExtentReports();
            extent.attachReporter(spark);

            // ── System / Environment info shown in the report dashboard ──────
            extent.setSystemInfo("Application",   "Financial Advisor Portal");
            extent.setSystemInfo("Environment",   "Local (localhost:4200 / :8080)");
            extent.setSystemInfo("Browser",       "Google Chrome");
            extent.setSystemInfo("OS",            System.getProperty("os.name") + " " + System.getProperty("os.arch"));
            extent.setSystemInfo("Java Version",  System.getProperty("java.version"));
            extent.setSystemInfo("Executed By",   System.getProperty("user.name"));
            extent.setSystemInfo("Execution Time",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy  HH:mm:ss")));
            extent.setSystemInfo("Framework",     "Selenium 4 + Cucumber 7 + TestNG 7");
            extent.setSystemInfo("Report Generated", REPORT_FILE);
        }
        return extent;
    }

    // ── Per-scenario lifecycle ────────────────────────────────────────────────

    public static void startTest(String scenarioName, String featureName) {
        String safeName = (scenarioName == null || scenarioName.isBlank()) ? "Scenario" : scenarioName;
        synchronized (ExtentReportManager.class) {
            ExtentTest test = getExtent().createTest(safeName);
            if (featureName != null && !featureName.isBlank()) {
                test.assignCategory(featureName);
            }
            // Tag the author / device
            test.assignAuthor("AutoAdvisor");
            test.assignDevice("Chrome – " + System.getProperty("os.name"));
            EXTENT_TEST.set(test);
        }
    }

    /** Backward-compatible overload used by existing Hooks. */
    public static void startTest(String scenarioName) {
        startTest(scenarioName, null);
    }

    public static ExtentTest getTest() {
        return EXTENT_TEST.get();
    }

    // ── Step-level logging helpers ────────────────────────────────────────────

    public static void logInfo(String message) {
        ExtentTest t = EXTENT_TEST.get();
        if (t != null) t.info(message);
    }

    public static void logPass(String message) {
        ExtentTest t = EXTENT_TEST.get();
        if (t != null) t.pass(message);
    }

    public static void logFail(String message) {
        ExtentTest t = EXTENT_TEST.get();
        if (t != null) t.fail(message);
    }

    public static void logWarning(String message) {
        ExtentTest t = EXTENT_TEST.get();
        if (t != null) t.warning(message);
    }

    public static void logScreenshot(String screenshotPath, String title) {
        ExtentTest t = EXTENT_TEST.get();
        if (t == null || screenshotPath == null || screenshotPath.isBlank()) return;
        try {
            t.addScreenCaptureFromPath(screenshotPath, title);
        } catch (Exception e) {
            t.warning("Could not attach screenshot: " + e.getMessage());
        }
    }

    // ── Teardown ──────────────────────────────────────────────────────────────

    public static void endTest() {
        EXTENT_TEST.remove();
    }

    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
