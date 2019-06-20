package com.extrace.ui.main;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.extrace.net.json.GetJsonDataUtil;
import com.extrace.ui.R;
import com.extrace.ui.entity.Province;
import com.extrace.ui.service.LoginService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.extrace.net.OkHttpClientManager.BASE_URL;

/**
 * 新建客户信息页
 */
public class CustomerEditActivity extends AppCompatActivity {
    String name,tel,addr,dpt,postcode;
    private EditText unedtaddr;
    //  省
    private List<Province> options1Items = new ArrayList<Province>();
    //  市
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    //  区
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_edit_);
        unedtaddr = findViewById(R.id.unedtAddr);
        unedtaddr.setFocusable(false);
        unedtaddr.setFocusableInTouchMode(false);
        unedtaddr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              解析数据
                parseData();
//              展示省市区选择器
                showPickerView();
            }
        });
    }
    public void click(View v){
        EditText edtname=(EditText) findViewById(R.id.edtName);
        name= edtname.getText().toString();
        EditText edttelcode=(EditText) findViewById(R.id.edtTelCode);
        tel= edttelcode.getText().toString();
        EditText unedtaddr=(EditText)findViewById(R.id.unedtAddr);
        addr=unedtaddr.getText().toString();
        EditText edtdpt=(EditText) findViewById(R.id.edtDpt);
        dpt= edtdpt.getText().toString();
        EditText edtpostcode=(EditText) findViewById(R.id.edtPostCode);
        postcode= edtpostcode.getText().toString();

        new Thread() {
            @Override
            public void run() {
                boolean result=init(name,tel,dpt,addr,postcode);
                if(result){
                    Intent intent = new Intent();
                    intent.putExtra("position",-1);
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        }.start();
    }

    private boolean init(String name,String tel,String dpt,String addr,String postcode){

        String urlPath=BASE_URL+"/ExtraceSystem/addCusInfo";
        URL url;
        int id=0;
        try {
            url=new URL(urlPath);
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("name",name);
            jsonObject.put("telcode",tel);
            jsonObject.put("department",dpt);
            jsonObject.put("address",addr);
            jsonObject.put("postcode",postcode);

            String content=String.valueOf(jsonObject);//json串转string类型

            HttpURLConnection conn=(HttpURLConnection) url.openConnection(); //开启连接
            conn.setConnectTimeout(5000);

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("ser-Agent", "Fiddler");
            conn.setRequestProperty("userId",new LoginService().userInfoSha256(this));
            conn.setRequestProperty("Content-Type","application/json");

            //写输出流，将要转的参数写入流里
            OutputStream os=conn.getOutputStream();
            os.write(content.getBytes()); //字符串写进二进流
            os.close();

            int code=conn.getResponseCode();
            if(code==200){   //与后台交互成功返回 200
                return true;
            }else{
                Toast.makeText (getApplicationContext(),"数据提交失败", Toast.LENGTH_LONG ).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
    /**
     * 解析数据并组装成自己想要的list
     */
    private void parseData(){
        String jsonStr = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据
//     数据解析
        Gson gson =new Gson();
        java.lang.reflect.Type type =new TypeToken<List<Province>>(){}.getType();
        List<Province>shengList=gson.fromJson(jsonStr, type);
//     把解析后的数据组装成想要的list
        options1Items = shengList;
//     遍历省
        for(int i = 0; i <shengList.size() ; i++) {
//         存放城市
            ArrayList<String> cityList = new ArrayList<>();
//         存放区
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();
//         遍历市
            for(int c = 0; c <shengList.get(i).city.size() ; c++) {
//        拿到城市名称
                String cityName = shengList.get(i).city.get(c).name;
                cityList.add(cityName);

                ArrayList<String> city_AreaList = new ArrayList<>();//该城市的所有地区列表
                if (shengList.get(i).city.get(c).area == null || shengList.get(i).city.get(c).area.size() == 0) {
                    city_AreaList.add("");
                } else {
                    city_AreaList.addAll(shengList.get(i).city.get(c).area);
                }
                province_AreaList.add(city_AreaList);
            }
            /**
             * 添加城市数据
             */
            options2Items.add(cityList);
            /**
             * 添加地区数据
             */
            options3Items.add(province_AreaList);
        }

    }

    /**
     * 展示选择器
     */
    private void showPickerView() {// 弹出选择器

        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).name +
                        options2Items.get(options1).get(options2) +
                        options3Items.get(options1).get(options2).get(options3);

                //Toast.makeText(CustomerInfo.this, tx, Toast.LENGTH_SHORT).show();
                unedtaddr.setText(tx);
            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setCancelColor(getResources().getColor(R.color.colorPrimary))
                .setSubmitColor(getResources().getColor(R.color.colorAccent))
                .setTextColorCenter(getResources().getColor(R.color.colorPrimary)) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }
}
