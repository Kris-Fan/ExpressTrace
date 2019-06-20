package com.extrace.ui.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.extrace.ui.R;
import com.extrace.ui.adapter.PackageContentAdapter;
import com.extrace.ui.entity.ExpressSheet;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * 包裹拆包，到件扫描后 展示里边列表
 */
public class PageContentActivity extends AppCompatActivity {
    private RecyclerView recyclerview;
    private PackageContentAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private TextView expressNum;
    private List<String> list;
    private static final String TAG = "PageContentActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_content);
        expressNum = findViewById(R.id.express_num);
        Intent intent=getIntent();
        String response = intent.getStringExtra("response");
        //String response="[{\"id\":\"1\",\"type\":5,\"sender\":1,\"recever\":2,\"weight\":15.0,\"tranfee\":20.0,\"packagefee\":60.0,\"insufee\":30.0,\"accepter\":\"12\",\"deliver\":\"0\",\"acceptetime\":\"2019-04-03 09:25:50\",\"delivetime\":\"2019-04-23 12:23:28\",\"acc1\":\"52\",\"acc2\":\"52\",\"status\":\"5\"},{\"id\":\"12\",\"type\":3,\"sender\":7,\"recever\":11,\"weight\":23.0,\"tranfee\":25.0,\"packagefee\":85.0,\"insufee\":63.0,\"accepter\":\"12\",\"deliver\":\"0\",\"acceptetime\":\"2019-04-10 12:23:09\",\"delivetime\":\"2019-04-23 12:23:19\",\"acc1\":null,\"acc2\":null,\"status\":\"3\"}]";
        //Toast.makeText(this, "response", Toast.LENGTH_SHORT).show();
        //ImageView iv_add = findViewById(R.id.scan_express);
//        iv_add.setOnClickListener(new ImageView.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(PageContentActivity.this, CustomerEditActivity.class);
//                startActivity(intent);
//            }
//        });

        try {
            //Json的解析类对象
            JsonParser parser = new JsonParser();
            //将JSON的String 转成一个JsonArray对象
            JsonArray jsonArray = parser.parse(response).getAsJsonArray();

            Gson gson = new Gson();
            ArrayList<ExpressSheet> jsonBeanArrayList = new ArrayList<>();

            //循环遍历json数组
            for (JsonElement json : jsonArray) {
                //使用GSON，转成Bean对象
                ExpressSheet jsonBean = gson.fromJson(json, ExpressSheet.class);
                jsonBeanArrayList.add(jsonBean);
                //Toast.makeText(this, String.valueOf(jsonBean.getId()), Toast.LENGTH_SHORT).show();
            }

            expressNum.setText("该包裹总共有 "+jsonBeanArrayList.size()+"个快递");
            recyclerview = findViewById(R.id.package_content_recy);
            mAdapter = new PackageContentAdapter(jsonBeanArrayList, this);
            mLayoutManager = new LinearLayoutManager(this);
            recyclerview.setLayoutManager(mLayoutManager);
            recyclerview.setAdapter(mAdapter);
            //recyclerview.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        }catch (RuntimeException e){
            Log.e(TAG, "onCreate: ", e.getCause());
            expressNum.setText("该包裹无效！请返回重试");
        }
    }

}
