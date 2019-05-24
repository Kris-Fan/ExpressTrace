package com.extrace.ui.service;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 控制所有Activity
 */
public class ActivityCollector {
    private static final String TAG = "ActivityCollector";
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    public static void finishAll(){
        for (Activity activity : activities){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
