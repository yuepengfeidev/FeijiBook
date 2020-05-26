package com.example.feijibook.activity.pie_chart_records_act;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
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
import com.example.feijibook.activity.one_type_records_line_chart_act.OneTypeRecordsLineChartActivity;
import com.example.feijibook.adapter.RecordMoneyRankAdapter;
import com.example.feijibook.app.Constants;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.util.SoundShakeUtil;
import com.example.feijibook.widget.MyNestedScrollView;
import com.example.feijibook.widget.SwipeBackLayout;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class PieChartRecordFragment extends Fragment implements PCRContract.View, View.OnClickListener {
    Activity mActivity;
    PCRContract.Presenter mPresenter;
    View mView;
    @BindView(R.id.tv_date_type_choose_title)
    TextView tvDateTypeChooseTitle;
    @BindView(R.id.rg_category_choose)
    RadioGroup rgCategoryChoose;
    @BindView(R.id.rb_week_chart_frag)
    RadioButton rbWeekChartFrag;
    @BindView(R.id.rb_month_chart_frag)
    RadioButton rbMonthChartFrag;
    @BindView(R.id.rb_year_chart_frag)
    RadioButton rbYearChartFrag;
    @BindView(R.id.tb_date_choose)
    TabLayout tbDateChoose;
    @BindView(R.id.pie_chart_pie_chart_records_frag)
    PieChart pieChartPieChartRecordsFrag;
    @BindView(R.id.layout_custom_legned)
    LinearLayout layoutCustomLegned;
    @BindView(R.id.tv_rank_title_name)
    TextView tvRankTitleName;
    @BindView(R.id.rv_rank_chart_frag)
    RecyclerView rvRankChartFrag;
    @BindView(R.id.nested_scroll_view_chart)
    MyNestedScrollView nestedScrollViewChart;
    @BindView(R.id.include_category_choose_top)
    View includeCategoryChooseTop;
    Unbinder unbinder;
    int[] mColors = {R.color.pie_chart_purple, R.color.pie_chart_blue,
            R.color.pie_chart_green, R.color.pie_chart_red,
            R.color.pie_chart_orange, R.color.pie_chart_yellow};
    int[] legendBlockColors = {R.drawable.pie_chart_purple_block, R.drawable.pie_chart_blue_block,
            R.drawable.pie_chart_green_block, R.drawable.pie_chart_red_block,
            R.drawable.pie_chart_organe_block, R.drawable.pie_chart_yellow_block};
    @BindView(R.id.vs_no_data)
    ViewStub vsNoData;
    TextView tvNoData;
    private boolean mIsInflate = false;

    /**
     * 当前选择的日期段
     */
    private int index = 0;

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
    View viewShadow;

    /**
     * tabLayout的数据列表
     */
    private List<String> tabLayoutDatas = new ArrayList<>();
    private RecordMoneyRankAdapter mAdapter;
    private String incomeOrExpend;
    private String mDateType = Constants.WEEK;

    /**
     * TabLayout item的字体属性
     */
    private static int tlSelectBlack;
    private static int tlUnSelectGray;
    private static float tlSelectSize;
    private static float tlUnSelectSize;
    /**
     * TabLayout在初始化时，总会选择一次，即除法选择监听，所以需要忽视不播放点击声音
     */
    private boolean notPlaySound = true;


    public PieChartRecordFragment() {
        // Required empty public constructor
    }

    public static PieChartRecordFragment newInstance() {
        return new PieChartRecordFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_pie_chart_record, container, false);
        unbinder = ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void finishAct() {
        mActivity.finish();
    }

    @Override
    public void initListener() {
        tlSelectBlack = ContextCompat.getColor(mActivity, R.color.tab_selected_color);
        tlUnSelectGray = ContextCompat.getColor(mActivity, R.color.tab_unselected_color);
        tlSelectSize = getResources().getDimension(R.dimen.tab_layout_text_size_selected);
        tlUnSelectSize = getResources().getDimension(R.dimen.tab_layout_text_size_unselected);

        onViewClicked(mView);
        rlExpend.setOnClickListener(this);
        rlIncome.setOnClickListener(this);
        viewShadow.setOnClickListener(this);

        // 初始化时，会执行一次其中的逻辑
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
                tabText.setTextSize(tlSelectSize);
                index = tab.getPosition();
                nestedScrollViewChart.scrollTo(0, 0);
                mPresenter.setInitRecyclerViewDataAndChart(mDateType, tabLayoutDatas.get(index), incomeOrExpend);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tabText = (TextView) (((LinearLayout) ((LinearLayout)
                        tbDateChoose.getChildAt(0)).getChildAt(tab.getPosition())).getChildAt(1));
                tabText.setTextColor(tlUnSelectGray);
                tabText.setTextSize(tlUnSelectSize);
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
                intent.putExtra("dateType", mDateType);
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
                        mDateType = Constants.WEEK;
                        break;
                    case "月":
                        mDateType = Constants.MONTH;
                        break;
                    case "年":
                        mDateType = Constants.YEAR;
                        break;
                    default:
                        break;
                }
                nestedScrollViewChart.scrollTo(0, 0);
                // 更改 日期类型（每周数据、每月数据、每年数据） ，根据该类型显示数据
                // 同时会通过TabLayout的监听器更改 chart 和 RecyclerView 的显示数据
                mPresenter.setLoadTabLayoutData(mDateType, -1);
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
    public void startAct(Intent intent) {
        BaseActivity.addBindAdjacentLayer((PieChartRecordsActivity) mActivity);
        startActivity(intent);
    }

    @Override
    public void getData(Activity activity, String dateType, int index, String expendOrIncome) {
        mActivity = activity;
        mDateType = dateType;
        this.index = index;
        this.incomeOrExpend = expendOrIncome;
    }

    @Override
    public void initCustomLegend(List<String> labelList, List<String> dataList) {
        // 先清除所又Legend item
        if (layoutCustomLegned.getChildCount() > 0) {
            layoutCustomLegned.removeAllViews();
        }
        for (int i = 0; i < labelList.size(); i++) {
            // 单条图例item的属性
            LinearLayout.LayoutParams singleItemParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            singleItemParams.weight = 1;
            singleItemParams.setMargins(0, 7, 0, 7);
            // 单条图例item的布局
            LinearLayout singleItemLayout = new LinearLayout(mActivity);
            // 横向显示颜色块、类型、比例
            singleItemLayout.setOrientation(LinearLayout.HORIZONTAL);
            singleItemLayout.setGravity(Gravity.CENTER_VERTICAL);
            singleItemLayout.setLayoutParams(singleItemParams);

            // 添加颜色块
            LinearLayout.LayoutParams colorLayoutParams = new LinearLayout.LayoutParams(30, 30);
            colorLayoutParams.setMargins(0, 0, 20, 0);
            LinearLayout colorLayout = new LinearLayout(mActivity);
            colorLayout.setLayoutParams(colorLayoutParams);
            colorLayout.setBackground(ContextCompat.getDrawable(mActivity, legendBlockColors[i]));
            singleItemLayout.addView(colorLayout);

            // 类别和比例部分
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            params.gravity = Gravity.CENTER;

            TextView tvLabel = new TextView(mActivity);
            tvLabel.setText(labelList.get(i));
            tvLabel.setTextColor(ContextCompat.getColor(mActivity, R.color.tab_unselected_color));
            tvLabel.setLayoutParams(params);
            singleItemLayout.addView(tvLabel);

            TextView tvPercent = new TextView(mActivity);
            tvPercent.setText(dataList.get(i));
            tvPercent.setTextColor(ContextCompat.getColor(mActivity, R.color.tab_selected_color));
            tvPercent.setLayoutParams(params);
            singleItemLayout.addView(tvPercent);

            layoutCustomLegned.addView(singleItemLayout);
        }

    }

    @Override
    public void loadChart(List<Float> moneyList, List<String> categoryList) {
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        // 没有记录时，显示一圈灰色饼块
        if (moneyList.size() == 0 && categoryList.size() == 0) {
            entries.add(new PieEntry(1, 0));
            colors.add(ContextCompat.getColor(mActivity, R.color.pie_chart_gray));
        } else {
            for (int i = 0; i < moneyList.size(); i++) {
                entries.add(new PieEntry(moneyList.get(i), i));
                colors.add(ContextCompat.getColor(mActivity, mColors[i]));
            }
        }
        PieDataSet dataSet = new PieDataSet(entries, "");
        // 不显示饼图值
        dataSet.setDrawValues(false);
        dataSet.setColors(colors);
        PieData pieData = new PieData(dataSet);
        pieChartPieChartRecordsFrag.setData(pieData);
        pieChartPieChartRecordsFrag.animateXY(750, 750);
        pieChartPieChartRecordsFrag.invalidate();
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

        // 根据上一个界面的日期类型选择情况，决定改界面的日期选择
        switch (mDateType) {
            case "week":
                rbWeekChartFrag.setChecked(true);
                break;
            case "month":
                rbMonthChartFrag.setChecked(true);
                break;
            case "year":
                rbYearChartFrag.setChecked(true);
                break;
            default:
        }

        initChart();

        viewEIChoose = LayoutInflater.from(MyApplication.sContext)
                .inflate(R.layout.popupwindow_expend_income_choose, pieChartPieChartRecordsFrag, false);
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

        viewShadow = mActivity.findViewById(R.id.view_shadow);
    }

    @Override
    public void initChart() {
        // 设置内部圆的半径
        pieChartPieChartRecordsFrag.setHoleRadius(65f);
        pieChartPieChartRecordsFrag.getLegend().setEnabled(false);
        pieChartPieChartRecordsFrag.setTouchEnabled(false);
        // 设置饼图环内透明圆环半径为0f,则不显示
        pieChartPieChartRecordsFrag.setTransparentCircleRadius(0f);
        // 设置触摸不突出显示
        pieChartPieChartRecordsFrag.setHighlightPerTapEnabled(false);
        pieChartPieChartRecordsFrag.setRotationEnabled(false);
        pieChartPieChartRecordsFrag.setDrawCenterText(true);
        pieChartPieChartRecordsFrag.getDescription().setEnabled(false);
    }

    @Override
    public void initData() {
        mPresenter.setLoadTabLayoutData(mDateType, index);
        mPresenter.setShowExpendOrIncome(incomeOrExpend);
    }

    @Override
    public void loadTabLayout(List<String> keyList, List<String> showList, int index) {
        if (tabLayoutDatas.size() != 0 && tabLayoutDatas.size() != 0) {
            tabLayoutDatas.clear();
            // 移除所有tab，重新加载TabLayout
            tbDateChoose.removeAllTabs();
        }
        tabLayoutDatas.addAll(keyList);
        for (String s : showList) {
            tbDateChoose.addTab(tbDateChoose.newTab().setText(s));
        }
        new Handler().postDelayed(() -> tbDateChoose.getTabAt(index).select(), 200);
    }

    @Override
    public void showExpendOrIncome(String expendOrIncome) {
        String typeChooseTitle = expendOrIncome + "▼";
        String rankTitle = expendOrIncome + "排行榜";
        tvDateTypeChooseTitle.setText(typeChooseTitle);
        tvRankTitleName.setText(rankTitle);
        if (Constants.INCOME.equals(expendOrIncome)) {
            ivExpendSelected.setVisibility(View.GONE);
            ivIncomeSelected.setVisibility(View.VISIBLE);
        } else {
            ivIncomeSelected.setVisibility(View.GONE);
            ivExpendSelected.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showExpendOrIncomeChoosePopupWindow() {
        mPresenter.setMarginShadowView();
        pwEIChoose.showAsDropDown(includeCategoryChooseTop);
        viewShadow.setVisibility(View.VISIBLE);
        if (Constants.INCOME.equals(incomeOrExpend)) {
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
        mPresenter.setInitRecyclerViewDataAndChart(mDateType, tabLayoutDatas.get(index), incomeOrExpend);
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
        marginLayoutParams.topMargin = includeCategoryChooseTop.getBottom();
        viewShadow.setLayoutParams(marginLayoutParams);
    }

    @Override
    public void showPieChartCenterText(SpannableString spannableString) {
        pieChartPieChartRecordsFrag.setCenterText(spannableString);
    }

    @Override
    public void bindMoveListener(final SwipeBackLayout rootView) {
        rootView.setMoveListener(new SwipeBackLayout.MoveListener() {
            @Override
            public void startMove() {
                // 当根视图开始滑动且类型选择弹窗显示时，则关闭
                if (pwEIChoose.isShowing()) {
                    mPresenter.setShowOrDismissExpendOrIncomeChoosePopupWindow(pwEIChoose);
                }
            }
        });
    }

    @Override
    public void updateRecord() {
        notPlaySound = true;
        // 更改 日期类型（每周数据、每月数据、每年数据） ，根据该类型显示数据
        mPresenter.setLoadTabLayoutData(mDateType, index);
    }

    @Override
    public void setPresenter(PCRContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_date_type_choose_title, R.id.iv_pie_chart_records_back, R.id.tv_pie_chart_records_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_date_type_choose_title:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setShowOrDismissExpendOrIncomeChoosePopupWindow(pwEIChoose);
                break;
            case R.id.iv_pie_chart_records_back:
            case R.id.tv_pie_chart_records_back:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setFinishAct();
                break;
            default:
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_expend_selected:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setChooseExpendOrIncome("expend");
                mPresenter.setShowOrDismissExpendOrIncomeChoosePopupWindow(pwEIChoose);
                break;
            case R.id.rl_income_selected:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setChooseExpendOrIncome("income");
                mPresenter.setShowOrDismissExpendOrIncomeChoosePopupWindow(pwEIChoose);
                break;
            case R.id.view_shadow:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setShowOrDismissExpendOrIncomeChoosePopupWindow(pwEIChoose);
                break;
            default:
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.checkUpdateRecord();
        }
    }

}
