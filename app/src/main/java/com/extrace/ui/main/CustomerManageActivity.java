package com.extrace.ui.main;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.extrace.net.OkHttpClientManager;
import com.extrace.ui.R;
import com.extrace.ui.adapter.CustomerManageAdapter;
import com.extrace.util.EmptyView;
import com.extrace.util.layout.ClearEditText;
import com.extrace.util.layout.PinyinComparator;
import com.extrace.util.layout.PinyinUtils;
import com.extrace.util.layout.SortModel;
import com.extrace.util.layout.TitleItemDecoration;
import com.extrace.util.layout.WaveSideBar;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.extrace.net.OkHttpClientManager.BASE_URL;

public class CustomerManageActivity extends AppCompatActivity {
    private   String date;
    private   String title;
    private EmptyView empty;
    private RecyclerView recyclerview;
    private List<Map<String,Object>> list=new ArrayList<>();
    private CustomerManageAdapter mAdapter;
    private boolean showRadio;
    private static final int REQUEST_DETAIL = 0X10;
    private static final String TAG = "CustomerManageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_manage);
        empty = findViewById(R.id.empty);
        recyclerview=  findViewById(R.id.recy);
        ImageView iv_add = findViewById(R.id.add_customer);
        iv_add.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerManageActivity.this, CustomerEditActivity.class);
                startActivity(intent);
            }
        });
        Log.e(TAG,"客户管理执行");
        sendRequestWithHttpClient();

        empty.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequestWithHttpClient();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    //点击新建按钮转到添加客户信息界面
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add:
                Intent intent = new Intent(CustomerManageActivity.this, CustomerEditActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }
    //向服务器请求客户信息
    private void sendRequestWithHttpClient() {
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                //用HttpClient发送请求，分为五步
//                //第一步：创建HttpClient对象
//                HttpClient httpCient = new DefaultHttpClient();
//                //第二步：创建代表请求的对象,参数是访问的服务器地址
//                HttpGet httpGet = new HttpGet("http://192.168.3.172:8080/ExtraceSystem/customerInfos");
//                try {
//                    //第三步：执行请求，获取服务器发还的相应对象
//                    HttpResponse httpResponse = httpCient.execute(httpGet);
//                    //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
//                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
//                        //第五步：从相应对象当中取出数据，放到entity当中
//                        HttpEntity entity = httpResponse.getEntity();
//                        String response = EntityUtils.toString(entity, "utf-8");//将entity当中的数据转换为字符串
//                        //解析json数据
//                        jsonData(response);
//                    }else { //链接不正常
//                        Message message = handler.obtainMessage();
//                        message.what = 404;
//                        message.obj = title;
//                        handler.sendMessage(message);
//                    }
//                } catch (IOException e) {
//                    Message message = handler.obtainMessage();
//                    message.what = 404;
//                    message.obj = title;
//                    handler.sendMessage(message);
//                    e.printStackTrace();
//                }
//            }
//        }).start();
        empty.setErrorType(EmptyView.NETWORK_LOADING);
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... params) {
//                执行网络请求
                //String s = HttpUtils.getStringByOkhttp(url);
                try {
                    Response response =  OkHttpClientManager.getAsyn(BASE_URL+"/ExtraceSystem/customerInfos");
                    Log.e("lalal","客户管理-请求状态"+response.code());
                    if (response.code() == 200){
                        date = response.body().string();
                    }else {
                        date = "";
                    }
                } catch (IOException e) {
                    Message message = handler.obtainMessage();
                    message.what = 404;
                    message.obj = title;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
                return date;
            }
            @Override
            protected void onPostExecute(String s) {

                if (s!=null && !s.isEmpty()) {
                    empty.setErrorType(EmptyView.HIDE_LAYOUT);
                    Log.e("lalal","开始执行解析json");
                    jsonData(s);
                }else {
                    Log.e("lalal","错误页面");
                    empty.setErrorType(EmptyView.NETWORK_ERROR);//错误页面
                }
            }
        }.execute();
    }
    //解析json数据
    private void jsonData(String date) {
        if(date!=null) {
            try {
                Log.e(TAG,"客户管理json解析数据："+date);
                JSONObject json = new JSONObject(date);
                JSONObject result = json.getJSONObject("extend");
                JSONArray data = result.getJSONArray("customerInfos");

                for (int i = 0; i < data.length(); i++) {
                    Map<String, Object> map = new HashMap<>();
                    JSONObject value = data.getJSONObject(i);
                    int id = value.getInt("id");
                    String name = value.getString("name");
                    String  telcode= value.getString("telcode");
                    String addr = value.getString("address");
                    String  dpt= value.getString("department");
                    String postcode = value.getString("postcode");
                    map.put("id", id);
                    map.put("name", name);
                    map.put("telcode", telcode);
                    map.put("addr", addr);
                    map.put("dpt", dpt);
                    map.put("postcode", postcode);
                    list.add(map);
                }
                if (list.isEmpty()){
                    empty.setErrorType(EmptyView.NODATA);       //加载无数据页面
                }else {
                    Message message = handler.obtainMessage();
                    message.what = 1;
                    message.obj = title;
                    handler.sendMessage(message);
                }
            } catch (JSONException e) {
                empty.setErrorType(EmptyView.NODATA);
                e.printStackTrace();
            }
        }
    }
    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    empty.setErrorType(EmptyView.HIDE_LAYOUT);
                    initViews(list);
                    break;
                case 404:
                    empty.setErrorType(EmptyView.NETWORK_ERROR);
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DETAIL && resultCode == RESULT_OK){
            int num = data.getIntExtra("position",-1);
            if (num == -1){
                Toast.makeText(this, "refresh all", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "刷新"+num, Toast.LENGTH_SHORT).show();
               // mDateList.clear();
                pp.clear();
                list.clear();
//                mAdapter.notifyItemChanged(num);
//                mAdapter.notifyDataSetChanged();
                sendRequestWithHttpClient();
            }
        }
    }

    private PinyinComparator mComparator;
    private WaveSideBar mSideBar;
    private LinearLayoutManager manager;
    private List<SortModel> mDateList;

    private ClearEditText mClearEditText;
    private List<Map<String,Object>> pp=new ArrayList<>();
    private ItemTouchHelper mItemTouchHelper;

    private void initViews(List<Map<String,Object>> data) {
        TitleItemDecoration mDecoration;
        mComparator = new PinyinComparator();
        mSideBar = (WaveSideBar) findViewById(R.id.sideBar);

        //设置右侧SideBar触摸监听
        mSideBar.setOnTouchLetterChangeListener(new WaveSideBar.OnTouchLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                //该字母首次出现的位置
                int position = mAdapter.getPositionForSection(letter.charAt(0));
                if (position != -1) {
                    manager.scrollToPositionWithOffset(position, 0);
                }
            }
        });

        recyclerview = (RecyclerView) findViewById(R.id.recy);
        mDateList = filledData(data);



        // 根据a-z进行排序源数据
        Collections.sort(mDateList, mComparator);
        //RecyclerView设置manager
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(manager);
        mAdapter = new CustomerManageAdapter(this, mDateList,list);

        Intent intent = getIntent();
        showRadio = intent.getBooleanExtra("REQUEST_TYPE",false);
        mAdapter.changetShowDelImage(showRadio);        //是否显示showradio
        mAdapter.setTypeShow(true);
        mAdapter.setOnItemClickListener(new CustomerManageAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//            }
//
//            @Override
//            public void onItemClick(CustomerManageAdapter.ViewName parent, View view, int position, Map<String,Object> data) {    //跳转详情页
////                Intent intent;
////                switch (view.getId()){
////                    case R.id.rb_selected:
////                        Log.e("lalal","点击radio");
////                        //Toast.makeText(CustomerManageActivity.this, "你点击了lala"+position, Toast.LENGTH_SHORT).show();
////                        intent = new Intent();
////                        Bundle bundle = new Bundle();
////                        bundle.putString("id",data.get("id").toString());
////                        bundle.putString("name",data.get("name").toString());
////                        bundle.putString("telcode",data.get("telcode").toString());
////                        bundle.putString("addr",data.get("addr").toString());
////                        bundle.putString("dpt",data.get("dpt").toString());
////                        bundle.putString("postcode",data.get("postcode").toString());
////                        intent.putExtras(bundle);
////                        setResult(RESULT_OK, intent);
////                        finish();
////                        break;
////                    default:
////                        Toast.makeText(CustomerManageActivity.this, "你点击了:"+position, Toast.LENGTH_SHORT).show();
////                        intent = new Intent(CustomerManageActivity.this, CustomerInfoDetailActivity.class);
////
////                        intent.putExtra("id",data.get("id").toString());
////                        intent.putExtra("name",data.get("name").toString());
////                        intent.putExtra("telcode",data.get("telcode").toString());
////                        intent.putExtra("addr",data.get("addr").toString());
////                        intent.putExtra("dpt",data.get("dpt").toString());
////                        intent.putExtra("postcode",data.get("postcode").toString());
////                        startActivity(intent);
////                        break;
////                }
//
//            }

            @Override
            public void onItemClick(CustomerManageAdapter.ViewName parent, View view, int position, SortModel data) {
                Intent intent;
                switch (view.getId()){
                    case R.id.rb_selected:
                        Log.e(TAG,"点击radio");
                        //Toast.makeText(CustomerManageActivity.this, "你点击了lala"+position, Toast.LENGTH_SHORT).show();
                        intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("id",data.getId());
                        bundle.putString("name",data.getName());
                        bundle.putString("telcode",data.getTelcode());
                        bundle.putString("addr",data.getAddress());
                        bundle.putString("dpt",data.getDepartment());
                        bundle.putString("postcode",data.getPostcode());
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    default:
                        //Toast.makeText(CustomerManageActivity.this, "你点击了:"+position, Toast.LENGTH_SHORT).show();
                        intent = new Intent(CustomerManageActivity.this, CustomerInfoDetailActivity.class);
                        intent.putExtra("position",position);
                        intent.putExtra("id",data.getId());
                        intent.putExtra("name",data.getName());
                        intent.putExtra("telcode",data.getTelcode());
                        intent.putExtra("addr",data.getAddress());
                        intent.putExtra("dpt",data.getDepartment());
                        intent.putExtra("postcode",data.getPostcode());

                        startActivityForResult(intent, REQUEST_DETAIL);
                        break;
                }
            }
        });


        recyclerview.setAdapter(mAdapter);

        mDecoration = new TitleItemDecoration(this, mDateList);
//        //如果add两个，那么按照先后顺序，依次渲染。
        recyclerview.addItemDecoration(mDecoration);
        //recyclerview.addItemDecoration(new DividerItemDecoration(CustomerManageActivity.this, DividerItemDecoration.VERTICAL));

        pp.addAll(data);
        mClearEditText = findViewById(R.id.filter_edit);

        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }
    /**
     * 为RecyclerView填充数据
     */
    private List<SortModel> filledData(List<Map<String,Object>> date) {
        List<SortModel> mSortList = new ArrayList<>();
        for (int i = 0; i < date.size(); i++) {
            SortModel sortModel = new SortModel();
            sortModel.setId(date.get(i).get("id").toString());
            sortModel.setName(date.get(i).get("name").toString());
            sortModel.setTelcode(date.get(i).get("telcode").toString());
            sortModel.setAddress(date.get(i).get("addr").toString());
            sortModel.setDepartment(date.get(i).get("dpt").toString());
            sortModel.setPostcode(date.get(i).get("postcode").toString());
            //汉字转换成拼音
            String pinyin = PinyinUtils.getPingYin(date.get(i).get("name").toString());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setLetters(sortString.toUpperCase());
            } else {
                sortModel.setLetters("#");
            }
            mSortList.add(sortModel);
        }
        return mSortList;

    }
    /**
     * 根据输入框中的值来过滤数据并更新RecyclerView
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<>();
        //Toast.makeText(MainActivity.this, String.valueOf(mDateList.size()), Toast.LENGTH_SHORT).show();
        //Toast.makeText(MainActivity.this, pp.get(0).get("name").toString(), Toast.LENGTH_SHORT).show();
        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = filledData(pp);
        }
        else {
            filterDateList.clear();
            //Toast.makeText(MainActivity.this, String.valueOf(mDateList.size()), Toast.LENGTH_SHORT).show();
            for (SortModel sortModel : mDateList) {
                String name = sortModel.getName();
                //Toast.makeText(MainActivity.this, filterStr.toString(), Toast.LENGTH_SHORT).show();
                if (name.indexOf(filterStr.toString()) != -1
//                        ||PinyinUtils.getFirstSpell(name).startsWith(filterStr.toString())
//                        || PinyinUtils.getFirstSpell(name).toLowerCase().startsWith(filterStr.toString())
//                        || PinyinUtils.getFirstSpell(name).toUpperCase().startsWith(filterStr.toString())
                ) {
                    filterDateList.add(sortModel);
                    //Toast.makeText(MainActivity.this, name.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(filterDateList, mComparator);
        mDateList.clear();
        mDateList.addAll(filterDateList);
        mAdapter.notifyDataSetChanged();
    }
}
