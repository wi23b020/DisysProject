package org.example.energygui;

public class EnergyData {
    private String day;
    private double communityProduced;
    private double communityUsed;
    private double gridUsed;

    public String getDay() {
        return day;
    }

    public double getCommunityProduced() {
        return communityProduced;
    }

    public double getCommunityUsed() {
        return communityUsed;
    }

    public double getGridUsed() {
        return gridUsed;
    }

    @Override
    public String toString() {
        return String.format("Day: %s\nProduced: %.2f\nUsed: %.2f\nGrid: %.2f",
                day, communityProduced, communityUsed, gridUsed);
    }
}
