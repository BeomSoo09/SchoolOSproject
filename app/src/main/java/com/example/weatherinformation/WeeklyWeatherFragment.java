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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeeklyWeatherFragment extends Fragment implements IFragment {
    private static final String TAG = WeeklyWeatherFragment.class.getSimpleName();

    private boolean executed = false;

    private RecyclerView recyclerView;
    private ArrayList<WeatherDailyData.Daily> items;

    private TextView txtAddress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly_weather, container, false);

        // 리사이클러뷰
        this.recyclerView = view.findViewById(R.id.recyclerView);
        this.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        this.txtAddress = view.findViewById(R.id.txtAddress);

        this.executed = true;
        if (GlobalVariable.weatherDailyData == null) {
            // 위치정보를 받기위해 0.3초간 딜레이를 적용
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // 초기화
                init();
            }, Constants.LoadingDelay.SHORT);
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
                this.executed = false;
                Log.d(TAG, "point: null");
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

        if (GlobalVariable.weatherDailyData == null) {
            // Open Weather api 호출
            callOpenApiWeather(latitude, longitude);
        } else {
            // 날씨 정보가 존재하면
            infoWeather(GlobalVariable.weatherDailyData);
        }
    }

    /* 날씨 정보 */
    private void infoWeather(WeatherDailyData data) {
        // 주간날씨정보
        this.items = data.daily;

        // 리스트에 어뎁터 설정
        WeeklyWeatherAdapter adapter = new WeeklyWeatherAdapter(this.items);
        this.recyclerView.setAdapter(adapter);

        if (GlobalVariable.weatherDailyData == null) {
            // 날씨 정보 설정
            GlobalVariable.weatherDailyData = data;
        }
        this.executed = false;
    }

    /* Open Weather api 호출 */
    private void callOpenApiWeather(double latitude, double longitude) {
        try {
            // 오픈 api 호출
            OkHttpClient okHttpClient = new OkHttpClient();

            String url = Constants.WeatherOpenApi.API_URL;
            url += "?lat=" + latitude;                                  // 위도
            url += "&lon=" + longitude;                                 // 경도
            url += "&exclude=" + "current,minutely,hourly,alerts";      // 주간날씨 빼고 나머지는 제외
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
                    WeatherDailyData data = gson.fromJson(responseData, WeatherDailyData.class);

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
