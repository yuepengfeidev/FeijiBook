package com.example.feijibook.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.feijibook.R;
import com.example.feijibook.app.MyApplication;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * RecordPhotosRVAdapter
 *
 * @author PengFei Yue
 * @date 2019/8/25
 * @description 记录详情界面中照片列表的Adapter
 */
public class RecordPhotosRVAdapter extends RecyclerView.Adapter {
    private List<Bitmap> mBitmapList = new ArrayList<>();
    private LayoutInflater mInflater;
    private OnItemClickListener mOnItemClickListener;
    /**
     * 是否是编辑状态，不是则不显示删除按钮
     */
    private boolean isEditing = false;

    public RecordPhotosRVAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setList(List<Bitmap> bitmaps) {
        mBitmapList = bitmaps;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_record_photo, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
        if (payloads.isEmpty()) {
            this.onBindViewHolder(holder, position);
            return;
        }
        ViewHolder h = (ViewHolder) holder;
        if (isEditing) {
            h.ivDelete.setVisibility(View.VISIBLE);
        } else {
            h.ivDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final int pos = i;
        ViewHolder holder = (ViewHolder) viewHolder;
        Glide.with(MyApplication.sContext).load(mBitmapList.get(pos)).
                thumbnail(0.1f).into(holder.ivPhoto);
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBitmapList.remove(pos);
                notifyItemRemoved(pos);
                mOnItemClickListener.onDeleteClick(pos);
            }
        });

        holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditing) {
                    mOnItemClickListener.onClick(pos);
                }
            }
        });
        // 长按切换编辑状态
        holder.ivPhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                changeEditStatus();
                return true;
            }
        });
    }

    /**
     * 切换编辑状态
     */
    private void changeEditStatus() {
        isEditing = !isEditing;
        notifyItemRangeChanged(0, mBitmapList.size(), R.id.iv_delete);
    }

    @Override
    public int getItemCount() {
        return mBitmapList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        /**
         * 照片点击监听
         *
         * @param pos 位置
         */
        void onClick(int pos);

        /**
         * 照片删除点击监听
         *
         * @param pos 照片位置
         */
        void onDeleteClick(int pos);
    }

    static
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_photo)
        ImageView ivPhoto;
        @BindView(R.id.iv_delete)
        ImageView ivDelete;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
