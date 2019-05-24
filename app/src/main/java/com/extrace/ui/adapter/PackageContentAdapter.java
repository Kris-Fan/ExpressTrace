package com.extrace.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.extrace.ui.entity.ExpressSheet;
import com.extrace.ui.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PackageContentAdapter extends RecyclerView.Adapter<PackageContentAdapter.ViewHolder> {
    //数据源
    public ArrayList<ExpressSheet> mList;
    public Context con;
    public  LayoutInflater inflater;
    public PackageContentAdapter(ArrayList<ExpressSheet> list,Context con) {
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= inflater.inflate(R.layout.item_package,null);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    //填充视图
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mView.setText(mList.get(position).getId());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView.findViewById(R.id.expressid);
        }
    }
}
