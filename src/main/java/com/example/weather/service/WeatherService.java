package com.example.weather.service;

import com.example.weather.constant.Constant;
import com.example.weather.entity.Weather;
import com.example.weather.entity.WeatherDetail;
import com.example.weather.helper.TimeConverter;
import com.example.weather.repository.RedisRepository;
import com.example.weather.repository.WeatherRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

@Service
public class WeatherService {
    @Autowired
    WeatherRepository weatherRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    RedisRepository redisRepository;

    public HashMap<String, Double> coordInfo(String location) throws JsonProcessingException {
        final String getCoordUrl = Constant.URL + "weather?q=" + location + "&appid=" + Constant.CURRENT_API_KEY;

        // get info from api
        String result = restTemplate.getForObject(getCoordUrl, String.class);
        ObjectMapper objectMapperCoord = new ObjectMapper();
        objectMapperCoord.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonNode jsonNodeRoot = objectMapperCoord.readTree(result);

        // get lon & lat for onecall
        Double lonResult = jsonNodeRoot.get("coord").get("lon").asDouble();
        Double latResult = jsonNodeRoot.get("coord").get("lat").asDouble();

        HashMap<String, Double> coordResult = new HashMap<>();
        coordResult.put("lat", latResult);
        coordResult.put("lon", lonResult);
        return coordResult;
    }

    public List<Weather> weatherInfo(String location, String date) throws Exception {
        Double lat;
        Double lon;
        Long dateTimeInput = TimeConverter.unixTimeConverter(date);
        try {
            if (redisRepository.getCity() != null) {
                if (redisRepository.getCity().equals(location.toLowerCase())) {
                    System.out.println("get from redis");
                    lat = redisRepository.getLat();
                    lon = redisRepository.getLon();
                } else {
                    // get from url
                    lat = coordInfo(location).get("lat");
                    lon = coordInfo(location).get("lon");

                    // save to redis
                    System.out.println("save to redis");
                    redisRepository.setCity(location.toLowerCase());
                    redisRepository.setLat(lat);
                    redisRepository.setLon(lon);
                }
            } else {
                // get from url
                lat = coordInfo(location).get("lat");
                lon = coordInfo(location).get("lon");

                // save to redis
                System.out.println("save to redis");
                redisRepository.setCity(location.toLowerCase());
                redisRepository.setLat(lat);
                redisRepository.setLon(lon);
            }

            // url to get info
            final String url = Constant.URL + "onecall?lat=" + lat + "&lon=" + lon + "&appid=" + Constant.CURRENT_API_KEY;

            // get info from api
            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode jsonNode = objectMapper.readTree(response);

            // get return info
            // create list
            ArrayList<Weather> responseWeather = new ArrayList<>();
            // current
            Weather currentWeather = new Weather();
            if (weatherRepository.findWeatherDetailByDate(dateTimeInput).size() == 0) {
                // set lat & lon
                currentWeather.setLat(lat);
                currentWeather.setLon(lon);
                // set date, temp, humidity, windspeed, visibility
                //String datetime = TimeFormatterForDb.timeFormatterForDb(jsonNode.get("/current/dt").asLong());
                Date datetime = Date.from(Instant.ofEpochSecond(jsonNode.get("current").get("dt").asLong() + 3600 * 7));
                currentWeather.setDate(datetime);
                currentWeather.setTemp(jsonNode.get("current").get("temp").asDouble());
                currentWeather.setHumidity(jsonNode.get("current").get("humidity").asDouble());
                currentWeather.setWindspeed(jsonNode.get("current").get("wind_speed").asDouble());
                currentWeather.setVisibility(jsonNode.get("current").get("visibility").asDouble());
                // set main, description
                ArrayNode weather = (ArrayNode) jsonNode.get("current").get("weather");
                List<JsonNode> weatherList = new ArrayList<>();
                for (Iterator<JsonNode> it = weather.iterator(); it.hasNext(); ) {
                    JsonNode node = it.next();
                    weatherList.add(node);
                }
                currentWeather.setMain(weatherList.get(0).get("main").asText());
                currentWeather.setDescription(weatherList.get(0).get("description").asText());
                // add currentWeather to return list
                responseWeather.add(currentWeather);

                // add to db
                WeatherDetail weatherDetail = new WeatherDetail();
                weatherDetail.setId(UUID.randomUUID().toString());
                weatherDetail.setLat(lat);
                weatherDetail.setLon(lon);
                weatherDetail.setDate(dateTimeInput);
                weatherDetail.setTemp(currentWeather.getTemp());
                weatherDetail.setHumidity(currentWeather.getHumidity());
                weatherDetail.setWindspeed(currentWeather.getWindspeed());
                weatherDetail.setVisibility(currentWeather.getVisibility());
                weatherDetail.setMain(currentWeather.getMain());
                weatherDetail.setDescription(currentWeather.getDescription());
                weatherRepository.save(weatherDetail);
                System.out.println("db updated");
            } else {
                // get current weather info
                WeatherDetail weatherDetail = weatherRepository.findWeatherDetailByDate(dateTimeInput).get(0);
                currentWeather.setLat(lat);
                currentWeather.setLon(lon);
                currentWeather.setDate(Date.from(Instant.ofEpochSecond(weatherDetail.getDate())));
                currentWeather.setTemp(weatherDetail.getTemp());
                currentWeather.setHumidity(weatherDetail.getHumidity());
                currentWeather.setWindspeed(weatherDetail.getWindspeed());
                currentWeather.setVisibility(weatherDetail.getVisibility());
                currentWeather.setMain(weatherDetail.getMain());
                currentWeather.setDescription(weatherDetail.getDescription());
                System.out.println("get from db");
                responseWeather.add(currentWeather);
            }

            // forecast daily
            Weather forecastWeather1 = new Weather();
            Weather forecastWeather2 = new Weather();
            ArrayNode daily = (ArrayNode) jsonNode.get("daily");
            List<JsonNode> dailyList = new ArrayList<>();
            for (Iterator<JsonNode> it = daily.iterator(); it.hasNext(); ) {
                JsonNode node = it.next();
                dailyList.add(node);
            }
            // set info forecastWeather1
            // set lat & lon
            forecastWeather1.setLon(lon);
            forecastWeather1.setLat(lat);
            // set date, temp, humidity, windspeed, visibility
            Date forecastDatetime1 = Date.from(Instant.ofEpochSecond(dailyList.get(1).get("dt").asLong() + 3600 * 7));
            forecastWeather1.setDate(forecastDatetime1);
            double min_temp1 = dailyList.get(1).get("temp").get("min").asDouble();
            double max_temp1 = dailyList.get(1).get("temp").get("max").asDouble();
            forecastWeather1.setTemp((min_temp1 + max_temp1) / 2);
            forecastWeather1.setHumidity(dailyList.get(1).get("humidity").asDouble());
            forecastWeather1.setWindspeed(dailyList.get(1).get("wind_speed").asDouble());
            forecastWeather1.setVisibility(0);
            // set main & description
            ArrayNode weatherf1 = (ArrayNode) dailyList.get(1).get("weather");
            List<JsonNode> weatherF1List = new ArrayList<>();
            for (Iterator<JsonNode> it = weatherf1.iterator(); it.hasNext(); ) {
                JsonNode node = it.next();
                weatherF1List.add(node);
            }
            forecastWeather1.setMain(weatherF1List.get(0).get("main").asText());
            forecastWeather1.setDescription(weatherF1List.get(0).get("description").asText());
            responseWeather.add(forecastWeather1);

            // set info forecastWeather2
            // set lat & lon
            forecastWeather2.setLon(lon);
            forecastWeather2.setLat(lat);
            // set date, temp, humidity, windspeed, visibility
            Date forecastDatetime2 = Date.from(Instant.ofEpochSecond(dailyList.get(2).get("dt").asLong() + 3600 * 7));
            forecastWeather2.setDate(forecastDatetime2);
            double min_temp2 = dailyList.get(2).get("temp").get("min").asDouble();
            double max_temp2 = dailyList.get(2).get("temp").get("max").asDouble();
            forecastWeather2.setTemp((min_temp2 + max_temp2) / 2);
            forecastWeather2.setHumidity(dailyList.get(2).get("humidity").asDouble());
            forecastWeather2.setWindspeed(dailyList.get(2).get("wind_speed").asDouble());
            forecastWeather2.setVisibility(0);
            // set main & description
            ArrayNode weatherf2 = (ArrayNode) dailyList.get(2).get("weather");
            List<JsonNode> weatherF2List = new ArrayList<>();
            for (Iterator<JsonNode> it = weatherf2.iterator(); it.hasNext(); ) {
                JsonNode node = it.next();
                weatherF2List.add(node);
            }
            forecastWeather2.setMain(weatherF2List.get(0).get("main").asText());
            forecastWeather2.setDescription(weatherF2List.get(0).get("description").asText());
            responseWeather.add(forecastWeather2);
            return responseWeather;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}

// tao db, luu thong tin current vao db (dung docker)
// info: id (uuid), date, moi thong tin 1 truong`
// caching
// check trong db: neu hom nay khong co thi call api, neu co roi chi lay tu db ra
// call api => save db




