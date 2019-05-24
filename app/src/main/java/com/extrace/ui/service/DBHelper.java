package com.extrace.ui.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.extrace.ui.entity.History;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="todo.db";
    public DBHelper(Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 创建一个数据库，建表，只在第一次时调用
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql="create table "+ History.TABLE+"("
                +History.KEY_id+" integer primary key autoincrement, "
                +History.KEY_title+" text, "
                +History.KEY_time+" text, "
                +History.KEY_context+" text)";
        sqLiteDatabase.execSQL(sql);
    }

    /**
     * 作用：更新数据库表结构
     * 调用时机：数据库版本发生变化的时候回调（取决于数据库版本）
     * 创建SQLiteOpenHelper子类对象的时候,必须传入一个version参数
     //该参数就是当前数据库版本, 只要这个版本高于之前的版本, 就会触发这个onUpgrade()方法
     * @param sqLiteDatabase
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists "+History.TABLE);
        onCreate(sqLiteDatabase);
    }
}