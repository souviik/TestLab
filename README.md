# TestX - Modern Test Automation Framework

A comprehensive test automation framework built with Java, Selenium WebDriver, Cucumber BDD, and TestNG. This framework demonstrates modern testing practices through real-world Web UI automation and REST API testing scenarios.

**Author**: [Souvik Das](https://www.linkedin.com/in/souviik/)

## What's Included

This framework showcases two complete test automation scenarios:

### Web UI Automation - Labcorp Career Site
- **Complete job search workflow**: Navigate, search, validate, and apply
- **Dynamic job data capture**: Extract and validate job details across pages  
- **Real-world interactions**: Handle cookies, popups, form submissions
- **Comprehensive validations**: Job titles, requirements, application flows

### REST API Testing - Beeceptor Echo Service  
- **GET request validation**: Path, IP, headers verification
- **POST data accuracy**: Complete order data validation (customer, payment, products)
- **JSON response parsing**: Complex nested object validation
- **Error handling**: Robust type casting and floating-point precision

## Key Features
- **Page Object Model** design pattern for maintainable UI tests
- **Behavior-Driven Development** with Cucumber and Gherkin syntax
- **Cross-browser support** (Chrome, Firefox, Edge) with headless options  
- **Multiple reporting formats** (Extent, Allure, Cucumber HTML)
- **ThreadSafe execution** with parallel testing capabilities
- **Advanced element handling** with dynamic waits and JavaScript fallbacks
- **Comprehensive logging** and screenshot capture on failures

## System Requirements

- **Java JDK 17** or higher
- **Maven 3.6** or higher  
- **Chrome browser** (default, others supported)
- **Git** for version control

### Optional Tools
- **Allure Reports** for interactive reporting
- **IntelliJ IDEA** with run configurations

## Quick Start

### 1. Clone and Setup
```bash
git clone https://github.com/souviik/TestX.git
cd TestX
mvn clean install
```

### 2. Run Tests
```bash
# Run Labcorp web UI tests only
mvn clean test -Dtest=TestRunnerLabcorp

# Run Beeceptor API tests only
mvn clean test -Dtest=TestRunnerBeeceptor

# Run all tests
mvn clean test -Dcucumber.filter.tags="@Labcorp"
```

### 3. View Reports
- **Extent Reports**: `exports/reports/CucumberExtentReports/CucumberExtentReports.html` (opens automatically)
- **Allure Reports**: `mvn allure:serve target/allure-results`

## Framework Architecture

### Design Patterns
- **Page Object Model**: Clean separation of page elements and test logic
- **Factory Pattern**: WebDriver management with ThreadLocal support for parallel execution
- **Builder Pattern**: Complex test data construction (order objects, job data)

### Core Components
- **WebUI Library**: Common interactions (clicks, waits, validations)
- **BaseAPIClient**: REST Assured wrapper with enhanced logging
- **Driver Management**: Browser-agnostic WebDriver handling
- **Properties Management**: Centralized configuration system
- **Logging System**: Comprehensive execution tracking with Log4j

### Test Organization
```
src/test/java/com/sdastest/projects/
├── website/labcorp/           # Web UI Tests
│   ├── pages/                 # Page Object classes
│   ├── models/                # Data models (JobData)
│   └── stepdefinitions/       # Cucumber steps
├── api/beeceptor/            # API Tests  
│   ├── client/               # API client classes
│   ├── models/               # Data models (OrderData, Customer, etc.)
│   └── stepdefinitions/      # Cucumber steps
└── runners/                  # TestNG test runners
```

## Test Scenarios

###Labcorp Career Site Testing
**Feature**: Complete job application workflow  
**Location**: `src/test/resources/features/LabcorpTest.feature`

**Key Capabilities**:
- Dynamic job data extraction and cross-page validation
- Cookie consent handling and popup management  
- Real-world form interactions and navigation flows
- Comprehensive job requirement text validation

### Beeceptor API Testing  
**Feature**: REST API validation with complex data structures  
**Location**: `src/test/resources/features/BeeceptorTest.feature`

```gherkin  
Scenario: Beeceptor API validation with both GET and POST
  # GET Request Validation
  Given User sends GET request to Beeceptor echo endpoint
  Then User should verify response includes field "path"
  And User should verify response contains IP address
  
  # POST Request with Order Data
  Given User sends POST request with order data
  Then User should verify customer information accuracy  
  And User should verify payment details accuracy
  And User should verify product information accuracy
```

**Key Capabilities**:
- Complex JSON parsing with nested object validation
- Floating-point precision handling for monetary values
- Robust type casting for different numeric formats
- Comprehensive error handling and detailed logging

## Advanced Usage

### Running Specific Tests
```bash
# Run all tests with @Labcorp tag
mvn clean test -Dcucumber.filter.tags="@Labcorp"

# Run with different browsers
mvn clean test -Dcucumber.filter.tags="@Labcorp" -Dbrowser=firefox
mvn clean test -Dcucumber.filter.tags="@Labcorp" -Dbrowser=edge

# Run in headless mode  
mvn clean test -Dcucumber.filter.tags="@Labcorp" -Dheadless=true

# Run with specific tags
mvn clean test -Dcucumber.filter.tags="@BeeceptorOrder"
mvn clean test -Dcucumber.filter.tags="@LabcorpTest"

# Run with TestNG suite files
mvn test -DsuiteXmlFile=src/test/resources/suites/SuiteFeatureLabcorp.xml
```

### IDE Integration (IntelliJ IDEA)
Pre-configured run configurations available in `.idea/runConfigurations/`:
- **LabcorpTests - Run**: Normal execution
- **LabcorpTests - Debug**: Debug mode with port 5005  
- **BeeceptorTests - Run**: Normal execution
- **BeeceptorTests - Debug**: Debug mode with port 5005

### Configuration Options
Example Properties settings in `config/config.properties`:
```properties
BROWSER=chrome              # Browser selection
HEADLESS=false              # Headless execution
OPEN_REPORTS_AFTER_EXECUTION=yes  # Auto-open reports
SCREENSHOT_FAILED_STEPS=no  # Failure screenshots
WAIT_EXPLICIT=10            # Default explicit wait
```

## Project Structure

```
TestX/
├── src/
│   ├── main/java/com/sdastest/
│   │   ├── config/             # Configuration management
│   │   ├── constants/          # Framework constants
│   │   ├── driver/            # WebDriver management
│   │   ├── helpers/           # Utility helper classes
│   │   ├── keywords/          # Main WebUI keyword library
│   │   ├── reports/           # Reporting utilities
│   │   └── utils/             # Common utility classes
│   └── test/
│       ├── java/com/sdastest/
│       │   ├── common/         # Base test classes
│       │   ├── hooks/          # Cucumber hooks
│       │   ├── listeners/      # TestNG listeners
│       │   ├── projects/       # Test implementations
│       │   │   ├── api/beeceptor/      # Beeceptor API tests
│       │   │   └── website/labcorp/    # Labcorp web UI tests
│       │   └── runners/        # Test runner classes
│       └── resources/
│           ├── config/         # Configuration files
│           ├── features/       # Cucumber feature files
│           ├── objects/        # Element locators and endpoints
│           ├── suites/         # TestNG suite XML files
│           └── testdata/       # Test data files
├── exports/                    # Generated reports and artifacts
├── target/                     # Build outputs and test results
├── pom.xml                     # Maven configuration
└── README.md                   # Project documentation
```

## Version Control
This project uses Git for version control with clean, focused commits and clear branch management:
- Feature branches for new test implementations  
- Descriptive commit messages following conventional practices
- Regular integration with main branch
- CI/CD ready structure for automated pipeline integration
