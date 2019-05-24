package com.extrace.util;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.extrace.ui.R;

public class TitleLayout extends LinearLayout {
    private TextView titleBack;
    private TextView titleEdit;
    private TextView titleText;
    private RelativeLayout background;
    public TitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.title,this);
        titleBack = findViewById(R.id.title_back);
        titleEdit = findViewById(R.id.title_edit);
        titleText = findViewById(R.id.title_text);
        background = findViewById(R.id.ly_bg);
        titleBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) getContext()).finish();
            }
        });
        titleEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "click edit", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setTitleBack(String title){
        titleBack.setText(title);
    }
    public void setTitleEdit(String title){
        titleEdit.setText(title);
    }

    public void setTitle(String title) {
        titleText.setText(title);
    }

    public void hideTitleEdit(){
        titleEdit.setVisibility(INVISIBLE);
    }

    public void setBackground(int color){
        background.setBackgroundColor(color);
    }

    public void  setTitleColor(int color){
        titleBack.setTextColor(color);
        titleText.setTextColor(color);
        titleEdit.setTextColor(color);
    }
}
