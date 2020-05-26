package com.example.feijibook.activity.record_detail_act;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.feijibook.R;
import com.example.feijibook.activity.add_record_act_from_add_icon_act.AddRecordFromAddIconActivity;
import com.example.feijibook.activity.camera_act.CameraActivity;
import com.example.feijibook.adapter.RecordPhotosRVAdapter;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.util.NoticeUpdateUtils;
import com.example.feijibook.util.SoundShakeUtil;
import com.example.feijibook.widget.MyArcMenu;
import com.example.feijibook.widget.MyToast;
import com.example.feijibook.widget.MyVideoView;
import com.example.feijibook.widget.RatioImage;
import com.hitomi.tilibrary.style.index.NumberIndexIndicator;
import com.hitomi.tilibrary.style.progress.ProgressBarIndicator;
import com.hitomi.tilibrary.transfer.TransferConfig;
import com.hitomi.tilibrary.transfer.Transferee;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordDetailFragment extends Fragment implements RDContract.View {
    RDContract.Presenter mPresenter;
    Activity mActivity;
    View mView;
    @BindView(R.id.radio_image)
    RatioImage radioImage;
    @BindView(R.id.view_transparent_status_bar)
    View viewTransparentStatusBar;
    Unbinder unbinder;
    @BindView(R.id.iv_detail_type_icon)
    ImageView ivDetailTypeIcon;
    @BindView(R.id.tv_detail_type)
    TextView tvDetailType;
    @BindView(R.id.tv_expend_or_income)
    TextView tvExpendOrIncome;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_remark)
    TextView tvRemark;
    @BindView(R.id.vs_photo)
    ViewStub vsPhoto;
    @BindView(R.id.vs_video)
    ViewStub vsVideo;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.iv_delete_or_save)
    ImageView ivDeleteOrSave;
    @BindView(R.id.arc_menu)
    MyArcMenu arcMenu;
    /**
     * 判断vsVideo和vsPhoto是否已经Inflate过
     */
    private boolean mIsVInflate = false;
    private boolean mIsPInflate = false;
    /**
     * 作为ViewStub的photo和video部分布局
     */
    ConstraintLayout clPhoto;
    ConstraintLayout clVideo;
    /**
     * 拍摄的照片列表和视频
     */
    RecyclerView rvPhotos;
    MyVideoView videoView;
    RecordPhotosRVAdapter mAdapter;

    private static final int SAVE_OR_DELETE_ICON = 1;
    private static final int EDIT_ICON = 2;
    private static final int VIDEO_ICON = 3;
    private static final int PHOTO_ICON = 4;

    /**
     * 该记录的id
     */
    private String mId;
    private MyToast mToast;
    Transferee transferee;
    TransferConfig config;

    public RecordDetailFragment() {
        // Required empty public constructor
    }

    public static RecordDetailFragment getInstance() {
        return new RecordDetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_record_detail, container, false);
        unbinder = ButterKnife.bind(this, mView);
        NoticeUpdateUtils.updateRecordsPresenters.add(mPresenter);
        mPresenter.setInit();
        return mView;
    }

    @Override
    public void finishAct() {
        mActivity.finish();
    }

    @Override
    public void startAct(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void getAct(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void getId(String id) {
        mId = id;
    }

    @Override
    public void initWidget() {
        // 设置透明任务栏高度，将返回按钮顶下去
        ViewGroup.LayoutParams params = viewTransparentStatusBar.getLayoutParams();
        params.height = MyApplication.sStatusBarHeight;
        viewTransparentStatusBar.setLayoutParams(params);
    }

    @Override
    public void showFirstPhoto(Bitmap bitmap) {
        radioImage.setBitmap(bitmap);
    }

    @Override
    public void changeArcMenuItem(Drawable drawable) {
        ivDeleteOrSave.setImageDrawable(drawable);
    }

    @Override
    public void showPhotosViewStub(boolean needShow) {
        if (needShow) {
            if (!mIsPInflate) {
                View view = vsPhoto.inflate();
                mIsPInflate = true;
                clPhoto = view.findViewById(R.id.cl_photo);
                rvPhotos = clPhoto.findViewById(R.id.rv_photo);
                mAdapter = new RecordPhotosRVAdapter(MyApplication.sContext);
                mAdapter.setOnItemClickListener(new RecordPhotosRVAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(int pos) {
                        SoundShakeUtil.playSound(SoundShakeUtil.SWOOSH1_SOUND);
                        config.setNowThumbnailIndex(pos);
                        transferee.apply(config).show();
                    }

                    @Override
                    public void onDeleteClick(int pos) {
                        SoundShakeUtil.playSound(SoundShakeUtil.DELETE_SOUND);
                        mPresenter.deleteImage(pos);
                    }
                });
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyApplication.sContext);
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                rvPhotos.setLayoutManager(linearLayoutManager);
                rvPhotos.setAdapter(mAdapter);
                transferee = Transferee.getDefault(mActivity);
                config = TransferConfig.build()
                        .setProgressIndicator(new ProgressBarIndicator())
                        .setIndexIndicator(new NumberIndexIndicator())
                        .setJustLoadHitImage(true)
                        .bindRecyclerView(rvPhotos, R.id.iv_photo);
            } else if (clPhoto != null) {
                clPhoto.setVisibility(View.VISIBLE);
            } else if (clPhoto != null && clPhoto.getVisibility() != View.GONE) {
                clPhoto.setVisibility(View.GONE);
            }
        } else if (clPhoto != null && clPhoto.getVisibility() == View.VISIBLE) {
            clPhoto.setVisibility(View.GONE);
        }
    }

    @Override
    public void showPhotos(List<Bitmap> bitmaps, List<String> urls) {
        config.setSourceImageList(urls);
        List<Bitmap> bitmapList = new ArrayList<>(bitmaps);
        mAdapter.setList(bitmapList);
    }

    @Override
    public void showVideo(String videoUrl) {
        if (videoView != null) {
            File file = new File(videoUrl);
            videoView.setUp(file.getAbsolutePath(), "");
            Glide.with(this)
                    .load(videoUrl)
                    .into(videoView.thumbImageView);
        }
    }


    @Override
    public void showVideoStubView(boolean needShow) {
        if (needShow) {
            if (!mIsVInflate) {
                View view = vsVideo.inflate();
                mIsVInflate = true;
                clVideo = view.findViewById(R.id.cl_video);
                videoView = clVideo.findViewById(R.id.video_view);
            } else if (clVideo != null) {
                clVideo.setVisibility(View.VISIBLE);
            }
        } else if (clVideo != null && clVideo.getVisibility() != View.GONE) {
            clVideo.setVisibility(View.GONE);
        }
    }


    @Override
    public void showToast(String content) {
        if (mToast == null) {
            mToast = new MyToast(mActivity);
        }
        mToast.showToast(content);
    }

    @Override
    public void updateRecord() {
        mPresenter.setShowData();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.checkUpdateRecord();
    }

    @Override
    public void showRecordDetail(RecordDetail recordDetail) {
        ivDetailTypeIcon.setImageDrawable(ContextCompat.getDrawable(MyApplication.sContext, recordDetail.getIconUrl()));
        tvDetailType.setText(recordDetail.getDetailType());
        tvExpendOrIncome.setText(recordDetail.getType());
        String date = recordDetail.getYear() + "年" + recordDetail.getMonth()
                + "月" + recordDetail.getDay() + "日 " + recordDetail.getWeek();
        tvDate.setText(date);
        tvRemark.setText(recordDetail.getRemark());
        tvMoney.setText(recordDetail.getMoney());
    }


    @Override
    public void setPresenter(RDContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void initListener() {
        arcMenu.setOnMenuItemClickListener(new MyArcMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                switch (pos) {
                    case SAVE_OR_DELETE_ICON:
                        mPresenter.setClickSaveOrDelete();
                        return;
                    case EDIT_ICON:
                        if (mId != null) {
                            // 有id表示是查询记录,点击编辑，打开编辑添加界面
                            Intent intent1 = new Intent(MyApplication.sContext, AddRecordFromAddIconActivity.class);
                            intent1.putExtra("id", mId);
                            mPresenter.setStartActivity(intent1);
                        } else {
                            // 否则为正在编辑状态，点击编辑，退出该界面到编辑添加界面
                            mPresenter.setFinishAct();
                        }
                        break;
                    case VIDEO_ICON:
                        if (clVideo != null && clVideo.getVisibility() == View.VISIBLE) {
                            showToast("只能拍摄一段视频!");
                        } else {
                            Intent intent2 = new Intent(MyApplication.sContext, CameraActivity.class);
                            intent2.putExtra("mode", 1);
                            intent2.putExtra("id", mId);
                            mPresenter.setStartActivity(intent2);
                        }
                        break;
                    case PHOTO_ICON:
                        Intent intent3 = new Intent(MyApplication.sContext, CameraActivity.class);
                        intent3.putExtra("mode", 0);
                        intent3.putExtra("id", mId);
                        mPresenter.setStartActivity(intent3);
                        break;
                    default:
                }
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_back, R.id.tv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
            case R.id.tv_back:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setFinishAct();
                break;
            default:
                break;
        }
    }

}
