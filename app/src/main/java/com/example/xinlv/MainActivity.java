package com.example.xinlv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xinlv.Bean.BeatsInfo;
import com.example.xinlv.Bean.NetBean;
import com.example.xinlv.adapter.MyAdapter1;
import com.example.xinlv.adapter.MyAdapter2;
import com.example.xinlv.db.BeatsInfoDao;
import com.example.xinlv.net.Connect;
import com.example.xinlv.tools.ImageProcessing;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;


public class MainActivity extends Activity implements View.OnClickListener {

    private static final String Tag = "Tag";
    private static final String listTag = "listviewTag";


    /**
     * heart beats test
     */
    private boolean isTesting = false;
    private boolean isCancel = false;
    private Timer timer;

    private TimerTask task, task1;

    private static int gx;
    private static int j;

    private static double flag = 1;
    private Handler handler;

    private PowerManager pm;

    double addY;
    int[] hua = new int[]{9, 10, 11, 12, 13, 14, 13, 12, 11, 10, 9, 8, 7, 6, 7, 8, 9, 10, 11, 10, 10};
    int[] sum10 = new int[10];
    int avg = 0;
    int time;

    private static final AtomicBoolean processing = new AtomicBoolean(false);
    private static SurfaceView surfaceView = null;
    private static SurfaceHolder surfaceHolder = null;
    private static Camera camera = null;
    private static WakeLock wakeLock = null;
    private static int averageIndex = 0;
    private static final int averageArraySize = 4;
    private static final int[] averageArray = new int[averageArraySize];


    public enum TYPE {
        GREEN, RED
    }


    private static TYPE currentType = TYPE.GREEN;


    private static int beatsIndex = 0;
    private static final int beatsArraySize = 3;
    private static final int[] beatsArray = new int[beatsArraySize];
    private static double beats = 0;
    private static long startTime = 0;

    private int flushTimes = 0;

    private TextView text, tv_click, tv_save, tv_send;
    private ImageButton ib_startRun;


    private static BeatsInfoDao dao;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = Camera.open();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (isTesting) {
                    update();
                }
                if(msg.what==10){
                    Toast.makeText(MainActivity.this,"发送成功",Toast.LENGTH_SHORT).show();
                }
            }
        };

        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(surfaceCallback);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        text = (TextView) findViewById(R.id.text);
        tv_click = (TextView) findViewById(R.id.tv_click);

        initSaveView();
        setTimeClickListener();

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

        initBeatsHistory();
    }

    private void initBeatsHistory() {
        if(dao==null){
            dao=new BeatsInfoDao(this);
        }
        ListView lv2 = (ListView) findViewById(R.id.lv2);
        ArrayList<BeatsInfo> infoList=dao.getBeats();
        Log.d(Tag,"setAdapter");
        lv2.setAdapter(new MyAdapter2(MainActivity.this, infoList));

    }

    private void setTimeClickListener() {
        tv_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isTesting) {//如果已经在测试，就什么都不做
                    timer = new Timer();
                    // timer1 = new Timer();
                    isTesting = true;

                    Camera.Parameters params = camera.getParameters();
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(params);
                    startUiCounter();
                    Toast.makeText(MainActivity.this, "手指覆盖摄像头后请勿移动或按压", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initSaveView() {
        tv_save = (TextView) findViewById(R.id.tv_save);
        tv_send = (TextView) findViewById(R.id.tv_send);
        ib_startRun = (ImageButton) findViewById(R.id.tv_save_send);

        tv_save.setOnClickListener(this);
        ib_startRun.setOnClickListener(this);
        tv_send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_save:
                save();
                break;
            case R.id.tv_send:
                send();
                break;
            case R.id.tv_save_send:
                startRun();
                break;

        }
    }

    private void save() {
        Log.d(Tag, "save被调用");

        //avg,time
        if (dao == null) {
            dao = new BeatsInfoDao(MainActivity.this);
        }
        Log.d(Tag, "dao已经创建");

       /* ArrayList<BeatsInfo> listInfo = dao.getBeats();
        Log.d(Tag, "listinfo");
        int time_dao=0;
        if(!listInfo.isEmpty()) {

            time_dao = listInfo.get(0).getTime();
            if (time_dao == time) {//time_dao==time就是已经存过了
                Toast.makeText(MainActivity.this, "已经保存", Toast.LENGTH_SHORT).show();
                return;
            }
        }*/

        Log.d(Tag, "向数据库写数据开始");

        dao.insertBeats(new BeatsInfo(avg, time));
        Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
        hadSave=true;
        Log.d(Tag, "向数据库写数据结束");

    }
    private boolean hadSend=false;
    private boolean hadSave=false;
    private void send() {
        if(!hadSend) {
            Log.d("web","send");
            Log.d("web","avg="+avg);
            Connect.commitBeats(new NetBean(MenuActivity.mac, avg + "", time + "", null), handler);
           Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(MainActivity.this, "已经上传", Toast.LENGTH_SHORT).show();
        }
        hadSend=true;
    }

    private void startRun() {
        Intent runIntent=new Intent(MainActivity.this,LocationActivity.class);
        this.finish();
        startActivity(runIntent);
    }

    void startUiCounter() {
        flushTimes = 0;
        for (int i = 0; i < 10; i++) {
            sum10[i] = 0;
        }


        task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };

        timer.schedule(task, 1, 20);
        isCancel = false;
        // timer1.schedule(task1, 1, 1000);

    }

    /**
     * 2 cases:
     * already get 10 data
     * 120s over
     */
    void stopUiCounter() {
        //  timer1.cancel();
        timer.cancel();
        isCancel = true;
        isTesting = false;
        Camera.Parameters params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(params);
    }

    @Override
    public void onDestroy() {
        if (isCancel) {
            timer.cancel();
        }
        super.onDestroy();

    }


    private void update() {

        if (flag == 1)
            addY = 10;
        else {
            flag = 1;
            if (gx < 200) {
                if (hua[20] > 1) {
                    if (isTesting) {
                        // Log.d(Tag,"isTesting=true");
                        Toast.makeText(MainActivity.this, "请把手指完全覆盖摄像头", Toast.LENGTH_SHORT).show();
                    }
                    hua[20] = 0;
                }
                hua[20]++;
                return;
            } else
                hua[20] = 10;
            j = 0;

        }
        if (j < 20) {
            addY = hua[j];
            j++;
        }

    }

    private int getAvg(int[] sum) {
        int avg8;
        int small = sum[0], big = sum[0];
        int sum10 = 0;
        for (int i = 0; i < 10; i++) {
            if (sum[i] > big) {
                big = sum[i];
            }
            if (sum[i] < small) {
                small = sum[i];
            }
            sum10 = sum10 + sum[i];
        }
        int sum8 = sum10 - small - big;
        avg8 = (sum8 / 8);
        return avg8;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
        wakeLock.acquire();
        startTime = System.currentTimeMillis();

    }

    @Override
    public void onPause() {
        super.onPause();
        wakeLock.release();
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private PreviewCallback previewCallback = new PreviewCallback() {

        public void onPreviewFrame(byte[] data, Camera cam) {
            if (data == null)
                throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null)
                throw new NullPointerException();
            if (!processing.compareAndSet(false, true))
                return;
            int width = size.width;
            int height = size.height;
            int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), height, width);
            gx = imgAvg;
            if (imgAvg == 0 || imgAvg == 255) {
                processing.set(false);
                return;
            }

            int averageArrayAvg = 0;
            int averageArrayCnt = 0;
            for (int i = 0; i < averageArray.length; i++) {
                if (averageArray[i] > 0) {
                    averageArrayAvg += averageArray[i];
                    averageArrayCnt++;
                }
            }

            int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
            TYPE newType = currentType;
            if (imgAvg < rollingAverage) {
                newType = TYPE.RED;
                //从红变绿的时候增加心跳计数，由波峰到波谷计为一次心跳
                if (newType != currentType) {
                    beats++;
                    flag = 0;
                }
            } else if (imgAvg > rollingAverage) {
                newType = TYPE.GREEN;
            }

            if (averageIndex == averageArraySize)
                averageIndex = 0;
            averageArray[averageIndex] = imgAvg;
            averageIndex++;

            // Transitioned from one state to another to the same
            if (newType != currentType) {
                currentType = newType;
                //				image.postInvalidate();
            }
            long endTime = System.currentTimeMillis();
            double totalTimeInSecs = (endTime - startTime) / 1000d;
            if (totalTimeInSecs >= 2) {
                double bps = (beats / totalTimeInSecs);
                int dpm = (int) (bps * 60d);
                if (dpm < 30 || dpm > 160 || imgAvg < 200) {
                    startTime = System.currentTimeMillis();
                    beats = 0;
                    processing.set(false);
                    return;
                }
                //				Log.e(TAG, "totalTimeInSecs=" + totalTimeInSecs + " beats="+ beats);
                if (beatsIndex == beatsArraySize)
                    beatsIndex = 0;
                beatsArray[beatsIndex] = dpm;
                beatsIndex++;
                int beatsArrayAvg = 0;
                int beatsArrayCnt = 0;
                for (int i = 0; i < beatsArray.length; i++) {
                    if (beatsArray[i] > 0) {
                        beatsArrayAvg += beatsArray[i];
                        beatsArrayCnt++;
                    }
                }
                int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
                text.setText("即时心跳为 : " + String.valueOf(beatsAvg));
                if (isTesting) {
                    sum10[flushTimes] = beatsAvg;
                    flushTimes++;
                    reflushList1(flushTimes);//左侧一共有10个数据
                }
                if (flushTimes == 10) {
                    stopUiCounter();
                    //TODO
                    avg = getAvg(sum10);
                    time = (int) (System.currentTimeMillis() / 1000);
                    tv_click.setText("" + avg);
                    if(avg<60||avg>100){
                        Toast.makeText(MainActivity.this,"您的心率不在正常范围内，请重测或到医院检查！",Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(MainActivity.this,"心率降到60可以活到90岁哦，点击正下方开始运动哦！",Toast.LENGTH_LONG).show();
                    hadSend=false;
                    hadSave=false;
                }
                Log.d(Tag, "Output times:" + flushTimes);
                startTime = System.currentTimeMillis();
                beats = 0;
            }
            processing.set(false);
        }


    };

    private void reflushList1(int length) {
        ListView lv1 = (ListView) findViewById(R.id.lv1);
        lv1.setAdapter(new MyAdapter1(this, sum10, length));
    }

    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(surfaceHolder);//Notice here
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
                //				Log.e("PreviewDemo-surfaceCallback","Exception in setPreviewDisplay()", t);
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            Camera.Parameters parameters = camera.getParameters();
            //parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                //				Log.d(TAG, "Using width=" + size.width + " height="	+ size.height);
            }
            camera.setParameters(parameters);
            camera.setDisplayOrientation(90);
            camera.startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // Ignore
        }
    };

    private static Camera.Size getSmallestPreviewSize(int width, int height,
                                                      Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea < resultArea)
                        result = size;
                }
            }
        }
        return result;
    }


}