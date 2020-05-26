package com.example.feijibook.activity.pie_chart_records_act;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.widget.PopupWindow;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter2;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;
import com.example.feijibook.widget.SwipeBackLayout;

import java.util.List;
import java.util.Map;

/**
 * PCRContract
 *
 * @author yuepengfei
 * @date 2019/6/30
 * @description
 */
public interface PCRContract {
    interface View extends BaseView<Presenter> {
        /**
         * 销毁当前活动
         */
        void finishAct();

        /**
         * 打开活动，添加该层活动前移滚动初始动画
         */
        void startAct(Intent intent);

        /**
         * 获得该碎片的activity 和 TabLayout的我i之和日期类型
         */
        void getData(Activity activity,String dateType,int index,String expendOrIncome);

        /**
         * 显示自定义图例
         * @param labelList 所有类型数据
         * @param dataList 所有比例数 据
         */
        void initCustomLegend(List<String> labelList,List<String> dataList);

        /**
         * 加载 折线图
         *
         * @param moneyList 饼图金额数据列表
         * @param categoryList 饼图金额类型列表
         */
        void loadChart(List<Float> moneyList, List<String> categoryList);

        /**
         * 加载 RecyclerView 数据
         *
         * @param list 总详情记录
         */
        void loadRecyclerViewData(List list);

        /**
         * 初始化控件
         */
        void initWidget();

        /**
         * 初始化 图表
         */
        void initChart();

        /**
         * 初始化所有数据
         */
        void initData();

        /**
         * 通过不同的选项 显示对应的tablayout数据
         *
         * @param keyList TabLayout数据列表，用于获取该日期的数据记录
         * @param showList   用于显示在tablayout上
         */
        void loadTabLayout(List<String> keyList, List<String> showList,int index);

        /**
         * 显示 是收入 还是 支出类型
         *
         * @param expendOrIncome expend\income
         */
        void showExpendOrIncome(String expendOrIncome);

        /**
         * 显示收入支出选择 popupwindow 弹窗
         */
        void showExpendOrIncomeChoosePopupWindow();

        /**
         * 关闭收入支出选择 popupwindow 弹窗
         */
        void dismissExpendOrIncomeChoosePopupWindow();

        /**
         * 选择收入 或 支出操作
         */
        void chooseExpendOrIncome(String expendOrIncome);

        /**
         * 重新设置 阴影层的margin，达到不覆盖 顶部类型选择布局
         */
        void reMarginShadowView();

        /**
         * 显示PieChart中的总金额文字内容
         * @param spannableString 总金额内容文字
          */
        void showPieChartCenterText(SpannableString spannableString);

        /**
         * 与根视图绑定滑动监听，监听滑动时，关闭显示状态的阴影层或选择PopWindow
         * @param rootView 根视图
         */
        void bindMoveListener(SwipeBackLayout rootView);
        /**
         * 更新该界面的记录
         */
        void updateRecord();


        /**
         * 显示或隐藏“暂无数据”标志
         */
        void showOrHideNoDataSign(List list);
    }

    interface Presenter extends BasePresenter2 {

        /**
         * 设置销毁当前活动
         */
        void setFinishAct();

        /**
         * 设置打开活动
         */
        void setStartAct(Intent intent);

        /**
         * 获取TabLayout数据并加载数据
         *
         * @param type 获取数据类型（week,month,year）
         */
        void setLoadTabLayoutData(String type,int index);

        /**
         * 设置加载折线图
         *
         * @param totalList 选择日期段的该日期段总详情记录
         * @param totalMoney 该日期段总金额
         */
        void setLoadChart(List totalList,String totalMoney);

        /**
         * 设置 加载金额排行 和 饼图
         *
         * @param dateType       类型（week、month、year)
         * @param dateRange      时间范围(week的 2019-14 , month 的 2019-05)，用于得到范围内所有日期
         * @param expendOrIncome 收入或支出
         */
        void setInitRecyclerViewDataAndChart(String dateType, String dateRange, String expendOrIncome);

        /**
         * 设置 显示 或 关闭 收入支出选择 popupwindow 弹窗
         */
        void setShowOrDismissExpendOrIncomeChoosePopupWindow(PopupWindow pwEIChoose);

        /**
         * 设置选择 收入 或 支出
         *
         * @param expendOrIncome income\expend
         */
        void setChooseExpendOrIncome(String expendOrIncome);

        /**
         * 设置 阴影层view的margin
         */
        void setMarginShadowView();

        /**
         * 设置PieChart中的总金额内容
         * @param totalMoney 总金额
         */
        void setPieCenterText(String totalMoney);

        /**
         * 设置显示 是收入 还是 支出类型
         *
         * @param expendOrIncome expend\income
         */
        void setShowExpendOrIncome(String expendOrIncome);

        /**
         * 设置监听滑动返回
         * @param rootView 滑动的根视图
         */
        void setMoveListener(SwipeBackLayout rootView);
    }

    interface Model {
        /**
         * 获取 该日期类型的最早日期和最晚日期
         *
         * @param type week、month、year
         * @return 返回最早日期和最晚日期的map
         */
        Map<String, String> getStartAndEndDate(String type);

        /**
         * 获取 该日期类型 中的 各类型的总详情记录
         *
         * @param dateType 类型（week、month、year)
         * @param dateKey  该日期作为主键
         * @return 返回 该日期类型 中的 各类型的总详情记录
         */
        Object getTotalRecord(String dateType, String dateKey);
    }

}
