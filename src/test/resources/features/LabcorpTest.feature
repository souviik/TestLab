Feature: Labcorp Career Job Search and Application Test

  @Regression @LabcorpTest @TestAutomationSvk @Labcorp
  Scenario: Search for Full Stack Developer job, validate details and navigate to application
    Given User navigates to "https://www.labcorp.com"
    And User accepts cookies if popup appears
    When User clicks on the "Careers" link
    Then User should verify the URL is "https://careers.labcorp.com/global/en"
    When User enters "Full Stack Developer" in the search bar
    And User clicks the search button
    Then User should see search results for "Full Stack Developer" in the list
    When User validates job details including "Job Title, Category, Job ID, Job Type"
    And User captures job details from the listing for "Full Stack Developer"
    And User clicks on the job position
    Then User should be on the Job Posting page
    And User should verify captured job details match the job posting page
    And User should verify "Requirements" section text "Bachelor’s degree in computer science or equivalent technical work experience."
    And User should verify "Requirements" section text "Experience with agile development tools such as Git"
    And User should verify "Essential Duties and Responsibilities" section text "Design and implement RESTful API endpoints using Spring Boot, Oracle JSON, and containerized Kubernetes microservices architecture"
    When User clicks on "Apply Now" button
    Then User should see the job title "Full Stack Developer" on application page
    When User returns to job listing page
    Then User should navigate back to job search page