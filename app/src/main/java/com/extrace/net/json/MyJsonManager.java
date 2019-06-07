package com.extrace.net.json;

import android.content.Context;
import android.util.Log;

import com.extrace.ui.entity.CustomerInfo;
import com.extrace.ui.entity.ExpressSheet;
import com.extrace.ui.entity.Trace;
import com.extrace.ui.entity.TransNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyJsonManager {


    private static final String TAG = "MyJsonManager";
    public List<Map<String, Object>> CustomerInfoJsonJXData(String data) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (data != null) {
            try {
//                JSONArray array = new JSONArray(date);
//
//                for (int i = 0; i < array.length(); i++) {
//                    JSONObject object = array.getJSONObject(i);
//                    title = object.getString("code");
//                    Log.i("TESTJSON_jsonJxDate", "name="+ title);
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("code", title);
//                    list.add(map);
//
//                }
                JSONObject jsonObject = new JSONObject(data);
                Log.i("TESTJSON", "code=" + jsonObject.getString("code"));
                Log.i("TESTJSON", "msg=" + jsonObject.getString("msg"));
                JSONObject objectExtends = jsonObject.getJSONObject("extend");
                JSONArray jsonArrayInfo = objectExtends.getJSONArray("customerInfos");
                JSONArray jsonArray1 = jsonObject.getJSONObject("extend").getJSONArray("customerInfos");

                for (int i = 0; i < jsonArrayInfo.length(); i++) {
                    JSONObject object = jsonArrayInfo.getJSONObject(i);
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", object.getInt("id"));
                    map.put("name", object.getString("name"));
                    map.put("telcode", object.getString("telcode"));
                    map.put("department", object.getString("department"));
                    map.put("regioncode", object.getString("regioncode"));
                    map.put("address", object.getString("address"));
                    map.put("postcode", object.getString("postcode"));
                    list.add(map);
                }
            } catch (JSONException e) {
                Log.e("customerinfojson", "异常：jsonJXDate");
                e.printStackTrace();
            }

        }
        return list;
    }


    public List<CustomerInfo> customerInfoListJsonJx(String data){
        List<CustomerInfo> list = new ArrayList<>();
        if (data != null){
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONObject objectExtends = jsonObject.getJSONObject("extend");
                JSONArray jsonArrayInfo = objectExtends.getJSONArray("customerInfos");

                Gson gson = new Gson();
                list = gson.fromJson(jsonArrayInfo.toString(), new TypeToken<List<CustomerInfo>>(){}.getType());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return list;
    }
    public List<ExpressSheet> historyPackageJsonJXData(String data) {
        List<ExpressSheet> list=new ArrayList<>();
        if (data != null){
            try {
                JSONObject jo=new JSONObject(data);
                JSONArray info=jo.getJSONArray("info");
                Log.e("lalal","info == "+info);
                String sinfo=info.toString();
                Log.e("lalal","sinfo == "+sinfo);
                Gson gson=new Gson();
                list=gson.fromJson(sinfo,new TypeToken<List<ExpressSheet>>(){}.getType());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    public CustomerInfo CustomerInfoJson(String data) {
        Gson gson=new Gson();
        return gson.fromJson(data,CustomerInfo.class);
    }
    public ExpressSheet ExpressSheetInfo(String data){
        ExpressSheet expressSheet = null;
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject jsonObject1 = jsonObject.getJSONObject("extend");
            JSONObject jsonObject2 = jsonObject1.getJSONObject("expressSheet");
            String info = jsonObject2.toString();
            Gson gson = new Gson();
            expressSheet = gson.fromJson(info, ExpressSheet.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return expressSheet;
    }

    public List<TransNode> TransNodeJson(Context context) {
        String jsonStr = new GetJsonDataUtil().getJson(context, "transnode.json");//获取assets目录下的json文件数据
        List<TransNode> list=new ArrayList<>();
        if (jsonStr != null){
            Log.e("lalal","网点 == "+jsonStr);
            Gson gson=new Gson();
            list=gson.fromJson(jsonStr,new TypeToken<List<TransNode>>(){}.getType());

            Log.e("lala","数组："+list.toString());

        }
        return list;
    }

    //查询快递，追踪快递历史的解析
    public List<Trace> traceListJsonJX(String data){
        List<Trace> traceList = new ArrayList<>();
        try {
            if (data != null && !data.isEmpty()){
                Gson gson = new Gson();
                traceList = gson.fromJson(data, new TypeToken<List<Trace>>(){}.getType());
                Log.d(TAG, "traceListJsonJX: 追踪到的快递信息："+traceList);
            }
        }catch (JsonSyntaxException e){
            traceList = null;
            e.printStackTrace();
        }

        return traceList;
    }

    /**
     * 派送列表json解析
     * @param data
     * @return
     */
    public List<Map<String, Object>> PaisongJsonJXData(String data) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (data != null) {
            try {
                JSONArray jsonArrayInfo = new JSONArray(data);

                for (int i = 0; i < jsonArrayInfo.length(); i++) {
                    JSONObject object = jsonArrayInfo.getJSONObject(i);
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", object.getString("name"));
                    map.put("telcode", object.getString("telcode"));
                    map.put("address", object.getString("address"));
                    list.add(map);
                }
            } catch (JSONException e) {
                Log.e("customerinfojson", "异常：jsonJXDate");
                e.printStackTrace();
            }

        }
        return list;
    }
}