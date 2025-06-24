package com.energycommunity.energyproducer.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.exchange}")
    private String exchange;

    @Value("${app.routing-key}")
    private String routingKey;

    @Value("${app.queue}")
    private String queue;

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue energyQueue() {
        return new Queue(queue);
    }

    @Bean
    public Binding binding(Queue energyQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(energyQueue).to(topicExchange).with(routingKey);
    }
}
