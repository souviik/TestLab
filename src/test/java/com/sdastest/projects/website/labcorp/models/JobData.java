package com.sdastest.projects.website.labcorp.models;

import com.sdastest.utils.LogUtils;

/**
 * Data model class to store job information captured from job listings
 * and validate against job posting page details
 */
public class JobData {
    private String jobTitle;
    private String category;
    private String jobId;
    private String jobType;
    private String location;

    public JobData() {
    }

    public JobData(String jobTitle, String category, String jobId, String jobType, String location) {
        this.jobTitle = jobTitle;
        this.category = category;
        this.jobId = jobId;
        this.jobType = jobType;
        this.location = location;
    }

    // Getters and Setters
    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Validates that the captured job data has essential fields
     */
    public boolean isValid() {
        return jobTitle != null && !jobTitle.trim().isEmpty() &&
               jobId != null && !jobId.trim().isEmpty() &&
               category != null && !category.trim().isEmpty();
    }

    /**
     * Compares this JobData with another JobData object for validation
     */
    public boolean matches(JobData other) {
        if (other == null) {
            LogUtils.info("Cannot match against null JobData");
            return false;
        }

        boolean titleMatch = matchesIgnoreCaseAndWhitespace(this.jobTitle, other.jobTitle);
        boolean categoryMatch = matchesIgnoreCaseAndWhitespace(this.category, other.category);
        boolean jobIdMatch = matchesIgnoreCaseAndWhitespace(this.jobId, other.jobId);
        boolean jobTypeMatch = matchesIgnoreCaseAndWhitespace(this.jobType, other.jobType);

        LogUtils.info("Job Data Validation Results:");
        LogUtils.info("Title Match: " + titleMatch + " ('" + this.jobTitle + "' vs '" + other.jobTitle + "')");
        LogUtils.info("Category Match: " + categoryMatch + " ('" + this.category + "' vs '" + other.category + "')");
        LogUtils.info("Job ID Match: " + jobIdMatch + " ('" + this.jobId + "' vs '" + other.jobId + "')");
        LogUtils.info("Job Type Match: " + jobTypeMatch + " ('" + this.jobType + "' vs '" + other.jobType + "')");

        return titleMatch && categoryMatch && jobIdMatch && jobTypeMatch;
    }

    /**
     * Helper method to compare strings ignoring case and extra whitespace
     */
    private boolean matchesIgnoreCaseAndWhitespace(String str1, String str2) {
        if (str1 == null && str2 == null) return true;
        if (str1 == null || str2 == null) return false;
        
        String normalized1 = str1.trim().replaceAll("\\s+", " ").toLowerCase();
        String normalized2 = str2.trim().replaceAll("\\s+", " ").toLowerCase();
        
        return normalized1.equals(normalized2);
    }

    /**
     * Returns a detailed string representation of the job data
     */
    @Override
    public String toString() {
        return "JobData{" +
                "jobTitle='" + jobTitle + '\'' +
                ", category='" + category + '\'' +
                ", jobId='" + jobId + '\'' +
                ", jobType='" + jobType + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    /**
     * Creates a formatted string for logging captured job data
     */
    public String getFormattedDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Captured Job Data ===\n");
        sb.append("Job Title: ").append(jobTitle != null ? jobTitle : "N/A").append("\n");
        sb.append("Category: ").append(category != null ? category : "N/A").append("\n");
        sb.append("Job ID: ").append(jobId != null ? jobId : "N/A").append("\n");
        sb.append("Job Type: ").append(jobType != null ? jobType : "N/A").append("\n");
        sb.append("Location: ").append(location != null ? location : "N/A").append("\n");
        sb.append("========================");
        return sb.toString();
    }
}