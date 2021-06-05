package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app1.R;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView temp,tempMax,tempMin,feelTemp,rode;
    ImageView i1;

    Permission p = new Permission();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println(p.tpMin);

        temp = (TextView)findViewById(R.id.temp);
        tempMax = (TextView)findViewById(R.id.tpmax);
        tempMin = (TextView)findViewById(R.id.tpmin);
        feelTemp = (TextView)findViewById(R.id.feeltemp);
        rode = (TextView)findViewById(R.id.geo);
        i1 = (ImageView)findViewById(R.id.weather);

        temp.setText(p.tp+"℃");
        tempMax.setText(p.tpMax+"℃");
        tempMin.setText(p.tpMin+"℃");
        feelTemp.setText(p.feels+"℃");
        rode.setText(p.geo);

        Image(p.description);

    }

    void Image(String id){
        try {
            switch (id) {
                case "800":
                    i1.setBackgroundResource(R.drawable._1d2x);
                case "801":
                    i1.setBackgroundResource(R.drawable._2d2x);
                    break;
                case "802":
                    i1.setBackgroundResource(R.drawable._3d2x);
                    break;
                case "803":
                case "804":
                    i1.setBackgroundResource(R.drawable._4d2x);
                    break;
                default:
                    switch (Integer.parseInt(id) / 100) {
                        case 2:
                            i1.setBackgroundResource(R.drawable._11d2x);
                            break;
                        case 3:
                            i1.setBackgroundResource(R.drawable._9d2x);
                            break;
                        case 5:
                            i1.setBackgroundResource(R.drawable._0d2x);
                            break;
                        case 6:
                            i1.setBackgroundResource(R.drawable._13d2x);
                            break;
                        case 7:
                            i1.setBackgroundResource(R.drawable._50d2x);
                            break;
                    }
                    break;
            }
        }catch(Exception e){ }
    }
}
