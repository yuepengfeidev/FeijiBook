package com.example.feijibook.activity.album_act.record_album_frag;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import com.example.feijibook.R;
import com.example.feijibook.adapter.AlbumAdapter;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.widget.my_progress_bar.MyCircleProgressBar;
import com.hitomi.tilibrary.style.index.NumberIndexIndicator;
import com.hitomi.tilibrary.style.progress.ProgressBarIndicator;
import com.hitomi.tilibrary.transfer.TransferConfig;
import com.hitomi.tilibrary.transfer.Transferee;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordAlbumFragment extends Fragment implements RAContract.View {
    @BindView(R.id.rv_album)
    RecyclerView rvAlbum;
    @BindView(R.id.vs_no_data)
    ViewStub vsNoData;
    TextView tvNoData;
    Unbinder unbinder;
    private RAContract.Presenter mPresenter;
    private View mView;
    private AlbumAdapter mAdapter;
    private Activity mActivity;

    public RecordAlbumFragment() {
        // Required empty public constructor
    }

    public static RecordAlbumFragment newInstance() {
        return new RecordAlbumFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_record_album, container, false);
        unbinder = ButterKnife.bind(this, mView);
        mPresenter.setInit();
        return mView;
    }

    @Override
    public void startAct(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void initRV(boolean isPhotosAlbum) {
        mAdapter = new AlbumAdapter(mActivity, isPhotosAlbum);
        rvAlbum.setLayoutManager(new LinearLayoutManager(MyApplication.sContext));
        rvAlbum.setAdapter(mAdapter);

    }

    @Override
    public void showRecords(List<Map.Entry<String, RecordDetail>> list) {
        List<Map.Entry<String, RecordDetail>> l = new ArrayList<>(list);
        mAdapter.setList(l);
    }

    @Override
    public void getAct(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void showSelect(boolean isShow) {
        mAdapter.showSelect(isShow);
    }

    @Override
    public void clearSelect() {
        mAdapter.getSelectedRecords().clear();
    }

    @Override
    public List<RecordDetail> getSelectedRecords() {
        return mAdapter.getSelectedRecords();
    }

    @Override
    public void showOrHideNoDataSign(boolean show) {
        if (show) {
            if (tvNoData == null) {
                View view = vsNoData.inflate();
                tvNoData = view.findViewById(R.id.tv_no_data);
            } else {
                tvNoData.setVisibility(View.VISIBLE);
            }
        } else if (tvNoData != null && tvNoData.getVisibility() != View.GONE) {
            tvNoData.setVisibility(View.GONE);
        }
    }

    @Override
    public void setPresenter(RAContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void initListener() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
