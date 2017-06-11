package com.example.xinlv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LocationActivity extends Activity implements AMap.OnMapScreenShotListener {

    public static final String startAction = "com.holdfoot.runningmeasure.start";

    private MapView mapView;
    public static AMap aMap;
    private TextView tv_time, tv_meters;

    private ImageView ib_status, ib_end;
    private int isRun = 0;//0未启动或结束，1启动，2暂停
    private Handler handler;

    private List<LatLng> lats = new ArrayList<LatLng>();
    private LatLng curLatLng;
    private float kmeters;
    private float mtime;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();

        init();
        initView();
        initIcon();
        initService();
    }

    private void initService() {
        Intent start = new Intent();
        start.setAction(startAction);
        start.setComponent(new ComponentName("com.example.xinlv", "com.example.xinlv.RunningService"));
        Log.d("runs", "bind start before bind");
        bindService(start, connection, Context.BIND_AUTO_CREATE);
    }

    private void initIcon() {
        //根据Service的状态初始化
        if (isRun == 0) {
            ib_status.setImageDrawable(getResources().getDrawable(R.drawable.start));
        } else if (isRun == 1) {
            ib_status.setImageDrawable(getResources().getDrawable(R.drawable.stop));
        } else if (isRun == 2) {
            ib_status.setImageDrawable(getResources().getDrawable(R.drawable.conti));
        }
    }

    private void init() {

        initTask();
        initHandler();
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0://绘制路线
                        refreshText();
                        drawPolyLine();
                        break;
                    case 1:
                        addMarkersToMap();
                        break;

                }

                super.handleMessage(msg);
            }
        };
    }

    private void refreshText() {
        refreshTimeMeters();
        int miao= (int) (mtime*10%10)*6;
        int fen= (int) mtime;
        tv_time.setText("时间" + "\n" + fen+":"+miao);
        tv_meters.setText("距离" + "\n" + kmeters);
    }

    private void refreshTimeMeters() {
        if (startTime == 0) return;
        kmeters = getMeters();
        mtime = ((float) ((System.currentTimeMillis() - startTime) / 6000))/10;
    }

    private void drawPolyLine() {

        lats = RunningService.latLngs;
        aMap.addPolyline((new PolylineOptions())
                .addAll(lats)
                .geodesic(true).color(Color.RED));
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap() {
        // 文字显示标注，可以设置显示内容，位置，字体大小颜色，背景色旋转角度
        String text = "" + kmeters;
        lats = RunningService.latLngs;
        if (lats.size() == 0) {
            Toast.makeText(LocationActivity.this, "您还未运动哦", Toast.LENGTH_SHORT).show();
            return;
        }

        curLatLng = lats.get(lats.size() - 1);//outof index
        TextOptions textOptions = new TextOptions()
                .position(curLatLng)
                .text(text)
                .fontColor(Color.RED)
                .backgroundColor(Color.GRAY)
                .fontSize(40)
                .rotate(20)
                .align(Text.ALIGN_CENTER_HORIZONTAL, Text.ALIGN_CENTER_VERTICAL)
                .zIndex(1.f).typeface(Typeface.DEFAULT_BOLD);
        aMap.addText(textOptions);

    }

    private void shotScreen() {
        aMap.getMapScreenShot(this);
    }

    private float getMeters() {
        lats = RunningService.latLngs;
        float sum = 0, dis = 0;
        LatLng startLatlng, endLatlng;
        for (int i = 0; i < lats.size() - 1; i++) {
            startLatlng = lats.get(i);
            endLatlng = lats.get(i + 1);
            dis = AMapUtils.calculateLineDistance(startLatlng, endLatlng);
            sum += dis;
        }
        float s = ((int) sum) / 100;
        return s / 10;
    }


    private void initTask() {
        if (timer != null) return;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (isRun == 0||isRun==2) return;
                handler.sendEmptyMessage(0);//用来更新UI
            }
        };
    }

    private void start() {
        initTask();
        //初始化地图
        aMap.clear();

        runningService.getHandler().sendEmptyMessage(3);
        ib_status.setImageDrawable(getResources().getDrawable(R.drawable.stop));
        startTime = System.currentTimeMillis();
        isRun = 1;
    }

    private void stop() {
        Log.d("runs", "Activity to stop service");
        runningService.getHandler().sendEmptyMessage(0);
        addMarkersToMap();
        ib_status.setImageDrawable(getResources().getDrawable(R.drawable.conti));
        isRun = 2;
    }

    private void restart() {
        aMap.clear();
        Log.d("runs", "Activity to restart");
        runningService.getHandler().sendEmptyMessage(1);
        ib_status.setImageDrawable(getResources().getDrawable(R.drawable.stop));
        isRun = 1;
    }

    private void endRun() {
        /**
         * 获取到latlngs，停止计数
         * 划线，
         * 求总里程，写到地图上
         * 弹出提示框提示跑步里程，判断是否截图
         */
        if (isRun == 0) return;
        if (isRun == 1) {//正在运行就暂停
            stop();
        }
        if (isRun == 2) {//如果暂停就绘图并提示是否截图
            addMarkersToMap();
            final AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this)
                    .setTitle("您的运动详情")
                    .setMessage("运动时间：" + mtime + "\n运动距离：" + kmeters)
                    .setPositiveButton("截图", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //截图
                            shotScreen();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            aMap.clear();
                            tv_time.setText("时间" + "\n" + 0.0);
                            tv_meters.setText("距离" + "\n" + 0.0);
                        }
                    });
            builder.create().show();
        }
        isRun = 0;
//        aMap.clear();
        initIcon();
    }

    private void initView() {

        tv_meters = (TextView) findViewById(R.id.tv_meters);
        tv_time = (TextView) findViewById(R.id.tv_time);

        ib_status = (ImageView) findViewById(R.id.ib_status);
        ib_end = (ImageView) findViewById(R.id.ib_end);
        ib_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果未开始就开始，如果正在运行就暂停，如果暂停就继续
                if (isRun == 0) {
                    start();
                } else if (isRun == 1) {
                    stop();
                } else if (isRun == 2) {
                    restart();
                }
            }
        });
        ib_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endRun();
            }
        });
    }


    private TimerTask timerTask;
    private Timer timer;

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        /**
         * 如果跑步就画图，显示里程
         */
        initTask();
        timer.schedule(timerTask, 0, 4000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        timer.cancel();
        timer = null;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

    }

    private RunningService runningService;

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            runningService = ((RunningService.MyBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            runningService = null;
        }
    };

    @Override
    public void onMapScreenShot(Bitmap bitmap) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        if (null == bitmap) {
            return;
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/RunningMap");
        if (!file.exists()) {
            file.mkdir();
        }
        Log.d("runs", "保存图片");
        try {
            FileOutputStream fos = new FileOutputStream(
                    Environment.getExternalStorageDirectory() + "/RunningMap" + "/test_"
                            + sdf.format(new Date()) + ".png");
            boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            try {
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuffer buffer = new StringBuffer();
            if (b)
                buffer.append("截屏成功 ");
            else {
                buffer.append("截屏失败 ");
            }
            Toast.makeText(LocationActivity.this, buffer, Toast.LENGTH_SHORT).show();
            aMap.clear();
            tv_time.setText("时间" + "\n" + 0.0);
            tv_meters.setText("距离" + "\n" + 0.0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapScreenShot(Bitmap bitmap, int i) {

    }

}
