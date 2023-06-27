package com.dfrobot.angelo.blunobasicdemo;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "db_ble_601CH";
    private static final int DB_VERSION = 1;

    DatabaseHelper(Context context){
        super(context, DB_NAME, null,  DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db){

        final String INIT_TABLE="create table table_ble_601CH (_id integer primary key autoincrement, P0 char, P1 char, P2 char, P3 char, P4 char, P5 char, P6 char, P7 char, P8 char)";
        db.execSQL(INIT_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){}
}
