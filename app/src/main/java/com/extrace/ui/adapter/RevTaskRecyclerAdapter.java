package com.extrace.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.extrace.ui.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 接受任务的适配器
 */
public class RevTaskRecyclerAdapter extends RecyclerView.Adapter<RevTaskRecyclerAdapter.ViewHolder> implements View.OnClickListener {
    public List<Map<String,Object>> list=new ArrayList<>();
    public Context con;
    public  LayoutInflater inflater;
    public RevTaskRecyclerAdapter(List<Map<String,Object>> list, Context con){
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
        holder.tv_name.setText("姓名："+list.get(position).get("name").toString());
        holder.tv_tel.setText("电话："+list.get(position).get("telcode").toString());
        holder.tv_addr.setText("地址："+list.get(position).get("address").toString());
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

