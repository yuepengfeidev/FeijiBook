package com.example.feijibook.activity.main_act;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.example.feijibook.R;
import com.example.feijibook.activity.add_record_act_from_add_icon_act.AddRecordFromAddIconActivity;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.activity.main_act.chart_frag.ChartFragment;
import com.example.feijibook.activity.main_act.detail_frag.DetailFragment;
import com.example.feijibook.activity.main_act.find_frag.FindFragment;
import com.example.feijibook.activity.main_act.me_frag.MeFragment;
import com.example.feijibook.activity.user_info_act.UIPresenter;
import com.example.feijibook.adapter.ViewPagerAdapter;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.record_bean.DayRecord;
import com.example.feijibook.entity.record_bean.MonthRecord;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.entity.record_bean.WeekRecord;
import com.example.feijibook.entity.record_bean.YearRecord;
import com.example.feijibook.entity.record_type_bean.CategoryType;
import com.example.feijibook.util.SoundShakeUtil;
import com.example.feijibook.widget.MyBottomBar;
import com.example.feijibook.widget.MyViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import static com.example.feijibook.util.PermissionsUtils.PERMISSION_REQUEST_CODE;
import static com.example.feijibook.util.PermissionsUtils.hasPermissions;
import static com.example.feijibook.util.PermissionsUtils.permissions;
import static com.example.feijibook.util.PermissionsUtils.requestNecessaryPermissions;

public class MainActivity extends BaseActivity implements MainContract.View, UIPresenter.OnDestroyActListener {

    ViewPagerAdapter mViewPagerAdapter;
    List<Fragment> fragments = new ArrayList<>();
    @BindView(R.id.view_pager_main)
    MyViewPager viewPagerMain;
    @BindView(R.id.bottom_bar)
    MyBottomBar mMyBottomBar;
    Unbinder unbinder;
    MainContract.Presenter mPresenter;
    DetailFragment mDetailFragment;
    ChartFragment mChartFragment;
    FindFragment mFindFragment;
    MeFragment mMeFragment;
    RealmAsyncTask mRealmAsyncTask;
    Realm realm;
    RealmResults<CategoryType> mResults;
    RealmResults<RecordDetail> mRecordDetailRealmResults;
    RealmResults<DayRecord> mDayRecordRealmResults;
    RealmResults<WeekRecord> mWeekRecordRealmResults;
    RealmResults<MonthRecord> mMonthRecordRealmResults;
    RealmResults<YearRecord> mYearRecordRealmResults;
    static List<ViewPagerSelect> mViewPagerSelectListeners = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        // 没有权限则获取权限
        if (!hasPermissions(permissions)) {
            requestNecessaryPermissions(this, permissions);
        }
        initPresenter();
        Log.d("yue", "onCreate: mainactivity");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && !hasPermissions(permissions)) {
            Toast.makeText(getApplicationContext(), "请确定打开所有权限", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * 初始化Presenter
     */
    private void initPresenter() {
        mDetailFragment = DetailFragment.newInstance();
        mChartFragment = ChartFragment.newInstance();
        mFindFragment = FindFragment.newInstance();
        mMeFragment = MeFragment.newInstance();

        mPresenter = new MainPresenter(this, this, mDetailFragment,
                mChartFragment, mFindFragment, mMeFragment);
        mPresenter.start();

        super.init(mPresenter);
    }

    @Override
    public boolean enableSliding() {
        return false;
    }

    @Override
    public void selectPage(int position) {
        viewPagerMain.setCurrentItem(position);
    }

    @Override
    public void init() {
        String[] titles = {"明细", "图表", "发现", "我的"};
        fragments.add(mDetailFragment);
        fragments.add(mChartFragment);
        fragments.add(mFindFragment);
        fragments.add(mMeFragment);

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addTitlesAndFragments(titles, fragments);
        viewPagerMain.setOffscreenPageLimit(4);
        viewPagerMain.setAdapter(mViewPagerAdapter);
        super.createRootView();
    }

    @Override
    public void initListener() {
        final BaseActivity activity = this;
        mMyBottomBar.setOnRadioClickListener(new MyBottomBar.OnRadioClickListener() {
            @Override
            public void onClick(int position) {
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                switch (position) {
                    case 0:
                    case 1:
                        mPresenter.setSelectPage(position);
                        break;
                    case 2:
                        SoundShakeUtil.playSound(SoundShakeUtil.SELECT_SWOOSH1_SOUND);
                        // 当打开记录添加页面时，添加该界面的相邻界面绑定接口
                        BaseActivity.addBindAdjacentLayer(activity);
                        startActivity(new Intent(MyApplication.sContext, AddRecordFromAddIconActivity.class));
                        return;
                    case 3:
                    case 4:
                        mPresenter.setSelectPage(position - 1);
                        break;
                    default:
                }
            }
        });

        UIPresenter.setOnDestroyActListener(this);

        viewPagerMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mPresenter.setCloseMenu();
                for (ViewPagerSelect viewPagerSelect : mViewPagerSelectListeners) {
                    viewPagerSelect.onPageSelect(i);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    @Override
    public void initDB() {
        realm = Realm.getDefaultInstance();
        mRealmAsyncTask = mPresenter.setInitDB(this, realm);
        // 不是第一次登陆,则加载 数据库中的类型列表
        if (mRealmAsyncTask == null) {
            mResults = realm.where(CategoryType.class).findAllAsync();
            mResults.addChangeListener(new RealmChangeListener<RealmResults<CategoryType>>() {
                @Override
                public void onChange(@NonNull RealmResults<CategoryType> categoryTypes) {
                    mPresenter.setModelTypeList(categoryTypes, realm);
                    mResults.removeAllChangeListeners();
                }
            });
        }

        mRecordDetailRealmResults = realm.where(RecordDetail.class).findAllAsync();
        mDayRecordRealmResults = realm.where(DayRecord.class).findAllAsync();
        mWeekRecordRealmResults = realm.where(WeekRecord.class).findAllAsync();
        mMonthRecordRealmResults = realm.where(MonthRecord.class).findAllAsync();
        mYearRecordRealmResults = realm.where(YearRecord.class).findAllAsync();

        mRecordDetailRealmResults.addChangeListener(new RealmChangeListener<RealmResults<RecordDetail>>() {
            @Override
            public void onChange(@NonNull RealmResults<RecordDetail> recordDetails) {
                mPresenter.setRecordDetailModelTypeList(recordDetails, realm);
                mRecordDetailRealmResults.removeAllChangeListeners();
            }
        });
        mDayRecordRealmResults.addChangeListener(new RealmChangeListener<RealmResults<DayRecord>>() {
            @Override
            public void onChange(@NonNull RealmResults<DayRecord> dayRecords) {
                mPresenter.setDayRecordModelTypeList(dayRecords, realm);
                mDayRecordRealmResults.removeAllChangeListeners();
            }
        });
        mWeekRecordRealmResults.addChangeListener(new RealmChangeListener<RealmResults<WeekRecord>>() {
            @Override
            public void onChange(@NonNull RealmResults<WeekRecord> weekRecords) {
                mPresenter.setWeekRecordModelTypeList(weekRecords, realm);
                mWeekRecordRealmResults.removeAllChangeListeners();
            }
        });
        mMonthRecordRealmResults.addChangeListener(new RealmChangeListener<RealmResults<MonthRecord>>() {
            @Override
            public void onChange(@NonNull RealmResults<MonthRecord> monthRecords) {
                mPresenter.setMonthRecordModelTypeList(monthRecords, realm);
                mMonthRecordRealmResults.removeAllChangeListeners();
            }
        });
        mYearRecordRealmResults.addChangeListener(new RealmChangeListener<RealmResults<YearRecord>>() {
            @Override
            public void onChange(@NonNull RealmResults<YearRecord> yearRecords) {
                mPresenter.setYearRecordModelTypeList(yearRecords, realm);
                mYearRecordRealmResults.removeAllChangeListeners();
            }
        });
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (mRealmAsyncTask != null && !mRealmAsyncTask.isCancelled()) {
            mRealmAsyncTask.cancel();
        }
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 如果添加了相邻绑定层的接口到静态list中，在onResume当前界面时就移除
        BaseActivity.removeMainBind();
    }

    public static void addViewPagerSelectListener(ViewPagerSelect viewPagerSelect) {
        mViewPagerSelectListeners.add(viewPagerSelect);
    }

    @Override
    public void onDestroyAct() {
        mViewPagerSelectListeners.clear();
        finish();
    }

    public interface ViewPagerSelect {
        void onPageSelect(int position);
    }

}
