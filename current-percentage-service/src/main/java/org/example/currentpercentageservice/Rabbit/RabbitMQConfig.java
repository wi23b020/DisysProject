package org.example.currentpercentageservice.Rabbit;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue energyUpdatesQueue() {
        return new Queue("energy-updates", true); // durable queue
    }
}
