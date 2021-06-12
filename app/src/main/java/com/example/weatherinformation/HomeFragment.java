package com.example.weatherinformation;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment implements IFragment {
    private static final String TAG = HomeFragment.class.getSimpleName();

    private boolean executed = false;

    private ImageView imgWeather;
    private TextView txtWeather, txtTemperature, txtAddress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        this.imgWeather = view.findViewById(R.id.imgWeather);
        this.txtWeather = view.findViewById(R.id.txtWeather);
        this.txtTemperature = view.findViewById(R.id.txtTemperature);
        this.txtAddress = view.findViewById(R.id.txtAddress);

        this.executed = true;
        if (GlobalVariable.weatherCurrentData == null) {
            // 위치정보를 받기위해 1초간 딜레이를 적용
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // 초기화
                init();
            }, Constants.LoadingDelay.LONG);
        } else {
            // 날씨 정보가 존재하면
            // 초기화
            init();
        }

        return view;
    }

    @Override
    public boolean isExecuted() {
        return this.executed;
    }

    // 초기화
    private void init() {
        double latitude, longitude;
        String message;

        // 위치정보 얻기
        Location location = ((MainActivity) getActivity()).getLocation();

        if (location == null) {
            // 위치정보 없음
            Log.d(TAG, "위치정보: null");
            message = getString(R.string.msg_location_disable) + "\n(" + Constants.DEFAULT_AREA + ")";
            this.txtAddress.setText(message);

            // 주소로 GPS 정보 얻기
            Point point = Utils.getGpsFromAddress(getContext(), Constants.DEFAULT_AREA);
            if (point == null) {
                Log.d(TAG, "point: null");
                this.executed = false;
                return;
            }

            latitude = point.latitude;
            longitude = point.longitude;
        } else {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            // GPS 정보로 주소 얻기
            String[] address = Utils.getAddressFromGps(getContext(), latitude, longitude);
            if (address != null) {
                this.txtAddress.setText(address[1]);
            } else {
                this.txtAddress.setText(R.string.msg_error);
            }
        }

        if (GlobalVariable.weatherCurrentData == null) {
            // Open Weather api 호출
            callOpenApiWeather(latitude, longitude);
        } else {
            // 날씨 정보가 존재하면
            infoWeather(GlobalVariable.weatherCurrentData);
        }
    }

    /* 날씨 정보 */
    private void infoWeather(WeatherCurrentData data) {
        int weatherId = data.current.weather.get(0).id;         // 날씨 상태 ID
        String weatherIcon = data.current.weather.get(0).icon;  // 날씨 아이콘

        Log.d(TAG, "Weather.Id:" + weatherId);
        Log.d(TAG, "아이콘:" + weatherIcon);
        Log.d(TAG, "날씨:" + data.current.weather.get(0).main);
        Log.d(TAG, "날씨:" + data.current.weather.get(0).description);
        Log.d(TAG, "온도:" + data.current.temp);
        Log.d(TAG, "체감온도:" + data.current.feels_like);

        // 아이콘 표시
        String iconUrl = Constants.WeatherOpenApi.ICON_URL + weatherIcon + "@4x.png";   // 사이즈 4배
        Log.d(TAG, "iconUrl:" + iconUrl);
        loadWeatherIcon(iconUrl);

        // 날씨 text
        String weather = Utils.getWeather(weatherId);
        this.txtWeather.setText(weather);

        String temperature = data.current.temp + "℃";                   // 기온
        temperature += " (체감온도:" + data.current.feels_like + "℃)";  // 체감온도
        this.txtTemperature.setText(temperature);

        if (GlobalVariable.weatherCurrentData == null) {
            // 날씨 정보 설정
            GlobalVariable.weatherCurrentData = data;
        }
        this.executed = false;
    }

    /* 아이콘 표시 */
    private void loadWeatherIcon(String url) {
        // url 을 적용하기 위해 Glide 대신 GlideApp 사용
        GlideApp.with(getContext())
                .load(url)
                .error(R.drawable.ic_alert_circle_24_gray)
                .transition(new DrawableTransitionOptions().crossFade())
                .into(imgWeather);
    }

    /* Open Weather api 호출 */
    private void callOpenApiWeather(double latitude, double longitude) {
        try {
            // 오픈 api 호출
            OkHttpClient okHttpClient = new OkHttpClient();

            String url = Constants.WeatherOpenApi.API_URL;
            url += "?lat=" + latitude;                                  // 위도
            url += "&lon=" + longitude;                                 // 경도
            url += "&exclude=" + "minutely,hourly,daily,alerts";        // 현재날씨 빼고 나머지는 제외
            url += "&units=" + "metric";                                // 섭씨
            url += "&lang=" + "kr";                                     // 한국어
            url += "&appid=" + Constants.WeatherOpenApi.API_KEY;        // api key

            Log.d(TAG, "url:" + url);

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            okHttpClient.newCall(request).enqueue(mCallbackWeather);
        } catch (Exception e) {
            // Error
            this.executed = false;
            Toast.makeText(getContext(), getString(R.string.msg_error), Toast.LENGTH_SHORT).show();
        }
    }

    /* 날씨 정보 조회 결과값 Callback */
    private final Callback mCallbackWeather = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d(TAG, "콜백오류:" + e.getMessage());
            // 위젯은 main 쓰레드에서 처리해야됨
            new Handler(Looper.getMainLooper()).post(() -> {
                // 오류 메시지
                executed = false;
                Toast.makeText(getContext(), getString(R.string.msg_error), Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String responseData = response.body().string();

            if (!TextUtils.isEmpty(responseData)) {
                Log.d(TAG, "서버에서 응답한 데이터:" + responseData);

                // 위젯은 main 쓰레드에서 처리해야됨
                new Handler(Looper.getMainLooper()).post(() -> {
                    // JSON to Object
                    Gson gson = new Gson();
                    WeatherCurrentData data = gson.fromJson(responseData, WeatherCurrentData.class);

                    if (data != null) {
                        // 날씨 정보
                        infoWeather(data);
                    } else {
                        executed = false;
                        Toast.makeText(getContext(), getString(R.string.msg_error), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                executed = false;
            }
        }
    };
}
