package com.extrace.ui.main;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
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

import com.extrace.net.json.GetJsonDataUtil;
import com.extrace.ui.R;
import com.extrace.ui.adapter.CustomerManageAdapter;
import com.extrace.ui.entity.TransNode;
import com.extrace.util.EmptyView;
import com.extrace.util.layout.ClearEditText;
import com.extrace.util.layout.PinyinComparator;
import com.extrace.util.layout.PinyinUtils;
import com.extrace.util.layout.SortModel;
import com.extrace.util.layout.TitleItemDecoration;
import com.extrace.util.layout.WaveSideBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeActivity extends AppCompatActivity {
    public  String date;
    public  String title;
    public RecyclerView recyclerview;
    public List<Map<String,Object>> list=new ArrayList<>();
    private CustomerManageAdapter mMyAdapter;
    private Context mContext = NodeActivity.this;
    private ClearEditText clearEditText;

    private String expressId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_manage);
        recyclerview= findViewById(R.id.recy);
        clearEditText = findViewById(R.id.filter_edit);
        EmptyView emptyView = findViewById(R.id.empty);
        ImageView addView = findViewById(R.id.add_customer);
        emptyView.setErrorType(EmptyView.HIDE_LAYOUT);
        addView.setVisibility(View.GONE);
        clearEditText.setHint("请输入网点名称");
        expressId = new Intent().getStringExtra("express_id");
        //Toast.makeText(MainActivity.this, String.valueOf(PinyinUtils.getFirstSpell("zhang").startsWith("z".toString())), Toast.LENGTH_SHORT).show();
        TransNodeJson(this);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public void TransNodeJson(Context context) {
        String jsonStr = new GetJsonDataUtil().getJson(context, "transnode.json");//获取assets目录下的json文件数据
        List<TransNode> list=new ArrayList<>();
        if (jsonStr != null){
            Log.e("lalal","网点 == "+jsonStr);
            Gson gson=new Gson();
            list=gson.fromJson(jsonStr,new TypeToken<List<TransNode>>(){}.getType());

            Log.e("lala","数组："+list.toString());

        }
        initViews(list);
    }


    private PinyinComparator mComparator;
    private WaveSideBar mSideBar;
    private CustomerManageAdapter mAdapter;
    private LinearLayoutManager manager;
    private List<SortModel> mDateList;
    private TitleItemDecoration mDecoration;
    private ClearEditText mClearEditText;
    private List<TransNode> pp=new ArrayList<>();
    private ItemTouchHelper mItemTouchHelper;

    private void initViews(List<TransNode> data) {
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

        mAdapter.setTypeShow(false);
        mAdapter.changetShowDelImage(false);
        mAdapter.setOnItemClickListener(new CustomerManageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CustomerManageAdapter.ViewName parent, View view, int position, SortModel data) {
                Intent intent = new Intent();

                intent.putExtra("expressID", expressId);
                intent.putExtra("nodeName",data.getName());
                intent.putExtra("nodeCode",data.getId());
                setResult(RESULT_OK,intent);
                finish();
                //Toast.makeText(NodeActivity.this, data.toString()+" "+data.getName()+data.getId(), Toast.LENGTH_SHORT).show();
            }
        });


        recyclerview.setAdapter(mAdapter);

        mDecoration = new TitleItemDecoration(this, mDateList);
        //如果add两个，那么按照先后顺序，依次渲染。
        recyclerview.addItemDecoration(mDecoration);
       // recyclerview.addItemDecoration(new DividerItemDecoration(NodeActivity.this, DividerItemDecoration.VERTICAL));

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
    private List<SortModel> filledData(List<TransNode> date) {
        List<SortModel> mSortList = new ArrayList<>();
        for (int i = 0; i < date.size(); i++) {
            SortModel sortModel = new SortModel();
            sortModel.setId(date.get(i).getId());
            sortModel.setName(date.get(i).getNodename());
            //汉字转换成拼音
            String pinyin = PinyinUtils.getPingYin(date.get(i).getNodename().toString());
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
        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = filledData(pp);
        }
        else {
            filterDateList.clear();
            for (SortModel sortModel : mDateList) {
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1) {
                    filterDateList.add(sortModel);
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