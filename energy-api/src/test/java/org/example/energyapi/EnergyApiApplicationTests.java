package org.example.energyapi;

import com.energycommunity.energy_api.EnergyApiApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class EnergyApiApplicationMockitoTest {

	@Test
	void main_invokesSpringApplicationRun() {
		System.out.println("Test: Prüfe, dass SpringApplication.run(...) in main aufgerufen wird…");
		try (MockedStatic<SpringApplication> springApp = mockStatic(SpringApplication.class)) {
			// Aufruf der main-Methode
			EnergyApiApplication.main(new String[]{"foo", "bar"});

			// Verifikation
			springApp.verify(() ->
					SpringApplication.run(EnergyApiApplication.class, new String[]{"foo", "bar"})
			);
			System.out.println("OK: SpringApplication.run wurde mit den erwarteten Parametern aufgerufen");
		}
	}
}
