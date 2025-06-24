package com.energycommunity.energy_api.service;

import org.example.currentpercentageservice.model.CurrentPercentage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnergyService {



    private final RestTemplate restTemplate;

    @Autowired
    public EnergyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CurrentPercentage getCurrentPercentage() {
        return restTemplate.getForObject("http://localhost:8083/internal/current", CurrentPercentage.class);
    }

    public List<CurrentPercentage> getHistorical(LocalDateTime start, LocalDateTime end) {
        String url = String.format("http://localhost:8083/internal/historical?start=%s&end=%s", start, end);
        ResponseEntity<List<CurrentPercentage>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CurrentPercentage>>() {}
        );
        return response.getBody();
    }



}
