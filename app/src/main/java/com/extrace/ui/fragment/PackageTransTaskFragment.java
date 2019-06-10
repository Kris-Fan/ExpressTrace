package com.extrace.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.extrace.net.OkHttpClientManager;
import com.extrace.net.json.MyJsonManager;
import com.extrace.ui.R;
import com.extrace.ui.adapter.PackageTansRecyclerAdapter;
import com.extrace.ui.adapter.RecyclerAdapter;
import com.extrace.ui.service.LoginService;
import com.extrace.util.EmptyView;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.extrace.net.OkHttpClientManager.BASE_URL;
/*
/转运任务
 */
public class PackageTransTaskFragment extends Fragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private PackageTansRecyclerAdapter mAdapter;
    private LinearLayoutManager mLinearManager;

    private EmptyView emptyView;
    private Button finished;
    private Button unfinished;
    //获取的json数据
    public  String date;
    public  String title;
    public List<Map<String,Object>> list;
    public String url=BASE_URL+"/ExtraceSystem/customerInfos";
    OkHttpClientManager okHttpClientManager;
    private static final String TAG = "PackageTransTaskFragmen";
    private int uid;//用户id
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_package_trans_task, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        emptyView = view.findViewById(R.id.empty);

        finished = view.findViewById(R.id.finished);
        finished.setOnClickListener(this);
        unfinished = view.findViewById(R.id.unfinished);
        unfinished.setOnClickListener(this);
        //设置布局管理器
        mLinearManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearManager);
        //设置数据源
        list = new ArrayList<>();
        //设置适配器
        mAdapter = new PackageTansRecyclerAdapter(list, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        SharedPreferences sPreferences = getContext().getSharedPreferences("USER_INFO", MODE_PRIVATE);
        uid = sPreferences.getInt("uid", -1);
        Bundle args=getArguments();
        if (uid == -1){
            Toast.makeText(getContext(), "未登录或登陆信息失效，请登陆后再进行操作", Toast.LENGTH_SHORT).show();
        }
        if(args!=null){
            url = args.getString("url")+uid;
        }
        okHttpClientManager = new OkHttpClientManager(new LoginService().userInfoSha256(getContext()));
        loadData(url);
        emptyView.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData(url);
            }
        });
        emptyView.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData(url);
            }
        });
        mAdapter.setItemClickListener(MyItemClickListener);
        return view;
    }

    private void loadData(final String url) {
        emptyView.setErrorType(EmptyView.NETWORK_LOADING);
        emptyView.setVisibility(View.VISIBLE);
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... params) {
                //执行网络请求
                //String s = HttpUtils
                // getStringByOkhttp(url);
                okHttpClientManager = new OkHttpClientManager(new LoginService().userInfoSha256(getContext()));
                try {
                    Response response =  okHttpClientManager.getAsyn(url);
                    if (response.code()==200){
                        date=response.body().string();
                    }else {
                        date = "";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return date;
            }
            @Override
            protected void onPostExecute(String s) {

                if (s!=null && !s.isEmpty()) {
                    List<Map<String, Object>> tempList = (new MyJsonManager()).CustomerInfoJsonJXData(s);
                    Log.d(TAG, "onPostExecute:快递任务RevTask"+tempList.toString());
                    if (!tempList.isEmpty()) {
                        list.addAll(tempList);
                        //提示更新数据
                        Toast.makeText(getContext(), list.get(0).get("telcode").toString(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.finished:
                url=BASE_URL+"/ExtraceSystem/yizhuanyunPackage/"+uid;
                loadData(url);
                finished.setBackgroundResource(R.drawable.bg_round_right_green);
                unfinished.setBackgroundResource(R.drawable.bg_round_left_trans);
                unfinished.setTextColor(getResources().getColor(R.color.colorPrimary));
                finished.setTextColor(getResources().getColor(R.color.white));
                //Toast.makeText(getContext(), "package已处理页面", Toast.LENGTH_SHORT).show();
                break;
            case R.id.unfinished:
                url=BASE_URL+"/ExtraceSystem/weizhuanyunPackage/"+uid;
                finished.setBackgroundResource(R.drawable.bg_round_right_trans);
                unfinished.setBackgroundResource(R.drawable.bg_round_left_green);
                unfinished.setTextColor(getResources().getColor(R.color.white));
                finished.setTextColor(getResources().getColor(R.color.colorPrimary));
                //Toast.makeText(getContext(), "package未处理页面", Toast.LENGTH_SHORT).show();
                loadData(url);
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            switch (msg.what){
                case 0:
                    emptyView.setErrorType(EmptyView.HIDE_LAYOUT);//有内容页面
                    //pd.dismiss();// 关闭ProgressDialog
                    //提示更新数据
                    mAdapter.notifyDataSetChanged();
                    break;
            }

        }
    };
    private PackageTansRecyclerAdapter.OnItemClickListener MyItemClickListener = new PackageTansRecyclerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            Intent intent;
            switch (v.getId()){
                case R.id.callprevious:
                    intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ list.get(position).get("上一网点电话")));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                case R.id.callnext:
                    intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ list.get(position).get("下一网点电话").toString()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }

    };
}
