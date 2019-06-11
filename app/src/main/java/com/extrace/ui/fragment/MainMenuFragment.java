package com.extrace.ui.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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
import com.baidu.mapapi.utils.DistanceUtil;
import com.extrace.ui.R;
import com.extrace.ui.main.CallActivity;
import com.extrace.ui.main.CustomerEditActivity;
import com.extrace.ui.main.CustomerManageActivity;
import com.extrace.ui.main.ExpressSearchActivity;
import com.extrace.ui.main.LoginActivity;
import com.extrace.ui.main.MyLocationActivity;
import com.extrace.ui.main.ScanBarcodeActivity;
import com.extrace.ui.service.LoginService;
import com.extrace.ui.service.MyService;
import com.extrace.util.CustomCaptureActivity;
import com.extrace.util.UriUtils;
import com.google.gson.Gson;
import com.king.zxing.util.CodeUtils;

import java.util.ArrayList;
import java.util.List;

import static com.extrace.ui.main.MainActivity.KEY_IS_CONTINUOUS;
import static com.extrace.ui.main.MainActivity.KEY_TITLE;
import static com.extrace.ui.main.ScanBarcodeActivity.KEY_CODE;
import static com.extrace.ui.main.ScanBarcodeActivity.REQUEST_CODE_SCAN_SEND_UPLOAD;
import static com.extrace.ui.service.LocationInfoShared.saveLatLngInfo;

public class MainMenuFragment extends Fragment implements View.OnClickListener {
    public static final int REQUEST_CODE_SCAN = 0X01;
    public static final int REQUEST_CODE_PHOTO = 0X02;

    public static final int RC_CAMERA = 0X01;

    public static final int RC_READ_PHOTO = 0X02;

    private static final int REQUEST_CODE = 111;
    private TextView appTitle,tvLoc;
    private LinearLayout ly_scan, ly_add, ly_search;
    private LinearLayout menu_1,menu_2,menu_3,menu_4,driver_menu_1,driver_menu_2,driver_menu_3;
    private RelativeLayout menu_5,driver_home_menus;
    private LinearLayout menu_6,home_menus;
    private LoginService loginService = new LoginService();
    private static final String TAG = "MainMenuFragment";

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    public LocationClient mLocationClient;
    public BDLocationListener myListener = new MyLocationListener();
    private TextView bt,showAddr;
    private TextView button;
    private TextView buttons;
    private LatLng latLng;
    private boolean isFirstLoc = true; // 是否首次定位
    public MainMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_main_menu, container, false);
        initView(view);
        tvLoc = view.findViewById(R.id.driver_tab_menu2_tag); //定位显示
        tvLoc.setText("开始定位");
        initMapView(view);
        Log.e("MainActivity_oncreat","oncreate调用");
        //bindView(view);
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getActivity().getIntent();
        initView(getView());
        mMapView.onResume();
        Log.d(TAG, "onResume: ");
    }

    private void bindView(View view) {
        menu_1.setOnClickListener(this);
        menu_2.setOnClickListener(this);
        menu_3.setOnClickListener(this);
        menu_4.setOnClickListener(this);
        menu_5.setOnClickListener(this);
        menu_6.setOnClickListener(this);

        ly_scan.setOnClickListener(this);
        ly_add.setOnClickListener(this);
        ly_search.setOnClickListener(this);
    }

    private void initView(View view) {
        appTitle = view.findViewById(R.id.app_title);
        home_menus = view.findViewById(R.id.home_menu);
        driver_home_menus = view.findViewById(R.id.driver_home_menu);
        int role = new LoginService().getUserRoll(getContext()) ;
        if (role == 0) { //快递员
            ly_scan = view.findViewById(R.id.ly_tab_menu3);
            ly_add = view.findViewById(R.id.ly_tab_menu2);
            ly_search = view.findViewById(R.id.ly_tab_menu1);
            menu_1 = view.findViewById(R.id.ly_function1);
            menu_2 = view.findViewById(R.id.ly_function2);
            menu_3 = view.findViewById(R.id.ly_function3);
            menu_4 = view.findViewById(R.id.ly_function4);
            menu_5 = view.findViewById(R.id.rl_function5);
            menu_6 = view.findViewById(R.id.ly_function6);
//            home_menus.setVisibility(View.VISIBLE);
//            driver_home_menus.setVisibility(View.GONE);
            bindView(view);
        }else { //司机
            home_menus.setVisibility(View.GONE);
            driver_home_menus.setVisibility(View.VISIBLE);
            driver_menu_1 = view.findViewById(R.id.driver_tab_menu1);
            driver_menu_2 = view.findViewById(R.id.driver_tab_menu2);
            driver_menu_3 = view.findViewById(R.id.driver_tab_menu3);
            driver_menu_1.setOnClickListener(this);
            driver_menu_2.setOnClickListener(this);
            driver_menu_3.setOnClickListener(this);
            if (role == 1) {
                appTitle.setText(getResources().getText(R.string.app_name) + "-司机版");
            }
        }

    }

    private Class<?> cls;
    private String title;
    private boolean isContinuousScan;
    @Override
    public void onClick(View v) {
        Intent intent;
        if (loginService.isLogined(getContext())) {
            switch (v.getId()) {
                case R.id.ly_tab_menu1:
                case R.id.driver_tab_menu1:
                    intent = new Intent(getContext(), ExpressSearchActivity.class);
                    startActivity(intent);
                    break;
                case R.id.ly_tab_menu2:
                    intent = new Intent(getContext(), CustomerEditActivity.class);
                    startActivity(intent);
                    break;
                case R.id.ly_tab_menu3:
                    intent = new Intent(getContext(), ScanBarcodeActivity.class);
                    startActivity(intent);
                    break;
                case R.id.ly_function1:
                    intent = new Intent(getContext(), CallActivity.class);
                    startActivity(intent);
                    break;
                case R.id.ly_function2:   //default
//                this.cls = CaptureActivity.class;
//                this.title = "";//((TextView)v).getText().toString();
//
//                startScan(cls,title);
                    break;
                case R.id.ly_function3:   //连续扫，+自定义高级界面
//                this.cls = CustomCaptureActivity.class;
//                this.title = "";//this.getText().toString();
//                isContinuousScan = true;
//                startScan(cls,title);
                    intent = new Intent(getContext(), ExpressSearchActivity.class);
                    startActivity(intent);
                    break;
                case R.id.ly_function4://消息
                    break;
                case R.id.rl_function5:
                    intent = new Intent(getContext(), CustomerManageActivity.class);
                    startActivity(intent);
                    break;
                case R.id.ly_function6:   //自定义一般界面
//                this.cls = EasyCaptureActivity.class;
//                this.title = "";//((TextView)v).getText().toString();
//
//                startScan(cls,title);
                    break;
                case R.id.driver_tab_menu3://装货扫描
                    this.cls = CustomCaptureActivity.class;
                    this.title = "装货上车";
                    isContinuousScan =true;
                    startScan(cls,title,REQUEST_CODE_SCAN_SEND_UPLOAD);
                    break;
                case R.id.bt:   //定位
                    MapStatusUpdate mapStatusUpdate1 = MapStatusUpdateFactory.newLatLng(latLng);
                    mBaiduMap.animateMapStatus(mapStatusUpdate1);
                    break;
                case R.id.driver_tab_menu2:
                    //把定位点再次显现出来
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},521);
                    }else {
                        if (isFirstLoc) {
                            isFirstLoc = false;
                            requesrLocation();
                        }else {
                            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
                            mBaiduMap.animateMapStatus(mapStatusUpdate);
                        }
                        Intent startIntent = new Intent(getActivity(), MyService.class);
                        getActivity().startService(startIntent);
                        tvLoc.setText("定位中");
                    }
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
        }else { //未登录状态将不可访问除搜索外的所有功能，并提示
            if (v.getId() == R.id.ly_tab_menu1){
                intent = new Intent(getContext(), ExpressSearchActivity.class);
                startActivity(intent);
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("请先登录！");
                builder.setMessage("功能受限，点击确认立即登录");
                builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivityForResult(intent, 666);
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.cancel), null);
                builder.show();
            }
        }

    }

    private void startPhotoCode(){
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(pickIntent, REQUEST_CODE_PHOTO);
    }
    /**
     * 扫码
     * @param cls
     * @param title
     */
    private void startScan(Class<?> cls,String title, int code){
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(getContext(),R.anim.in,R.anim.out);
        Intent intent = new Intent(getActivity(), cls);
        intent.putExtra(KEY_TITLE,title);
        intent.putExtra(KEY_CODE,code);
        intent.putExtra(KEY_IS_CONTINUOUS,isContinuousScan);
        ActivityCompat.startActivityForResult(getActivity(),intent,code,optionsCompat.toBundle());
    }


    private void parsePhoto(Intent data){
        final String path = UriUtils.INSTANCE.getImagePath(getContext(),data);
        Log.d("Jenly","path:" + path);
        if(TextUtils.isEmpty(path)){
            return;
        }
        //异步解析
        asyncThread(new Runnable() {
            @Override
            public void run() {
                final String result = CodeUtils.parseCode(path);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Jenly","result:" + result);
                        Toast.makeText(getContext(),result,Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }
    private void asyncThread(Runnable runnable){
        new Thread(runnable).start();
    }
    //检查是否开启了相机权限【Android 6.0之后必须添加】
    private void checkCameraPermission() {
        int MY_PERMISSIONS_REQUEST_CALL_CAMERA = 100;
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //如果没有授权，则请求授权
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CALL_CAMERA);
        } else {
            //有授权，直接开启摄像头扫描
        }
    }
    //地图相关：
    private void initMapView(View view) {
        mMapView = view.findViewById(R.id.bmapView);
        bt =  view.findViewById(R.id.bt);
        bt.setOnClickListener(this);
        button =  view.findViewById(R.id.button);
        button.setOnClickListener(this);
        buttons = view.findViewById(R.id.buttons);
        buttons.setOnClickListener(this);
        showAddr = view.findViewById(R.id.show_addr);
        initMap();
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
        mLocationClient = new LocationClient(getContext());     //声明LocationClient类
        //配置定位SDK参数
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},521);
        }else {
            requesrLocation();
        }
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
        int span = 5*1000;
        option.setScanSpan(5000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
        // .getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);
        option.setWifiCacheTimeOut(5*60*1000);//可选，V7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位

        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    //实现BDLocationListener接口,BDLocationListener为结果监听接口，异步获取定位结果
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("纬度：").append(location.getLatitude());
            currentPosition.append(" 经度：").append(location.getLongitude()).append("\n");
            currentPosition.append(location.getProvince());
            currentPosition.append(location.getCity());
            currentPosition.append(location.getDistrict());
            currentPosition.append(location.getStreet()).append("\n");
            LatLng p1 = new LatLng(location.getLatitude(), location.getLongitude());
            int oldLen = points.size();
            points.add(p1);
            int len = points.size();
            if ( len>= 4){
                double x = points.get(len-2).latitude;
                double y = points.get(len-2).longitude;
                double gapX = Math.abs(x-p1.latitude);
                double gapY = Math.abs(y-p1.longitude);
                double dis = DistanceUtil.getDistance(p1, points.get(len-2));
                if (gapX < 0.0002 && gapY < 0.0002 || dis<=12.0){
                    Log.d(TAG, "onReceiveLocation: 不格数据 移除原因： gap:"+gapX+"\t"+gapY+"\tdistance="+dis);
                    points.remove(len-1);
                }
                if (oldLen != points.size()) {
                    drawLineOnMap(points);
                }
            }
            if(location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation
                    ||location.getLocType() == BDLocation.TypeOffLineLocation) {
                if (isFirstLoc)Toast.makeText(getContext(), "定位启动", Toast.LENGTH_SHORT).show();
                navigateTo(location,currentPosition);
            }else if (location.getLocType() == BDLocation.TypeServerError) {
                Toast.makeText(getContext(), "服务器错误，请检查", Toast.LENGTH_SHORT).show();
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                Toast.makeText(getContext(), "网络错误，请检查", Toast.LENGTH_SHORT).show();
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                Toast.makeText(getContext(), "手机模式错误，请检查是否飞行", Toast.LENGTH_SHORT).show();
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
        saveLatLngInfo(getContext(),jsonObject);

        List<LatLng> tempPoints = points;
        tempPoints.remove(0);
        //设置折线的属性
        OverlayOptions mOverlayOptions = new PolylineOptions()
                .width(10)
                .color(getResources().getColor(R.color.colorAccent))
                .points(tempPoints);
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
        }
        showAddr.setText(currentPosition);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 521:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    for (int result :grantResults){
                        if (result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(getContext(), "必须所有权限才能使用本服务", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    requesrLocation();
                }else {
                    Toast.makeText(getContext(), "You Permission Denied", Toast.LENGTH_SHORT).show();
                    showAddr.setText("请授予位置权限！否则定位系统无法启动");
                    //finish();
                }
                break;
            default:
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
//        super.onDestroy();
    }
    

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
            Log.d(TAG, "onHiddenChanged: ");
            mMapView.onPause();
        }
    }

}
