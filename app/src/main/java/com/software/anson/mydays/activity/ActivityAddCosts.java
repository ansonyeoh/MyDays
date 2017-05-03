package com.software.anson.mydays.activity;

import android.app.TabActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.software.anson.mydays.R;
import com.software.anson.mydays.db.DbCosts;
import com.software.anson.mydays.model.Cost;

import java.sql.Date;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Anson on 2017/2/24.
 */

public class ActivityAddCosts extends TabActivity {
    private static final String TAG = "addcosts";
    private TabHost tabhost;
    private DbCosts dbCosts;
    private SQLiteDatabase dbwriter;
    private ContentValues cv;

//    BindView(Butterknif)
    @BindView(R.id.img_item)
    ImageView img_item;
    @BindView(R.id.img_item2)
    ImageView img_item2;
    @BindView(R.id.name_item)
    TextView tv_name;
    @BindView(R.id.name_item2)
    TextView tv_name2;
    @BindView(R.id.cost_item)
    EditText et_price;
    @BindView(R.id.et_remarks)
    EditText et_remarks;
    @BindView(R.id.amount_item)
    EditText et_income;
    @BindView(R.id.et_remarks2)
    EditText et_remarks2;
    @BindView(R.id.radiogroup1)
    RadioGroup radioGroup1;
    @BindView(R.id.radiogroup2)
    RadioGroup radioGroup2;
    @BindView(R.id.radiogroup3)
    RadioGroup radioGroup3;
    @BindView(R.id.radiogroup4)
    RadioGroup radioGroup4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_costs);
        ButterKnife.bind(this);

        Backendless.initApp(this,
                "816605BB-C858-510E-FF58-19AAD0ADFE00", "77B405BF-FCE5-31B9-FF2D-1B0F4737DC00", "v1");//Backendless API

        // Bind two tab views
        LayoutInflater inflater = this.getLayoutInflater();
        View view1 = inflater.inflate(R.layout.tab_style, null);
        TextView txt1 = (TextView) view1.findViewById(R.id.tab_lable);
        txt1.setText("Expense");

        View view2 = inflater.inflate(R.layout.tab_style, null);
        TextView txt2 = (TextView) view2.findViewById(R.id.tab_lable);
        txt2.setText("Income");

        //Configure tabs
        tabhost = getTabHost();

        tabhost.addTab(tabhost
                .newTabSpec("one")
                .setIndicator(view1)
                .setContent(R.id.tab1));

        tabhost.addTab(tabhost
                .newTabSpec("two")
                .setIndicator(view2)
                .setContent(R.id.tab2));
        updateTab(tabhost);
        tabhost.setOnTabChangedListener(new tabChangedListener());

        //Set default images and names
        img_item.setImageResource(R.drawable.food);
        tv_name.setText("Food");
        img_item2.setImageResource(R.drawable.wage);
        tv_name2.setText("Wage");

        //Set items check listener
        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.food:
                        img_item.setImageResource(R.drawable.food);
                        tv_name.setText("Food");
                        radioGroup1.clearCheck();
                        break;
                    case R.id.traffic:
                        img_item.setImageResource(R.drawable.traffic);
                        tv_name.setText("Traffic");
                        radioGroup1.clearCheck();
                        break;
                    case R.id.clothes:
                        img_item.setImageResource(R.drawable.clothes);
                        tv_name.setText("Clothes");
                        radioGroup1.clearCheck();
                        break;
                    case R.id.commodity:
                        img_item.setImageResource(R.drawable.commodity);
                        tv_name.setText("Commodity");
                        radioGroup1.clearCheck();
                        break;
                    case R.id.kids:
                        img_item.setImageResource(R.drawable.kids);
                        tv_name.setText("Kids");
                        radioGroup1.clearCheck();
                        break;
                }
            }
        });

        //Set items check listener
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.online:
                        img_item.setImageResource(R.drawable.onlineshopping);
                        tv_name.setText("Online Shopping");
                        radioGroup2.clearCheck();
                        break;
                    case R.id.phone:
                        img_item.setImageResource(R.drawable.phone);
                        tv_name.setText("Phone Bill");
                        radioGroup2.clearCheck();
                        break;
                    case R.id.amuse:
                        img_item.setImageResource(R.drawable.emtertainment);
                        tv_name.setText("Amusement");
                        radioGroup2.clearCheck();
                        break;
                    case R.id.medical:
                        img_item.setImageResource(R.drawable.medical);
                        tv_name.setText("Medical Care");
                        radioGroup2.clearCheck();
                        break;
                    case R.id.makeup:
                        img_item.setImageResource(R.drawable.makeup);
                        tv_name.setText("Makeup");
                        radioGroup2.clearCheck();
                        break;
                }
            }
        });

        //Set items check listener
        radioGroup3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.others:
                        img_item.setImageResource(R.drawable.others);
                        tv_name.setText("Others");
                        radioGroup3.clearCheck();
                        break;
                }
            }
        });

        //Set items check listener
        radioGroup4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.wage:
                        img_item2.setImageResource(R.drawable.wage);
                        tv_name2.setText("Wage");
                        radioGroup4.clearCheck();
                        break;
                    case R.id.parttime:
                        img_item2.setImageResource(R.drawable.parttime);
                        tv_name2.setText("Part-time Job");
                        radioGroup4.clearCheck();
                        break;
                    case R.id.investment:
                        img_item2.setImageResource(R.drawable.invest);
                        tv_name2.setText("Investment");
                        radioGroup4.clearCheck();
                        break;
                    case R.id.reward:
                        img_item2.setImageResource(R.drawable.reward);
                        tv_name2.setText("Reward");
                        radioGroup4.clearCheck();
                        break;
                    case R.id.others2:
                        img_item2.setImageResource(R.drawable.others);
                        tv_name2.setText("Others");
                        radioGroup4.clearCheck();
                        break;
                }
            }
        });

        //Declare the database objects
        dbCosts = new DbCosts(this);
        dbwriter = dbCosts.getWritableDatabase();
    }

    //Submit button click event
    @OnClick(R.id.btn_submit)
    void onClickSubmit(View v){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");//set the format of the date
        Date curDate = new Date(System.currentTimeMillis());// obtain system current date
        final String date = formatter.format(curDate);
        cv = new ContentValues();//declare local storage object

        //Determine if the cost or income pages
        if (tabhost.getCurrentTab() == 0) {
            final String price = et_price.getText().toString();
            final String remarks = et_remarks.getText().toString();
            final String name = (String) tv_name.getText();
            // Determine if the editview is empty
            if (TextUtils.isEmpty(et_price.getText().toString())) {
                Toast.makeText(ActivityAddCosts.this, "Please input your cost.", Toast.LENGTH_SHORT).show();
            } else {
                cv.put("date", date);
                cv.put("name", name);
                cv.put("price", price);
                cv.put("remark", remarks);
                dbwriter.insert("cost", null, cv);//save data to local storage
                dbwriter.close();

                //Put the data to the Cost object for the cloud
                Cost cost = new Cost();
                cost.setName(name);
                cost.setPrice(price);
                cost.setRemark(remarks);
                cost.setTime(date);

                //Upload the Cost to cloud
                Backendless.Persistence.save(cost, new AsyncCallback<Cost>() {

                    @Override
                    public void handleResponse(Cost cost) {
                        Log.d("AddCost", "Saved cost");
                        Toast.makeText(ActivityAddCosts.this, "saved cost", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void handleFault(BackendlessFault e) {
                        Log.d("AddCost", "Error: " + e.getMessage());
                        Toast.makeText(ActivityAddCosts.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                });
                // Finish Activity and go back to Finance fragment
                Intent intent = new Intent();
                intent.setClass(ActivityAddCosts.this, MainActivity.class);
                intent.putExtra("id", 1);
                startActivity(intent);
                overridePendingTransition(R.anim.pull_up_in, R.anim.pull_up_out);
                finish();
            }

        } else if (tabhost.getCurrentTab() == 1) { //determine if the add income tab
            final String price = et_income.getText().toString();
            final String remarks = et_remarks2.getText().toString();
            final String name = (String) tv_name2.getText();

            if (TextUtils.isEmpty(et_income.getText().toString())) {//determine if the editview is empty
                Toast.makeText(ActivityAddCosts.this, "Please input your amount.", Toast.LENGTH_SHORT).show();
            } else {
                cv.put("date", date);
                cv.put("name", name);
                cv.put("price", price);
                cv.put("remark", remarks);
                dbwriter.insert("income", null, cv);
                dbwriter.close();

                Intent intent = new Intent();
                intent.setClass(ActivityAddCosts.this, MainActivity.class);
                intent.putExtra("id", 1);
                startActivity(intent);
                overridePendingTransition(R.anim.pull_up_in, R.anim.pull_up_out);
                finish();
            }
        }
    }

    //Tab style
    private void updateTab(final TabHost tabHost)
    {
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++)
        {
            View view = tabHost.getTabWidget().getChildAt(i);
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(R.id.tab_lable);
            tv.setTextSize(20);
            //tv.setTypeface(Typeface.SERIF, 0);
            if (tabHost.getCurrentTab() == i)
            {
                //checked
                view.setBackground(getResources().getDrawable(R.color.background_basic));
                tv.setTextColor(this.getResources().getColorStateList(R.color.colorPrimary));

            }
            else
            {
                //unchecked
                view.setBackground(getResources().getDrawable(R.color.colorPrimary));
                tv.setTextColor(this.getResources().getColorStateList(android.R.color.white));
            }
        }
    }

    //Tab change listener
    private class tabChangedListener implements TabHost.OnTabChangeListener {

        @Override
        public void onTabChanged(String tabId)
        {
            tabhost.setCurrentTabByTag(tabId);
            updateTab(tabhost);
        }
    }

    //Cancel button click listener
     @OnClick(R.id.btn_cancel)
     void onClickCancel(View v) {
                Intent intent = new Intent();
                intent.setClass(ActivityAddCosts.this, MainActivity.class);
                intent.putExtra("id",1);
                startActivity(intent);
                overridePendingTransition(R.anim.pull_up_in, R.anim.pull_up_out);
                finish();
     }
}
