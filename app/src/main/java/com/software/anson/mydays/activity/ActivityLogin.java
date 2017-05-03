package com.software.anson.mydays.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.software.anson.mydays.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Anson on 2017/4/8.
 * Backendless Login from https://backendless.com/documentation/users/android/users_login.htm
 */

public class ActivityLogin extends Activity {

    @BindView(R.id.ed_email_login)
    EditText ed_email;
    @BindView(R.id.ed_password)
    EditText ed_pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Backendless.initApp(this, "816605BB-C858-510E-FF58-19AAD0ADFE00", "77B405BF-FCE5-31B9-FF2D-1B0F4737DC00", "v1");
    }

    @OnClick(R.id.bt_register)
    void OnClikeRegitser() {
        Intent intent = new Intent();
        intent.setClass(ActivityLogin.this, ActivityRegister.class);
        startActivity(intent);
    }

    @OnClick(R.id.bt_login)
    void OnClickLogin() {
        final String username = ed_email.getText().toString();
        final String password = ed_pw.getText().toString();
        //Backendless Login service
        Backendless.UserService.login( username, password, new
                AsyncCallback<BackendlessUser>()
                {
                    public void handleResponse( BackendlessUser user )
                    {
                        SharedPreferences myShared = getSharedPreferences("user", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = myShared.edit();
                        editor.putString("username", username);
                        editor.putString("password", password);
                        editor.commit();
                        // user has been logged in
                        Intent intent = new Intent();
                        intent.setClass(ActivityLogin.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    public void handleFault( BackendlessFault fault )
                    {
                        // login failed, to get the error code call fault.getCode()
                        Toast.makeText(ActivityLogin.this, fault.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }
}
