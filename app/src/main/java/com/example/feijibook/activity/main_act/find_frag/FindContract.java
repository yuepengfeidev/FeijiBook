package com.example.feijibook.activity.main_act.find_frag;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter2;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;
import com.example.feijibook.entity.weather_bean.ResultBean;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * Created by 你是我的 on 2019/3/11
 */
public interface FindContract {
    interface View extends BaseView<Presenter> {

        /**
         * 打开活动，添加该层活动前移滚动初始动画
         */
        void startAct(Intent intent);

        /**
         * 获得该碎片的activity
         */
        void getAct(Activity activity);

        /**
         * 显示账单
         *
         * @param month  当前月份
         * @param expend 支出
         * @param income 收入
         * @param remain 结余
         */
        void showBill(SpannableString month, SpannableString expend,
                      SpannableString income, SpannableString remain);

        /**
         * 显示预算
         *
         * @param centerText   pieChart中心内容
         * @param month        当月
         * @param remainBudget 剩余预算
         * @param allBudget    所有预算
         * @param allExpend    全部支出
         * @param textContent  预算TextView按钮内容
         * @param textSize     字体大小
         * @param textColor    字体颜色
         * @param textBg       字体背景
         */
        void showBudget(SpannableString centerText, String month, String remainBudget, String allBudget, String allExpend
                , String textContent, float textSize, int textColor, Drawable textBg);

        /**
         * 初始化控件
         */
        void initWidget();

        /**
         * 更新该界面的记录
         */
        void updateRecord();


        /**
         * 显示PieChart动画
         */
        void showPieChartAnim();

        /**
         * 显示预算设置的Dialog
         */
        void showBudgetSettingDialog();

        /**
         * 显示 清除预算 还是设置预算的Dialog
         */
        void showBudgetEditChooseDialog();

        /**
         * 显示天气
         *
         * @param area       地区
         * @param resultBean 天气数据
         */
        void showWeather(String area, ResultBean resultBean);

        /**
         * 显示空气质量
         *
         * @param bg  对应空气质量底色
         * @param air 空气质量
         */
        void showAir(int bg, String air);

        void showDate(String date);
    }

    interface Presenter extends BasePresenter2 {


        /**
         * 设置打开活动
         */
        void setStartActivity(Intent intent);

        /**
         * 设置预算
         *
         * @param budgetMoney      预算金额
         * @param totalExpendMoney 总支出
         */
        void setEditBudget(String budgetMoney, String totalExpendMoney);

        /**
         * 设置显示账单和预算
         */
        void setShowBillAndBudget();

        /**
         * 设置显示预算
         *
         * @param curMonth         当月
         * @param budgetMoney      预算金额
         * @param totalExpendMoney 总支出金额
         */
        void setShowBudget(String curMonth, String budgetMoney, String totalExpendMoney);

        /**
         * 设置显示账单
         *
         * @param curMonth    当月
         * @param totalExpend 总支出
         * @param totalIncome 总收入
         */
        void setShowBill(String curMonth, String totalExpend, String totalIncome);

        /**
         * 设置后存储预算
         *
         * @param budgetMoney 预算金额
         */
        void setSaveBudgetSetting(String budgetMoney);

        /**
         * 设置清除预算设置
         */
        void setClearBudgetSetting();

        /**
         * 设置显示对应状态的Dialog
         */
        void setShowDialog();

        /**
         * 设置显示预算设置Dialog
         */
        void setShowBudgetSettingDialog();

        /**
         * 获取该城市的天气
         */
        void getWeather();

        /**
         * 设置显示空气质量
         *
         * @param aqi 空气质量
         */
        void setShowAir(String aqi);

    }

    interface Model {
        /**
         * 获取当月的收入支出
         *
         * @param month 当前月份
         * @return 当月的总详情记录map(收入 、 支出)
         */
        Map<String, String> getCurMonthTotalRecord(String month);

        /**
         * 获取当前月设置的预算
         *
         * @return 当前的预算
         */
        String getCurMonthBudgetMoney();

        /**
         * 设置当前月设置的预算
         */
        void setSaveMonthBudgetMoney(String budgetMoney);

        /**
         * 获取该城市的天气接口
         *
         * @param city 城市
         */
        Observable<ResponseBody> requestWeather(String city);

        /**
         * 从数据库中获取天气数据
         */
        ResultBean getWeather();

        /**
         * 存储天气数据
         */
        void saveWeather(ResultBean resultBean);
    }
}
