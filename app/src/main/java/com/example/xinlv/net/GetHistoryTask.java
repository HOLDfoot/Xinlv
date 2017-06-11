package com.example.xinlv.net;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.xinlv.Bean.ChartBean;
import com.example.xinlv.ChartActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by foot on 2016/4/3.
 */
public class GetHistoryTask extends AsyncTask<Void, Void, String> {

    private String mac = null, num = null;
    private Activity activity = null;

    /**
     * @param m mac作为唯一标识
     * @param n num作为周1，月2
     * @serialData
     */
    public GetHistoryTask(String m, String n, Activity activity) {
        super();
        this.activity = activity;
        this.mac = m;
        this.num = n;
    }

    @Override
    protected String doInBackground(Void... params) {
        String site = "http://"+Connect.ip+":"+Connect.port+"/XinlvServer/servlet/gethis?mac=" + mac + "&num=" + num;
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
            decodestr = URLDecoder.decode(sb.toString(), "UTF-8");

            reader.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return decodestr;
    }

    @Override
    protected void onPostExecute(String result) {

        /*JSONArray array= JSONArray.fromObject(result);
        List<ChartBean> chartBeans=new ArrayList<ChartBean>();
        ChartBean bean=null;
        JSONObject jsonObject;
        for(int i=0;i<array.size();i++){
            jsonObject=array.getJSONObject(i);
            bean=new ChartBean(jsonObject.getInt("time")+"",jsonObject.getInt("beats"));
            chartBeans.add(bean);
        }*/
        List<ChartBean> chartBeans=new ArrayList<ChartBean>();
        try {
            Log.d("chart","result="+result);
            if(result==null) {
                Toast.makeText(activity, "无法获取数据，请检查网络", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONArray array=new JSONArray(result);
           // Log.d("chart","Jsonarray:"+"array.length()"+array.length()+array.toString());
            ChartBean bean=null;
            JSONObject jsonObject;
            for(int i=0;i<array.length();i++){
                jsonObject=array.getJSONObject(i);
                Log.d("chart","time:"+jsonObject.getInt("time")+" beats:"+jsonObject.getInt("beats"));
                bean=new ChartBean(jsonObject.getInt("time")+"",jsonObject.getInt("beats"));
                chartBeans.add(bean);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //atomicBoolean.compareAndSet(false,true);
        ((ChartActivity) (activity)).initChart(chartBeans, num);
       // atomicBoolean.set(true);
        super.onPostExecute(result);
    }
    private static AtomicBoolean atomicBoolean=new AtomicBoolean(false);
}
