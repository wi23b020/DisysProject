package org.example.energyuser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.energyuser.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private RabbitTemplate rabbitTemplate;
    @InjectMocks private UserService service;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    // Datenträger für Queue + Nachricht
    private record SendResult(String queue, JsonNode message) {}

    // Führt sendEnergyUsage unter fixierter Zeit & Zufall aus und liefert das Ergebnis
    private SendResult executeSendEnergyUsage(double rndValue, LocalDateTime fixedDate) throws Exception {
        setField(service, "initialized", true);

        Random rnd = mock(Random.class);
        when(rnd.nextDouble()).thenReturn(rndValue);
        setField(service, "random", rnd);

        try (MockedStatic<LocalDateTime> mockTime = mockStatic(LocalDateTime.class)) {
            mockTime.when(LocalDateTime::now).thenReturn(fixedDate);

            service.sendEnergyUsage();

            ArgumentCaptor<String> queueCap = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> msgCap   = ArgumentCaptor.forClass(String.class);
            verify(rabbitTemplate).convertAndSend(queueCap.capture(), msgCap.capture());

            String queue = queueCap.getValue();
            JsonNode json = objectMapper.readTree(msgCap.getValue());
            return new SendResult(queue, json);
        }
    }

    @Test
    void sendEnergyUsage_sendsCorrectMessage() throws Exception {
        LocalDateTime fixed = LocalDateTime.of(2025, 6, 25, 7, 15);
        SendResult result = executeSendEnergyUsage(0.5, fixed);

        System.out.printf("sendEnergyUsage → queue='%s', message=%s%n",
                result.queue(), result.message());

        assertThat(result.queue()).isEqualTo("energy-queue");
        assertThat(result.message().get("type").asText()).isEqualTo("USER");

        double expectedKwh = 0.3 + 0.4 * 0.5;
        assertThat(result.message().get("kwh").asDouble()).isEqualTo(expectedKwh);

        String expectedDt = fixed.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        assertThat(result.message().get("datetime").asText()).isEqualTo(expectedDt);
    }

    // Helfer für generateKwhUsage
    private double invokeGenerateKwhUsage(double rndValue, LocalDateTime fixedDate) throws Exception {
        Random rnd = mock(Random.class);
        when(rnd.nextDouble()).thenReturn(rndValue);
        setField(service, "random", rnd);

        try (MockedStatic<LocalDateTime> mockTime = mockStatic(LocalDateTime.class)) {
            mockTime.when(LocalDateTime::now).thenReturn(fixedDate);

            Method m = UserService.class.getDeclaredMethod("generateKwhUsage");
            m.setAccessible(true);
            return (double) m.invoke(service);
        }
    }

    @Test
    void generateKwhUsage_peakHoursCalculatesCorrectly() throws Exception {
        LocalDateTime peak = LocalDateTime.of(2025, 6, 25, 8, 0);
        double result = invokeGenerateKwhUsage(0.25, peak);

        System.out.printf("generateKwhUsage (peak) → %.3f%n", result);
        assertThat(result).isEqualTo(0.3 + 0.4 * 0.25);
    }

    @Test
    void generateKwhUsage_offPeakHoursCalculatesCorrectly() throws Exception {
        LocalDateTime offPeak = LocalDateTime.of(2025, 6, 25, 14, 0);
        double result = invokeGenerateKwhUsage(0.5, offPeak);

        System.out.printf("generateKwhUsage (off-peak) → %.3f%n", result);
        assertThat(result).isEqualTo(0.05 + 0.05 * 0.5);
    }

    // Hilfsmethode, um private Felder zu setzen
    private static void setField(Object target, String name, Object value) throws Exception {
        Field f = UserService.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }
}
