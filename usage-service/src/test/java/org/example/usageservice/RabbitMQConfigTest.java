package org.example.usageservice;

import org.example.usageservice.rabbit.RabbitMQConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.springframework.amqp.core.Queue;

import static org.assertj.core.api.Assertions.assertThat;

class RabbitMQConfigTest {

    @Test
    @DisplayName("energyUpdatesQueue ist langlebig und heißt »energy-updates«")
    void energyUpdatesQueue_isDurableAndNamedCorrectly(TestReporter reporter) {
        // Arrange
        RabbitMQConfig cfg = new RabbitMQConfig();

        // Act
        Queue q = cfg.energyUpdatesQueue();

        // Reporting: erscheint im Test-Log unter "Published entries"
        reporter.publishEntry("queue.name", q.getName());
        reporter.publishEntry("queue.durable", String.valueOf(q.isDurable()));

        // AssertJ-Assertions mit erklärenden Nachrichten
        assertThat(q.getName())
                .as("Prüfe, dass der Queue-Name korrekt ist")
                .isEqualTo("energy-updates");
        assertThat(q.isDurable())
                .as("Prüfe, dass die Queue dauerhaft (durable) ist")
                .isTrue();
    }
}
