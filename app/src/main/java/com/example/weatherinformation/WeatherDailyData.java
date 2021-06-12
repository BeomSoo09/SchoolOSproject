package com.example.weatherinformation;

import java.util.ArrayList;

public class WeatherDailyData {

    public double lat;                  // 위도
    public double lon;                  // 경도
    public String timezone;             // 요청 된 위치의 시간대 이름
    public long timezone_offset;        // UTC에서 초 단위로 이동

    public ArrayList<Daily> daily;      // 일일 예보 날씨 데이터

    public class Daily {
        public long dt;                 // 예측 데이터 시간, Unix, UTC
        public long sunrise;            // 일출 시간, Unix, UTC
        public long sunset;             // 일몰 시간, Unix, UTC
        public long moonrise;           // 달이 떠오르는 시간, Unix, UTC
        public long moonset;            // 달이지는 시간, Unix, UTC
        public double moon_phase;       // 달의 위상
        public int pressure;            // 해수면의 기압
        public int humidity;            // 습도, %
        public double dew_point;        // 물방울이 응축되기 시작하고 이슬이 형성 될 수있는 대기 온도 (압력 및 습도에 따라 다름)
        public double uvi;              // 현재 UV 지수
        public int clouds;              // 흐림, %
        public double wind_speed;       // 풍속
        public int wind_deg;            // 풍향 (도)
        public double wind_gust;        // 바람 돌풍
        public double pop;              // 강수 확률

        public Temp temp;               // 온도
        public FeelsLike feels_like;    // 온도 (날씨에 대한 인간의 인식)

        public ArrayList<Weather> weather;

        // 날씨
        public class Weather {
            public int id;              // 날씨 상태 ID
            public String main;         // 날씨 매개 변수 그룹 (비, 눈, 극한 등)
            public String description;  // 그룹 내 기상 조건
            public String icon;         // 날씨 아이콘 ID
        }

        // 온도
        public class Temp {
            public double morn;         // 아침 온도
            public double day;          // 점심 온도
            public double eve;          // 저녁 온도
            public double night;        // 밤 온도
            public double min;          // 최소 일일 온도.
            public double max;          // 최대 일일 온도
        }

        // 온도 (날씨에 대한 인간의 인식)
        public class FeelsLike {
            public double morn;         // 아침 온도
            public double day;          // 점심 온도
            public double eve;          // 저녁 온도
            public double night;        // 밤 온도
        }
    }
}
