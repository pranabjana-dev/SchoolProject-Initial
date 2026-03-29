package com.school.model;

public class FeeStructure {
    private String id;
    private String programId;
    private String programName;
    private double fixedComponent;      // One-time or annual fixed fee (registration, etc.)
    private double sessionFees;         // Monthly recurring session fee
    private String academicYear;        // e.g., "2024-25"
    private String notes;               // Any additional notes
    private long updatedAt;

    public FeeStructure() {}

    // ---- Getters & Setters ----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProgramId() { return programId; }
    public void setProgramId(String programId) { this.programId = programId; }

    public String getProgramName() { return programName; }
    public void setProgramName(String programName) { this.programName = programName; }

    public double getFixedComponent() { return fixedComponent; }
    public void setFixedComponent(double fixedComponent) { this.fixedComponent = fixedComponent; }

    public double getSessionFees() { return sessionFees; }
    public void setSessionFees(double sessionFees) { this.sessionFees = sessionFees; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
