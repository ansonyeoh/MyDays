package com.software.anson.mydays.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.software.anson.mydays.activity.ActivityAddCosts;
import com.software.anson.mydays.activity.ActivityIncome;
import com.software.anson.mydays.model.Cost;
import com.software.anson.mydays.db.DbCosts;
import com.software.anson.mydays.R;
import com.software.anson.mydays.swipemenu.SwipeMenu;
import com.software.anson.mydays.swipemenu.SwipeMenuCreator;
import com.software.anson.mydays.swipemenu.SwipeMenuItem;
import com.software.anson.mydays.swipemenu.SwipeMenuListView;

/**
 * Created by Anson on 2017/2/11.
 * Swipemenu from http://www.jcodecraeer.com/a/opensource/2014/1108/1940.html
 */
public class FragmentCosts extends Fragment {
    private static final String TAG = "FragmentCosts";
    private Button btn_add_cost;
    private TextView tv_income, tv_budget, tv_month_cost;
    private EditText et_budget;
    private String num;
    private SwipeMenuListView listView;
    private DbCosts dbCosts;
    private SQLiteDatabase dbreader, dbwriter;
    private SimpleCursorAdapter adapter;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_costs, container, false);

        dbCosts = new DbCosts(getActivity());
        dbwriter = dbCosts.getWritableDatabase();
        dbreader = dbCosts.getReadableDatabase();

        tv_income = (TextView) v.findViewById(R.id.tv_income);
        tv_budget = (TextView) v.findViewById(R.id.tv_budget);
        tv_month_cost = (TextView) v.findViewById(R.id.tv_month_cost);


        //Load items
        listView = (SwipeMenuListView) v.findViewById(R.id.listview2);
        adapter = new SimpleCursorAdapter(getActivity(), R.layout.item_cost_cell, null,
                new String[]{"date", "name", "price", "remark"},
                new int[]{R.id.item_date, R.id.item_name, R.id.item_amount, R.id.item_remarks});
        listView.setAdapter(adapter);
        refreshListView();


        //Load the budget
        SharedPreferences mShared = getActivity().getSharedPreferences("budget", Activity.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mShared.edit();
        num = mShared.getString("amount", "0");
        tv_budget.setText(num);//display budget


        // Go to ActivityAddCosts
        btn_add_cost = (Button) v.findViewById(R.id.btn_add_cost);
        btn_add_cost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ActivityAddCosts.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.pull_down_in, R.anim.pull_down_out);
            }
        });


        //Click the TextView, pop up dialog and set up budget
        tv_budget.setClickable(true);
        tv_budget.setFocusable(true);
        tv_budget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final View viewDialog = (View) getActivity().getLayoutInflater().inflate(R.layout.input_dialog, null);

                // Set up budget
                new AlertDialog.Builder(getActivity())
                        .setTitle("Budget")
                        .setView(viewDialog)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText et_budget = (EditText) viewDialog.findViewById(R.id.et_budget);
                                String budget = et_budget.getText().toString();

                                editor.putString("amount", budget);
                                editor.commit();
                                tv_budget.setText(budget);
                                Log.i(TAG, budget);
                                Toast.makeText(getActivity(), "Set up budget successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();//turn off dialog
                    }
                }).show();
            }
        });

        tv_income.setClickable(true);
        tv_income.setFocusable(true);
        tv_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ActivityIncome.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);

            }
        });

        // step 1. set menu
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
        listView.setMenuCreator(creator);

        // step 2. listener item click event
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                Cursor c = adapter.getCursor();
                c.moveToPosition(position);
                final int itemId = c.getInt(c.getColumnIndex("_id"));
                final String name = c.getString(c.getColumnIndex("name"));
                final String price = c.getString(c.getColumnIndex("price"));
                final String remark = c.getString(c.getColumnIndex("remark"));
                final String time = c.getString(c.getColumnIndex("date"));

                switch (index) {
                    case 0:
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Remind")
                                .setMessage("You will rewrite this item, and the former one will be removed.")
                                .setNegativeButton("Cancel", null)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        intent.setClass(getActivity(), ActivityAddCosts.class);
                                        startActivity(intent);
                                        getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
                                        dbwriter.delete("cost", "_id=?", new String[]{itemId + ""});
                                        refreshListView();
                                    }
                                }).show();
                        break;
                    case 1:
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Remind")
                                .setMessage("This item will be removed.")
                                .setNegativeButton("Cancel", null)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dbwriter.delete("cost", "_id=?", new String[]{itemId + ""});
                                        refreshListView();
                                        //deleteCost(name, price, remark, time);
                                    }
                                }).show();
                        break;
                }
            }
        });

        // set SwipeListener
        listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });

        return v;
    }
    //Delete item on cloud
    public void deleteCost(String name, String price, String remark, String time)
    {
        // make sure to put the initApp call somewhere early on
        // in the app - main activity is the best place
        Backendless.initApp(getActivity(), "816605BB-C858-510E-FF58-19AAD0ADFE00", "77B405BF-FCE5-31B9-FF2D-1B0F4737DC00", "v1");
        // save a new object first, so there is something to delete.
        final Cost cost = new Cost();
        cost.setName(name);
        cost.setPrice(price);
        cost.setRemark(remark);
        cost.setTime(time);

        // now delete the saved object
        Long result = Backendless.Persistence.of( Cost.class ).remove( cost );
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
    private void refreshListView() {
        Cursor c = dbreader.query("cost", null, null, null, null, null, null);
        adapter.changeCursor(c);
        //Get the sum of cost
        Cursor cursor = dbreader.rawQuery("select sum(price) from cost", null);
        while (cursor.moveToNext()) {
            String sum = cursor.getString(0);
            // Log.i(TAG, sum);
            if (sum == null) {
                tv_month_cost.setText("0");
            } else {
                tv_month_cost.setText(sum);
            }
        }
        //Get the sum of income
        Cursor cursor2 = dbreader.rawQuery("select sum(price) from income", null);
        while (cursor2.moveToNext()) {
            String sum2 = cursor2.getString(0);
            if (sum2 == null) {
                tv_income.setText("0");
            } else {
                tv_income.setText(sum2);
            }
        }
    }
}
