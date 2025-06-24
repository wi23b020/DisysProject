package com.energycommunity.energyproducer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class ProducerService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    public ProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostConstruct
    public void startProducing() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                double kwh = generateKwhProduction();
                LocalDateTime now = LocalDateTime.now();

                Map<String, Object> message = new HashMap<>();
                message.put("type", "PRODUCER");
                message.put("association", "COMMUNITY");
                message.put("kwh", kwh);
                message.put("datetime", now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                String jsonMessage = objectMapper.writeValueAsString(message);
                rabbitTemplate.convertAndSend("energy-queue", jsonMessage);
                System.out.println("Sent energy production: " + jsonMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 2 + random.nextInt(4), TimeUnit.SECONDS);
    }

    private double generateKwhProduction() {
        return 0.005 + (0.01 * random.nextDouble()); // ~ 5 - 15 Wh
    }
}
