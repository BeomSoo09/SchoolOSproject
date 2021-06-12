package com.example.weatherinformation;

public class Constants {

    public static final String DEFAULT_AREA = "충청남도 천안시";   // 위치 서비스를 이용하지 않을 때 적용할 지역

    /* 날씨 open api 관련 데이터 */
    public static class WeatherOpenApi {
        public static final String API_URL = "http://api.openweathermap.org/data/2.5/onecall";  // api url
        public static final String ICON_URL = "http://openweathermap.org/img/wn/";              // icon url
        public static final String API_KEY = "13c868c104eadd8e1a510e57a1231508";                // api key
    }

    /* 로딩 딜레이 */
    public static class LoadingDelay {
        public static final int SHORT = 300;
        public static final int LONG = 1000;
    }
}
