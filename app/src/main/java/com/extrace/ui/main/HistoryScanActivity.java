package com.extrace.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.extrace.ui.R;
import com.extrace.ui.service.HistoryOperator;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryScanActivity extends AppCompatActivity implements View.OnClickListener {


    //private Button add;
    private ListView listView;
    private ArrayList<HashMap<String, String>> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_scan);
        //add = findViewById(R.id.add);
        listView = findViewById(R.id.listView);
        //add.setOnClickListener(this);
        //通过list获取数据库表中的所有id和title，通过ListAdapter给listView赋值
        final HistoryOperator historyOperator = new HistoryOperator(HistoryScanActivity.this);
        list = historyOperator.getHistoryList();
        final ListAdapter listAdapter = new SimpleAdapter(HistoryScanActivity.this, list, R.layout.item_history_scan,
                new String[]{"id", "title","time","context"}, new int[]{R.id.note_id, R.id.note_title,R.id.history_time,R.id.history_content});
        listView.setAdapter(listAdapter);

        //通过添加界面传来的值判断是否要刷新listView
        Intent intent = getIntent();
        int flag = intent.getIntExtra("Insert", 0);
        if (flag == 1) {
            list = historyOperator.getHistoryList();
            listView.setAdapter(listAdapter);
        }

        /**
         * 4、点击待办事项中的任何一项，可以跳到详情页面查看详情，这时需要给listView
         * 设置一个点击事件（通过intent将id传递给DetailActivity.java，然后目标页面再通过接收到的id进行查找title和context，并显示出来）：
         */
        //点击listView的任何一项跳到详情页面
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long i) {

                String id = list.get(position).get("id");
//                Intent intent = new Intent();
//                intent.setClass(HistoryScanActivity.this, ScanBarcodeActivity.class);
//                intent.putExtra("note_id", Integer.parseInt(id));
//                startActivity(intent);
                Toast.makeText(HistoryScanActivity.this, "click："+id, Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * 7、待办事项页面中listView长按可以实现列表数据的删除：
         */
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryScanActivity.this);
                builder.setMessage("确定删除？");
                builder.setTitle("提示");

                //添加AlterDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String id = list.get(position).get("id");
                        historyOperator.delete(Integer.parseInt(id));
                        list.remove(position);
                        //listAdapter.notify();
                        listView.setAdapter(listAdapter);
                    }
                });

                //添加AlterDialog.Builder对象的setNegativeButton()方法
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
//        Intent intent = new Intent();
//        intent.setClass(HistoryScanActivity.this, AddActivity.class);
//        HistoryScanActivity.this.startActivity(intent);
    }
}
