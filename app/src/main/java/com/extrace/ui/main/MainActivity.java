package com.extrace.ui.main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.extrace.net.OkHttpClientManager;
import com.extrace.ui.R;
import com.extrace.ui.adapter.MainAdapter;
import com.extrace.ui.entity.History;
import com.extrace.ui.fragment.ExpressTaskFragment;
import com.extrace.ui.fragment.MainHomeFragment;
import com.extrace.ui.fragment.MainMenuFragment;
import com.extrace.ui.fragment.MainOrderFragment;
import com.extrace.ui.fragment.MePageFragment;
import com.extrace.ui.service.ActivityCollector;
import com.extrace.ui.service.HistoryOperator;
import com.extrace.ui.service.LoginService;
import com.extrace.util.UriUtils;
import com.king.zxing.Intents;
import com.king.zxing.util.CodeUtils;
import com.squareup.okhttp.Request;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.extrace.net.OkHttpClientManager.BASE_URL;
import static com.extrace.ui.fragment.MainMenuFragment.REQUEST_CODE_PHOTO;
import static com.extrace.ui.main.ScanBarcodeActivity.REQUEST_CODE_SCAN_ARRIVE;
import static com.extrace.ui.main.ScanBarcodeActivity.REQUEST_CODE_SCAN_DISPATCH;
import static com.extrace.ui.main.ScanBarcodeActivity.REQUEST_CODE_SCAN_RECEIVE;
import static com.extrace.ui.main.ScanBarcodeActivity.REQUEST_CODE_SCAN_SEND;
import static com.extrace.ui.main.ScanBarcodeActivity.REQUEST_CODE_SCAN_SEND_UPLOAD;
import static com.extrace.ui.main.ScanBarcodeActivity.REQUEST_CODE_SCAN_SIGN;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KEY_TITLE = "key_title";
    public static final String KEY_IS_QR_CODE = "key_code";
    public static final String KEY_IS_CONTINUOUS = "key_continuous_scan";
    public static final int REQUEST_CODE_SCAN = 0X01;
    public static final int REQUEST_CODE_PHOTO = 0X02;

    public static final int RC_CAMERA = 0X01;

    public static final int RC_READ_PHOTO = 0X02;

    private static final int REQUEST_CODE = 111;

    private LinearLayout ly_one,ly_two,ly_four;
    private TextView mTextView1,mTextView2,mTextView4;
    private TextView mTextNum1,mTextNum2,mTextNum3,mTextNum4;
    private ImageView mImageView;
    private MainMenuFragment fg1;       //首页
    private MainHomeFragment fg11; //首页-新
    private MainOrderFragment fg2;     // 订单页面
    private MePageFragment fg4;     //"我的"页面
    private LoginService loginService = new LoginService();
    private static final String TAG = "MainActivity";
    /**
     * 下边是与首页ViewPager
     */
    private ViewPager pager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCollector.addActivity(this);
        bindView();
        ly_one.performClick();
        if (!new LoginService().isLogined(this)){
            showAlert("请先登录","点击确认立即登录");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) { //有一个mainActivity,设置了加载模式为singleTask, 想要在onresume中得到从其他activity传递过来的参数，直接接收的话，发现接收不到，在其他地方，找到了答案，说是在mainActivity中 要重写onNewIntent函数，像这样
        super.onNewIntent(intent);
        setIntent(intent);
        Log.d(TAG, "onNewIntent: ");
        ly_two.setVisibility(new LoginService().getUserRoll(this) == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        ly_two.setVisibility(new LoginService().getUserRoll(this) == 0 ? View.VISIBLE : View.GONE);
    }

    private void bindView() {

        ly_one = findViewById(R.id.ly_tab_menu_deal);
        ly_two = findViewById(R.id.ly_tab_menu_poi);
        ly_four = findViewById(R.id.ly_tab_menu_user);

        mTextView1 = findViewById(R.id.tab_menu_deal);
        mTextView2 = findViewById(R.id.tab_menu_poi);
        mTextView4 = findViewById(R.id.tab_menu_user);

       // mTextNum1 = findViewById(R.id.tab_menu_deal_num);
        mTextNum2 = findViewById(R.id.tab_menu_poi_num);

        mImageView = findViewById(R.id.tab_menu_setting_partner);

        ly_one.setOnClickListener(this);
        ly_two.setOnClickListener(this);
        ly_four.setOnClickListener(this);


    }
    //重置所有文本的选中状态
    private void setSelected() {
        mTextView1.setSelected(false);
        mTextView2.setSelected(false);
        mTextView4.setSelected(false);
    }
    public void hideAllFragment(FragmentTransaction transaction){
        if(fg1!=null){
            transaction.hide(fg1);
        }
        if (fg11!=null){
            transaction.hide(fg11);
        }
        if(fg2!=null){
            transaction.hide(fg2);
        }
        if(fg4!=null){
            transaction.hide(fg4);
        }
    }
    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        switch (v.getId()) {
            case R.id.ly_tab_menu_deal:
                setSelected();
                mTextView1.setSelected(true);
                if (new LoginService().getUserRoll(this) == 0){
                    if(fg11==null){
                        fg11 = new MainHomeFragment();
                        transaction.add(R.id.fragment_container,fg11);
                    }else{
                        transaction.show(fg11);
                    }
                }else {
                    if (fg1 == null) {
                        fg1 = new MainMenuFragment();
                        //fg1 = new HomeSub1Fragment();
                        transaction.add(R.id.fragment_container, fg1);
                    } else {
                        transaction.show(fg1);
                    }
                }

                break;
            case R.id.ly_tab_menu_poi:
                if (loginService.isLogined(this)) {
                    setSelected();
                    mTextView2.setSelected(true);
                    mTextNum2.setVisibility(View.INVISIBLE);
                    if (fg2 == null) {
                        fg2 = new MainOrderFragment();
                        transaction.add(R.id.fragment_container, fg2);
                    } else {
                        transaction.show(fg2);
                    }
                }else{
                    showAlert("请先登录！","访问受限，点击确认立即登录");
                }
                break;
            case R.id.ly_tab_menu_user:
                setSelected();
                mTextView4.setSelected(true);
                // mTextNum4.setVisibility(View.INVISIBLE);
                mImageView.setVisibility(View.INVISIBLE);
                if(fg4==null){
                    fg4 = new MePageFragment();
                    transaction.add(R.id.fragment_container,fg4);
                }else{
                    transaction.show(fg4);
                }
                break;
        }transaction.commit();
    }

    private void showAlert(String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, 666);
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String result = "";
        String msg ="";
        Intent intent;
        if(resultCode == RESULT_OK && data!=null){
            switch (requestCode){
                case REQUEST_CODE_SCAN_RECEIVE: //收件扫描结果
                    msg = "收件扫描";
                    result = data.getStringExtra(Intents.Scan.RESULT);
                    //Toast.makeText(getContext(),"扫描到了："+result,Toast.LENGTH_SHORT).show();
                    intent = new Intent(MainActivity.this, ExpressEditActivity.class);
                    intent.putExtra("EXPRESS_ID",result);
                    startActivity(intent);
                    break;
                case REQUEST_CODE_SCAN_SEND:    //发件扫描结果
                    msg = "包裹打包扫描";
                    result = data.getStringExtra(Intents.Scan.RESULT);
                    //Toast.makeText(MainActivity.this,"扫jhhhh描到了："+result+REQUEST_CODE_SCAN_SEND,Toast.LENGTH_SHORT).show();
                    queryPackage(result);
                    break;
                case REQUEST_CODE_SCAN_SEND_UPLOAD:
                    msg = "装货扫描";
                    break;
                case REQUEST_CODE_SCAN_ARRIVE:      //到件扫描结果
                    msg ="包裹拆包、到件扫描";
                    result = data.getStringExtra(Intents.Scan.RESULT);
                    Toast.makeText(MainActivity.this,"扫描到了："+result+REQUEST_CODE_SCAN_ARRIVE,Toast.LENGTH_SHORT).show();
                    break;
                case REQUEST_CODE_SCAN_DISPATCH:    //派送扫描结果
                    msg = "派件扫描";
                    result = data.getStringExtra(Intents.Scan.RESULT);
                    Toast.makeText(MainActivity.this,"扫描到了："+result+REQUEST_CODE_SCAN_DISPATCH,Toast.LENGTH_SHORT).show();
                    break;
                case REQUEST_CODE_SCAN_SIGN:    //签收扫描
                    msg = "签收扫描";
                    break;
                case REQUEST_CODE_PHOTO:
                    //parsePhoto(data);
                    break;
                default:
                    break;
            }

            if (new LoginService().getUserRoll(this) == 0) {
                HistoryOperator historyOperator = new HistoryOperator(MainActivity.this);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
//获取当前时间
                Date date = new Date(System.currentTimeMillis());
                History history = new History();
                history.expressId = result;

                history.context = "扫描方式：" + msg;//String.valueOf(requestCode);
                history.time = simpleDateFormat.format(date);
                boolean add = historyOperator.insert(history);
//            if (add){
//                Toast.makeText(MainActivity.this, "记录已同步", Toast.LENGTH_SHORT).show();
//            }else {
//                Toast.makeText(MainActivity.this, "扫描记录添加失败", Toast.LENGTH_SHORT).show();
//            }
            }


        }
    }

    //包裹打包 REQUEST_CODE_SCAN_SEND
    private boolean queryPackage(final String string){
        //Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "queryPackage: "+string);
        OkHttpClientManager okHttpClientManager = new OkHttpClientManager(new LoginService().userInfoSha256(MainActivity.this));
        okHttpClientManager.getAsyn(BASE_URL +"/ExtraceSystem/queryPackage/"+string,
                new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        //Toast.makeText(getApplicationContext(), "出错了", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder  = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("扫描失败" ) ;
                        builder.setMessage("请检查网络环境！" ) ;
                        builder.setPositiveButton("是" ,  null );
                        builder.show();
                    }

                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, SendExpressActivity.class);
                        intent.putExtra("response", response);
                        Log.e("lalal_CustomCapture",response);
                        if ("".equals(response)){
                            AlertDialog.Builder builder  = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("扫描失败" ) ;
                            builder.setMessage("处理失败，该单号不存在！" ) ;
                            builder.setPositiveButton("是" ,  null );
                            builder.show();
                            //restartPreviewAndDecode();
                        }else {
                            Log.e(TAG, "onResponse: "+response );
                            startActivity(intent);
                            //finish();
                        }
                    }
                });

        return false;
    }
    private void parsePhoto(Intent data){
        final String path = UriUtils.INSTANCE.getImagePath(this,data);
        Log.d("Jenly","path:" + path);
        if(TextUtils.isEmpty(path)){
            return;
        }
        //异步解析
        asyncThread(new Runnable() {
            @Override
            public void run() {
                final String result = CodeUtils.parseCode(path);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Jenly","result:" + result);
                        Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //如果没有授权，则请求授权
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CALL_CAMERA);
        } else {
            //有授权，直接开启摄像头扫描
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private boolean mIsExit;

    /**
     * 双击返回
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsExit) {
                this.finish();

            } else {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                mIsExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }//  转载来源 https://blog.csdn.net/a15286856575/article/details/50883807
}
