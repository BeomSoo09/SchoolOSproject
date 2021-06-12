package com.example.weatherinformation;

import java.util.ArrayList;

public class WeatherCurrentData {

    public double lat;                  // 위도
    public double lon;                  // 경도
    public String timezone;             // 요청 된 위치의 시간대 이름
    public long timezone_offset;        // UTC에서 초 단위로 이동

    public Current current;             // 현재 날씨 데이터

    public class Current {
        public long dt;                 // 현재 시간, Unix, UTC
        public long sunrise;            // 일출 시간, Unix, UTC
        public long sunset;             // 일몰 시간, Unix, UTC
        public double temp;             // 온도
        public double feels_like;       // 온도 (날씨에 대한 인간의 인식)
        public int pressure;            // 해수면의 기압
        public int humidity;            // 습도, %
        public double dew_point;        // 물방울이 응축되기 시작하고 이슬이 형성 될 수있는 대기 온도 (압력 및 습도에 따라 다름)
        public double uvi;              // 현재 UV 지수
        public int clouds;              // 흐림, %
        public int visibility;          // 가시성, 미터
        public double wind_speed;       // 풍속
        public int wind_deg;            // 풍향 (도)
        public double wind_gust;        // 바람 돌풍

        public ArrayList<Weather> weather;

        // 날씨
        public class Weather {
            public int id;              // 날씨 상태 ID
            public String main;         // 날씨 매개 변수 그룹 (비, 눈, 극한 등)
            public String description;  // 그룹 내 기상 조건
            public String icon;         // 날씨 아이콘 ID
        }
    }
}
