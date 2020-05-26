package com.example.feijibook.activity.main_act;

import android.app.Activity;

import com.example.feijibook.activity.main_act.chart_frag.ChartFragment;
import com.example.feijibook.activity.main_act.chart_frag.ChartPresenter;
import com.example.feijibook.activity.main_act.detail_frag.DetailFragment;
import com.example.feijibook.activity.main_act.detail_frag.DetailPresenter;
import com.example.feijibook.activity.main_act.find_frag.FindFragment;
import com.example.feijibook.activity.main_act.find_frag.FindPresenter;
import com.example.feijibook.activity.main_act.me_frag.MeFragment;
import com.example.feijibook.activity.main_act.me_frag.MePresenter;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.gson_bean.UserInfoBean;
import com.example.feijibook.entity.record_bean.DayRecord;
import com.example.feijibook.entity.record_bean.MonthRecord;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.entity.record_bean.WeekRecord;
import com.example.feijibook.entity.record_bean.YearRecord;
import com.example.feijibook.entity.record_type_bean.CategoryType;
import com.example.feijibook.http.BaseObserver;
import com.example.feijibook.http.ExceptionHandle;
import com.example.feijibook.util.FileUtils;
import com.example.feijibook.util.NoticeUpdateUtils;
import com.example.feijibook.util.SharedPreferencesUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmResults;
import okhttp3.ResponseBody;

/**
 * Created by 你是我的 on 2019/3/11
 */
public class MainPresenter implements MainContract.Presenter {
    private Activity mActivity;
    private MainContract.View mView;
    private DetailFragment mDetailFragment;
    private ChartFragment mChartFragment;
    private FindFragment mFindFragment;
    private MeFragment mMeFragment;
    private MainContract.Model mModel;

    MainPresenter(Activity activity, MainContract.View view,
                  DetailFragment detailFragment, ChartFragment chartFragment,
                  FindFragment findFragment, MeFragment meFragment) {
        mView = view;
        mActivity = activity;

        mDetailFragment = detailFragment;
        mChartFragment = chartFragment;
        mFindFragment = findFragment;
        mMeFragment = meFragment;

        mView.setPresenter(this);
    }

    @Override
    public void setSelectPage(int position) {
        mView.selectPage(position);
    }

    @Override
    public void setInit() {
        mView.initDB();
        mView.init();
        mView.initListener();
    }

    @Override
    public RealmAsyncTask setInitDB(Activity activity, Realm realm) {
        if (mModel.checkFirstLogin(activity)) {
            // 存储该用户默认设置
            mModel.saveUserSetting();
            // 如果是第一次登录该手机，则获取其昵称头像，存储到SharedPreferences
            mModel.requestUserInfo(SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.ACCOUNT))
                    .subscribe(new BaseObserver<UserInfoBean>() {
                        @Override
                        public void onSuccess(UserInfoBean userInfoBean) {
                            SharedPreferencesUtils.saveStrToSp(SharedPreferencesUtils.NICKNAME, userInfoBean.getNickname());
                            SharedPreferencesUtils.saveStrToSp(SharedPreferencesUtils.SEX, userInfoBean.getSex());
                            // 获取头像
                            if (userInfoBean.getPortrait() != null || "".equals(userInfoBean.getPortrait())) {
                                mModel.downLoadPortrait(userInfoBean.getPortrait()).subscribe(new BaseObserver<ResponseBody>() {
                                    @Override
                                    public void onSuccess(ResponseBody responseBody) {
                                        FileUtils.saveFile(getPath(), responseBody.byteStream());
                                        SharedPreferencesUtils.saveStrToSp(SharedPreferencesUtils.PORTRAIT, getPath());
                                        NoticeUpdateUtils.updateInfo = true;
                                    }

                                    @Override
                                    public void onHttpError(ExceptionHandle.ResponseException exception) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onHttpError(ExceptionHandle.ResponseException exception) {

                        }
                    });
            return mModel.saveDefaultChooseType(realm);
        }
        return null;
    }

    @Override
    public void setModelTypeList(RealmResults<CategoryType> categoryTypes, Realm realm) {
        mModel.setTypeList(categoryTypes, realm);
    }

    @Override
    public void setRecordDetailModelTypeList(RealmResults<RecordDetail> recordDetails, Realm realm) {
        mModel.setRecordDetailTypeList(recordDetails, realm);
    }

    @Override
    public void setDayRecordModelTypeList(RealmResults<DayRecord> dayRecords, Realm realm) {
        mModel.setDayRecordTypeList(dayRecords, realm);
    }

    @Override
    public void setWeekRecordModelTypeList(RealmResults<WeekRecord> weekRecords, Realm realm) {
        mModel.setWeekRecordTypeList(weekRecords, realm);
    }

    @Override
    public void setMonthRecordModelTypeList(RealmResults<MonthRecord> monthRecords, Realm realm) {
        mModel.setMonthRecordTypeList(monthRecords, realm);
    }

    @Override
    public void setYearRecordModelTypeList(RealmResults<YearRecord> yearRecords, Realm realm) {
        mModel.setYearRecordTypeList(yearRecords, realm);
    }

    @Override
    public void setCloseMenu() {
        mDetailFragment.closeMenu();
    }

    /**
     * 裁剪后的地址
     */
    private String getPath() {
        return MyApplication.BASE_PATH + "/portrait.png";
    }

    @Override
    public void start() {
        new DetailPresenter(mActivity, mDetailFragment).start();
        new ChartPresenter(mActivity, mChartFragment).start();
        new FindPresenter(mActivity, mFindFragment).start();
        new MePresenter(mActivity, mMeFragment).start();

        mModel = new MainModel();
    }
}
