package com.software.anson.mydays.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Anson on 2017/2/19.
 */

public class Db extends SQLiteOpenHelper {

    public Db(Context context){
        super(context, "db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create event table in local storage
        db.execSQL("CREATE TABLE event("+
                "_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "date TEXT DEFAULT NONE,"+
                "time TEXT DEFAULT NONE,"+
                "note TEXT DEFAULT NONE,"+
                "lat DOUBLE DEFAULT NONE,"+
                "lng DOUBLE DEFAULT NONE,"+
                "address TEXT DEFAULT NONE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
