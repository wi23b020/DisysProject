package org.example.currentpercentageservice.repository;

import org.example.currentpercentageservice.model.CurrentPercentage;
import org.springframework.data.repository.CrudRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface CurrentPercentageRepository extends CrudRepository<CurrentPercentage, LocalDateTime> {
    CurrentPercentage findTopByOrderByHourDesc();

    List<CurrentPercentage> findByHourBetween(LocalDateTime start, LocalDateTime end);
}