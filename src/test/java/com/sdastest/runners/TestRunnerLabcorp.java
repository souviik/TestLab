package com.sdastest.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
@CucumberOptions(
        features = "src/test/resources/features/LabcorpTest.feature",
        glue = {
                "com.sdastest.projects.website.labcorp.stepdefinitions",
                "com.sdastest.hooks",
                "com.sdastest.common"
        },
        plugin = {
                "com.sdastest.hooks.CucumberListener",
                "pretty",
                "html:target/cucumber-reports/TestRunnerLabcorp.html",
                "json:target/cucumber-reports/TestRunnerLabcorp.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        },
        monochrome = true,
        tags = "@LabcorpTest"
)

public class TestRunnerLabcorp extends AbstractTestNGCucumberTests {
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}