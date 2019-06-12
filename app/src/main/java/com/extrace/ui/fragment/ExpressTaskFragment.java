package com.extrace.ui.fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.extrace.net.OkHttpClientManager;
import com.extrace.net.json.MyJsonManager;
import com.extrace.ui.R;
import com.extrace.ui.adapter.RecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extrace.ui.main.ExpressEditActivity;
import com.extrace.ui.service.LoginService;
import com.extrace.util.EmptyView;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.extrace.net.OkHttpClientManager.BASE_URL;

public class ExpressTaskFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ExpressTaskFragment";
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mMyAdapter;
    private LinearLayoutManager mLayoutManager;
    private EmptyView emptyView;
    private String message="";

    private Button button_sendRequest;
    private Button finished;
    private Button unfinished;
    //获取的json数据
    public  String date;
    public  String title;
    public List<Map<String,Object>> list=new ArrayList<>();

    private String url="";
    private String type="";
    OkHttpClientManager okHttpClientManager;
    public ExpressTaskFragment() {
        // Required empty public constructor
    }
    public void alert_edit(View view){
        final EditText et = new EditText(getContext());
        et.setText(message);
        new AlertDialog.Builder(getContext()).setTitle("请输入短信模板内容")
                //.setIcon(android.R.drawable.sym_def_app_icon)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //按下确定键后的事件
                        message = et.getText().toString();
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("取消",null).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView:啦啦啦 ExpressTask");
        View view = inflater.inflate(R.layout.fragment_list_extask,container, false);
        Bundle args=getArguments();
        SharedPreferences sPreferences = getContext().getSharedPreferences("USER_INFO", MODE_PRIVATE);
        int uid = sPreferences.getInt("uid", -1);
        if (uid == -1){
            Toast.makeText(getContext(), "未登录或登陆信息失效，请登陆后再进行操作", Toast.LENGTH_SHORT).show();
        }
        if(args!=null){
            url = args.getString("url")+uid;
            type = args.getString("type");
        }
        button_sendRequest = view.findViewById(R.id.button1);
        emptyView = view.findViewById(R.id.empty);
        button_sendRequest.setOnClickListener(this);
        finished = view.findViewById(R.id.finished);
        finished.setOnClickListener(this);
        unfinished = view.findViewById(R.id.unfinished);
        unfinished.setOnClickListener(this);
        //initData();
        Log.d(TAG, "onCreateView: list!null 啦啦啦 =  "+list);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        okHttpClientManager = new OkHttpClientManager(new LoginService().userInfoSha256(getContext()));
        loadData(url);
        emptyView.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(url);
            }
        });
        return view;
    }


    private void loadData(final String url) {

        emptyView.setErrorType(EmptyView.NETWORK_LOADING);
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... params) {
                //执行网络请求
                try {

                    okHttpClientManager = new OkHttpClientManager(new LoginService().userInfoSha256(getContext()));
                    Log.e(TAG, "doInBackground: "+ url);
                    Response response =  okHttpClientManager.getAsyn(url);
                    Log.e(TAG, "doInBackground: "+ response.code());
                    if (response.code() == 200) {
                        date = response.body().string();
                        Log.e(TAG, "doInBackground:派送 "+ date);
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
                list.clear();
                List<Map<String, Object>> tempList;
                if (s!=null&&!s.isEmpty()) {
                    if(type.equals("paisong")) {
                        tempList = (new MyJsonManager()).PaisongJsonJXData(s);
                    }else {
                        tempList = (new MyJsonManager()).LanshouJsonJXData(s);
                    }
                    Log.d(TAG, "onPostExecute: "+tempList.toString());
                    if (!tempList.isEmpty()) {
                        emptyView.setErrorType(EmptyView.HIDE_LAYOUT);
                        list.addAll(tempList);
                        //提示更新数据
                        handler.sendEmptyMessage(1);
                    }else {
                        emptyView.setErrorType(EmptyView.NODATA);
                    }
                }else {
                    Toast.makeText(getContext(), "加载失败！", Toast.LENGTH_SHORT).show();
                    emptyView.setErrorType(EmptyView.NETWORK_ERROR);
                }
            }
        }.execute();
    }

    private String data1,senderId,receiverId,expressId,sendername,senderprovince,sendercity,sendertown,senderaddressdetail,
            sendertelephonenumber, receivername,receiverprovince,receivercity,receivertown,
            receiveraddressdetail,receivertelephonenumber,packagestate,relateddeliverid;
    public static final int REQUEST_LANSHOU = 0X700;

    private void lanshou(final String id) {
        SharedPreferences sPreferences = getContext().getSharedPreferences("USER_INFO", MODE_PRIVATE);
        final int uid = sPreferences.getInt("uid", -1);
        if (uid == -1){
            Toast.makeText(getContext(), "未登录或登陆信息失效，请登陆后再进行操作", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "lanshou: 揽收id="+id);
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... params) {
                //执行网络请求
                try {
                    okHttpClientManager = new OkHttpClientManager(new LoginService().userInfoSha256(getContext()));
                    Response response =  okHttpClientManager.getAsyn(BASE_URL+"/ExtraceSystem/onLineLanjian/"+id);
                    if (response.code() == 200) {
                        data1 = response.body().string();
                        Log.i(TAG, "doInBackground: 请求到的数据："+data1);
                    }else {
                        data1 = "";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return data1;
            }
            @Override
            protected void onPostExecute(String s) {
                Log.e(TAG, "onPostExecute: "+s);
                try {
                    if (s!=null && !s.equals("{}")) {
                        JSONObject jsonObject = new JSONObject(s);
                        senderId = jsonObject.getString("senderId");
                        receiverId = jsonObject.getString("receiverId");
                        expressId = jsonObject.getString("快件编码");
                        JSONObject orderInfo = jsonObject.getJSONObject("订单信息");
                        int sn = orderInfo.getInt("sn");
                        sendername = orderInfo.getString("sendername");
                        senderprovince = orderInfo.getString("senderprovince")
                                +orderInfo.getString("sendercity")
                                +orderInfo.getString("sendertown");
                        senderaddressdetail = orderInfo.getString("senderaddressdetail");
                        sendertelephonenumber = orderInfo.getString("sendertelephonenumber");
                        receivername = orderInfo.getString("receivername");
                        receiverprovince = orderInfo.getString("receiverprovince")
                                +orderInfo.getString("receivercity")
                                +orderInfo.getString("receivertown");
                        receiveraddressdetail = orderInfo.getString("receiveraddressdetail");
                        receivertelephonenumber = orderInfo.getString("receivertelephonenumber");
                        packagestate = orderInfo.getInt("packagestate")+"";
                        relateddeliverid = orderInfo.getString("relateddeliverid");

                        Intent intent = new Intent(getContext(), ExpressEditActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("sn",sn);
                        bundle.putString("senderId",senderId);
                        bundle.putString("receiverId",receiverId);
                        bundle.putString("expressId", expressId);

                        bundle.putString("sendername",sendername);
                        bundle.putString("senderprovince",senderprovince);
                        bundle.putString("senderaddressdetail",senderaddressdetail);
                        bundle.putString("sendertelephonenumber",sendertelephonenumber);

                        bundle.putString("receivername",receivername);
                        bundle.putString("receiverprovince",receiverprovince);
                        bundle.putString("receiveraddressdetail",receiveraddressdetail);
                        bundle.putString("receivertelephonenumber",receivertelephonenumber);

                        intent.putExtras(bundle);
                        getActivity().startActivityForResult(intent,REQUEST_LANSHOU);
                    }else {
                        Toast.makeText(getContext(), "处理失败！", Toast.LENGTH_SHORT).show();
                        //emptyView.setErrorType(EmptyView.NETWORK_ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    emptyView.setErrorType(EmptyView.HIDE_LAYOUT);
                    //添加分割线
//                    mRecyclerView.addItemDecoration(new DividerItemDecoration(
//                            getContext(), DividerItemDecoration.VERTICAL));
                    mMyAdapter=new RecyclerAdapter(list,getContext(),type);
                    //设置布局显示格式
                    mLayoutManager = new LinearLayoutManager(getContext());
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mMyAdapter);
                    mMyAdapter.setItemClickListener(MyItemClickListener);
                    break;
            }
        }
    };


    @Override
    public void onClick(View v) {
        String url1,url2;
        SharedPreferences sPreferences = getContext().getSharedPreferences("USER_INFO", MODE_PRIVATE);
        int uid = sPreferences.getInt("uid", -1);
        if (uid == -1){
            Toast.makeText(getContext(), "未登录或登陆信息失效，请登陆后再进行操作", Toast.LENGTH_SHORT).show();
        }
        if(type.equals("paisong")) {
            url1 = BASE_URL+"/ExtraceSystem/weipaisongList/"+uid;
            url2 = BASE_URL+"/ExtraceSystem/daipaisong/"+uid;

        }else{
            url1 = BASE_URL+"/ExtraceSystem/yilanjian/"+uid;
            url2 = BASE_URL+"/ExtraceSystem/weilanjian/"+uid;
        }
        switch (v.getId()){
            case R.id.finished:
                url=url1;
                if(type.equals("weilanshou")){
                    type="yilanshou";
                }
                loadData(url);
                finished.setBackgroundResource(R.drawable.bg_round_right_green);
                unfinished.setBackgroundResource(R.drawable.bg_round_left_trans);
                unfinished.setTextColor(getResources().getColor(R.color.colorPrimary));
                finished.setTextColor(getResources().getColor(R.color.white));
                //Toast.makeText(getContext(), "express已处理页面", Toast.LENGTH_SHORT).show();
                break;
            case R.id.unfinished:
                url=url2;
                if(type.equals("yilanshou")){
                    type="weilanshou";
                }
                finished.setBackgroundResource(R.drawable.bg_round_right_trans);
                unfinished.setBackgroundResource(R.drawable.bg_round_left_green);
                unfinished.setTextColor(getResources().getColor(R.color.white));
                finished.setTextColor(getResources().getColor(R.color.colorPrimary));
                //Toast.makeText(getContext(), "express未处理页面", Toast.LENGTH_SHORT).show();
                loadData(url);
                break;
            case R.id.button1:
                alert_edit(v);
                break;
        }

    }

    private RecyclerAdapter.OnItemClickListener MyItemClickListener = new RecyclerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            Intent intent;
            switch (v.getId()){
                case R.id.msg:
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setType("vnd.android-dir/mms-sms");
                    //address为数据表中存放电话号码的字段
                    intent.putExtra("address", (list.get(position).get("telcode")).toString());//要发短信的手机号，如果没有传空串就行
                    intent.putExtra("sms_body", message);//短信内容
                    startActivity(intent);
                    break;
                case R.id.phone:
                    intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ list.get(position).get("telcode")));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                case R.id.expressId:
                    lanshou(list.get(position).get("sn").toString());
                    break;
                default:
                    break;
            }
        }

    };

}