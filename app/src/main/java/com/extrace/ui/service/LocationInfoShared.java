package com.extrace.ui.service;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class LocationInfoShared {
    public static void saveLatLngInfo(Context context, String info){

//      SharedPreferences将用户的位置坐标数据存储到该包下的shared_prefs/locataion.xml文件中，
//      并且设置该文件的读取方式为私有，即只有该软件自身可以访问该文件

        SharedPreferences sPreferences=context.getSharedPreferences("latLng", MODE_PRIVATE);
        SharedPreferences.Editor editor=sPreferences.edit();

        editor.putString("LatLng", info);

        editor.apply();
    }
    public static String getLatLngInfo(Context context){
        SharedPreferences sPreferences=context.getSharedPreferences("latLng", MODE_PRIVATE);
        return sPreferences.getString("LatLng","");
    }
    public static boolean clearLatLng(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("latLng",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.putString("LatLng", "");
        editor.clear();
        return editor.commit();
    }
}
