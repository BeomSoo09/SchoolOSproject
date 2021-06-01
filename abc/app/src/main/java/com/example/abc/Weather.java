
package com.example.abc;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

public class Weather extends Thread{

    URL url;//URL 주소 객체
    URLConnection connection;//URL접속을 가지는 객체
    InputStream is;//URL접속에서 내용을 읽기위한 Stream
    InputStreamReader isr;
    BufferedReader br;


    static double lat;//위도
    static double lng ;//경도

    static String temp;
    static String description;
    static String tpmax;
    static String tpmin;
    static String feels;


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
                System.out.println(sc);
                cut = buf.substring(sc);
                sc1 = cut.indexOf(",");
                db = Double.parseDouble(buf.substring(sc + 6, sc + sc1)) - 273.15;
                temp = String.valueOf((int) Math.round(db));
                System.out.println(temp);
                sc = buf.indexOf("id");
                cut = buf.substring(sc);
                sc1 = cut.indexOf(",");
                description = buf.substring(sc + 4, sc + 7);
                sc = buf.indexOf("temp_max");
                System.out.println(sc);
                cut = buf.substring(sc);
                sc1 = cut.indexOf(",");
                db = Double.parseDouble(buf.substring(sc + 10, sc + sc1)) - 273.15;
                tpmax = String.valueOf((int) Math.round(db));
                sc = buf.indexOf("temp_min");
                System.out.println(sc);
                cut = buf.substring(sc);
                sc1 = cut.indexOf(",");
                db = Double.parseDouble(buf.substring(sc + 10, sc + sc1)) - 273.15;
                tpmin = String.valueOf((int) Math.round(db));
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
