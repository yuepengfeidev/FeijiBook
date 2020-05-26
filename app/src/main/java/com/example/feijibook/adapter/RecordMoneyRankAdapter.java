package com.example.feijibook.adapter;
/*
 * Created by 你是我的 on 2019/5/13
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.feijibook.R;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.entity.record_bean.TotalExpend;
import com.example.feijibook.entity.record_bean.TotalIncome;
import com.example.feijibook.widget.my_progress_bar.ProportionScaleView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordMoneyRankAdapter extends RecyclerView.Adapter {
    private LayoutInflater mLayoutInflater;
    private List mList = new ArrayList<>();
    private String mType;
    private ItemClickListener mItemClickListener;
    private NumberFormat mNumberFormat = NumberFormat.getPercentInstance();
    private BigDecimal mBigestMoney;
    /**
     * item中是否添加事件信息
     */
    private boolean mIsAddTime;

    public RecordMoneyRankAdapter(Context context, boolean isAddTime) {
        mLayoutInflater = LayoutInflater.from(context);
        // 设置百分比后一位小数
        mNumberFormat.setMinimumFractionDigits(1);
        mIsAddTime = isAddTime;
    }

    public void setList(String type, List list) {
        mType = type;
        mList = list;
        notifyDataSetChanged();
    }

    public void setList(List list) {
        if (list != null) {
            mList = list;
        } else {
            mList = new ArrayList();
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if (mIsAddTime) {
            view = mLayoutInflater.inflate(R.layout.item_money_rank_2, viewGroup, false);
            return new ViewHolder2(view);
        } else {
            view = mLayoutInflater.inflate(R.layout.item_money_rank, viewGroup, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        BigDecimal proportion = null;
        Object o = mList.get(i);
        String id = null;
        if (o instanceof RecordDetail) {
            ViewHolder2 holder = (ViewHolder2) viewHolder;
            RecordDetail recordDetail = (RecordDetail) o;
            if (i == 0) {
                mBigestMoney = new BigDecimal(recordDetail.getMoney());
            }
            holder.ivMoneyRankIcon.setImageResource(recordDetail.getIconUrl());
            proportion = new BigDecimal(recordDetail.getMoney()).divide(mBigestMoney,
                    8, BigDecimal.ROUND_UP);
            String time = recordDetail.getYear() + "年" + recordDetail.getMonth() + "月" + recordDetail.getDay() + "日";
            // 类型加占比的字符串
            String typeAndProportion = recordDetail.getDetailType() + " " + mNumberFormat.format(proportion);
            holder.tvMoneyRankType.setText(typeAndProportion);
            holder.tvMoneyRankMoney.setText(recordDetail.getMoney());
            holder.tvRecordTime.setText(time);
            id = recordDetail.getId();
            final String recordId = id;
            holder.proportionScaleView.setScales(proportion.floatValue());
            holder.clMoneyRankItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.itemClick(recordId);
                }
            });
        } else {
            ViewHolder holder;
            holder = (ViewHolder) viewHolder;
            switch (mType) {
                case "income":
                    if (o instanceof TotalIncome) {
                        TotalIncome totalIncome = (TotalIncome) o;
                        if (i == 0) {
                            mBigestMoney = new BigDecimal(totalIncome.getTotalIncome());
                        }
                        holder.ivMoneyRankIcon.setImageResource(totalIncome.getIconUrl());
                        proportion = new BigDecimal(totalIncome.getTotalIncome()).divide(mBigestMoney,
                                8, BigDecimal.ROUND_UP);
                        // 类型加占比的字符串
                        String typeAndProportion = totalIncome.getType() + " " + mNumberFormat.format(proportion);
                        holder.tvMoneyRankType.setText(typeAndProportion);
                        holder.tvMoneyRankMoney.setText(totalIncome.getTotalIncome());
                        id = totalIncome.getType();
                    }
                    break;
                case "expend":
                    if (o instanceof TotalExpend) {
                        TotalExpend totalExpend = (TotalExpend) o;
                        if (i == 0) {
                            mBigestMoney = new BigDecimal(totalExpend.getTotalExpend());
                        }
                        holder.ivMoneyRankIcon.setImageResource(totalExpend.getIconUrl());
                        proportion = new BigDecimal(totalExpend.getTotalExpend()).divide(mBigestMoney,
                                8, BigDecimal.ROUND_UP);
                        // 类型加占比的字符串
                        String typeAndProportion = totalExpend.getType() + " " + mNumberFormat.format(proportion);
                        holder.tvMoneyRankType.setText(typeAndProportion);
                        holder.tvMoneyRankMoney.setText(totalExpend.getTotalExpend());
                        id = totalExpend.getType();
                    }
                    break;
                default:
                    break;
            }
            final String recordId = id;
            holder.proportionScaleView.setScales(proportion.floatValue());
            holder.clMoneyRankItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.itemClick(recordId);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    static
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.proportion_scale_view)
        ProportionScaleView proportionScaleView;
        @BindView(R.id.iv_money_rank_icon)
        ImageView ivMoneyRankIcon;
        @BindView(R.id.tv_money_rank_type)
        TextView tvMoneyRankType;
        @BindView(R.id.tv_money_rank_money)
        TextView tvMoneyRankMoney;
        @BindView(R.id.cl_money_rank_item)
        ConstraintLayout clMoneyRankItem;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static
    class ViewHolder2 extends RecyclerView.ViewHolder {

        @BindView(R.id.proportion_scale_view)
        ProportionScaleView proportionScaleView;
        @BindView(R.id.iv_money_rank_icon)
        ImageView ivMoneyRankIcon;
        @BindView(R.id.tv_money_rank_type)
        TextView tvMoneyRankType;
        @BindView(R.id.tv_money_rank_money)
        TextView tvMoneyRankMoney;
        @BindView(R.id.tv_record_time)
        TextView tvRecordTime;
        @BindView(R.id.cl_money_rank_item)
        ConstraintLayout clMoneyRankItem;

        ViewHolder2(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void itemClick(String id);
    }
}
