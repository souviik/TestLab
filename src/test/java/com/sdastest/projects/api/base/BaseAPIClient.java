package com.sdastest.projects.api.base;

import com.sdastest.utils.LogUtils;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class BaseAPIClient {
    
    private static final String DEFAULT_CONTENT_TYPE = "application/json";
    private Response response;
    
    public BaseAPIClient() {
        configureRestAssured();
    }
    
    private void configureRestAssured() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
    
    public RequestSpecification getBaseRequestSpec() {
        return given()
                .contentType(DEFAULT_CONTENT_TYPE)
                .header("Accept", DEFAULT_CONTENT_TYPE);
    }
    
    public Response sendGETRequest(String endpoint) {
        LogUtils.info("Sending GET request to: " + endpoint);
        response = getBaseRequestSpec()
                .when()
                .get(endpoint);
        
        LogUtils.info("Response Status Code: " + response.getStatusCode());
        LogUtils.info("Response Time: " + response.getTime() + "ms");
        
        return response;
    }
    
    public Response sendGETRequest(String baseUrl, String endpoint) {
        String fullUrl = baseUrl + endpoint;
        return sendGETRequest(fullUrl);
    }
    
    public Response sendPOSTRequest(String endpoint, Object requestBody) {
        LogUtils.info("Sending POST request to: " + endpoint);
        response = getBaseRequestSpec()
                .body(requestBody)
                .when()
                .post(endpoint);
        
        LogUtils.info("Response Status Code: " + response.getStatusCode());
        LogUtils.info("Response Time: " + response.getTime() + "ms");
        
        return response;
    }
    
    public Response sendPOSTRequest(String baseUrl, String endpoint, Object requestBody) {
        String fullUrl = baseUrl + endpoint;
        return sendPOSTRequest(fullUrl, requestBody);
    }
    
    public Response getLastResponse() {
        return response;
    }
    
    public void validateStatusCode(int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        if (actualStatusCode != expectedStatusCode) {
            throw new AssertionError("Expected status code: " + expectedStatusCode + 
                                    ", but got: " + actualStatusCode);
        }
        LogUtils.info("Status code validation passed: " + actualStatusCode);
    }
    
    public void validateResponseTime(long maxResponseTimeMs) {
        long actualResponseTime = response.getTime();
        if (actualResponseTime > maxResponseTimeMs) {
            throw new AssertionError("Response time exceeded limit. Expected: <" + 
                                    maxResponseTimeMs + "ms, but got: " + actualResponseTime + "ms");
        }
        LogUtils.info("Response time validation passed: " + actualResponseTime + "ms");
    }
    
    public String getResponseBody() {
        return response.getBody().asString();
    }
    
    public <T> T getResponseAs(Class<T> clazz) {
        return response.as(clazz);
    }
}