package com.extrace.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
public class PackageTansRecyclerAdapter extends RecyclerView.Adapter<PackageTansRecyclerAdapter.ViewHolder> implements View.OnClickListener {
    private static final String TAG = "PackageTansRecyclerAdap";
    public List<Map<String,Object>> list=new ArrayList<>();
    public Context con;
    public  LayoutInflater inflater;
    public PackageTansRecyclerAdapter(List<Map<String,Object>> list, Context con){
        this.con=con;
        this.list=list;
        inflater=LayoutInflater.from(con);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= inflater.inflate(R.layout.fragment_package_trans_task_item,null);
        ViewHolder viewHolder=new ViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: 快递任务RevTask");
        holder.former.setText("上一网点："+list.get(position).get("上一网点").toString());
        holder.latter.setText("下一网点："+list.get(position).get("下一网点").toString());
        holder.packageId.setText("包裹编号："+list.get(position).get("包裹id").toString());
        holder.size.setText(list.get(position).get("包裹大小").toString());
        holder.previous.setTag(position);
        holder.next.setTag(position);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
        holder.previous.setOnClickListener(this);
        holder.next.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener!=null){
            mItemClickListener.onItemClick(v,(Integer) v.getTag());
        }
    }
    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }
    private OnItemClickListener mItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView former;
        public TextView latter;
        public TextView previous;
        public TextView next;
        public TextView packageId;
        public TextView size;


        public ViewHolder(View itemView) {
            super(itemView);
            former= itemView.findViewById(R.id.former);
            latter = itemView.findViewById(R.id.latter);
            previous = itemView.findViewById(R.id.callprevious);
            next = itemView.findViewById(R.id.callnext);
            packageId = itemView.findViewById(R.id.packageId);
            size = itemView.findViewById(R.id.size);
        }
    }

}


