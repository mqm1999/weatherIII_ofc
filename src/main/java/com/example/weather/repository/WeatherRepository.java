package com.example.weather.repository;

import com.example.weather.entity.WeatherDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WeatherRepository extends JpaRepository<WeatherDetail, String> {
    public List<WeatherDetail> findWeatherDetailByDate(long date);

    // test @Query
//    public default void addWeatherRecord(WeatherDetail weatherDetail) {
//        @Query("insert into weather (id, lat, lon, date, temp, humidity, windspeed, visibility, main, description) values (?,?,?,?,?,?,?,?,?,?);";)
//
//
//    }
}
