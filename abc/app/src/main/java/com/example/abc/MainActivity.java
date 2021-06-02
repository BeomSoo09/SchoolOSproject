package com.example.abc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    TextView temp,tempmax,tempmin,feeltemp,geo;
    ImageView i1;

    Weather weather = new Weather();

    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int REQUEST_CODE_LOCATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temp = (TextView)findViewById(R.id.temp);
        tempmax = (TextView)findViewById(R.id.tpmax);
        tempmin = (TextView)findViewById(R.id.tpmin);
        feeltemp = (TextView)findViewById(R.id.feeltemp);
        geo = (TextView)findViewById(R.id.geo);
        i1 = (ImageView)findViewById(R.id.weather);

        settingGPS();
        Location userLocation = getMyLocation();

        weather.start();

        if( userLocation != null ) {
            weather.lat = userLocation.getLatitude();
            weather.lng = userLocation.getLongitude();
        }else{
            weather.lat = 36.798097;

            weather.lng = 127.077877;

        }

        temp.setText(weather.temp+"℃");
        tempmax.setText(weather.tpmax+"℃");
        tempmin.setText(weather.tpmin+"℃");
        feeltemp.setText(weather.feels+"℃");
        geo.setText(Geo(weather.lat,weather.lng));

        System.out.println(Geo(weather.lat,weather.lng));

        Image(weather.description);

    }

    void Image(String id){
        try {
            switch (id) {
                case "800":
                    i1.setBackgroundResource(R.drawable._1d);
                case "801":
                    i1.setBackgroundResource(R.drawable._2d);
                    break;
                case "802":
                    i1.setBackgroundResource(R.drawable._3d);
                    break;
                case "803":
                case "804":
                    i1.setBackgroundResource(R.drawable._4d);
                    break;
                default:
                    switch (Integer.parseInt(id) / 100) {
                        case 2:
                            i1.setBackgroundResource(R.drawable._11d);
                            break;
                        case 3:
                            i1.setBackgroundResource(R.drawable._9d);
                            break;
                        case 5:
                            i1.setBackgroundResource(R.drawable._10d);
                            break;
                        case 6:
                            i1.setBackgroundResource(R.drawable._13d);
                            break;
                        case 7:
                            i1.setBackgroundResource(R.drawable._50d);
                            break;
                    }
                    break;
            }
        }catch(Exception e){ }
    }

    private void settingGPS() {
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                weather.lat = location.getLatitude();
                System.out.println(weather.lat);
                weather.lng = location.getLongitude();
                System.out.println(weather.lng);
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
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            // 수동으로 위치 구하기
            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            if (currentLocation != null) {
                weather.lat = currentLocation.getLatitude();
                weather.lng = currentLocation.getLongitude();
            }
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
}
