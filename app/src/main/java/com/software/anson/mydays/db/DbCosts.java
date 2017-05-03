package com.software.anson.mydays.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Anson on 2017/2/19.
 */

public class DbCosts extends SQLiteOpenHelper {

    public DbCosts(Context context){
        super(context, "db_cost", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create cost and income table in local storage
        db.execSQL("CREATE TABLE IF NOT EXISTS cost("+
                "_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "date TEXT DEFAULT NONE,"+
                "name TEXT DEFAULT NONE,"+
                "price TEXT DEFAULT NONE,"+
                "remark TEXT DEFAULT NONE)");

        db.execSQL("CREATE TABLE IF NOT EXISTS income("+
                "_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "date TEXT DEFAULT NONE,"+
                "name TEXT DEFAULT NONE,"+
                "price TEXT DEFAULT NONE,"+
                "remark TEXT DEFAULT NONE)");
    }

    //If there is a change, then update
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //If table exist, then ignore
        db.execSQL("DROP TABLE IF EXISTS cost");
        db.execSQL("DROP TABLE IF EXISTS income");
        onCreate(db);
    }
}
