package com.extrace.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.extrace.ui.R;
import com.extrace.ui.entity.ExpressSheet;

import java.util.ArrayList;

public class WrapPackageAdapter extends RecyclerView.Adapter<WrapPackageAdapter.ViewHolder> {
    //数据源
    private ArrayList<ExpressSheet> mList;
    public Context con;
    public LayoutInflater inflater;
    public WrapPackageAdapter(ArrayList<ExpressSheet> list, Context con) {
        this.con=con;
        this.mList=list;
        inflater=LayoutInflater.from(con);
    }

    //返回item个数
    @Override
    public int getItemCount() {
        return mList.size() ;
    }

    //创建ViewHolder
    @Override
    public WrapPackageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= inflater.inflate(R.layout.item_wrap_package,null);
        WrapPackageAdapter.ViewHolder viewHolder=new WrapPackageAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mView.setText(mList.get(position).getId());
        holder.status.setText(mList.get(position).getStatus());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mView;
        private TextView status;

        private ViewHolder(View itemView) {
            super(itemView);
            mView = itemView.findViewById(R.id.expressid);
            status = itemView.findViewById(R.id.expressStatus);
        }
    }
}
