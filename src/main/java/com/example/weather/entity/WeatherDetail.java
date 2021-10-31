package com.example.weather.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="weather_detail")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WeatherDetail implements Serializable {
    @Id
    @Column(name="id")
    private String id;

    @Column(name="lat")
    private double lat;

    @Column(name="lon")
    private double lon;

    @Column(name="date")
    private long date;

    @Column(name="temp")
    private double temp;

    @Column(name="humidity")
    private double humidity;

    @Column(name="main")
    private String main;

    @Column(name="description")
    private String description;

    @Column(name="windspeed")
    private double windspeed;

    @Column(name="visibility")
    private double visibility;
}
