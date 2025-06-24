package org.example.energygui.model;

import java.time.LocalDate;

public class EnergyData {
    private LocalDate day;
    private double communityProduced;
    private double communityUsed;
    private double gridUsed;
    private String hour;




    public EnergyData() {} // Required for Jackson

    public String getHour() { return hour; }
    public void setHour(String hour) { this.hour = hour; }

    public LocalDate getDay() { return day; }
    public void setDay(LocalDate day) { this.day = day; }

    public double getCommunityProduced() { return communityProduced; }
    public void setCommunityProduced(double communityProduced) { this.communityProduced = communityProduced; }

    public double getCommunityUsed() { return communityUsed; }
    public void setCommunityUsed(double communityUsed) { this.communityUsed = communityUsed; }

    public double getGridUsed() { return gridUsed; }
    public void setGridUsed(double gridUsed) { this.gridUsed = gridUsed; }

    @Override
    public String toString() {
        return String.format("Day: %s, Produced: %.2f, Used: %.2f, Grid: %.2f",
                day != null ? day.toString() : "null",
                communityProduced, communityUsed, gridUsed);
    }
}
