package com.advisor.automation.pages;

import com.advisor.automation.utils.ExtentReportManager;
import com.advisor.automation.utils.ScreenshotUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientFormPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public ClientFormPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void fillPersonalDetails(String name, String dob, String phone, String email) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name"))).clear();
        driver.findElement(By.id("name")).sendKeys(name);

        // type="date" on Windows Chrome has MM/DD/YYYY segments — sendKeys scrambles them.
        // Use JS native value setter + dispatch input/change so Angular reactive form picks it up.
        setDateInputById("dob", dob);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("phone"))).clear();
        driver.findElement(By.id("phone")).sendKeys(phone);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email"))).clear();
        driver.findElement(By.id("email")).sendKeys(email);
    }

    /**
     * Sets a native <input type="date"> value via JavaScript.
     * Expects isoDate in "YYYY-MM-DD" format.
     * Fires bubbling input + change events so Angular's reactive form control updates.
     */
    private void setDateInputById(String inputId, String isoDate) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(inputId)));
        ((JavascriptExecutor) driver).executeScript(
            "var el = arguments[0];" +
            "var nativeSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
            "nativeSetter.call(el, arguments[1]);" +
            "el.dispatchEvent(new Event('input',  { bubbles: true }));" +
            "el.dispatchEvent(new Event('change', { bubbles: true }));",
            el, isoDate
        );
    }

    public void fillFinancialDetails(String amount, String duration) {
        WebElement amountField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("investmentAmount")));
        amountField.clear();
        amountField.sendKeys(amount);
        // Tab away so Angular marks the field as touched and shows mat-error immediately
        amountField.sendKeys(org.openqa.selenium.Keys.TAB);
        captureIfValidationError("investmentAmount", amount);

        WebElement durationField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("investmentDuration")));
        durationField.clear();
        durationField.sendKeys(duration);
        durationField.sendKeys(org.openqa.selenium.Keys.TAB);
        captureIfValidationError("investmentDuration", duration);
    }

    public void completeFinancialRiskAssessment(String riskTolerance,
                                                  String marketDropReaction,
                                                  String investmentExperience,
                                                  boolean policyOptIn,
                                                  boolean kycCompleted,
                                                  String politicallyExposed) {
        // Risk Tolerance — use formControlName since Angular may not propagate id to DOM
        selectRadioByFormControlGroup("riskTolerance", riskTolerance);
        // Market Drop Reaction
        selectRadioByFormControlGroup("marketDropReaction", marketDropReaction);
        // Investment Experience
        selectRadioByFormControlGroup("investmentExperience", investmentExperience);

        // Policy Opt-in checkbox (id="policy")
        setCheckboxByIdIfNeeded("policy", policyOptIn);
        // KYC completed checkbox
        setCheckboxByFormControl("kycCompleted", kycCompleted);
        // Politically exposed yes/no
        selectRadioByFormControlGroup("politicallyExposed", politicallyExposed);
    }

    public void uploadDocument(String filePath) {
        String resolvedPath = resolveDocumentPath(filePath);
        WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("fileUpload")));
        fileInput.sendKeys(resolvedPath);
    }

    public void setAcceptTerms(boolean accept) {
        // acceptTerms mat-checkbox (formControlName="acceptTerms")
        setCheckboxByFormControl("acceptTerms", accept);
    }

    public void submit() {
        WebElement submitBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("submitBtn")));
        try {
            submitBtn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);
        }

        // Short pause for Angular to render the banner and scroll to first error
        try { Thread.sleep(600); } catch (InterruptedException ignored) {}

        // If still on the form page, validation blocked submission — capture submit-level screenshot
        boolean stillOnForm = !driver.findElements(By.cssSelector(".submit-error-banner")).isEmpty();
        if (stillOnForm) {
            // Scroll to the banner so it's visible in the screenshot
            driver.findElements(By.cssSelector(".submit-error-banner")).stream().findFirst().ifPresent(banner ->
                ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({ behavior: 'instant', block: 'center' });", banner
                )
            );
            String screenshotPath = ScreenshotUtil.captureScreenshot(driver, "submit_validation_blocked");
            ExtentReportManager.logWarning("<b>Form submission blocked</b> — required fields are missing or invalid.");
            ExtentReportManager.logScreenshot(screenshotPath, "Submit Blocked – Validation Banner");
        }
    }

    private String buildFormDebugSnapshot() {
        StringBuilder sb = new StringBuilder();
        sb.append("Form snapshot: ");

        sb.append("name=").append(safeInputValue(By.id("name"))).append(", ");
        sb.append("dob=").append(safeInputValue(By.id("dob"))).append(", ");
        sb.append("phone=").append(safeInputValue(By.id("phone"))).append(", ");
        sb.append("email=").append(safeInputValue(By.id("email"))).append(", ");
        sb.append("idNumber=").append(safeInputValue(By.id("idNumber"))).append(", ");
        sb.append("investmentAmount=").append(safeInputValue(By.id("investmentAmount"))).append(", ");
        sb.append("investmentDuration=").append(safeInputValue(By.id("investmentDuration"))).append(", ");

        sb.append("acceptTermsChecked=").append(safeCheckboxChecked());
        return sb.toString();
    }

    private String safeInputValue(By by) {
        try {
            WebElement el = driver.findElement(by);
            String v = el.getAttribute("value");
            return v == null ? "" : v.trim();
        } catch (Exception e) {
            return "<missing>";
        }
    }

    private String safeCheckboxChecked() {
        try {
            WebElement checkbox = driver.findElement(By.xpath(
                    "//mat-checkbox[@formcontrolname='acceptTerms' or @formControlName='acceptTerms']"
            ));
            WebElement input = checkbox.findElement(By.cssSelector("input[type='checkbox']"));
            return String.valueOf(input.isSelected());
        } catch (Exception e) {
            return "<unknown>";
        }
    }

    public void updatePhone(String phone) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("phone")));
        el.clear();
        el.sendKeys(phone);
    }

    public void updateFinancialDetails(String amount, String duration) {
        WebElement elAmount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("investmentAmount")));
        elAmount.clear();
        elAmount.sendKeys(amount);
        elAmount.sendKeys(org.openqa.selenium.Keys.TAB);
        captureIfValidationError("investmentAmount", amount);

        WebElement elDur = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("investmentDuration")));
        elDur.clear();
        elDur.sendKeys(duration);
        elDur.sendKeys(org.openqa.selenium.Keys.TAB);
        captureIfValidationError("investmentDuration", duration);
    }

    /**
     * After typing a value into a field, checks if Angular shows a mat-error for that field.
     * If a validation error is visible (red error text), scrolls the field into view and
     * captures a screenshot immediately — showing exactly which field has the error.
     */
    private void captureIfValidationError(String fieldId, String enteredValue) {
        try {
            By errorLocator = By.cssSelector(
                "mat-form-field:has(#" + fieldId + ") mat-error"
            );
            new WebDriverWait(driver, java.time.Duration.ofSeconds(1))
                .until(ExpectedConditions.visibilityOfElementLocated(errorLocator));

            // Scroll the invalid field into view so the screenshot clearly shows it
            WebElement field = driver.findElement(By.id(fieldId));
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({ behavior: 'instant', block: 'center' });", field
            );

            String errorText = driver.findElement(errorLocator).getText();
            String screenshotPath = ScreenshotUtil.captureScreenshot(
                driver, "field_validation_error_" + fieldId
            );
            ExtentReportManager.logWarning(
                "<b>Validation error on field [" + fieldId + "]</b> with value <i>\"" + enteredValue + "\"</i>: " + errorText
            );
            ExtentReportManager.logScreenshot(screenshotPath, "Field Validation Error – " + fieldId);
        } catch (org.openqa.selenium.TimeoutException ignored) {
            // No validation error — input is valid
        }
    }

    private void selectRadioByGroup(String groupId, String value) {
        // Delegate to formControlName-based lookup since Angular may not propagate id to DOM
        selectRadioByFormControlGroup(groupId, value);
    }

    private void selectRadioByXpath1(String xpath) {
        WebElement radioButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", radioButton);
        try {
            WebElement input = radioButton.findElement(By.cssSelector("input[type='radio']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", input);
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioButton);
        }
    }

    private void selectRadioByFormControlGroup(String formControlName, String value) {
        // Find the mat-radio-button inside the group by label text or input value
        String groupXpath = "//mat-radio-group[@formcontrolname='" + formControlName +
                "' or @formControlName='" + formControlName + "']";

        // Wait for the group to be present
        WebElement group = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(groupXpath)));

        // Find matching radio button within the group by visible text
        WebElement matched = null;
        java.util.List<WebElement> buttons = group.findElements(By.tagName("mat-radio-button"));
        for (WebElement btn : buttons) {
            String text = btn.getText().trim();
            if (text.equalsIgnoreCase(value.trim()) || text.contains(value.trim())) {
                matched = btn;
                break;
            }
        }

        // Fallback: match by input value attribute
        if (matched == null) {
            for (WebElement btn : buttons) {
                try {
                    WebElement input = btn.findElement(By.cssSelector("input[type='radio']"));
                    String inputVal = input.getAttribute("value");
                    if (value.trim().equalsIgnoreCase(inputVal != null ? inputVal.trim() : "")) {
                        matched = btn;
                        break;
                    }
                } catch (Exception ignored) {}
            }
        }

        if (matched == null) {
            throw new IllegalArgumentException(
                "Could not find radio button with value='" + value + "' in group formControlName='" + formControlName + "'. " +
                "Available: " + buttons.stream().map(WebElement::getText).collect(java.util.stream.Collectors.joining(", "))
            );
        }

        // Scroll into view and click the inner input via JS for reliable Angular Material interaction
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", matched);
        try {
            WebElement input = matched.findElement(By.cssSelector("input[type='radio']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", input);
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", matched);
        }
    }

    private void selectRadioByXpath(String xpath) {
        WebElement radioButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", radioButton);
        try {
            WebElement input = radioButton.findElement(By.cssSelector("input[type='radio']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", input);
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioButton);
        }
    }

    private void setCheckboxByIdIfNeeded(String checkboxId, boolean shouldBeChecked) {
        WebElement checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("mat-checkbox#" + checkboxId)));
        WebElement input = checkbox.findElement(By.cssSelector("input[type='checkbox']"));
        boolean isChecked = input.isSelected() || checkbox.getAttribute("aria-checked") != null && checkbox.getAttribute("aria-checked").equalsIgnoreCase("true");
        if (isChecked != shouldBeChecked) {
            try {
                checkbox.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
            }
        }
    }

    private void setCheckboxByFormControl(String formControlName, boolean shouldBeChecked) {
        String xpath = "//mat-checkbox[@formcontrolname='" + formControlName + "'] | //mat-checkbox[@formControlName='" + formControlName + "']";
        WebElement checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        WebElement input = checkbox.findElement(By.cssSelector("input[type='checkbox']"));
        boolean isChecked = input.isSelected() || "true".equalsIgnoreCase(checkbox.getAttribute("aria-checked"));
        if (isChecked != shouldBeChecked) {
            try {
                checkbox.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
            }
        }
    }

    /**
     * Client form has many required fields. This helper fills the "required identity + income" section.
     */
    public void fillRequiredIdentityAndIncomeDetails(
            String gender,
            String bloodGroup,
            String idType,
            String idNumber,
            String annualIncome,
            String employmentStatus
    ) {
        // Gender (mat-radio-group formControlName="gender")
        selectRadioByFormControlGroup("gender", gender);

        // Blood group and ID type are mat-select with stable ids in the template.
        selectMatSelectById("bloodGroup", bloodGroup);
        selectMatSelectById("idType", idType);

        // ID number is an input with stable id.
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("idNumber"))).clear();
        driver.findElement(By.id("idNumber")).sendKeys(idNumber);

        // Annual income / employment status are mat-select bound via formControlName.
        selectMatSelectByFormControlName("annualIncome", annualIncome);
        selectMatSelectByFormControlName("employmentStatus", employmentStatus);
    }

    private void selectMatSelectById(String selectId, String value) {
        selectMatSelect(selectById(selectId), value);
    }

    private void selectMatSelectByFormControlName(String formControlName, String value) {
        // Works with both Angular's formcontrolname and formControlName attribute casing.
        By selectLocator = By.xpath(
                "//mat-select[@formcontrolname='" + formControlName + "' or @formControlName='" + formControlName + "']"
        );
        selectMatSelect(selectLocator, value);
    }

    private By selectById(String id) {
        return By.id(id);
    }

    private void selectMatSelect(By selectLocator, String value) {
        WebElement matSelect = wait.until(ExpectedConditions.elementToBeClickable(selectLocator));
        matSelect.click();

        // Click the option by visible text (e.g., "Under $50k").
        //
        // Angular Material option text can vary slightly (unicode hyphen "−", whitespace, currency symbols).
        // So we match by normalized text rather than exact raw string equality.
        String desiredNorm = normalizeOptionText(value);

        // Wait for overlay options to appear.
        WebElement overlay = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".cdk-overlay-container")));

        // Gather all options currently shown.
        java.util.List<WebElement> options = overlay.findElements(By.cssSelector("mat-option"));
        if (options.isEmpty()) {
            // Fallback: overlay container might be outside the current root reference.
            options = driver.findElements(By.cssSelector("mat-option"));
        }

        WebElement matched = null;
        for (WebElement opt : options) {
            String optionText = opt.getText();
            if (optionText == null) continue;
            String optionNorm = normalizeOptionText(optionText);
            if (optionNorm.equals(desiredNorm)) {
                matched = opt;
                break;
            }
        }

        // Secondary match: contains relationship.
        if (matched == null) {
            for (WebElement opt : options) {
                String optionText = opt.getText();
                if (optionText == null) continue;
                String optionNorm = normalizeOptionText(optionText);
                if (!optionNorm.isBlank() && !desiredNorm.isBlank()
                        && (optionNorm.contains(desiredNorm) || desiredNorm.contains(optionNorm))) {
                    matched = opt;
                    break;
                }
            }
        }

        if (matched == null) {
            throw new IllegalArgumentException(
                    "Could not find mat-option for value='" + value + "'. " +
                            "Desired normalized='" + desiredNorm + "'. Available options: " +
                            options.stream().map(o -> o.getText()).collect(java.util.stream.Collectors.joining(", "))
            );
        }

        // Normal click sometimes fails due to overlay animations; JS click is more reliable.
        try {
            wait.until(ExpectedConditions.elementToBeClickable(matched)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", matched);
        }
    }

    private String normalizeOptionText(String s) {
        if (s == null) return "";
        // Normalize common unicode variants and remove currency symbols.
        String norm = s.replace('\u2212', '-'); // unicode minus
        norm = norm.replace("$", "");
        norm = norm.replace(",", "");
        norm = norm.trim();
        // Collapse multiple spaces and normalize around hyphens.
        norm = norm.replaceAll("\\s+", "");
        return norm;
    }

    private String resolveDocumentPath(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("DocumentPath is missing in Excel data.");
        }

        Path candidate = Paths.get(filePath);
        if (candidate.isAbsolute()) {
            if (!Files.exists(candidate)) {
                throw new IllegalArgumentException("Document file not found at: " + candidate);
            }
            return candidate.toString();
        }

        // Treat as a file under src/test/resources/test-files/ (packaged into the test classpath).
        String normalized = filePath.replace("\\", "/");
        String resourceRel = "test-files/" + normalized;
        URL resourceUrl = ClientFormPage.class.getClassLoader().getResource(resourceRel);
        if (resourceUrl != null) {
            try {
                if ("file".equals(resourceUrl.getProtocol())) {
                    Path resourcePath = Paths.get(resourceUrl.toURI());
                    if (Files.exists(resourcePath)) {
                        return resourcePath.toString();
                    }
                }
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Invalid document resource path for: " + filePath, e);
            }
        }

        // Fallback to filesystem relative to the current project root.
        Path fallback = Paths.get("src", "test", "resources", "test-files").resolve(filePath);
        if (Files.exists(fallback)) {
            return fallback.toString();
        }

        throw new IllegalArgumentException(
                "Document file not found: '" + filePath + "'. " +
                        "Provide an absolute path or place the file under 'src/test/resources/test-files/'."
        );
    }
}

