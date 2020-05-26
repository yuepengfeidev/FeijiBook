package com.example.feijibook.activity.add_record_act_from_add_icon_act.type_choose_frag;

import android.app.Activity;
import android.content.Intent;

import com.example.feijibook.activity.add_record_act_from_add_icon_act.ARFAContract;
import com.example.feijibook.adapter.RecordTypeChooseRVAdapter;
import com.example.feijibook.app.Constants;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.entity.record_type_bean.OptionalType;
import com.example.feijibook.util.KeyBoardUtil;

/**
 * TCPresenter
 *
 * @author PengFei Yue
 * @date 2019/10/5
 * @description
 */
public class TCPresenter implements TCContract.Presenter {
    private ARFAContract.Presenter mARFAPresenter;
    private TCContract.View mView;
    private RecordTypeChooseRVAdapter.ViewHolder lastViewHolder = null;
    private TCContract.Model mModel;
    private boolean isNeedUpdate = false;
    private boolean mIsExpend;

    public TCPresenter(ARFAContract.Presenter presenter, TCContract.View view, boolean isExpend) {
        mARFAPresenter = presenter;
        mView = view;
        mIsExpend = isExpend;

        mView.setPresenter(this);
    }


    @Override
    public void setSelectItem(OptionalType optionalType, int position, RecordTypeChooseRVAdapter.ViewHolder curViewHolder, Activity activity) {
        // 第一次点击，上一次的viewholder为null
        if (this.lastViewHolder != null) {
            // 取消上一次checkbox的选择
            this.lastViewHolder.cbRecordType.setSelected(false);
        } else {
            mARFAPresenter.setShowFigureSoftKeyboard();
            mView.changRVSize(position, getRecyclerViewHeight(activity, true));
        }
        mView.selectDispose(position, true);
        this.lastViewHolder = curViewHolder;
        // 将选中的值 传给 根布局 碎片
        mARFAPresenter.setChooseType(optionalType);
    }

    @Override
    public void setSettingClick(Intent intent, Activity activity) {
        if (lastViewHolder != null) {
            lastViewHolder.cbRecordType.setSelected(false);
            lastViewHolder = null;
            // 设置设配器中的 上一次点击的item为空
            mView.setLastViewHolderNull();
        }
        if (mIsExpend) {
            intent.putExtra("type", Constants.EXPEND);
        } else {
            intent.putExtra("type", Constants.INCOME);
        }
        mView.startAct(intent);
        // 如果数字键盘显示，则关闭
        if (mARFAPresenter.getFigureSoftKeyBoardState()) {
            mARFAPresenter.setDismissFigureSoftKeyboard(0, activity);
        }
    }

    @Override
    public int getRecyclerViewHeight(Activity activity, boolean isShrink) {
        int height;
        int screenHeight = KeyBoardUtil.getScreenSize(activity).y;
        // 缩小高度
        if (isShrink) {
            height = screenHeight - screenHeight / 11 - screenHeight / 3 - 50 - mARFAPresenter.getTabLayoutBottom();
        }// 恢复原来大小
        else {
            height = screenHeight - mARFAPresenter.getTabLayoutBottom();
        }
        return height;
    }

    @Override
    public void recoverRV(Activity activity) {
        mView.changRVSize(0, getRecyclerViewHeight(activity, false));
        if (lastViewHolder != null) {
            lastViewHolder.cbRecordType.setSelected(false);
            lastViewHolder = null;
            mView.setLastViewHolderNull();
        }
    }

    @Override
    public void getRVData() {
        if (mIsExpend) {
            mView.initRVData(mModel.getExpendTypeListData());
        } else {
            mView.initRVData(mModel.getIncomeTypeListData());
        }
    }

    @Override
    public void setSaveTypeChooseSetting() {
        if (mIsExpend) {
            mModel.saveTypeSettingToDB(Constants.EXPEND);
        } else {
            mModel.saveTypeSettingToDB(Constants.INCOME);
        }
    }


    @Override
    public void setCheckType(RecordDetail recordDetail) {
        mView.checkType(recordDetail);
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
        mView.updateRecord();
        isNeedUpdate = false;
    }

    @Override
    public void start() {
        mModel = new TCModel();
    }

    @Override
    public void setInit() {
        mView.initWidget();
        mView.initListener();
        getRVData();
    }
}
