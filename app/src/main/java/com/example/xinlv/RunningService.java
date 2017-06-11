package com.example.xinlv;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class RunningService extends Service implements LocationSource, AMapLocationListener {

    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;

    public static final String tag = "runs", backlatlng = "backlatlng";
    public static List<LatLng> latLngs = new ArrayList<LatLng>();
    private Handler handler;
    private boolean isRun = false;

    private AMap aMap = LocationActivity.aMap;

    public RunningService() {
    }

    public List<LatLng> getLatLngs() {
        return latLngs;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initHandler();
        Log.d(tag, "service oncreate");
        //start();
        //设置定位监听
        aMap.setLocationSource(this);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);
        //设置定位的类型为定位模式
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);//设置定位的类型为根据地图面向方向旋转
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.d("runs", "service handler");
                switch (msg.what) {
                    case 3:
                        start();
                        break;
                    case 0://暂停
                        stop();
                        break;
                    case 1://继续
                        restart();
                        break;
                    case 2:
                        stopSelf();
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    public Handler getHandler() {
        return handler;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //start();
        Log.d(tag, "service onStartCommand");
        //onStartCommand里面只能打log，其他都不行，比如发送广播，比如写其他函数
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        Log.d(tag, "service onStartCommand");
        return new MyBinder();
    }

    private void start() {
        latLngs = new ArrayList<LatLng>();
        Log.d(backlatlng, "service start");
        latLngs.clear();
        isRun = true;

        latLng0 = null;
        latLng1 = null;
        latLng2 = null;
        latLng_last = null;
    }

    private void stop() {
        isRun = false;
    }

    private void restart() {
        isRun = true;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
    }


    /**
     * 激活定位
     *
     * @param onLocationChangedListener
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            /**
             * 此方法为每隔固定的时间发起一次定位请求，为了减少电量消耗或网络流量消耗，
             * 注意设置合适的定位时间间隔（最小间隔支持2000ms），并且在合适的时间调用stopLocation()方法来取消定位请求
             * 在定位结束后，在合适的什么周期调用onDestroy()方法
             * 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
             */
            mLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                //  mLocationErrText.setVisibility(View.GONE);
                mListener.onLocationChanged(aMapLocation);//显示系统小蓝点
                if (isRun) {
                    LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                   // if (isRight(latLng)) {
                        latLngs.add(latLng);
                        Log.d(backlatlng, "latlng" + latLng.toString());
                   // }
                }
            } else {
                String errText = "定位失败，" + aMapLocation.getErrorCode() + ":" + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
                // mLocationErrText.setText(errText);
            }
        }
    }

    private boolean isRight(LatLng latLng) {
        boolean b = false;
        if (setAllLatlng(latLng)) {
            return false;//这个坐标因为初始坐标不能用于参考
        } else {
            //setLastLatLng();
            //根据latLng_last判断下一个数据是否有效
           float dis= AMapUtils.calculateLineDistance(latLng_last, latLng);
            if (dis>Meter_60){//最多支持最快的人（10m/s）6秒3次都定位不准
                return false;
            }else {
                latLng_last=latLng;
            }
        }
        b=true;
        return b;
    }

    private boolean setAllLatlng(LatLng latLng) {
        if (latLng0 == null) {
            latLng0 = latLng;
            return true;//如果有null并设置了就返回设置成功
        } else if (latLng1 == null) {
            latLng1 = latLng;
            return true;
        } else if (latLng2 == null) {
            latLng2 = latLng;//收集好3个坐标就确定参考坐标
            setLastLatLng();
            return true;
        } else {
            return false;//表示都已经设置了数据，不需要设置数据
        }
    }

    private void setLastLatLng() {
        float d0 = AMapUtils.calculateLineDistance(latLng0, latLng1);
        float d1 = AMapUtils.calculateLineDistance(latLng1, latLng2);
        float d2 = AMapUtils.calculateLineDistance(latLng2, latLng0);
        //假定3个点有一个是准确的，然后距离最近的2个点选择编号较大的
        float d;
        if (d1 <= d2) {
            if (d1 <= d0) {//d1最小
                latLng_last = latLng2;
            } else {//d0最小
                latLng_last = latLng1;
            }
        } else {
            if (d1 <= d0) {//d0最小
                latLng_last = latLng1;
            } else {
                if(d0<=d2){//d0最小
                    latLng_last = latLng1;
                }else{//d2最小
                    latLng_last=latLng2;
                }
            }
        }
    }

    private LatLng latLng0 = null;
    private LatLng latLng1 = null;
    private LatLng latLng2 = null;
    private LatLng latLng_last = null;
    private static final float Meter_60=40;//没人能跑这么远
    private static final float Meter_40=40;//跑的快的人2*2秒能跑40米
    private static final float Meter_20=40;//跑的慢的人2*2秒能跑20米

    public class MyBinder extends Binder {
        public RunningService getService() {
            return RunningService.this;
        }
    }
}
