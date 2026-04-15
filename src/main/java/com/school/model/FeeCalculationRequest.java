package com.school.model;

import java.util.List;

public class FeeCalculationRequest {
    private String studentName;
    private String dateOfBirth;       // Required: "yyyy-MM-dd"
    private String joiningDate;       // Required: "yyyy-MM-dd"
    private List<String> discountIds;
    private String programMethod;     // "govt" | "actual" | "parent"
    private String parentChoiceProgram; // programme name when method = "parent"

    // Kept for backward compatibility — derived from programMethod
    public boolean isUseGovtRecommended() {
        return "govt".equalsIgnoreCase(programMethod);
    }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getJoiningDate() { return joiningDate; }
    public void setJoiningDate(String joiningDate) { this.joiningDate = joiningDate; }

    public List<String> getDiscountIds() { return discountIds; }
    public void setDiscountIds(List<String> discountIds) { this.discountIds = discountIds; }

    public String getProgramMethod() { return programMethod; }
    public void setProgramMethod(String programMethod) { this.programMethod = programMethod; }

    public String getParentChoiceProgram() { return parentChoiceProgram; }
    public void setParentChoiceProgram(String parentChoiceProgram) { this.parentChoiceProgram = parentChoiceProgram; }
}
