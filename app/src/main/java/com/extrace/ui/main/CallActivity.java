package com.extrace.ui.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;

import com.extrace.net.json.GetJsonDataUtil;
import com.extrace.net.json.MyJsonManager;
import com.extrace.ui.R;
import com.extrace.ui.adapter.CustomerManageAdapter;
import com.extrace.ui.entity.CustomerInfo;
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
import java.util.List;
import java.util.Map;

public class CallActivity extends AppCompatActivity {
    public  String date;
    public  String title;
    public RecyclerView recyclerview;
    public List<Map<String,Object>> list=new ArrayList<>();
    private CustomerManageAdapter mMyAdapter;
    private Context mContext = CallActivity.this;
    private ClearEditText clearEditText;

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
        //Toast.makeText(MainActivity.this, String.valueOf(PinyinUtils.getFirstSpell("zhang").startsWith("z".toString())), Toast.LENGTH_SHORT).show();
        CustomerInfoJson(this);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public void CustomerInfoJson(Context context) {
        String jsonStr = new GetJsonDataUtil().getJson(context, "customerInfos.json");//获取assets目录下的json文件数据
        List<CustomerInfo> list=new MyJsonManager().customerInfoListJsonJx(jsonStr);
        initViews(list);
    }


    private PinyinComparator mComparator;
    private WaveSideBar mSideBar;
    private CustomerManageAdapter mAdapter;
    private LinearLayoutManager manager;
    private List<SortModel> mDateList;
    private TitleItemDecoration mDecoration;
    private ClearEditText mClearEditText;
    private List<CustomerInfo> pp=new ArrayList<>();
    private ItemTouchHelper mItemTouchHelper;

    private void initViews(List<CustomerInfo> data) {
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
//                Intent intent = new Intent();
//
//                intent.putExtra("nodeName",data.getName());
//                intent.putExtra("nodeCode",data.getId());
//                setResult(RESULT_OK,intent);
//                finish();
                call(data.getTelcode());
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
    private List<SortModel> filledData(List<CustomerInfo> date) {
        List<SortModel> mSortList = new ArrayList<>();
        for (int i = 0; i < date.size(); i++) {
            SortModel sortModel = new SortModel();
            sortModel.setTelcode(date.get(i).getTelcode());
            sortModel.setName(date.get(i).getName());
            //汉字转换成拼音
            String pinyin = PinyinUtils.getPingYin(date.get(i).getName());
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


    /**
     * 调用拨号界面
     * @param phone 电话号码
     */
    private void call(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}