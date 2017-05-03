package com.software.anson.mydays.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.software.anson.mydays.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Anson on 2017/4/10.
 * Backendless Password reset from https://backendless.com/documentation/users/android/users_password_recovery.htm
 */

public class ActivityChangePassword extends Activity {

    @BindView(R.id.ed_currentpw)
    EditText ed_current_pw;
    @BindView(R.id.ed_newpw)
    EditText ed_password;
    @BindView(R.id.ed_newpw_confirm)
    EditText ed_password_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        ButterKnife.bind(this);
        Backendless.initApp(this, "816605BB-C858-510E-FF58-19AAD0ADFE00", "77B405BF-FCE5-31B9-FF2D-1B0F4737DC00", "v1");
    }

    @OnClick(R.id.bt_modify)
    void OnClickModify() {
        //SharedPreference to get the username and previous password
        SharedPreferences myShared = getSharedPreferences("user", Activity.MODE_PRIVATE);
        String username = myShared.getString("username","");
        String previous = myShared.getString("password","");

        if (TextUtils.isEmpty(ed_current_pw.getText()))
        {
            Toast.makeText(ActivityChangePassword.this, "Please enter your password", Toast.LENGTH_SHORT).show();
        }else if(ed_current_pw.getText().toString().equals(previous))
        {//If the password is right then go to next step
            if(ed_password.getText().toString().equals(ed_password_confirm.getText().toString())) {
                Backendless.UserService.restorePassword(username , new
                        AsyncCallback<Void>()
                        {
                            public void handleResponse( Void response )
                            {
                                // Backendless has completed the operation - an email has been sent to the user
                                Intent intent = new Intent(ActivityChangePassword.this, ActivityLogin.class);
                                startActivity(intent);
                                Toast.makeText(ActivityChangePassword.this, "Please check your Email.", Toast.LENGTH_LONG).show();
                                finish();
                            }
                            public void handleFault( BackendlessFault fault )
                            {
                                // Password revovery failed, to get the error code call fault.
                                Toast.makeText(ActivityChangePassword.this, fault.getMessage(), Toast.LENGTH_LONG).show();
                            }});
            }else{
                Toast.makeText(ActivityChangePassword.this, "Your password and confirmation password must be match!", Toast.LENGTH_SHORT).show();
            }

        }else if (TextUtils.isEmpty(ed_password.getText()) || TextUtils.isEmpty(ed_password_confirm.getText()) )
        {
            Toast.makeText(ActivityChangePassword.this, "Please enter your new password.", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(ActivityChangePassword.this, "Please enter correct password", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.back4)
    void onClickBack() {
        Intent intent = new Intent();
        intent.setClass(ActivityChangePassword.this, ActivitySettings.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
        finish();
    }
}
