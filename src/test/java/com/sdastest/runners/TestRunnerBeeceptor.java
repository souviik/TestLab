package com.sdastest.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
@CucumberOptions(
        features = "src/test/resources/features/BeeceptorTest.feature",
        glue = {
                "com.sdastest.projects.api.beeceptor.stepdefinitions",
                "com.sdastest.hooks"
        },
        plugin = {
                "com.sdastest.hooks.CucumberListener",
                "pretty",
                "html:target/cucumber-reports/TestRunnerBeeceptor.html",
                "json:target/cucumber-reports/TestRunnerBeeceptor.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        },
        monochrome = true,
        tags = "@BeeceptorEcho or @BeeceptorOrder or @BeeceptorComprehensive"
)

public class TestRunnerBeeceptor extends AbstractTestNGCucumberTests {
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}