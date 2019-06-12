package com.extrace.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.*;

import com.extrace.ui.R;
import com.extrace.ui.main.CustomerInfoDetailActivity;
import com.extrace.util.layout.SortModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomerManageAdapter extends RecyclerView.Adapter<CustomerManageAdapter.DataViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private Context mContext;
    private RecyclerView recyclerView;
    private LayoutInflater mInflater;
    private List<SortModel> mData;
    public List<Map<String,Object>> mList=new ArrayList<>();

    public CustomerManageAdapter() {}

    public CustomerManageAdapter(List<Map<String,Object>> mList,Context mContext) {
        this.mContext = mContext;
        this.mList = mList;
    }
    public CustomerManageAdapter(Context context, List<SortModel> data,List<Map<String,Object>> mList) {
        mInflater = LayoutInflater.from(context);
        mData = data;
        this.mList=mList;
        this.mContext = context;
    }
    /**
     * 用于创建ViewHolder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_customer_manage,null);
        //使用代码设置宽高（xml布局设置无效时）
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        DataViewHolder holder = new DataViewHolder(view);
        return holder;
    }

    /**
     * 绑定数据
     */
    @Override
    public void onBindViewHolder(final DataViewHolder holder, final int position) {
        holder.tv_data1.setText(this.mData.get(position).getName());
        holder.tv_data2.setText(this.mData.get(position).getTelcode());
        holder.tv_more.setText(this.mData.get(position).getAddress()+"，"+mData.get(position).getDepartment());
        holder.radioButton.setActivated(false);
        holder.itemView.setTag(position);
        holder.radioButton.setTag(position);
        if(isShow) {//通过isShow去控制图标的显示
            holder.radioButton.setVisibility(View.VISIBLE);
        }else {
            holder.radioButton.setVisibility(View.GONE);
        }
       // holder.ly_customer.setVisibility(typeShow ? View.VISIBLE : View.GONE);
        //holder.ly_node_list.setVisibility(typeShow ? View.GONE : View.VISIBLE);
        if (!typeShow){
//            holder.ly_customer.setVisibility(View.GONE);
//            holder.ly_node_list.setVisibility(View.VISIBLE);
//            holder.node_title.setText(this.mData.get(position).getName());
            holder.tv_more.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.GONE);
        }


//        holder.tv_data1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(mContext,CustomerInfoDetailActivity.class);//你要跳转的界面
//                intent.putExtra("id",mData.get(position).getId());
//                intent.putExtra("name",mData.get(position).getName());
//                intent.putExtra("telcode",mData.get(position).getTelcode());
//                intent.putExtra("addr",mData.get(position).getAddress());
//                intent.putExtra("dpt",mData.get(position).getDepartment());
//                intent.putExtra("postcode",mData.get(position).getPostcode());
//                mContext.startActivity(intent);
//            }
//        });
    }

    /**
     * 选项总数
     * @return
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public boolean onLongClick(View view) {
        int position = (int) view.getTag();
        //程序执行到此，会去执行具体实现的onItemClick()方法
        if (mOnItemLongClickListener!=null){
            switch (view.getId()){
                case R.id.recy:
                    mOnItemLongClickListener.onItemLongClick(ViewName.PRACTISE,view,position,mData.get(position));
                    break;
                default:
                    mOnItemLongClickListener.onItemLongClick(ViewName.ITEM,view,position,mData.get(position));
                    break;
            }
        }
        return false;
    }

    //item里面有多个控件可以点击（item+item内部控件）
    public enum ViewName {
        ITEM,
        PRACTISE
    }
    @Override
    public void onClick(View view) {
        //根据RecyclerView获得当前View的位置
        //int position = recyclerView.getChildAdapterPosition(view);
        int position = (int) view.getTag();
        //程序执行到此，会去执行具体实现的onItemClick()方法
        if (mOnItemClickListener!=null){
            switch (view.getId()){
                case R.id.recy:
                    mOnItemClickListener.onItemClick(ViewName.PRACTISE,view,position,mData.get(position));
                    break;
                default:
                    mOnItemClickListener.onItemClick(ViewName.ITEM,view,position,mData.get(position));
                    break;
            }
        }
    }

    //**********************itemClick************************
    public interface OnItemClickListener {
       // void onItemClick(View view, int position);
        //void onItemClick(ViewName parent,View view, int position, Map<String,Object> data);
        void onItemClick(ViewName parent,View view, int position, SortModel data);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
    //**************************************************************


    //**********************itemLongClick************************
    public interface OnItemLongClickListener {
        // void onItemLongClick(View view, int position);
        //void onItemLongClick(ViewName parent,View view, int position, Map<String,Object> data);
        void onItemLongClick(ViewName parent,View view, int position, SortModel data);
    }

    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }
    //**************************************************************

    /**
     * 创建ViewHolder
     */
    public class DataViewHolder extends RecyclerView.ViewHolder{
        LinearLayout ly_customer,ly_node_list;
        TextView node_title;
        TextView tv_data1;
        TextView tv_data2;
        TextView tv_more;
        ImageView imageView;
        RadioButton radioButton;
        public DataViewHolder(View itemView) {
            super(itemView);
            ly_customer = itemView.findViewById(R.id.ly_customer_manage);
            ly_node_list = itemView.findViewById(R.id.ly_node_list);
            node_title = itemView.findViewById(R.id.node_title);

            imageView = itemView.findViewById(R.id.image);
            radioButton = itemView.findViewById(R.id.rb_selected);
            tv_data1 = itemView.findViewById(R.id.recy_item1);
            tv_data2 = itemView.findViewById(R.id.recy_item2);
            tv_more = itemView.findViewById(R.id.more_detail);

            //radioButton.setOnCheckedChangeListener(CustomerManageAdapter.this);
            itemView.setOnClickListener(CustomerManageAdapter.this);
            itemView.setOnLongClickListener(CustomerManageAdapter.this);
            radioButton.setOnClickListener(CustomerManageAdapter.this);
        }
    }

    /**
     * 提供给Activity刷新数据
     * @param list
     */
    public void updateList(List<SortModel> list){
        this.mData = list;
        notifyDataSetChanged();
    }

    public Object getItem(int position) {
        return mData.get(position);
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    public int getSectionForPosition(int position) {
        return mData.get(position).getLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mData.get(i).getLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }


    /**
     * 显示RadioButton
     */
    private boolean isShow;
    private boolean typeShow;
    //改变显示删除的imageview，通过定义变量isShow去接收变量isManager
    public void changetShowDelImage(boolean isShow) {
        this.isShow = isShow;
        notifyDataSetChanged();
    }

    public void setTypeShow(boolean typeShow){
        this.typeShow = typeShow;
        notifyDataSetChanged();
    }

}