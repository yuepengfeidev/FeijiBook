package com.example.feijibook.activity.main_act;

import android.app.Activity;

import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BaseView;
import com.example.feijibook.entity.gson_bean.UserInfoBean;
import com.example.feijibook.entity.record_bean.DayRecord;
import com.example.feijibook.entity.record_bean.MonthRecord;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.entity.record_bean.WeekRecord;
import com.example.feijibook.entity.record_bean.YearRecord;
import com.example.feijibook.entity.record_type_bean.CategoryType;

import io.reactivex.Observable;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmResults;
import okhttp3.ResponseBody;

/**
 * Created by 你是我的 on 2019/3/11
 */
public interface MainContract {
    interface View extends BaseView<Presenter> {
        void selectPage(int position);// 选择viewpager界面

        void init();// 初始化


        /**
         * 获取 realm存储 的事务, 加载数据库
         */
        void initDB();

    }

    interface Presenter extends BasePresenter {
        void setSelectPage(int position);// 设置viewpager选择页面


        /**
         * 初始化 数据库
         *
         * @return 返回 realm 处理的事务
         */
        RealmAsyncTask setInitDB(Activity activity, Realm realm);

        /**
         * 通知 model 设置类型列表数据
         *
         * @param categoryTypes 所有类型
         */
        void setModelTypeList(RealmResults<CategoryType> categoryTypes, Realm realm);

        /**
         * 通知 model 设置没想账单记录列表数据
         *
         * @param recordDetails 所有账单记录
         * @param realm         realm数据库
         */
        void setRecordDetailModelTypeList(RealmResults<RecordDetail> recordDetails, Realm realm);

        /**
         * 通知 model 设置每日总详情列表数据
         *
         * @param dayRecords 所有日总详情
         * @param realm      realm数据库
         */
        void setDayRecordModelTypeList(RealmResults<DayRecord> dayRecords, Realm realm);

        /**
         * 通知 model 设置每周总详情列表数据
         *
         * @param weekRecords 所有周总详情
         * @param realm       realm数据库
         */
        void setWeekRecordModelTypeList(RealmResults<WeekRecord> weekRecords, Realm realm);

        /**
         * 通知 model 设置每月总详情列表数据
         *
         * @param monthRecords 所有月总详情
         * @param realm        realm数据库
         */
        void setMonthRecordModelTypeList(RealmResults<MonthRecord> monthRecords, Realm realm);

        /**
         * 通知 model 设置每年 总详情列表数据
         *
         * @param yearRecords 所有年总详情
         * @param realm       realm数据库
         */
        void setYearRecordModelTypeList(RealmResults<YearRecord> yearRecords, Realm realm);

        /**
         * 切换viewpager时 关闭打开的删除按钮
         */
        void setCloseMenu();

    }

    interface Model {
        /**
         * 检查是否第一次登陆，初始化存储数据
         *
         * @param activity activity
         * @return 是否是第一次登陆
         */
        boolean checkFirstLogin(Activity activity);

        /**
         * 储存 默认记录选择类型(餐饮、购物。。) 到数据库
         *
         * @param realm 数据库realm
         * @return 返回事务，进行处理
         */
        RealmAsyncTask saveDefaultChooseType(Realm realm);

        /**
         * 设置 类型列表数据
         *
         * @param categoryTypes 所有账单类型
         */
        void setTypeList(RealmResults<CategoryType> categoryTypes, Realm realm);

        /**
         * 设置所有 账单记录到存储类中，处理数据后，方便获取
         *
         * @param recordDetails 所有账单数据库
         * @param realm         realm数据库
         */
        void setRecordDetailTypeList(RealmResults<RecordDetail> recordDetails, Realm realm);

        void setDayRecordTypeList(RealmResults<DayRecord> dayRecords, Realm realm);

        void setWeekRecordTypeList(RealmResults<WeekRecord> weekRecords, Realm realm);

        void setMonthRecordTypeList(RealmResults<MonthRecord> monthRecords, Realm realm);

        void setYearRecordTypeList(RealmResults<YearRecord> yearRecords, Realm realm);

        /**
         * 网络请求获取账户信息接口
         *
         * @param account 账户名
         * @return 账户信息请求接口
         */
        Observable<UserInfoBean> requestUserInfo(String account);

        /**
         * 下载头像
         *
         * @param url 头像地址
         * @return 头像文件
         */
        Observable<ResponseBody> downLoadPortrait(String url);

        /**
         * 存储用户设置
         */
        void saveUserSetting();
    }

}
