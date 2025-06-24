package org.example.usageservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "hourly_energy_usage")
public class HourlyEnergyUsage {
    @Id
    private LocalDateTime hour;

    private double communityProduced;
    private double communityUsed;
    private double gridUsed;

    // Getters & setters
    public LocalDateTime getHour() { return hour; }
    public void setHour(LocalDateTime hour) { this.hour = hour; }

    public double getCommunityProduced() { return communityProduced; }
    public void setCommunityProduced(double value) { this.communityProduced = value; }

    public double getCommunityUsed() { return communityUsed; }
    public void setCommunityUsed(double value) { this.communityUsed = value; }

    public double getGridUsed() { return gridUsed; }
    public void setGridUsed(double value) { this.gridUsed = value; }
}
