package com.js980112.hotweather;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class WeatherAPI {
    private String dust="설정안됨",weather="설정안됨";
    int gpsX,gpsY;

    String serviceKey="TNMkH24ASMXMAWIxdz7LS98cJ84n%2BYtt0IYDbYXfAc8%2FOkGN79yMjjQqeE1B3vW6REvoaoivA1Lbd6vd7KCmdw%3D%3D";

    String url;
    String url2;

    String sido;

    Context context;

    DBHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor;


    private static int pty,sky,temperature,dust1,dust2;
    //pty:비/눈 sky:날씨 temperature:온도(3시간 기준-T3H)
    //dust1:미세먼지 dust2:초미세먼지

    private static String gungu;
    //군/구

    public WeatherAPI(String area1, String area2, Context context,DBHelper dbHelper){
        this.context=context;
        this.dbHelper=dbHelper;
        db=this.dbHelper.getWritableDatabase();


        setGPS(area1,area2);
        setTime();
        setReport();
        setDust(area1);
    }

    private void setTime(){
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat( "yyyyMMdd", Locale.KOREA );
        SimpleDateFormat mSimpleDateFormat2 = new SimpleDateFormat( "HH", Locale.KOREA );
        SimpleDateFormat mSimpleDateFormat3 = new SimpleDateFormat( "mm", Locale.KOREA );
        String mTime = mSimpleDateFormat.format (new Date());
        String bTime = mSimpleDateFormat2.format (new Date());
        String sTime = mSimpleDateFormat3.format (new Date());

        if(Integer.parseInt(bTime)<3){
            Calendar calendar=new GregorianCalendar();
            calendar.add(Calendar.DATE,-1);
            mTime = mSimpleDateFormat.format (calendar.getTime());
            bTime="2300";
        }else{
                switch (Integer.parseInt(bTime)){
                    case 3:case 4:case 5:
                        bTime="0200";
                        break;
                    case 6:case 7:case 8:
                        bTime="0500";
                        break;
                    case 9:case 10:case 11:
                        bTime="0800";
                        break;
                    case 12: case 13:case 14:
                        bTime="1100";
                        break;
                    case 15:case 16:case 17:
                        bTime="1400";
                        break;
                    case 18:case 19: case 20:
                        bTime="1700";
                        break;
                    case 21:case 22:case 23:
                        bTime="2000";
                        break;
                }
        }

        url="http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData?serviceKey="+serviceKey+
                "&base_date="+mTime+"&base_time="+bTime+"&nx="+gpsX+"&ny="+gpsY+"&numOfRows=10&pageSize=10&pageNo=1&startPage=1&_type=xml";
    }


//    - 하늘상태(SKY) 코드 : 맑음(1), 구름조금(2), 구름많음(3), 흐림(4)
//    - 강수형태(PTY) 코드 : 없음(0), 비(1), 비/눈(2), 눈(3)
//    여기서 비/눈은 비와 눈이 섞여 오는 것을 의미 (진눈개비)
//    T3H- 3시간 동안 기온
    private void setReport(){
        new DownloadTask_weather().execute(url);
        Log.i("WeatherAPI",sky+" "+pty+" "+temperature+" "+url);
    }

    private void setDust(String area){
        int rows=30;
        switch (area){
            case "경기도":
                rows=40;
                break;
            case "광주광역시":
            case "대전광역시":
            case "울산광역시":
            case "강원도":
            case "충청북도":
                rows=8;
                break;
            case "전라남도":
            case "경상북도":
            case "경상남도":
            case "인천광역시":
                rows=10;
                break;
            case "제주도":
            case "세종특별자치시":
                rows=2;
                break;
            case "서울특별시":
                rows=30;
                break;
             default:
                 rows=20;
        }

        url2="http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnMesureSidoLIst?serviceKey="+serviceKey
        +"&numOfRows="+rows+"&pageSize=30&pageNo=1&startPage=1&sidoName="+sido+"&searchCondition=DAILY";

        new DownloadTask_dust().execute(url2);
        Log.i("WeatherAPI",dust1+" "+dust2+" "+url2);
    }


    private void setGPS(String area1,String area2){
        gungu =area2;

        switch (area1){
            case "서울특별시":
                sido="%EC%84%9C%EC%9A%B8";
                break;
            case "부산광역시":
                sido="%EB%B6%80%EC%82%B0";
                break;
            case "대구광역시":
                sido="%EB%8C%80%EA%B5%AC";
                break;
            case "인천광역시":
                sido="%EC%9D%B8%EC%B2%9C";
                break;
            case "광주광역시":
                sido="%EA%B4%91%EC%A3%BC";
                break;
            case "대전광역시":
                sido="%EB%8C%80%EC%A0%84";
                break;
            case "울산광역시":
                sido="%EC%9A%B8%EC%82%B0";
                break;
            case "경기도":
                sido="%EA%B2%BD%EA%B8%B0";
                break;
            case "강원도":
                sido="%EA%B0%95%EC%9B%90";
                break;
            case "충청북도":
                sido="%EC%B6%A9%EB%B6%81";
                break;
            case "충청남도":
                sido="%EC%B6%A9%EB%82%A8";
                break;
            case "전라북도":
                sido="%EC%A0%84%EB%B6%81";
                break;
            case "전라남도":
                sido="%EC%A0%84%EB%82%A8";
                break;
            case "경상북도":
                sido="%EA%B2%BD%EB%B6%81";
                break;
            case "경상남도":
                sido="%EA%B2%BD%EB%82%A8";
                break;
            case "제주도":
                sido="%EC%A0%9C%EC%A3%BC";
                break;
            case "세종특별자치시":
                sido="%EC%84%B8%EC%A2%85";
                break;
        }
        //select x,y from map where sido=area1 and gungu=area2;
        String query="select x,y from maps where sido='"+area1+"'and gungu='"+area2+"'";
        try{
        cursor=db.rawQuery(query,null);
        while (cursor.moveToNext()){
            gpsX=Integer.parseInt(cursor.getString(cursor.getColumnIndex("x")));
            gpsY=Integer.parseInt(cursor.getString(cursor.getColumnIndex("y")));
        }
        }catch (Exception e){
            e.printStackTrace();
            gpsX=60;
            gpsY=127;
        }
    }

    public int getTemperature() {
        return temperature;
    }

    public String[] getDust() {
        String[] dusts=new String[2];
        if(dust1>101)
            dusts[0]="매우 나쁨";
        else if(dust1>51)
            dusts[0]="나쁨";
        else if(dust1>31)
            dusts[0]="보통";
        else
            dusts[0]="좋음";

        if(dust2>51)
            dusts[1]="매우 나쁨";
        else if(dust2>26)
            dusts[1]="나쁨";
        else if(dust2>16)
            dusts[1]="보통";
        else
            dusts[1]="좋음";

        return dusts;
    }

    public String getWeather() {
        if(pty!=0){
            switch (pty){
                case 1:
                case 2:
                    return "비";
                case 3:
                    return "눈";
            }
        }else{
            switch (sky){
                case 1:
                    return "맑음";
                case 2:
                    return "구름조금";
                case 3:
                    return "구름많음";
                case 4:
                    return "흐림";
            }
        }
        return "맑음";
    }

    private static class DownloadTask_weather extends AsyncTask<String ,Void,String> {
        //    - 하늘상태(SKY) 코드 : 맑음(1), 구름조금(2), 구름많음(3), 흐림(4)
        //    - 강수형태(PTY) 코드 : 없음(0), 비(1), 비/눈(2), 눈(3)
        //    여기서 비/눈은 비와 눈이 섞여 오는 것을 의미 (진눈개비)
        //    T3H- 3시간 동안 기온
        boolean sType=false,pType=false,tType=false,cType=false,fType=false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            parseXml(s);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return (String) downloadUrl(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return "다운로드실패";
            }
        }

        private String downloadUrl(String myurl) throws IOException {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(myurl);
                conn = (HttpURLConnection) url.openConnection();
                InputStream is = conn.getInputStream();
                BufferedInputStream bufIs = new BufferedInputStream(is);
                InputStreamReader isReader = new InputStreamReader(bufIs, "utf-8");
                BufferedReader bufReader = new BufferedReader(isReader);

                String line = null;
                StringBuilder builder = new StringBuilder();
                while ((line = bufReader.readLine()) != null) {
                    builder.append(line);
                }
                return builder.toString();
            } finally {
                conn.disconnect();
            }
        }

        private void parseXml(String result) {
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new StringReader(result));

                int parserEvent = parser.getEventType();
                //StringBuilder builder=new StringBuilder();

                //    - 하늘상태(SKY) 코드 : 맑음(1), 구름조금(2), 구름많음(3), 흐림(4)
                //    - 강수형태(PTY) 코드 : 없음(0), 비(1), 비/눈(2), 눈(3)
                //    여기서 비/눈은 비와 눈이 섞여 오는 것을 의미 (진눈개비)
                //    T3H- 3시간 동안 기온

                while (parserEvent != XmlPullParser.END_DOCUMENT) {
                    if (parserEvent == XmlPullParser.START_TAG) {
                        if (parser.getName().equals("category"))
                            cType=true;
                         else if (parser.getName().equals("fcstValue"))
                            fType=true;

                    } else if (parserEvent == XmlPullParser.TEXT) {
                        if (cType) {

                            Log.i("WeatherAPI",parser.getText());
                            if(parser.getText().equals("PTY"))
                                pType=true;
                            else if(parser.getText().equals("SKY"))
                                sType=true;
                            else if(parser.getText().equals("T3H"))
                                tType=true;
                            cType=false;
                        }else if(fType){
                            if(pType){
                                pty = Integer.parseInt(parser.getText());
                                pType=false;
                            }else if(sType){
                                sky = Integer.parseInt(parser.getText());
                                sType=false;
                            }else if(tType){
                                temperature = Integer.parseInt(parser.getText());
                                tType=false;
                            }
                            fType = false;
                            Log.i("Weather",pty+" "+sky+" "+temperature);
                        }
                    }
                    //}
                    parserEvent = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //DUST
    private static class DownloadTask_dust extends AsyncTask<String ,Void,String> {
        //   cityName-구/군
        //   pm10Value-미세먼지
        //   pm25Value-초미세먼지

        boolean cType=false,pType1=false,pType2=false,check=false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            parseXml(s);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return (String) downloadUrl(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return "다운로드실패";
            }
        }

        private String downloadUrl(String myurl) throws IOException {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(myurl);
                conn = (HttpURLConnection) url.openConnection();
                InputStream is = conn.getInputStream();
                BufferedInputStream bufIs = new BufferedInputStream(is);
                InputStreamReader isReader = new InputStreamReader(bufIs, "utf-8");
                BufferedReader bufReader = new BufferedReader(isReader);

                String line = null;
                StringBuilder builder = new StringBuilder();
                while ((line = bufReader.readLine()) != null) {
                    builder.append(line);
                }
                return builder.toString();
            } finally {
                conn.disconnect();
            }
        }

        private void parseXml(String result) {
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new StringReader(result));

                int parserEvent = parser.getEventType();
                //StringBuilder builder=new StringBuilder();

                while (parserEvent != XmlPullParser.END_DOCUMENT) {
                    if (parserEvent == XmlPullParser.START_TAG) {
                        if (parser.getName().equals("cityName"))
                            cType=true;
                        else if (parser.getName().equals("pm10Value"))
                            pType1=true;
                        else if (parser.getName().equals("pm25Value"))
                            pType2=true;

                    } else if (parserEvent == XmlPullParser.TEXT) {
                        if (cType) {
                            if(parser.getText().equals(gungu)){
                                check=true;
                            }
                            cType=false;
                        }else if(pType1){
                            if(check)
                                dust1 = Integer.parseInt(parser.getText());

                            pType1 = false;
                        }else if(pType2){
                            if(check) {
                                dust2 = Integer.parseInt(parser.getText());
                                check=false;
                            }
                            pType2 = false;
                            Log.i("Weather",dust1+" "+dust2);
                        }
                    }
                    //}
                    parserEvent = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
