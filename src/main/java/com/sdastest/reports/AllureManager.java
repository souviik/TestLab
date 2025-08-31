/*
 * Souvik Das
 * Automation Framework Selenium
 */

package com.sdastest.reports;

import com.sdastest.constants.FrameworkConstants;
import com.sdastest.driver.DriverManager;
import com.sdastest.enums.Browser;
import com.sdastest.utils.BrowserInfoUtils;
import com.github.automatedowl.tools.AllureEnvironmentWriter;
import com.google.common.collect.ImmutableMap;
import io.qameta.allure.Attachment;
import org.openqa.selenium.TakesScreenshot;

import static org.openqa.selenium.OutputType.BYTES;

public class AllureManager {

    private AllureManager() {
    }

    public static void setAllureEnvironmentInformation() {
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
                        build());

        System.out.println("Allure Reports is installed.");

    }

    @Attachment(value = "Failed test screenshot", type = "image/png")
    public static byte[] takeScreenshotToAttachOnAllureReport() {
        try {
            return ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(BYTES);
        } catch (Exception ex) {
            ex.getMessage();
        }
        return new byte[0];
    }

    @Attachment(value = "Take step screenshot", type = "image/png")
    public static byte[] takeScreenshotStep() {
        try {
            return ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(BYTES);
        } catch (Exception ex) {
            ex.getMessage();
        }
        return new byte[0];
    }

    @Attachment(value = "Browser Information", type = "text/plain")
    public static String addBrowserInformationOnAllureReport() {
        return BrowserInfoUtils.getOSInfo();
    }


    //Text attachments for Allure
    @Attachment(value = "{0}", type = "text/plain")
    public static String saveTextLog(String message) {
        return message;
    }

    //HTML attachments for Allure
    @Attachment(value = "{0}", type = "text/html")
    public static String attachHtml(String html) {
        return html;
    }

}
