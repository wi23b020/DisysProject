package com.energycommunity.energy_api.model;

public class CurrentData {
    private double communityDepleted;
    private double gridPortion;

    public CurrentData(double communityDepleted, double gridPortion) {
        this.communityDepleted = communityDepleted;
        this.gridPortion = gridPortion;
    }

    public double getCommunityDepleted() {
        return communityDepleted;
    }

    public void setCommunityDepleted(double communityDepleted) {
        this.communityDepleted = communityDepleted;
    }

    public double getGridPortion() {
        return gridPortion;
    }

    public void setGridPortion(double gridPortion) {
        this.gridPortion = gridPortion;
    }

    @Override
    public String toString() {
        return String.format("Community Pool: %.2f%%\nGrid Portion: %.2f%%", communityDepleted, gridPortion);
    }
}
