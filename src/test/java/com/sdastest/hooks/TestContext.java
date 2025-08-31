package com.sdastest.hooks;

import com.sdastest.driver.DriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ThreadGuard;

public class TestContext {

    private WebDriver driver;

    public WebDriver getDriver() {
        return DriverManager.getDriver();
    }

}
