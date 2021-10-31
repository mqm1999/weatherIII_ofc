package com.example.weather.controller;

import com.example.weather.entity.Weather;
import com.example.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/weather")
public class BaseController {
    @Autowired
    WeatherService weatherService;

    @GetMapping("/{location}/{date}")
    public List<Weather> weatherInfo(@PathVariable String location, @PathVariable String date) throws Exception {
        return weatherService.weatherInfo(location, date);
    }

}

// oauth2