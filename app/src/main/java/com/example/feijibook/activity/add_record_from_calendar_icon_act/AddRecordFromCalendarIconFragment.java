package com.example.feijibook.activity.add_record_from_calendar_icon_act;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.feijibook.R;
import com.example.feijibook.activity.add_record_act_from_add_icon_act.AddRecordFromAddIconActivity;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.activity.record_detail_act.RecordDetailActivity;
import com.example.feijibook.adapter.RecordRVAdapter;
import com.example.feijibook.app.Constants;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.record_bean.DayRecord;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.util.NoticeUpdateUtils;
import com.example.feijibook.util.RecordListUtils;
import com.example.feijibook.util.SoundShakeUtil;
import com.example.feijibook.widget.my_datepicker.MyDataPicker;
import com.example.feijibook.widget.my_recyclerview.MyRecyclerView;
import com.example.feijibook.widget.my_recyclerview.SectionDecoration;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddRecordFromCalendarIconFragment extends Fragment implements ARFCContract.View {
    ARFCContract.Presenter mPresenter;
    @BindView(R.id.tv_ym_choose_arfc)
    TextView tvYmChooseArfc;
    @BindView(R.id.iv_back_to_detail_frag)
    ImageView ivBackToDetailFrag;
    @BindView(R.id.tv_back_to_detail_frag)
    TextView tvBackToDetailFrag;
    @BindView(R.id.tv_locate_to_today)
    TextView tvLocateToToday;
    @BindView(R.id.cv_in_arfc)
    CalendarView cvInArfc;
    @BindView(R.id.rv_in_arfc)
    MyRecyclerView rvInArfc;
    @BindView(R.id.cl_in_arfc)
    CalendarLayout clInArfc;
    @BindView(R.id.fab_add_record)
    FloatingActionButton floatingActionButton;
    Unbinder unbinder;
    /**
     * 显示"年、月"的时间选择控件
     */
    MyDataPicker ymDatePicker;
    View view;
    List<RecordDetail> mRecordDetailList = new ArrayList<>();
    RecordRVAdapter mRecordRVAdapter;
    List<DayRecord> mDayRecordList = new ArrayList<>();
    String year;
    String month;
    String day;
    @BindView(R.id.vs_no_data)
    ViewStub vsNoData;
    TextView tvNoData;
    private boolean mIsInflate = false;
    private boolean notPlaySound = true;


    public AddRecordFromCalendarIconFragment() {
        // Required empty public constructor
    }

    public static AddRecordFromCalendarIconFragment newInstance() {
        return new AddRecordFromCalendarIconFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_record_from_calendar, container, false);
        unbinder = ButterKnife.bind(this, view);
        NoticeUpdateUtils.updateRecordsPresenters.add(mPresenter);
        return view;
    }

    @Override
    public void setPresenter(ARFCContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (ymDatePicker != null) {
            ymDatePicker.onDestroy();
        }
    }

    @OnClick({R.id.tv_ym_choose_arfc, R.id.iv_back_to_detail_frag, R.id.tv_back_to_detail_frag, R.id.tv_locate_to_today, R.id.fab_add_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_ym_choose_arfc:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setShowDataPicker(tvYmChooseArfc.getText().toString());
                break;
            case R.id.iv_back_to_detail_frag:
            case R.id.tv_back_to_detail_frag:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setFinishAct();
                break;
            case R.id.tv_locate_to_today:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setLocateToday();
                break;
            case R.id.fab_add_record:
                SoundShakeUtil.playSound(SoundShakeUtil.SELECT_SWOOSH1_SOUND);
                Intent intent = new Intent(MyApplication.sContext, AddRecordFromAddIconActivity.class);
                // 传递 Calendar 选择的日期,Calendar 没有选择则设置为null
                String date;
                if (year != null && month != null && day != null) {
                    date = year + "/" + month + "/" + day;
                } else {
                    date = "今天";
                }
                intent.putExtra("date", date);
                startAct(intent);
                return;
            default:
        }
    }

    @Override
    public void finishAct() {
        Objects.requireNonNull(getActivity()).finish();
    }

    @Override
    public void showDatePicker(String dateStr) {
        ymDatePicker.show(dateStr);
    }

    @Override
    public void locateToday() {
        cvInArfc.scrollToCurrent(true);
        // 获取当天的记录
        mPresenter.setInitRecrords("0", "0", "0");
    }

    @Override
    public void locateToChoose(int year, int month, int day) {
        cvInArfc.scrollToCalendar(year, month, day);
    }

    @Override
    public void showSelectDate(String dateString) {
        tvYmChooseArfc.setText(dateString);
    }

    @Override
    public void initDatePicker(long beginTime, long endTime) {
        // 通过时间戳初始化日期
        ymDatePicker = new MyDataPicker(getActivity(), new MyDataPicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                mPresenter.setShowSelectDate(timestamp, false);
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
    public void initListener() {
        onViewClicked(view);
        // 月视图改变监听
        cvInArfc.setOnMonthChangeListener((year, month) -> {
            String time = year + "-" + month;
            mPresenter.setShowSelectDate(time); });
        cvInArfc.setOnCalendarSelectListener
                (new CalendarView.OnCalendarSelectListener() {@Override
            public void onCalendarOutOfRange(Calendar calendar) { }@Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                if (notPlaySound) {
                    notPlaySound = false;
                } else { SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SOUND); }
                DecimalFormat decimalFormat = new DecimalFormat("00");
                year = String.valueOf(calendar.getYear());
                month = String.valueOf(decimalFormat.format(calendar.getMonth()));
                day = String.valueOf(decimalFormat.format(calendar.getDay()));
                if (isClick) { mPresenter.setInitRecrords(year, month, day); }
                // 如果是当天日期，则让year为null，此时数字软键盘时间选择按钮为 今天
                if (calendar.isCurrentDay()) { year = null; } }});
        mRecordRVAdapter.setOnClickListener(new RecordRVAdapter.OnClickListener() {
            @Override
            public void deleteRecord(int position) {
                SoundShakeUtil.playSound(SoundShakeUtil.DELETE_SOUND);
                mPresenter.setDeleteRecord(position, mRecordDetailList.get(position));
            }

            @Override
            public void clickItem(String id) {
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                BaseActivity.addBindAdjacentLayer(((AddRecordFromCalendarIconActivity) getActivity()));
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
    public void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(MyApplication.sContext, LinearLayoutManager.VERTICAL, false);
        rvInArfc.setLayoutManager(manager);
        mRecordRVAdapter = new RecordRVAdapter(MyApplication.sContext, rvInArfc);
        rvInArfc.addItemDecoration(new SectionDecoration(MyApplication.sContext,
                new SectionDecoration.DecorationCallback() {
                    @Override
                    public String getGroupId(int position) {
                        if (mRecordDetailList.get(position).getDay() != null) {
                            return mRecordDetailList.get(position).getDay();
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
                }));
        rvInArfc.setAdapter(mRecordRVAdapter);

    }

    @Override
    public void deleteRecord(int position) {
        rvInArfc.closeMenu();// 在删除之前需要关闭删除菜单按钮
        RecordDetail recordDetail = mRecordDetailList.get(position);
        // 删除数据库中的数据
        mPresenter.setDeleteRecordDetail(recordDetail);
        mRecordDetailList.remove(position);
        mRecordRVAdapter.notifyDataSetChanged();
        showOrHideNoDataSign();
        // 当该天没有记录时，移除Calendar上该天的标记
        if (mRecordDetailList.size() == 0) {
            cvInArfc.removeSchemeDate(getSchemeCalendar(Integer.valueOf(recordDetail.getYear()),
                    Integer.valueOf(recordDetail.getMonth()), Integer.valueOf(recordDetail.getDay())));
        }
        NoticeUpdateUtils.noticeUpdateRecords();
    }

    @Override
    public void startAct(Intent intent) {
        rvInArfc.closeMenu();// 打开活动之前关闭 打开着的删除菜单栏
        startActivity(intent);
    }

    @Override
    public void initRecordsToRv(List<RecordDetail> recordDetails, List<DayRecord> dayRecords) {
        mDayRecordList.clear();
        mRecordDetailList.clear();
        if (recordDetails != null && dayRecords != null) {
            mDayRecordList.addAll(dayRecords);
            mRecordDetailList.addAll(recordDetails);
        }
        mRecordRVAdapter.setList(mRecordDetailList);
        showOrHideNoDataSign();
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
    public void markRecordDay(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM", Locale.CHINA);
        Date d = format.parse(date);
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(d);
        // 获取Calendar选择的月份的上一个月
        calendar.add(java.util.Calendar.MONTH, -1);
        String lastMonth = format.format(calendar.getTime());
        // 获取Calendar选择的月份的下一个月
        calendar.add(java.util.Calendar.MONTH, +2);
        String nextMonth = format.format(calendar.getTime());
        Map<String, Calendar> calendarMap = new HashMap<>(16);
        Map<String, List<DayRecord>> map = RecordListUtils.getDayRecordsInMonthMap();
        List<DayRecord> list = new ArrayList<>();

        // 获取选择月份几上一个月和下一个月的所有日总详情记录，进行标记
        // 因为一个月视图中可能会出现上一月或下一个月的日期，所以需要显示标记
        if (map.get(lastMonth) != null) {
            list.addAll(Objects.requireNonNull(map.get(lastMonth)));
        }
        if (map.get(date) != null) {
            list.addAll(Objects.requireNonNull(map.get(date)));
        }
        if (map.get(nextMonth) != null) {
            list.addAll(Objects.requireNonNull(map.get(nextMonth)));
        }

        for (DayRecord dayRecord : list) {
            double totalExpend = 0;
            double totalIncome = 0;
            if (dayRecord.getDayTotalExpend() != null) {
                totalExpend = Double.parseDouble(dayRecord.getDayTotalExpend());
            }
            if (dayRecord.getDayTotalIncome() != null) {
                totalIncome = Double.parseDouble(dayRecord.getDayTotalIncome());
            }
            if (totalExpend == 0 && totalIncome == 0) {
                continue;
            }
            int year = Integer.valueOf(dayRecord.getYear());
            int month = Integer.valueOf(dayRecord.getMonth());
            int day = Integer.valueOf(dayRecord.getDay());
            calendarMap.put(getSchemeCalendar(year, month, day).toString(),
                    getSchemeCalendar(year, month, day));
        }
        cvInArfc.setSchemeDate(calendarMap);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.checkUpdateRecord();
    }

    @Override
    public void onDestroy() {
        NoticeUpdateUtils.updateRecordsPresenters.remove(mPresenter);
        super.onDestroy();
    }

    @Override
    public Calendar getSchemeCalendar(int year, int month, int day) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.addScheme(new Calendar.Scheme());
        return calendar;
    }

    @Override
    public void updateRecord() {
        notPlaySound = true;
        Map<String, String> map = RecordListUtils.getAddRecordDateMap();
        // 有添加记录的日期，则定位到添加记录的日期，否则定位到当前日期
        if (map.containsKey(Constants.YEAR)) {
            mPresenter.setInitAddRecord(map.get(Constants.YEAR),
                    map.get(Constants.MONTH), map.get(Constants.DAY));
        } else {
            mPresenter.setInitAddRecord(year, month, day);
        }
    }

}
