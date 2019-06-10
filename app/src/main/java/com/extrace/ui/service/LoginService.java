package com.extrace.ui.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.extrace.net.HashCode;
import com.extrace.ui.entity.UserInfo;

import static android.content.Context.MODE_PRIVATE;

public class LoginService {
    public LoginService() {
    }

    /**
     * 保存用户名 密码的业务方法
     * @param context 上下文
     * @param username 用户名
     * @param pas 密码
     */
    public static void saveUserInfo(Context context, String username, String pas, Boolean ch){
        //SharedPreferences将用户的数据存储到该包下的shared_prefs/config.xml文件中，
        // 并且设置该文件的读取方式为私有，即只有该软件自身可以访问该文件
        SharedPreferences sPreferences=context.getSharedPreferences("config", context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sPreferences.edit();
        //当然sharepreference会对一些特殊的字符进行转义，使得读取的时候更加准确
        editor.putString("username", username);
        editor.putString("password", pas);
        //这里我们输入一些特殊的字符来实验效果
        //editor.putString("specialtext", "hajsdh><?//");
        editor.putBoolean("or", ch);
        //editor.putInt("int", 47);
        //切记最后要使用commit方法将数据写入文件
        editor.apply();
    }

    public static void saveUserInfo(Context context, UserInfo userInfo, Boolean ch,Boolean keepLogin){
        SharedPreferences sPreferences=context.getSharedPreferences("USER_INFO", context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sPreferences.edit();

        if (userInfo == null){
            editor.clear();
        }else {

            editor.putInt("uid", userInfo.getUid());
            editor.putString("username", userInfo.getName());
            if (ch) {    //是否保存密码
                editor.putString("password", userInfo.getPwd());
            } else {
                editor.putString("password", "");
            }
            editor.putString("telcode", userInfo.getTelcode());
            editor.putInt("urull", userInfo.getUrull());
            editor.putInt("status", userInfo.getStatus());
            editor.putString("dptid", userInfo.getDptid());
            editor.putString("receivepackageid", userInfo.getReceivepackageid());
            editor.putString("delivepackageid", userInfo.getDelivepackageid());
            editor.putString("transpackageid", userInfo.getTranspackageid());

            editor.putBoolean("or", ch);
            editor.putBoolean("keepLoginSta", keepLogin);
            //editor.putInt("int", 47);
        }
        //切记最后要使用commit方法将数据写入文件
        editor.apply();

    }
    public int getUserId(Context context){
        SharedPreferences sPreferences=context.getSharedPreferences("USER_INFO", MODE_PRIVATE);
        return sPreferences.getInt("uid",-1);
    }
    public int getUserRoll(Context context){
        SharedPreferences sPreferences=context.getSharedPreferences("USER_INFO", MODE_PRIVATE);
        return sPreferences.getInt("urull",-1);
    }
    public boolean isLogined(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("USER_INFO",MODE_PRIVATE);
        return sharedPreferences.getBoolean("keepLoginSta",false) && sharedPreferences.getInt("uid",-1)!=-1;
    }
    //退出账号
    public void logout(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("USER_INFO",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("keepLoginSta",false);
        editor.commit();
    }
    //返回加密后的用戶名+密碼值
    public String userInfoSha256(Context context){
        SharedPreferences sPreferences=context.getSharedPreferences("USER_INFO", MODE_PRIVATE);
        String useT = sPreferences.getString("username","");
        String userPassword = sPreferences.getString("password","");
        HashCode hashCode = new HashCode();
        return hashCode.getSHA256(useT+userPassword);
    }
}
