package org.example.usageservice;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UsageServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UsageServiceApplication.class, args);
    }

    @Bean
    public Queue queue() {
        return new Queue("energy-queue", true);
    }
}
