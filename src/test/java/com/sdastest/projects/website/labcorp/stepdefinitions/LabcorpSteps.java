package com.sdastest.projects.website.labcorp.stepdefinitions;

import com.sdastest.keywords.WebUI;
import com.sdastest.projects.website.labcorp.pages.LabcorpHomePage;
import com.sdastest.projects.website.labcorp.pages.LabcorpCareersPage;
import com.sdastest.projects.website.labcorp.pages.LabcorpJobDetailsPage;
import com.sdastest.projects.website.labcorp.models.JobData;
import com.sdastest.utils.LogUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LabcorpSteps {

    private LabcorpHomePage labcorpHomePage;
    private LabcorpCareersPage careersPage;
    private LabcorpJobDetailsPage jobDetailsPage;
    private String originalWindowHandle;
    private JobData capturedJobData;

    public LabcorpSteps() {
        labcorpHomePage = new LabcorpHomePage();
        careersPage = new LabcorpCareersPage();
        jobDetailsPage = new LabcorpJobDetailsPage();
    }

    @Given("User navigates to {string}")
    public void userNavigatesToUrl(String url) {
        if (url.contains("labcorp.com")) {
            labcorpHomePage.navigateToLabcorp();
            labcorpHomePage.verifyPageLoaded();
            LogUtils.info("Successfully navigated to Labcorp homepage");
        } else {
            WebUI.openWebsite(url);
            WebUI.waitForPageLoaded();
            LogUtils.info("Navigated to: " + url);
        }
    }

    @Given("User accepts cookies if popup appears")
    public void userAcceptsCookiesIfPopupAppears() {
        labcorpHomePage.handleCookieConsent();
        LogUtils.info("Handled cookie consent if present");
    }

    @When("User clicks on the {string} link")
    public void userClicksOnTheLink(String linkText) {
        if (linkText.equalsIgnoreCase("Careers")) {
            careersPage = labcorpHomePage.clickCareersLink();
            LogUtils.info("Clicked on Careers link");
        } else {
            LogUtils.info("Link not implemented: " + linkText);
            throw new RuntimeException("Link not implemented: " + linkText);
        }
    }

    @Then("User should verify the URL is {string}")
    public void userShouldVerifyTheURLIs(String expectedUrl) {
        careersPage.verifyUrl(expectedUrl);
        LogUtils.info("URL verification successful: " + expectedUrl);
    }

    @When("User enters {string} in the search bar")
    public void userEntersInTheSearchBar(String searchTerm) {
        careersPage.searchForJob(searchTerm);
        LogUtils.info("Entered search term: " + searchTerm);
    }

    @When("User clicks the search button")
    public void userClicksTheSearchButton() {
        jobDetailsPage = careersPage.clickSearchButton();
        LogUtils.info("Clicked the search button");
    }

    @Then("User should see search results for {string} in the list")
    public void userShouldSeeSearchResultsFor(String searchTerm) {
        careersPage.verifyJobListingsDisplayed();
        LogUtils.info("Verified search results are displayed for: " + searchTerm);
    }

    @When("User validates job details including {string}")
    public void userValidatesJobDetailsIncluding(String fieldsToValidate) {
        String[] fields = fieldsToValidate.split(",");
        // Trim whitespace from each field
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }
        careersPage.validateSpecificJobDetails(fields);
        LogUtils.info("Job details validation completed for: " + fieldsToValidate);
    }

    @When("User clicks on the job position")
    public void userClicksOnTheFirstJobPosition() {
        jobDetailsPage = careersPage.clickFirstJob();
        LogUtils.info("Clicked on first job position");
    }

    @Then("User should be on the Job Posting page")
    public void userShouldBeOnTheJobPostingPage() {
        jobDetailsPage.verifyJobPostingPageLoaded();
        LogUtils.info("Job posting page loaded successfully");
    }

    @Then("User should verify {string} section text {string}")
    public void userShouldVerifyRequirementExists(String section, String requirementText) {
        jobDetailsPage.verifySpecificRequirement(section, requirementText);
        LogUtils.info("Requirement verified: " + requirementText);
    }

    @When("User clicks on {string} button")
    public void userClicksOnButton(String buttonName) {
        if (buttonName.equalsIgnoreCase("Apply Now")) {
            originalWindowHandle = jobDetailsPage.clickApplyNowButton();
            LogUtils.info("Clicked on Apply Now button");
        } else {
            LogUtils.info("Button not implemented: " + buttonName);
        }
    }

    @Then("User should see the job title {string} on application page")
    public void userShouldSeeTheJobTitleOnApplicationPage(String jobTitle) {
        jobDetailsPage.verifyJobTitleInNewTab(jobTitle);
        LogUtils.info("Job title verified on application page: " + jobTitle);
    }

    @When("User returns to job listing page")
    public void userReturnsToJobListingPage() {
        jobDetailsPage.returnToJobListingPage(originalWindowHandle);
        LogUtils.info("Returned to job listing page");
    }

    @Then("User should navigate back to job search page")
    public void userShouldNavigateBackToJobSearchPage() {
        jobDetailsPage.navigateBackToJobSearch();
        LogUtils.info("Navigated back to job search page");
    }

    @When("User captures job details from the listing for {string}")
    public void userCapturesJobDetailsFromFirstListing(String jobTitle) {
        capturedJobData = careersPage.captureJobDetailsFromListing();
        LogUtils.info("Captured job details for: " + jobTitle);
        if (capturedJobData != null && capturedJobData.isValid()) {
            LogUtils.info("Successfully captured job details from listing");
        } else {
            LogUtils.info("WARNING: Captured job data may be incomplete");
        }
    }

    @Then("User should verify captured job details match the job posting page")
    public void userShouldVerifyCapturedJobDetailsMatchJobPostingPage() {
        if (capturedJobData == null) {
            throw new RuntimeException("No job data was captured from the listings page. Cannot perform validation.");
        }
        
        jobDetailsPage.validateCapturedJobDetails(capturedJobData);
        LogUtils.info("Job data validation completed successfully - all fields match between listing and job posting pages");
    }
}