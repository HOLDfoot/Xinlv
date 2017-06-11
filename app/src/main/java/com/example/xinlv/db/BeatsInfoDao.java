package com.example.xinlv.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.xinlv.Bean.BeatsInfo;

import java.util.ArrayList;

/**
 * Created by user on 16-3-22.
 */
public class BeatsInfoDao {
    private DBHelper helper = null;
    private SQLiteDatabase db;
    private static ArrayList<BeatsInfo> infoList;
    private static ContentValues cv;
    public static String TABLE_NAME = "beats_info";


    public BeatsInfoDao(Context context) {
        helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }

    public ArrayList<BeatsInfo> getBeats() {
        infoList = new ArrayList<BeatsInfo>();
        Cursor cursor = db.query(TABLE_NAME, new String[]{"beats", "time"}, null, null, null, null,"time desc");
        int beats, time;
        BeatsInfo info;
        int n=0;
        while (cursor.moveToNext()) {
            beats = cursor.getInt(cursor.getColumnIndex("beats"));
            time = cursor.getInt(cursor.getColumnIndex("time"));
            info = new BeatsInfo(beats, time);
            infoList.add(info);
            n++;
            if(n==10){
                break;
            }
        }
        return infoList;
    }

    public void insertBeats(BeatsInfo info) {
        cv = new ContentValues();
        cv.put("beats", info.getBeats());
        cv.put("time", info.getTime());
        db.insert(TABLE_NAME,null,cv );//  null如果cv为null的时候插入的列，在该列为null
        cv.clear();
    }


}
