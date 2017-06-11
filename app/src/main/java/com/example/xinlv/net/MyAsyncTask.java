package com.example.xinlv.net;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by foot on 2016/3/27.
 * getPlan
 */
public class MyAsyncTask extends AsyncTask<Void, Void, String> {
    private String mac = null, num = null, beats = null, time = null;

    private TextView tv_plan;

    public MyAsyncTask(String m, String n, String b, TextView tv) {
        super();

        this.mac = m;
        this.num = n;
        this.beats = b;
        this.tv_plan = tv;
    }

    @Override
    protected String doInBackground(Void... params) {
        String site = "http://"+Connect.ip+":"+Connect.port+"/XinlvServer/servlet/getplan?mac=" + mac + "&num=" + num + "&beats=" + beats;
        URL url = null;
        HttpURLConnection conn = null;
        StringBuffer sb = new StringBuffer();
        try {
            url = new URL(site);

            conn = (HttpURLConnection) url.openConnection();
            conn.connect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String decodestr = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));

            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            decodestr=URLDecoder.decode(sb.toString(), "UTF-8");
            reader.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return decodestr;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("web","result="+result);
        tv_plan.setText(result);//.replace("\\n", "\n"
        super.onPostExecute(result);
    }
}
