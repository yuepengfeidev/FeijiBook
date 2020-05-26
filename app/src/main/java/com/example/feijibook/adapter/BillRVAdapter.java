package com.example.feijibook.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.feijibook.R;
import com.example.feijibook.entity.BillBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * BillRVAdapter
 *
 * @author yuepengfei
 * @date 2019/7/24
 * @description
 */
public class BillRVAdapter extends RecyclerView.Adapter {
    private LayoutInflater mLayoutInflater;
    private List<BillBean> mBillBeans = new ArrayList<>();

    public BillRVAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setList(List<BillBean> billBeans) {
        mBillBeans = billBeans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.item_bill, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder holder = (ViewHolder) viewHolder;
        BillBean billBean = mBillBeans.get(i);
        holder.tvExpend.setText(billBean.getMonthExpend());
        holder.tvIncome.setText(billBean.getMonthIncome());
        holder.tvMonth.setText(billBean.getMonthStr());
        holder.tvSurplus.setText(billBean.getMonthSurplus());
    }

    @Override
    public int getItemCount() {
        return mBillBeans.size();
    }

    static
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_month)
        TextView tvMonth;
        @BindView(R.id.tv_income)
        TextView tvIncome;
        @BindView(R.id.tv_expend_income)
        TextView tvExpend;
        @BindView(R.id.tv_surplus)
        TextView tvSurplus;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
