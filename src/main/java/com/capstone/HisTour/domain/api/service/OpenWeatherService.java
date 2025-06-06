package com.capstone.HisTour.domain.api.service;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class OpenWeatherService {

    @Value("${api.open-weather.api-key}")
    String openWeatherKey;

    private final RestClient restClient;

    // indoor only 반환 함수
    public boolean isIndoorOnly(Double lat, Double lon) {

        // openweather api 호출 결과 json
        JSONObject response = new JSONObject(getWeatherJson(lat, lon));

        // 가장 가까운 시간대 예보 사용
        JSONObject weatherData = response.getJSONArray("list").getJSONObject(0);

        JSONObject main = weatherData.getJSONObject("main");
        double tempCelsius = main.getDouble("temp");

        // clouds, rain, snow 등, 날씨 조회
        String weatherMain = weatherData
                .getJSONArray("weather")
                .getJSONObject(0)
                .getString("main")
                .toLowerCase();

        // 강수확률 조회
        double pop = weatherData.has("pop") ? weatherData.getDouble("pop") : 0.0;

        // 강수량 조회
        double rain = 0.0;
        if (weatherData.has("rain")) {
            rain = weatherData.getJSONObject("rain").optDouble("3h", 0.0);
        }

        // 눈내리는양 조회
        double snow = 0.0;
        if (weatherData.has("snow")) {
            snow = weatherData.getJSONObject("snow").optDouble("3h", 0.0);
        }

        // IndoorOnly 인지 true, false 반환
        return weatherMain.contains("rain")
                || weatherMain.contains("snow")
                || pop > 0.3
                || rain > 1.0
                || snow > 0.0
                || tempCelsius < 0.0
                || tempCelsius > 30.0;
    }

    // openweather api 호출
    private String getWeatherJson(Double lat, Double lon) {
        String url = "http://api.openweathermap.org/data/2.5/forecast?"
                + "lat=" + lat
                + "&lon=" + lon
                + "&units=metric"
                + "&appid=" + openWeatherKey;

        return restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);
    }
}
