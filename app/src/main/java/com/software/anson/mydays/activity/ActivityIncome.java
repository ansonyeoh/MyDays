package com.software.anson.mydays.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.TypedValue;
import android.view.View;

import com.software.anson.mydays.R;
import com.software.anson.mydays.db.DbCosts;
import com.software.anson.mydays.swipemenu.SwipeMenu;
import com.software.anson.mydays.swipemenu.SwipeMenuCreator;
import com.software.anson.mydays.swipemenu.SwipeMenuItem;
import com.software.anson.mydays.swipemenu.SwipeMenuListView;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Anson on 2017/2/26.
 * Swipemenu from http://www.jcodecraeer.com/a/opensource/2014/1108/1940.html
 */


public class ActivityIncome extends Activity{
    private SwipeMenuListView lv_income;
    private DbCosts dbCosts;
    private SQLiteDatabase dbreader, dbwriter;
    private SimpleCursorAdapter adapter;
    private Context context;

    @OnClick(R.id.back2)
    void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(ActivityIncome.this, MainActivity.class);
        intent.putExtra("id",1);
        startActivity(intent);
        overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);
        ButterKnife.bind(this);

        lv_income = (SwipeMenuListView) findViewById(R.id.lv_income);

        //Read Income data from local storage
        dbCosts = new DbCosts(this);
        dbwriter = dbCosts.getWritableDatabase();
        dbreader = dbCosts.getReadableDatabase();
        adapter = new SimpleCursorAdapter(this,R.layout.item_cost_cell, null,
                new String[]{"date","name","price","remark"},
                new int[]{R.id.item_date, R.id.item_name, R.id.item_amount, R.id.item_remarks});
        lv_income.setAdapter(adapter);
        refreshListView();

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override

            public void create(SwipeMenu menu) {
                // create "modifyItem" item
                SwipeMenuItem modifyItem = new SwipeMenuItem(context);
                SwipeMenuItem deleteItem = new SwipeMenuItem(context);
                // set item width
                modifyItem.setWidth(dp2px(90));
                deleteItem.setWidth(dp2px(90));
                // set item title
                modifyItem.setBackground(new ColorDrawable(Color.rgb(0xf1, 0xc4,
                        0x0f)));
                modifyItem.setIcon(getResources().getDrawable(R.drawable.modify));
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xe7, 0x4c,
                        0x3c)));
                deleteItem.setIcon(getResources().getDrawable(R.drawable.delete));
                // add to menu
                menu.addMenuItem(modifyItem);
                menu.addMenuItem(deleteItem);

            }
        };
        // set creator
        lv_income.setMenuCreator(creator);

        // step 2. listener item click event
        lv_income.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                Cursor c = adapter.getCursor();
                c.moveToPosition(position);
                final int itemId = c.getInt(c.getColumnIndex("_id"));

                switch (index) {
                    case 0:
                        new AlertDialog.Builder(ActivityIncome.this)
                                .setTitle("Remind")
                                .setMessage("You will rewrite this item, and the former one will be removed.")
                                .setNegativeButton("Cancel", null)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        intent.setClass(ActivityIncome.this, ActivityAddCosts.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
                                        dbwriter.delete("income", "_id=?",new String[]{itemId + ""});
                                        refreshListView();
                                    }
                                }).show();
                        break;
                    case 1:
                        new AlertDialog.Builder(ActivityIncome.this)
                                .setTitle("Remind")
                                .setMessage("This item will be removed.")
                                .setNegativeButton("Cancel", null)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dbwriter.delete("income", "_id=?",new String[]{itemId + ""});
                                        refreshListView();
                                    }
                                }).show();
                        break;
                }
            }
        });

        // set SwipeListener
        lv_income.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });
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

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    //Refresh List
    private void refreshListView(){
        Cursor c = dbreader.query("income", null, null, null, null, null, null);
        adapter.changeCursor(c);
    }
}
