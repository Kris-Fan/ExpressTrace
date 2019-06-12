/*
 * Copyright (C) 2018 Jenly Yu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.extrace.util;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.extrace.net.OkHttpClientManager;
import com.extrace.net.json.MyJsonManager;
import com.extrace.ui.R;
import com.extrace.ui.entity.ExpressSheet;
import com.extrace.ui.main.CustomerManageActivity;
import com.extrace.ui.main.ExpressEditActivity;
import com.extrace.ui.main.MainActivity;
import com.extrace.ui.main.NodeActivity;
import com.extrace.ui.main.PageContentActivity;
import com.extrace.ui.main.SendExpressActivity;
import com.extrace.ui.service.EditDialog;
import com.extrace.ui.service.LoginService;
import com.extrace.ui.service.RevExpressDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.Result;
import com.king.zxing.CaptureActivity;
import com.king.zxing.ViewfinderView;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.extrace.net.OkHttpClientManager.BASE_URL;
import static com.extrace.ui.main.ScanBarcodeActivity.KEY_CODE;
import static com.extrace.ui.main.ScanBarcodeActivity.KEY_IS_CONTINUOUS;
import static com.extrace.ui.main.ScanBarcodeActivity.KEY_TITLE;
import static com.extrace.ui.main.ScanBarcodeActivity.REQUEST_CODE_SCAN_ARRIVE;
import static com.extrace.ui.main.ScanBarcodeActivity.REQUEST_CODE_SCAN_DISPATCH;
import static com.extrace.ui.main.ScanBarcodeActivity.REQUEST_CODE_SCAN_RECEIVE;
import static com.extrace.ui.main.ScanBarcodeActivity.REQUEST_CODE_SCAN_SEND;
import static com.extrace.ui.main.ScanBarcodeActivity.REQUEST_CODE_SCAN_SEND_UPLOAD;
import static com.extrace.ui.main.ScanBarcodeActivity.REQUEST_CODE_SCAN_SIGN;
import static com.extrace.ui.main.SendExpressActivity.REQUEST_CODE_SCAN_EXPRESS;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class CustomCaptureActivity extends CaptureActivity implements View.OnClickListener {

    private static final String TAG = "CustomCaptureActivity";
    private static final int REQUESTCODE_NODE = 0x200 ;
    private boolean isContinuousScan;
    private Button skip;
    private ViewfinderView viewfinderView;
    private int code,userId;
    private String packageId;

    private int screenWidth;
    private int screenHeight;
    String url=BASE_URL+"/ExtraceSystem/dabao";
    String url_upload = BASE_URL +"/ExtraceSystem/entrucking/";
    String url_dispatch = BASE_URL +"/ExtraceSystem/paijian/";
    String url_sign = BASE_URL + "/ExtraceSystem/qianshou/";
    private OkHttpClientManager okHttpClientManager;
    @Override
    public int getLayoutId() {
        return R.layout.custom_capture_activity;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Toolbar toolbar = findViewById(R.id.toolbar);
        StatusBarUtils.immersiveStatusBar(this,toolbar,0.2f);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(getIntent().getStringExtra(KEY_TITLE));
        //权限相关
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(CustomCaptureActivity.this, Manifest.permission.CAMERA )
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(CustomCaptureActivity.this,permissions,520);
        }else {
            initCapture();
        }

    }

    private void initCapture() {
        viewfinderView = findViewById(R.id.viewfinder_view);
        skip = findViewById(R.id.skip);
        skip.setOnClickListener(this);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        screenWidth= dm.widthPixels;
//        screenHeight= dm.heightPixels;

        isContinuousScan = getIntent().getBooleanExtra(KEY_IS_CONTINUOUS,false);

        code = getIntent().getIntExtra(KEY_CODE,-1);
        Log.i("lalal","code是+"+code);
        okHttpClientManager = new OkHttpClientManager(new LoginService().userInfoSha256(this));
        if (code == REQUEST_CODE_SCAN_RECEIVE){
            this.cls = ExpressEditActivity.class;
            skip.setVisibility(View.VISIBLE);
        }else if (code == REQUEST_CODE_SCAN_SEND){
            //this.cls = SendExpressActivity.class;
            skip.setVisibility(View.VISIBLE);
        }else if (code == REQUEST_CODE_SCAN_SEND_UPLOAD || code == REQUEST_CODE_SCAN_DISPATCH || code == REQUEST_CODE_SCAN_ARRIVE || code == REQUEST_CODE_SCAN_SIGN){
            skip.setVisibility(View.VISIBLE);
        }else if (code == REQUEST_CODE_SCAN_ARRIVE){
            this.cls = PageContentActivity.class;
            skip.setVisibility(View.VISIBLE);
        }else if (code == REQUEST_CODE_SCAN_EXPRESS){
            //Toast.makeText(this, "看集合集合缓解缓解", Toast.LENGTH_SHORT).show();
            userId = getIntent().getIntExtra("userId",-1);
            packageId = getIntent().getStringExtra("packageId");
            Toast.makeText(this, "小花"+String.valueOf(userId)+packageId, Toast.LENGTH_SHORT).show();

            //this.cls = SendExpressActivity.class;
            skip.setVisibility(View.VISIBLE);
        }

        getBeepManager().setPlayBeep(true);
        getBeepManager().setVibrate(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 520:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    for (int result :grantResults){
                        if (result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this, "必须所有权限才能使用本服务", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                        initCapture();
                    }
                }else {
                    Toast.makeText(this, "You Permission Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
    /**
     * 关闭闪光灯（手电筒）
     */
    private void offFlash(){
        Camera camera = getCameraManager().getOpenCamera().getCamera();
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(parameters);
    }

    /**
     * 开启闪光灯（手电筒）
     */
    public void openFlash(){
        Camera camera = getCameraManager().getOpenCamera().getCamera();
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(parameters);
    }

    /**
     * 接收扫码结果，想支持连扫时，可将{@link #isContinuousScan()}返回为{@code true}并重写此方法
     * 如果{@link #isContinuousScan()}支持连扫，则默认重启扫码和解码器；当连扫逻辑太复杂时，
     * 请将{@link #isAutoRestartPreviewAndDecode()}返回为{@code false}，并手动调用{@link #restartPreviewAndDecode()}
     * @param result 扫码结果
     */
    @Override
    public void onResult(Result result) {
        super.onResult(result);
        if(isContinuousScan){//连续扫码时，直接弹出结果
            //Toast.makeText(this, result.getText(), Toast.LENGTH_SHORT).show();
            switch (code){
                case REQUEST_CODE_SCAN_SEND:
                    break;
                case REQUEST_CODE_SCAN_EXPRESS:
                    promptBox(result.getText());
                    break;
                case REQUEST_CODE_SCAN_DISPATCH:
                case REQUEST_CODE_SCAN_SEND_UPLOAD:
                case REQUEST_CODE_SCAN_SIGN:
                    uploadExpress(result.getText());
                    break;
                case REQUEST_CODE_SCAN_ARRIVE:
                    isVaildResult(result.getText());
                    break;
            }
        }
    }

    /**
     * 包裹装货，派送，签收验证
     * @param text 代表快递/包裹单号
     */
    private void uploadExpress(final String text) {

        SharedPreferences sPreferences = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        int uid = sPreferences.getInt("uid", -1);
        if (uid == -1){
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
        }else {
            String myurl,msg;
            if (code == REQUEST_CODE_SCAN_DISPATCH){
                myurl = url_dispatch+ text + "/" + uid + "/";
                msg = "派送" ;
            }else if (code == REQUEST_CODE_SCAN_SEND_UPLOAD){
                myurl = url_upload+ text + "/" + uid + "/";
                msg = "装货" ;
            }else if (code == REQUEST_CODE_SCAN_SIGN){
                myurl = url_sign+ text ;
                msg = "签收" ;
            }else {
                msg ="快递";
                myurl = url_upload;
            }
            final String finalMsg = msg;
            Log.d(TAG, "uploadExpress: 发送链接："+myurl);
            //OkHttpClientManager okHttpClientManager = new OkHttpClientManager(new LoginService().userInfoSha256(this));
            okHttpClientManager.getAsyn(myurl,
                new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        AlertDialog.Builder builder  = new AlertDialog.Builder(CustomCaptureActivity.this);
                        builder.setTitle(finalMsg +"扫描" ) ;
                        builder.setMessage("请求错误，网络异常" ) ;
                        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                restartPreviewAndDecode();
                            }
                        });
                        builder.show();

                    }

                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, finalMsg+"respnse:" + response);
                        if (response != null) {
                            JsonObject jsonObject = (JsonObject) new JsonParser().parse(response);
                            String myMsg =jsonObject.get("msg").getAsString();
                            if ("处理失败!".equals(myMsg)) {
                                Log.e(TAG, "onResponse: 处理失败");
                                AlertDialog.Builder builder = new AlertDialog.Builder(CustomCaptureActivity.this);
                                builder.setTitle(finalMsg + "扫描");
                                builder.setMessage("处理失败，该单号不存在或已" + finalMsg + "！");
                                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        restartPreviewAndDecode();
                                    }
                                });
                                builder.show();
                                //Toast.makeText(CustomCaptureActivity.this, "处理失败！", Toast.LENGTH_SHORT).show();
                            } else if("处理成功!".equals(myMsg) || "处理成功".equals(myMsg)){
                                Log.e(TAG, "onResponse: 处理成功");
                                AlertDialog.Builder builder = new AlertDialog.Builder(CustomCaptureActivity.this);
                                builder.setTitle(finalMsg + "扫描成功");
                                builder.setMessage("请选择“是”，继续" + finalMsg + "！");
                                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        restartPreviewAndDecode();
                                    }
                                });
                                builder.show();
                            }
                            Log.d(TAG, finalMsg+"onResponse: msg:"+jsonObject.get("msg").getAsString());
                        }else {
                            AlertDialog.Builder builder  = new AlertDialog.Builder(CustomCaptureActivity.this);
                            builder.setTitle(finalMsg +"扫描" ) ;
                            builder.setMessage("处理失败，该单号无效" ) ;
                            builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    restartPreviewAndDecode();
                                }
                            });
                            builder.show();

                        }
                    }
                });
        }
    }

    /**
     * 是否连续扫码，如果想支持连续扫码，则将此方法返回{@code true}并重写{@link #onResult(Result)}
     * @return 默认返回 false
     */
    @Override
    public boolean isContinuousScan() {
        return isContinuousScan;
    }

    /**
     * 是否自动重启扫码和解码器，当支持连扫时才起作用。
     * @return 默认返回 true
     */
    @Override
    public boolean isAutoRestartPreviewAndDecode() {
        //return super.isAutoRestartPreviewAndDecode();
        return false;
    }

    private void clickFlash(View v){
        if(v.isSelected()){
            offFlash();
            v.setSelected(false);
        }else{
            openFlash();
            v.setSelected(true);
        }

    }

    public void OnClick(View v){
        switch (v.getId()){
            case R.id.ivLeft:
                onBackPressed();
                break;
            case R.id.ivFlash:
                clickFlash(v);
                break;
        }
    }

    private Class<?> cls;
    @Override
    public void onClick(View v) {
        if (code == REQUEST_CODE_SCAN_SEND_UPLOAD || code == REQUEST_CODE_SCAN_DISPATCH ||  code == REQUEST_CODE_SCAN_ARRIVE
                || code == REQUEST_CODE_SCAN_SIGN || code == REQUEST_CODE_SCAN_EXPRESS || code== REQUEST_CODE_SCAN_SEND){
            showSimpleDialog(1,"请输入单号","仅支持数字");
        }else {
            Intent intent = new Intent(CustomCaptureActivity.this, cls);
            Bundle bundle = new Bundle();
            bundle.putInt("sn",-1);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
    }

    private RevExpressDialog editDialog;// = new RevExpressDialog(this);
    private void showEditDialog(String text,String code,String msg) {
        editDialog = new RevExpressDialog(this);
        editDialog.setTitle(text);
        editDialog.setCode(code);
        editDialog.setMessage(msg);
        editDialogEvent(text);
    }


    /**
     * 包裹拆包逻辑，REQUEST_CODE_SCAN_ARRIVE（验证扫描条形码的有效性-
     */
    private boolean isVaildResult(final String string){
        SharedPreferences sharedPreferences = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        int uid = sharedPreferences.getInt("uid",-1);
        if (uid != -1) {
            //OkHttpClientManager okHttpClientManager = new OkHttpClientManager(new LoginService().userInfoSha256(this));
            okHttpClientManager.getAsyn(BASE_URL + "/ExtraceSystem/chaibao/" + string+"/"+uid,
                    new OkHttpClientManager.ResultCallback<String>() {
                        @Override
                        public void onError(Request request, Exception e) {
                            restartPreviewAndDecode();
                        }

                        @Override
                        public void onResponse(String response) {

                            Intent intent = new Intent();
                            intent.setClass(getApplicationContext(), PageContentActivity.class);
                            intent.putExtra("response", response);
                            Log.e(TAG, "Onresponse包裹拆包"+response);
                            if ("[]".equals(response)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(CustomCaptureActivity.this);
                                builder.setTitle("扫描失败");
                                builder.setMessage("处理失败，该单号不存在！");
                                builder.setPositiveButton("是", null);
                                builder.show();
                                restartPreviewAndDecode();
                            } else {
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }else {
            AlertDialog.Builder builder  = new AlertDialog.Builder(CustomCaptureActivity.this);
            builder.setTitle("包裹拆包-无权限" ) ;
            builder.setMessage("未登录！或登录信息失效" ) ;
            builder.setPositiveButton("是" ,  null );
            builder.show();
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESTCODE_NODE){
            Toast.makeText(this, "返回网点结果", Toast.LENGTH_SHORT).show();
            Log.e("lalal",data.getStringExtra("nodeName")+" "+data.getStringExtra("nodeCode"));
            showEditDialog(data.getStringExtra("expressID"),data.getStringExtra("nodeCode"),data.getStringExtra("nodeName"));
        }
    }


    private void editDialogEvent(final String text) {
        editDialog.setSkipOnclickListener("点击选择网点", new RevExpressDialog.onSkipOnclickListener() {


            @Override
            public void onSkipClick() {
                Intent intent = new Intent(getApplicationContext(), NodeActivity.class);
                intent.putExtra("express_id",text);
                startActivityForResult(intent, REQUESTCODE_NODE);
            }
        });

        editDialog.setYesOnclickListener("确定", new RevExpressDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(String phone) {
                if (TextUtils.isEmpty(phone)) {
                    //Toast.makeText(getContext(), "不可为空", Toast.LENGTH_SHORT).show();
                    //ToastUtils.showShort(getActivity(), "请输入电话号码");
                } else {
                    Toast.makeText(CustomCaptureActivity.this, phone+"***"+text, Toast.LENGTH_SHORT).show();
                    //OkHttpClientManager okHttpClientManager = new OkHttpClientManager(new LoginService().userInfoSha256(getApplicationContext()));
                    okHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<String>() {    //一定要有String 类型 否则抛出异常
                        @Override
                        public void onError(Request request, Exception e) {
                            Log.e("tag_express_edit", "post请求，错误");
                            e.printStackTrace();
                            Toast.makeText(CustomCaptureActivity.this, "提交失败：请检查网络", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(CustomCaptureActivity.this, "完成——已成功提交", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }, new OkHttpClientManager.Param[]{
                            new OkHttpClientManager.Param("expressId",text),
                    });
                }
                restartPreviewAndDecode();
            }
        });
        editDialog.setNoOnclickListener("取消", new RevExpressDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                editDialog.dismiss();
                restartPreviewAndDecode();
            }
        });
        editDialog.show();
    }


    /**
     * 显示简单对话框，一个编辑框
     */
    private void showSimpleDialog(int i, String title, String hint) {
        //点击弹出对话框
        final EditDialog editDialog = new EditDialog(this);
        editDialog.setTitle(title);
        editDialog.setHintStr(hint);
        if (i == 2){
            //editDialog.findViewById(R.id.et_phone).setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            editDialog.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        }
        editDialog.setYesOnclickListener("确定", new EditDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(String phone) {
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getApplicationContext(), "不可为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (code == REQUEST_CODE_SCAN_ARRIVE){
                        isVaildResult(phone);
                    }else if(code == REQUEST_CODE_SCAN_SEND){
                        isExist(phone);
                    }else if(code == REQUEST_CODE_SCAN_EXPRESS){
                        //addExpress(phone);
                        promptBox(phone);
                    }else {
                        uploadExpress(phone);
                    }
                    editDialog.dismiss();
                    //让软键盘隐藏
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(getView().getApplicationWindowToken(), 0);
                }
            }
        });
        editDialog.setNoOnclickListener("取消", new EditDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                editDialog.dismiss();
            }
        });
        editDialog.show();
    }
    private void promptBox(final String expressId){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final android.support.v7.app.AlertDialog.Builder normalDialog =
                new android.support.v7.app.AlertDialog.Builder(CustomCaptureActivity.this);
        normalDialog.setIcon(R.mipmap.icon_upload);
        normalDialog.setTitle("确定添加？");
        normalDialog.setMessage("快件编号："+expressId);
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uploadExpressInfo(expressId);
                        //Toast.makeText(CustomCaptureActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                        restartPreviewAndDecode();
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restartPreviewAndDecode();
                    }
                });
        // 显示
        normalDialog.show();
    }
    //包裹打包-添加快件时上传信息
    private void uploadExpressInfo(String expressId) {
        if (!new LoginService().isLogined(this)){
            Toast.makeText(this, "未登录或登陆信息失效，请登陆后再进行操作", Toast.LENGTH_SHORT).show();
        }else {

            okHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<String>() {    //一定要有String 类型 否则抛出异常
                @Override
                public void onError(Request request, Exception e) {
                    Log.e("tag_express_edit", "post请求，错误");
                    e.printStackTrace();
                    Toast.makeText(CustomCaptureActivity.this, "提交失败：请检查网络", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(String response) {
                    Toast.makeText(CustomCaptureActivity.this, "完成——已成功提交", Toast.LENGTH_SHORT).show();
                    //finish();
                }
            }, new OkHttpClientManager.Param[]{
                    new OkHttpClientManager.Param("userId",new LoginService().getUserId(this)+""),
                    new OkHttpClientManager.Param("packageId",packageId),
                    new OkHttpClientManager.Param("expressId",expressId),
            });
        }
    }
    //包裹打包时验证手动输入的包裹号是否存在
    private boolean isExist(final String string){
        //Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
        okHttpClientManager.getAsyn(BASE_URL +"/ExtraceSystem/queryPackage/"+string,
                new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        Toast.makeText(getApplicationContext(), "出错了", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(), SendExpressActivity.class);
                        intent.putExtra("response", response);
                        Log.e("lalal_CustomCapture",response);
                        if ("{}".equals(response)){
                            AlertDialog.Builder builder  = new AlertDialog.Builder(CustomCaptureActivity.this);
                            builder.setTitle("扫描失败" ) ;
                            builder.setMessage("处理失败，该单号不存在！" ) ;
                            builder.setPositiveButton("是" ,  null );
                            builder.show();
                            //restartPreviewAndDecode();
                        }else {
                            startActivity(intent);
                            finish();
                        }
                    }
                });

        return false;
    }
}
