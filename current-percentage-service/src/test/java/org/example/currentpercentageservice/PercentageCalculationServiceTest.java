package org.example.currentpercentageservice;// PercentageCalculationServiceTest.java

import org.example.currentpercentageservice.model.CurrentPercentage;
import org.example.currentpercentageservice.model.HourlyEnergyUsage;
import org.example.currentpercentageservice.repository.HourlyUsageRepository;
import org.example.currentpercentageservice.repository.CurrentPercentageRepository;
import org.example.currentpercentageservice.service.PercentageCalculationService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class PercentageCalculationServiceTest {

    @Mock
    private HourlyUsageRepository hourlyUsageRepository;
    @Mock
    private CurrentPercentageRepository currentPercentageRepository;
    @InjectMocks
    private PercentageCalculationService service;

    @Test
    void whenUsageExists_thenSaveCalculatedPercentage() {
        // Arrange
        HourlyEnergyUsage usage = new HourlyEnergyUsage();
        LocalDateTime hour = LocalDateTime.of(2025, 6, 25, 14, 0);
        usage.setHour(hour);
        usage.setCommunityProduced(100.0);
        usage.setCommunityUsed(60.0);
        usage.setGridUsed(40.0);
        when(hourlyUsageRepository.findTopByOrderByHourDesc()).thenReturn(usage);

        // Act
        service.calculateAndUpdateCurrentPercentage();

        // Assert
        ArgumentCaptor<CurrentPercentage> captor = ArgumentCaptor.forClass(CurrentPercentage.class);
        verify(currentPercentageRepository).save(captor.capture());
        CurrentPercentage saved = captor.getValue();
        assertEquals(hour, saved.getHour());
        assertEquals(60.0, saved.getCommunityDepleted(), 0.001);
        assertEquals(40.0, saved.getGridPortion(), 0.001);
    }

    @Test
    void whenUsageIsNull_thenNoSave() {
        // Arrange
        when(hourlyUsageRepository.findTopByOrderByHourDesc()).thenReturn(null);

        // Act
        service.calculateAndUpdateCurrentPercentage();

        // Assert
        verify(currentPercentageRepository, never()).save(any());
    }
}

