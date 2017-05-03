package com.software.anson.mydays.activity;

//import android.content.res.Configuration;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.software.anson.mydays.fragment.FragmentCosts;
import com.software.anson.mydays.fragment.FragmentEvents;
import com.software.anson.mydays.fragment.FragmentUser;
import com.software.anson.mydays.fragment.FragmentMap;
import com.software.anson.mydays.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends ActionBarActivity{

    private FragmentEvents fragmentEvents;
    private FragmentCosts fragmentCosts;
    private FragmentMap fragmentMap;
    private FragmentUser fragmentUser;

    @BindView(R.id.layout_events)
    RadioButton rbEvent;
    @BindView(R.id.layout_costs)
    RadioButton rbCost;
    @BindView(R.id.layout_map)
    RadioButton rbMap;
    @BindView(R.id.layout_user)
    RadioButton rbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Init view
        initView();
        clickEventsBtn();

        //According to the extra data from different activity, go to previous fragment
        Intent intent = getIntent();
        final int id = intent.getIntExtra("id",0);
        if (id == 0){
            clickEventsBtn();
        }else if (id == 1) {
            rbCost.setChecked(true);
            clickCostsBtn();
        }else if (id == 2){
            rbUser.setChecked(true);
            clickUserBtn();
        }
    }

    //Set the basic view
    private void initView() {
        //Configure navigation
        RadioGroup myTabRg;
        myTabRg = (RadioGroup) findViewById(R.id.tab_menu);
        Drawable event_bg = getResources().getDrawable(R.drawable.tab_selector_event);
        event_bg.setBounds(0,0,64,64);
        rbEvent.setCompoundDrawables(null, event_bg, null, null);

        Drawable cost_bg = getResources().getDrawable(R.drawable.tab_selector_cost);
        cost_bg.setBounds(0,0,64,64);
        rbCost.setCompoundDrawables(null, cost_bg, null, null);

        Drawable map_bg = getResources().getDrawable(R.drawable.tab_selector_map);
        map_bg.setBounds(0,0,64,64);
        rbMap.setCompoundDrawables(null, map_bg, null, null);

        Drawable user_bg = getResources().getDrawable(R.drawable.tab_selector_user);
        user_bg.setBounds(0,0,64,64);
        rbUser.setCompoundDrawables(null, user_bg, null, null);

        //Navigation change listener
        myTabRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.layout_events:
                        clickEventsBtn();
                        break;
                    case R.id.layout_costs:
                        clickCostsBtn();
                        break;
                    case R.id.layout_map:
                        clickMapBtn();
                        break;
                    case R.id.layout_user:
                        clickUserBtn();
                        break;
                }
            }
        });
    }

    //Show the selected fragment and button, and hide other fragments
    private void clickEventsBtn() {
        FragmentTransaction transaction = this
                .getSupportFragmentManager().beginTransaction();

        if (fragmentEvents == null) {
            fragmentEvents = new FragmentEvents();
            transaction.add(R.id.frame_content, fragmentEvents, "events");
        }
        hideFragment(transaction);
        transaction.show(fragmentEvents);
        transaction.commit();
    }

    //Show the selected fragment and button, and hide other fragments
    private void clickCostsBtn() {
        FragmentTransaction transaction = this
                .getSupportFragmentManager().beginTransaction();

        if (fragmentCosts == null) {
            fragmentCosts = new FragmentCosts();
            transaction.add(R.id.frame_content, fragmentCosts, "costs");
        }

        hideFragment(transaction);
        transaction.show(fragmentCosts);
        transaction.commit();
    }

    //Show the selected fragment and button, and hide other fragments
    private void clickMapBtn() {
        //this.getActionBar().setTitle(R.string.Map);
        FragmentTransaction transaction = this
                .getSupportFragmentManager().beginTransaction();

        if (fragmentMap == null) {
            fragmentMap = new FragmentMap();
            transaction.add(R.id.frame_content, fragmentMap, "map");
        }

        hideFragment(transaction);
        transaction.show(fragmentMap);
        transaction.commit();
    }

    //Show the selected fragment and button, and hide other fragments
    private void clickUserBtn() {
        FragmentTransaction transaction = this
                .getSupportFragmentManager().beginTransaction();

        if (fragmentUser == null) {
            fragmentUser = new FragmentUser();
            transaction.add(R.id.frame_content, fragmentUser, "user");
        }

        hideFragment(transaction);
        transaction.show(fragmentUser);
        transaction.commit();
    }

    //Hide fragments
    private void hideFragment(FragmentTransaction transaction) {

        if (fragmentEvents != null) {
            transaction.hide(fragmentEvents);
        }
        if (fragmentCosts != null) {
            transaction.hide(fragmentCosts);
        }
        if (fragmentMap != null) {
            transaction.hide(fragmentMap);
        }
        if (fragmentUser != null) {
            transaction.hide(fragmentUser);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}