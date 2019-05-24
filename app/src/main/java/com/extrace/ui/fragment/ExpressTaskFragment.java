package com.extrace.ui.fragment;


import android.content.SharedPreferences;
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

import com.extrace.util.EmptyView;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import static com.extrace.net.OkHttpClientManager.BASE_URL;

public class ExpressTaskFragment extends Fragment implements View.OnClickListener {

    private final String tag="ExpressTaskFragment_LOG";
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mMyAdapter;
    private LinearLayoutManager mLayoutManager;

    private EmptyView emptyView;
    //private ProgressDialog pd;

    public static final int SHOW_RESPONSE = 0;
    private Button button_sendRequest;
    //获取的json数据
    public  String date;
    public  String title;
    public List<Map<String,Object>> list=new ArrayList<>();

    private String url=BASE_URL+"/ExtraceSystem/customerInfos";
    public ExpressTaskFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("zhn","啦啦啦 ExpressTask");
        View view = inflater.inflate(R.layout.fragment_list_extask,container, false);

        button_sendRequest = view.findViewById(R.id.button1);
        emptyView = view.findViewById(R.id.empty);

        button_sendRequest.setOnClickListener(this);

        //initData();
        Log.e("zhn","list!null 啦啦啦 =  "+list);
        mRecyclerView = view.findViewById(R.id.recycler_view);

        //okhttpDate();
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
//                执行网络请求
                try {
                    Response response =  OkHttpClientManager.getAsyn(url);
                    if (response.code() == 200) {
                        date = response.body().string();
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
                if (s!=null&&!s.isEmpty()) {
                    List<Map<String, Object>> tempList = (new MyJsonManager()).CustomerInfoJsonJXData(s);
                    Log.e("lalala",tempList.toString());
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

    private void okhttpDate() {
        Log.i("TAG","--ok-");
        new Thread(new Runnable() {
            @Override
            public void run() {

                OkHttpClient client=new OkHttpClient();
                Request request=new Request.Builder().url(url).build();
                try {
                    Call call =client.newCall(request);
                    Response sponse=call.execute();
                    date=sponse.body().string();
                    //解析
                    jsonJXDate(date);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
    private void jsonJXDate(String date) {
        if(date!=null) {
            try {
                JSONObject jsonObject = new JSONObject(date);
                Log.i("TESTJSON", "code=" + jsonObject.getString("code"));
                Log.i("TESTJSON", "msg=" + jsonObject.getString("msg"));
                JSONObject objectExtends = jsonObject.getJSONObject("extend");
                JSONArray jsonArrayInfo = objectExtends.getJSONArray("customerInfos");

                for (int i = 0; i < jsonArrayInfo.length(); i++) {
                    JSONObject object = jsonArrayInfo.getJSONObject(i);
                    Map<String, Object> map = new HashMap<>();
                    map.put("id",object.getInt("id"));
                    map.put("name",object.getString("name"));
                    map.put("telcode",object.getString("telcode"));
                    map.put("department",object.getString("department"));
                    map.put("regioncode",object.getString("regioncode"));
                    map.put("address",object.getString("address"));
                    map.put("postcode",object.getString("postcode"));
                    list.add(map);
                }
                if (list.isEmpty()){
                    emptyView.setErrorType(EmptyView.NODATA);
                }else {
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            } catch (JSONException e) {
                emptyView.setErrorType(EmptyView.NODATA);
                Log.e(tag, "异常：jsonJXDate");
                e.printStackTrace();
            }

        }



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
                    mMyAdapter=new RecyclerAdapter(list,getContext());
                    //设置布局显示格式
                    mLayoutManager = new LinearLayoutManager(getContext());
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mMyAdapter);

                    break;


            }
        }
    };


    @Override
    public void onClick(View v) {
        testJson();
    }

    private void testJson() {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(getActivity().getAssets().open("customerInfos.json"), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            jsonJXDate(stringBuilder.toString());
            Log.i("TESTJSON", "code=" + jsonObject.getString("code"));
            Log.i("TESTJSON", "msg=" + jsonObject.getString("msg"));
            JSONObject objectExtends = jsonObject.getJSONObject("extend");
            JSONArray jsonArrayInfo = objectExtends.getJSONArray("customerInfos");
            for (int i = 0; i < jsonArrayInfo.length(); i++) {
                JSONObject object = jsonArrayInfo.getJSONObject(i);
                Log.i("TESTJSON", "----------------");
                Log.i("TESTJSON", "id=" + object.getInt("id"));
                Log.i("TESTJSON", "name=" + object.getString("name"));
                Log.i("TESTJSON", "telcode=" + object.getString("telcode"));
                Log.i("TESTJSON", "department=" + object.getString("department"));
                Log.i("TESTJSON", "regioncode=" + object.getString("regioncode"));
                Log.i("TESTJSON", "address=" + object.getString("address"));
                Log.i("TESTJSON", "postcode=" + object.getString("postcode"));
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}