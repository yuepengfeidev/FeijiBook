package com.example.feijibook.activity.add_record_act_from_add_icon_act.type_choose_frag;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.feijibook.R;
import com.example.feijibook.activity.add_record_act_from_add_icon_act.AddRecordFromAddIconActivity;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.activity.category_setting_act.CategorySettingActivity;
import com.example.feijibook.adapter.RecordTypeChooseRVAdapter;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.entity.record_type_bean.OptionalType;
import com.example.feijibook.util.NoticeUpdateUtils;
import com.example.feijibook.util.SoundShakeUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * TypeChooseFragment
 *
 * @author PengFei Yue
 * @date 2019/10/5
 * @description
 */
public class TypeChooseFragment extends Fragment implements TCContract.View {
    @BindView(R.id.rv_choose_type)
    RecyclerView rvChooseType;
    Unbinder unbinder;
    private TCContract.Presenter mPresenter;
    List<OptionalType> mOptionalTypeList = new ArrayList<>();
    RecordTypeChooseRVAdapter mAdapter;

    public static TypeChooseFragment newInstance() {
        return new TypeChooseFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_type_choose, container, false);
        unbinder = ButterKnife.bind(this, view);
        NoticeUpdateUtils.updateTypesPresenters.add(mPresenter);
        mPresenter.setInit();
        return view;
    }


    @Override
    public void initWidget() {
        mAdapter = new RecordTypeChooseRVAdapter(MyApplication.sContext);
        rvChooseType.setLayoutManager(new GridLayoutManager(MyApplication.sContext, 4));
        rvChooseType.setAdapter(mAdapter);
    }

    @Override
    public void selectDispose(int position, boolean state) {
        RecordTypeChooseRVAdapter.ViewHolder viewHolder
                = (RecordTypeChooseRVAdapter.ViewHolder)
                rvChooseType.findViewHolderForAdapterPosition(position);
        assert viewHolder != null;
        viewHolder.cbRecordType.setSelected(state);
    }

    @Override
    public void startAct(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void changRVSize(int position, int height) {
        // 滚动到该位置再缩小recyclerview
        rvChooseType.scrollToPosition(position);

        ViewGroup.LayoutParams params = rvChooseType.getLayoutParams();
        params.height = height;
        rvChooseType.setLayoutParams(params);
    }

    @Override
    public void setLastViewHolderNull() {
        mAdapter.lastViewHolder = null;
    }

    @Override
    public void initRVData(List<OptionalType> incomeOptionalList) {
        mOptionalTypeList = incomeOptionalList;
        mAdapter.setList(mOptionalTypeList);
    }

    @Override
    public void updateRecord() {
        mPresenter.getRVData();
        mPresenter.setSaveTypeChooseSetting();
    }

    @Override
    public void checkType(RecordDetail recordDetail) {
        mAdapter.checkType(recordDetail);
    }

    @Override
    public void setPresenter(TCContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void initListener() {
        mAdapter.setItemClickListener(new RecordTypeChooseRVAdapter.ItemClickListener() {
            @Override
            public void checkBoxSelect(int position, RecordTypeChooseRVAdapter.ViewHolder
                    curViewHolder) {
                SoundShakeUtil.playSound(SoundShakeUtil.TAP2_SOUND);
                mPresenter.setSelectItem(mOptionalTypeList.get(position), position, curViewHolder
                        , getActivity());
            }

            @Override
            public void settingClick() {
                SoundShakeUtil.playSound(SoundShakeUtil.TAP2_SOUND);
                BaseActivity.addBindAdjacentLayer((AddRecordFromAddIconActivity) getActivity());
                mPresenter.setSettingClick(new Intent(getActivity(), CategorySettingActivity.class),
                        getActivity());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.checkUpdateRecord();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
