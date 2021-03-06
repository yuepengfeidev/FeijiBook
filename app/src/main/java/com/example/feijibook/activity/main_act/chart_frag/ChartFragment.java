package com.example.feijibook.activity.main_act.chart_frag;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.feijibook.R;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.activity.main_act.MainActivity;
import com.example.feijibook.activity.one_type_records_line_chart_act.OneTypeRecordsLineChartActivity;
import com.example.feijibook.activity.pie_chart_records_act.PieChartRecordsActivity;
import com.example.feijibook.adapter.RecordMoneyRankAdapter;
import com.example.feijibook.app.Constants;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.util.NoticeUpdateUtils;
import com.example.feijibook.util.SoundShakeUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChartFragment extends Fragment implements ChartContract.View, MainActivity.ViewPagerSelect, View.OnClickListener {
    ChartContract.Presenter mPresenter;

    Unbinder unbinder;
    Activity mActivity;
    @BindView(R.id.rg_category_choose)
    RadioGroup rgCategoryChoose;
    @BindView(R.id.tv_date_type_choose_title)
    TextView tvChartFragCategoryChoose;
    @BindView(R.id.rb_week_chart_frag)
    RadioButton rbWeekChartFrag;
    @BindView(R.id.rb_month_chart_frag)
    RadioButton rbMonthChartFrag;
    @BindView(R.id.rb_year_chart_frag)
    RadioButton rbYearChartFrag;
    @BindView(R.id.iv_pie_chart)
    ImageView ivPieChart;
    @BindView(R.id.tv_total_money)
    TextView tvTotalMoney;
    @BindView(R.id.tv_average_money)
    TextView tvAverageMoney;
    @BindView(R.id.line_chart_chart_frag)
    LineChart lineChartChartFrag;
    @BindView(R.id.rv_rank_chart_frag)
    RecyclerView rvRankChartFrag;
    @BindView(R.id.tv_rank_title_name)
    TextView tvRankTitleName;
    @BindView(R.id.tb_date_choose)
    TabLayout tbDateChoose;
    @BindView(R.id.nested_scroll_view_chart)
    NestedScrollView mNestedScrollView;
    @BindView(R.id.chart_frag_main_context)
    ConstraintLayout chartFragMainContext;
    /**
     * chartFragment 顶部 选择类别视图
     */
    @BindView(R.id.include_type_choose_top_in_chart_frag)
    View includeChooseTop;

    View viewShadow;

    /**
     * 类型选择弹窗 中的构型标记
     */
    ImageView ivExpendSelected;
    ImageView ivIncomeSelected;
    /**
     * 类型选择项（收入、支出）
     */
    RelativeLayout rlExpend;
    RelativeLayout rlIncome;

    /**
     * 收入支出弹窗view
     */
    PopupWindow pwEIChoose;
    View viewEIChoose;

    /**
     * chart marker 的 View
     */
    View detailView;
    View positionView;
    View roundView;
    /**
     * 自定义 chart marker
     */
    PopupWindow detailsWindow;
    PopupWindow positionWindow;
    PopupWindow roundWindow;
    TextView tvItemRemarkTotalMoney;
    TextView tvRemarkTitle;
    TextView tvItemRemarkNoMoney;
    ImageView ivRemarkTypeIcon1;
    TextView tvRemarkRecordTime1;
    TextView tvRemarkRecordType1;
    TextView tvRemarkRecordMoney1;
    ImageView ivRemarkTypeIcon2;
    TextView tvRemarkRecordTime2;
    TextView tvRemarkRecordType2;
    TextView tvRemarkRecordMoney2;
    ImageView ivRemarkTypeIcon3;
    TextView tvRemarkRecordTime3;
    TextView tvRemarkRecordType3;
    TextView tvRemarkRecordMoney3;
    View firstIncludeItem;
    View secondIncludeItem;
    View thirdIncludeItem;
    @BindView(R.id.vs_no_data)
    ViewStub vsNoData;
    TextView tvNoData;
    private boolean mIsInflate = false;


    /**
     * tabLayout的数据列表
     */
    private List<String> tabLayoutDatas = new ArrayList<>();
    /**
     * chart的x轴上的日期列表，用于获取选择日期的记录
     */
    private List<String> mKeyList = new ArrayList<>();
    private RecordMoneyRankAdapter mAdapter;
    private String incomeOrExpend = Constants.EXPEND;
    private String dateType = Constants.WEEK;
    private String mTotalMoney = Constants.ZERO_MONEY;

    private static int lightGray;
    private static int heavyGray;
    private static int skyBlue;
    private static int tlSelectBlack;
    private static int tlUnSelectGray;
    private static float tlSelectSize;
    private static float tlUnSelectSize;
    private int index = -1;
    View view;
    /**
     * TabLayout在初始化时，总会选择一次，即除法选择监听，所以需要忽视不播放点击声音
     */
    private boolean notPlaySound = true;

    public ChartFragment() {
        // Required empty public constructor
    }

    public static ChartFragment newInstance() {
        return new ChartFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chart, container, false);
        unbinder = ButterKnife.bind(this, view);
        NoticeUpdateUtils.updateRecordsPresenters.add(mPresenter);
        mPresenter.setInit();
        return view;
    }

    @Override
    public void setPresenter(ChartContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void initListener() {
        tlSelectBlack = ContextCompat.getColor(mActivity, R.color.tab_selected_color);
        tlUnSelectGray = ContextCompat.getColor(mActivity, R.color.tab_unselected_color);
        tlSelectSize = getResources().getDimension(R.dimen.tab_layout_text_size_selected);
        tlUnSelectSize = getResources().getDimension(R.dimen.tab_layout_text_size_unselected);

        onViewClicked(view);
        rlExpend.setOnClickListener(this);
        rlIncome.setOnClickListener(this);
        viewShadow.setOnClickListener(this);

        lineChartChartFrag.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, Highlight highlight) {
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SOUND);
                mPresenter.setInitChartMarker(highlight, mKeyList, dateType, incomeOrExpend);
                mPresenter.setShowChartMarker(highlight, lineChartChartFrag, detailsWindow, positionWindow, roundWindow);
            }

            @Override
            public void onNothingSelected() {
            }
        });

        tbDateChoose.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (notPlaySound) {
                    notPlaySound = false;
                } else {
                    SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                }
                TextView tabText = (TextView) (((LinearLayout) ((LinearLayout)
                        tbDateChoose.getChildAt(0)).getChildAt(tab.getPosition())).getChildAt(1));
                tabText.setTextColor(tlSelectBlack);
                tabText.setTextSize(20);
                index = tab.getPosition();
                mNestedScrollView.scrollTo(0, 0);
                mPresenter.setInitRecyclerViewData(dateType, tabLayoutDatas.get(index), incomeOrExpend);
                mPresenter.setLoadChart(dateType, tabLayoutDatas.get(index), incomeOrExpend);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tabText = (TextView) (((LinearLayout) ((LinearLayout)
                        tbDateChoose.getChildAt(0)).getChildAt(tab.getPosition())).getChildAt(1));
                tabText.setTextColor(tlUnSelectGray);
                tabText.setTextSize(13);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mAdapter.setItemClickListener(new RecordMoneyRankAdapter.ItemClickListener() {
            @Override
            public void itemClick(String detailType) {
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                Intent intent = new Intent(mActivity, OneTypeRecordsLineChartActivity.class);
                intent.putExtra("title", detailType);
                intent.putExtra("expendOrIncome", incomeOrExpend);
                intent.putExtra("dateType", dateType);
                intent.putExtra("date", tabLayoutDatas.get(index));
                mPresenter.setStartAct(intent);
            }
        });

        rgCategoryChoose.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                RadioButton radioButton = mActivity.findViewById(checkedId);
                String type = radioButton.getText().toString();
                switch (type) {
                    case "周":
                        dateType = "week";
                        break;
                    case "月":
                        dateType = "month";
                        break;
                    case "年":
                        dateType = "year";
                        break;
                    default:
                        break;
                }
                mNestedScrollView.scrollTo(0, 0);
                // 更改 日期类型（每周数据、每月数据、每年数据） ，根据该类型显示数据
                mPresenter.setLoadTabLayoutData(dateType, -1);
            }
        });

        vsNoData.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, View inflated) {
                mIsInflate = true;
            }
        });

        MainActivity.addViewPagerSelectListener(this);
    }

    @Override
    public void startAct(Intent intent) {
        BaseActivity.addBindAdjacentLayer((MainActivity) mActivity);
        startActivity(intent);
    }

    @Override
    public void getAct(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void loadChart(List<Float> yDataList, final List<String> xAxisList,
                          List<String> keyList, String totalMoney, String averageMoney) {

        mTotalMoney = totalMoney;
        mKeyList = keyList;

        List<Integer> colors = new ArrayList<>();
        List<Entry> entries = new ArrayList<>();
        final List<String> mXAxisList = new ArrayList<>(xAxisList);

        for (int i = 0; i < yDataList.size(); i++) {
            float data = yDataList.get(i);
            entries.add(new Entry(i, data));
            // 该日期没有金额时，折现圆圈孔为白色
            if (data == 0) {
                colors.add(heavyGray);
            } else {
                colors.add(skyBlue);
            }
        }

        XAxis xAxis = lineChartChartFrag.getXAxis();
        YAxis axisLeft = lineChartChartFrag.getAxisLeft();
        // 设置y轴最大值和最小值
        axisLeft.setAxisMaximum(Collections.max(yDataList));
        axisLeft.setAxisMinimum(Collections.min(yDataList));

        // 当有金额时，才显示 平均值 和 顶部 线
        if (!totalMoney.equals(Constants.ZERO_MONEY)) {
            // 重新设置平均线 也需要移除所有线
            axisLeft.removeAllLimitLines();
            // LineChart 的顶部实线
            LimitLine topLine = new LimitLine(Collections.max(yDataList));
            topLine.setLineColor(lightGray);
            //三个参数，第一个线长度，第二个线段之间宽度，第三个一般为0，是个补偿
            topLine.enableDashedLine(0, 0f, 0);

            // LineChart 的平均值 虚线
            LimitLine averageLine = new LimitLine(Float.valueOf(averageMoney));
            averageLine.setLineColor(lightGray);
            averageLine.enableDashedLine(15f, 5f, 0);
            // 添加顶部实线 和 平均值虚线
            axisLeft.addLimitLine(topLine);
            axisLeft.addLimitLine(averageLine);
        } else {
            axisLeft.removeAllLimitLines();
        }

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                int index = (int) v;
                if (index < 0 || index >= mXAxisList.size()) {
                    return "";
                } else {
                    if (mXAxisList.get(index) != null) {
                        return mXAxisList.get(index);
                    } else {
                        return "";
                    }
                }
            }
        });
        // 设置x轴 label的个数，必须指定，否则会自动省略个别来达到全部显示
        xAxis.setLabelCount(mXAxisList.size());

        // 每次更新必须重新创建LineDataSet,否则CircleColors只会在第一次更新
        LineDataSet lineDataSet = new LineDataSet(entries, "");
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setCircleColorHole(Color.WHITE);
        lineDataSet.setCircleColors(colors);
        lineDataSet.setColor(Color.GRAY);
        lineDataSet.setDrawValues(false);
        // 设置高亮交叉线颜色为透明
        lineDataSet.setHighLightColor(Color.TRANSPARENT);

        LineData lineData = new LineData(lineDataSet);
        lineChartChartFrag.setData(lineData);
        lineChartChartFrag.invalidate();
    }

    @Override
    public void loadRecyclerViewData(List list) {
        mAdapter.setList(incomeOrExpend, list);
        showOrHideNoDataSign(list);
    }

    @Override
    public void showOrHideNoDataSign(List list) {
        if (list.size() == 0) {
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
    public void initWidget() {
        mAdapter = new RecordMoneyRankAdapter(MyApplication.sContext, false);
        // 设置RecyclerView在NestedScrollView中无法滑动，解决滑动冲突，RecyclerView只会拦截Down
        rvRankChartFrag.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MyApplication.sContext,
                LinearLayoutManager.VERTICAL, false);
        rvRankChartFrag.setLayoutManager(layoutManager);
        rvRankChartFrag.setAdapter(mAdapter);

        rbWeekChartFrag.setChecked(true);

        initChart();

        // 初始化chart的marker的view
        detailView = LayoutInflater.from(MyApplication.sContext)
                .inflate(R.layout.layout_detail_remark, chartFragMainContext, false);
        positionView = LayoutInflater.from(MyApplication.sContext)
                .inflate(R.layout.layout_position_marker_view, chartFragMainContext, false);
        roundView = LayoutInflater.from(MyApplication.sContext)
                .inflate(R.layout.layout_round_marker_view, chartFragMainContext, false);

        viewEIChoose = LayoutInflater.from(MyApplication.sContext)
                .inflate(R.layout.popupwindow_expend_income_choose, chartFragMainContext, false);

        firstIncludeItem = detailView.findViewById(R.id.item_remark_record_first_in_three_records);
        ivRemarkTypeIcon1 = firstIncludeItem.findViewById(R.id.iv_remark_type_icon);
        tvRemarkRecordTime1 = firstIncludeItem.findViewById(R.id.tv_remark_record_time);
        tvRemarkRecordType1 = firstIncludeItem.findViewById(R.id.tv_remark_record_type);
        tvRemarkRecordMoney1 = firstIncludeItem.findViewById(R.id.tv_remark_record_money);
        secondIncludeItem = detailView.findViewById(R.id.item_remark_record_second_in_three_records);
        ivRemarkTypeIcon2 = secondIncludeItem.findViewById(R.id.iv_remark_type_icon);
        tvRemarkRecordTime2 = secondIncludeItem.findViewById(R.id.tv_remark_record_time);
        tvRemarkRecordType2 = secondIncludeItem.findViewById(R.id.tv_remark_record_type);
        tvRemarkRecordMoney2 = secondIncludeItem.findViewById(R.id.tv_remark_record_money);
        thirdIncludeItem = detailView.findViewById(R.id.item_remark_record_third_in_three_records);
        ivRemarkTypeIcon3 = thirdIncludeItem.findViewById(R.id.iv_remark_type_icon);
        tvRemarkRecordTime3 = thirdIncludeItem.findViewById(R.id.tv_remark_record_time);
        tvRemarkRecordType3 = thirdIncludeItem.findViewById(R.id.tv_remark_record_type);
        tvRemarkRecordMoney3 = thirdIncludeItem.findViewById(R.id.tv_remark_record_money);

        tvRemarkTitle = detailView.findViewById(R.id.tv_remark_title);
        tvItemRemarkTotalMoney = detailView.findViewById(R.id.tv_item_remark_total_money);
        tvItemRemarkNoMoney = detailView.findViewById(R.id.tv_item_remark_no_money);

        ivExpendSelected = viewEIChoose.findViewById(R.id.iv_expend_selected);
        ivIncomeSelected = viewEIChoose.findViewById(R.id.iv_income_selected);
        rlExpend = viewEIChoose.findViewById(R.id.rl_expend_selected);
        rlIncome = viewEIChoose.findViewById(R.id.rl_income_selected);

        // 初始化 类型选择弹窗PopupWindow
        pwEIChoose = new PopupWindow(viewEIChoose);
        pwEIChoose.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pwEIChoose.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pwEIChoose.setAnimationStyle(R.style.PopupWindowAnimStyle);
        pwEIChoose.setTouchable(true);
        pwEIChoose.setFocusable(false);
        pwEIChoose.setOutsideTouchable(false);

        // 阴影层view
        viewShadow = mActivity.findViewById(R.id.view_shadow);
    }

    @Override
    public void initChart() {
        lightGray = ContextCompat.getColor(mActivity, R.color.xaxis_line_or_limit_line_light_gray);
        heavyGray = ContextCompat.getColor(mActivity, R.color.line_or_circle_or_chart_text_heavy_gray);
        skyBlue = ContextCompat.getColor(mActivity, R.color.sky_blue_like);

        lineChartChartFrag.getLegend().setEnabled(false);
        lineChartChartFrag.getDescription().setEnabled(false);
        lineChartChartFrag.setScaleEnabled(false);
        lineChartChartFrag.setTouchEnabled(true);
        XAxis xAxis = lineChartChartFrag.getXAxis();
        YAxis axisLeft = lineChartChartFrag.getAxisLeft();
        lineChartChartFrag.getAxisRight().setEnabled(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1);
        xAxis.setTextColor(heavyGray);
        xAxis.setAxisLineColor(lightGray);
        // 设置x轴粗细
        xAxis.setAxisLineWidth(1f);
        axisLeft.setDrawLabels(false);
        axisLeft.setDrawGridLines(false);
        axisLeft.setAxisLineColor(Color.TRANSPARENT);
    }

    @Override
    public void initData() {
        mPresenter.setShowExpendOrIncome(incomeOrExpend);
    }

    @Override
    public void loadTabLayout(List<String> stringList, List<String> showList, int index) {
        if (tabLayoutDatas.size() != 0 && tabLayoutDatas.size() != 0) {
            tabLayoutDatas.clear();
            // 移除所有tab，重新加载TabLayout
            tbDateChoose.removeAllTabs();
        }
        tabLayoutDatas.addAll(stringList);
        for (String s : showList) {
            tbDateChoose.addTab(tbDateChoose.newTab().setText(s));
        }
        new Handler().postDelayed(() -> tbDateChoose.getTabAt(index).select(), 200);

    }

    @Override
    public void showTotalAverage(String totalMoney, String averageMoney) {
        tvTotalMoney.setText(totalMoney);
        tvAverageMoney.setText(averageMoney);
    }

    @Override
    public void showExpendOrIncome(String expendOrIncome) {
        String typeChooseTitle = expendOrIncome + "▼";
        String rankTitle = expendOrIncome + "排行榜";
        tvChartFragCategoryChoose.setText(typeChooseTitle);
        tvRankTitleName.setText(rankTitle);
        if (expendOrIncome.equals(Constants.INCOME)) {
            ivExpendSelected.setVisibility(View.GONE);
            ivIncomeSelected.setVisibility(View.VISIBLE);
        } else {
            ivIncomeSelected.setVisibility(View.GONE);
            ivExpendSelected.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initChartMarker(float yValue, String totalMoney, List<RecordDetail> recordDetailList) {
        if (detailsWindow == null && positionWindow == null && roundWindow == null) {
            ColorDrawable transparent = new ColorDrawable(Color.TRANSPARENT);
            detailsWindow = new PopupWindow(detailView);
            positionWindow = new PopupWindow(positionView, 13, 70, true);
            roundWindow = new PopupWindow(roundView, 15, 15, true);
            detailsWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            detailsWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            detailsWindow.setBackgroundDrawable(transparent);
            // 触摸marker外部则会关闭marker
            detailsWindow.setOutsideTouchable(true);
            detailsWindow.setTouchable(false);
            positionWindow.setBackgroundDrawable(transparent);
            positionWindow.setOutsideTouchable(true);
            positionWindow.setTouchable(false);
            roundWindow.setBackgroundDrawable(transparent);
            roundWindow.setOutsideTouchable(true);
            roundWindow.setTouchable(false);
        }

        // 该x轴日期金额为 0，则显示 “没有费用”的marker
        if (yValue == 0 || recordDetailList.size() == 0) {
            firstIncludeItem.setVisibility(View.GONE);
            secondIncludeItem.setVisibility(View.GONE);
            thirdIncludeItem.setVisibility(View.GONE);
            tvItemRemarkTotalMoney.setVisibility(View.GONE);
            tvRemarkTitle.setVisibility(View.GONE);
            tvItemRemarkNoMoney.setVisibility(View.VISIBLE);
        } else {
            if (recordDetailList.size() > 0) {
                RecordDetail firstRecordDetail = recordDetailList.get(0);
                String year1 = firstRecordDetail.getYear();
                String time1 = year1.substring(year1.length() - 2) + "/"
                        + firstRecordDetail.getMonth() + "/" + firstRecordDetail.getDay();
                firstIncludeItem.setVisibility(View.VISIBLE);
                ivRemarkTypeIcon1.setImageResource(firstRecordDetail.getIconUrl());
                tvRemarkRecordTime1.setText(time1);
                String detailType1 = firstRecordDetail.getRemark();
                if (detailType1.length() > 4) {
                    detailType1 = detailType1.substring(0, 4) + "...";
                }
                String money1 = firstRecordDetail.getMoney();
                if (money1.length() > 4) {
                    money1 = money1.substring(0, 4) + "...";
                }
                tvRemarkRecordType1.setText(detailType1);
                tvRemarkRecordMoney1.setText(money1);
                if (recordDetailList.size() > 1) {
                    RecordDetail secondRecordDetail = recordDetailList.get(1);
                    String year2 = firstRecordDetail.getYear();
                    String time2 = year1.substring(year2.length() - 2) + "/"
                            + firstRecordDetail.getMonth() + "/" + firstRecordDetail.getDay();
                    secondIncludeItem.setVisibility(View.VISIBLE);
                    ivRemarkTypeIcon2.setImageResource(secondRecordDetail.getIconUrl());
                    tvRemarkRecordTime2.setText(time2);
                    String detailType2 = secondRecordDetail.getRemark();
                    if (detailType2.length() > 4) {
                        detailType2 = detailType2.substring(0, 4) + "...";
                    }
                    String money2 = secondRecordDetail.getMoney();
                    if (money2.length() > 4) {
                        money2 = money2.substring(0, 4) + "...";
                    }
                    tvRemarkRecordType2.setText(detailType2);
                    tvRemarkRecordMoney2.setText(money2);
                    if (recordDetailList.size() > 2) {
                        RecordDetail thirdRecordDetail = recordDetailList.get(2);
                        String year3 = firstRecordDetail.getYear();
                        String time3 = year1.substring(year3.length() - 2) + "/"
                                + firstRecordDetail.getMonth() + "/" + firstRecordDetail.getDay();
                        thirdIncludeItem.setVisibility(View.VISIBLE);
                        ivRemarkTypeIcon3.setImageResource(thirdRecordDetail.getIconUrl());
                        tvRemarkRecordTime3.setText(time3);
                        String detailType3 = thirdRecordDetail.getRemark();
                        if (detailType3.length() > 4) {
                            detailType3 = detailType3.substring(0, 4) + "...";
                        }
                        String money3 = thirdRecordDetail.getMoney();
                        if (money3.length() > 4) {
                            money3 = money3.substring(0, 4) + "...";
                        }
                        tvRemarkRecordType3.setText(detailType3);
                        tvRemarkRecordMoney3.setText(money3);
                    } else {
                        thirdIncludeItem.setVisibility(View.GONE);
                    }
                } else {
                    secondIncludeItem.setVisibility(View.GONE);
                }
            }
            tvItemRemarkTotalMoney.setVisibility(View.VISIBLE);
            tvItemRemarkTotalMoney.setText(totalMoney);
            tvRemarkTitle.setVisibility(View.VISIBLE);
            tvItemRemarkNoMoney.setVisibility(View.GONE);
        }
    }

    @Override
    public void showChartMarker(int chartCenterX, int chartCenterY, int dwX, int dwY, int pwX,
                                int pwY, int rwX, int rwY, boolean onlyShowDetailMarker) {
        View rootView = chartFragMainContext.getRootView();

        if (detailsWindow.isShowing()) {
            detailsWindow.update(dwX, dwY, -1, -1, true);
        } else {
            // 当该时间段没有费用时（即总费用为0），则将”没有费用“的marker显示在LineChart中间
            if (mTotalMoney.equals(Constants.ZERO_MONEY)) {
                detailsWindow.showAtLocation(rootView, Gravity.NO_GRAVITY, chartCenterX, chartCenterY);
            } else {
                detailsWindow.showAtLocation(rootView, Gravity.NO_GRAVITY, dwX, dwY);
            }
        }


        if (mTotalMoney.equals(Constants.ZERO_MONEY)) {
            // 当前日期范围没有任何记录时，只显示DetailMarker
            positionWindow.dismiss();
            roundWindow.dismiss();
        } else if (positionWindow.isShowing() && roundWindow.isShowing()) {
            // 只显示 DetailMarker时，把显示的 positionMarker 和 roundMaker关闭
            if (onlyShowDetailMarker) {
                positionWindow.dismiss();
                roundWindow.dismiss();
            } else {
                positionWindow.update(pwX, pwY, -1, -1, true);
                roundWindow.update(rwX, rwY, -1, -1, true);
            }
        } else if (!onlyShowDetailMarker) {
            positionWindow.showAtLocation(rootView, Gravity.NO_GRAVITY, pwX, pwY);
            roundWindow.showAtLocation(rootView, Gravity.NO_GRAVITY, rwX, rwY);
        }
    }


    @Override
    public void showExpendOrIncomeChoosePopupWindow() {
        mPresenter.setMarginShadowView();
        pwEIChoose.showAsDropDown(includeChooseTop);
        viewShadow.setVisibility(View.VISIBLE);
        if (incomeOrExpend.equals(Constants.INCOME)) {
            ivIncomeSelected.setVisibility(View.VISIBLE);
            ivExpendSelected.setVisibility(View.GONE);
        } else {
            ivIncomeSelected.setVisibility(View.GONE);
            ivExpendSelected.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void dismissExpendOrIncomeChoosePopupWindow() {
        pwEIChoose.dismiss();
        viewShadow.setVisibility(View.GONE);
    }

    @Override
    public void chooseExpendOrIncome(String expendOrIncome) {
        incomeOrExpend = expendOrIncome;
        // 更改 chart 和 RecyclerView 的显示数据
        mPresenter.setInitRecyclerViewData(dateType, tabLayoutDatas.get(index), incomeOrExpend);
        mPresenter.setLoadChart(dateType, tabLayoutDatas.get(index), incomeOrExpend);
    }

    @Override
    public void reMarginShadowView() {
        ViewGroup.LayoutParams params = viewShadow.getLayoutParams();
        ViewGroup.MarginLayoutParams marginLayoutParams;
        if (params instanceof ViewGroup.MarginLayoutParams) {
            marginLayoutParams = (ViewGroup.MarginLayoutParams) params;
        } else {
            marginLayoutParams = new ViewGroup.MarginLayoutParams(params);
        }
        marginLayoutParams.topMargin = includeChooseTop.getBottom();
        viewShadow.setLayoutParams(marginLayoutParams);
    }

    @OnClick({R.id.tv_date_type_choose_title, R.id.iv_pie_chart})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_date_type_choose_title:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setShowOrDismissExpendOrIncomeChoosePopupWindow(pwEIChoose);
                break;
            case R.id.iv_pie_chart:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                Intent intent = new Intent(mActivity, PieChartRecordsActivity.class);
                intent.putExtra("dateType", dateType);
                intent.putExtra("index", index);
                intent.putExtra("expendOrIncome", incomeOrExpend);
                mPresenter.setStartAct(intent);
                break;
            default:
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.checkUpdateRecord();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NoticeUpdateUtils.updateRecordsPresenters.remove(mPresenter);
    }

    @Override
    public void updateRecord() {
        notPlaySound = true;
        // 更改 日期类型（每周数据、每月数据、每年数据） ，根据该类型显示数据
        mPresenter.setLoadTabLayoutData(dateType, index);
    }

    @Override
    public void onPageSelect(int position) {
        if (position == Constants.CHART_PAGE) {
            mPresenter.checkUpdateRecord();
            if (mNestedScrollView != null && mNestedScrollView.getScrollY() != 0) {
                mNestedScrollView.scrollTo(0, 0);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_expend_selected:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setChooseExpendOrIncome(Constants.EXPEND);
                mPresenter.setShowOrDismissExpendOrIncomeChoosePopupWindow(pwEIChoose);
                break;
            case R.id.rl_income_selected:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setChooseExpendOrIncome(Constants.INCOME);
                mPresenter.setShowOrDismissExpendOrIncomeChoosePopupWindow(pwEIChoose);
                break;
            case R.id.view_shadow:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setShowOrDismissExpendOrIncomeChoosePopupWindow(pwEIChoose);
                break;
            default:
        }
    }
}
