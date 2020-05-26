package com.example.feijibook.helper;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.feijibook.R;
import com.example.feijibook.app.MyApplication;

/**
 * Created by 你是我的 on 2019/3/25
 */
public class ItemDragHelperCallBack extends ItemTouchHelper.Callback {
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags;
        dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        // 不同type之间不可移动
        if (viewHolder.getItemViewType() != target.getItemViewType()){
            return false;
        }
        if (recyclerView.getAdapter() instanceof  OnItemMoveListener){
            OnItemMoveListener listener = (OnItemMoveListener) recyclerView.getAdapter();
            listener.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        }
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        // 不在闲置状态
    if (actionState != ItemTouchHelper.ACTION_STATE_IDLE){
        if (viewHolder instanceof OnDragVHListener){
           viewHolder.itemView.setBackgroundColor(MyApplication.sContext.getResources().getColor(R.color.itemPressedGray));
        }
    }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setBackgroundColor(Color.WHITE);
        OnDragVHListener onDragVHListener = (OnDragVHListener)viewHolder;
        onDragVHListener.onItemFinish();
        super.clearView(recyclerView, viewHolder);
    }
}
