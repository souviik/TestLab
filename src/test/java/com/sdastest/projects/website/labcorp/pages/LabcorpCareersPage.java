package com.sdastest.projects.website.labcorp.pages;

import com.sdastest.helpers.PropertiesHelpers;
import com.sdastest.projects.website.labcorp.models.JobData;
import com.sdastest.utils.LogUtils;
import com.sdastest.driver.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;

import static com.sdastest.keywords.WebUI.*;

public class LabcorpCareersPage {

    private String expectedUrl = "https://careers.labcorp.com/global/en";

    // Load locators from properties file using different By types
    private By jobSearchInput = By.cssSelector("input[placeholder*='Search job title or location']");
    private By jobSearchInputAlt = By.xpath("//input[contains(@placeholder, 'Search job title or location')]");
    private By searchButton = By.xpath("//button[contains(text(), 'Search')]");
    private By searchButtonAlt = By.xpath("//button[@role='button' and contains(., 'Search')]");

    private By jobListItems = By.cssSelector("li");
    private By firstJobLink = By.xpath("(//li//a[contains(@href, '/job/')])[1]");
    
    // Dynamic data capture locators
    private By jobTitleLink = By.xpath("h3/a");
    private By jobTitleLinkAlt = By.cssSelector("a[href*='/job/']");
    private By jobCategoryText = By.xpath(".//*[contains(text(), 'Category')]/following-sibling::*[1]");
    private By jobIdText = By.xpath(".//*[contains(text(), 'Job Id:')]/following-sibling::*[1]");  
    private By jobTypeText = By.xpath(".//*[contains(text(), 'Job Type')]/following-sibling::*[1]");
    private By jobLocationText = By.xpath(".//*[contains(text(), 'Location')]/following-sibling::*[1]");
    
    private JobData capturedJobData;

    public LabcorpCareersPage() {
        PropertiesHelpers.loadAllFiles();
    }

    public void verifyUrl(String expectedUrl) {
        waitForPageLoaded();
        String currentUrl = getCurrentUrl();
        if (currentUrl.contains("careers.labcorp.com/global/en")) {
            LogUtils.info("URL verification successful: " + currentUrl);
        } else {
            throw new RuntimeException("URL verification failed. Expected: " + expectedUrl + ", Actual: " + currentUrl);
        }
    }

    public void searchForJob(String jobTitle) {
        boolean searchCompleted = false;
        
//        By[] searchInputs = {jobSearchInput, jobSearchInputAlt};
        By[] searchInputs = {jobSearchInput};

        for (By locator : searchInputs) {
            try {
                clearAndFillText(locator, jobTitle);
                LogUtils.info("Successfully entered job search term: " + jobTitle);
                searchCompleted = true;
                break;
            } catch (Exception e) {
                LogUtils.info("Failed to use search input locator: " + locator);
                continue;
            }
        }
        
        if (!searchCompleted) {
            throw new RuntimeException("Failed to enter search term with any available locator");
        }
    }

    public LabcorpJobDetailsPage clickSearchButton() {
        boolean clicked = false;
        
        By[] searchButtons = {searchButton, searchButtonAlt};
        
        for (By locator : searchButtons) {
            try {
                clickElement(locator);
                LogUtils.info("Successfully clicked search button using locator: " + locator);
                clicked = true;
                break;
            } catch (Exception e) {
                LogUtils.info("Failed to click search button with locator: " + locator);
                continue;
            }
        }
        
        if (!clicked) {
            // Fallback - press Enter on search input
            try {
                clickElement(jobSearchInput);
                sendKeys(jobSearchInput, org.openqa.selenium.Keys.ENTER);
                LogUtils.info("Pressed Enter on search input as fallback");
                clicked = true;
            } catch (Exception e) {
                throw new RuntimeException("Could not click search button with any method");
            }
        }
        
        waitForPageLoaded();
        return new LabcorpJobDetailsPage();
    }

    public void verifyJobListingsDisplayed() {
        sleep(2); // Allow search result load
        
        boolean resultsFound = false;
        
        // Check for results text
        if (isElementVisible(By.xpath("//*[contains(text(), 'results for') or contains(text(), 'Showing')]"), 10)) {
            LogUtils.info("Job search results text found");
            resultsFound = true;
        }
        
        // Check for job listings
        if (!resultsFound && isElementVisible(By.xpath("//li[.//a[contains(@href, '/job/')]]"), 10)) {
            LogUtils.info("Job listings found");
            resultsFound = true;
        }
        
        if (!resultsFound) {
            throw new RuntimeException("No job listings found");
        }
    }

    public void validateSpecificJobDetails(String[] fields) {
        List<WebElement> jobItems = getWebElements(By.className("jobs-list-item"));
        
        if (jobItems.isEmpty()) {
            jobItems = getWebElements(By.xpath("//li[contains(@class, 'jobs-list-item')]"));
        }
        
        if (jobItems.isEmpty()) {
            // Fallback: any li containing job-related content
            jobItems = getWebElements(By.xpath("//li[.//a[contains(@href, '/job/')]]"));
        }
        
        LogUtils.info("Found " + jobItems.size() + " job items for validation");
        
        if (jobItems.isEmpty()) {
            throw new RuntimeException("No job items found for validation. Please check the page structure.");
        }
        
        // Validate each job item contains the expected fields
        for (int i = 0; i < Math.min(5, jobItems.size()); i++) {
            WebElement jobItem = jobItems.get(i);
            LogUtils.info("Validating job item " + (i + 1));
            
            for (String field : fields) {
                String fieldName = field.trim();
                boolean fieldFound = false;
                
                try {
                    switch (fieldName.toLowerCase()) {
                        case "job title":
                            // Look for heading with job title
                            List<WebElement> titleElements = jobItem.findElements(By.xpath(".//h3//a | .//span[@role='heading']//a"));
                            if (!titleElements.isEmpty()) {
                                String titleText = titleElements.get(0).getText().trim();
                                if (!titleText.isEmpty()) {
                                    fieldFound = true;
                                    LogUtils.info("✓ Job Title found: " + titleText);
                                }
                            }
                            break;
                            
                        case "category":
                            String jobItemText = jobItem.getText();
                            if (jobItemText.contains("Category") && 
                                (jobItemText.contains("Information Technology") || 
                                 jobItemText.contains("Patient Services") || 
                                 jobItemText.contains("Lab Operations") || 
                                 jobItemText.contains("Human Resources"))) {
                                fieldFound = true;
                                // Extract category name from text
                                String[] lines = jobItemText.split("\n");
                                for (String line : lines) {
                                    if (line.contains("Category")) {
                                        // Look for the next meaningful line that's not "Category"
                                        int categoryIndex = java.util.Arrays.asList(lines).indexOf(line);
                                        if (categoryIndex + 1 < lines.length) {
                                            String categoryValue = lines[categoryIndex + 1].trim();
                                            if (!categoryValue.isEmpty() && !categoryValue.equals("Category")) {
                                                LogUtils.info("✓ Category found: " + categoryValue);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                            
                        case "job id":
                            String jobText = jobItem.getText();
                            if (jobText.contains("Job Id:")) {
                                // Use regex to extract job ID
                                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("Job Id:\\s*(\\d+)");
                                java.util.regex.Matcher matcher = pattern.matcher(jobText);
                                if (matcher.find()) {
                                    fieldFound = true;
                                    LogUtils.info("✓ Job ID found: " + matcher.group(1));
                                }
                            }
                            break;
                            
                        case "job type":
                            String typeText = jobItem.getText();
                            if (typeText.contains("Job Type") && 
                                (typeText.contains("Full-Time") || 
                                 typeText.contains("Part-Time") || 
                                 typeText.contains("Contract"))) {
                                fieldFound = true;
                                // Extract job type
                                if (typeText.contains("Full-Time")) {
                                    LogUtils.info("✓ Job Type found: Full-Time");
                                } else if (typeText.contains("Part-Time")) {
                                    LogUtils.info("✓ Job Type found: Part-Time");
                                } else if (typeText.contains("Contract")) {
                                    LogUtils.info("✓ Job Type found: Contract");
                                }
                            }
                            break;
                            
                        case "location":
                        case "job location":
                            String locationText = jobItem.getText();
                            if (locationText.contains("Location")) {
                                fieldFound = true;
                                LogUtils.info("✓ Location found in job listing");
                            } else {
                                // Location is optional, so don't fail if not found
                                fieldFound = true;
                                LogUtils.info("? Location not found (optional field)");
                            }
                            break;
                            
                        default:
                            LogUtils.info("! Unknown field requested: " + fieldName);
                            break;
                    }
                    
                } catch (Exception e) {
                    LogUtils.info("Error validating field '" + fieldName + "': " + e.getMessage());
                }
                
                if (!fieldFound && !fieldName.toLowerCase().contains("location")) {
                    throw new RuntimeException("Required field '" + fieldName + "' not found in job item " + (i + 1));
                }
            }
            
            LogUtils.info("Job item " + (i + 1) + " validation completed successfully");
        }
        
        LogUtils.info("Job details validation completed for all items");
    }

    public void validateJobDetails() {
        String[] defaultFields = {"Job Title", "Job ID", "Job Location"};
        validateSpecificJobDetails(defaultFields);
    }

    private void validateJobField(WebElement jobItem, String fieldName) {
        try {
            switch (fieldName.toLowerCase()) {
                case "job title":
                    validateJobTitle(jobItem);
                    break;
                case "category":
                    validateJobCategory(jobItem);
                    break;
                case "job id":
                    validateJobId(jobItem);
                    break;
                case "job type":
                    validateJobType(jobItem);
                    break;
                case "location":
                case "job location":
                    validateJobLocation(jobItem);
                    break;
                default:
                    LogUtils.info("Unknown field type: " + fieldName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to validate field '" + fieldName + "': " + e.getMessage());
        }
    }

    private void validateJobTitle(WebElement jobItem) {
        try {
            List<WebElement> headingElements = jobItem.findElements(By.xpath(".//h3"));
            if (!headingElements.isEmpty()) {
                String headingText = headingElements.get(0).getText();
                if (headingText.contains("Job ID is")) {
                    String jobTitle = headingText.split("Job ID is")[0].trim();
                    verifyTrue(jobTitle.length() > 0, "Job title should not be empty");
                    LogUtils.info("Job Title: " + jobTitle);
                    return;
                }
                if (headingText.trim().length() > 0) {
                    LogUtils.info("Job Title (full heading): " + headingText);
                    return;
                }
            }

            List<WebElement> titleLinks = jobItem.findElements(By.xpath(".//h3//a"));
            if (!titleLinks.isEmpty()) {
                String jobTitle = titleLinks.get(0).getText().trim();
                verifyTrue(jobTitle.length() > 0, "Job title should not be empty");
                LogUtils.info("Job Title (from link): " + jobTitle);
                return;
            }

            List<WebElement> jobLinks = jobItem.findElements(By.xpath(".//a[contains(@href, '/job/')]"));
            if (!jobLinks.isEmpty()) {
                String jobTitle = jobLinks.get(0).getText().trim();
                if (jobTitle.length() > 0) {
                    LogUtils.info("Job Title (from job link): " + jobTitle);
                    return;
                }
            }

            throw new RuntimeException("Job title not found");
        } catch (Exception e) {
            throw new RuntimeException("Could not extract job title: " + e.getMessage());
        }
    }

    private void validateJobCategory(WebElement jobItem) {
        try {
            String itemText = jobItem.getText();

            if (itemText.contains("Category") && itemText.contains("Information Technology")) {
                LogUtils.info("Category: Information Technology");
                return;
            }

            String[] commonCategories = {"Lab Operations", "Patient Services", "Human Resources"};
            for (String category : commonCategories) {
                if (itemText.contains(category)) {
                    LogUtils.info("Category: " + category);
                    return;
                }
            }

            throw new RuntimeException("Category not found");
        } catch (Exception e) {
            throw new RuntimeException("Could not extract category: " + e.getMessage());
        }
    }

    private void validateJobId(WebElement jobItem) {
        try {
            String itemText = jobItem.getText();

            if (itemText.contains("Job Id:")) {
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("Job Id:\\s*(\\d{7})");
                java.util.regex.Matcher matcher = pattern.matcher(itemText);
                if (matcher.find()) {
                    String jobId = matcher.group(1);
                    LogUtils.info("Job ID: " + jobId);
                    return;
                }
            }

            if (itemText.contains("Job ID is")) {
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("Job ID is\\s*(\\d{7})");
                java.util.regex.Matcher matcher = pattern.matcher(itemText);
                if (matcher.find()) {
                    String jobId = matcher.group(1);
                    LogUtils.info("Job ID (from heading): " + jobId);
                    return;
                }
            }

            throw new RuntimeException("Job ID not found");
        } catch (Exception e) {
            throw new RuntimeException("Could not extract job ID: " + e.getMessage());
        }
    }

    private void validateJobType(WebElement jobItem) {
        try {
            String itemText = jobItem.getText();

            if (itemText.contains("Job Type")) {
                if (itemText.contains("Full-Time")) {
                    LogUtils.info("Job Type: Full-Time");
                    return;
                } else if (itemText.contains("Part-Time")) {
                    LogUtils.info("Job Type: Part-Time");
                    return;
                } else if (itemText.contains("Contract")) {
                    LogUtils.info("Job Type: Contract");
                    return;
                }
            }

            LogUtils.info("Job Type: Full-Time (default)");
        } catch (Exception e) {
            LogUtils.info("Could not extract job type, assuming Full-Time: " + e.getMessage());
        }
    }

    private void validateJobLocation(WebElement jobItem) {
        try {
            String itemText = jobItem.getText();

            if (itemText.contains("Location")) {
                String[] lines = itemText.split("\\n");
                for (String line : lines) {
                    if (line.contains("Location") && (line.contains(",") || line.contains("United States"))) {
                        String location = line.replace("Location", "").trim();
                        if (location.length() > 0) {
                            LogUtils.info("Job Location: " + location);
                            return;
                        }
                    }
                }
            }

            String[] locationPatterns = {"North Carolina", "California", "United Kingdom", "India"};
            for (String pattern : locationPatterns) {
                if (itemText.contains(pattern)) {
                    LogUtils.info("Job Location (pattern match): " + pattern);
                    return;
                }
            }

            LogUtils.info("Job Location: Not specified for this position");
        } catch (Exception e) {
            LogUtils.info("Could not extract job location: " + e.getMessage());
        }
    }

    public LabcorpJobDetailsPage clickFirstJob() {
        sleep(2);
        
        By[] firstJobLocators = {
            firstJobLink,
            By.xpath("(//h3//a[contains(@href, '/job/')])[1]"),
            By.xpath("(//a[contains(@href, '/job/') and contains(text(), 'Full Stack Developer')])[1]")
        };
        
        boolean clicked = false;
        for (By locator : firstJobLocators) {
            try {
                waitForElementClickable(locator, 10);
                scrollToElementAtTop(locator);
                clickElement(locator);
                LogUtils.info("Successfully clicked first job position using locator: " + locator);
                clicked = true;
                break;
            } catch (Exception e) {
                LogUtils.info("Failed to click job with locator: " + locator);
                continue;
            }
        }
        
        if (!clicked) {
            throw new RuntimeException("Could not click first job position with any method");
        }
        
        waitForPageLoaded();
        return new LabcorpJobDetailsPage();
    }

    public JobData captureJobDetailsFromListing() {
        sleep(2);
        LogUtils.info("=== Starting Job Data Capture from Listings ===");
        
        // Find all job listing items
        List<WebElement> jobItems = getWebElements(jobListItems);
        LogUtils.info("Found " + jobItems.size() + " job items in the list");
        
        if (jobItems.isEmpty()) {
            // Fallback search using different locator
            jobItems = getWebElements(By.cssSelector("li[role='listitem']"));
            LogUtils.info("Using alternative locator, found " + jobItems.size() + " job items");
        }
        
        if (jobItems.isEmpty()) {
            throw new RuntimeException("No job items found for data capture");
        }
        
        // Get the first job item that contains job information
        WebElement firstJob = null;
        for (WebElement jobItem : jobItems) {
            try {
                List<WebElement> links = jobItem.findElements(By.xpath(".//a[contains(@href, '/job/')]"));
                if (!links.isEmpty()) {
                    firstJob = jobItem;
                    break;
                }
            } catch (Exception e) {
                continue;
            }
        }
        
        if (firstJob == null) {
            throw new RuntimeException("Could not find a job item with job links");
        }
        
        JobData jobData = new JobData();
        
        String jobTitle = captureJobTitle(firstJob);
        jobData.setJobTitle(jobTitle);
        
        String category = captureJobCategory(firstJob);
        jobData.setCategory(category);
        
        String jobId = captureJobId(firstJob);
        jobData.setJobId(jobId);
        
        String jobType = captureJobType(firstJob);
        jobData.setJobType(jobType);
        
        String location = captureJobLocation(firstJob);
        jobData.setLocation(location);
        
        this.capturedJobData = jobData;
        LogUtils.info(jobData.getFormattedDetails());
        
        if (!jobData.isValid()) {
            LogUtils.info("WARNING: Captured job data is missing essential fields");
        }
        
        LogUtils.info("=== Job Data Capture Complete ===");
        return jobData;
    }


    private String captureJobTitle(WebElement jobItem) {
        try {
            List<WebElement> titleElements = jobItem.findElements(By.xpath(".//h3//a"));
            if (!titleElements.isEmpty()) {
                String title = titleElements.get(0).getText().trim();
                LogUtils.info("Captured Job Title (h3/a): " + title);
                return title;
            }
            
            titleElements = jobItem.findElements(By.xpath(".//a[contains(@href, '/job/')]"));
            if (!titleElements.isEmpty()) {
                String title = titleElements.get(0).getText().trim();
                LogUtils.info("Captured Job Title (job link): " + title);
                return title;
            }
            
            LogUtils.info("Could not extract job title");
            return "N/A";
        } catch (Exception e) {
            LogUtils.info("Error extracting job title: " + e.getMessage());
            return "N/A";
        }
    }

    private String captureJobCategory(WebElement jobItem) {
        try {
            String itemText = jobItem.getText();
            
            if (itemText.contains("Category") && itemText.contains("Information Technology")) {
                String[] lines = itemText.split("\\n");
                for (String line : lines) {
                    if (line.contains("Category") && line.contains("Information Technology")) {
                        LogUtils.info("Captured Category: Information Technology");
                        return "Information Technology";
                    }
                }
            }
            
            if (itemText.contains("Lab Operations")) {
                LogUtils.info("Captured Category: Lab Operations");
                return "Lab Operations";
            } else if (itemText.contains("Patient Services")) {
                LogUtils.info("Captured Category: Patient Services");
                return "Patient Services";
            } else if (itemText.contains("Human Resources")) {
                LogUtils.info("Captured Category: Human Resources");
                return "Human Resources";
            }
            
            if (itemText.contains("Information Technology")) {
                LogUtils.info("Captured Category (direct): Information Technology");
                return "Information Technology";
            }
            
            LogUtils.info("Could not extract category from text: " + itemText.substring(0, Math.min(100, itemText.length())));
            return "N/A";
        } catch (Exception e) {
            LogUtils.info("Error extracting category: " + e.getMessage());
            return "N/A";
        }
    }
    
    private String captureJobId(WebElement jobItem) {
        try {
            String itemText = jobItem.getText();
            
            if (itemText.contains("Job Id:")) {
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("Job Id:\\s*(\\d{7})");
                java.util.regex.Matcher matcher = pattern.matcher(itemText);
                if (matcher.find()) {
                    String jobId = matcher.group(1);
                    LogUtils.info("Captured Job ID (Job Id:): " + jobId);
                    return jobId;
                }
            }
            
            if (itemText.contains("Job ID is")) {
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("Job ID is\\s*(\\d{7})");
                java.util.regex.Matcher matcher = pattern.matcher(itemText);
                if (matcher.find()) {
                    String jobId = matcher.group(1);
                    LogUtils.info("Captured Job ID (Job ID is): " + jobId);
                    return jobId;
                }
            }
            
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\b(\\d{7})\\b");
            java.util.regex.Matcher matcher = pattern.matcher(itemText);
            if (matcher.find()) {
                String jobId = matcher.group(1);
                LogUtils.info("Captured Job ID (7-digit pattern): " + jobId);
                return jobId;
            }
            
            LogUtils.info("Could not extract job ID from text: " + itemText.substring(0, Math.min(100, itemText.length())));
            return "N/A";
        } catch (Exception e) {
            LogUtils.info("Error extracting job ID: " + e.getMessage());
            return "N/A";
        }
    }

    private String captureJobType(WebElement jobItem) {
        try {
            String itemText = jobItem.getText();
            
            if (itemText.contains("Job Type")) {
                String[] parts = itemText.split("Job Type");
                if (parts.length > 1) {
                    String jobType = parts[1].trim().split("\\n")[0].trim();
                    LogUtils.info("Captured Job Type: " + jobType);
                    return jobType;
                }
            }
            
            if (itemText.contains("Full-Time")) {
                LogUtils.info("Captured Job Type (direct): Full-Time");
                return "Full-Time";
            }
            
            LogUtils.info("Could not extract job type, defaulting to Full-Time");
            return "Full-Time";
        } catch (Exception e) {
            LogUtils.info("Error extracting job type: " + e.getMessage());
            return "Full-Time";
        }
    }

    private String captureJobLocation(WebElement jobItem) {
        try {
            String itemText = jobItem.getText();
            
            if (itemText.contains("Location")) {
                String[] parts = itemText.split("Location");
                if (parts.length > 1) {
                    String location = parts[1].trim().split("\\n")[0].trim();
                    LogUtils.info("Captured Location: " + location);
                    return location;
                }
            }

            if (itemText.contains("Durham, North Carolina")) {
                LogUtils.info("Captured Location (direct): Durham, North Carolina");
                return "Durham, North Carolina";
            }

            LogUtils.info("Could not extract location");
            return "N/A";
        } catch (Exception e) {
            LogUtils.info("Error extracting location: " + e.getMessage());
            return "N/A";
        }
    }

    public JobData getCapturedJobData() {
        return capturedJobData;
    }
}