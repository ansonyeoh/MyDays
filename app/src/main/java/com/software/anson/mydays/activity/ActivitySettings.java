package com.software.anson.mydays.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.backendless.Backendless;
import com.software.anson.mydays.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Anson on 2017/3/20.
 */

public class ActivitySettings extends Activity {

    @BindView(R.id.temp_switch)
    Switch temp_switch;
    @BindView(R.id.notify_switch)
    Switch notify_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        Backendless.initApp(this, "816605BB-C858-510E-FF58-19AAD0ADFE00", "77B405BF-FCE5-31B9-FF2D-1B0F4737DC00", "v1");

        //Sharedpreference get the temperature mode
        SharedPreferences mShared = getSharedPreferences("temp", Activity.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mShared.edit();

        //Set the switch status
        int cur_mode = mShared.getInt("mode", 1);
        if (cur_mode == 1){
            temp_switch.setChecked(false);
        }else if ( cur_mode == 2){
            temp_switch.setChecked(true);
        }

        //Reset the mode on SharedPreference
        temp_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    editor.putInt("mode", 2);
                    editor.commit();
                }else {
                    editor.putInt("mode", 1);
                    editor.commit();
                }
            }
        });

//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.icon)
//                        .setContentTitle("My notification")
//                        .setContentText("Hello World!");
//        Notification notification = builder.build();
//        notification.flags = Notification.FLAG_NO_CLEAR;
//        notificationManager.notify(0,notification);

        //Not finished yet
        notify_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){

                }
            }
        });
    }

    //Inform Weather page to change the temp mode
    @OnClick(R.id.tv_temp_mode)
    void OnClicktoWeather(){
        Intent intent = new Intent();
        intent.setClass(ActivitySettings.this, ActivityWeather.class);
        intent.putExtra("id", 3);
        startActivity(intent);
        finish();
    }
    // Go back Setting fragment
    @OnClick(R.id.back3)
    void onClickBack() {
        Intent intent = new Intent();
        intent.setClass(ActivitySettings.this, MainActivity.class);
        intent.putExtra("id", 2);
        startActivity(intent);
        overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
        finish();
    }
    //Go to Changepassword Activity
    @OnClick(R.id.tv_changePW)
    void OnClickCPW() {
        Intent intent = new Intent();
        intent.setClass(ActivitySettings.this, ActivityChangePassword.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
        finish();
    }

    //Log out
    @OnClick(R.id.bt_logout)
    void OnClickLogout() {
        SharedPreferences myShared = getSharedPreferences("user",Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = myShared.edit();
        editor.putString("username", "");
        editor.putString("password", "");
        editor.commit();

        Intent intent = new Intent();
        intent.setClass(ActivitySettings.this, ActivityLogin.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
        finish();
    }
}
