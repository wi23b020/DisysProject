package org.example.currentpercentageservice.controller;

import org.example.currentpercentageservice.model.CurrentPercentage;
import org.example.currentpercentageservice.repository.CurrentPercentageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/internal")
public class CurrentPercentageController {

    @Autowired
    private CurrentPercentageRepository repository;

    @GetMapping("/current")
    public CurrentPercentage getCurrent() {
        return repository.findTopByOrderByHourDesc();
    }

    @GetMapping("/historical")
    public List<CurrentPercentage> getHistorical(
            @RequestParam("start") LocalDateTime start,
            @RequestParam("end") LocalDateTime end
    ) {
        return repository.findByHourBetween(start, end);
    }

}

