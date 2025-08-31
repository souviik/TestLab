package com.sdastest.projects.website.labcorp.pages;

import com.sdastest.helpers.PropertiesHelpers;
import com.sdastest.utils.LogUtils;
import org.openqa.selenium.By;

import static com.sdastest.keywords.WebUI.*;

public class LabcorpHomePage {

    private String pageUrl = "https://www.labcorp.com";
    private String pageTitle = "Lab Diagnostics & Drug Development, Global Life Sciences Leader | Labcorp";

    // Load locators from properties file with alternate locators in case of failure
    private By careersLink = By.linkText("Careers");
    private By careersLinkAlt = By.xpath("//a[contains(@href, 'careers.labcorp.com')]");
    private By careersLinkXpath = By.cssSelector("a[href*='careers.labcorp.com']");

    public LabcorpHomePage() {
        PropertiesHelpers.loadAllFiles();
    }

    public void navigateToLabcorp() {
        openWebsite(pageUrl);
        waitForPageLoaded();
        verifyContains(getCurrentUrl(), "labcorp.com", "Failed to navigate to Labcorp homepage");
        verifyContains(getPageTitle(), "Labcorp", "Labcorp page title not match");
    }

    public LabcorpCareersPage clickCareersLink() {
        boolean clicked = false;
        
        // Handle cookie consent first if present
        handleCookieConsent();
        
        By[] careersLinkLocators = {careersLink, careersLinkAlt, careersLinkXpath};
        
        for (By locator : careersLinkLocators) {
            try {
                waitForElementClickable(locator, 10);
                clickElement(locator);
                LogUtils.info("Clicked Careers link using locator: " + locator);
                clicked = true;
                break;
            } catch (Exception e) {
                LogUtils.info("Failed to click Careers link with locator: " + locator + " - " + e.getMessage());
                continue;
            }
        }
        
        if (!clicked) {
            throw new RuntimeException("Could not click Careers link with any method");
        }
        
        waitForPageLoaded();
        return new LabcorpCareersPage();
    }

    public void handleCookieConsent() {
        try {
            By cookieAcceptButton = By.xpath("//button[@id='onetrust-accept-btn-handler'][contains(text(),'Accept All Cookies')]");
            if (isElementVisible(cookieAcceptButton, 3)) {
                clickElement(cookieAcceptButton);
                sleep(1);
                LogUtils.info("Accepted cookies");
            }
        } catch (Exception e) {
            LogUtils.info("No cookie consent popup found or already handled");
        }
    }

    public void verifyPageLoaded() {
        waitForPageLoaded();
        boolean careersLinkFound = false;
        
        By[] careersLinkLocators = {careersLink, careersLinkAlt, careersLinkXpath};
        
        for (By locator : careersLinkLocators) {
            if (isElementVisible(locator, 5)) {
                careersLinkFound = true;
                break;
            }
        }
        
        if (careersLinkFound) {
            LogUtils.info("Labcorp homepage loaded successfully - Careers link found");
        } else {
            throw new RuntimeException("Labcorp homepage not loaded correctly - Careers link not found");
        }
    }
}