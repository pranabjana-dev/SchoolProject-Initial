package com.school.model;

public class Discount {
    private String id;
    private String name;
    private String category;        // EARLY_BIRD | LOYALTY
    private String discountType;    // FIXED | PERCENTAGE
    private double value;           // Flat amount or percentage value
    private String description;
    private String academicYear;    // Optional: scope to a year, or "ALL" for all years
    private boolean active;
    private long createdAt;

    public Discount() {}

    // ---- Getters & Setters ----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
