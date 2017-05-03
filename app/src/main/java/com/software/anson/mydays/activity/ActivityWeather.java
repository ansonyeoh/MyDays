package com.software.anson.mydays.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.software.anson.mydays.R;
import com.software.anson.mydays.model.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.MalformedInputException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Anson on 2017/3/19.
 * Backendless Login from https://backendless.com/documentation/users/android/users_login.htm
 */

public class ActivityWeather extends Activity {

    private static final String TAG = "ActivityWeather";
    private static final int MSG_SUCCESS = 0;
    private static final int MSG_FAILED = 1;

    @BindView(R.id.tv_city_name)
    TextView tv_city_name;
    @BindView(R.id.tv_tem)
    TextView tv_temp;
    @BindView(R.id.tv_wind)
    TextView tv_wind;
    @BindView(R.id.tv_hum)
    TextView tv_humidity;
    @BindView(R.id.tv_pressure)
    TextView tv_pressure;
    @BindView(R.id.tv_des)
    TextView tv_des;

    //Obtain the message to update UI
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what) {
                case MSG_SUCCESS:
                    Weather weather = (Weather) msg.obj;
                    String city_name = weather.getCity_name();
                    String des = weather.getDes();
                    String icon = weather.getIcon();
                    Double humidity = weather.getHumidity();
                    Double pressure = weather.getPressure();
                    Double temp = weather.getTemp();
                    Double speed = weather.getSpeed();

                    //According to the mode, display the information
                    SharedPreferences mShared = getSharedPreferences("temp", Activity.MODE_PRIVATE);
                    int temp_mode = mShared.getInt("mode", 1);
                    String tempstr = String.valueOf(Float.parseFloat(String.valueOf(temp - 273.15))) + "°C";

                    if (temp_mode == 2){
                        tempstr = String.valueOf(Float.parseFloat(String.valueOf((temp-273.15)*1.8 + 32))) + "°F";
                    }

                    //Dispaly the data on UI
                    Log.i(TAG, city_name);
                    Toast.makeText(ActivityWeather.this, city_name, Toast.LENGTH_SHORT).show();
                    tv_city_name.setText(city_name);
                    tv_humidity.setText(String.valueOf(humidity) + "%");
                    tv_pressure.setText(String.valueOf(pressure) + "mb");
                    tv_temp.setText(tempstr);
                    tv_wind.setText(String.valueOf(speed)  + "m/s");
                    tv_des.setText(des);
                    break;
                case MSG_FAILED:
                    Toast.makeText(ActivityWeather.this, "Connect Internet Failed...", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        ButterKnife.bind(this);
        Backendless.initApp(this, "816605BB-C858-510E-FF58-19AAD0ADFE00", "77B405BF-FCE5-31B9-FF2D-1B0F4737DC00", "v1");

        //Obtain the Location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //If the location is null, get a new one.
        if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            //String provider = locationManager.getBestProvider(criteria, true);
            String provider = locationManager.NETWORK_PROVIDER;
            myLocation = locationManager.getLastKnownLocation(provider);
            Log.i(TAG, "Location check...");
        }

        //Get the location
        Double lat = myLocation.getLatitude();
        Log.i(TAG, String.valueOf(lat));
        Double lng = myLocation.getLongitude();

        //Send the request to openweathermap
        String url = "http://api.openweathermap.org/data/2.5/weather";
        url = url + "?lat=" + lat + "&lon=" + lng + "&appid=0f5880077aefc6a4feddcef9f74a7494";
        new HttpThread(url).start();
        Log.i(TAG, url);

        Intent intent = getIntent();
        final int id = intent.getIntExtra("id",0);
        SharedPreferences myShared = getSharedPreferences("user", Activity.MODE_PRIVATE);
        final String username = myShared.getString("username", "");
        final String password = myShared.getString("password", "");

        //Display delay 3 seconds
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                if (id == 0){
                    Backendless.UserService.login( username, password, new
                            AsyncCallback<BackendlessUser>()
                            {
                                public void handleResponse( BackendlessUser user )
                                {
                                    // user has been logged in
                                    Intent intent = new Intent();
                                    intent.setClass(ActivityWeather.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                                public void handleFault( BackendlessFault fault )
                                {
                                    // login failed, go to Login Activity
                                    Intent intent = new Intent(ActivityWeather.this, ActivityLogin.class);
                                    startActivity(intent);
                                    Toast.makeText(ActivityWeather.this, fault.getMessage(), Toast.LENGTH_LONG).show();
                                     finish();
                                }
                            });
                }else if (id == 3){
                    Intent intent = new Intent(ActivityWeather.this, ActivitySettings.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 3000);
    }

    //Thread for weather request
    class HttpThread extends Thread {
        private String url;

        HttpThread(String url) {
            this.url = url;
        }
        @Override
        public void run() {
            try {
                //Http request and get the weather data
                URL httpUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(1000);
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer();
                String data;

                while ((data = reader.readLine()) != null) {
                    sb.append(data);
                    Log.i(TAG, sb.toString());
                    System.out.print(sb);
                }
                reader.close();

                Weather current = parseJson(sb.toString());
                if (current != null) {
                    handler.obtainMessage(MSG_SUCCESS, current).sendToTarget();
                } else {
                    handler.obtainMessage(MSG_FAILED).sendToTarget();
                }

            } catch (MalformedInputException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private Weather parseJson(String json) throws JSONException {
        JSONObject object = new JSONObject(json);
        if (object.getString("id") != null) {
            //Json parse
            JSONObject jo = new JSONObject(object.toString());

            //Data of array weather
            JSONArray weather = jo.getJSONArray("weather");
            JSONObject basic = weather.getJSONObject(0);
            String main = basic.getString("main");
            String des = basic.getString("description");
            String icon = basic.getString("icon");

            //Data of object main
            JSONObject detail = jo.getJSONObject("main");
            Double temp = detail.getDouble("temp");
            Double humidity = detail.getDouble("humidity");
            Double pressure = detail.getDouble("pressure");

            //Data of wind
            JSONObject wind = jo.getJSONObject("wind");
            Double speed = wind.getDouble("speed");

            //Get the name of city
            String city_name = jo.getString("name");

            Weather weather1 = new Weather();
            weather1.setCity_name(city_name);
            weather1.setDes(des);
            weather1.setHumidity(humidity);
            weather1.setPressure(pressure);
            weather1.setSpeed(speed);
            weather1.setTemp(temp);
            weather1.setIcon(icon);
            return weather1;
        }
        return null;
    }
}
