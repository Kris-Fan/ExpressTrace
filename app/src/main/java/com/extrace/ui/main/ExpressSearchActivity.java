package com.extrace.ui.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.extrace.net.OkHttpClientManager;
import com.extrace.net.json.MyJsonManager;
import com.extrace.ui.R;
import com.extrace.ui.adapter.TraceListAdapter;
import com.extrace.ui.entity.ExpressSheet;
import com.extrace.ui.entity.Location;
import com.extrace.ui.entity.Trace;
import com.extrace.ui.service.LoginService;
import com.extrace.util.EmptyView;
import com.extrace.util.TitleLayout;
import com.extrace.util.layout.ClearEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.extrace.net.OkHttpClientManager.BASE_URL;
import static com.extrace.ui.service.LocationInfoShared.getLatLngInfo;
import static com.extrace.ui.service.LocationInfoShared.saveLatLngInfo;

public class ExpressSearchActivity extends AppCompatActivity implements View.OnKeyListener {
//    RecyclerViewAdapter mAdapter;
//    private ArrayList<Integer> mItems;

    private static final String TAG = "ExpressSearchActivity";
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LatLng latLng;
    
    private NestedScrollView nestedScrollView;
    private EmptyView emptyView;
    private ClearEditText clearEditText;
    private RecyclerView rvTrace;
    private List<Trace> traceList = new ArrayList<>();
    private TraceListAdapter adapter;

    private TitleLayout titleLayout ; // 标题栏
    private String urlMap = BASE_URL+"/ExtraceSystem/queryMap/";
    private LoginService loginService = new LoginService();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express_search);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){ //隐藏自带的标题栏
            actionBar.hide();
        }

        titleLayout = findViewById(R.id.title);
        titleLayout.setTitle(getResources().getString(R.string.action_search));
        titleLayout.hideTitleEdit();
        titleLayout.setBackground(getResources().getColor(R.color.colorPrimaryTrans));
        titleLayout.setTitleColor(getResources().getColor(R.color.white));
        findView();
        bindEvent();
       // initData();
        initMap();
    }


    private void bindEvent() {
        clearEditText.setOnKeyListener(this);
    }

    private int screenWidth,screenHeight;
    private void findView() {
        clearEditText = findViewById(R.id.filter_edit);
        rvTrace = findViewById(R.id.rv_trace);
        emptyView = findViewById(R.id.empty);
        emptyView.setErrorType(EmptyView.HIDE_LAYOUT);
        mMapView = findViewById(R.id.bmapView);
        nestedScrollView = findViewById(R.id.nestScroll);

        rvTrace.setVisibility(View.INVISIBLE);
        //設置地圖高度為屏幕高度
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        screenWidth= dm.widthPixels;
//        screenHeight= dm.heightPixels;
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mMapView.getLayoutParams();
//        params.height = screenHeight;
//        mMapView.setLayoutParams(params);

        // 解决地图拖动和ScrollView上下滑动冲突问题
        View v = mMapView.getChildAt(0);
        v.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    nestedScrollView.requestDisallowInterceptTouchEvent(false);
                } else {
                    nestedScrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

        rvTrace.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //滑动的距离
                mDistanceY += dy;
                //toolbar的高度

                Log.i("lalal_expressSearch","dx ="+dx+"  dy="+dy);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//                lp.setMargins(5, 200-mDistanceY,5,0);
//                linearLayout.setLayoutParams(lp);

                int toolbarHeight = clearEditText.getBottom();
                int rvHeight = rvTrace.getTop();
                Log.i(TAG,"toolbarHeight = " +toolbarHeight +" mDistance"+mDistanceY+ "  rvTravce ="+rvHeight);

            }
        });

        emptyView.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!clearEditText.getText().toString().isEmpty()){ //输入框内不为空时
                    startSearchTrace(clearEditText.getText().toString());
                }
            }
        });
    }

    private int mDistanceY = 0;
    private void initData() {
        // 模拟一些假的数据
        traceList.add(new Trace("2016-05-25 17:48:00", "[沈阳市] [沈阳和平五部]的派件已签收 感谢使用中通快递,期待再次为您服务!"));
        traceList.add(new Trace("2016-05-25 14:13:00", "[沈阳市] [沈阳和平五部]的东北大学代理点正在派件 电话:18040xxxxxx 请保持电话畅通、耐心等待"));
        traceList.add(new Trace("2016-05-25 13:01:04", "[沈阳市] 快件到达 [沈阳和平五部]"));
        traceList.add(new Trace("2016-05-25 12:19:47", "[沈阳市] 快件离开 [沈阳中转]已发往[沈阳和平五部]"));
        traceList.add(new Trace("2016-05-25 11:12:44", "[沈阳市] 快件到达 [沈阳中转]"));
        traceList.add(new Trace("2016-05-24 03:12:12", "[嘉兴市] 快件离开 [杭州中转部]已发往[沈阳中转]"));
        traceList.add(new Trace("2016-05-23 21:06:46", "[杭州市] 快件到达 [杭州汽运部]"));
        traceList.add(new Trace("2016-05-23 18:59:41", "[杭州市] 快件离开 [杭州乔司区]已发往[沈阳]"));
        traceList.add(new Trace("2016-05-23 18:35:32", "[杭州市] [杭州乔司区]的市场部已收件 电话:18358xxxxxx"));
        adapter = new TraceListAdapter(this, traceList);
        rvTrace.setLayoutManager(new LinearLayoutManager(this));
        rvTrace.setAdapter(adapter);
        rvTrace.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //滑动的距离
                mDistanceY += dy;
                //toolbar的高度

                Log.i("lalal_expressSearch","dx ="+dx+"  dy="+dy);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                lp.setMargins(5, 1050-mDistanceY,5,0);
                rvTrace.setLayoutParams(lp);

                int toolbarHeight = clearEditText.getBottom();
                int rvHeight = rvTrace.getTop();
                Log.i("lalal_expressSearch","toolbarHeight = " +toolbarHeight +" mDistance"+mDistanceY+ "  rvTravce ="+rvHeight);


//                if (mDistanceY <= 200 && dy>=0){
//                    curY = 260-mDistanceY;
//                    lp.setMargins(14, 14,14,curY);
//                    clearEditText.setLayoutParams(lp);
//                }

//                if (mDistanceY > 200 && dy<0){ //下滑时
//                    curY = curY+ mDistanceY;
//                    lp.setMargins(14, 14,14,curY);
//                }
//                //当滑动的距离 <= toolbar高度的时候，改变Toolbar背景色的透明度，达到渐变的效果
//                if (mDistanceY <= toolbarHeight) {
//
//                    float scale = (float) mDistanceY / toolbarHeight;
//                    float alpha = scale * 255;
//                    titleLayout.setBackgroundColor(Color.argb((int) alpha, 128, 0, 0));
//                } else {
//                    //上述虽然判断了滑动距离与toolbar高度相等的情况，但是实际测试时发现，标题栏的背景色
//                    //很少能达到完全不透明的情况，所以这里又判断了滑动距离大于toolbar高度的情况，
//                    //将标题栏的颜色设置为完全不透明状态
//                    titleLayout.setBackgroundResource(R.color.colorPrimary);
//                }

            }
        });
/*        ---------------------
                作者：放眼未来活在当下
        来源：CSDN
        原文：https://blog.csdn.net/smart_yc/article/details/52575651
        版权声明：本文为博主原创文章，转载请附上博文链接！*/
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_ENTER){    //按下enter键
            if (!clearEditText.getText().toString().isEmpty()){ //输入框内不为空时
                startSearchTrace(clearEditText.getText().toString());
            }
        }
        return false;
    }

    private String urlEx = BASE_URL +"/ExtraceSystem/queryHistory/";
    /**
     * 追踪快递
     * @param id
     */
    private void startSearchTrace(final String id) {
        rvTrace.setVisibility(View.VISIBLE);
        emptyView.setErrorType(EmptyView.NETWORK_LOADING);

        OkHttpClientManager okHttpClientManager = new OkHttpClientManager(loginService.userInfoSha256(this));
        okHttpClientManager.getAsyn(urlEx + id, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                handler.sendEmptyMessage(404);
            }

            @Override
            public void onResponse(String response) {
                List<Trace> expressSheet = new MyJsonManager().traceListJsonJX(response);
                Log.d(TAG, "onResponse: 搜索结果："+response);
                if (expressSheet!= null && expressSheet.size()>0) {
                    findResult(expressSheet);
                    handler.sendEmptyMessage(200);//Message(message);
                }else {
                    handler.sendEmptyMessage(404);
                }
            }
        });

        okHttpClientManager.getAsyn(urlMap + id, new OkHttpClientManager.ResultCallback<List<Location>>() {
            @Override
            public void onError(Request request, Exception e) {
                handler.sendEmptyMessage(401);
            }

            @Override
            public void onResponse(List<Location> response) {
                Log.e(TAG, "onResponse: "+response);
                if (response!=null && !response.isEmpty()) {
                    showTraceOnMap(response);
                }else {
                    handler.sendEmptyMessage(401);
                }
            }
        });

    }

    /**
     * 显示地图上轨迹
     * @param locationList 获取到的坐标点列表
     */
    private void showTraceOnMap(List<Location> locationList) {
        for (int i = 0; i < locationList.size()-2; i++) {
            LatLng latLng = new LatLng(locationList.get(i).x,locationList.get(i).y);
            points.add(latLng);
        }
        Log.e(TAG, "showTraceOnMap: point:" + points.toString());
        if (points!=null && points.size()>2) {
            navigateTo();
        }else {

            handler.sendEmptyMessage(401);
        }
    }

    private void findResult(List<Trace> traces) {
        traceList = new ArrayList<>();
        traceList.addAll(traces);
        handler.sendEmptyMessage(200);

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 200:
                    adapter = new TraceListAdapter(getApplicationContext(), traceList);
                    rvTrace.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rvTrace.setAdapter(adapter);

                    emptyView.setErrorType(EmptyView.HIDE_LAYOUT);
                    adapter.notifyDataSetChanged();
                    break;
                case 401:
                    Toast.makeText(ExpressSearchActivity.this, "无当前单号对应的地图轨迹信息", Toast.LENGTH_SHORT).show();
                    mBaiduMap.clear();
                    break;
                case 404:
                    Toast.makeText(getApplicationContext(), "无效单号！", Toast.LENGTH_SHORT).show();
                    //initData();
                    mMapView.setVisibility(View.GONE);
                    traceList.clear();
                    traceList.add(new Trace("啦啦啦快递提醒您", "无结果，请检查输入是否有误后重试"));

                    adapter = new TraceListAdapter(getApplicationContext(), traceList);
                    rvTrace.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rvTrace.setAdapter(adapter);

                    adapter.notifyDataSetChanged();
                    emptyView.setErrorType(EmptyView.NODATA);
                    break;
                case 500:
                    Toast.makeText(getApplicationContext(), "服务异常！", Toast.LENGTH_SHORT).show();
                    emptyView.setErrorType(EmptyView.NETWORK_ERROR2);
                    break;
            }
        }
    };

    //地图相关
    private void initMap() {
        //获取地图控件引用
        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);

        //默认显示普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //initLocation();

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

        //navigateTo();
    }
    private void navigateTo() {
        Log.d(TAG, "navigateTo: ");
//        String latStr = getLatLngInfo(getApplicationContext());
//        if (!"".equals(latStr)){
//            Gson gson = new Gson();
//            List<LatLng> latLngList = gson.fromJson(latStr,new TypeToken<List<LatLng>>(){}.getType());
//            if (latLngList != null){
//                points.addAll(latLngList);
//                Log.e(TAG, "navigateTo: "+points.toString() );
//            }
//        }
        mMapView.setVisibility(View.VISIBLE);
        int index = points.size()-1;
        LatLng ll = new LatLng(points.get(index).latitude,
                points.get(index).longitude);
        //设置地图的缩放级别：
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(14.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        drawLineOnMap(points);
    }
    /**
     * 在地图上划线，轨迹！
     */
    private List<LatLng> points = new ArrayList<>();
    private void drawLineOnMap(List<LatLng> points) {

        //设置折线的属性
        OverlayOptions mOverlayOptions = new PolylineOptions()
                .width(10)
                .color(getResources().getColor(R.color.colorAccent))
                .points(points);
        //在地图上绘制折线
        //mPloyline 折线对象
        Overlay mPolyline = mBaiduMap.addOverlay(mOverlayOptions);
    }
}
