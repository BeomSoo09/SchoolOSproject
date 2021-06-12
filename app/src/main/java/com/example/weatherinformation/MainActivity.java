package com.example.weatherinformation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private BackPressHandler backPressHandler;

    private Fragment fragment;

    // GPS
    private LocationManager locationManager;
    private Location location;

    // 최소 GPS 정보 업데이트 시간 밀리세컨이므로 1분
    private static final long GPS_MIN_TIME_BW_UPDATES = 1000 * 60;
    // 최소 GPS 정보 업데이트 거리 10미터
    private static final long GPS_MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.app_name);

        // 네비게이션 뷰 (하단에 표시되는 메뉴)
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(mItemSelectedListener);

        // 종료 핸들러
        this.backPressHandler = new BackPressHandler(this);

        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 위치정보 사용여부 체크
        if (checkLocationServicesStatus()) {
            // Location 초기화
            initLocation();
        } else {
            // 위치정보 설정값으로 보여주기
            showLocationSettings();
        }

        // Fragment 메니저를 이용해서 layContent 레이아웃에 Fragment 넣기
        this.fragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.layContent, this.fragment).commit();
    }

    @Override
    public void onBackPressed() {
        this.backPressHandler.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (this.locationManager != null) {
            // 위치정보 갱신 리스너 제거
            this.locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "GPS: " + "위도 " + location.getLatitude() + ", 경도 " + location.getLongitude());
        this.location = location;
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d(TAG, "GPS OFF");

        // GPS OFF 될때
        if (this.locationManager != null) {
            this.locationManager.removeUpdates(this);
        }
        this.location = null;
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d(TAG, "GPS ON");

        // GPS ON 될때
        initLocation();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    /* 위치정보 얻기 (Fragment 에서 호출) */
    public Location getLocation() {
        return this.location;
    }

    /* Location 초기화 */
    private void initLocation() {
        // GPS 사용여부
        boolean gpsEnabled = this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 네트워크 사용여부
        boolean networkEnabled = this.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gpsEnabled && !networkEnabled) {
            // GPS 와 네트워크사용이 가능하지 않음
            Toast.makeText(this, getString(R.string.msg_location_disable), Toast.LENGTH_SHORT).show();
        } else {
            try {
                // 네트워크 정보로 부터 위치값 가져오기
                if (networkEnabled) {
                    this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            GPS_MIN_TIME_BW_UPDATES, GPS_MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    // 이전에 저장된 위치정보가 있으면 가져옴
                    this.location = this.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }

                // GPS 로 부터 위치값 가져오기
                if (gpsEnabled && this.locationManager != null) {
                    this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            GPS_MIN_TIME_BW_UPDATES, GPS_MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (this.location == null) {
                        // 이전에 저장된 위치정보가 있으면 가져옴
                        this.location = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }

                if (this.location != null) {
                    Log.d(TAG, "GPS: " + "위도 " + this.location.getLatitude() + ", 경도 " + this.location.getLongitude());
                } else {
                    // 처음 위치정보 가져올 때는 이전 정보가 없기 때문에 null 값임
                    Log.d(TAG, "GPS: NULL");
                }
            } catch (SecurityException e) {
                Log.d(TAG, "Error: " + e.toString());
            }
        }

        // 날씨

    }

    /* 위치정보 사용여부 체크 */
    private boolean checkLocationServicesStatus() {
        return this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || this.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /* 위치정보 설정값으로 보여주기 */
    private void showLocationSettings() {
        new AlertDialog.Builder(this)
                .setPositiveButton(getString(R.string.dialog_ok), (dialog, id) -> {
                    // 위치 서비스 설정창
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton(getString(R.string.dialog_cancel), (dialog, which) -> dialog.cancel())
                .setCancelable(true)
                .setTitle(getString(R.string.dialog_title_location_setting))
                .setMessage(getString(R.string.dialog_msg_location_setting))
                .show();
    }




    /* BottomNavigationView 선택 리스너 */
    private final BottomNavigationView.OnNavigationItemSelectedListener mItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            // 실행중인지 체크
            if (((IFragment) fragment).isExecuted()) {
                return false;
            }

            switch (menuItem.getItemId()) {
                case R.id.menu_button_home:
                    // 홈
                    setTitle(R.string.app_name);
                    fragment = new HomeFragment();
                    break;
                case R.id.menu_button_temperature_by_time:
                    // 시간대별기온
                    setTitle(R.string.menu_temperature_by_time);
                    fragment = new TemperatureByTimeFragment();
                    break;
                case R.id.menu_button_weekly_weather:
                    // 주간날씨
                    setTitle(R.string.menu_weekly_weather);
                    fragment = new WeeklyWeatherFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.layContent, fragment).commit();
            return true;
        }
    };

    /* Back Press Class */
    private class BackPressHandler {
        private Context context;
        private Toast toast;

        private long backPressedTime = 0;

        public BackPressHandler(Context context) {
            this.context = context;
        }

        public void onBackPressed() {
            if (System.currentTimeMillis() > this.backPressedTime + (Constants.LoadingDelay.LONG * 2)) {
                this.backPressedTime = System.currentTimeMillis();

                this.toast = Toast.makeText(this.context, R.string.msg_back_press_end, Toast.LENGTH_SHORT);
                this.toast.show();

                return;
            }

            if (System.currentTimeMillis() <= this.backPressedTime + (Constants.LoadingDelay.LONG * 2)) {
                // 종료
                moveTaskToBack(true);
                finish();
                this.toast.cancel();
            }
        }
    }
}