Feature: Beeceptor Echo Service API Testing

  # This feature tests the Beeceptor echo service with both GET and POST requests
  # GET request validates echo response fields like path, ip, and headers
  # POST request validates order data accuracy including customer, payment, and product information

  @Regression @Labcorp @APITest @BeeceptorOrder @TestAutomationSvk
  Scenario: Beeceptor API validation with both GET and POST
    # Test GET request
    Given User sends GET request to Beeceptor echo endpoint "https://echo.free.beeceptor.com/sample-request?author=beeceptor"
    When User receives the API response
    Then User should verify the response status code is 200
    And User should verify response contains request headers
    And User should verify response includes field "path"
    And User should verify response contains IP address
    And User should verify all required echo fields are present
    And User should verify the response time is less than 10 seconds
    And User should verify the response contains valid JSON data
    # Test POST request with order data
    Given User sends POST request to Beeceptor endpoint "http://echo.free.beeceptor.com/sample-request?author=beeceptor" with order data
    When User receives the API response
    Then User should verify the response status code is 200
    And User should verify order ID is "12345"
    And User should verify order status is "processing"
    And User should verify response includes field "ip"
    And User should verify customer information accuracy
    And User should verify payment details accuracy
    And User should verify product information accuracy