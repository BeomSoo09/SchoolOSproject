package com.example.weatherinformation;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.weatherinformation.graph.GraphData;
import com.example.weatherinformation.graph.LineGraphView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TemperatureByTimeFragment extends Fragment implements IFragment {
    private static final String TAG = TemperatureByTimeFragment.class.getSimpleName();

    private boolean executed = false;

    private FrameLayout layGraph;
    private LineGraphView graph;                // 선그래프

    private TextView txtAddress;

    private static final int GRAPH_X_MAX = 7;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temperature_by_time, container, false);

        // 그래프 레이아웃
        this.layGraph = view.findViewById(R.id.layGraph);

        this.txtAddress = view.findViewById(R.id.txtAddress);

        // 그래프 초기화
        this.graph = new LineGraphView(getContext(), 2);
        this.layGraph.addView(this.graph, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        this.executed = true;
        if (GlobalVariable.weatherHourlyData == null) {
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

        if (GlobalVariable.weatherHourlyData == null) {
            // Open Weather api 호출
            callOpenApiWeather(latitude, longitude);
        } else {
            // 날씨 정보가 존재하면
            infoWeather(GlobalVariable.weatherHourlyData);
        }
    }

    /* 날씨 정보 */
    private void infoWeather(WeatherHourlyData data) {
        if (data.hourly.size() == 0) {
            return;
        }

        ArrayList<GraphData[]> graphDataList = new ArrayList<>();

        ArrayList<String> labels = new ArrayList<>();   // X축 라벨
        long max = 0;                                   // 최대 Y 값

        for (int i=0; i<data.hourly.size(); i++) {
            WeatherHourlyData.Hourly hourly = data.hourly.get(i);

            // 데이터 구성
            GraphData[] graphData = new GraphData[2];

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(hourly.dt * 1000);
            // 시간 라벨
            labels.add(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));

            // 소수점 표시하기 위해 * 10
            graphData[0] = new GraphData(i, (int) Math.floor(hourly.temp * 10));
            graphData[1] = new GraphData(i, (int) Math.floor(hourly.feels_like * 10));

            if (max < graphData[0].y) {
                max = graphData[0].y;
            }

            graphDataList.add(graphData);

            if (i == GRAPH_X_MAX) {
                break;
            }
        }

        // 다시 10으로 나눔
        max = max / 10;

        if (max > 12) {
            max += (max / 5);
            // 홀수이면 짝수로
            if ((max % 2) == 1) {
                max++;
            }
        } else {
            // 12보다 작으면 최대값 12으로 적용
            max = 12;
        }

        // 소수점 1자리 포함하기 때문에 100
        if (max > 100) {
            max = 100;
        }

        // 6등분하기 위해 max 값 조정
        max = (long) Math.ceil(max / 6.0) * 6;

        this.graph.setMaxX(GRAPH_X_MAX, 7, false);
        // 소수점 표시하기 위해 * 10
        this.graph.setMaxY((int) max * 10, 6, true);

        this.graph.setLabels(labels);               // X축 라벨
        this.graph.addData(graphDataList);          // 데이터 넣기

        // 그래프 색상
        int[] color = new int[2];
        color[0] = ContextCompat.getColor(getContext(), R.color.graph_data_temp_color);         // 온도
        color[1] = ContextCompat.getColor(getContext(), R.color.graph_data_feels_like_color);   // 체감온도

        // 색상 적용
        this.graph.setLineColor(color);

        // 선 두께 설정
        this.graph.setStrokeWidth(3);

        // 그리기
        this.graph.reDrawAll();
        this.graph.invalidate();

        if (GlobalVariable.weatherHourlyData == null) {
            // 날씨 정보 설정
            GlobalVariable.weatherHourlyData = data;
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
            url += "&exclude=" + "current,minutely,daily,alerts";       // 시간별날씨 빼고 나머지는 제외
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
                    WeatherHourlyData data = gson.fromJson(responseData, WeatherHourlyData.class);

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
