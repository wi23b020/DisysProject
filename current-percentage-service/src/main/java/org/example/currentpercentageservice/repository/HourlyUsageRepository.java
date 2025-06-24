package org.example.currentpercentageservice.repository;

import org.example.currentpercentageservice.model.HourlyEnergyUsage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface HourlyUsageRepository extends CrudRepository<HourlyEnergyUsage, LocalDateTime> {

    @Query("SELECT h FROM HourlyEnergyUsage h WHERE h.hour BETWEEN :from AND :to")
    HourlyEnergyUsage findByHourRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // 🆕 Fügt automatisch die neueste Stunde ein
    HourlyEnergyUsage findTopByOrderByHourDesc();
}
