package com.example.feijibook.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.feijibook.R;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.entity.record_type_bean.OptionalType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 你是我的 on 2019/3/22
 */
public class RecordTypeChooseRVAdapter extends RecyclerView.Adapter<RecordTypeChooseRVAdapter.ViewHolder> {
    private List<OptionalType> mOptionalTypeList = new ArrayList<>();
    private List<ViewHolder> mViewHolderList = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private ItemClickListener mItemClickListener;
    /**
     * 上次点击的item的viewholder，便于切换item时关闭上一次的item,第一点 上次默认为null
     */
    public ViewHolder lastViewHolder = null;

    public RecordTypeChooseRVAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setList(List<OptionalType> optionalTypes) {
        mOptionalTypeList = optionalTypes;
        mViewHolderList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.item_record_type_choose, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final int position = i;
        mViewHolderList.add(viewHolder);

        // 最后一个为设置按钮
        if (position < mOptionalTypeList.size()) {
            OptionalType optionalType = mOptionalTypeList.get(position);
            viewHolder.cbRecordType.setCompoundDrawablesWithIntrinsicBounds(0, optionalType.getTypeIconUrl_O(), 0, 0);
            // 默认类型 设置默认名称，否则则为 自定义 则设置 自定义名称
            if (optionalType.getDefaultOrCustom_O().equals("d")) {
                viewHolder.cbRecordType.setText(optionalType.getTypeName_O());
            } else {
                viewHolder.cbRecordType.setText(optionalType.getCustomTypeName());
            }
        } else {
            viewHolder.cbRecordType.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tallytype_set, 0, 0);
            viewHolder.cbRecordType.setText("设置");
        }

        viewHolder.cbRecordType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checkDispose(position, viewHolder);
            }
        });
    }

    private void checkDispose(int position, ViewHolder viewHolder) {
        if (position < mOptionalTypeList.size()) {
            // 该item没有check过才执行下面
            if (!viewHolder.cbRecordType.isSelected()) {
                if (lastViewHolder != viewHolder) {
                    mItemClickListener.checkBoxSelect(position, viewHolder);
                    lastViewHolder = viewHolder;
                }
            }
        } else {
            mItemClickListener.settingClick();
            lastViewHolder = null;
        }
    }

    /**
     * 根据类型check相应的box
     *
     * @param recordDetail 编辑的记录详情
     */
    public void checkType(RecordDetail recordDetail) {
        int position = 0;
        for (OptionalType type : mOptionalTypeList) {
            if (recordDetail.getDetailType().equals(type.getTypeName_O())
                    || recordDetail.getDetailType().equals(type.getCustomTypeName())) {
                position = mOptionalTypeList.indexOf(type);
                break;
            }
        }
        mViewHolderList.get(position).cbRecordType.setChecked(true);
    }

    @Override
    public int getItemCount() {
        if (mOptionalTypeList == null) {
            return 0;
        }
        // 多出最后一个设置按钮
        return mOptionalTypeList.size() + 1;
    }

    public static
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cb_record_type)
        public CheckBox cbRecordType;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void checkBoxSelect(int position, ViewHolder curViewHolder);// 记录类型 选择

        void settingClick();// 设置按钮点击
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
