package com.extrace.ui.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.extrace.ui.R;
import com.extrace.util.TitleLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static com.extrace.ui.service.LocationInfoShared.getLatLngInfo;
import static com.extrace.ui.service.LocationInfoShared.saveLatLngInfo;

public class MyLocationActivity extends AppCompatActivity implements View.OnClickListener {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    public LocationClient mLocationClient;
    public BDAbstractLocationListener myListener = new MyLocationListener();
    private TextView bt,showAddr;
    private TextView button;
    private TextView buttons;
    private LatLng latLng;
    private boolean isFirstLoc = true; // 是否首次定位
    private static final String TAG = "MyLocationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_my_location);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){ //隐藏自带的标题栏
            actionBar.hide();
        }
        TitleLayout titleLayout ;
        titleLayout = findViewById(R.id.title);
        titleLayout.setTitle(getResources().getString(R.string.action_my_location));
        titleLayout.hideTitleEdit();

        initView();
        String latStr = getLatLngInfo(getApplicationContext());
        if (!"".equals(latStr)){
            Gson gson = new Gson();
            List<LatLng> latLngList = gson.fromJson(latStr,new TypeToken<List<LatLng>>(){}.getType());
            if (latLngList != null){
                points.addAll(latLngList);
                Log.e(TAG, "onCreate: "+points.toString());
            }
        }
        initMap();
        if (ContextCompat.checkSelfPermission(MyLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MyLocationActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else {
            requesrLocation();
        }
    }


    private void initMap() {
        //获取地图控件引用
        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);

        //默认显示普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开启交通图
        //mBaiduMap.setTrafficEnabled(true);
        //开启热力图
        //mBaiduMap.setBaiduHeatMapEnabled(true);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        //配置定位SDK参数

    }
    private void requesrLocation(){
        initLocation();
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        //开启定位
        mLocationClient.start();
        //图片点击事件，回到定位点
        mLocationClient.requestLocation();
    }
    //配置定位SDK参数
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
        // .getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);
        option.setOpenGps(true); // 打开gps

        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    //实现BDLocationListener接口,BDLocationListener为结果监听接口，异步获取定位结果
    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("维度：").append(location.getLatitude());
            currentPosition.append(" 经度：").append(location.getLongitude()).append("\n");
            currentPosition.append("省：").append(location.getProvince());
            currentPosition.append(" 市：").append(location.getCity());
            currentPosition.append(" 区：").append(location.getDistrict());
            currentPosition.append(" 街道：").append(location.getStreet()).append("\n");
            LatLng p1 = new LatLng(location.getLatitude(), location.getLongitude());
            points.add(p1);
            if (points.size() > 1){
                drawLineOnMap(points);
            }
            if(location.getLocType() == BDLocation.TypeGpsLocation){
                if (isFirstLoc){Toast.makeText(MyLocationActivity.this, "GSP定位中", Toast.LENGTH_SHORT).show();}
                navigateTo(location,currentPosition);
            }else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
                if(isFirstLoc)Toast.makeText(MyLocationActivity.this, "网络定位中", Toast.LENGTH_SHORT).show();
                navigateTo(location,currentPosition);
            }else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                if (isFirstLoc)Toast.makeText(MyLocationActivity.this, "启动离线定位", Toast.LENGTH_SHORT).show();
                navigateTo(location,currentPosition);
            }else if (location.getLocType() == BDLocation.TypeServerError) {
                Toast.makeText(MyLocationActivity.this, "服务器错误，请检查", Toast.LENGTH_SHORT).show();
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                Toast.makeText(MyLocationActivity.this, "网络错误，请检查", Toast.LENGTH_SHORT).show();
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                Toast.makeText(MyLocationActivity.this, "手机模式错误，请检查是否飞行", Toast.LENGTH_SHORT).show();
            }

            //positionText.setText(currentPosition);

        }
    }



    /**
     * 在地图上划线，轨迹！
     */
    private List<LatLng> points = new ArrayList<>();
    private void drawLineOnMap(List<LatLng> points) {

        Gson gson = new Gson();
        String jsonObject = gson.toJson(points);
        Log.d(TAG, "drawLineOnMap: "+jsonObject);
        saveLatLngInfo(getApplicationContext(),jsonObject);

        //设置折线的属性
        OverlayOptions mOverlayOptions = new PolylineOptions()
                .width(10)
                .color(getResources().getColor(R.color.colorAccent))
                .points(points);
        //在地图上绘制折线
        //mPloyline 折线对象
        Overlay mPolyline = mBaiduMap.addOverlay(mOverlayOptions);
    }

    private void navigateTo(BDLocation location, StringBuilder currentPosition) {
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        // 构造定位数据
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        // 设置定位数据
        mBaiduMap.setMyLocationData(locData);
        // 当不需要定位图层时关闭定位图层
        //mBaiduMap.setMyLocationEnabled(false);
        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            //设置地图的缩放级别：
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(17.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

//            if (location.getLocType() == BDLocation.TypeGpsLocation) {
//                // GPS定位结果
//                Toast.makeText(MyLocationActivity.this, location.getAddrStr(), Toast.LENGTH_SHORT).show();
//                showAddr.setText("你的位置："+location.getAddrStr());
//            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
//                // 网络定位结果
//                Toast.makeText(MyLocationActivity.this, location.getAddrStr(), Toast.LENGTH_SHORT).show();
//                showAddr.setText("你的位置："+location.getAddrStr());
//            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
//                // 离线定位结果
//                Toast.makeText(MyLocationActivity.this, "启动离线定位", Toast.LENGTH_SHORT).show();
//                showAddr.setText("你的位置："+location.getAddrStr());
//            } else if (location.getLocType() == BDLocation.TypeServerError) {
//                Toast.makeText(MyLocationActivity.this, "服务器错误，请检查", Toast.LENGTH_SHORT).show();
//            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
//                Toast.makeText(MyLocationActivity.this, "网络错误，请检查", Toast.LENGTH_SHORT).show();
//            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
//                Toast.makeText(MyLocationActivity.this, "手机模式错误，请检查是否飞行", Toast.LENGTH_SHORT).show();
//            }
        }
        showAddr.setText(currentPosition);
    }

    private void initView() {
        mMapView = findViewById(R.id.bmapView);
        bt =  findViewById(R.id.bt);
        bt.setOnClickListener(this);
        button =  findViewById(R.id.button);
        button.setOnClickListener(this);
        buttons = findViewById(R.id.buttons);
        buttons.setOnClickListener(this);

        showAddr = findViewById(R.id.show_addr);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
//        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt:
                //把定位点再次显现出来
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(mapStatusUpdate);
                break;
            case R.id.button:
                //卫星地图
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.buttons:
                //普通地图
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    for (int result :grantResults){
                        if (result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this, "必须所有权限才能使用本服务", Toast.LENGTH_SHORT).show();
                            finish();return;
                        }
                    }
                    requesrLocation();
                }else {
                    Toast.makeText(this, "You Permission Denied", Toast.LENGTH_SHORT).show();
                    showAddr.setText("请授予位置权限！否则定位系统无法启动");
                    //finish();
                }
                break;
            default:
        }
    }
}