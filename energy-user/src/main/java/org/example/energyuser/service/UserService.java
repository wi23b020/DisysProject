package org.example.energyuser.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();
    private boolean initialized = false;

    public UserService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedRate = 5000)
    public void sendEnergyUsage() throws Exception {
        if (!initialized) {
            System.out.println("Delaying first user message to let producers warm up...");
            Thread.sleep(15000); // wait 15 seconds before first user message
            initialized = true;
        }

        double kwh = generateKwhUsage();
        LocalDateTime now = LocalDateTime.now();

        Map<String, Object> message = new HashMap<>();
        message.put("type", "USER");
        message.put("association", "COMMUNITY");
        message.put("kwh", kwh);
        message.put("datetime", now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        String jsonMessage = objectMapper.writeValueAsString(message);
        rabbitTemplate.convertAndSend("energy-queue", jsonMessage);
        System.out.println("Sent energy usage: " + jsonMessage);
    }

    private double generateKwhUsage() {
        int hour = LocalDateTime.now().getHour();
        return (hour >= 6 && hour <= 9 || hour >= 18 && hour <= 21)
                ? 0.3 + (0.4 * random.nextDouble())
                : 0.05 + (0.05 * random.nextDouble());
    }
}
