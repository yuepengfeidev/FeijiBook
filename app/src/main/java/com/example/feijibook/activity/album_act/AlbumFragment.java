package com.example.feijibook.activity.album_act;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.feijibook.R;
import com.example.feijibook.adapter.AlbumAdapter;
import com.example.feijibook.adapter.ViewPagerAdapter;
import com.example.feijibook.util.SoundShakeUtil;
import com.example.feijibook.widget.MyToast;
import com.example.feijibook.widget.MyViewPager;
import com.example.feijibook.widget.my_dialog.CircleProgressBarDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumFragment extends Fragment implements AlbumContract.View, AlbumAdapter.OnItemSelectedListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.tl_album)
    TabLayout tlAlbum;
    @BindView(R.id.vp_album)
    MyViewPager vpAlbum;
    Unbinder unbinder;
    @BindView(R.id.tv_select)
    TextView tvSelect;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_select_count)
    TextView tvSelectCount;
    private Activity mActivity;
    private AlbumContract.Presenter mPresenter;
    private View mView;
    private String[] mTitles = {"照片", "视频"};
    MyToast mToast;
    CircleProgressBarDialog mDialog;

    public AlbumFragment() {
        // Required empty public constructor
    }

    public static AlbumFragment newInstance() {
        return new AlbumFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_album, container, false);
        unbinder = ButterKnife.bind(this, mView);
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
    public void initWidget(List<Fragment> fragments, boolean isPhotosAlbum) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addTitlesAndFragments(mTitles, fragments);
        vpAlbum.setAdapter(viewPagerAdapter);

        tlAlbum.addTab(tlAlbum.newTab().setText(mTitles[0]));
        tlAlbum.addTab(tlAlbum.newTab().setText(mTitles[1]));

        tlAlbum.setupWithViewPager(vpAlbum);

        if (isPhotosAlbum) {
            tlAlbum.getTabAt(0).select();
        } else {
            tlAlbum.getTabAt(1).select();
        }
    }

    @Override
    public void showSelectLayout() {
        tvBack.setVisibility(View.GONE);
        ivBack.setVisibility(View.GONE);
        tlAlbum.setVisibility(View.GONE);
        tvSelectCount.setVisibility(View.VISIBLE);
        tvCancel.setVisibility(View.VISIBLE);
        tvSelect.setText("上传");
    }

    @Override
    public void hideSelectLayout() {
        tvBack.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        tlAlbum.setVisibility(View.VISIBLE);
        tvSelectCount.setVisibility(View.GONE);
        tvCancel.setVisibility(View.GONE);
        tvSelect.setText("选择");
    }

    @Override
    public void showToast(String content) {
        if (mToast == null) {
            mToast = new MyToast(mActivity);
        }
        mToast.showToast(content);
    }

    @Override
    public void showSelected(String content) {
        tvSelectCount.setText(content);
    }

    @Override
    public void showDialog() {
        if (mDialog == null) {
            mDialog = new CircleProgressBarDialog(mActivity);
        }
        mDialog.show();
    }

    @Override
    public void setDialogPercent(String percent) {
        mDialog.setPercent(percent);
    }

    @Override
    public void dismissDialog() {
        mDialog.close();
    }

    @Override
    public void setPresenter(AlbumContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void initListener() {
        onViewClicked(mView);
        tlAlbum.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setPage(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        AlbumAdapter.setmOnItemSelectedListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_back, R.id.tv_back, R.id.tv_cancel, R.id.tv_select})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
            case R.id.tv_back:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setFinishAct();
                break;
            case R.id.tv_cancel:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setHideSelectLayout();
                mPresenter.setShowSelect(false);
                break;
            case R.id.tv_select:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                String content = tvSelect.getText().toString();
                mPresenter.setSelectOrUpload(content);
                break;
            default:
        }
    }

    @Override
    public void onItemSelected(int size) {
        mPresenter.setShowSelectedContent(size);
    }
}
