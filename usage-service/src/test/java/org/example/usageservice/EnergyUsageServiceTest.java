package org.example.usageservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.usageservice.model.HourlyEnergyUsage;
import org.example.usageservice.repository.EnergyUsageRepository;
import org.example.usageservice.service.EnergyUsageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnergyUsageServiceTest {

    @Mock
    private EnergyUsageRepository repository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private EnergyUsageService service;

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    private String buildJson(String type, String association, double kwh, String datetime) {
        ObjectNode node = mapper.createObjectNode();
        node.put("type", type);
        node.put("association", association);
        node.put("kwh", kwh);
        node.put("datetime", datetime);
        return node.toString();
    }

    @Test
    @DisplayName("PRODUCER COMMUNITY: neuer Datensatz wird angelegt")
    void receive_producerCommunity_updatesCommunityProduced(TestReporter reporter) throws Exception {
        // given
        double kwhVal = 12.5;
        String dtString = "2025-06-25T10:15:30";
        String msg = buildJson("PRODUCER", "COMMUNITY", kwhVal, dtString);
        LocalDateTime truncated = LocalDateTime.parse(dtString).truncatedTo(ChronoUnit.HOURS);

        when(repository.findById(truncated)).thenReturn(Optional.empty());

        // when
        service.receive(msg);

        // then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<HourlyEnergyUsage> captor = ArgumentCaptor.forClass(HourlyEnergyUsage.class);
        verify(repository).save(captor.capture());

        HourlyEnergyUsage saved = captor.getValue();
        // Reporting
        reporter.publishEntry("saved.hour", saved.getHour().toString());
        reporter.publishEntry("saved.communityProduced", String.valueOf(saved.getCommunityProduced()));
        reporter.publishEntry("saved.communityUsed", String.valueOf(saved.getCommunityUsed()));
        reporter.publishEntry("saved.gridUsed", String.valueOf(saved.getGridUsed()));

        assertThat(saved.getHour()).isEqualTo(truncated);
        assertThat(saved.getCommunityProduced()).isEqualTo(kwhVal);
        assertThat(saved.getCommunityUsed()).isZero();
        assertThat(saved.getGridUsed()).isZero();

        // verify send
        String expectedRoutingKey = "energy-updates";
        String expectedMessage = "updated:" + truncated.toString();
        verify(rabbitTemplate).convertAndSend(eq(expectedRoutingKey), eq(expectedMessage));
        reporter.publishEntry("sent.routingKey", expectedRoutingKey);
        reporter.publishEntry("sent.message", expectedMessage);
    }

    @Test
    @DisplayName("USER COMMUNITY mit ausreichend Community-Energie")
    void receive_userCommunity_withEnoughCommunityEnergy(TestReporter reporter) throws Exception {
        // given
        double produced = 20.0;
        double usedBefore = 0.0;
        double kwhVal = 10.0;
        String dtString = "2025-06-25T11:05:00";
        String msg = buildJson("USER", "COMMUNITY", kwhVal, dtString);
        LocalDateTime truncated = LocalDateTime.parse(dtString).truncatedTo(ChronoUnit.HOURS);

        HourlyEnergyUsage existing = new HourlyEnergyUsage();
        existing.setHour(truncated);
        existing.setCommunityProduced(produced);
        existing.setCommunityUsed(usedBefore);
        existing.setGridUsed(0.0);

        when(repository.findById(truncated)).thenReturn(Optional.of(existing));

        // when
        service.receive(msg);

        // then
        verify(repository).save(existing);

        // Reporting
        reporter.publishEntry("before.communityUsed", String.valueOf(usedBefore));
        reporter.publishEntry("delta.kwh", String.valueOf(kwhVal));
        reporter.publishEntry("after.communityUsed", String.valueOf(existing.getCommunityUsed()));
        reporter.publishEntry("after.gridUsed", String.valueOf(existing.getGridUsed()));

        assertThat(existing.getCommunityUsed()).isEqualTo(usedBefore + kwhVal);
        assertThat(existing.getGridUsed()).isZero();

        String expectedMessage = "updated:" + truncated.toString();
        verify(rabbitTemplate).convertAndSend(eq("energy-updates"), eq(expectedMessage));
        reporter.publishEntry("sent.message", expectedMessage);
    }

    @Test
    @DisplayName("USER COMMUNITY mit unzureichender Community-Energie")
    void receive_userCommunity_withNotEnoughCommunityEnergy(TestReporter reporter) throws Exception {
        // given
        double produced = 5.0;
        double usedBefore = 0.0;
        double kwhVal = 12.0;
        String dtString = "2025-06-25T12:30:00";
        String msg = buildJson("USER", "COMMUNITY", kwhVal, dtString);
        LocalDateTime truncated = LocalDateTime.parse(dtString).truncatedTo(ChronoUnit.HOURS);

        HourlyEnergyUsage existing = new HourlyEnergyUsage();
        existing.setHour(truncated);
        existing.setCommunityProduced(produced);
        existing.setCommunityUsed(usedBefore);
        existing.setGridUsed(0.0);

        when(repository.findById(truncated)).thenReturn(Optional.of(existing));

        // when
        service.receive(msg);

        // then
        verify(repository).save(existing);

        double usedFromCommunity = produced - usedBefore; // = 5
        double usedFromGrid = kwhVal - usedFromCommunity;  // = 7

        // Reporting
        reporter.publishEntry("before.communityUsed", String.valueOf(usedBefore));
        reporter.publishEntry("produced", String.valueOf(produced));
        reporter.publishEntry("delta.requested", String.valueOf(kwhVal));
        reporter.publishEntry("usedFromCommunity", String.valueOf(usedFromCommunity));
        reporter.publishEntry("after.communityUsed", String.valueOf(existing.getCommunityUsed()));
        reporter.publishEntry("after.gridUsed", String.valueOf(existing.getGridUsed()));

        assertThat(existing.getCommunityUsed()).isEqualTo(usedBefore + usedFromCommunity);
        assertThat(existing.getGridUsed()).isEqualTo(usedFromGrid);

        String expectedMessage = "updated:" + truncated.toString();
        verify(rabbitTemplate).convertAndSend(eq("energy-updates"), eq(expectedMessage));
        reporter.publishEntry("sent.message", expectedMessage);
    }
}
