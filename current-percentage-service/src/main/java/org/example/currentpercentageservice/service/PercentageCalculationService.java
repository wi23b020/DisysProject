package org.example.currentpercentageservice.service;

import org.example.currentpercentageservice.model.CurrentPercentage;
import org.example.currentpercentageservice.model.HourlyEnergyUsage;
import org.example.currentpercentageservice.repository.CurrentPercentageRepository;
import org.example.currentpercentageservice.repository.HourlyUsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PercentageCalculationService {

    @Autowired
    private HourlyUsageRepository hourlyUsageRepository;

    @Autowired
    private CurrentPercentageRepository currentPercentageRepository;

    public void calculateAndUpdateCurrentPercentage() {
        // Hole den aktuellsten Stunden-Eintrag
        HourlyEnergyUsage usage = hourlyUsageRepository.findTopByOrderByHourDesc();

        if (usage != null) {
            LocalDateTime hour = usage.getHour();
            double produced = usage.getCommunityProduced();
            double used = usage.getCommunityUsed();
            double grid = usage.getGridUsed();

            double communityDepleted;
            if (produced == 0.0) {
                communityDepleted = 0.0;
            } else {
                communityDepleted = (used >= produced) ? 100.0 : (used / produced) * 100.0;
            }

            double totalUsed = used + grid;
            double gridPortion = (totalUsed == 0.0) ? 0.0 : (grid / totalUsed) * 100.0;

            CurrentPercentage percentage = new CurrentPercentage();
            percentage.setHour(hour); // Nimm hour aus der DB, nicht aus LocalDateTime.now()
            percentage.setCommunityDepleted(communityDepleted);
            percentage.setGridPortion(gridPortion);
            currentPercentageRepository.save(percentage);

            System.out.printf("Saved percentage for hour: %s — CommunityDepleted: %.2f%%, GridPortion: %.2f%%%n",
                    hour, communityDepleted, gridPortion);
        } else {
            System.out.println("No hourly usage data available.");
        }
    }
}
