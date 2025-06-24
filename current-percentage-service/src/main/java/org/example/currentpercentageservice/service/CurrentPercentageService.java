package org.example.currentpercentageservice.service;

import lombok.RequiredArgsConstructor;
import org.example.currentpercentageservice.model.CurrentPercentage;
import org.example.currentpercentageservice.model.HourlyEnergyUsage;
import org.example.currentpercentageservice.repository.CurrentPercentageRepository;
import org.example.currentpercentageservice.repository.HourlyUsageRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CurrentPercentageService {

    private final CurrentPercentageRepository repository;
    private final HourlyUsageRepository hourlyUsageRepository;

    @Transactional
    @RabbitListener(queues = "energy-usage-updates")
    public void calculatePercentage(String message) {
        LocalDateTime currentHour = LocalDateTime.now()
                .withMinute(0).withSecond(0).withNano(0);

        LocalDateTime from = currentHour;
        LocalDateTime to = currentHour.plusMinutes(59).plusSeconds(59);

        System.out.println("[CurrentPercentageService] Update received for hour: " + currentHour);

        HourlyEnergyUsage usage = hourlyUsageRepository.findByHourRange(from, to);

        if (usage == null) {
            System.out.println("No hourly usage found for hour range: " + from + " → " + to);
            return;
        }

        double produced = usage.getCommunityProduced();
        double used = usage.getCommunityUsed();

        System.out.printf("Hourly stats — Produced: %.2f, Used: %.2f%n", produced, used);

        double communityDepleted = (used >= produced) ? 100.0 : (used / produced) * 100;
        double gridPortion = (used > produced) ? ((used - produced) / used) * 100 : 0.0;

        CurrentPercentage cp = new CurrentPercentage();
        cp.setHour(currentHour);
        cp.setCommunityDepleted(communityDepleted);
        cp.setGridPortion(gridPortion);

        repository.save(cp);

        System.out.println("Saved current percentage for " + currentHour);
    }
}
