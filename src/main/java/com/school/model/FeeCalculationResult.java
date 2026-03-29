package com.school.model;

import java.util.List;

public class FeeCalculationResult {

    // Student info
    private String studentName;
    private String dateOfBirth;
    private String joiningDate;

    // Age display
    private int ageYears;
    private int ageMonths;
    private int ageDays;
    private int totalAgeMonths;
    private String ageDisplay;           // "3 years, 2 months, 15 days"

    // Program recommendations
    private String govtRecommendedProgram;
    private int govtAgeMonths;           // Age used for govt calculation
    private String govtJune1Date;        // The June 1 reference date used
    private String actualAgeProgram;
    private int actualAgeMonths;         // Age used for actual calculation
    private String selectedProgram;      // Which program fee is calculated for

    // Academic year
    private String academicYear;         // e.g., "2024-25"
    private String endDate;              // "March 31, 2025"

    // Duration
    private int durationMonths;
    private String durationDescription; // "June 2024 to March 2025 (10 months)"

    // Fees
    private boolean feeConfigured;       // false if no fee defined for this program/year
    private double fixedComponent;
    private double sessionFees;
    private double totalBaseFees;        // fixedComponent + (sessionFees * durationMonths)

    // Discounts
    private List<Discount> appliedDiscounts;
    private double totalDiscountAmount;
    private double discountedFees;       // totalBaseFees - totalDiscountAmount

    // Payment schedule
    private double bookingAmount;        // = fixedComponent
    private double installment1;         // 40% of (discountedFees - fixedComponent)
    private double installment2;         // 30% of (discountedFees - fixedComponent)
    private double installment3;         // 30% of (discountedFees - fixedComponent)
    private double totalPayable;         // = discountedFees

    // ---- Getters & Setters ----
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getJoiningDate() { return joiningDate; }
    public void setJoiningDate(String joiningDate) { this.joiningDate = joiningDate; }

    public int getAgeYears() { return ageYears; }
    public void setAgeYears(int ageYears) { this.ageYears = ageYears; }

    public int getAgeMonths() { return ageMonths; }
    public void setAgeMonths(int ageMonths) { this.ageMonths = ageMonths; }

    public int getAgeDays() { return ageDays; }
    public void setAgeDays(int ageDays) { this.ageDays = ageDays; }

    public int getTotalAgeMonths() { return totalAgeMonths; }
    public void setTotalAgeMonths(int totalAgeMonths) { this.totalAgeMonths = totalAgeMonths; }

    public String getAgeDisplay() { return ageDisplay; }
    public void setAgeDisplay(String ageDisplay) { this.ageDisplay = ageDisplay; }

    public String getGovtRecommendedProgram() { return govtRecommendedProgram; }
    public void setGovtRecommendedProgram(String govtRecommendedProgram) { this.govtRecommendedProgram = govtRecommendedProgram; }

    public int getGovtAgeMonths() { return govtAgeMonths; }
    public void setGovtAgeMonths(int govtAgeMonths) { this.govtAgeMonths = govtAgeMonths; }

    public String getGovtJune1Date() { return govtJune1Date; }
    public void setGovtJune1Date(String govtJune1Date) { this.govtJune1Date = govtJune1Date; }

    public String getActualAgeProgram() { return actualAgeProgram; }
    public void setActualAgeProgram(String actualAgeProgram) { this.actualAgeProgram = actualAgeProgram; }

    public int getActualAgeMonths() { return actualAgeMonths; }
    public void setActualAgeMonths(int actualAgeMonths) { this.actualAgeMonths = actualAgeMonths; }

    public String getSelectedProgram() { return selectedProgram; }
    public void setSelectedProgram(String selectedProgram) { this.selectedProgram = selectedProgram; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public int getDurationMonths() { return durationMonths; }
    public void setDurationMonths(int durationMonths) { this.durationMonths = durationMonths; }

    public String getDurationDescription() { return durationDescription; }
    public void setDurationDescription(String durationDescription) { this.durationDescription = durationDescription; }

    public boolean isFeeConfigured() { return feeConfigured; }
    public void setFeeConfigured(boolean feeConfigured) { this.feeConfigured = feeConfigured; }

    public double getFixedComponent() { return fixedComponent; }
    public void setFixedComponent(double fixedComponent) { this.fixedComponent = fixedComponent; }

    public double getSessionFees() { return sessionFees; }
    public void setSessionFees(double sessionFees) { this.sessionFees = sessionFees; }

    public double getTotalBaseFees() { return totalBaseFees; }
    public void setTotalBaseFees(double totalBaseFees) { this.totalBaseFees = totalBaseFees; }

    public List<Discount> getAppliedDiscounts() { return appliedDiscounts; }
    public void setAppliedDiscounts(List<Discount> appliedDiscounts) { this.appliedDiscounts = appliedDiscounts; }

    public double getTotalDiscountAmount() { return totalDiscountAmount; }
    public void setTotalDiscountAmount(double totalDiscountAmount) { this.totalDiscountAmount = totalDiscountAmount; }

    public double getDiscountedFees() { return discountedFees; }
    public void setDiscountedFees(double discountedFees) { this.discountedFees = discountedFees; }

    public double getBookingAmount() { return bookingAmount; }
    public void setBookingAmount(double bookingAmount) { this.bookingAmount = bookingAmount; }

    public double getInstallment1() { return installment1; }
    public void setInstallment1(double installment1) { this.installment1 = installment1; }

    public double getInstallment2() { return installment2; }
    public void setInstallment2(double installment2) { this.installment2 = installment2; }

    public double getInstallment3() { return installment3; }
    public void setInstallment3(double installment3) { this.installment3 = installment3; }

    public double getTotalPayable() { return totalPayable; }
    public void setTotalPayable(double totalPayable) { this.totalPayable = totalPayable; }
}
