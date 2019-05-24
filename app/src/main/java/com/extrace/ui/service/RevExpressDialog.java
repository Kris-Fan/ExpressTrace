package com.extrace.ui.service;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.extrace.net.json.MyJsonManager;
import com.extrace.ui.R;
import com.extrace.ui.entity.TransNode;
import com.extrace.ui.main.NodeActivity;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;


/**
 * 编辑快递单号提示框
 */

public class RevExpressDialog extends Dialog {
    private Button yes, no;//确定按钮
    private TextView titleTv;//消息标题文本
    private TextView et_node;
    private TextView tv_node_code,ex_addr;

    private String titleStr;//从外界设置的title文本
    private String messageStr,codeStr,addrStr;//从外界设置的消息文本,//从外界设置code，地址
    //确定文本和取消文本的显示内容
    private String yesStr, noStr,skipStr;

    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器
    private onSkipOnclickListener onSkipOnclickListener; //跳转点击了的监听器

    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param str
     * @param onNoOnclickListener
     */
    public void setNoOnclickListener(String str, onNoOnclickListener onNoOnclickListener) {
        if (str != null) {
            noStr = str;
        }
        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param str
     * @param onYesOnclickListener
     */
    public void setYesOnclickListener(String str, onYesOnclickListener onYesOnclickListener) {
        if (str != null) {
            yesStr = str;
        }
        this.yesOnclickListener = onYesOnclickListener;
    }

    /**
     * 设置跳转的显示内容
     * @param str
     * @param onSkipOnclickListener
     */
    public void setSkipOnclickListener(String str, onSkipOnclickListener onSkipOnclickListener){
        if (str != null){
            skipStr = str;
        }
        this.onSkipOnclickListener = onSkipOnclickListener;
    }

    public RevExpressDialog(Context context) {
        super(context, R.style.Dialog_Msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_rev_express);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();

    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        et_node.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSkipOnclickListener != null){
                    onSkipOnclickListener.onSkipClick();
                }
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    //yesOnclickListener.onYesClick(et_phone.getText().toString());
                    yesOnclickListener.onYesClick(tv_node_code.getText().toString());
                }
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });

    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {
        //如果用户自定了title和message
        if (titleStr != null) {
            titleTv.setText(titleStr);
        }
        if (messageStr != null) {
            et_node.setText(messageStr);
        }

        if (codeStr != null){
            tv_node_code.setText(codeStr);
        }
        if (addrStr != null){
            ex_addr.setText(addrStr);
        }

        if (skipStr != null) {
            et_node.setText(skipStr);
        }
        //如果设置按钮的文字
        if (yesStr != null) {
            yes.setText(yesStr);
        }

    }

    /**
     * 初始化界面控件
     */

    private void initView() {
        yes = (Button) findViewById(R.id.yes);
        no = (Button) findViewById(R.id.no);
        titleTv = (TextView) findViewById(R.id.title);
        //et_phone = (EditText) findViewById(R.id.et_phone);
        et_node = findViewById(R.id.next_node);
        tv_node_code = findViewById(R.id.next_node_code);
        ex_addr = findViewById(R.id.rev_addr);


    }

    /**
     * 从外界Activity为Dialog设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        titleStr = title;
    }

    public void setNode(String title) {
        skipStr = title;
    }

    public void setCode(String code){
        codeStr = code;
    }
    public void setAddr(String addr){
        addrStr = addr;
    }
    /**
     * 从外界Activity为Dialog设置dialog的message
     *
     * @param message
     */
    public void setMessage(String message) {
        messageStr = message;
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesOnclickListener {
        public void onYesClick(String phone);
    }

    public interface onNoOnclickListener {
        public void onNoClick();
    }
    public interface onSkipOnclickListener {
        public void onSkipClick();
    }

    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width= ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height= ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);
    }
}
