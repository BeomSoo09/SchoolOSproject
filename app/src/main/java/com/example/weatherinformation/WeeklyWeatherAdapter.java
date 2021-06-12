package com.example.weatherinformation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;

public class WeeklyWeatherAdapter extends RecyclerView.Adapter<WeeklyWeatherAdapter.ViewHolder> {

    private ArrayList<WeatherDailyData.Daily> items;

    public WeeklyWeatherAdapter(ArrayList<WeatherDailyData.Daily> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weekly_weather, null);

        // Item 사이즈 조절
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        // ViewHolder 생성
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtWeek.setText(Utils.getDate("EE", this.items.get(position).dt * 1000));    // 요일
        holder.txtDate.setText(Utils.getDate("MM.dd", this.items.get(position).dt * 1000)); // 날자
        holder.txtTemperatureMax.setText(this.items.get(position).temp.max + "℃");          // 최고온도
        holder.txtTemperatureMin.setText(this.items.get(position).temp.min + "℃");          // 최저온도

        // 아이콘 표시
        String iconUrl = Constants.WeatherOpenApi.ICON_URL + this.items.get(position).weather.get(0).icon + ".png";
        // url 을 적용하기 위해 Glide 대신 GlideApp 사용
        GlideApp.with(holder.imgWeather.getContext())
                .load(iconUrl)
                .error(R.drawable.ic_alert_circle_24_gray)
                .transition(new DrawableTransitionOptions().crossFade())
                .into(holder.imgWeather);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgWeather;
        private TextView txtWeek, txtDate, txtTemperatureMax, txtTemperatureMin;

        private ViewHolder(View view) {
            super(view);

            this.imgWeather = view.findViewById(R.id.imgWeather);
            this.txtWeek = view.findViewById(R.id.txtWeek);
            this.txtDate = view.findViewById(R.id.txtDate);
            this.txtTemperatureMax = view.findViewById(R.id.txtTemperatureMax);
            this.txtTemperatureMin = view.findViewById(R.id.txtTemperatureMin);
        }
    }
}
