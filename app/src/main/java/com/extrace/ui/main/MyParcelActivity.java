package com.extrace.ui.main;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.extrace.net.OkHttpClientManager;
import com.extrace.net.json.MyJsonManager;
import com.extrace.ui.R;
import com.extrace.ui.adapter.MyHistoryParcelAdapter;
import com.extrace.ui.adapter.RevTaskRecyclerAdapter;
import com.extrace.ui.entity.CustomerInfo;
import com.extrace.ui.entity.ExpressSheet;
import com.extrace.util.EmptyView;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.extrace.net.OkHttpClientManager.BASE_URL;

public class MyParcelActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MyHistoryParcelAdapter mAdapter;
    private LinearLayoutManager mLinearManager;

    private EmptyView emptyView;

    //获取的json数据
    public  String date;
    public  String title;
    public List<ExpressSheet> list;
    public String url=BASE_URL+"/ExtraceSystem/getExpresseetByAccepter/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_parcel);
        mRecyclerView = findViewById(R.id.recy);
        emptyView = findViewById(R.id.empty);

        //设置布局管理器
        mLinearManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearManager);
        //设置数据源
        list = new ArrayList<>();
        //设置适配器
        mAdapter = new MyHistoryParcelAdapter(list,this);
        mRecyclerView.setAdapter(mAdapter);

        loadData(url);
        emptyView.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(url);
            }
        });
    }

    private void loadData(final String url) {

        emptyView.setErrorType(EmptyView.NETWORK_LOADING);
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... params) {
                try {

                    SharedPreferences sharedPreferences = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                    int uid = sharedPreferences.getInt("uid",-1);
                    if (uid == -1) {
                        date = "";
                        Log.e("lalal","拒绝服务uid="+uid);
                        handler.sendEmptyMessage(500);
                    }else {
                        Response response = OkHttpClientManager.getAsyn(url + uid);
                        if (response.code() == 200) {
                            date = response.body().string();
                        } else {
                            date = "";
                            Log.e("lalal","请求响应失败：code="+response.code());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return date;
            }
            @Override
            protected void onPostExecute(String s) {

                if (s!=null && !s.isEmpty()) {
                    List<ExpressSheet> tempList = (new MyJsonManager()).historyPackageJsonJXData(s);
//                    Log.e("lalala","快递任务RevTask"+tempList.toString());
                    Log.e("lalal","请求响应成功，数据："+s);
                    if (!tempList.isEmpty()) {
                        list.addAll(tempList);
                        handler.sendEmptyMessage(0);
                    }else {
                        emptyView.setErrorType(EmptyView.NODATA);//无数据页面
                    }
                }else {
                    emptyView.setErrorType(EmptyView.NETWORK_ERROR);//错误页面
                }
            }
        }.execute();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            switch (msg.what){
                case 0://提示更新数据
                    emptyView.setErrorType(EmptyView.HIDE_LAYOUT);
                    mAdapter.notifyDataSetChanged();
                    break;
                case 500://提示更新数据
                    emptyView.setErrorType(EmptyView.NODATA);
                    Toast.makeText(MyParcelActivity.this, "拒绝服务，未登录或登录信息失效", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };
}
