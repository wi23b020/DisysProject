package org.example.usageservice.repository;

import org.example.usageservice.model.HourlyEnergyUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface EnergyUsageRepository extends JpaRepository<HourlyEnergyUsage, LocalDateTime> {}
