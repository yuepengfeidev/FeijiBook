package com.example.feijibook.adapter;

/**
 * Created by 你是我的 on 2019/3/12
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.feijibook.R;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.widget.my_recyclerview.MyRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 记录recyclerview的适配器
 */
public class RecordRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<RecordDetail> mRecordDetailList = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private OnClickListener mOnClickListener;
    private MyRecyclerView mMyRecyclerView;

    public RecordRVAdapter(Context context, MyRecyclerView myRecyclerView) {
        mContext = context;
        this.mMyRecyclerView = myRecyclerView;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setList(List<RecordDetail> list){
        mRecordDetailList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.item_detail_record, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder holder = (ViewHolder) viewHolder;
        RecordDetail recordDetail = mRecordDetailList.get(i);
        holder.ivDetailTypeIcon.setImageResource(recordDetail.getIconUrl());
        holder.tvDetailType.setText(recordDetail.getRemark());
        String money = null;
        if (recordDetail.getType().equals("收入")){
            money = "+" + recordDetail.getMoney();
        }else if (recordDetail.getType().equals("支出")){
            money = "-" + recordDetail.getMoney();
        }
        holder.tvDetailTypeMoney.setText(money);
       initListener(holder,i);
    }

    private void initListener(ViewHolder holder, final int i) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.clickItem(mRecordDetailList.get(i).getId());
            }
        });

        holder.tvDeleteRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.deleteRecord(i);
            }
        });
    }


    @Override
    public int getItemCount() {
        if (mRecordDetailList == null || mRecordDetailList.size() == 0){
            return 0;
        }
        return mRecordDetailList.size();
    }

    static
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_detail_type)
        TextView tvDetailType;
        @BindView(R.id.tv_detail_type_money)
        TextView tvDetailTypeMoney;
        @BindView(R.id.iv_detail_type_icon)
        ImageView ivDetailTypeIcon;
        @BindView(R.id.tv_delete_record)
        TextView tvDeleteRecord;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public interface OnClickListener{
        void deleteRecord(int position);
        void clickItem(String  id);
    }
}
