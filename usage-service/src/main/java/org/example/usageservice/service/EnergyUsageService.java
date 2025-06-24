package org.example.usageservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.usageservice.model.HourlyEnergyUsage;
import org.example.usageservice.repository.EnergyUsageRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class EnergyUsageService {

    private final EnergyUsageRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RabbitTemplate rabbitTemplate;

    public EnergyUsageService(EnergyUsageRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "energy-queue")
    public void receive(String message) throws Exception {
        JsonNode json = objectMapper.readTree(message);
        String type = json.get("type").asText();
        String association = json.get("association").asText();
        double kwh = json.get("kwh").asDouble();
        LocalDateTime dt = LocalDateTime.parse(json.get("datetime").asText()).truncatedTo(ChronoUnit.HOURS);

        HourlyEnergyUsage usage = repository.findById(dt).orElseGet(() -> {
            HourlyEnergyUsage u = new HourlyEnergyUsage();
            u.setHour(dt);
            u.setCommunityProduced(0);
            u.setCommunityUsed(0);
            u.setGridUsed(0);
            return u;
        });

        if (type.equals("PRODUCER") && association.equals("COMMUNITY")) {
            usage.setCommunityProduced(usage.getCommunityProduced() + kwh);
        } else if (type.equals("USER") && association.equals("COMMUNITY")) {
            double available = usage.getCommunityProduced() - usage.getCommunityUsed();

            if (available >= kwh) {
                // gesamte Energie kommt aus der Community
                usage.setCommunityUsed(usage.getCommunityUsed() + kwh);
            } else {
                // Teilweise oder vollständig aus dem Netz
                double usedFromCommunity = Math.max(available, 0);
                double usedFromGrid = kwh - usedFromCommunity;

                usage.setCommunityUsed(usage.getCommunityUsed() + usedFromCommunity);
                usage.setGridUsed(usage.getGridUsed() + usedFromGrid);
            }
        }

        repository.save(usage);

        // Sende Update-Nachricht an CurrentPercentageService
        rabbitTemplate.convertAndSend("energy-updates", "updated:" + dt.toString());
        System.out.println("Sent message to energy-updates: " + dt);
    }
}
