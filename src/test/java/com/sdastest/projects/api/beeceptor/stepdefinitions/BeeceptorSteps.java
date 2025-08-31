package com.sdastest.projects.api.beeceptor.stepdefinitions;

import com.sdastest.projects.api.beeceptor.client.BeeceptorAPIClient;
import com.sdastest.projects.api.beeceptor.models.OrderData;
import com.sdastest.utils.LogUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

public class BeeceptorSteps {
    
    private BeeceptorAPIClient beeceptorClient;
    private Response apiResponse;
    private OrderData sentOrderData;
    
    public BeeceptorSteps() {
        beeceptorClient = new BeeceptorAPIClient();
    }
    
    @Given("User sends GET request to Beeceptor echo endpoint {string}")
    public void userSendsGETRequestToBeeceptorEchoEndpoint(String endpoint) {
        LogUtils.info("Sending GET request to Beeceptor echo service");
        apiResponse = beeceptorClient.sendGetEchoRequest();
        LogUtils.info("GET request sent successfully to: " + endpoint);
    }
    
    @Given("User sends POST request to Beeceptor endpoint {string} with order data")
    public void userSendsPOSTRequestToBeeceptorEndpointWithOrderData(String endpoint) {
        LogUtils.info("Sending POST request to Beeceptor with order data");
        sentOrderData = OrderData.createSampleOrder();
        apiResponse = beeceptorClient.sendPostOrderRequest(sentOrderData);
        LogUtils.info("POST request sent successfully to: " + endpoint);
    }
    
    @When("User receives the API response")
    public void userReceivesTheAPIResponse() {
        Assert.assertNotNull(apiResponse, "API response should not be null");
        LogUtils.info("API response received successfully");
        LogUtils.info("Response Status Code: " + apiResponse.getStatusCode());
        LogUtils.info("Response Time: " + apiResponse.getTime() + "ms");
        LogUtils.info("Response Headers: " + apiResponse.getHeaders().toString());
        LogUtils.info("Response Body:");
        try {
            LogUtils.info(apiResponse.jsonPath().prettify());
        } catch (Exception e) {
            LogUtils.info(apiResponse.getBody().asString());
        }
    }
    
    @Then("User should verify the response status code is {int}")
    public void userShouldVerifyTheResponseStatusCodeIs(int expectedStatusCode) {
        beeceptorClient.validateStatusCode(expectedStatusCode);
        LogUtils.info("Status code validation passed: " + expectedStatusCode);
    }
    
    @Then("User should verify response includes field {string}")
    public void userShouldVerifyResponseIncludesField(String fieldName) {
        boolean hasField = beeceptorClient.hasEchoField(fieldName);
        Assert.assertTrue(hasField, "Response should contain field: " + fieldName);
        
        String fieldValue = beeceptorClient.getEchoField(fieldName);
        Assert.assertNotNull(fieldValue, "Field '" + fieldName + "' should not be null");
        
        LogUtils.info("Field validation passed for: " + fieldName + " = " + fieldValue);
    }
    
    @Then("User should verify response contains IP address")
    public void userShouldVerifyResponseContainsIPAddress() {
        boolean hasIp = beeceptorClient.hasEchoField("ip");
        Assert.assertTrue(hasIp, "Response should contain IP address field");
        
        String ipAddress = beeceptorClient.getEchoField("ip");
        Assert.assertNotNull(ipAddress, "IP address should not be null");
        Assert.assertFalse(ipAddress.trim().isEmpty(), "IP address should not be empty");
        
        LogUtils.info("IP address validation passed: " + ipAddress);
    }
    
    @Then("User should verify response contains request headers")
    public void userShouldVerifyResponseContainsRequestHeaders() {
        boolean hasHeaders = beeceptorClient.hasEchoField("headers");
        Assert.assertTrue(hasHeaders, "Response should contain headers field");
        
        Object headers = apiResponse.jsonPath().get("headers");
        Assert.assertNotNull(headers, "Headers should not be null");
        
        LogUtils.info("Request headers validation passed");
        LogUtils.info("Headers: " + headers.toString());
    }
    
    @Then("User should verify customer information accuracy")
    public void userShouldVerifyCustomerInformationAccuracy() {
        Assert.assertNotNull(sentOrderData, "Sent order data should not be null");
        Assert.assertNotNull(sentOrderData.getCustomer(), "Customer data should not be null");
        
        boolean customerValid = beeceptorClient.validateCustomerInformation(sentOrderData.getCustomer());
        Assert.assertTrue(customerValid, "Customer information should match the sent data");
        
        LogUtils.info("Customer information validation passed");
        LogUtils.info("Expected customer: " + sentOrderData.getCustomer().toString());
    }
    
    @Then("User should verify payment details accuracy")
    public void userShouldVerifyPaymentDetailsAccuracy() {
        Assert.assertNotNull(sentOrderData, "Sent order data should not be null");
        Assert.assertNotNull(sentOrderData.getPayment(), "Payment data should not be null");
        
        boolean paymentValid = beeceptorClient.validatePaymentDetails(sentOrderData.getPayment());
        Assert.assertTrue(paymentValid, "Payment details should match the sent data");
        
        LogUtils.info("Payment details validation passed");
        LogUtils.info("Expected payment: " + sentOrderData.getPayment().toString());
    }
    
    @Then("User should verify product information accuracy")
    public void userShouldVerifyProductInformationAccuracy() {
        Assert.assertNotNull(sentOrderData, "Sent order data should not be null");
        Assert.assertNotNull(sentOrderData.getItems(), "Items data should not be null");
        Assert.assertFalse(sentOrderData.getItems().isEmpty(), "Items list should not be empty");
        
        boolean productsValid = beeceptorClient.validateProductInformation(sentOrderData.getItems());
        Assert.assertTrue(productsValid, "Product information should match the sent data");
        
        LogUtils.info("Product information validation passed");
        LogUtils.info("Expected items: " + sentOrderData.getItems().toString());
    }
    
    @Then("User should verify all required echo fields are present")
    public void userShouldVerifyAllRequiredEchoFieldsArePresent() {
        boolean allFieldsPresent = beeceptorClient.hasRequiredEchoFields();
        Assert.assertTrue(allFieldsPresent, "All required echo fields (path, ip, headers) should be present");
        LogUtils.info("All required echo fields validation passed");
    }
    
    @Then("User should verify the response time is less than {int} seconds")
    public void userShouldVerifyTheResponseTimeIsLessThanSeconds(int maxSeconds) {
        long maxResponseTimeMs = maxSeconds * 1000L;
        beeceptorClient.validateResponseTime(maxResponseTimeMs);
        LogUtils.info("Response time validation passed: " + apiResponse.getTime() + "ms");
    }
    
    @Then("User should verify the response contains valid JSON data")
    public void userShouldVerifyTheResponseContainsValidJSONData() {
        try {
            apiResponse.jsonPath().get();
            LogUtils.info("JSON validation passed - response contains valid JSON");
        } catch (Exception e) {
            Assert.fail("Response does not contain valid JSON: " + e.getMessage());
        }
    }
    
    @Then("User should verify order ID is {string}")
    public void userShouldVerifyOrderIDIs(String expectedOrderId) {
        String actualOrderId = apiResponse.jsonPath().get("parsedBody.order_id");
        Assert.assertEquals(actualOrderId, expectedOrderId, "Order ID should match expected value");
        LogUtils.info("Order ID validation passed: " + actualOrderId);
    }
    
    @Then("User should verify order status is {string}")
    public void userShouldVerifyOrderStatusIs(String expectedStatus) {
        String actualStatus = apiResponse.jsonPath().get("parsedBody.order_status");
        Assert.assertEquals(actualStatus, expectedStatus, "Order status should match expected value");
        LogUtils.info("Order status validation passed: " + actualStatus);
    }
}