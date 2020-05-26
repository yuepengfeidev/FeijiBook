package com.example.feijibook.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.feijibook.R;
import com.example.feijibook.entity.record_type_bean.CustomType;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 你是我的 on 2019/3/28
 */
public class AddCustomTypeRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CustomType> mCustomTypeList;
    private LayoutInflater mLayoutInflater;
    private ChooseType mChooseType;
    // 上一次点击的checkbox
    private IconHolder lastViewHolder = null;
    // 是否是刚加载，是则初始化第一item为select状态
    private boolean isFirstInit = true;

    public AddCustomTypeRVAdapter(Context context, List<CustomType> customTypeList) {
        mCustomTypeList = customTypeList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        // 如果checkbox资源路径为0，表示该position为类型标题
        if (mCustomTypeList.get(i).getTypeIconUrl() == 0){
            view = mLayoutInflater.inflate(R.layout.item_add_custom_type_title, viewGroup, false);
            return new TitleHolder(view);
        }else {
            view = mLayoutInflater.inflate(R.layout.item_add_custom_type_icon, viewGroup, false);
            return new IconHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        if (viewHolder instanceof TitleHolder) {
            ((TitleHolder) viewHolder).tvAddCustomTypeTitle.setText(mCustomTypeList.get(i).getCategory());
        } else if (viewHolder instanceof IconHolder) {

            ((IconHolder) viewHolder).cbAddCustomTypeIcon.setButtonDrawable(mCustomTypeList.get(i).getTypeIconUrl());

            ((IconHolder) viewHolder).cbAddCustomTypeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mChooseType.chooseListener(mCustomTypeList.get(i));
                    ((IconHolder) viewHolder).cbAddCustomTypeIcon.setSelected(true);
                    if (lastViewHolder != null && lastViewHolder != viewHolder){// 如果有上一次的checkbox被选择，则取消
                        lastViewHolder.cbAddCustomTypeIcon.setSelected(false);
                    }
                    lastViewHolder = (IconHolder) viewHolder;
                }
            });
            if (i == 1 && isFirstInit){
                isFirstInit = false;
                lastViewHolder = (IconHolder) viewHolder;
                ((IconHolder) viewHolder).cbAddCustomTypeIcon.setSelected(true);
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        final GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        assert layoutManager != null;
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                // 如果是标题,则设置spanCount个单元格
                if (mCustomTypeList.get(i).getTypeIconUrl() == 0) {
                    return layoutManager.getSpanCount();
                }
                return 1;
            }
        });
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mCustomTypeList.size();
    }

    static
    class TitleHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_add_custom_type_title)
        TextView tvAddCustomTypeTitle;

        TitleHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static
    class IconHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cb_add_custom_type_icon)
        CheckBox cbAddCustomTypeIcon;

        IconHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setChooseType(ChooseType chooseType) {
        mChooseType = chooseType;
    }

    public interface ChooseType {
        void chooseListener(CustomType customType);
    }
}
