package com.extrace.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.extrace.net.OkHttpClientManager;
import com.extrace.net.json.MyJsonManager;
import com.extrace.ui.R;
import com.extrace.ui.entity.CustomerInfo;
import com.extrace.ui.entity.ExpressSheet;
import com.extrace.ui.service.EditDialog;
import com.extrace.util.CustomCaptureActivity;
import com.extrace.util.EmptyView;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;

import static com.baidu.mapapi.BMapManager.getContext;
import static com.extrace.net.OkHttpClientManager.BASE_URL;

/**
 * 发件扫描，打包扫描
 */
public class SendExpressActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SendExpressActivity";
    private static final int REQUESTCODE_NODE = 0x200 ;
    private TextView expressId,rev_addr,next_node_code;
    private ImageView copy_exId;
    private EditText next_node;
    private LinearLayout postInfo;
    private String url=BASE_URL+"/ExtraceSystem/dabao";
    private String urlEx = BASE_URL +"/ExtraceSystem/expresssheet/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_express);
        expressId = findViewById(R.id.title);
        rev_addr = findViewById(R.id.rev_addr);
        next_node = findViewById(R.id.next_node);
        next_node_code = findViewById(R.id.next_node_code);
        postInfo = findViewById(R.id.ly_save);
        copy_exId = findViewById(R.id.action_ex_capture_icon);

        bindView();
        bindEvent();

    }

    private void bindEvent() {
        next_node.setFocusable(false);
        next_node.setFocusableInTouchMode(false);
        next_node.setOnClickListener(this);
        postInfo.setOnClickListener(this);
        copy_exId.setOnClickListener(this);
    }
    private String mExpressId;
    private void bindView() {
        Intent intent = getIntent();
        mExpressId = intent.getStringExtra("EXPRESS_ID");
        //Log.e("lalal","快递编辑show"+mExpressId);
        if (mExpressId != null) {
            expressId.setText(mExpressId);
            //createBarCode(mExpressId);
            loadData(urlEx);
        } else {
            Toast.makeText(this, "未成功识别条形码，请返回重写", Toast.LENGTH_SHORT).show();
            showEditDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESTCODE_NODE){
            Toast.makeText(this, "返回网点结果", Toast.LENGTH_SHORT).show();
            Log.e("lalal",data.getStringExtra("nodeName")+" "+data.getStringExtra("nodeCode"));
            next_node.setText(data.getStringExtra("nodeName")+" "+data.getStringExtra("nodeCode"));
            next_node_code.setText(data.getStringExtra("nodeCode"));
        }
    }

    private String data;
    private void loadData(final String url) {

        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... params) {
                try {
                    Response response = OkHttpClientManager.getAsyn(url + mExpressId);
                    Log.e("lalal","sendex：运单id："+mExpressId);
                    if (response.code() == 200) {
                        data = response.body().string();
                    } else {
                        data = "";
                        Log.e("lalal","请求响应失败：code="+response.code());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return data;
            }
            @Override
            protected void onPostExecute(String s) {

                if (s!=null && !s.isEmpty()) {
                    ExpressSheet expressSheet = new MyJsonManager().ExpressSheetInfo(s);
                    if (expressSheet == null){
                        Toast.makeText(SendExpressActivity.this, "无此快递信息！！", Toast.LENGTH_SHORT).show();

                    }else  {
                        Log.e("lalal", "请求响应成功，数据：" + s + " \n" + expressSheet.getRecever());
                        rev_addr.setText(String.valueOf(expressSheet.getRecever()));
                        showCustomerAddr(expressSheet.getRecever());
                    }
                }else {
                    //emptyView.setErrorType(EmptyView.NETWORK_ERROR);//错误页面
                    Toast.makeText(SendExpressActivity.this, "无此快递单信息 或网络限制", Toast.LENGTH_SHORT).show();
                    Log.e("lalal","请求错误，数据："+s);
                }
            }
        }.execute();
    }

    private void showCustomerAddr(Integer recever) {
        String cust_data = null;
        Log.e("lala","sendex客户id："+String.valueOf(recever));
        OkHttpClientManager.getAsyn(BASE_URL + "/ExtraceSystem/customerInfo/" + String.valueOf(recever), new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                rev_addr.setText("收件人地址：");
                Log.e("lala","包裹打包：获取失败：客户地址无");
            }

            @Override
            public void onResponse(String response) {
                Log.e("lala","包裹打包：显示客户地址："+response);
                CustomerInfo customerInfo = new MyJsonManager().CustomerInfoJson(response);
                if (customerInfo == null){
                    Toast.makeText(SendExpressActivity.this, "客户地址无法获取！请手动确认", Toast.LENGTH_SHORT).show();
                }else
                rev_addr.setText(customerInfo.getAddress()+" "+customerInfo.getDepartment());
            }
        });

    }

    private void showEditDialog() {
        //点击弹出对话框
        final EditDialog editDialog = new EditDialog(this);
        editDialog.setTitle("请输入快递单号");
        editDialog.setYesOnclickListener("确定", new EditDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(String phone) {
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getContext(), "不可为空", Toast.LENGTH_SHORT).show();
                } else {
//                    editors.putString("phone", phone);
//                    editors.commit();
                    expressId.setText(phone);
                    mExpressId=phone;
                    loadData(urlEx);
                    //createBarCode(phone);
                    editDialog.dismiss();
                    //让软键盘隐藏
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
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

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.next_node:
                intent = new Intent(SendExpressActivity.this, NodeActivity.class);
                startActivityForResult(intent, REQUESTCODE_NODE);
                break;
            case R.id.action_ex_capture_icon:
                showEditDialog();
                break;
            case R.id.ly_save:
                attempSubmit();
                break;
        }
    }

    private void attempSubmit() {
        if (mExpressId == null || expressId.getText().toString().equals("")){
            showEditDialog();
        }else if ("收件人地址：".equals(rev_addr.getText().toString())){
            Toast.makeText(this, "快递单号无效", Toast.LENGTH_SHORT).show();

        }else if (next_node_code.getText().toString().isEmpty() ||next_node_code.getText().equals("点击选择网点")){
            Toast.makeText(this, "运往网点未选择！", Toast.LENGTH_SHORT).show();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(SendExpressActivity.this);
            builder.setMessage("确定提交？");
            builder.setTitle("运往：" + next_node.getText().toString());

            //添加AlterDialog.Builder对象的setPositiveButton()方法
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    uploadExpressInfo();
                }
            });

            //添加AlterDialog.Builder对象的setNegativeButton()方法
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.create().show();
        }
    }

    private void uploadExpressInfo() {
        SharedPreferences sPreferences = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        int uid = sPreferences.getInt("uid", -1);
        boolean keepLoginSta = sPreferences.getBoolean("keepLoginSta",false);
        Log.d(TAG, "uploadExpressInfo: 验证登录信息：uid="+uid+" keeplogin:"+keepLoginSta);
        if (!keepLoginSta || uid == -1){
            Toast.makeText(this, "未登录或登陆信息失效，请登陆后再进行操作", Toast.LENGTH_SHORT).show();
        }else {

            OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<String>() {    //一定要有String 类型 否则抛出异常
                @Override
                public void onError(Request request, Exception e) {
                    Log.e("tag_express_edit", "post请求，错误");
                    e.printStackTrace();
                    Toast.makeText(SendExpressActivity.this, "提交失败：请检查网络", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(String response) {
                    Toast.makeText(SendExpressActivity.this, "完成——已成功提交", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }, new OkHttpClientManager.Param[]{
                    new OkHttpClientManager.Param("userId",String.valueOf(uid)),
                    new OkHttpClientManager.Param("expressId",expressId.getText().toString()),
                    new OkHttpClientManager.Param("expressNodeCode",next_node_code.getText().toString()),
            });
        }
    }
}
