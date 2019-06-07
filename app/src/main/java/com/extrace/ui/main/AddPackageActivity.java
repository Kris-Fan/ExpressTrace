package com.extrace.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.extrace.net.OkHttpClientManager;
import com.extrace.ui.R;
import com.google.zxing.BarcodeFormat;
import com.king.zxing.util.CodeUtils;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import static com.extrace.net.OkHttpClientManager.BASE_URL;

public class AddPackageActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUESTCODE_NODE = 0x200 ;
    private TextView userId,next_node_code,package_barcode;
    private EditText next_node;
    private ImageView ivCode;
    private LinearLayout postInfo;
    private String url=BASE_URL+"/ExtraceSystem/addPackage";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_package);
        next_node = findViewById(R.id.next_node);
        next_node_code = findViewById(R.id.next_node_code);
        postInfo = findViewById(R.id.ly_save);
        userId = findViewById(R.id.title);
        ivCode = findViewById(R.id.ivCode);
        package_barcode = findViewById(R.id.package_barcode);

        SharedPreferences sPreferences = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        int uid = sPreferences.getInt("uid", -1);
        userId.setText(String.valueOf(uid));

        bindEvent();
    }
    private void bindEvent() {
        next_node.setFocusable(false);
        next_node.setFocusableInTouchMode(false);
        next_node.setOnClickListener(this);
        postInfo.setOnClickListener(this);
        ivCode.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.next_node:
                intent = new Intent(AddPackageActivity.this, NodeActivity.class);
                startActivityForResult(intent, REQUESTCODE_NODE);
                break;
            case R.id.ly_save:
                attempSubmit();
                break;
        }
    }
    private void attempSubmit() {
        if (next_node_code.getText().toString().isEmpty() ||next_node_code.getText().equals("点击选择网点")){
            Toast.makeText(this, "运往网点未选择！", Toast.LENGTH_SHORT).show();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddPackageActivity.this);
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

        Toast.makeText(this, String.valueOf(uid), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, next_node_code.getText().toString(), Toast.LENGTH_SHORT).show();
        if (uid == -1){
            Toast.makeText(this, "未登录或登陆信息失效，请登陆后再进行操作", Toast.LENGTH_SHORT).show();
        }else {

            OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<String>() {    //一定要有String 类型 否则抛出异常
                @Override
                public void onError(Request request, Exception e) {
                    Log.e("tag_express_edit", "post请求，错误");
                    e.printStackTrace();
                    Toast.makeText(AddPackageActivity.this, "提交失败：请检查网络", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(String response) {
                    Log.i("res",response);
                    Toast.makeText(AddPackageActivity.this, response, Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONObject extend = json.getJSONObject("extend");
                        String packageId = extend.getString("packageId");
                        //生成条形码最好放子线程生成防止阻塞UI，这里只是演示
                        Bitmap bitmap = CodeUtils.createBarCode(packageId, BarcodeFormat.CODE_128,760,160,null,true);
                        //显示条形码
                        ivCode.setImageBitmap(bitmap);
                        //postInfo.setVisibility(View.INVISIBLE);
                        package_barcode.setVisibility(View.VISIBLE);
                        Toast.makeText(AddPackageActivity.this, packageId, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //finish();
                }
            }, new OkHttpClientManager.Param[]{
                    new OkHttpClientManager.Param("userId",String.valueOf(uid)),
                    new OkHttpClientManager.Param("expressNodeCode",next_node_code.getText().toString()),
            });
        }
    }
}
