package org.example.usageservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.springframework.amqp.core.Queue;

import static org.assertj.core.api.Assertions.assertThat;

class UsageServiceApplicationTest {

    @Test
    @DisplayName("Queue-Bean ist langlebig und heißt »energy-queue«")
    void queueBean_isDurableAndNamedEnergyQueue(TestReporter reporter) {
        // Arrange
        UsageServiceApplication app = new UsageServiceApplication();

        // Act
        Queue q = app.queue();

        // Reporting: diese Einträge erscheinen unter "Published entries" im Test-Log
        reporter.publishEntry("queue.name", q.getName());
        reporter.publishEntry("queue.durable", String.valueOf(q.isDurable()));

        // AssertJ-Assertions mit erklärenden Nachrichten
        assertThat(q.getName())
                .as("Prüfe, dass der Queue-Name korrekt ist")
                .isEqualTo("energy-queue");
        assertThat(q.isDurable())
                .as("Prüfe, dass die Queue dauerhaft (durable) ist")
                .isTrue();
    }
}
