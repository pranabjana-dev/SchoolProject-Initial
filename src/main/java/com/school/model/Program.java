package com.school.model;

public class Program {
    private String id;
    private String name;
    private int maxAgeMonths;       // Upper age limit for this program (in months)
    private int minAgeMonths;       // Lower age limit for this program (in months)
    private String description;
    private String ageRangeDisplay; // Human-readable age range, e.g., "Up to 20 months"
    private boolean active;
    private int displayOrder;

    public Program() {}

    public Program(String id, String name, int minAgeMonths, int maxAgeMonths,
                   String description, String ageRangeDisplay, boolean active, int displayOrder) {
        this.id = id;
        this.name = name;
        this.minAgeMonths = minAgeMonths;
        this.maxAgeMonths = maxAgeMonths;
        this.description = description;
        this.ageRangeDisplay = ageRangeDisplay;
        this.active = active;
        this.displayOrder = displayOrder;
    }

    // ---- Getters & Setters ----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getMaxAgeMonths() { return maxAgeMonths; }
    public void setMaxAgeMonths(int maxAgeMonths) { this.maxAgeMonths = maxAgeMonths; }

    public int getMinAgeMonths() { return minAgeMonths; }
    public void setMinAgeMonths(int minAgeMonths) { this.minAgeMonths = minAgeMonths; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAgeRangeDisplay() { return ageRangeDisplay; }
    public void setAgeRangeDisplay(String ageRangeDisplay) { this.ageRangeDisplay = ageRangeDisplay; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }
}
