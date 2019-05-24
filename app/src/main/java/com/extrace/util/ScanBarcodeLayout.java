package com.extrace.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.extrace.ui.R;
import com.extrace.ui.entity.History;
import com.extrace.ui.main.ExpressEditActivity;
import com.extrace.ui.main.HistoryScanActivity;
import com.extrace.ui.main.SendExpressActivity;
import com.extrace.ui.service.HistoryOperator;
import com.king.zxing.Intents;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ScanBarcodeLayout extends LinearLayout implements View.OnClickListener {

    public static final String KEY_TITLE = "key_title";
    public static final String KEY_CODE = "key_code";
    public static final String KEY_IS_QR_CODE = "key_code";
    public static final String KEY_IS_CONTINUOUS = "key_continuous_scan";
    public static final int REQUEST_CODE_SCAN_RECEIVE = 0X101;
    public static final int REQUEST_CODE_SCAN_SEND = 0X102;
    public static final int REQUEST_CODE_SCAN_SEND_UPLOAD = 0X106; //装货扫描
    public static final int REQUEST_CODE_SCAN_ARRIVE = 0X103;
    public static final int REQUEST_CODE_SCAN_DISPATCH = 0X104;
    public static final int REQUEST_CODE_SCAN_SIGN = 0X105;

    public static final int REQUEST_CODE_PHOTO = 0X02;

    public static final int RC_CAMERA = 0X01;

    public static final int RC_READ_PHOTO = 0X02;

    private static final int REQUEST_CODE = 111;


    private LinearLayout menu_1,menu_2,menu_3,menu_4, menu_5,menu_6, menu_7;
    private TextView tv_courier_name,tv_tel,tv_site;

    public ScanBarcodeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.activity_scan_barcode,this);
        initView(context);
        bindView(context);
    }

    private void initView(Context context){

        TitleLayout titleLayout ;
        titleLayout = findViewById(R.id.title);
        titleLayout.setTitle(getResources().getString(R.string.scan_gun));
        titleLayout.hideTitleEdit();

        menu_1 = findViewById(R.id.ly_function1);
        menu_2 = findViewById(R.id.ly_function2);
        menu_3 = findViewById(R.id.ly_function3);
        menu_4 = findViewById(R.id.ly_function4);
        menu_5 = findViewById(R.id.ly_function5);
        menu_6 = findViewById(R.id.ly_function6);
        menu_7 = findViewById(R.id.ly_function7);
        tv_courier_name = findViewById(R.id.tv_courier_name);
        tv_tel = findViewById(R.id.tv_tel);
        tv_site = findViewById(R.id.tv_site);
    }

    private void bindView(Context context) {
        menu_1.setOnClickListener(this);
        menu_2.setOnClickListener(this);
        menu_3.setOnClickListener(this);
        menu_4.setOnClickListener(this);
        menu_5.setOnClickListener(this);
        menu_6.setOnClickListener(this);
        menu_7.setOnClickListener(this);

        //显示用户此前录入的数据
        SharedPreferences sPreferences = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);
        String pwd = sPreferences.getString("password", "");
        if (!"".equals(pwd)) {
            String username = sPreferences.getString("username", "");
            String tel = sPreferences.getString("telcode", "");
            String dptid = sPreferences.getString("dptid", "");
            tv_courier_name.setText(username);
            tv_tel.setText(tel);
            tv_site.setText(dptid);
        }
    }
    private Class<?> cls;
    private String title;
    private boolean isContinuousScan;   //是否连续扫码
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ly_function1:
                this.cls = CustomCaptureActivity.class;
                this.title = "收件扫描";
                startScan(cls,title,REQUEST_CODE_SCAN_RECEIVE);
                break;
            case R.id.ly_function2:
                this.cls = CustomCaptureActivity.class;
                this.title = "包裹打包";
                isContinuousScan =false;
                startScan(cls,title,REQUEST_CODE_SCAN_SEND);
                break;
            case R.id.ly_function3:
                this.cls = CustomCaptureActivity.class;
                this.title = "装货上车";
                isContinuousScan =true;
                startScan(cls,title,REQUEST_CODE_SCAN_SEND_UPLOAD);
                break;
            case R.id.ly_function4:
                this.cls = CustomCaptureActivity.class;
                this.title = "包裹拆包| 到件扫描";
                isContinuousScan = true;
                startScan(cls,title,REQUEST_CODE_SCAN_ARRIVE);
                break;
            case R.id.ly_function5:
                this.cls = CustomCaptureActivity.class;
                this.title = "派件扫描";
                isContinuousScan = true;
                startScan(cls,title,REQUEST_CODE_SCAN_DISPATCH);
                break;
            case R.id.ly_function6:
                this.cls = CustomCaptureActivity.class;
                this.title = "签收扫描";
                isContinuousScan = true;
                startScan(cls,title,REQUEST_CODE_SCAN_SIGN);
                //Toast.makeText(this, "签收扫描", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ly_function7:
                Toast.makeText(getContext(), "扫描记录~", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(getContext(), HistoryScanActivity.class);
                intent.putExtra("Insert", 1);
                getContext().startActivity(intent);
                break;
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
        ActivityCompat.startActivityForResult((Activity) getContext(),intent,code,optionsCompat.toBundle());
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        String result = "";
//        String msg = "";
//        Intent intent;
//        if (resultCode == RESULT_OK && data != null) {
//            switch (requestCode) {
//                case REQUEST_CODE_SCAN_RECEIVE: //收件扫描结果
//                    msg = "收件扫描";
//                    result = data.getStringExtra(Intents.Scan.RESULT);
//                    //Toast.makeText(this,"扫描到了："+result,Toast.LENGTH_SHORT).show();
//                    intent = new Intent(getContext(), ExpressEditActivity.class);
//
//                    intent.putExtra("EXPRESS_ID", result);
//                    getContext().startActivity(intent);
//                    break;
//                case REQUEST_CODE_SCAN_SEND:    //发件扫描结果
//                    msg = "包裹打包扫描";
//                    result = data.getStringExtra(Intents.Scan.RESULT);
//                    //Toast.makeText(this,"扫描到了："+result+REQUEST_CODE_SCAN_SEND,Toast.LENGTH_SHORT).show();
//                    intent = new Intent(getContext().this, SendExpressActivity.class);
//                    intent.putExtra("EXPRESS_ID", result);
//                    getContext().startActivity(intent);
//                    break;
//                case REQUEST_CODE_SCAN_SEND_UPLOAD:
//                    msg = "装货扫描";
//                    break;
//                case REQUEST_CODE_SCAN_ARRIVE:      //到件扫描结果
//                    msg = "包裹拆包、到件扫描";
//                    result = data.getStringExtra(Intents.Scan.RESULT);
//                    Toast.makeText(getContext(), "扫描到了：" + result + REQUEST_CODE_SCAN_ARRIVE, Toast.LENGTH_SHORT).show();
//                    break;
//                case REQUEST_CODE_SCAN_DISPATCH:    //派送扫描结果
//                    msg = "派件扫描";
//                    result = data.getStringExtra(Intents.Scan.RESULT);
//                    Toast.makeText(getContext(), "扫描到了：" + result + REQUEST_CODE_SCAN_DISPATCH, Toast.LENGTH_SHORT).show();
//                    break;
//                case REQUEST_CODE_SCAN_SIGN:    //签收扫描
//                    msg = "签收扫描";
//                    break;
//                case REQUEST_CODE_PHOTO:
//                    //parsePhoto(data);
//                    break;
//                default:
//                    break;
//            }
//
//            HistoryOperator historyOperator = new HistoryOperator(getContext());
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
////获取当前时间
//            Date date = new Date(System.currentTimeMillis());
//            History history = new History();
//            history.expressId = result;
//
//            history.context = "扫描方式：" + msg;//String.valueOf(requestCode);
//            history.time = simpleDateFormat.format(date);
//            boolean add = historyOperator.insert(history);
//        }
//    }
}
