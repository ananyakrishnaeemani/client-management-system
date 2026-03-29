package com.advisor.automation.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ScreenshotUtil {

    private static final String SCREENSHOT_DIR = "target/screenshots";

    /**
     * Captures a screenshot and saves it under target/screenshots/.
     * Returns the absolute path string so ExtentReports can embed it.
     */
    public static String captureScreenshot(WebDriver driver, String filePrefix) {
        if (driver == null) return "";
        try {
            Path dir = Path.of(SCREENSHOT_DIR);
            if (!Files.exists(dir)) Files.createDirectories(dir);

            String timestamp  = String.valueOf(System.currentTimeMillis());
            String safePrefix = sanitize(filePrefix);
            String fileName   = safePrefix + "_" + timestamp + ".png";
            Path   dest       = dir.resolve(fileName);

            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);

            // Return absolute path — Extent resolves it correctly from the report HTML
            return dest.toAbsolutePath().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String sanitize(String name) {
        if (name == null || name.isBlank()) return "screenshot";
        return name.replaceAll("[^a-zA-Z0-9._-]", "_").substring(0, Math.min(name.length(), 80));
    }
}
