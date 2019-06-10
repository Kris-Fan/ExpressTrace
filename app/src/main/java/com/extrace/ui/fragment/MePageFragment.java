package com.extrace.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.extrace.ui.R;
import com.extrace.ui.main.LoginActivity;
import com.extrace.ui.main.MyInfoActivity;
import com.extrace.ui.main.MyLocationActivity;
import com.extrace.ui.main.MyMessageActivity;
import com.extrace.ui.main.MyParcelActivity;
import com.extrace.ui.main.NodeActivity;
import com.extrace.ui.main.SendExpressActivity;
import com.extrace.ui.main.SettingsActivity;
import com.extrace.ui.service.LoginService;
import com.extrace.ui.service.MyService;
import com.extrace.ui.service.RevExpressDialog;
import com.extrace.util.CustomCaptureActivity;
import com.extrace.util.EasyCaptureActivity;
import com.extrace.util.UriUtils;
import com.king.zxing.CaptureActivity;
import com.king.zxing.util.CodeUtils;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.extrace.ui.main.MainActivity.KEY_IS_CONTINUOUS;
import static com.extrace.ui.main.MainActivity.KEY_TITLE;

public class MePageFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_LOGIN = 0X666;
    private static final int LOGOUT_CODE =0X555;    //退出状态码
    private Button btnSign;
    //
    private LinearLayout menu_1;
    private LinearLayout menu_2;
    private LinearLayout menu_3;
    private LinearLayout menu_4;

    private RelativeLayout ly_login;
    private LinearLayout ly_myInfo;
    private TextView tv_name;
    private TextView textView;
    int permission_Check;
    String info = "-1";

    private LoginService loginService = new LoginService();
    public MePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me_page, container, false);
        textView = view.findViewById(R.id.text_in_main);
        btnSign = view.findViewById(R.id.button_sign);

        menu_1 = view.findViewById(R.id.menu_1);
        menu_2 = view.findViewById(R.id.menu_2);
        menu_3 = view.findViewById(R.id.menu_3);
        menu_4 = view.findViewById(R.id.menu_4);

        ly_login = view.findViewById(R.id.ly_login);
        ly_myInfo = view.findViewById(R.id.ly_myInfo);
        tv_name = view.findViewById(R.id.login_big_font);
        initView(view);
        //showEditDialog(view);
        Log.e("MainActivity_oncreat", "oncreate调用");
        bindView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getActivity().getIntent();
        permission_Check = intent.getIntExtra("permission_check",-1);
        initView(getView());
        Log.d("MainActivity_onresume", "重新加载");
    }

    private void bindView() {
        btnSign.setOnClickListener(this);
        menu_1.setOnClickListener(this);
        menu_2.setOnClickListener(this);
        menu_3.setOnClickListener(this);
        menu_4.setOnClickListener(this);

        ly_myInfo.setOnClickListener(this);
        ly_login.setOnClickListener(this);
    }


    private void initView(View view) {

        //显示用户此前录入的数据
        SharedPreferences sPreferences = getActivity().getSharedPreferences("USER_INFO", MODE_PRIVATE);
        String username = sPreferences.getString("username", "");
        if (!loginService.isLogined(getContext())) {
            textView.setText("请先登录");
            ly_login.setVisibility(View.VISIBLE);
            ly_myInfo.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            btnSign.setText("重新登录");
            textView.setText("欢迎您" + username + "， 现已登录");
            tv_name.setText(username);
            ly_myInfo.setVisibility(View.VISIBLE);
            ly_login.setVisibility(View.GONE);

        }
    }

    private Class<?> cls;
    private String title;
    private boolean isContinuousScan;

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ly_myInfo:
                intent = new Intent(getContext(), MyInfoActivity.class);
                startActivityForResult(intent,LOGOUT_CODE);
                break;
            case R.id.ly_login:
            case R.id.button_sign:
                intent = new Intent(getContext(), LoginActivity.class);
                startActivityForResult(intent, REQUEST_LOGIN);
                break;
            case R.id.menu_1://startPhotoCode();
                if (loginService.isLogined(getContext())) {
                    intent = new Intent(getContext(), MyParcelActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menu_2:   //default
//                this.cls = CaptureActivity.class;
//                this.title = ((TextView) v).getText().toString();
//                startScan(cls, title);
                if (loginService.isLogined(getContext())) {
                    intent = new Intent(getContext(), MyMessageActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menu_3:   //连续扫，+自定义高级界面
//                this.cls = CustomCaptureActivity.class;
//                this.title = ((TextView) v).getText().toString();
//                isContinuousScan = true;
//                startScan(cls, title);
                if (loginService.isLogined(getContext())) {
                    Intent startIntent = new Intent(getActivity(), MyService.class);
                    getActivity().startService(startIntent);
                    intent = new Intent(getContext(), MyLocationActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menu_4:   //自定义一般界面
//                this.cls = EasyCaptureActivity.class;
//                this.title = ((TextView) v).getText().toString();
//                startScan(cls, title);
                intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK ) {
            if (requestCode == REQUEST_LOGIN && data != null) {
                if (loginService.getUserRoll(getContext()) != -1) {
                    if (permission_Check == 1) {
                        //Log.d("MainActivity_onresume", "重新加载" + permission_Check);
                        Toast.makeText(getContext(), "登录成功, 欢迎您老司机", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "登录成功，欢迎您快递员", Toast.LENGTH_SHORT).show();
                    }
                    Log.e("lalal", "mePage 登录回显：" + permission_Check);
                    info = data.getStringExtra("info_username");
                    textView.setVisibility(View.VISIBLE);
                    btnSign.setText("重新登录/退出");
                    textView.setText("欢迎您" + info + "， 登录成功");
                    tv_name.setText(info);
                    ly_login.setVisibility(View.GONE);
                    ly_myInfo.setVisibility(View.VISIBLE);
                }
            }
            if (requestCode == LOGOUT_CODE){
                Toast.makeText(getContext(), "账号已退出", Toast.LENGTH_SHORT).show();
                textView.setText("请先登录");
                ly_login.setVisibility(View.VISIBLE);
                ly_myInfo.setVisibility(View.GONE);
            }
        }
    }
}


//    public static final int REQUEST_CODE_SCAN = 0X01;
//    public static final int REQUEST_CODE_PHOTO = 0X02;
//
//    public static final int RC_CAMERA = 0X01;
//
//    public static final int RC_READ_PHOTO = 0X02;
//
//    private static final int REQUEST_CODE = 111;

//    private void startPhotoCode() {
//        Intent pickIntent = new Intent(Intent.ACTION_PICK,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//        startActivityForResult(pickIntent, REQUEST_CODE_PHOTO);
//    }
//
//    /**
//     * 扫码
//     *
//     * @param cls
//     * @param title
//     */
//    private void startScan(Class<?> cls, String title) {
//        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(getContext(), R.anim.in, R.anim.out);
//        Intent intent = new Intent(getContext(), cls);
//        intent.putExtra(KEY_TITLE, title);
//        intent.putExtra(KEY_IS_CONTINUOUS, isContinuousScan);
//        ActivityCompat.startActivityForResult(getActivity(), intent, REQUEST_CODE_SCAN, optionsCompat.toBundle());
//    }
//    private void parsePhoto(Intent data) {
//        final String path = UriUtils.INSTANCE.getImagePath(getContext(), data);
//        Log.d("Jenly", "path:" + path);
//        if (TextUtils.isEmpty(path)) {
//            return;
//        }
//        //异步解析
//        asyncThread(new Runnable() {
//            @Override
//            public void run() {
//                final String result = CodeUtils.parseCode(path);
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d("Jenly", "result:" + result);
//                        Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//            }
//        });
//
//    }
//
//    private void asyncThread(Runnable runnable) {
//        new Thread(runnable).start();
//    }
//
//    //检查是否开启了相机权限【Android 6.0之后必须添加】
//    private void checkCameraPermission() {
//        int MY_PERMISSIONS_REQUEST_CALL_CAMERA = 100;
//        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            //如果没有授权，则请求授权
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CALL_CAMERA);
//        } else {
//            //有授权，直接开启摄像头扫描
//        }
//    }
