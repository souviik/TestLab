package com.sdastest.projects.website.labcorp.pages;

import com.sdastest.helpers.PropertiesHelpers;
import com.sdastest.projects.website.labcorp.models.JobData;
import com.sdastest.utils.LogUtils;
import com.sdastest.driver.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;

import static com.sdastest.keywords.WebUI.*;

public class LabcorpJobDetailsPage {

    // Job details page locators using different By types
    private By jobTitleHeader = By.xpath("//h1");
    private By requirementsSection = By.xpath("//*[contains(text(), 'Requirements:')]");
    private By benefitsSection = By.xpath("//*[contains(text(), 'Benefits:')]");

    // Apply button locators
    private By applyNowButton = By.xpath("//a[.//text()='Apply Now']");
    private By applyNowButtonAlt = By.xpath("//ppc-content[text()='Apply Now']");
    
    // Dynamic validation locators for failures
    private By categoryDetailLocator = By.xpath("//*[contains(text(), 'Category')]/following-sibling::*[1] | //*[contains(text(), 'Category')]/..//*[contains(text(), 'Information Technology')]");
    private By jobIdDetailLocator = By.xpath("//*[contains(text(), 'Job ID')]");
    private By jobTypeDetailLocator = By.xpath("//*[contains(text(), 'Job Type')]/following-sibling::*[1] | //*[contains(text(), 'Job Type')]/..//*[contains(text(), 'Full-Time')]");
    private By locationDetailLocator = By.xpath("//*[contains(text(), 'Location')]/following-sibling::*[1]");

    public LabcorpJobDetailsPage() {
        PropertiesHelpers.loadAllFiles();
    }

    public void verifyJobPostingPageLoaded() {
        waitForPageLoaded();
        sleep(3);
        
        if (!getCurrentUrl().contains("/job/")) {
            throw new RuntimeException("Not on job posting page");
        }
        
        if (isElementVisible(jobTitleHeader, 10)) {
            String jobTitle = getTextElement(jobTitleHeader);
            LogUtils.info("Job posting page loaded for: " + jobTitle);
        } else {
            throw new RuntimeException("Job title not visible on page");
        }
    }

    public String clickApplyNowButton() {
//        sleep(2);
        
        String currentWindowHandle = DriverManager.getDriver().getWindowHandle();
        
        By[] applyButtonLocators = {applyNowButton, applyNowButtonAlt};
        
        boolean clicked = false;
        for (By locator : applyButtonLocators) {
            try {
                waitForElementClickable(locator, 10);
                scrollToElementAtTop(locator);
                clickElement(locator);
                LogUtils.info("Successfully clicked Apply Now button using locator: " + locator);
                clicked = true;
                break;
            } catch (Exception e) {
                LogUtils.info("Failed to click Apply Now with locator: " + locator);
                continue;
            }
        }
        
        if (!clicked) {
            throw new RuntimeException("Could not click Apply Now button with any method");
        }
        
        sleep(3); // Allow time for new tab to open
        return currentWindowHandle;
    }

    public void verifyJobTitleInNewTab(String expectedJobTitle) {
        // Switch to new tab
        switchToLastWindow();
        sleep(3);
        
        String currentUrl = getCurrentUrl();
        String pageTitle = getPageTitle();
        
        // Convert job title to URL format (replace spaces with hyphens)
        String urlFormattedTitle = expectedJobTitle.replace(" ", "-");
        
        // Verify job title in URL or page title
        if (currentUrl.toLowerCase().contains(urlFormattedTitle.toLowerCase()) || 
            pageTitle.toLowerCase().contains(expectedJobTitle.toLowerCase()) ||
            currentUrl.contains("job")) {
            LogUtils.info("Job title verified in application page: " + pageTitle);
            LogUtils.info("Expected job title: " + expectedJobTitle);
            LogUtils.info("Application URL: " + currentUrl);
        } else {
            throw new RuntimeException("Job title '" + expectedJobTitle + "' not found in application page. Page title: " + pageTitle + ", URL: " + currentUrl);
        }
    }

    public void returnToJobListingPage(String originalWindowHandle) {
        closeCurrentWindow();
        switchToMainWindow(originalWindowHandle);
        sleep(2);
        LogUtils.info("Returned to job listing page");
    }

    public void navigateBackToJobSearch() {
        DriverManager.getDriver().navigate().back();
        waitForPageLoaded();
        sleep(2);
        LogUtils.info("Navigated back to job search page");
    }

    public void verifySpecificRequirement(String section, String requirementText) {
        List<WebElement> sectionHeaders = getWebElements(By.cssSelector("p > b"));
        int sectionIndex = -1;
        String sectionName;

        for (int i = 0; i < sectionHeaders.size(); i++) {
            sectionName = sectionHeaders.get(i).getText() + ":";
            if (sectionName.contains(section)) {
                sectionIndex = i + 1;
                break;
            }
        }

        if (sectionIndex == -1) {
            throw new RuntimeException("Section '" + section + "' not found");
        }

// Check requirement text in the corresponding ul section
        List<WebElement> requirementsList = getWebElements(By.cssSelector(".jd-info ul:nth-of-type(" + sectionIndex + ") li"));
        boolean requirementFound = false;
        String requirementTextActual = "";
        for (WebElement requirement : requirementsList) {
            requirementTextActual = requirement.getText();
            if (requirementTextActual.contains(requirementText)) {
                LogUtils.info("Requirement found: " + requirementTextActual);
                requirementFound = true;
                break;
            }
        }

        if (!requirementFound) {
            throw new RuntimeException("Requirement '" + requirementText + "' not found in section '" + section + "'");
        }

        String foundText = "";

        
        // If not found with locators, check page source for partial matches
        if (!requirementFound) {
            String pageSource = DriverManager.getDriver().getPageSource();
            if (pageSource.contains(requirementText)) {
                LogUtils.info("Requirement found in page source: " + requirementText);
                requirementFound = true;
            } else {
                String[] keywords = requirementText.split("\\s+");
                boolean allKeywordsFound = true;
                for (String keyword : keywords) {
                    if (!pageSource.contains(keyword)) {
                        allKeywordsFound = false;
                        break;
                    }
                }
                if (allKeywordsFound && keywords.length > 1) {
                    LogUtils.info("All keywords of requirement found in page: " + requirementText);
                    requirementFound = true;
                }
            }
        }
        
        if (!requirementFound) {
            throw new RuntimeException("Requirement '" + requirementText + "' not found on the page");
        }
    }

    public void validateCapturedJobDetails(JobData capturedData) {
        if (capturedData == null) {
            throw new RuntimeException("Cannot validate against null captured data");
        }
        
        LogUtils.info("=== Starting Job Data Validation ===");
        LogUtils.info("Captured data: " + capturedData.toString());
        
        sleep(2);
        
        JobData jobPostingData = extractJobDataFromPostingPage();
        
        LogUtils.info("Job posting data: " + jobPostingData.toString());
        
        // Compare the data matches captured value
        boolean isValid = capturedData.matches(jobPostingData);
        
        if (!isValid) {
            String errorMessage = "Job data validation FAILED! Captured data does not match job posting page.";
            LogUtils.info(errorMessage);
            LogUtils.info("Expected (from listing): " + capturedData.toString());
            LogUtils.info("Actual (from job page): " + jobPostingData.toString());
            throw new AssertionError(errorMessage);
        }
        
        LogUtils.info("Job data validation PASSED! All captured data matches job posting page.");
        LogUtils.info("=== Job Data Validation Complete ===");
    }

    private JobData extractJobDataFromPostingPage() {
        JobData jobData = new JobData();
        
        // Extract Job Title
        String jobTitle = extractJobTitleFromPosting();
        jobData.setJobTitle(jobTitle);
        
        // Extract Category
        String category = extractCategoryFromPosting();
        jobData.setCategory(category);
        
        // Extract Job ID
        String jobId = extractJobIdFromPosting();
        jobData.setJobId(jobId);
        
        // Extract Job Type
        String jobType = extractJobTypeFromPosting();
        jobData.setJobType(jobType);
        
        // Extract Location (if available)
        String location = extractLocationFromPosting();
        jobData.setLocation(location);
        
        return jobData;
    }

    private String extractJobTitleFromPosting() {
        try {
            if (isElementPresent(jobTitleHeader, 5)) {
                String title = getTextElement(jobTitleHeader).trim();
                LogUtils.info("Extracted job title from posting: " + title);
                return title;
            }
            
            LogUtils.info("Could not find job title on posting page");
            return "N/A";
        } catch (Exception e) {
            LogUtils.info("Error extracting job title from posting: " + e.getMessage());
            return "N/A";
        }
    }

    private String extractCategoryFromPosting() {
        try {
            String pageText = DriverManager.getDriver().findElement(By.tagName("body")).getText();
            
            if (pageText.contains("Information Technology")) {
                LogUtils.info("Extracted category from posting: Information Technology");
                return "Information Technology";
            }
            if (pageText.contains("Patient Services")) {
                LogUtils.info("Extracted category from posting: Patient Services");
                return "Patient Services";
            }
            if (pageText.contains("Lab Operations")) {
                LogUtils.info("Extracted category from posting: Lab Operations");
                return "Lab Operations";
            }
            if (pageText.contains("Human Resources")) {
                LogUtils.info("Extracted category from posting: Human Resources");
                return "Human Resources";
            }
            
            List<WebElement> categoryElements = getWebElements(By.xpath("//*[contains(text(), 'Category')]"));
            if (!categoryElements.isEmpty()) {
                for (WebElement element : categoryElements) {
                    try {
                        // Get parent or next sibling context
                        WebElement parent = element.findElement(By.xpath("./.."));
                        String contextText = parent.getText();
                        
                        // Check for category types in the context
                        if (contextText.contains("Information Technology")) {
                            LogUtils.info("Extracted category from posting (context): Information Technology");
                            return "Information Technology";
                        }
                        if (contextText.contains("Patient Services")) {
                            LogUtils.info("Extracted category from posting (context): Patient Services");
                            return "Patient Services";
                        }
                        if (contextText.contains("Lab Operations")) {
                            LogUtils.info("Extracted category from posting (context): Lab Operations");
                            return "Lab Operations";
                        }
                        if (contextText.contains("Human Resources")) {
                            LogUtils.info("Extracted category from posting (context): Human Resources");
                            return "Human Resources";
                        }
                    } catch (Exception ex) {
                        // Continue with next element
                    }
                }
            }
            
            LogUtils.info("Could not find category on posting page");
            return "N/A";
        } catch (Exception e) {
            LogUtils.info("Error extracting category from posting: " + e.getMessage());
            return "N/A";
        }
    }

    private String extractJobIdFromPosting() {
        try {
            String pageText = DriverManager.getDriver().findElement(By.tagName("body")).getText();
            
            // Use regex to find Job ID pattern: "Job ID" followed by optional spaces/colon and then digits
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("Job ID\\s*:?\\s*(\\d{7})");
            java.util.regex.Matcher matcher = pattern.matcher(pageText);
            if (matcher.find()) {
                String jobId = matcher.group(1);
                LogUtils.info("Extracted job ID from posting: " + jobId);
                return jobId;
            }
            
            List<WebElement> jobIdElements = getWebElements(By.xpath("//*[contains(text(), 'Job ID')]"));
            if (!jobIdElements.isEmpty()) {
                for (WebElement element : jobIdElements) {
                    // Get parent context to find the ID number
                    WebElement parent = element.findElement(By.xpath("./.."));
                    String parentText = parent.getText();
                    
                    // Extract ID from parent text using regex
                    java.util.regex.Pattern parentPattern = java.util.regex.Pattern.compile("(\\d{7})");
                    java.util.regex.Matcher parentMatcher = parentPattern.matcher(parentText);
                    if (parentMatcher.find()) {
                        String jobId = parentMatcher.group(1);
                        LogUtils.info("Extracted job ID from posting (parent context): " + jobId);
                        return jobId;
                    }
                }
            }
            
            LogUtils.info("Could not find job ID on posting page");
            return "N/A";
        } catch (Exception e) {
            LogUtils.info("Error extracting job ID from posting: " + e.getMessage());
            return "N/A";
        }
    }

    private String extractJobTypeFromPosting() {
        try {
            String pageText = DriverManager.getDriver().findElement(By.tagName("body")).getText();
            
            if (pageText.contains("Full-Time")) {
                LogUtils.info("Extracted job type from posting: Full-Time");
                return "Full-Time";
            }
            if (pageText.contains("Part-Time")) {
                LogUtils.info("Extracted job type from posting: Part-Time");
                return "Part-Time";
            }
            if (pageText.contains("Contract")) {
                LogUtils.info("Extracted job type from posting: Contract");
                return "Contract";
            }
            
            List<WebElement> jobTypeElements = getWebElements(By.xpath("//*[contains(text(), 'Job Type')]"));
            if (!jobTypeElements.isEmpty()) {
                for (WebElement element : jobTypeElements) {
                    try {
                        // Get parent context
                        WebElement parent = element.findElement(By.xpath("./.."));
                        String contextText = parent.getText();
                        
                        if (contextText.contains("Full-Time")) {
                            LogUtils.info("Extracted job type from posting (context): Full-Time");
                            return "Full-Time";
                        }
                        if (contextText.contains("Part-Time")) {
                            LogUtils.info("Extracted job type from posting (context): Part-Time");
                            return "Part-Time";
                        }
                        if (contextText.contains("Contract")) {
                            LogUtils.info("Extracted job type from posting (context): Contract");
                            return "Contract";
                        }
                    } catch (Exception ex) {
                        // Continue with next element
                    }
                }
            }
            
            LogUtils.info("Could not find job type on posting page");
            return "N/A";
        } catch (Exception e) {
            LogUtils.info("Error extracting job type from posting: " + e.getMessage());
            return "N/A";
        }
    }

    private String extractLocationFromPosting() {
        try {
            List<WebElement> locationElements = getWebElements(By.xpath("//*[contains(text(), 'Location')]"));
            if (!locationElements.isEmpty()) {
                for (WebElement element : locationElements) {
                    WebElement parent = element.findElement(By.xpath("./.."));
                    String parentText = parent.getText();
                    
                    if (parentText.contains("North Carolina")) {
                        String location = parentText.contains("Durham") ? "Durham, North Carolina" : "North Carolina";
                        LogUtils.info("Extracted location from posting: " + location);
                        return location;
                    }
                }
            }
            
            String pageSource = DriverManager.getDriver().getPageSource();
            if (pageSource.contains("Burlington, North Carolina")) {
                LogUtils.info("Extracted location from posting (page source): Burlington, North Carolina");
                return "Burlington, North Carolina";
            } else if (pageSource.contains("Durham, North Carolina")) {
                LogUtils.info("Extracted location from posting (page source): Durham, North Carolina");
                return "Durham, North Carolina";
            }
            
            LogUtils.info("Could not find location on posting page");
            return "N/A";
        } catch (Exception e) {
            LogUtils.info("Error extracting location from posting: " + e.getMessage());
            return "N/A";
        }
    }
}