package com.software.anson.mydays.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.software.anson.mydays.R;
import com.software.anson.mydays.activity.ActivitySettings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Anson on 2017/3/12.
 */

public class FragmentUser extends Fragment {
    private static final String TAG = "FragmentUser";

    @BindView(R.id.tv_username)
    TextView tv_username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this, v);

        //Show the username
        SharedPreferences myShared = getActivity().getSharedPreferences("user", Activity.MODE_PRIVATE);
        String username = myShared.getString("username", "");
        tv_username.setText(username);

        return v;
    }


    //Click to Setting Activity
    @OnClick(R.id.btn_settings)
    void onClick() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), ActivitySettings.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
    }

}
