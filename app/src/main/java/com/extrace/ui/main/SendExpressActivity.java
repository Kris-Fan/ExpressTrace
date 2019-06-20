package com.extrace.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.extrace.ui.adapter.WrapPackageAdapter;
import com.extrace.ui.entity.CustomerInfo;
import com.extrace.ui.entity.ExpressSheet;
import com.extrace.ui.service.EditDialog;
import com.extrace.ui.service.LoginService;
import com.extrace.util.CustomCaptureActivity;
import com.extrace.util.EmptyView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.king.zxing.Intents;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.baidu.mapapi.BMapManager.getContext;
import static com.extrace.net.OkHttpClientManager.BASE_URL;

/**
 * 发件扫描，打包扫描
 */
public class SendExpressActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerview;
    private WrapPackageAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<String> list;
    private static final int REQUESTCODE_NODE = 0x200 ;
    public static final int REQUEST_CODE_SCAN_EXPRESS = 0X112; //快件装进包裹的码
    private TextView packageId, rev_addr;
    private ImageView copy_exId;
    private  int uid;
    private String url=BASE_URL+"/ExtraceSystem/dabao";
    private static final String TAG = "SendExpressActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_express);
        packageId = findViewById(R.id.title);
        rev_addr = findViewById(R.id.rev_addr);
        copy_exId = findViewById(R.id.action_ex_capture_icon);
        SharedPreferences sPreferences = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        uid = sPreferences.getInt("uid", -1);
        Intent intent=getIntent();
        String response = intent.getStringExtra("response");
        Log.d(TAG, "onCreate: "+response);
        bindEvent();
        init(response);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.scan, menu);
        return super.onCreateOptionsMenu(menu);
    }
    //点击新建按钮转到添加客户信息界面
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.scan:
                startScan(CustomCaptureActivity.class,"包裹打包",REQUEST_CODE_SCAN_EXPRESS);
                break;
            default:
                break;
        }
        return true;
    }
    public void init(String response){

        //response ="{\"快件列表\":[{\"id\":\"520\",\"type\":12,\"sender\":1,\"recever\":3,\"weight\":20.0,\"tranfee\":1.0,\"packagefee\":0.0,\"insufee\":0.0,\"accepter\":\"13\",\"deliver\":\"13\",\"acceptetime\":\"2019-05-23 10:24:55\",\"delivetime\":\"2019-05-23 11:05:59\",\"acc1\":\"暂无\",\"acc2\":\"暂无\",\"status\":\"已签收\"},{\"id\":\"521\",\"type\":0,\"sender\":3,\"recever\":1,\"weight\":2.0,\"tranfee\":2.0,\"packagefee\":0.0,\"insufee\":0.0,\"accepter\":\"13\",\"deliver\":\"0\",\"acceptetime\":\"2019-05-23 10:32:45\",\"delivetime\":null,\"acc1\":\"暂无\",\"acc2\":\"暂无\",\"status\":\"转运中\"},{\"id\":\"524\",\"type\":12,\"sender\":1,\"recever\":3,\"weight\":20.0,\"tranfee\":1.0,\"packagefee\":0.0,\"insufee\":0.0,\"accepter\":\"13\",\"deliver\":\"13\",\"acceptetime\":\"2019-05-23 10:24:55\",\"delivetime\":\"2019-05-23 11:05:59\",\"acc1\":\"暂无\",\"acc2\":\"暂无\",\"status\":\"已签收\"},{\"id\":\"524\",\"type\":12,\"sender\":1,\"recever\":3,\"weight\":20.0,\"tranfee\":1.0,\"packagefee\":0.0,\"insufee\":0.0,\"accepter\":\"13\",\"deliver\":\"13\",\"acceptetime\":\"2019-05-23 10:24:55\",\"delivetime\":\"2019-05-23 11:05:59\",\"acc1\":\"暂无\",\"acc2\":\"暂无\",\"status\":\"已签收\"},{\"id\":\"524\",\"type\":12,\"sender\":1,\"recever\":3,\"weight\":20.0,\"tranfee\":1.0,\"packagefee\":0.0,\"insufee\":0.0,\"accepter\":\"13\",\"deliver\":\"13\",\"acceptetime\":\"2019-05-23 10:24:55\",\"delivetime\":\"2019-05-23 11:05:59\",\"acc1\":\"暂无\",\"acc2\":\"暂无\",\"status\":\"已签收\"},{\"id\":\"524\",\"type\":12,\"sender\":1,\"recever\":3,\"weight\":20.0,\"tranfee\":1.0,\"packagefee\":0.0,\"insufee\":0.0,\"accepter\":\"13\",\"deliver\":\"13\",\"acceptetime\":\"2019-05-23 10:24:55\",\"delivetime\":\"2019-05-23 11:05:59\",\"acc1\":\"暂无\",\"acc2\":\"暂无\",\"status\":\"已签收\"},{\"id\":\"524\",\"type\":12,\"sender\":1,\"recever\":3,\"weight\":20.0,\"tranfee\":1.0,\"packagefee\":0.0,\"insufee\":0.0,\"accepter\":\"13\",\"deliver\":\"13\",\"acceptetime\":\"2019-05-23 10:24:55\",\"delivetime\":\"2019-05-23 11:05:59\",\"acc1\":\"暂无\",\"acc2\":\"暂无\",\"status\":\"已签收\"},{\"id\":\"524\",\"type\":12,\"sender\":1,\"recever\":3,\"weight\":20.0,\"tranfee\":1.0,\"packagefee\":0.0,\"insufee\":0.0,\"accepter\":\"13\",\"deliver\":\"13\",\"acceptetime\":\"2019-05-23 10:24:55\",\"delivetime\":\"2019-05-23 11:05:59\",\"acc1\":\"暂无\",\"acc2\":\"暂无\",\"status\":\"已签收\"}],\"源点\":\"金水区网点\",\"装车状态\":\"已装车\",\"创建时间\":1558607513000,\"包裹号\":\"155860751278141010500\",\"终点\":\"东明县网点\"}";
        //Json的解析类对象
        JsonParser parser = new JsonParser();
        //将JSON的String 转成一个JsonArray对象
        JSONObject jsonObject = null;
        ArrayList<ExpressSheet> jsonBeanArrayList = new ArrayList<>();
        try {
            jsonObject = new JSONObject(response);
            //JSONObject result = json.getJSONObject("快件列表");
            String packId = jsonObject.getString("包裹号");
            String destination = jsonObject.getString("终点");
            packageId.setText(packId);
            rev_addr.setText(destination);
            JSONArray data = jsonObject.getJSONArray("快件列表");
            JsonArray jsonArray = parser.parse(data.toString()).getAsJsonArray();
            Gson gson = new Gson();
            //循环遍历json数组
            for (JsonElement json : jsonArray) {
                //使用GSON，转成Bean对象
                ExpressSheet jsonBean = gson.fromJson(json, ExpressSheet.class);
                jsonBeanArrayList.add(jsonBean);
                //Toast.makeText(this, String.valueOf(jsonBean.getId()), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        recyclerview = findViewById(R.id.wrap_package);
        mAdapter = new WrapPackageAdapter(jsonBeanArrayList,this);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setAdapter(mAdapter);
        //recyclerview.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }
    private void bindEvent() {
        copy_exId.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.next_node:
                intent = new Intent(SendExpressActivity.this, NodeActivity.class);
                startActivityForResult(intent, REQUESTCODE_NODE);
                break;
        }
    }
    /**
     * 扫码
     * @param cls
     * @param title
     */
    private void startScan(Class<?> cls,String title, int code){
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(this,R.anim.in,R.anim.out);
        Intent intent = new Intent(this, cls);
        intent.putExtra("key_title",title);
        intent.putExtra("key_code",code);
        intent.putExtra("key_continuous_scan",true);
        intent.putExtra("userId",uid);
        intent.putExtra("packageId",packageId.getText());
        ActivityCompat.startActivityForResult(this,intent,code,optionsCompat.toBundle());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data!=null) {
            switch (requestCode) {
                case REQUEST_CODE_SCAN_EXPRESS: //快件扫描结果
                    String result = data.getStringExtra(Intents.Scan.RESULT);
                    //Toast.makeText(this,"扫描到了："+result,Toast.LENGTH_SHORT).show();
                    //promptBox(result);
                    break;
            }
        }
    }
    private boolean queryPackage(){
        String string = packageId.getText().toString();
        //Toast.makeText(getApplicationContext(), "刷新"+string, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
        if(string.equals("")){
            return false;
        }else {
            OkHttpClientManager okHttpClientManager = new OkHttpClientManager(new LoginService().userInfoSha256(this));
            okHttpClientManager.getAsyn(BASE_URL + "/ExtraceSystem/queryPackage/" + string,
                    new OkHttpClientManager.ResultCallback<String>() {
                        @Override
                        public void onError(Request request, Exception e) {
                            Toast.makeText(getApplicationContext(), "出错了", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onResponse(String response) {
                            //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                            if ("".equals(response)) {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SendExpressActivity.this);
                                builder.setTitle("扫描失败");
                                builder.setMessage("处理失败，该单号不存在！");
                                builder.setPositiveButton("是", null);
                                builder.show();
                            } else {
                                init(response);
                            }
                        }
                    });

        }
        return false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        queryPackage();//刷新数据
    }

}