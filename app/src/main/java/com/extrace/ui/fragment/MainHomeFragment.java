package com.extrace.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.extrace.net.OkHttpClientManager;
import com.extrace.ui.R;
import com.extrace.ui.adapter.MainAdapter;
import com.extrace.ui.entity.History;
import com.extrace.ui.main.AddPackageActivity;
import com.extrace.ui.main.CustomerManageActivity;
import com.extrace.ui.main.ExpressEditActivity;
import com.extrace.ui.main.ExpressSearchActivity;
import com.extrace.ui.main.HistoryScanActivity;
import com.extrace.ui.main.LoginActivity;
import com.extrace.ui.main.ScanBarcodeActivity;
import com.extrace.ui.main.SendExpressActivity;
import com.extrace.ui.service.HistoryOperator;
import com.extrace.ui.service.LoginService;
import com.extrace.ui.service.MyService;
import com.extrace.util.CustomCaptureActivity;
import com.king.zxing.Intents;
import com.squareup.okhttp.Request;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.extrace.net.OkHttpClientManager.BASE_URL;
import static com.extrace.ui.fragment.MainMenuFragment.REQUEST_CODE_PHOTO;
import static com.extrace.ui.main.ScanBarcodeActivity.KEY_CODE;
import static com.extrace.ui.main.ScanBarcodeActivity.KEY_IS_CONTINUOUS;
import static com.extrace.ui.main.ScanBarcodeActivity.KEY_TITLE;
import static com.extrace.ui.main.ScanBarcodeActivity.REQUEST_CODE_SCAN_ARRIVE;
import static com.extrace.ui.main.ScanBarcodeActivity.REQUEST_CODE_SCAN_DISPATCH;
import static com.extrace.ui.main.ScanBarcodeActivity.REQUEST_CODE_SCAN_RECEIVE;
import static com.extrace.ui.main.ScanBarcodeActivity.REQUEST_CODE_SCAN_SEND;
import static com.extrace.ui.main.ScanBarcodeActivity.REQUEST_CODE_SCAN_SEND_UPLOAD;
import static com.extrace.ui.main.ScanBarcodeActivity.REQUEST_CODE_SCAN_SIGN;

public class MainHomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MainHomeFragment";
    private View view;
    private LoginService loginService = new LoginService();
    private LinearLayout menu_1,menu_2,menu_3,menu_4, menu_5,menu_6, menu_7, menu_8;
    private TextView tv_courier_name,tv_tel,tv_site,tv_courier_name_tag,appTitle;
    private LinearLayout ly_scan, ly_add, ly_search, driver_menu_1,driver_menu_2,driver_menu_3;
    private RelativeLayout driver_home_menus;
    private LinearLayout home_menus;
    public MainHomeFragment() {
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_home,container,false);
        initView(view);
        //bindView();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getActivity().getIntent();
        initView(getView());
        Log.d(TAG, "onResume: ");
    }
    private void initView(View view) {
        appTitle = view.findViewById(R.id.app_title);
        home_menus = view.findViewById(R.id.home_menu);
        driver_home_menus = view.findViewById(R.id.driver_home_menu);
        int role =new LoginService().getUserRoll(getContext());
        if (role == 0) { //快递员
            ly_add = view.findViewById(R.id.ly_tab_menu2);  //客户管理
            ly_search = view.findViewById(R.id.ly_tab_menu1);   //搜索
            menu_1 = view.findViewById(R.id.ly_function1);
            menu_2 = view.findViewById(R.id.ly_function2);
            menu_3 = view.findViewById(R.id.ly_function3);
            menu_4 = view.findViewById(R.id.ly_function4);
            menu_5 = view.findViewById(R.id.ly_function5);
            menu_6 = view.findViewById(R.id.ly_function6);
            menu_7 = view.findViewById(R.id.ly_function7);
            menu_8 = view.findViewById(R.id.ly_function8);
            home_menus.setVisibility(View.VISIBLE);
            driver_home_menus.setVisibility(View.GONE);
            appTitle.setText(getResources().getText(R.string.app_name)+"-快递员版");
            bindView();
        }else { //司机
            if (role == 1){
                appTitle.setText(getResources().getText(R.string.app_name)+"-司机版");
                home_menus.setVisibility(View.GONE);
                driver_home_menus.setVisibility(View.VISIBLE);
                driver_menu_1 = view.findViewById(R.id.driver_tab_menu1);
                driver_menu_2 = view.findViewById(R.id.driver_tab_menu2);
                driver_menu_3 = view.findViewById(R.id.driver_tab_menu3);
                driver_menu_1.setOnClickListener(this);
                driver_menu_2.setOnClickListener(this);
                driver_menu_3.setOnClickListener(this);
            }
            ly_add = view.findViewById(R.id.ly_tab_menu2);  //客户管理
            ly_search = view.findViewById(R.id.ly_tab_menu1);   //搜索
            menu_1 = view.findViewById(R.id.ly_function1);
            menu_2 = view.findViewById(R.id.ly_function2);
            menu_3 = view.findViewById(R.id.ly_function3);
            menu_4 = view.findViewById(R.id.ly_function4);
            menu_5 = view.findViewById(R.id.ly_function5);
            menu_6 = view.findViewById(R.id.ly_function6);
            menu_7 = view.findViewById(R.id.ly_function7);
            menu_8 = view.findViewById(R.id.ly_function8);
            home_menus.setVisibility(View.VISIBLE);
            driver_home_menus.setVisibility(View.GONE);
            appTitle.setText(getResources().getText(R.string.app_name)+"-快递员版");
            bindView();
        }
    }
    private void bindView() {
        menu_1.setOnClickListener(this);
        menu_2.setOnClickListener(this);
        menu_3.setOnClickListener(this);
        menu_4.setOnClickListener(this);
        menu_5.setOnClickListener(this);
        menu_6.setOnClickListener(this);
        menu_7.setOnClickListener(this);
        menu_8.setOnClickListener(this);
        ly_add.setOnClickListener(this);
        ly_search.setOnClickListener(this);
    }

    protected Activity mActivity;
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.mActivity =(Activity) context;
    }

    private Class<?> cls;
    private String title;
    private boolean isContinuousScan;   //是否连续扫码
    @Override
    public void onClick(View v) {
        if (loginService.isLogined(getContext())) {
            int role = loginService.getUserRoll(getContext());
            Intent intent;
            switch (v.getId()) {
                case R.id.ly_tab_menu1: //搜索快递
                case R.id.driver_tab_menu1:
                    intent = new Intent(getContext(), ExpressSearchActivity.class);
                    startActivity(intent);
                    break;
                case R.id.ly_tab_menu2:
                    intent = new Intent(getContext(), CustomerManageActivity.class);
                    startActivity(intent);
                    break;
                case R.id.ly_function1:
                    this.cls = CustomCaptureActivity.class;
                    this.title = "收件扫描";
                    startScan(cls, title, REQUEST_CODE_SCAN_RECEIVE);
                    break;
                case R.id.ly_function2:
                    this.cls = CustomCaptureActivity.class;
                    this.title = "包裹打包";
                    isContinuousScan = false;
                    startScan(cls, title, REQUEST_CODE_SCAN_SEND);
                    break;
                case R.id.driver_tab_menu3://装货扫描
                case R.id.ly_function3:
                    Intent startIntent = new Intent(getActivity(), MyService.class);
                    getActivity().startService(startIntent);
                    this.cls = CustomCaptureActivity.class;
                    this.title = "装货上车";
                    isContinuousScan = true;
                    startScan(cls, title, REQUEST_CODE_SCAN_SEND_UPLOAD);
                    break;
                case R.id.ly_function4:
                    this.cls = CustomCaptureActivity.class;
                    this.title = "包裹拆包| 到件扫描";
                    isContinuousScan = true;
                    startScan(cls, title, REQUEST_CODE_SCAN_ARRIVE);
                    break;
                case R.id.ly_function5:
                    this.cls = CustomCaptureActivity.class;
                    this.title = "派件扫描";
                    isContinuousScan = true;
                    startScan(cls, title, REQUEST_CODE_SCAN_DISPATCH);
                    break;
                case R.id.ly_function6:
                    this.cls = CustomCaptureActivity.class;
                    this.title = "签收扫描";
                    isContinuousScan = true;
                    startScan(cls, title, REQUEST_CODE_SCAN_SIGN);
                    break;
                case R.id.ly_function7:
                    //Toast.makeText(getContext(), "扫描记录~", Toast.LENGTH_SHORT).show();
                    intent = new Intent();
                    intent.setClass(getContext(), HistoryScanActivity.class);
                    intent.putExtra("Insert", 1);
                    startActivity(intent);
                    break;
                case R.id.ly_function8:
                    //Toast.makeText(getContext(), "创建包裹", Toast.LENGTH_SHORT).show();
                    Intent intent0 = new Intent();
                    intent0.setClass(getContext(), AddPackageActivity.class);
                    startActivity(intent0);
                    break;
            }
        }else {
            showAlert("请先登录！","点击‘确认’立即登录");
        }
    }
    /**
     * 扫码
     * @param cls
     * @param title
     */
    private void startScan(Class<?> cls,String title, int code){
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(getContext(),R.anim.in,R.anim.out);
        Intent intent = new Intent(getContext(), cls);
        intent.putExtra(KEY_TITLE,title);
        intent.putExtra(KEY_CODE,code);
        intent.putExtra(KEY_IS_CONTINUOUS,isContinuousScan);
        ActivityCompat.startActivityForResult(getActivity(),intent,code,optionsCompat.toBundle());
    }

    private void showAlert(String title,String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setMessage(msg);
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
