package com.software.anson.mydays.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.software.anson.mydays.R;
import com.software.anson.mydays.activity.ActivityAddEvents;
import com.software.anson.mydays.db.Db;
import com.software.anson.mydays.swipemenu.SwipeMenu;
import com.software.anson.mydays.swipemenu.SwipeMenuCreator;
import com.software.anson.mydays.swipemenu.SwipeMenuItem;
import com.software.anson.mydays.swipemenu.SwipeMenuListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Anson on 2017/2/11.
 * Swipemenu from http://www.jcodecraeer.com/a/opensource/2014/1108/1940.html
 */
public class FragmentEvents extends Fragment {
    private static final String TAG = "FragmentNote";
    private Context context;
    private SimpleCursorAdapter adapter;
    private Db db;
    private SQLiteDatabase dbreader;
    private SQLiteDatabase dbwriter;
    private TextView tv_detail_date, tv_detail_time, tv_detail_note, tv_detail_address;

    @BindView(R.id.listview)
    SwipeMenuListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_events, container, false);
        ButterKnife.bind(this, v);

        //Display on the event list
        db = new Db(getActivity());
        dbreader = db.getReadableDatabase();
        dbwriter = db.getWritableDatabase();
        adapter = new SimpleCursorAdapter(getActivity(),R.layout.item_event_cell, null,
                new String[]{"note","address","date","time"},
                new int[]{R.id.tv_note, R.id.tv_address, R.id.tv_date, R.id.tv_time});
        listView.setAdapter(adapter);
        refreshListView();


        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override

            public void create(SwipeMenu menu) {
                // create "modifyItem" item
                SwipeMenuItem modifyItem = new SwipeMenuItem(context);
                SwipeMenuItem deleteItem = new SwipeMenuItem(context);
                SwipeMenuItem accomplishItem = new SwipeMenuItem(context);
                // set item width
                modifyItem.setWidth(dp2px(90));
                deleteItem.setWidth(dp2px(90));
                accomplishItem.setWidth(dp2px(90));
                // set item title
                modifyItem.setBackground(new ColorDrawable(Color.rgb(0xf1, 0xc4, 0x0f)));
                modifyItem.setIcon(getResources().getDrawable(R.drawable.modify));
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xe7, 0x4c, 0x3c)));
                deleteItem.setIcon(getResources().getDrawable(R.drawable.delete));
                accomplishItem.setBackground(new ColorDrawable(Color.rgb(0x4c, 0x8b, 0x50)));
                accomplishItem.setTitle("DONE");
                // add to menu
                menu.addMenuItem(modifyItem);
                menu.addMenuItem(deleteItem);
                menu.addMenuItem(accomplishItem);
            }
        };

        listView.setMenuCreator(creator);

        //Set item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor c = adapter.getCursor();
                c.moveToPosition(i);

                String date = c.getString(c.getColumnIndex("date"));
                String time = c.getString(c.getColumnIndex("time"));
                String note = c.getString(c.getColumnIndex("note"));
                String address = c.getString(c.getColumnIndex("address"));

                // display details of items
                final View viewDialog = (View) getActivity().getLayoutInflater().inflate(R.layout.detail_dialog, null);

                tv_detail_date = (TextView) viewDialog.findViewById(R.id.tv_detail_date);
                tv_detail_time = (TextView) viewDialog.findViewById(R.id.tv_detail_time);
                tv_detail_note = (TextView) viewDialog.findViewById(R.id.tv_detail_note);
                tv_detail_address = (TextView) viewDialog.findViewById(R.id.tv_detail_address);

                tv_detail_date.setText(date);
                tv_detail_time.setText(time);
                tv_detail_note.setText(note);
                tv_detail_address.setText(address);

                new AlertDialog.Builder(getActivity())
                        .setTitle("Details")
                        .setView(viewDialog)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        //Menu Item click listener
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                Cursor c = adapter.getCursor();
                c.moveToPosition(position);
                final int itemId = c.getInt(c.getColumnIndex("_id"));

                switch (index) {
                    case 0://menu modify button
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Remind")
                                .setMessage("You will rewrite this item, and the former one will be removed.")
                                .setNegativeButton("Cancel", null)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        intent.setClass(getActivity(), ActivityAddEvents.class);
                                        startActivity(intent);
                                        getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
                                        dbwriter.delete("event", "_id=?",new String[]{itemId + ""});
                                        refreshListView();
                                    }
                                }).show();
                        break;
                    case 1://menu delete button
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Remind")
                                .setMessage("This item will be removed.")
                                .setNegativeButton("Cancel", null)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dbwriter.delete("event", "_id=?",new String[]{itemId + ""});
                                        refreshListView();
                                    }
                                }).show();
                        break;
                    case 2://menu finish button
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Remind")
                                .setMessage("Event finished?")
                                .setNegativeButton("Cancel", null)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dbwriter.delete("event", "_id=?",new String[]{itemId + ""});
                                        refreshListView();
                                        Toast.makeText(getActivity(),"Event Finished.", Toast.LENGTH_SHORT).show();
                                    }
                                }).show();
                        break;
                }
            }
        });

        listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {
            }
            @Override
            public void onSwipeEnd(int position) {
            }
        });
        return v;
    }
    //List refresh
    private void refreshListView(){
        Cursor c =dbreader.query("event", null, null, null, null, null, null);
        adapter.changeCursor(c);
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
    //Click go to Add Events Activity
    @OnClick(R.id.btn_add_events)
    void onAddClick(View v) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), ActivityAddEvents.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
    }

}
