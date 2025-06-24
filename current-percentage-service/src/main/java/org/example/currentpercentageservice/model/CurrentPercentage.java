package org.example.currentpercentageservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "current_percentage")
public class CurrentPercentage {

    @Getter
    @Setter
    @Id
    private LocalDateTime hour;

    @Getter
    @Setter
    private double communityDepleted;

    @Getter
    @Setter
    private double gridPortion;



}
