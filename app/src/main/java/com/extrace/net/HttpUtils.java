package com.extrace.net;

import android.os.Handler;
import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;

public class HttpUtils {

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");//mdiatype 这个需要和服务端保持一致
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");//mdiatype 这个需要和服务端保持一致
    private static final String TAG = HttpUtils.class.getSimpleName();
    private static final String BASE_URL = "http://xxx.com/openapi";//请求接口根地址
    private static volatile HttpUtils mInstance;//单利引用
    public static final int TYPE_GET = 0;//get请求
    public static final int TYPE_POST_JSON = 1;//post请求参数为json
    public static final int TYPE_POST_FORM = 2;//post请求参数为表单
    private OkHttpClient mOkHttpClient;//okHttpClient 实例
    private Handler okHttpHandler;//全局处理子线程和M主线程通信

    public static String getStringByOkhttp(String path) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get().url(path).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                return body.string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}






//
//    /**
//     *  okHttp同步请求统一入口
//     * @param actionUrl  接口地址
//     * @param requestType 请求类型
//     * @param paramsMap   请求参数
//     */
//    public void requestSyn(String actionUrl, int requestType, HashMap<String, String> paramsMap) {
//        switch (requestType) {
//            case TYPE_GET:
//                requestGetBySyn(actionUrl, paramsMap);
//                break;
//            case TYPE_POST_JSON:
//                requestPostBySyn(actionUrl, paramsMap);
//                break;
//            case TYPE_POST_FORM:
//                requestPostBySynWithForm(actionUrl, paramsMap);
//                break;
//        }
//    }
//
//    /**
//     * okHttp get同步请求
//     * @param actionUrl  接口地址
//     * @param paramsMap   请求参数
//     */
//    private void requestGetBySyn(String actionUrl, HashMap<String, String> paramsMap) {
//        StringBuilder tempParams = new StringBuilder();
//        try {
//            //处理参数
//            int pos = 0;
//            for (String key : paramsMap.keySet()) {
//                if (pos > 0) {
//                    tempParams.append("&");
//                }
//                //对参数进行URLEncoder
//                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
//                pos++;
//            }
//            //补全请求地址
//            String requestUrl = String.format("%s/%s?%s", BASE_URL, actionUrl, tempParams.toString());
//            //创建一个请求
//            Request request = addHeaders().url(requestUrl).build();
//            //创建一个Call
//            final Call call = mOkHttpClient.newCall(request);
//            //执行请求
//            final Response response = call.execute();
//            response.body().string();
//        } catch (Exception e) {
//            Log.e(TAG, e.toString());
//        }
//    }
//
//    /**
//     * okHttp post同步请求
//     * @param actionUrl  接口地址
//     * @param paramsMap   请求参数
//     */
//    private void requestPostBySyn(String actionUrl, HashMap<String, String> paramsMap) {
//        try {
//            //处理参数
//            StringBuilder tempParams = new StringBuilder();
//            int pos = 0;
//            for (String key : paramsMap.keySet()) {
//                if (pos > 0) {
//                    tempParams.append("&");
//                }
//                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
//                pos++;
//            }
//            //补全请求地址
//            String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
//            //生成参数
//            String params = tempParams.toString();
//            //创建一个请求实体对象 RequestBody
//            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params);
//            //创建一个请求
//            final Request request = addHeaders().url(requestUrl).post(body).build();
//            //创建一个Call
//            final Call call = mOkHttpClient.newCall(request);
//            //执行请求
//            Response response = call.execute();
//            //请求执行成功
//            if (response.isSuccessful()) {
//                //获取返回数据 可以是String，bytes ,byteStream
//                Log.e(TAG, "response ----->" + response.body().string());
//            }
//        } catch (Exception e) {
//            Log.e(TAG, e.toString());
//        }
//    }
//
//    /**
//     * okHttp post同步请求表单提交
//     * @param actionUrl 接口地址
//     * @param paramsMap 请求参数
//     */
//    private void requestPostBySynWithForm(String actionUrl, HashMap<String, String> paramsMap) {
//        try {
//            //创建一个FormBody.Builder
//            FormBody.Builder builder = new FormBody.Builder();
//            for (String key : paramsMap.keySet()) {
//                //追加表单信息
//                builder.add(key, paramsMap.get(key));
//            }
//            //生成表单实体对象
//            RequestBody formBody = builder.build();
//            //补全请求地址
//            String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
//            //创建一个请求
//            final Request request = addHeaders().url(requestUrl).post(formBody).build();
//            //创建一个Call
//            final Call call = mOkHttpClient.newCall(request);
//            //执行请求
//            Response response = call.execute();
//            if (response.isSuccessful()) {
//                Log.e(TAG, "response ----->" + response.body().string());
//            }
//        } catch (Exception e) {
//            Log.e(TAG, e.toString());
//        }
//    }
