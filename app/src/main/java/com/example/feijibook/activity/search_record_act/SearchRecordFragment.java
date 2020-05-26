package com.example.feijibook.activity.search_record_act;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.feijibook.R;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.activity.record_detail_act.RecordDetailActivity;
import com.example.feijibook.adapter.RecordRVAdapter;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.record_bean.DayRecord;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.util.NoticeUpdateUtils;
import com.example.feijibook.util.SoundShakeUtil;
import com.example.feijibook.widget.my_recyclerview.MyRecyclerView;
import com.example.feijibook.widget.my_recyclerview.SectionDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchRecordFragment extends Fragment implements SRContract.View {
    SRContract.Presenter mPresenter;
    Activity mActivity;
    @BindView(R.id.et_search_record)
    EditText etSearchRecord;
    @BindView(R.id.tv_cancel_search_record)
    TextView tvCancelSearchRecord;
    @BindView(R.id.rv_search_record_result)
    MyRecyclerView rvSearchRecordResult;
    Unbinder unbinder;
    RecordRVAdapter mAdapter;
    List<RecordDetail> mRecordDetailList = new ArrayList<>();
    List<DayRecord> mDayRecordList = new ArrayList<>();

    public SearchRecordFragment() {
        // Required empty public constructor
    }

    public static SearchRecordFragment getInstance() {
        return new SearchRecordFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_record, container, false);
        unbinder = ButterKnife.bind(this, view);
        NoticeUpdateUtils.updateRecordsPresenters.add(mPresenter);
        return view;
    }

    @Override
    public void finishAct() {
        mActivity.finish();
    }

    @Override
    public void initListener() {
        tvCancelSearchRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setFinishAct();
            }
        });
        etSearchRecord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged
                    (CharSequence s, int start, int count, int after) {
            }@Override
            public void onTextChanged
                    (CharSequence s, int start, int before, int count) {
                if (!"".equals(s.toString())) {
                    // 输入时搜索显示记录
                    mPresenter.setSearchRecords(s.toString());
                }// 输入为空时清除列表
                else {
                    mPresenter.setClearRV(); } }
                    @Override public void afterTextChanged(Editable s) { }});

        mAdapter.setOnClickListener(new RecordRVAdapter.OnClickListener() {
            @Override
            public void deleteRecord(int position) {
                SoundShakeUtil.playSound(SoundShakeUtil.DELETE_SOUND);
                mPresenter.setDeleteRecord(position);
            }

            @Override
            public void clickItem(String id) {
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                Intent intent = new Intent(MyApplication.sContext, RecordDetailActivity.class);
                intent.putExtra("id", id);
                mPresenter.setStartActivity(intent);
            }

        });
    }

    @Override
    public void initWidget() {
        // EditText获取焦点,随之弹出软键盘
        etSearchRecord.setFocusable(true);
        etSearchRecord.setFocusableInTouchMode(true);
        etSearchRecord.requestFocus();

        LinearLayoutManager manager = new LinearLayoutManager(MyApplication.sContext, LinearLayoutManager.VERTICAL, false);
        SectionDecoration sectionDecoration = new SectionDecoration(MyApplication.sContext,
                new SectionDecoration.DecorationCallback() {
                    @Override
                    public String getGroupId(int position) {
                        RecordDetail recordDetail = mRecordDetailList.get(position);
                        if (recordDetail.getYear() != null && recordDetail.getMonth() != null
                                && recordDetail.getDay() != null) {
                            return recordDetail.getYear() + "-" + recordDetail.getMonth() + "-" + recordDetail.getDay();
                        }
                        return "-1";
                    }

                    @Override
                    public DayRecord getDayTotal(int position) {
                        RecordDetail recordDetail = mRecordDetailList.get(position);
                        for (DayRecord record : mDayRecordList) {
                            if (record.getYear().equals(recordDetail.getYear())
                                    && record.getMonth().equals(recordDetail.getMonth())
                                    && record.getDay().equals(recordDetail.getDay())) {
                                return record;
                            }
                        }
                        return null;
                    }
                });
        mAdapter = new RecordRVAdapter(mActivity, rvSearchRecordResult);
        rvSearchRecordResult.addItemDecoration(sectionDecoration);
        rvSearchRecordResult.setLayoutManager(manager);
        rvSearchRecordResult.setAdapter(mAdapter);
    }

    @Override
    public void startAct(Intent intent) {
        BaseActivity.addBindAdjacentLayer((SearchRecordActivity) mActivity);
        startActivity(intent);
    }

    @Override
    public void getAct(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void updateRecord() {
        // 更新输入框中搜索出的记录
        mPresenter.setSearchRecords(etSearchRecord.getText().toString());
    }

    @Override
    public void setList(List<RecordDetail> recordDetails, List<DayRecord> dayRecords) {
        mRecordDetailList.clear();
        mDayRecordList.clear();

        if (mRecordDetailList != null && mDayRecordList != null) {
            mRecordDetailList.addAll(recordDetails);
            mDayRecordList.addAll(dayRecords);
        }

        mAdapter.setList(mRecordDetailList);
    }

    @Override
    public void deleteRecord(int position) {
        mPresenter.setDeleteRecordDetail(mRecordDetailList.get(position));
        mRecordDetailList.remove(position);
        mAdapter.notifyDataSetChanged();
        NoticeUpdateUtils.noticeUpdateRecords();
    }

    @Override
    public void closeMenu() {
        if (rvSearchRecordResult != null) {
            rvSearchRecordResult.closeMenu();
        }
    }

    @Override
    public void clearRV() {
        mRecordDetailList.clear();
        mDayRecordList.clear();
        mAdapter.setList(mRecordDetailList);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.checkUpdateRecord();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NoticeUpdateUtils.updateRecordsPresenters.remove(mPresenter);
    }

    @Override
    public void setPresenter(SRContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 等待弹出键盘
     */
    private void waitPop() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) etSearchRecord.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etSearchRecord, 0);
            }
        }, 40);
    }
}
