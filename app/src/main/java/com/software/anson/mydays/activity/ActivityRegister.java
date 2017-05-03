package com.software.anson.mydays.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
 * Backendless Register form https://backendless.com/documentation/users/android/users_user_registration.htm
 */

public class ActivityRegister extends Activity{

    @BindView(R.id.ed_email_register)
    EditText ed_email;
    @BindView(R.id.ed_PW_register)
    EditText ed_pw;
    @BindView(R.id.ed_PW_confirm)
    EditText ed_pw_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        Backendless.initApp(this, "816605BB-C858-510E-FF58-19AAD0ADFE00", "77B405BF-FCE5-31B9-FF2D-1B0F4737DC00", "v1");
        
    }

    @OnClick(R.id.bt_register)
    void OnClickResgister() {

        String email = ed_email.getText().toString();
        String pw = ed_pw.getText().toString();

        if (TextUtils.isEmpty(ed_email.getText())) {
            Toast.makeText(ActivityRegister.this, "Please enter your E-mail.", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(ed_pw.getText()) || TextUtils.isEmpty(ed_pw_confirm.getText()) ) {
            Toast.makeText(ActivityRegister.this, "Please enter your password.", Toast.LENGTH_LONG).show();
        }else if(ed_pw.getText().toString().equals(ed_pw_confirm.getText().toString())) {
            BackendlessUser user = new BackendlessUser();
            user.setProperty( "email", email );
            user.setPassword( pw );
            Backendless.UserService.register( user, new AsyncCallback<BackendlessUser>() {
                public void handleResponse( BackendlessUser registeredUser ) {
                    // User has been registered and now can login
                    Toast.makeText(ActivityRegister.this,"Register Success. Please check your Email", Toast.LENGTH_LONG).show();
                }
                public void handleFault( BackendlessFault fault ) {
                    // An error has occurred, the error code can be retrieved with
                    fault.getCode();
                    Log.d("Register", "Error: " + fault.getMessage());
                    Toast.makeText(ActivityRegister.this, fault.getMessage(), Toast.LENGTH_LONG).show();
                }
            } );
            finish();
        }else{
            Toast.makeText(ActivityRegister.this, "Your password and confirmation password must be match!", Toast.LENGTH_SHORT).show();
        }
    }
}
