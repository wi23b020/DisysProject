package org.example.energyapi;

import com.energycommunity.energy_api.service.EnergyService;
import org.example.currentpercentageservice.model.CurrentPercentage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnergyServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EnergyService energyService;

    @Test
    void getCurrentPercentage_returnsExpected() {
        System.out.println("Test: getCurrentPercentage_returnsExpected – rufe /internal/current auf und erwarte das zurückgegebene Objekt");
        // Arrange
        CurrentPercentage cp = new CurrentPercentage();
        cp.setCommunityDepleted(10.0);
        cp.setGridPortion(90.0);
        when(restTemplate.getForObject(
                "http://localhost:8083/internal/current",
                CurrentPercentage.class))
                .thenReturn(cp);

        // Act
        CurrentPercentage result = energyService.getCurrentPercentage();

        // Assert
        assertSame(cp, result);
        verify(restTemplate)
                .getForObject("http://localhost:8083/internal/current", CurrentPercentage.class);
        System.out.println("OK: getCurrentPercentage hat das erwartete CurrentPercentage-Objekt zurückgegeben");
    }

    @Test
    void getHistorical_returnsExpectedList() {
        System.out.println("Test: getHistorical_returnsExpectedList – rufe /internal/historical?start=&end= auf und erwarte eine Liste");
        // Arrange
        LocalDateTime start = LocalDateTime.of(2025, 6, 1, 0, 0);
        LocalDateTime end   = LocalDateTime.of(2025, 6, 2, 0, 0);
        CurrentPercentage cp = new CurrentPercentage();
        cp.setCommunityDepleted(20.0);
        cp.setGridPortion(80.0);
        List<CurrentPercentage> list = Collections.singletonList(cp);
        ResponseEntity<List<CurrentPercentage>> response =
                new ResponseEntity<>(list, HttpStatus.OK);

        String url = String.format(
                "http://localhost:8083/internal/historical?start=%s&end=%s",
                start, end
        );
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(response);

        // Act
        List<CurrentPercentage> result = energyService.getHistorical(start, end);

        // Assert
        assertEquals(list, result);
        verify(restTemplate).exchange(
                eq(url),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
        System.out.println("OK: getHistorical hat die erwartete Liste zurückgegeben");
    }
}
