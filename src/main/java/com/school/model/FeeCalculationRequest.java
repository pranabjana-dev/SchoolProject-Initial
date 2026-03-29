package com.school.model;

import java.util.List;

public class FeeCalculationRequest {
    private String studentName;       // Optional
    private String dateOfBirth;       // Required: "yyyy-MM-dd"
    private String joiningDate;       // Required: "yyyy-MM-dd" (defaults to June 1)
    private List<String> discountIds; // IDs of selected discounts
    private boolean useGovtRecommended; // true = use govt program; false = use actual age program

    public FeeCalculationRequest() {}

    // ---- Getters & Setters ----
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getJoiningDate() { return joiningDate; }
    public void setJoiningDate(String joiningDate) { this.joiningDate = joiningDate; }

    public List<String> getDiscountIds() { return discountIds; }
    public void setDiscountIds(List<String> discountIds) { this.discountIds = discountIds; }

    public boolean isUseGovtRecommended() { return useGovtRecommended; }
    public void setUseGovtRecommended(boolean useGovtRecommended) { this.useGovtRecommended = useGovtRecommended; }
}
