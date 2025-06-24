package org.example.currentpercentageservice.Rabbit;

import org.example.currentpercentageservice.service.PercentageCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class QueueListener {

    @Autowired
    private PercentageCalculationService calculationService;

    @RabbitListener(queues = "energy-updates")
    public void handleEnergyUpdate(String message) {
        System.out.println("[CurrentPercentageService] Message received: " + message);
        calculationService.calculateAndUpdateCurrentPercentage();
    }
}