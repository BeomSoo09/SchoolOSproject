package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class Permission extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int REQUEST_CODE_LOCATION=0;

    Weather weather = new Weather();

    static double lat;//위도
    static double lng ;//경도

    static String tp=null;
    static String description=null;
    static String tpMax=null;
    static String tpMin=null;
    static String feels=null;
    static String geo=null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission); //xml , java 소스 연결

        settingGPS();
        Location userLocation = getMyLocation();

        System.out.println(userLocation);
        if(userLocation == null){
            lat = 36.798097266645;
            lng = 127.0778772836;
        }else{
            lat = userLocation.getLatitude();
            lng = userLocation.getLongitude();
        }
        weather.start();
        geo = Geo(lat,lng);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent); //인트로 실행 후 바로 MainActivity로 넘어감.
                finish();
            }
        }, 3000); //1초 후 인트로 실행
   }

    private void settingGPS() {
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                System.out.println(lat);
                lng = location.getLongitude();
                System.out.println(lng);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
    }

    private Location getMyLocation() {
        Location currentLocation = null;
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 사용자 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, this.REQUEST_CODE_LOCATION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            // 수동으로 위치 구하기
            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            if (currentLocation != null) {
                lat = currentLocation.getLatitude();
                lng = currentLocation.getLongitude();
            }
            System.out.println(currentLocation);
        }
        return currentLocation;
    }

    String Geo(double lat, double lng){
        Geocoder geocoder = new Geocoder(this);

        List<Address> list = null;
        try{
            list = geocoder.getFromLocation(lat, lng, 10);
        }catch(IOException e){ }


        return list.get(0).getLocality()+" "+list.get(0).getThoroughfare();
    }

    public class Weather extends Thread{

        URL url;//URL 주소 객체
        URLConnection connection;//URL접속을 가지는 객체
        InputStream is;//URL접속에서 내용을 읽기위한 Stream
        InputStreamReader isr;
        BufferedReader br;

        public void run() {
            try {
                //URL객체를 생성하고 해당 URL로 접속한다..
                url = new URL("https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lng+"&appid=8dae23d3aa83e3ef697b5e05ba1a6dbe");
                connection = url.openConnection();
                //내용을 읽어오기위한 InputStream객체를 생성한다..
                is = connection.getInputStream();
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);
                int sc,sc1;
                //버퍼에서 해당 문자열의 번호를 읽어오는 역할
                double db=0;
                //데이터 포맷을 해줄때 대신 받아줄 더블 변수
                String cut;
                //가끔 기온의 소숫점 자리가 달라져서 그 달라지는 값을 확인하기 위한 변수

                //내용을 읽어서 화면에 출력한다..
                String buf = "";
                while (true) {
                    buf = br.readLine();
                    System.out.println(buf);
                    if (buf == null) break;
                    sc= buf.indexOf("temp");
                    cut = buf.substring(sc);
                    sc1 = cut.indexOf(",");
                    db = Double.parseDouble(buf.substring(sc + 6, sc + sc1)) - 273.15;
                    tp = String.valueOf((int) Math.round(db));
                    sc = buf.indexOf("id");
                    cut = buf.substring(sc);
                    sc1 = cut.indexOf(",");
                    description = buf.substring(sc + 4, sc + 7);
                    sc = buf.indexOf("temp_max");
                    cut = buf.substring(sc);
                    sc1 = cut.indexOf(",");
                    db = Double.parseDouble(buf.substring(sc + 10, sc + sc1)) - 273.15;
                    tpMax = String.valueOf((int) Math.round(db));
                    sc = buf.indexOf("temp_min");
                    cut = buf.substring(sc);
                    System.out.println(sc);
                    sc1 = cut.indexOf(",");
                    System.out.println(sc1);
                    db = Double.parseDouble(buf.substring(sc + 10, sc + sc1)) - 273.15;
                    tpMin = String.valueOf((int) Math.round(db));
                    System.out.println(tpMin);
                    sc = buf.indexOf("feels_like");
                    cut = buf.substring(sc);
                    sc1 = cut.indexOf(",");
                    db = Double.parseDouble(buf.substring(sc + 12, sc + sc1)) - 273.15;
                    feels = String.valueOf((int) Math.round(db));
                }
            } catch (MalformedURLException mue) {
            } catch (IOException ioe) {
            }
        }

    }
}