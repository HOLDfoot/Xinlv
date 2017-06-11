package com.example.xinlv.net;

import android.app.Activity;
import android.os.Handler;
import android.widget.TextView;

import com.example.xinlv.Bean.NetBean;

/**
 * Created by user on 16-3-24.
 */
public class Connect {
    //TODO
    public static String port="8080";//27049
    public static String ip="192.168.43.170";
    public static String domain="holdfoot123.6655.la";

    public static void setPlan(String m, String n, String b,TextView tv) {
       MyAsyncTask task=new MyAsyncTask(m,n,b,tv);
        task.execute();
    }
    public static void commitBeats(NetBean bean,Handler handler){
        CommitTask commitTask=new CommitTask();
        commitTask.setHandler(handler);
        commitTask.execute(bean);
    }
    public static void getBeatsHistory(String m, String n,Activity activity){
        GetHistoryTask ghtask=new GetHistoryTask(m,n,activity);
        ghtask.execute();
    }


    public static void sop(Object obj) {
        System.out.println("-------------------------------------");
        System.out.println(obj);
    }
}
