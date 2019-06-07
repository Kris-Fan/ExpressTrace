package com.extrace.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.extrace.ui.R;
import com.extrace.ui.service.ActivityCollector;
import com.extrace.ui.service.EditDialog;
import com.extrace.ui.service.LoginService;
import com.extrace.util.TitleLayout;

public class MyInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout menu_1;
    private LinearLayout menu_2;
    private LinearLayout menu_3;
    private Button menu_4;
    private LinearLayout logout,existApp;

    private TextView name,telcode,tv_site;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        ActivityCollector.addActivity(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){ //隐藏自带的标题栏
            actionBar.hide();
        }
        TitleLayout titleLayout ;
        titleLayout = findViewById(R.id.title);
        titleLayout.setTitle(getResources().getString(R.string.tab_bar_me));
        titleLayout.hideTitleEdit();
        bindView();
        bindEvent();
    }

    private void bindEvent() {
        menu_1.setOnClickListener(this);
        menu_2.setOnClickListener(this);
        menu_3.setOnClickListener(this);
        menu_4.setOnClickListener(this);
        logout.setOnClickListener(this);
        existApp.setOnClickListener(this);
    }

    private void bindView() {
        name = findViewById(R.id.name);
        telcode = findViewById(R.id.telCode);
        tv_site = findViewById(R.id.tv_site);
        menu_1 = findViewById(R.id.menu_1);
        menu_2 = findViewById(R.id.menu_2);
        menu_3 = findViewById(R.id.menu_3);
        menu_4 = findViewById(R.id.btn_save);
        logout = findViewById(R.id.login_out);
        existApp = findViewById(R.id.exist_app);
        //显示用户此前录入的数据
        SharedPreferences sPreferences = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        String username = sPreferences.getString("username", "");
        String tel = sPreferences.getString("telcode", "");
        String dpt = sPreferences.getString("dptid","");
        name.setText(username);
        telcode.setText(tel);
        tv_site.setText(dpt);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu_1:
                //showEditDialog(name);
                break;
            case R.id.menu_2:
                showEditDialog(2,"修改姓名","请输入姓名");
                break;
            case R.id.menu_3:
                showEditDialog(3,"修改手机号","请输入手机号");
                break;
            case R.id.menu_4:   //保存
                break;
            case R.id.login_out:
                LoginService.logout(this);
                Intent intent = new Intent(MyInfoActivity.this, MainActivity.class);
                setResult(RESULT_OK,intent);
                finish();
                break;
            case R.id.exist_app://退出程序
                ActivityCollector.finishAll();
                break;
        }
    }


    private void showEditDialog(final int i, String title, String hint) {
        //点击弹出对话框
        final EditDialog editDialog = new EditDialog(this);
        editDialog.setTitle(title);
        editDialog.setHintStr(hint);
        if (i == 2){
            //editDialog.findViewById(R.id.et_phone).setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            editDialog.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        }
        editDialog.setYesOnclickListener("确定", new EditDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(String phone) {
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getApplicationContext(), "不可为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (i == 2) {
                        name.setText(phone);
                    }else if (i == 3){
                        telcode.setText(phone);
                    }else if (i == 4){
                        tv_site.setText(phone);
                    }
//                    createBarCode(phone);
                    editDialog.dismiss();
                    //让软键盘隐藏
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(getView().getApplicationWindowToken(), 0);
                }
            }
        });
        editDialog.setNoOnclickListener("取消", new EditDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                editDialog.dismiss();
            }
        });
        editDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
