package com.energycommunity.energy_api.controller;

import com.energycommunity.energy_api.EnergyService;
import com.energycommunity.energy_api.model.EnergyData;
import org.example.currentpercentageservice.CurrentPercentage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/energy")
public class EnergyController {

    @Autowired
    private EnergyService energyService;

    @GetMapping("/current")
    public ResponseEntity<CurrentPercentage> getCurrentPercentage() {
        return ResponseEntity.ok(energyService.getCurrentPercentage());
    }

    @GetMapping("/historical")
    public ResponseEntity<List<CurrentPercentage>> getHistorical(
            @RequestParam("start") LocalDateTime start,
            @RequestParam("end") LocalDateTime end
    ) {
        return ResponseEntity.ok(energyService.getHistorical(start, end));
    }


}
