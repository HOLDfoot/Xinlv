package com.example.xinlv.net;

import android.os.AsyncTask;
import android.os.Handler;

import com.example.xinlv.Bean.NetBean;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by foot on 2016/3/27.
 * put Beats
 */
public class CommitTask extends AsyncTask<NetBean,Void,Void> {

    private Handler handler;
    public void setHandler(Handler handler){
        this.handler=handler;
    }
    @Override
    protected Void doInBackground(NetBean... params) {
        putBeats(params[0].getMac(),params[0].getBeats(),params[0].getTime());
        return null;
    }
    private  void putBeats(String mac,String beats,String time){
        String putUrl = "http://"+Connect.ip+":"+Connect.port+"/XinlvServer/servlet/putbeats";
        try {
            URL url = new URL(putUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
            conn.setUseCaches(false);

            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            conn.connect();

            DataOutputStream out = new DataOutputStream(conn.getOutputStream());

            // TODO
            String send = "mac="+mac+"&beats="+beats+"&time="+time;
            out.write(send.getBytes());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            ;
            StringBuffer sb = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
           /* if(sb.toString().equals("putok")){
                handler.sendEmptyMessage(10);
            }*/
            reader.close();
            out.close();
            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onPostExecute(Void s) {
        super.onPostExecute(s);
    }
}
