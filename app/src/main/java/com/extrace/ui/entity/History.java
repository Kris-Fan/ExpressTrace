package com.extrace.ui.entity;

/**
 * 在本应用中涉及到了待办事项的id,title和context，所以建立一个方法将他们封装起来，方便后面的使用
 */
public class History {
    //表名
    public static final String TABLE = "scanHistory";
    //列名
    public static final String KEY_id="id";
    public static final String KEY_title="expressId";
    public static final String KEY_time="time";
    public static final String KEY_context="context";

    public int id;
    public String expressId;
    public String time;
    public String context;
}