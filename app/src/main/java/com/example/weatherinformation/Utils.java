package com.example.weatherinformation;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utils {

    /* 날자/시간/요일 구하기 */
    public static String getDate(String format, long timeMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        Date date = new Date(timeMillis);

        return dateFormat.format(date);
    }

    /* GPS 정보로 주소 얻기 */
    public static String[] getAddressFromGps(Context context, double lat, double lng) {
        // 지오코더... GPS 를 주소로 변환
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
        } catch (IllegalArgumentException illegalArgumentException) {
            return null;
        } catch (IOException ioException) {
            //네트워크 문제
            return null;
        }

        if (addresses == null || addresses.size() == 0) {
            return null;
        } else {
            Address address = addresses.get(0);

            // 주소
            String addr = address.getAddressLine(0);
            addr = addr.replace("대한민국", "").trim();

            String[] area = new String[2];
            area[0] = address.getAdminArea();           // 시도
            area[1] = addr;                             // 주소
            return area;
        }
    }

    /* 주소로 GPS (위도,경도) 얻기 */
    public static Point getGpsFromAddress(Context context, String address) {
        // 지오코더... 주소 를 GPS 로 변환
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocationName(address, 1);
        } catch (IllegalArgumentException illegalArgumentException) {
            return new Point(0 ,0);
        } catch (IOException ioException) {
            //네트워크 문제
            return null;
        }

        if (addresses == null || addresses.size() == 0) {
            return null;
        } else {
            // 위도 경도 넘김
            return new Point(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
        }
    }

    /* 날씨 text */
    public static String getWeather(int weatherId) {
        String weather = "";

        if (weatherId >= 200 && weatherId < 300) {
            // 뇌우
            switch (weatherId) {
                case 200:
                    weather = "약한 비를 동반 한 뇌우";
                    break;
                case 201:
                    weather = "비와 뇌우";
                    break;
                case 202:
                    weather = "폭우를 동반 한 뇌우";
                    break;
                case 210:
                    weather = "약한 뇌우";
                    break;
                case 211:
                    weather = "뇌우";
                    break;
                case 212:
                    weather = "심한 뇌우";
                    break;
                case 221:
                    weather = "비정형 뇌우";
                    break;
                case 230:
                    weather = "약한 이슬비를 동반 한 뇌우";
                    break;
                case 231:
                    weather = "이슬비를 동반 한 뇌우";
                    break;
                case 232:
                    weather = "심한 이슬비를 동반 한 뇌우";
                    break;
            }
        } else if (weatherId >= 300 && weatherId < 400) {
            // 이슬비
            switch (weatherId) {
                case 300:
                case 310:
                    weather = "보슬비";
                    break;
                case 301:
                case 311:
                    weather = "이슬비";
                    break;
                case 302:
                case 312:
                    weather = "강렬한 이슬비";
                    break;
                case 313:
                    weather = "소나기 비와 이슬비";
                    break;
                case 314:
                    weather = "폭우와 이슬비";
                    break;
                case 321:
                    weather = "소나기성 이슬비";
                    break;
            }
        } else if (weatherId >= 500 && weatherId < 600) {
            // 비
            switch (weatherId) {
                case 500:
                    weather = "약한 비";
                    break;
                case 501:
                    weather = "적당한 비";
                    break;
                case 502:
                    weather = "호우";
                    break;
                case 503:
                    weather = "폭우";
                    break;
                case 504:
                    weather = "극심한 비";
                    break;
                case 511:
                    weather = "얼어 붙은 비";
                    break;
                case 520:
                    weather = "가벼운 소나기 비";
                    break;
                case 521:
                case 531:
                    weather = "소나기 비";
                    break;
                case 522:
                    weather = "심한 소나기 비";
                    break;
            }
        } else if (weatherId >= 600 && weatherId < 700) {
            // 눈
            switch (weatherId) {
                case 600:
                    weather = "가벼운 눈";
                    break;
                case 601:
                    weather = "눈";
                    break;
                case 602:
                    weather = "폭설";
                    break;
                case 611:
                    weather = "진눈깨비";
                    break;
                case 612:
                    weather = "가벼운 소나기성 진눈깨비";
                    break;
                case 613:
                    weather = "소나기성 진눈깨비";
                    break;
                case 615:
                    weather = "약한 비와 눈";
                    break;
                case 616:
                    weather = "비와 눈";
                    break;
                case 620:
                    // 소나기 눈
                    weather = "가벼운 소나기 눈";
                    break;
                case 621:
                    weather = "소나기 눈";
                    break;
                case 622:
                    weather = "심한 소나기 눈";
                    break;
            }
        } else if (weatherId >= 700 && weatherId < 800) {
            // 분위기
            switch (weatherId) {
                case 701:
                case 721:
                case 741:
                    weather = "안개";
                    break;
                case 711:
                    weather = "연기";
                    break;
                case 731:
                case 761:
                    weather = "먼지";
                    break;
                case 751:
                    weather = "모래";
                    break;
                case 762:
                    weather = "화산재";
                    break;
                case 771:
                    weather = "돌풍";
                    break;
                case 781:
                    weather = "폭풍";
                    break;
            }
        } else if (weatherId >= 800 && weatherId < 900) {
            // 클리어 / 구름
            switch (weatherId) {
                case 800:
                    // 맑음
                    weather = "맑음";
                    break;
                case 801:
                    // 약간의 구름 : 11-25 %
                    weather = "구름 (11-25 %)";
                    break;
                case 802:
                    // 흩어진 구름 : 25-50 %
                    weather = "구름 (25-50 %)";
                    break;
                case 803:
                    // 부서진 구름 : 51-84 %
                    weather = "구름 (51-84 %)";
                    break;
                case 804:
                    // 흐린 구름 : 85-100 %
                    weather = "구름 (85-100 %)";
                    break;
            }
        }

        return weather;
    }
}
