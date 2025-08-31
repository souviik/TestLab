package com.sdastest.projects.api.beeceptor.client;

import com.sdastest.projects.api.base.BaseAPIClient;
import com.sdastest.projects.api.beeceptor.models.*;
import com.sdastest.utils.LogUtils;
import io.restassured.response.Response;

import java.util.List;

public class BeeceptorAPIClient extends BaseAPIClient {
    
    private static final String BASE_URL = "https://echo.free.beeceptor.com";
    private static final String POST_BASE_URL = "http://echo.free.beeceptor.com";
    private static final String SAMPLE_ENDPOINT = "/sample-request?author=beeceptor";
    
    public Response sendGetEchoRequest() {
        LogUtils.info("Sending GET request to Beeceptor echo service");
        return sendGETRequest(BASE_URL, SAMPLE_ENDPOINT);
    }
    
    public Response sendPostOrderRequest(OrderData orderData) {
        LogUtils.info("Sending POST request to Beeceptor with order data");
        LogUtils.info("Order data: " + orderData.toString());
        return sendPOSTRequest(POST_BASE_URL, SAMPLE_ENDPOINT, orderData);
    }
    
    public void validateEchoResponse() {
        validateStatusCode(200);
        validateResponseTime(10000); // 10 seconds max
        LogUtils.info("Echo response validation completed successfully");
    }
    
    public boolean hasRequiredEchoFields() {
        Response response = getLastResponse();
        
        try {
            boolean hasPath = response.jsonPath().get("path") != null;
            boolean hasIp = response.jsonPath().get("ip") != null;
            boolean hasHeaders = response.jsonPath().get("headers") != null;
            
            LogUtils.info("Echo response field validation - Path: " + hasPath + ", IP: " + hasIp + ", Headers: " + hasHeaders);
            return hasPath && hasIp && hasHeaders;
        } catch (Exception e) {
            LogUtils.error("Error checking required echo fields: " + e.getMessage());
            return false;
        }
    }
    
    public boolean hasEchoField(String fieldName) {
        Response response = getLastResponse();
        
        try {
            Object value = response.jsonPath().get(fieldName);
            boolean exists = value != null;
            LogUtils.info("Echo field '" + fieldName + "' exists: " + exists);
            if (exists) {
                LogUtils.info("Field value: " + value.toString());
            }
            return exists;
        } catch (Exception e) {
            LogUtils.error("Error checking echo field '" + fieldName + "': " + e.getMessage());
            return false;
        }
    }
    
    public boolean validateCustomerInformation(Customer expectedCustomer) {
        Response response = getLastResponse();
        LogUtils.info("Response body:\n" + response.jsonPath().prettify());
        try {
            String actualName = response.jsonPath().get("parsedBody.customer.name");
            String actualEmail = response.jsonPath().get("parsedBody.customer.email");
            String actualPhone = response.jsonPath().get("parsedBody.customer.phone");
            
            boolean nameMatch = safeEquals(expectedCustomer.getName(), actualName);
            boolean emailMatch = safeEquals(expectedCustomer.getEmail(), actualEmail);
            boolean phoneMatch = safeEquals(expectedCustomer.getPhone(), actualPhone);
            
            LogUtils.info("Customer validation - Name: " + nameMatch + ", Email: " + emailMatch + ", Phone: " + phoneMatch);
            LogUtils.info("Expected: Name='" + expectedCustomer.getName() + "', Email='" + expectedCustomer.getEmail() + "', Phone='" + expectedCustomer.getPhone() + "'");
            LogUtils.info("Actual: Name='" + actualName + "', Email='" + actualEmail + "', Phone='" + actualPhone + "'");
            
            // Validate address
            boolean addressMatch = validateAddress(expectedCustomer.getAddress());
            
            return nameMatch && emailMatch && phoneMatch && addressMatch;
        } catch (Exception e) {
            LogUtils.error("Error validating customer information: " + e.getMessage());
            return false;
        }
    }
    
    private boolean validateAddress(Address expectedAddress) {
        Response response = getLastResponse();
        
        try {
            String actualStreet = response.jsonPath().get("parsedBody.customer.address.street");
            String actualCity = response.jsonPath().get("parsedBody.customer.address.city");
            String actualState = response.jsonPath().get("parsedBody.customer.address.state");
            String actualZipcode = response.jsonPath().get("parsedBody.customer.address.zipcode");
            String actualCountry = response.jsonPath().get("parsedBody.customer.address.country");
            
            boolean streetMatch = safeEquals(expectedAddress.getStreet(), actualStreet);
            boolean cityMatch = safeEquals(expectedAddress.getCity(), actualCity);
            boolean stateMatch = safeEquals(expectedAddress.getState(), actualState);
            boolean zipcodeMatch = safeEquals(expectedAddress.getZipcode(), actualZipcode);
            boolean countryMatch = safeEquals(expectedAddress.getCountry(), actualCountry);
            
            LogUtils.info("Address validation - Street: " + streetMatch + ", City: " + cityMatch + 
                         ", State: " + stateMatch + ", Zipcode: " + zipcodeMatch + ", Country: " + countryMatch);
            
            return streetMatch && cityMatch && stateMatch && zipcodeMatch && countryMatch;
        } catch (Exception e) {
            LogUtils.error("Error validating address: " + e.getMessage());
            return false;
        }
    }
    
    public boolean validatePaymentDetails(Payment expectedPayment) {
        Response response = getLastResponse();
        
        try {
            String actualMethod = response.jsonPath().get("parsedBody.payment.method");
            String actualTransactionId = response.jsonPath().get("parsedBody.payment.transaction_id");
            Object amountObj = response.jsonPath().get("parsedBody.payment.amount");
            String actualCurrency = response.jsonPath().get("parsedBody.payment.currency");
            
            // Handle both Float and Double types for amount
            double actualAmount = 0.0;
            if (amountObj instanceof Float) {
                actualAmount = ((Float) amountObj).doubleValue();
            } else if (amountObj instanceof Double) {
                actualAmount = (Double) amountObj;
            } else if (amountObj instanceof Number) {
                actualAmount = ((Number) amountObj).doubleValue();
            }
            
            boolean methodMatch = safeEquals(expectedPayment.getMethod(), actualMethod);
            boolean transactionMatch = safeEquals(expectedPayment.getTransaction_id(), actualTransactionId);
            // Use epsilon-based comparison for floating point values to handle precision issues
            boolean amountMatch = amountObj != null && Math.abs(expectedPayment.getAmount() - actualAmount) < 0.01;
            boolean currencyMatch = safeEquals(expectedPayment.getCurrency(), actualCurrency);
            
            LogUtils.info("Payment validation - Method: " + methodMatch + ", Transaction ID: " + transactionMatch + 
                         ", Amount: " + amountMatch + ", Currency: " + currencyMatch);
            LogUtils.info("Expected payment amount: " + expectedPayment.getAmount() + ", Actual: " + actualAmount + " (Type: " + (amountObj != null ? amountObj.getClass().getSimpleName() : "null") + ")");
            
            return methodMatch && transactionMatch && amountMatch && currencyMatch;
        } catch (Exception e) {
            LogUtils.error("Error validating payment details: " + e.getMessage());
            return false;
        }
    }
    
    public boolean validateProductInformation(List<Item> expectedItems) {
        Response response = getLastResponse();
        
        try {
            List<Object> actualItems = response.jsonPath().getList("parsedBody.items");
            
            if (actualItems.size() != expectedItems.size()) {
                LogUtils.error("Items count mismatch. Expected: " + expectedItems.size() + ", Actual: " + actualItems.size());
                return false;
            }
            
            for (int i = 0; i < expectedItems.size(); i++) {
                Item expectedItem = expectedItems.get(i);
                String actualProductId = response.jsonPath().get("parsedBody.items[" + i + "].product_id");
                String actualName = response.jsonPath().get("parsedBody.items[" + i + "].name");
                Object quantityObj = response.jsonPath().get("parsedBody.items[" + i + "].quantity");
                Object priceObj = response.jsonPath().get("parsedBody.items[" + i + "].price");
                
                // Handle different numeric types for quantity
                int actualQuantity = 0;
                if (quantityObj instanceof Integer) {
                    actualQuantity = (Integer) quantityObj;
                } else if (quantityObj instanceof Number) {
                    actualQuantity = ((Number) quantityObj).intValue();
                }
                
                // Handle different numeric types for price
                double actualPrice = 0.0;
                if (priceObj instanceof Float) {
                    actualPrice = ((Float) priceObj).doubleValue();
                } else if (priceObj instanceof Double) {
                    actualPrice = (Double) priceObj;
                } else if (priceObj instanceof Number) {
                    actualPrice = ((Number) priceObj).doubleValue();
                }
                
                boolean productIdMatch = safeEquals(expectedItem.getProduct_id(), actualProductId);
                boolean nameMatch = safeEquals(expectedItem.getName(), actualName);
                boolean quantityMatch = quantityObj != null && expectedItem.getQuantity() == actualQuantity;
                // Use epsilon-based comparison for floating point values to handle precision issues
                boolean priceMatch = priceObj != null && Math.abs(expectedItem.getPrice() - actualPrice) < 0.01;
                
                LogUtils.info("Item " + i + " validation - Product ID: " + productIdMatch + ", Name: " + nameMatch + 
                             ", Quantity: " + quantityMatch + ", Price: " + priceMatch);
                LogUtils.info("Item " + i + " expected - ID: '" + expectedItem.getProduct_id() + "', Name: '" + expectedItem.getName() + 
                             "', Quantity: " + expectedItem.getQuantity() + ", Price: " + expectedItem.getPrice());
                LogUtils.info("Item " + i + " actual - ID: '" + actualProductId + "', Name: '" + actualName + 
                             "', Quantity: " + actualQuantity + " (Type: " + (quantityObj != null ? quantityObj.getClass().getSimpleName() : "null") + 
                             "), Price: " + actualPrice + " (Type: " + (priceObj != null ? priceObj.getClass().getSimpleName() : "null") + ")");
                
                if (!productIdMatch || !nameMatch || !quantityMatch || !priceMatch) {
                    return false;
                }
            }
            
            LogUtils.info("All product information validated successfully");
            return true;
        } catch (Exception e) {
            LogUtils.error("Error validating product information: " + e.getMessage());
            return false;
        }
    }
    
    public String getEchoField(String fieldName) {
        Response response = getLastResponse();
        try {
            Object value = response.jsonPath().get(fieldName);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            LogUtils.error("Error getting echo field '" + fieldName + "': " + e.getMessage());
            return null;
        }
    }
    
    private boolean safeEquals(String str1, String str2) {
        if (str1 == null && str2 == null) return true;
        if (str1 == null || str2 == null) return false;
        return str1.equals(str2);
    }
}