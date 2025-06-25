package org.example.energyapi;

import com.energycommunity.energy_api.controller.EnergyController;
import org.example.currentpercentageservice.model.CurrentPercentage;
import com.energycommunity.energy_api.service.EnergyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EnergyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EnergyService energyService;

    @InjectMocks
    private EnergyController energyController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(energyController).build();
    }

    @Test
    void getCurrentPercentage_returnsOkAndJson() throws Exception {
        // Default-Konstruktor und Setter verwenden
        CurrentPercentage cp = new CurrentPercentage();
        cp.setCommunityDepleted(10.0);
        cp.setGridPortion(90.0);
        when(energyService.getCurrentPercentage()).thenReturn(cp);

        mockMvc.perform(get("/energy/current"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.communityDepleted").value(10.0))
                .andExpect(jsonPath("$.gridPortion").value(90.0));

        verify(energyService).getCurrentPercentage();
    }

    @Test
    void getHistorical_returnsOkAndJsonList() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 6, 1, 0, 0);
        LocalDateTime end   = LocalDateTime.of(2025, 6, 2, 0, 0);
        CurrentPercentage cp = new CurrentPercentage();
        cp.setCommunityDepleted(20.0);
        cp.setGridPortion(80.0);
        when(energyService.getHistorical(start, end))
                .thenReturn(Collections.singletonList(cp));

        mockMvc.perform(get("/energy/historical")
                        .param("start", start.toString())
                        .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].communityDepleted").value(20.0))
                .andExpect(jsonPath("$[0].gridPortion").value(80.0));

        verify(energyService).getHistorical(start, end);
    }
}
