package com.example.feijibook.activity.one_type_records_line_chart_act;

import android.app.Activity;
import android.content.Intent;
import android.widget.PopupWindow;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter2;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.highlight.Highlight;

import java.util.List;
import java.util.Map;

/**
 * OTRLCContract
 *
 * @author yuepengfei
 * @date 2019/6/13
 * @description
 */
public interface OTRLCContract {
    interface View extends BaseView<Presenter> {
        /**
         * 销毁当前活动
         */
        void finishAct();

        /**
         * 加载 折线图
         *
         * @param yDataList    y轴数据
         * @param xAxisList    x轴的轴值
         * @param keyList      日期
         * @param totalMoney   总金额
         * @param averageMoney 平均金额,按照该范围内记录的天数进行平均
         */
        void loadChart(List<Float> yDataList, List<String> xAxisList, List<String> keyList, String totalMoney, String averageMoney);

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
         * @param index tabLayout 的位置
         */
        void loadTabLayout(List<String> keyList, List<String> showList,int index);

        /**
         * 显示 总金额和平均金额
         *
         * @param totalMoney   总金额
         * @param averageMoney 平均金额
         */
        void showTotalAverage(String totalMoney, String averageMoney);

        /**
         * 初始化 chart的marker
         *
         * @param yValue           chart的HighLight的XPx（y轴值）
         * @param totalMoney       选择日期的总费用
         * @param recordDetailList 选择日期的所有记录
         */
        void initChartMarker(float yValue, String totalMoney, List<RecordDetail> recordDetailList);

        /**
         * 显示 chart的marker
         *
         * @param chartCenterX         lineChart的中心坐标X
         * @param chartCenterY         lineChart的中心坐标Y
         * @param dwX                  detailPopupWindow的X坐标
         * @param dwY                  detailPopupWindow的Y坐标
         * @param pwX                  positionPopupWindowX坐标
         * @param pwY                  positionPopupWindowY坐标
         * @param rwX                  roundPopupWindowX坐标
         * @param rwY                  roundPopupWindowY坐标
         * @param onlyShowDetailMarker 是否只显示DetailMarker
         */
        void showChartMarker(int chartCenterX, int chartCenterY,
                             int dwX, int dwY,
                             int pwX, int pwY,
                             int rwX, int rwY,
                             boolean onlyShowDetailMarker);


        /**
         * 打开活动
         */
        void startAct(Intent intent);

        /**
         * 拿到父布局的activity 和 选择日期类、TabLayout的选择位置的日期段
         *
         * @param activity 该碎片的活动
         */
        void getData(Activity activity,String dateType,String date);

        /**
         * 显示 该类型标题
         *
         * @param titleString 标题
         */
        void showTitle(String titleString);

        /**
         * 显示 是收入 还是 支出类型
         *
         * @param expendOrIncome expend\income
         */
        void showExpendOrIncome(String expendOrIncome);
        /**
         * 更新该界面的记录
         */
        void updateRecord();
    }

    interface Presenter extends BasePresenter2 {

        /**
         * 设置销毁当前活动
         */
        void setFinishAct();

        /**
         * 设置打开活动
         */
        void setStartActivity(Intent intent);


        /**
         * 获取TabLayout数据并加载数据
         *
         * @param type 获取数据类型（week,month,year）
         * @param date 选择的日期 date为""则选择最后一个TabItem
         */
        void setLoadTabLayoutData(String type,String date);

        /**
         * 设置加载折线图
         *
         * @param dateType       类型（week、month、year)
         * @param dateRange      时间范围(week的 2019-14 , month 的 2019-05)，用于得到范围内所有日期
         */
        void setLoadChart(String dateType, String dateRange);

        /**
         * 设置 加载金额排行
         *
         * @param dateType       类型（week、month、year)
         * @param dateRange      时间范围(week的 2019-14 , month 的 2019-05)，用于得到范围内所有日期
         */
        void setInitRecyclerViewData(String dateType, String dateRange);


        /**
         * 设置初始化 chart 的marker标记
         *
         * @param highlight      chart的highlight
         * @param keyList        日期key列表
         * @param dateType       日期类型（week、month、year）
         */
        void setInitChartMarker(Highlight highlight, List<String> keyList, String dateType);

        /**
         * 显示chart的marker标记
         *
         * @param highlight chart的highlight
         * @param lineChart 图表linechart
         * @param dw        detailPopupWindow的view
         * @param pw        positionPopupWindow的View
         * @param rw        roundPopupWindow的view
         */
        void setShowChartMarker(Highlight highlight, LineChart lineChart, PopupWindow dw, PopupWindow pw, PopupWindow rw);

    }

    interface Model {
        /**
         * 获取 该类型的最早日期和最晚日期
         *
         * @param detailType 记录类型
         * @param dateType   week、month、year
         * @return 返回最早日期和最晚日期的map
         */
        Map<String, String> getStartAndEndDate(String detailType, String dateType);

        /**
         * 获取该时间段的的数据记录
         *
         * @param detailType 记录类型
         * @param dateType   日期类型（day,week、month、year）
         * @param dateKey    时间作为的主键(2019-05-09)
         * @return 返回该天所有记录
         */
        List<RecordDetail> getRecord(String detailType, String dateType, String dateKey);

        /**
         * 获取该日期的 总额
         *
         * @param detailType 记录类型
         * @param dateType 类型（week、month、year)
         * @param dateKey  该日期作为主键
         * @return 该日期总额
         */
        Float getTotalMoney(String detailType, String dateType, String dateKey);

    }
}


