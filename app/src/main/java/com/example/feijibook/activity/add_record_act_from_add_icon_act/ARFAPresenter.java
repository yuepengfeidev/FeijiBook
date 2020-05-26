package com.example.feijibook.activity.add_record_act_from_add_icon_act;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.feijibook.activity.add_record_act_from_add_icon_act.type_choose_frag.TCContract;
import com.example.feijibook.activity.add_record_act_from_add_icon_act.type_choose_frag.TCPresenter;
import com.example.feijibook.activity.add_record_act_from_add_icon_act.type_choose_frag.TypeChooseFragment;
import com.example.feijibook.app.Constants;
import com.example.feijibook.entity.gson_bean.RecordType;
import com.example.feijibook.entity.gson_bean.RecordTypeBean;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.entity.record_type_bean.AddtiveType;
import com.example.feijibook.entity.record_type_bean.OptionalType;
import com.example.feijibook.http.BaseObserver;
import com.example.feijibook.http.ExceptionHandle;
import com.example.feijibook.util.DateFormatUtils;
import com.example.feijibook.util.DateUtils;
import com.example.feijibook.util.NoticeUpdateUtils;
import com.example.feijibook.util.RecordListUtils;
import com.example.feijibook.util.SetTypeListUtil;
import com.example.feijibook.util.SharedPreferencesUtils;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by 你是我的 on 2019/3/20
 */
public class ARFAPresenter implements ARFAContract.Presenter {
    private Context mContext;
    private ARFAContract.View mView;
    private TCContract.Presenter mETCPresenter;
    private TCContract.Presenter mITCPresenter;
    // 选中的类型
    private OptionalType mOptionalType;
    private ARFAContract.Model mModel;
    private String chooseDate;
    private String mId;
    /**
     * 当前记录
     */
    private RecordDetail mRecordDetail;
    private boolean isNeedUpdate = false;


    ARFAPresenter(Activity activity, ARFAContract.View view, String id) {
        mContext = activity;
        mView = view;
        mId = id;
        mView.setPresenter(this);
        mView.getAct(activity);
        mView.getId(mId);
    }

    @Override
    public void start() {
        mModel = new ARFAModel();
        if (mId != null) {
            // 有id则获取该id的记录
            mRecordDetail = mModel.getRecord(mId);
            // 编辑原数据之前，先保存原数据
            RecordListUtils.setResRecord((RecordDetail) mRecordDetail.clone());
        } else {
            // 没有id表示为正在编辑状态，重置 编辑的静态记录
            RecordListUtils.setIsEditingRecord(new RecordDetail());
        }
    }

    @Override
    public void setInit() {
        List<Fragment> fragments = new ArrayList<>();
        TypeChooseFragment expendTypeChooseFragment = TypeChooseFragment.newInstance();
        TypeChooseFragment incomeTypeChooseFragment = TypeChooseFragment.newInstance();
        fragments.add(expendTypeChooseFragment);
        fragments.add(incomeTypeChooseFragment);
        mETCPresenter = new TCPresenter(this, expendTypeChooseFragment, true);
        mITCPresenter = new TCPresenter(this, incomeTypeChooseFragment, false);
        mITCPresenter.start();
        mETCPresenter.start();
        mView.initWidget(fragments);
    }

    @Override
    public void setFinishAct() {
        NoticeUpdateUtils.updateRecordsPresenters.remove(this);
        mView.finishAct();
    }

    @Override
    public void setChooseType(OptionalType optionalType) {
        this.mOptionalType = optionalType;
    }

    @Override
    public void setUpInput() {
        mView.upInput();
    }

    @Override
    public void setDownInput() {
        mView.downInput();
    }

    @Override
    public void setDismissFigureSoftKeyboard(int pagerPosition, Activity activity) {
        // 当pagerPosition为1时，则viewpager从0滑动到1，恢复0页面
        switch (pagerPosition) {
            case 0:
                // 恢复1页面
                mITCPresenter.recoverRV(activity);
                break;
            case 1:
                // 恢复0页面
                mETCPresenter.recoverRV(activity);
                break;
            default:
        }
        mView.dismissFigureSoftKeyboard();
    }

    @Override
    public void setShowFigureSoftKeyboard() {
        mView.showFigureSoftKeyboard();
    }

    @Override
    public int getTabLayoutBottom() {
        return mView.tabLayoutBottom();
    }

    @Override
    public void setShowDatePicker() {
        mView.showYMDDatePicker();
    }

    @Override
    public void setShowSelectDate(long time) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        String curTime = format.format(System.currentTimeMillis());
        String selectedTime = format.format(time);
        if (selectedTime.equals(curTime)) {
            mView.showSelectDate("今天");
        } else {
            mView.showSelectDate(selectedTime);
        }
    }

    @Override
    public boolean getFigureSoftKeyBoardState() {
        return mView.figureSoftKeyBoardState();
    }

    @Override
    public void setAsnyInit() {
        // 初始化时间选择器数据
        // 时间选择控件的最早和最晚选择时间
        String beginTime = "1998-10-13";
        long beginTimestamp = DateFormatUtils.str2Long(beginTime, false);
        String endTime = "2025-10-13";
        long endTimestamp = DateFormatUtils.str2Long(endTime, false);
        mView.initYMDDatePicker(beginTimestamp, endTimestamp);
        mView.initFigureSoftKeyBoard();

        mView.initListener();

        setShowRecordOnKB();
    }

    @Override
    public void setSave(Map<String, String> map, boolean saveOrChange) {
        RecordDetail recordDetail;
        // 每条记录
        if (RecordListUtils.getIsEditingRecord() != null) {
            recordDetail = RecordListUtils.getIsEditingRecord();
        } else {
            recordDetail = new RecordDetail();
        }
        String remarkContent;
        // 如果 备注 为空 则备注为 类型(餐饮、娱乐。。）
        if (Objects.equals(map.get("remark"), "")) {
            // 如果为 默认 自带类型，则备注呢容 为 类型
            if (mOptionalType.getDefaultOrCustom_O().equals("d")) {
                remarkContent = mOptionalType.getTypeName_O();
            }// 否则，不是默认 则 为 自定义名称
            else {
                remarkContent = mOptionalType.getCustomTypeName();
            }
        } else {
            remarkContent = map.get("remark");
        }

        String money = map.get("money");
        String date = map.get("date");

        Map<String, String> curDateMap = DateUtils.getDate(date);
        String year = curDateMap.get(Constants.YEAR);
        String month = curDateMap.get(Constants.MONTH);
        String day = curDateMap.get(Constants.DAY);
        String dayOfWeek = curDateMap.get("dayOfWeek");
        String weekOfYear = curDateMap.get("weekOfYear");

        recordDetail.setYear(year);
        recordDetail.setMonth(month);
        recordDetail.setDay(day);
        recordDetail.setWeek(dayOfWeek);
        recordDetail.setWeekOfYear(weekOfYear);
        recordDetail.setType(mOptionalType.getIncomeOrExpend_O());

        // 通过选择类型图表地址得到该类型显示的图表的地址
        String s = mContext.getResources().getResourceName(mOptionalType.getTypeIconUrl_O());
        String url = null;
        // 自定义图表地址和默认图标地址命名格式不一样，分别处理
        if (s.contains("category_")) {
            // icon是默认可选择的
            url = s + "_s";
        } else if (s.contains("cc_")) {
            // icon是自定义添加的
            url = s.replace("selector", "s");
        }


        ApplicationInfo applicationInfo = mContext.getApplicationInfo();
        int iconUrl = mContext.getResources().getIdentifier(url, "drawable", applicationInfo.packageName);

        recordDetail.setIconUrl(iconUrl);
        // 如果为不是默认类型，则设置 自定义类型 名称
        if (mOptionalType.getDefaultOrCustom_O().equals("d")) {
            recordDetail.setDetailType(mOptionalType.getTypeName_O());
        } else {
            recordDetail.setDetailType(mOptionalType.getCustomTypeName());
        }
        recordDetail.setMoney(money);
        recordDetail.setRemark(remarkContent);

        RecordListUtils.setIsEditingRecord(recordDetail);
        if (saveOrChange) {
            // 编辑状态，改变数据
            if (mId != null) {
                RecordDetail record = mModel.changeRecord();
                mModel.requestChangeDetailRecord(SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.ACCOUNT),
                        mId, record).subscribe(new BaseObserver<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (s.equals(Constants.CHANGE_SUCCESS)) {
                            Log.d(Constants.HTTP_TAG, "请求记录修改成功!");
                        }else {
                            Log.d(Constants.HTTP_TAG, "请求记录修改失败!");
                        }
                    }

                    @Override
                    public void onHttpError(ExceptionHandle.ResponseException exception) {
                        Log.d(Constants.HTTP_TAG, "请求记录修改错误!");
                    }
                });
            } else {
                // 否则添加数据
                mModel.saveRecord();
                mModel.requestAddDetailRecord(SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.ACCOUNT), recordDetail)
                        .subscribe(new BaseObserver<String>() {
                            @Override
                            public void onSuccess(String s) {
                                if (s.equals(Constants.UPLOAD_SUCCESS)) {
                                    Log.d(Constants.HTTP_TAG, "上传添加记录成功！");
                                }else {
                                    Log.d(Constants.HTTP_TAG, "上传添加记录失败！");
                                }
                            }

                            @Override
                            public void onHttpError(ExceptionHandle.ResponseException exception) {
                                Log.d(Constants.HTTP_TAG, "上传添加记录错误！");
                            }
                        });
            }
            // 通知更新各个存在界面的记录
            NoticeUpdateUtils.noticeUpdateRecords();
            setFinishAct();
        }


    }

    @Override
    public void setChooseDate(String chooseDate) {
        this.chooseDate = chooseDate;
    }

    @Override
    public void setInitChooseDate() {
        if (chooseDate != null) {
            mView.showSelectDate(chooseDate);
        }
    }

    @Override
    public void setStartAct(Intent intent) {
        mView.startAct(intent);
    }

    @Override
    public void setShowRecordOnKB() {
        if (mId != null) {
            // 显示该记录的选择类型状态
            if ("支出".equals(mRecordDetail.getType())) {
                // 支出类型选择第一个界面
                mView.selectPage(0);
                // 在该页面选择相应类型
                mETCPresenter.setCheckType(mRecordDetail);
            } else {
                mView.selectPage(1);
                mITCPresenter.setCheckType(mRecordDetail);
            }
            // 显示编辑记录的时间
            String date = mRecordDetail.getYear() + "-" + mRecordDetail.getMonth() + "-" + mRecordDetail.getDay();
            long time = DateFormatUtils.str2Long(date, true);
            setShowSelectDate(time);
            mView.showMoneyAndRemark(mRecordDetail);
        }
    }

    @Override
    public void isNeedUpdateRecord(boolean needUpdate) {
        isNeedUpdate = needUpdate;
    }

    @Override
    public void checkUpdateRecord() {
        if (!isNeedUpdate) {
            return;
        }
        // 更改设置则请求添加类型设置到服务器
        List<OptionalType> expendOt = SetTypeListUtil.getOptionalTypeListInDB("支出");
        List<AddtiveType> expendAt = SetTypeListUtil.getAddibleTypeLstInDB("支出");
        List<OptionalType> incomeOt = SetTypeListUtil.getOptionalTypeListInDB("收入");
        List<AddtiveType> incomeAt = SetTypeListUtil.getAddibleTypeLstInDB("收入");
        List<RecordType> recordTypeList = new ArrayList<>();
        for (OptionalType type : expendOt) {
            recordTypeList.add(new RecordType("optional", "支出",
                    type.getTypeName_O(), type.getTypeIconUrl_O(), type.getCustomTypeName(),
                    type.getDefaultOrCustom_O()));
        }
        for (OptionalType type : incomeOt) {
            recordTypeList.add(new RecordType("optional", "收入",
                    type.getTypeName_O(), type.getTypeIconUrl_O(), type.getCustomTypeName(),
                    type.getDefaultOrCustom_O()));
        }
        for (AddtiveType type : expendAt) {
            recordTypeList.add(new RecordType("addible", "支出",
                    type.getTypeName_A(), type.getTypeIconUrl_A(), null,
                    "d"));
        }
        for (AddtiveType type : incomeAt) {
            recordTypeList.add(new RecordType("addible", "收入",
                    type.getTypeName_A(), type.getTypeIconUrl_A(), null,
                    "d"));
        }

        RecordTypeBean recordTypeBean = new RecordTypeBean(SharedPreferencesUtils.getStrFromSp(SharedPreferencesUtils.ACCOUNT),
                recordTypeList);
        Gson gson = new Gson();
        String json = gson.toJson(recordTypeBean);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        mModel.requestUploadTypeSetting(body).subscribe(new BaseObserver<String>() {
            @Override
            public void onSuccess(String s) {
                if (s.equals(Constants.UPLOAD_SUCCESS)) {
                    Log.d(Constants.HTTP_TAG, "类型设置上传成功!");
                }else {
                    Log.d(Constants.HTTP_TAG, "类型设置上传失败!");
                }
            }

            @Override
            public void onHttpError(ExceptionHandle.ResponseException exception) {
                Log.d(Constants.HTTP_TAG, "类型设置上传错误!");
            }
        });
        isNeedUpdate = false;
    }
}
