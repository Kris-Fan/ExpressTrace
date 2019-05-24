package com.extrace.ui.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.extrace.ui.entity.History;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryOperator {
    private DBHelper dbHelper;

    public HistoryOperator(Context context) {

        dbHelper = new DBHelper(context);
    }

    /**
     * 插入数据
     *
     * @param history
     * @return
     */
    public boolean insert(History history) {
        //与数据库建立连接
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(History.KEY_title, history.expressId);
        contentValues.put(History.KEY_context, history.context);
        contentValues.put(History.KEY_time, history.time);
        //插入每一行数据
        long history_id = db.insert(History.TABLE, null, contentValues);
        db.close();
        if (history_id != -1)
            return true;
        else
            return false;
    }

    /**
     * 删除数据
     *
     * @param history_id
     */
    public void delete(int history_id) {
        //与数据库建立连接
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(History.TABLE, History.KEY_id + "=?", new String[]{String.valueOf(history_id)});
        db.close();
    }

    /**
     * 从数据库中查找 id，title，context
     *
     * @return ArrayList
     */
    public ArrayList<HashMap<String, String>> getHistoryList() {
        //与数据库建立连接
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "select " + History.KEY_id + "," + History.KEY_title + "," +History.KEY_time+","+ History.KEY_context +
                " from " + History.TABLE;
        //通过游标将每一条数据放进ArrayList中
        ArrayList<HashMap<String, String>> historyList = new ArrayList<HashMap<String, String>>();
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            HashMap<String, String> history = new HashMap<String, String>();
            history.put("id", cursor.getString(cursor.getColumnIndex(History.KEY_id)));
            history.put("title", cursor.getString(cursor.getColumnIndex(History.KEY_title)));
            history.put("time", cursor.getString(cursor.getColumnIndex(History.KEY_time)));
            history.put("context", cursor.getString(cursor.getColumnIndex(History.KEY_context)));
            historyList.add(history);
        }
        cursor.close();
        db.close();
        return historyList;
    }

    /**
     * 通过id查找，返回一个History对象
     *
     * @param id
     * @return
     */
    public History getHistoryById(int id) {
        //与数据库建立连接
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "select " + History.KEY_title + "," + History.KEY_context +
                " from " + History.TABLE + " where " + History.KEY_id + "=?";
        History history = new History();
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});
        while (cursor.moveToNext()) {
            history.expressId = cursor.getString(cursor.getColumnIndex(History.KEY_title));
            history.context = cursor.getString(cursor.getColumnIndex(History.KEY_context));
        }
        cursor.close();
        db.close();
        return history;
    }

    /**
     * 更新数据
     *
     * @param history
     */
    public void update(History history) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(History.KEY_title, history.expressId);
        contentValues.put(History.KEY_context, history.context);

        db.update(History.TABLE, contentValues, History.KEY_id + "=?", new String[]{String.valueOf(history.id)});
        db.close();
    }
}