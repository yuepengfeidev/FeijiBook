package com.example.feijibook.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.feijibook.R;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.util.SoundShakeUtil;
import com.example.feijibook.widget.MyVideoView;
import com.hitomi.tilibrary.style.index.NumberIndexIndicator;
import com.hitomi.tilibrary.style.progress.ProgressBarIndicator;
import com.hitomi.tilibrary.transfer.TransferConfig;
import com.hitomi.tilibrary.transfer.Transferee;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * AlbumAdapter
 *
 * @author PengFei Yue
 * @date 2019/10/5
 * @description 相册的记录的列表的适配器，可显示照片记录和视频记录
 */
public class AlbumAdapter extends RecyclerView.Adapter {
    private LayoutInflater mInflater;
    private List<Map.Entry<String, RecordDetail>> mRecordDetailList = new ArrayList<>();
    private boolean mIsPhotosAlbum = true;
    /**
     * 是否显示选择
     */
    private boolean mShowSelect = false;
    List<RecordDetail> mSelectedRecords = new ArrayList<>();
    Transferee transferee;
    Activity mActivity;

    public AlbumAdapter(Activity activity, Boolean isPhotosAlbum) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        mIsPhotosAlbum = isPhotosAlbum;
        transferee = Transferee.getDefault(mActivity);
    }

    public void setList(List<Map.Entry<String, RecordDetail>> list) {
        mRecordDetailList = list;
        notifyDataSetChanged();
    }

    public void showSelect(boolean isShow) {
        mShowSelect = isShow;
        // 只刷新所有“选择”textview
        notifyItemRangeChanged(0, mRecordDetailList.size(), R.id.tv_select);
    }

    public List<RecordDetail> getSelectedRecords() {
        return mSelectedRecords;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if (mIsPhotosAlbum) {
            view = mInflater.inflate(R.layout.item_album_photos, viewGroup, false);
            return new PhotosAlbumHolder(view);
        } else {
            view = mInflater.inflate(R.layout.item_album_video, viewGroup, false);
            return new VideoAlbumHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position, @NonNull List payloads) {
        if (payloads.isEmpty()) {
            this.onBindViewHolder(viewHolder, position);
            return;
        }
        RecordDetail recordDetail = mRecordDetailList.get(position).getValue();
        boolean isImgUpload = recordDetail.isImgUpload();
        boolean isVideoUpload = recordDetail.isVideoUpload();
        if (mIsPhotosAlbum) {
            PhotosAlbumHolder holder = (PhotosAlbumHolder) viewHolder;
            disPoseSelect(holder.tvSelect, isImgUpload, position, false);
        } else {
            VideoAlbumHolder holder = (VideoAlbumHolder) viewHolder;
            disPoseSelect(holder.tvSelect, isVideoUpload, position, false);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        RecordDetail recordDetail = mRecordDetailList.get(i).getValue();
        String ymd = recordDetail.getYear() + "年" + recordDetail.getMonth() + "月" + recordDetail.getDay() + "日";
        String week = recordDetail.getWeek();
        String expendOrIncome = recordDetail.getType();
        String remark = recordDetail.getRemark();
        String money = recordDetail.getMoney();
        boolean isImgUpload = recordDetail.isImgUpload();
        boolean isVideoUpload = recordDetail.isVideoUpload();

        // 通过选择类型图表地址得到该类型显示的图表的地址
        String s = MyApplication.sContext.getResources().getResourceName(recordDetail.getIconUrl());
        String url = s.substring(0, s.length() - 1) + "selected";
        ApplicationInfo applicationInfo = MyApplication.sContext.getApplicationInfo();
        int iconUrl = MyApplication.sContext.getResources().getIdentifier(url, "drawable", applicationInfo.packageName);

        if (mIsPhotosAlbum) {
            PhotosAlbumHolder holder = (PhotosAlbumHolder) viewHolder;
            holder.ivTypeIcon.setImageDrawable(ContextCompat.getDrawable(MyApplication.sContext, iconUrl));
            holder.tvYmd.setText(ymd);
            holder.tvWeek.setText(week);
            holder.tvIncomeExpend.setText(expendOrIncome);
            holder.tvMoney.setText(money);
            holder.tvRemark.setText(remark);
            disPoseSelect(holder.tvSelect, isImgUpload, i, true);
            String[] imgUrl = recordDetail.getImgUrl().split("\\|");
            List<Bitmap> bitmaps = new ArrayList<>();
            List<String> urls = new ArrayList<>();
            for (String u : imgUrl) {
                bitmaps.add(compressBitmap(u));
                urls.add("file:/" + MyApplication.PHOTO_PATH + u);
            }
            PhotosAdapter adapter = new PhotosAdapter(MyApplication.sContext);
            holder.rvPhotos.setLayoutManager(new GridLayoutManager(MyApplication.sContext, 3));
            holder.rvPhotos.setAdapter(adapter);
            TransferConfig config = TransferConfig.build()
                    .setSourceImageList(urls)
                    .setProgressIndicator(new ProgressBarIndicator())
                    .setIndexIndicator(new NumberIndexIndicator())
                    .setJustLoadHitImage(true)
                    .bindRecyclerView(holder.rvPhotos, R.id.iv_photo);
            adapter.setList(bitmaps, config);
        } else {
            VideoAlbumHolder holder = (VideoAlbumHolder) viewHolder;
            holder.ivTypeIcon.setImageDrawable(ContextCompat.getDrawable(MyApplication.sContext, iconUrl));
            holder.tvYmd.setText(ymd);
            holder.tvWeek.setText(week);
            holder.tvIncomeExpend.setText(expendOrIncome);
            holder.tvMoney.setText(money);
            holder.tvRemark.setText(remark);
            disPoseSelect(holder.tvSelect, isVideoUpload, i, true);
            File file = new File(MyApplication.VIDEO_PATH + recordDetail.getVideoUrl());
            holder.videoView.setUp(file.getAbsolutePath(), "");
            Glide.with(MyApplication.sContext).load(MyApplication.VIDEO_PATH + recordDetail.getVideoUrl())
                    .into(holder.videoView.thumbImageView);
        }
    }

    private void disPoseSelect(TextView textView, boolean isUpload, int pos, boolean isLoadListener) {
        if (mShowSelect) {
            textView.setVisibility(View.VISIBLE);
            if (isUpload) {
                textView.setClickable(false);
                textView.setText("已上传");
                textView.setTextColor(ContextCompat.getColor(MyApplication.sContext, R.color.text_light_gray_color));
            } else {
                textView.setClickable(true);
            }
        } else {
            textView.setVisibility(View.GONE);
        }

        if (isLoadListener) {
            textView.setOnClickListener(v -> {
                if ("选择".equals(textView.getText().toString())) {
                    mSelectedRecords.add(mRecordDetailList.get(pos).getValue());
                    textView.setText("取消");
                } else {
                    mSelectedRecords.remove(mRecordDetailList.get(pos).getValue());
                    textView.setText("选择");
                }
                AlbumAdapter.mOnItemSelectedListener.onItemSelected(mSelectedRecords.size());
            });
        }
    }

    private Bitmap compressBitmap(String url) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ALPHA_8;
        // 值越大，图像越不清晰，但读取资源会更快
        options.inSampleSize = 8;
        return BitmapFactory.decodeFile(MyApplication.PHOTO_PATH + url, options);
    }

    @Override
    public int getItemCount() {
        return mRecordDetailList.size();
    }

    static
    class PhotosAlbumHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rv_photos)
        RecyclerView rvPhotos;
        @BindView(R.id.iv_type_icon)
        ImageView ivTypeIcon;
        @BindView(R.id.tv_ymd)
        TextView tvYmd;
        @BindView(R.id.tv_week)
        TextView tvWeek;
        @BindView(R.id.tv_money)
        TextView tvMoney;
        @BindView(R.id.tv_remark)
        TextView tvRemark;
        @BindView(R.id.tv_expend_income)
        TextView tvIncomeExpend;
        @BindView(R.id.tv_select)
        TextView tvSelect;

        PhotosAlbumHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static
    class VideoAlbumHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_type_icon)
        ImageView ivTypeIcon;
        @BindView(R.id.video_view)
        MyVideoView videoView;
        @BindView(R.id.tv_ymd)
        TextView tvYmd;
        @BindView(R.id.tv_income_expend)
        TextView tvIncomeExpend;
        @BindView(R.id.tv_remark)
        TextView tvRemark;
        @BindView(R.id.tv_money)
        TextView tvMoney;
        @BindView(R.id.tv_week)
        TextView tvWeek;
        @BindView(R.id.tv_select)
        TextView tvSelect;

        VideoAlbumHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class PhotosAdapter extends RecyclerView.Adapter {
        private LayoutInflater mInflater;
        private List<Bitmap> mBitmapList;
        TransferConfig config;

        PhotosAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public void setList(List<Bitmap> bitmaps, TransferConfig config) {
            this.config = config;
            mBitmapList = bitmaps;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.item_photo, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ViewHolder holder = (ViewHolder) viewHolder;
            Bitmap bitmap = mBitmapList.get(i);
            Glide.with(MyApplication.sContext).load(bitmap)
                    .apply(RequestOptions.centerCropTransform())
                    .thumbnail(0.1f)
                    .into(holder.ivPhoto);
            holder.ivPhoto.setOnClickListener(v -> {
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                config.setNowThumbnailIndex(i);
                transferee.apply(config).show();
            });
        }

        @Override
        public int getItemCount() {
            return mBitmapList.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.iv_photo)
            ImageView ivPhoto;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }

    private static OnItemSelectedListener mOnItemSelectedListener;

    public static void setmOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
    }

    public interface OnItemSelectedListener {
        /**
         * item选择监听
         *
         * @param size 选中的数量
         */
        void onItemSelected(int size);
    }

}
