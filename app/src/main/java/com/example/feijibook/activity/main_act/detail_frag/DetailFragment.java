package com.example.feijibook.activity.main_act.detail_frag;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.feijibook.R;
import com.example.feijibook.activity.add_record_from_calendar_icon_act.AddRecordFromCalendarIconActivity;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.activity.main_act.MainActivity;
import com.example.feijibook.activity.record_detail_act.RecordDetailActivity;
import com.example.feijibook.activity.search_record_act.SearchRecordActivity;
import com.example.feijibook.app.Constants;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.record_bean.DayRecord;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.util.NoticeUpdateUtils;
import com.example.feijibook.util.RecordListUtils;
import com.example.feijibook.util.SoundShakeUtil;
import com.example.feijibook.widget.my_datepicker.MyDataPicker;
import com.example.feijibook.widget.my_recyclerview.PullChangeRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements DetailContract.View, MainActivity.ViewPagerSelect {
    DetailContract.Presenter mPresenter;
    @BindView(R.id.tv_year)
    TextView tvYear;
    @BindView(R.id.tv_month)
    TextView tvMonth;
    @BindView(R.id.tv_expend_money)
    TextView tvExpendMoney;
    @BindView(R.id.tv_income_money)
    TextView tvIcomeMoney;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.iv_calendar)
    ImageView ivCalendar;
    @BindView(R.id.tv_yue)
    TextView tvYue;
    @BindView(R.id.iv_down)
    ImageView ivDown;
    @BindView(R.id.rv_change_month)
    PullChangeRecyclerView rvChangeMonth;
    Unbinder unbinder;
    List<DayRecord> mDayRecordList = new ArrayList<>();
    List<RecordDetail> mRecordDetailList = new ArrayList<>();
    /**
     * 显示"年、月"的时间选择控件
     */
    MyDataPicker ymDatePicker;
    /**
     * 选择器选择的时间
     */
    String date;
    View view;
    /**
     * ViewStub是否Inflate过
     */
    Boolean mIsInflate = false;
    ViewStub vsNoData;
    TextView tvNoData;


    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        NoticeUpdateUtils.updateRecordsPresenters.add(mPresenter);

        vsNoData = rvChangeMonth.findViewById(R.id.vs_no_data);
        mPresenter.setInit();
        initListener();

        return view;
    }

    @Override
    public void initListener() {
        onViewClicked(view);
        MainActivity.addViewPagerSelectListener(this);

        rvChangeMonth.setOnPullListener(new PullChangeRecyclerView.OnPullListener() {
            @Override
            public boolean onPullUpRelease() {
                return mPresenter.setGetNextOrLastMonthData(tvYear.getText().toString(),
                        tvMonth.getText().toString(), false);
            }

            @Override
            public boolean onPullDownRelease() {
                return mPresenter.setGetNextOrLastMonthData(tvYear.getText().toString(),
                        tvMonth.getText().toString(), true);
            }

            @Override
            public void deleteRecord(int position) {
                SoundShakeUtil.playSound(SoundShakeUtil.DELETE_SOUND);
                mPresenter.setDeleteRecord(position, mRecordDetailList.get(position));
            }

            @Override
            public void onItemClick(String id) {
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                BaseActivity.addBindAdjacentLayer((MainActivity) getActivity());
                Intent intent = new Intent(MyApplication.sContext, RecordDetailActivity.class);
                intent.putExtra("id", id);
                mPresenter.setStartActivity(intent);
            }
        });

        vsNoData.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, View inflated) {
                mIsInflate = true;
            }
        });
    }

    @Override
    public void setPresenter(DetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        NoticeUpdateUtils.updateRecordsPresenters.add(mPresenter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ymDatePicker.onDestroy();
    }

    @OnClick({R.id.tv_month, R.id.iv_search, R.id.iv_calendar, R.id.tv_yue, R.id.iv_down})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_search:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setStartActivity(new Intent(MyApplication.sContext, SearchRecordActivity.class));
                break;
            case R.id.iv_calendar:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                BaseActivity.addBindAdjacentLayer((MainActivity) getActivity());
                mPresenter.setStartActivity(new Intent(MyApplication.sContext, AddRecordFromCalendarIconActivity.class));
                break;
            case R.id.tv_yue:
            case R.id.tv_month:
            case R.id.iv_down:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setShowDatePicker();
                break;
            default:
        }
    }


    @Override
    public void showDatePicker() {
        ymDatePicker.show(tvYear.getText().toString() + "-" + tvMonth.getText().toString());
    }

    @Override
    public void showSelectDate(String year, String month) {
        date = year + "-" + month;
        tvYear.setText(year);
        tvMonth.setText(month);
    }

    @Override
    public void deleteRecord(int position) {
        mPresenter.setDeleteRecordDetail(mRecordDetailList.get(position));
        rvChangeMonth.removeItem(position);
        showOrHideNoDataSign();
        mPresenter.setUpdateMonthTotalMoney(tvYear.getText().toString(), tvMonth.getText().toString());
        NoticeUpdateUtils.noticeUpdateRecords();
    }

    @Override
    public void startAct(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void initDatePicker(long beginTime, long endTime) {
        // 通过时间戳初始化日期
        ymDatePicker = new MyDataPicker(getActivity(), new MyDataPicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                mPresenter.setShowSelectDate(timestamp, false);
                mPresenter.setGetData(date);
            }
        }, beginTime, endTime);
        ymDatePicker.setCancelable(true);
        // 不显示"日"
        ymDatePicker.setCanShowDay(false);
        // 不允许循环滚动
        ymDatePicker.setScrollLoop(false);
        // 不允许滚动动画
        ymDatePicker.setCanShowAnim(false);
    }

    @Override
    public void initClassData(List<RecordDetail> recordDetails, List<DayRecord> dayRecords) {
        mDayRecordList.clear();
        mRecordDetailList.clear();

        if (recordDetails != null && dayRecords != null) {
            mDayRecordList.addAll(dayRecords);
            mRecordDetailList.addAll(recordDetails);
        }

        rvChangeMonth.setList(mDayRecordList, mRecordDetailList);
        showOrHideNoDataSign();
        mPresenter.setUpdateMonthTotalMoney(tvYear.getText().toString(), tvMonth.getText().toString());
    }

    @Override
    public void showOrHideNoDataSign() {
        if (mRecordDetailList.size() == 0) {
            if (!mIsInflate) {
                View view = vsNoData.inflate();
                tvNoData = view.findViewById(R.id.tv_no_data);
            } else if (tvNoData != null) {
                tvNoData.setVisibility(View.VISIBLE);
            }
        } else if (tvNoData != null && tvNoData.getVisibility() != View.GONE) {
            tvNoData.setVisibility(View.GONE);
        }

    }

    @Override
    public void updateMonthTotalMoney(SpannableString totalIncome, SpannableString totalExpend) {
        tvIcomeMoney.setText(totalIncome);
        tvExpendMoney.setText(totalExpend);
    }

    @Override
    public void closeMenu() {
        rvChangeMonth.closeMenu();
    }

    @Override
    public void updateRecord() {
        Map<String, String> map = RecordListUtils.getAddRecordDateMap();
        // 添加记录时，更新显示该记录时间位置
        if (map.containsKey(Constants.YEAR)) {
            String year = map.get(Constants.YEAR);
            String month = map.get(Constants.MONTH);
            map.clear();
            tvYear.setText(year);
            tvMonth.setText(month);
            String date = year + "-" + month;
            mPresenter.setGetData(date);
        }// 初始化 或 删除记录 的更新，则显示当前时间位置的记录
        else {
            mPresenter.setGetData(this.date);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.checkUpdateRecord();
    }

    @Override
    public void onPageSelect(int position) {
        if (position == Constants.DETAIL_PAGE) {
            mPresenter.checkUpdateRecord();
        }
    }


}
