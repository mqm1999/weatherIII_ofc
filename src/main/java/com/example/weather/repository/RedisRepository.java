package com.example.weather.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisRepository {
    @Autowired
    private RedisTemplate redisTemplate;

    public void setCity(String city) {
        redisTemplate.opsForValue().set("City", city);
    }

    public String getCity() {
        return (String) redisTemplate.opsForValue().get("City");
    }

    public void setLat(Double lat) {
        redisTemplate.opsForValue().set("Lat", lat);
    }

    public Double getLat() {
        return (Double) redisTemplate.opsForValue().get("Lat");
    }

    public void setLon(Double lon) {
        redisTemplate.opsForValue().set("Lon", lon);
    }

    public Double getLon() {
        return (Double) redisTemplate.opsForValue().get("Lon");
    }
}
