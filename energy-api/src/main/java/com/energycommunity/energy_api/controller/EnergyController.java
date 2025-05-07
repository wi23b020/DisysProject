package com.energycommunity.energy_api.controller;
import com.energycommunity.energy_api.model.EnergyData;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/energy")
public class EnergyController {

    @GetMapping("/current")
    public EnergyData getCurrent() {
        return new EnergyData(LocalDate.now(), 20.0, 18.0, 2.0);
    }

    @GetMapping("/historical")
    public List<EnergyData> getHistorical(@RequestParam String start, @RequestParam String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);


        List<EnergyData> mockData = List.of(
                new EnergyData(LocalDate.parse("2025-01-10"), 10, 9, 1),
                new EnergyData(LocalDate.parse("2025-01-11"), 12, 10, 2),
                new EnergyData(LocalDate.parse("2025-01-12"), 14, 13, 1),
                new EnergyData(LocalDate.parse("2025-01-13"), 18, 17, 1),
                new EnergyData(LocalDate.parse("2025-01-14"), 20, 18, 2)
        );


        return mockData.stream()
                .filter(d -> !d.getDay().isBefore(startDate) && !d.getDay().isAfter(endDate))
                .toList();


    }

}
