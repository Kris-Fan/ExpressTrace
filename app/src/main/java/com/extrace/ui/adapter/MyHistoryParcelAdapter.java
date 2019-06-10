package com.extrace.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.extrace.net.OkHttpClientManager;
import com.extrace.net.json.MyJsonManager;
import com.extrace.ui.R;
import com.extrace.ui.entity.CustomerInfo;
import com.extrace.ui.entity.ExpressSheet;
import com.extrace.ui.service.LoginService;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.extrace.net.OkHttpClientManager.BASE_URL;

/**
 * 我的包裹适配器、历史 与我相关的包裹
 */
public class MyHistoryParcelAdapter extends RecyclerView.Adapter<MyHistoryParcelAdapter.ViewHolder> implements View.OnClickListener {
    public List<ExpressSheet> list=new ArrayList<>();
    private CustomerInfo customerInfo;
    public Context con;
    public  LayoutInflater inflater;
    public MyHistoryParcelAdapter(List<ExpressSheet> list, Context con){
        this.con=con;
        this.list=list;
        inflater=LayoutInflater.from(con);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= inflater.inflate(R.layout.fragment_express_rev_task_item,null);
        ViewHolder viewHolder=new ViewHolder(view);
        view.setOnClickListener(this);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.tv_name.setText("rcv："+list.get(position).getRecever());
        holder.tv_tel.setText("type："+list.get(position).getType());
        holder.tv_addr.setText("addr："+list.get(position).getAcceptetime());
        holder.tv_st.setText("status:"+list.get(position).getStatus());
        final int index=position;
       final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Log.e("lalal", "handleMessage: 进入handler");
                super.handleMessage(msg);
                Bundle db=msg.getData();
                String info=db.getString("customer");
                CustomerInfo customerInfo= new MyJsonManager().CustomerInfoJson(info);

                holder.tv_name.setText(customerInfo.getName());

            }
        };
        new Thread(){
            @Override
            public void run() {
                super.run();
                Message msg=new Message();
                try {
                    OkHttpClientManager okHttpClientManager = new OkHttpClientManager(new LoginService().userInfoSha256(con));
                    Response response = okHttpClientManager.getAsyn(BASE_URL+"/ExtraceSystem/customerInfo/"+list.get(index).getRecever());
                    if (response.code() ==200){
                            Log.e("lalal", "handleMessage: 进入线程");
                            Bundle dab=new Bundle();
                            dab.putString("customer",response.body().string());
                            msg.setData(dab);
                            handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    Log.e("lala","请求异常！"+e.toString());
                }
            }
        }.start();


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener!=null){
            mItemClickListener.onItemClick((Integer) v.getTag());
        }
    }
    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }
    private OnItemClickListener mItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_name;
        public TextView tv_tel;
        public TextView tv_addr;
        public TextView tv_time;
        public TextView tv_st;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_name= itemView.findViewById(R.id.name);
            tv_tel = itemView.findViewById(R.id.tel);
            tv_addr = itemView.findViewById(R.id.addr);
            tv_time = itemView.findViewById(R.id.time);
            tv_st = itemView.findViewById(R.id.st);
        }
    }

}

