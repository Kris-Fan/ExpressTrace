package com.extrace.ui.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.extrace.net.OkHttpClientManager;
import com.extrace.ui.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.extrace.net.OkHttpClientManager.BASE_URL;
import static com.extrace.ui.service.LocationInfoShared.clearLatLng;
import static com.extrace.ui.service.LocationInfoShared.getLatLngInfo;
import static com.extrace.ui.service.LocationInfoShared.saveLatLngInfo;

public class MyService extends Service {
    private static final String TAG = "MyService";
    private LoginService loginService = new LoginService();
    private Runnable runnable;
    private Handler handler;
    private int Time = 1000*1*20;//周期时间1min，为了演示 降低为10s
    private int anHour =3000;// 毫秒
    private Timer timer = new Timer();
    private String url = BASE_URL +"/ExtraceSystem/collectPoint/";
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private List<LatLng> points = new ArrayList<>();
    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: ");
        super.onCreate();
        /**
         * 方式二：采用timer及TimerTask结合的方法
         */

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                String latStr = getLatLngInfo(getApplicationContext());
                double distance = 0.0; //坐标点距离
                if (!"".equals(latStr)){
                    Gson gson = new Gson();
                    List<LatLng> latLngList = gson.fromJson(latStr,new TypeToken<List<LatLng>>(){}.getType());
                    if (latLngList!= null && latLngList.size()>=2){
                        points.addAll(latLngList);
                        Log.e(TAG, "onCreate: "+points.toString());
                        distance = DistanceUtil.getDistance(points.get(0), points.get(points.size()-1));
                    }
                }
                double finalDistance = distance;

                Log.e(TAG, "run: 2min重复执行:distance:"+finalDistance+" 点坐标长度："+points.size());

                if (loginService.isLogined(getApplicationContext()) && finalDistance >=180.0 || points.size() >2000) {
                    Log.d(TAG, "run: 上传坐标："+latStr);
                    for (int i = 0; i< 3 && points.size()>4; i++) {//上传坐标前剔除 开始的3个可能的无效值
                        points.remove(i);
                    }
                    Gson gson = new Gson();
                    String jsonObject = gson.toJson(points);
                    saveLatLngInfo(getApplicationContext(),jsonObject);
                    latStr = getLatLngInfo(getApplicationContext());


                    Log.e(TAG, "run: 命中条件1-距离disatance"+finalDistance+"\t2-坐标点长度length："+points.size());
                    points.clear();
                    OkHttpClientManager okHttpClientManager = new OkHttpClientManager(new LoginService().userInfoSha256(getApplicationContext()));
                    okHttpClientManager.postAsyn(url + loginService.getUserId(getApplicationContext()), new OkHttpClientManager.ResultCallback<String>() {
                                @Override
                                public void onError(Request request, Exception e) {
                                    Log.d(TAG, "onError: 上传坐标信息失败");
                                    Toast.makeText(MyService.this, "啦啦啦快递：坐标上传失败！", Toast.LENGTH_SHORT).show();
                                    showNotification("包裹位置信息上传失败","原因：网络环境异常，2min后将重新尝试");
                                    Log.e(TAG, "onError: " + clearLatLng(getApplicationContext()));
                                    Log.d(TAG, "onError: 现在的LatLng值："+getLatLngInfo(getApplicationContext()));
                                }

                                @Override
                                public void onResponse(String response) {
                                    showNotification("定位服务开启中","位置持续定位中，更新成功，请保持");
                                    Log.d(TAG, "onResponse: 上传成功：清空本地坐标"+clearLatLng(getApplicationContext()));
                                    Log.d(TAG, "onResponse: 现在的LatLng值："+getLatLngInfo(getApplicationContext()));
                                }
                            }, new OkHttpClientManager.Param("latLng", latStr)
                    );
                }

            }

        };

        timer.schedule(timerTask,
                1000,//延迟1s秒执行
                Time);//周期时间


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        showNotification("定位服务开启","位置持续定位中，请保持");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }

    private NotificationManager manager;
    private Notification notification;

    /**
     * 位置上传失败时 显示提示信息
     */
    private void showNotification(String title,String text) {
        //ActivityCollector.finishAll();
        Log.d(TAG, "onClick: 显示通知");
        String id = "my_channel_01";
        String name=getResources().getString(R.string.app_name);    //渠道名字
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
           // Toast.makeText(getApplicationContext(), mChannel.toString(), Toast.LENGTH_SHORT).show();
            Log.i(TAG, mChannel.toString());
            manager.createNotificationChannel(mChannel);
            builder.setChannelId(id)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    //        .setSound(Uri.fromFile(new File("/system/media/audio/ringtones/Luna.ogg")))
                    //        .setVibrate(new long[]{0, 1000, 1000, 1000})
                    //        .setLights(Color.GREEN, 1000, 1000)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    //        .setStyle(new NotificationCompat.BigTextStyle().bigText("Learn how to build notifications, send and sync data, and use voice actions. Get the official Android IDE and developer tools to build apps for Android."))
                    .setAutoCancel(true)   //设置点击取消
            ;
            notification = builder.build();
        } else {

            builder.setContentTitle(title)
                    .setContentText(text)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    //        .setSound(Uri.fromFile(new File("/system/media/audio/ringtones/Luna.ogg")))
                    //        .setVibrate(new long[]{0, 1000, 1000, 1000})
                    //        .setLights(Color.GREEN, 1000, 1000)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    //        .setStyle(new NotificationCompat.BigTextStyle().bigText("Learn how to build notifications, send and sync data, and use voice actions. Get the official Android IDE and developer tools to build apps for Android."))
                    .setAutoCancel(true)   //设置点击取消
            ;
            notification = builder.build();
        }
        manager.notify(1, notification);

    }
}
