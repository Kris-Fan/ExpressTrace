package com.extrace.ui.main;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.extrace.net.OkHttpClientManager;
import com.extrace.ui.R;
import com.extrace.ui.adapter.FmPagerAdapter;
import com.extrace.ui.fragment.ExpressEditAdvanceInfoFragment;
import com.extrace.ui.fragment.ExpressEditBaseInfoFragment;
import com.extrace.util.CustomCaptureActivity;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.extrace.net.OkHttpClientManager.BASE_URL;

public class ExpressEditActivity extends AppCompatActivity{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FmPagerAdapter pagerAdapter;
    private LinearLayout postInfo;

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private String[] titles = new String[]{"基础信息","拓展信息"};
    String url=BASE_URL+"/ExtraceSystem/addExpSheet";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express_edit);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("收件编辑");
        }
        initView();
        bindView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        postInfo = findViewById(R.id.ly_save);

        fragments.add(new ExpressEditBaseInfoFragment());
        fragments.add(new ExpressEditAdvanceInfoFragment());
        for(int i=0;i<titles.length;i++){
            tabLayout.addTab(tabLayout.newTab());
        }
        tabLayout.setupWithViewPager(viewPager,false);
        pagerAdapter = new FmPagerAdapter(fragments,getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        for(int i=0;i<titles.length;i++){
            tabLayout.getTabAt(i).setText(titles[i]);
        }

    }
    private TextView tv_exId,tv_exStatus,tv_time;
    private TextView tv_exRevTel,tv_exSendTel;
    private TextView expressType,expressWeight,expressFee,expressInsureFee;
    private void bindView() {
        postInfo.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempSubmit();
            }
        });
    }

    private boolean attempSubmit() {
        tv_exId = getSupportFragmentManager().getFragments().get(0).getView().findViewById(R.id.expressId);
        tv_exRevTel = getSupportFragmentManager().getFragments().get(0).getView().findViewById(R.id.expressRcvID);

        tv_exSendTel = getSupportFragmentManager().getFragments().get(0).getView().findViewById(R.id.expressSndID);
        tv_time = getSupportFragmentManager().getFragments().get(1).getView().findViewById(R.id.expressAccTime);
        tv_exStatus = getSupportFragmentManager().getFragments().get(1).getView().findViewById(R.id.expressStatus);

        expressType = getSupportFragmentManager().getFragments().get(0).getView().findViewById(R.id.expressType);
        expressInsureFee = getSupportFragmentManager().getFragments().get(0).getView().findViewById(R.id.expressInsureFee);
        expressFee = getSupportFragmentManager().getFragments().get(0).getView().findViewById(R.id.expressFee);
        expressWeight = getSupportFragmentManager().getFragments().get(0).getView().findViewById(R.id.expressWeight);

        if (tv_exId.getText().toString().isEmpty()){
            Toast.makeText(this, "快递单号未填写", Toast.LENGTH_SHORT).show();
        }else if (tv_exSendTel.getText().toString().isEmpty() || tv_exSendTel.getText().toString().isEmpty()){
            Toast.makeText(this, "收发件人信息未录入", Toast.LENGTH_SHORT).show();
        }else if (expressFee.getText().toString().isEmpty() || expressWeight.getText().toString().isEmpty() || expressType.getText().toString().isEmpty()){
            Toast.makeText(this, "请完善物品信息", Toast.LENGTH_SHORT).show();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ExpressEditActivity.this);
            builder.setMessage("确定提交？");
            builder.setTitle("快递信息");

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
        return false;
    }

    private void uploadExpressInfo() {
        SharedPreferences sPreferences = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        int uid = sPreferences.getInt("uid", -1);
        if (uid == -1){
            Toast.makeText(this, "未登录或登陆信息失效，请登陆后再进行操作", Toast.LENGTH_SHORT).show();
        }else {
            Map<String, String> map = new HashMap<>();
            map.put("uid",String.valueOf(uid));     //快递员id
            map.put("expressId", tv_exId.getText().toString());  //快递id;
            map.put("expressSndId", tv_exSendTel.getText().toString()); //发件人id
            map.put("expressRcvId", tv_exRevTel.getText().toString());  //收件人id
            map.put("expressAccTime", tv_time.getText().toString());    //接受时间
            map.put("expressStatus", tv_exStatus.getText().toString()); //状态

            map.put("expressType", expressType.getText().toString());   //物品类型
            map.put("expressWeight", expressWeight.getText().toString());   //重量
            map.put("expressFee", expressFee.getText().toString()); //费用
            map.put("expressInsureFee", expressInsureFee.getText().toString());//保险费用

            try {
                Log.e("tag_express_edit", "post请求" + tv_exId.getText().toString());
                OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<String>() {    //一定要有String 类型 否则抛出异常
                    @Override
                    public void onError(Request request, Exception e) {
                        Log.e("tag_express_edit", "post请求，错误");
                        e.printStackTrace();
                        Toast.makeText(ExpressEditActivity.this, "提交失败：请检查网络", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.e("lalal","ExpressEdit:respnse:"+response);
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(response);
                        Log.e("lalal","respnse:"+ jsonObject.get("msg").getAsString());
                        if("处理失败!".equals(jsonObject.get("msg").getAsString())){
                            android.app.AlertDialog.Builder builder  = new android.app.AlertDialog.Builder(ExpressEditActivity.this);
                            builder.setTitle("快件揽收-处理失败" ) ;
                            builder.setMessage("可能原因：快件单号冲突！" ) ;
                            builder.setPositiveButton("是" ,  null );
                            builder.show();
                        }else {
                            Toast.makeText(ExpressEditActivity.this, "完成——已成功提交", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }, map);
            } catch (Exception e) {
                Log.e("lalal_tag_express_edit", "错误 lalla！" + e.toString());
                e.printStackTrace();
            }
        }
    }


    //返回页面
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
