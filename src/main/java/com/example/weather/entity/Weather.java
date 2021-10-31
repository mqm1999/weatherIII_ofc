package com.example.weather.entity;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Weather {
    private double lat;
    private double lon;
    private Date date;
    private double temp;
    private double humidity;
    private String main;
    private String description;
    private double windspeed;
    private double visibility;
}
