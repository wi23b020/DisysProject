package org.example.currentpercentageservice;// CurrentPercentageServiceTest.java


import org.example.currentpercentageservice.model.CurrentPercentage;
import org.example.currentpercentageservice.model.HourlyEnergyUsage;
import org.example.currentpercentageservice.repository.HourlyUsageRepository;
import org.example.currentpercentageservice.repository.CurrentPercentageRepository;
import org.example.currentpercentageservice.service.CurrentPercentageService;
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
public class CurrentPercentageServiceTest {

	@Mock
	private HourlyUsageRepository hourlyUsageRepository;
	@Mock
	private CurrentPercentageRepository repository;
	@InjectMocks
	private CurrentPercentageService service;

	@Test
	void whenUsageExists_thenSaveCurrentPercentage() {
		// Arrange
		HourlyEnergyUsage usage = new HourlyEnergyUsage();
		usage.setCommunityProduced(80.0);
		usage.setCommunityUsed(100.0);
		when(hourlyUsageRepository.findByHourRange(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(usage);

		// Act
		service.calculatePercentage("test-message");

		// Assert
		ArgumentCaptor<CurrentPercentage> captor = ArgumentCaptor.forClass(CurrentPercentage.class);
		verify(repository).save(captor.capture());
		CurrentPercentage saved = captor.getValue();
		assertEquals(100.0, saved.getCommunityDepleted(), 0.001);
		assertEquals(20.0, saved.getGridPortion(), 0.001);
		assertEquals(0, saved.getHour().getMinute());
		assertEquals(0, saved.getHour().getSecond());
	}

	@Test
	void whenUsageIsNull_thenNoSave() {
		// Arrange
		when(hourlyUsageRepository.findByHourRange(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(null);

		// Act
		service.calculatePercentage("test-message");

		// Assert
		verify(repository, never()).save(any());
	}
}
