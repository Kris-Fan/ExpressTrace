package com.extrace.ui.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.Toast;

import com.extrace.ui.R;
import com.extrace.ui.main.CallActivity;
import com.extrace.ui.main.CustomerEditActivity;
import com.extrace.ui.main.CustomerManageActivity;
import com.extrace.ui.main.ExpressSearchActivity;
import com.extrace.ui.main.LoginActivity;
import com.extrace.ui.main.ScanBarcodeActivity;
import com.extrace.ui.service.LoginService;
import com.extrace.util.CustomCaptureActivity;
import com.extrace.util.UriUtils;
import com.king.zxing.util.CodeUtils;

import static com.extrace.ui.main.MainActivity.KEY_IS_CONTINUOUS;
import static com.extrace.ui.main.MainActivity.KEY_TITLE;
import static com.extrace.ui.main.ScanBarcodeActivity.KEY_CODE;
import static com.extrace.ui.main.ScanBarcodeActivity.REQUEST_CODE_SCAN_SEND_UPLOAD;

public class MainMenuFragment extends Fragment implements View.OnClickListener {
    public static final int REQUEST_CODE_SCAN = 0X01;
    public static final int REQUEST_CODE_PHOTO = 0X02;

    public static final int RC_CAMERA = 0X01;

    public static final int RC_READ_PHOTO = 0X02;

    private static final int REQUEST_CODE = 111;

    //private ConstraintLayout menuConstraint;
    private LinearLayout ly_scan, ly_add, ly_search;
    private LinearLayout menu_1,menu_2,menu_3,menu_4,driver_menu_1,driver_menu_2,driver_menu_3;
    private RelativeLayout menu_5,driver_home_menus;
    private LinearLayout menu_6,home_menus;
    private LoginService loginService = new LoginService();
    private static final String TAG = "MainMenuFragment";

    public MainMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_main_menu, container, false);

        initView(view);
        Log.e("MainActivity_oncreat","oncreate调用");
        //bindView(view);
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getActivity().getIntent();
        initView(getView());
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
        home_menus = view.findViewById(R.id.home_menu);
        driver_home_menus = view.findViewById(R.id.driver_home_menu);
        if (new LoginService().getUserRoll(getContext()) == 0) { //快递员
            ly_scan = view.findViewById(R.id.ly_tab_menu3);
            ly_add = view.findViewById(R.id.ly_tab_menu2);
            ly_search = view.findViewById(R.id.ly_tab_menu1);
            menu_1 = view.findViewById(R.id.ly_function1);
            menu_2 = view.findViewById(R.id.ly_function2);
            menu_3 = view.findViewById(R.id.ly_function3);
            menu_4 = view.findViewById(R.id.ly_function4);
            menu_5 = view.findViewById(R.id.rl_function5);
            menu_6 = view.findViewById(R.id.ly_function6);
            home_menus.setVisibility(View.VISIBLE);
            driver_home_menus.setVisibility(View.GONE);
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
                case R.id.ly_function4:
                case R.id.driver_tab_menu2: //消息
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

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == RESULT_OK && data!=null){
//            switch (requestCode){
//                case REQUEST_CODE_SCAN:
//                    String result = data.getStringExtra(Intents.Scan.RESULT);
//                    Toast.makeText(getContext(),"扫描到了："+result,Toast.LENGTH_SHORT).show();
//                    break;
//                case REQUEST_CODE_PHOTO:
//                    parsePhoto(data);
//                    break;
//            }
//
//        }
//    }

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
}
