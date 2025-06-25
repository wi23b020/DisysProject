package com.energycommunity.energyproducer;

import com.energycommunity.energyproducer.config.RabbitMQConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class RabbitMQConfigTest {

    private RabbitMQConfig cfg;

    @BeforeEach
    void setUp() throws Exception {
        cfg = new RabbitMQConfig();
        // private @Value-Felder via Reflection setzen
        setField("exchange", "my-exchange");
        setField("routingKey", "my.routing.key");
        setField("queue", "my-queue");
    }

    private void setField(String name, String value) throws Exception {
        Field f = RabbitMQConfig.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(cfg, value);
    }

    @Test
    void topicExchangeBean_hasCorrectName() {
        System.out.println("Test: Prüfe, ob die TopicExchange den richtigen Namen hat…");
        TopicExchange ex = cfg.topicExchange();
        assertThat(ex.getName()).isEqualTo("my-exchange");
        System.out.println("OK: Exchange-Name ist 'my-exchange'");
    }

    @Test
    void energyQueueBean_hasCorrectName() {
        System.out.println("Test: Prüfe, ob die Queue den richtigen Namen hat und durable ist…");
        Queue q = cfg.energyQueue();
        assertThat(q.getName()).isEqualTo("my-queue");
        System.out.println("OK: Queue-Name ist 'my-queue'");
        assertThat(q.isDurable()).isTrue();
        System.out.println("OK: Queue ist durable");
    }

    @Test
    void bindingBean_bindsQueueToExchangeWithRoutingKey() {
        System.out.println("Test: Prüfe Binding von Queue zu Exchange mit RoutingKey…");
        Queue q = cfg.energyQueue();
        TopicExchange ex = cfg.topicExchange();
        Binding b = cfg.binding(q, ex);

        assertThat(b.getDestination()).isEqualTo("my-queue");
        System.out.println("OK: Destination ist 'my-queue'");
        assertThat(b.getDestinationType()).isEqualTo(Binding.DestinationType.QUEUE);
        System.out.println("OK: DestinationType ist QUEUE");
        assertThat(b.getExchange()).isEqualTo("my-exchange");
        System.out.println("OK: Exchange ist 'my-exchange'");
        assertThat(b.getRoutingKey()).isEqualTo("my.routing.key");
        System.out.println("OK: RoutingKey ist 'my.routing.key'");
    }
}
