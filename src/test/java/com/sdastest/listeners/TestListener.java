package com.sdastest.listeners;

import com.sdastest.annotations.FrameworkAnnotation;
import com.sdastest.constants.FrameworkConstants;
import com.sdastest.driver.DriverManager;
import com.sdastest.enums.AuthorType;
import com.sdastest.enums.Browser;
import com.sdastest.enums.CategoryType;
import com.sdastest.helpers.CaptureHelpers;
import com.sdastest.helpers.PropertiesHelpers;
import com.sdastest.helpers.ScreenRecoderHelpers;
import com.sdastest.keywords.WebUI;
import com.sdastest.reports.AllureManager;
import com.sdastest.reports.ExtentReportManager;
import com.sdastest.utils.BrowserInfoUtils;
import com.sdastest.utils.LogUtils;
import com.sdastest.utils.ZipUtils;
import com.aventstack.extentreports.Status;
import com.github.automatedowl.tools.AllureEnvironmentWriter;
import com.google.common.collect.ImmutableMap;
import org.testng.*;

import java.awt.*;
import java.io.IOException;

import static com.sdastest.constants.FrameworkConstants.*;

public class TestListener implements ITestListener, ISuiteListener, IInvokedMethodListener {

    static int count_totalTCs;
    static int count_passedTCs;
    static int count_skippedTCs;
    static int count_failedTCs;

    private ScreenRecoderHelpers screenRecorder;

    public TestListener() {
        try {
            screenRecorder = new ScreenRecoderHelpers();
        } catch (IOException | AWTException e) {
            System.out.println(e.getMessage());
        }
    }

    public String getTestName(ITestResult result) {
        return result.getTestName() != null ? result.getTestName() : result.getMethod().getConstructorOrMethod().getName();
    }

    public String getTestDescription(ITestResult result) {
        return result.getMethod().getDescription() != null ? result.getMethod().getDescription() : getTestName(result);
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        // Before every method in the Test Class
        //System.out.println(method.getTestMethod().getMethodName());
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        // After every method in the Test Class
        //System.out.println(method.getTestMethod().getMethodName());
    }


    @Override
    public void onStart(ISuite iSuite) {
        System.out.println("========= INSTALLING CONFIGURATION DATA =========");
        PropertiesHelpers.loadAllFiles();
        AllureManager.setAllureEnvironmentInformation();
        ExtentReportManager.initReports();
        System.out.println("========= INSTALLED CONFIGURATION DATA =========");
        System.out.println("");
        LogUtils.info("Starting Suite: " + iSuite.getName());
    }

    @Override
    public void onFinish(ISuite iSuite) {
        LogUtils.info("End Suite: " + iSuite.getName());
        WebUI.stopSoftAssertAll();
        //End Suite and execute Extents Report
        ExtentReportManager.flushReports();
        //Zip Folder reports
        ZipUtils.zipReportFolder();
        //Write information in Allure Report
        AllureEnvironmentWriter.allureEnvironmentWriter(
                ImmutableMap.<String, String>builder().
                        put("Test URL", FrameworkConstants.MY_PROFILE).
                        put("Target Execution", FrameworkConstants.TARGET).
                        put("Global Timeout", String.valueOf(FrameworkConstants.WAIT_DEFAULT)).
                        put("Page Load Timeout", String.valueOf(FrameworkConstants.WAIT_PAGE_LOADED)).
                        put("Headless Mode", FrameworkConstants.HEADLESS).
                        put("Local Browser", String.valueOf(Browser.CHROME)).
                        put("Remote URL", FrameworkConstants.REMOTE_URL).
                        put("Remote Port", FrameworkConstants.REMOTE_PORT).
                        put("TCs Total", String.valueOf(count_totalTCs)).
                        put("TCs Passed", String.valueOf(count_passedTCs)).
                        put("TCs Skipped", String.valueOf(count_skippedTCs)).
                        put("TCs Failed", String.valueOf(count_failedTCs)).
                        build());
        

    }

    public AuthorType[] getAuthorType(ITestResult iTestResult) {
        if (iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(FrameworkAnnotation.class) == null) {
            return null;
        }
        AuthorType authorType[] = iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(FrameworkAnnotation.class).author();
        return authorType;
    }

    public CategoryType[] getCategoryType(ITestResult iTestResult) {
        if (iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(FrameworkAnnotation.class) == null) {
            return null;
        }
        CategoryType categoryType[] = iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(FrameworkAnnotation.class).category();
        return categoryType;
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {
        LogUtils.info("Test case: " + getTestDescription(iTestResult) + " is starting...");
        count_totalTCs = count_totalTCs + 1;

        ExtentReportManager.createTest(iTestResult.getName());
        ExtentReportManager.addAuthors(getAuthorType(iTestResult));
        ExtentReportManager.addCategories(getCategoryType(iTestResult));
        ExtentReportManager.addDevices();

        ExtentReportManager.info(BrowserInfoUtils.getOSInfo());

        if (VIDEO_RECORD.toLowerCase().trim().equals(YES)) {
            screenRecorder.startRecording(getTestName(iTestResult));
        }

    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        LogUtils.info("Test case: " + getTestDescription(iTestResult) + " is passed.");
        count_passedTCs = count_passedTCs + 1;

        if (SCREENSHOT_PASSED_STEPS.equals(YES)) {
            CaptureHelpers.captureScreenshot(DriverManager.getDriver(), getTestName(iTestResult));
        }

        //AllureManager.saveTextLog("Test case: " + getTestName(iTestResult) + " is passed.");
        //ExtentReports log operation for passed tests.
        ExtentReportManager.logMessage(Status.PASS, "Test case: " + getTestName(iTestResult) + " is passed.");

        if (VIDEO_RECORD.trim().toLowerCase().equals(YES)) {
            screenRecorder.stopRecording(true);
        }
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        LogUtils.error("Test case: " + getTestDescription(iTestResult) + " is failed.");
        count_failedTCs = count_failedTCs + 1;

        if (SCREENSHOT_FAILED_STEPS.equals(YES)) {
            CaptureHelpers.captureScreenshot(DriverManager.getDriver(), getTestName(iTestResult));
        }

        //Allure reports screenshot file and log
        LogUtils.error("FAILED !! Screenshot for test case: " + getTestName(iTestResult));
        LogUtils.error(iTestResult.getThrowable());

        AllureManager.takeScreenshotToAttachOnAllureReport();

        //Extent reports screenshot file and log
        ExtentReportManager.addScreenShot(Status.FAIL, getTestName(iTestResult));
        ExtentReportManager.logMessage(Status.FAIL, iTestResult.getThrowable().toString());

        if (VIDEO_RECORD.toLowerCase().trim().equals(YES)) {
            screenRecorder.stopRecording(true);
        }

    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        LogUtils.warn("Test case: " + getTestDescription(iTestResult) + " is skipped.");
        count_skippedTCs = count_skippedTCs + 1;

        ExtentReportManager.logMessage(Status.SKIP, "Test case: " + getTestName(iTestResult) + " is skipped.");

        if (VIDEO_RECORD.toLowerCase().trim().equals(YES)) {
            screenRecorder.stopRecording(true);
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        LogUtils.error("Test failed but it is in defined success ratio: " + getTestDescription(iTestResult));
        ExtentReportManager.logMessage("Test failed but it is in defined success ratio: " + getTestDescription(iTestResult));
    }

}
