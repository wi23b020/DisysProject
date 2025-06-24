package org.example.energygui.model;

public class CurrentData {
    private String hour;
    private double communityDepleted;
    private double gridPortion;

    public CurrentData() {}  // Required for Jackson

    public String getHour() { return hour; }
    public void setHour(String hour) { this.hour = hour; }

    public double getCommunityDepleted() { return communityDepleted; }
    public void setCommunityDepleted(double communityDepleted) { this.communityDepleted = communityDepleted; }

    public double getGridPortion() { return gridPortion; }
    public void setGridPortion(double gridPortion) { this.gridPortion = gridPortion; }

    @Override
    public String toString() {
        return String.format("Hour: %s\nCommunity Depleted: %.2f%%\nGrid Portion: %.2f%%", hour, communityDepleted, gridPortion);
    }
}
