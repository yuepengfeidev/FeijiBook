package com.example.feijibook.activity.add_record_act_from_add_icon_act;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter2;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.entity.record_type_bean.OptionalType;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Field;

/**
 * Created by 你是我的 on 2019/3/20
 */
public interface ARFAContract {
    interface View extends BaseView<Presenter> {
        /**
         * 关闭活动
         */
        void finishAct();

        /**
         * 初始化控件,传入viewpager的fragment列表数据
         *
         * @param fragments 碎片列表
         */
        void initWidget(List<Fragment> fragments);

        /**
         * 初始化数字键盘
         */
        void initFigureSoftKeyBoard();

        void initYMDDatePicker(long beginTime, long endTime);// 初始化年月日时间选择器

        void showYMDDatePicker();// 显示年月日时间选择器

        void showFigureSoftKeyboard();// 显示数字软键盘

        void dismissFigureSoftKeyboard();// 关闭数字软键盘

        void getAct(Activity activity);// 拿到父布局的activity

        /**
         * 获取记录的id
         *
         * @param id 记录id
         */
        void getId(String id);

        void upInput();// 当软键盘弹起，提高输入框

        void downInput();// 当软键盘收起，降低恢复输入框

        int tabLayoutBottom();// tablayout的getBottom

        void showSelectDate(String selectDate);// 显示选择的时间

        /**
         * 数字键盘状态
         *
         * @return 是否显示
         */
        boolean figureSoftKeyBoardState();

        /**
         * 打开活动
         */
        void startAct(Intent intent);

        /**
         * 选择相应的类型添加界面
         *
         * @param position viewpager选择的类型界面（支出：0， 收入：1）
         */
        void selectPage(int position);

        /**
         * 显示记录的金额和表述到数字键盘上
         *
         * @param recordDetail 选择的记录
         */
        void showMoneyAndRemark(RecordDetail recordDetail);
    }

    interface Presenter extends BasePresenter2 {

        void setFinishAct();// 设置关闭活动

        void setChooseType(OptionalType optionalType);// 设置 选择到的类型

        void setUpInput();// 设置提高输入框

        void setDownInput();// 设置降低恢复输入框

        void setDismissFigureSoftKeyboard(int pagerPosition, Activity activity);// 设置关闭数字软键盘

        void setShowFigureSoftKeyboard();// 设置显示数字软键盘

        int getTabLayoutBottom();// 获取tablayout的getBottom

        void setShowDatePicker();// 设置显示时间选择

        void setShowSelectDate(long time);// 设置显示时间

        /**
         * 是否显示数字键盘
         *
         * @return 数字键怕状态
         */
        boolean getFigureSoftKeyBoardState();

        /**
         * 异步加载 不显示的控件
         */
        void setAsnyInit();

        /**
         * 设置存储，如果不是编辑状态，直接存储记录，否则存入静态存储类中
         *
         * @param map       输入的备注 和金额
         * @param saveOrChange 是否保存或改变记录
         */
        void setSave(Map<String, String> map, boolean saveOrChange);

        /**
         * 设置 Calendar 选择的日期
         *
         * @param chooseDate 用于显示的选择的日期字符串
         */
        void setChooseDate(String chooseDate);

        /**
         * 初始化显示数字软件盘中的选择日期
         */
        void setInitChooseDate();

        /**
         * 设置打开活动
         */
        void setStartAct(Intent intent);

        /**
         * 显示选择的记录的详情到数字键盘上
         */
        void setShowRecordOnKB();
    }

    interface Model {
        /**
         * 存储数据
         */
        void saveRecord();

        /**
         * 获取指定id的记录
         *
         * @param id 记录的id
         */
        RecordDetail getRecord(String id);

        /**
         * 改变记录
         * @return 新id
         */
        RecordDetail  changeRecord();

        Observable<String> requestAddDetailRecord(String account,RecordDetail recordDetail);

        Observable<String> requestChangeDetailRecord(String account, String resId, RecordDetail recordDetail);

        Observable<String> requestUploadTypeSetting( RequestBody body);
    }

}
