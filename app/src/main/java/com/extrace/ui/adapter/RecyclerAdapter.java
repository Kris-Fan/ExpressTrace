package com.extrace.ui.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.extrace.ui.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{
    public List<Map<String,Object>> list;
    public Context con;
    public String type;
    public  LayoutInflater inflater;
    public RecyclerAdapter(List<Map<String,Object>> list, Context con, String type){
        this.con=con;
        this.list=list;
        this.type = type;
        inflater=LayoutInflater.from(con);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= inflater.inflate(R.layout.fragment_list_extask_item,null);
        ViewHolder viewHolder=new ViewHolder(view);
        // view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.tv_name.setText(list.get(position).get("name").toString());
        holder.tv_tel.setText(list.get(position).get("telcode").toString());
        holder.tv_addr.setText("地址："+list.get(position).get("address").toString());
        if(type.equals("paisong")) {
            holder.tv_expressId.setText("快递单号：" + list.get(position).get("expressId").toString());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tv_name;
        private TextView tv_tel;
        private TextView tv_addr;
        private TextView tv_msg;
        private TextView tv_phone;
        private TextView tv_expressId;
        private ViewHolder(View itemView) {
            super(itemView);
            tv_name= itemView.findViewById(R.id.name);
            tv_tel = itemView.findViewById(R.id.tel);
            tv_addr = itemView.findViewById(R.id.addr);
            tv_msg = itemView.findViewById(R.id.msg);
            tv_phone = itemView.findViewById(R.id.phone);
            tv_expressId = itemView.findViewById(R.id.expressId);
            Drawable drawableLeft = itemView.getResources().getDrawable(R.mipmap.ic_sender);
            if(type.equals("weilanshou")) {
                tv_expressId.setText("揽收");
                tv_expressId.setGravity(Gravity.CENTER);
                tv_expressId.setBackground(con.getResources().getDrawable(R.drawable.bg_round_normal_green));
                tv_expressId.setTextColor(con.getResources().getColor(R.color.white));
                tv_expressId.setOnClickListener(this);
            }else if(type.equals("yilanshou")){
                tv_expressId.setHeight(0);
                tv_expressId.setVisibility(View.INVISIBLE);
            }else {
                drawableLeft = itemView.getResources().getDrawable(R.mipmap.ic_receiver);
            }
            tv_name.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,null,null,null);

            tv_msg.setOnClickListener(this);
            tv_phone.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (mItemClickListener!=null){
                mItemClickListener.onItemClick(v,getAdapterPosition());
            }
        }
    }


    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }
    private OnItemClickListener mItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

}

