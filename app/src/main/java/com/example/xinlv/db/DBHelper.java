package com.example.xinlv.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 16-3-22.
 */
public class DBHelper extends SQLiteOpenHelper{

    public static String DATABASE_NAME="xinlv";
    public static int   DATABASE_VERSION=1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null,DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table beats_info(_id INTEGER PRIMARY KEY AUTOINCREMENT,beats INTEGER,time INTEGER)";
        db.execSQL(sql);
        int time= (int) (System.currentTimeMillis()/1000);
        int value=68;//70到88
        for(int i=0;i<10;i++) {
            value=value+2;
            time=time+60*i;//每次多加一分钟
            String inset_sql = "insert into beats_info(beats,time) values("+value+"," + time + ")";
            db.execSQL(inset_sql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
