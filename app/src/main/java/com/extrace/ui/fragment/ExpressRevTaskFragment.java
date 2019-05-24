package com.extrace.ui.fragment;

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

import com.extrace.net.OkHttpClientManager;
import com.extrace.net.json.MyJsonManager;
import com.extrace.ui.R;
import com.extrace.ui.adapter.RevTaskRecyclerAdapter;
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

import static com.extrace.net.OkHttpClientManager.BASE_URL;

public class ExpressRevTaskFragment extends Fragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private RevTaskRecyclerAdapter mAdapter;
    private LinearLayoutManager mLinearManager;

    private EmptyView emptyView;
//    private ProgressDialog pd;
    private Button button_sendRequest;
    //获取的json数据
    public  String date;
    public  String title;
    public List<Map<String,Object>> list;
    public String url=BASE_URL+"/ExtraceSystem/customerInfos";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_express_rev_task, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        emptyView = view.findViewById(R.id.empty);

        button_sendRequest = view.findViewById(R.id.button1);
//        pd = new ProgressDialog(getContext());
//        pd .setCanceledOnTouchOutside(true); //设置点击空白处取消
        button_sendRequest.setOnClickListener(this);
        //设置布局管理器
        mLinearManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearManager);
        //设置数据源
        list = new ArrayList<>();
        //设置适配器
        mAdapter = new RevTaskRecyclerAdapter(list, getActivity());
        mRecyclerView.setAdapter(mAdapter);


        loadData(url);
        emptyView.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData(url);
            }
        });
        return view;
    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            Log.e("tag_my", "加载fragment——revTask");
//
//        }
//    }


    private void loadData(final String url) {
       emptyView.setErrorType(EmptyView.NETWORK_LOADING);
       emptyView.setVisibility(View.VISIBLE);

        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... params) {
//                执行网络请求
                //String s = HttpUtils.getStringByOkhttp(url);
                try {
                   Response response =  OkHttpClientManager.getAsyn(url);
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
////                    解析数据 ：解析成功之后要什么，解析之后的数据给RecyclerView来显示
//                    List<FoodBean> beanList = ParseXMLData.parseXML(s);
////                  获取到网络数据，并且解析了，添加数据到数据源当中
////                    数据源：适配器当中传递的集合对象，就是数据源
//                    mDatas.addAll(beanList);
////                    提示adapter更新数据
//                    adapter.notifyDataSetChanged();
                    List<Map<String, Object>> tempList = (new MyJsonManager()).CustomerInfoJsonJXData(s);
                    Log.e("lalala","快递任务RevTask"+tempList.toString());
                    if (!tempList.isEmpty()) {
                        list.addAll(tempList);
                        //提示更新数据
                        //mAdapter.notifyDataSetChanged();
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
            List<Map<String, Object>> tempList = (new MyJsonManager()).CustomerInfoJsonJXData(stringBuilder.toString());
            if (!tempList.isEmpty()) {
                list.addAll(tempList);
            }
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

    @Override
    public void onClick(View v) {
        emptyView.setErrorType(EmptyView.NETWORK_LOADING);
        /* 开启一个新线程，在新线程里执行耗时的方法 */
        new Thread(new Runnable() {
            @Override
            public void run() {
                testJson();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);// 执行耗时的方法之后发送消给handler
            }

        }).start();

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            switch (msg.what){
                case 0:
                    emptyView.setErrorType(EmptyView.HIDE_LAYOUT);//有内容页面
//                    pd.dismiss();// 关闭ProgressDialog
                    //提示更新数据
                    mAdapter.notifyDataSetChanged();
                    break;
            }

        }
    };
}
