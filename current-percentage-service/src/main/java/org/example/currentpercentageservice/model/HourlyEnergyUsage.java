package org.example.currentpercentageservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
public class HourlyEnergyUsage {

    @Getter
    @Setter
    @Id
    private LocalDateTime hour;

    @Getter
    @Setter
    private double communityProduced;

    @Getter
    @Setter
    private double communityUsed;

    @Getter
    @Setter
    private double gridUsed;

}