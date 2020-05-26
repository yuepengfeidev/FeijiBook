package com.example.feijibook.activity.add_record_act_from_add_icon_act;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.feijibook.R;
import com.example.feijibook.activity.base.base_activity.BaseActivity;
import com.example.feijibook.activity.record_detail_act.RecordDetailActivity;
import com.example.feijibook.adapter.ViewPagerAdapter;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.record_bean.RecordDetail;
import com.example.feijibook.util.KeyBoardUtil;
import com.example.feijibook.util.NoticeUpdateUtils;
import com.example.feijibook.util.SoundShakeUtil;
import com.example.feijibook.widget.my_dialog.KeyBoardDialog;
import com.example.feijibook.widget.MyToast;
import com.example.feijibook.widget.my_datepicker.MyDataPicker;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.feijibook.util.KeyBoardUtil.getSoftButtonsBarHeight;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddRecordFromAddIconFragment extends Fragment implements ARFAContract.View,
        View.OnClickListener {
    ARFAContract.Presenter mPresenter;
    @BindView(R.id.tl_expane_income)
    TabLayout tlExpandIncome;
    @BindView(R.id.vp_detail_type_choose)
    ViewPager vpDetailTypeChoose;
    @BindView(R.id.tv_cancel_finish)
    TextView tvCancelFinish;
    @BindView(R.id.arfa_main_context)
    RelativeLayout mRelativeLayout;
    Unbinder unbinder;
    private String[] mTitles = {"支出", "收入"};
    PopupWindow keyboardPopupWindow; // 数字软键盘的PopupWindow的弹窗
    boolean keyboardShow = false;
    int width;
    int height;
    WindowManager.LayoutParams params;
    Window window;
    KeyBoardDialog mDialog;// 软键盘输入框为dialog
    Activity mActivity;
    ViewHolder mViewHolder;
    private static UpdateKeyBoardDialog mUpdateKeyBoardDialog;
    private MyDataPicker ymdDatePicker;
    KeyBoardUtil.SoftKeyBoardListener softKeyBoardListener;
    private String mId;

    public AddRecordFromAddIconFragment() {
        // Required empty public constructor
    }

    public static AddRecordFromAddIconFragment getInstance() {
        return new AddRecordFromAddIconFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_record_from_add_icon, container, false);
        unbinder = ButterKnife.bind(this, view);
        NoticeUpdateUtils.updateTypesPresenters.add(mPresenter);
        return view;
    }

    @Override
    public void setPresenter(ARFAContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void finishAct() {
        mActivity.finish();
    }

    @Override
    public void initListener() {

        softKeyBoardListener = new KeyBoardUtil.SoftKeyBoardListener(mActivity);
        initKeyBoardListener();

        tvCancelFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundShakeUtil.playSound(SoundShakeUtil.SELECT_SWOOSH1_SOUND);
                mPresenter.setFinishAct();
            }
        });

        vpDetailTypeChoose.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setDismissFigureSoftKeyboard(i, getActivity());
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        mDialog.setDialogListener(new KeyBoardDialog.DialogListener() {
            @Override
            public void buttonText(boolean isEqual) {
                if (isEqual) {
                    mViewHolder.tvEqual.setText("=");
                } else {
                    mViewHolder.tvEqual.setText("完成");
                }
            }

            @Override
            public void onIconClick() {
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                if (mId != null) {
                    // 表示是修改已保存的记录，保存修改记录,退出当前界面
                    Map<String, String> map = KeyBoardDialog.getInputContent();
                    map.put("date", mViewHolder.tvDate.getText().toString());
                    mPresenter.setSave(map, true);
                    mPresenter.setFinishAct();
                } else {
                    if (KeyBoardDialog.isMoneyResult()) {
                        MyToast myToast = new MyToast(mActivity);
                        myToast.showToast("请输入金额!");
                    } else {
                        Map<String, String> map = KeyBoardDialog.getInputContent();
                        map.put("date", mViewHolder.tvDate.getText().toString());
                        mPresenter.setSave(map, false);
                        BaseActivity.addBindAdjacentLayer((AddRecordFromAddIconActivity) mActivity);
                        mPresenter.setStartAct(new Intent(MyApplication.sContext, RecordDetailActivity.class));
                    }
                }
            }
        });

    }

    @Override
    public void initWidget(List<Fragment> fragments) {
        initTabLayout();
        initViewPager(fragments);
        // 关联tabLayout和viewpager
        tlExpandIncome.setupWithViewPager(vpDetailTypeChoose);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPresenter.setAsnyInit();
            }
        }, 100);
    }

    /**
     * 初始化viewpager控件
     */
    private void initViewPager(List<Fragment> fragments) {

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addTitlesAndFragments(mTitles, fragments);
        vpDetailTypeChoose.setAdapter(viewPagerAdapter);

    }

    /**
     * 初始化TabLayout控件
     */
    private void initTabLayout() {
        for (String mTitle : mTitles) {
            tlExpandIncome.addTab(tlExpandIncome.newTab().setText(mTitle));
        }
    }

    @Override
    public void initFigureSoftKeyBoard() {
        // 获取屏幕大小
        Point point = KeyBoardUtil.getScreenSize(mActivity);

        width = point.x;
        height = point.y;

        View keyboardView = LayoutInflater.from(mActivity).inflate(R.layout.popupwindow_keyboard, mRelativeLayout, false);
        mViewHolder = new ViewHolder(keyboardView);
        mViewHolder.tvNb0.setOnClickListener(this);
        mViewHolder.tvNb1.setOnClickListener(this);
        mViewHolder.tvNb2.setOnClickListener(this);
        mViewHolder.tvNb3.setOnClickListener(this);
        mViewHolder.tvNb4.setOnClickListener(this);
        mViewHolder.tvNb5.setOnClickListener(this);
        mViewHolder.tvNb6.setOnClickListener(this);
        mViewHolder.tvNb7.setOnClickListener(this);
        mViewHolder.tvNb8.setOnClickListener(this);
        mViewHolder.tvNb9.setOnClickListener(this);
        mViewHolder.tvPoint.setOnClickListener(this);
        mViewHolder.tvAdd.setOnClickListener(this);
        mViewHolder.tvSub.setOnClickListener(this);
        mViewHolder.tvEqual.setOnClickListener(this);
        mViewHolder.layoutDate.setOnClickListener(this);
        mViewHolder.layoutDel.setOnClickListener(this);

        keyboardPopupWindow = new PopupWindow(keyboardView, width, height / 3 + 5, true);
        keyboardPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        keyboardPopupWindow.setTouchable(true);
        keyboardPopupWindow.setOutsideTouchable(false);
        keyboardPopupWindow.setFocusable(false);
        keyboardPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        keyboardPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mDialog = new KeyBoardDialog(mActivity);
        window = mDialog.getWindow();
        assert window != null;
        params = window.getAttributes();
        params.x = 0;
        params.y = (int) (height * 0.108);
        params.width = width;
        params.height = height / 11;
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        window.setAttributes(params);

        // 如果 从Calendar界面打开，且选择了日期，则初始化选择的日期，否则默认显示今天
        mPresenter.setInitChooseDate();
    }

    @Override
    public void initYMDDatePicker(long beginTime, long endTime) {
        ymdDatePicker = new MyDataPicker(getActivity(), new MyDataPicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setShowSelectDate(timestamp);
            }
        }, beginTime, endTime);
        ymdDatePicker.setCancelable(true);
        ymdDatePicker.setCanShowDay(true);
        ymdDatePicker.setScrollLoop(false);
        ymdDatePicker.setCanShowAnim(false);
    }

    @Override
    public void showYMDDatePicker() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        if ("今天".equals(mViewHolder.tvDate.getText().toString())) {
            ymdDatePicker.show(format.format(System.currentTimeMillis()));
        } else {
            String[] time = mViewHolder.tvDate.getText().toString().split("/");
            String selectedTime = time[0] + "-" + time[1] + "-" + time[2];
            ymdDatePicker.show(selectedTime);
        }
    }

    @Override
    public void showFigureSoftKeyboard() {
        keyboardPopupWindow.showAtLocation(mRelativeLayout.getRootView(), Gravity.NO_GRAVITY,
                0, height - getSoftButtonsBarHeight(mActivity));
        mDialog.show();
    }

    @Override
    public void dismissFigureSoftKeyboard() {
        if (keyboardPopupWindow.isShowing() && mDialog.isShowing()) {
            mUpdateKeyBoardDialog.clearContent();
            mDialog.dismiss();
            keyboardPopupWindow.dismiss();

            KeyBoardUtil.hideInput(mActivity, mRelativeLayout);
        }
    }

    @Override
    public void getAct(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public void getId(String id) {
        mId = id;
    }

    @Override
    public void upInput() {
        keyboardShow = true;

        params.x = 0;
        params.y = 85;
        params.width = width;
        params.height = height / 9 + 10;
        window.setAttributes(params);

    }

    @Override
    public void downInput() {
        keyboardShow = false;

        params.x = 0;
        params.y = 205;
        params.width = width;
        params.height = height / 11;
        window.setAttributes(params);

    }

    @Override
    public int tabLayoutBottom() {
        return tlExpandIncome.getBottom();
    }

    @Override
    public void showSelectDate(String selectedDate) {
        if ("今天".equals(selectedDate)) {
            mViewHolder.ivDate.setVisibility(View.VISIBLE);
            mViewHolder.tvDate.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        } else {
            mViewHolder.ivDate.setVisibility(View.GONE);
            mViewHolder.tvDate.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        mViewHolder.tvDate.setText(selectedDate);
    }

    @Override
    public boolean figureSoftKeyBoardState() {
        return mDialog.isShowing() || keyboardPopupWindow.isShowing();
    }

    @Override
    public void startAct(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void selectPage(int position) {
        vpDetailTypeChoose.setCurrentItem(position);
    }

    @Override
    public void showMoneyAndRemark(RecordDetail recordDetail) {
        mDialog.showMoneyAndRemark(recordDetail);
    }

    @Override
    public void onClick(View v) {
        String content;
        switch (v.getId()) {
            case R.id.tv_nb0:
                content = "0";
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SOUND);
                mUpdateKeyBoardDialog.inputContent(content);
                break;
            case R.id.tv_nb1:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SOUND);
                content = "1";
                mUpdateKeyBoardDialog.inputContent(content);
                break;
            case R.id.tv_nb2:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SOUND);
                content = "2";
                mUpdateKeyBoardDialog.inputContent(content);
                break;
            case R.id.tv_nb3:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SOUND);
                content = "3";
                mUpdateKeyBoardDialog.inputContent(content);
                break;
            case R.id.tv_nb4:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SOUND);
                content = "4";
                mUpdateKeyBoardDialog.inputContent(content);
                break;
            case R.id.tv_nb5:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SOUND);
                content = "5";
                mUpdateKeyBoardDialog.inputContent(content);
                break;
            case R.id.tv_nb6:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SOUND);
                content = "6";
                mUpdateKeyBoardDialog.inputContent(content);
                break;
            case R.id.tv_nb7:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SOUND);
                content = "7";
                mUpdateKeyBoardDialog.inputContent(content);
                break;
            case R.id.tv_nb8:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SOUND);
                content = "8";
                mUpdateKeyBoardDialog.inputContent(content);
                break;
            case R.id.tv_nb9:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SOUND);
                content = "9";
                mUpdateKeyBoardDialog.inputContent(content);
                break;
            case R.id.layout_date:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setShowDatePicker();
                break;
            case R.id.layout_del:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SOUND);
                mUpdateKeyBoardDialog.deleteContent();
                break;
            case R.id.tv_point:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SOUND);
                content = ".";
                mUpdateKeyBoardDialog.inputContent(content);
                break;
            case R.id.tv_equal:
                if ("完成".equals(mViewHolder.tvEqual.getText().toString())) {
                    if (KeyBoardDialog.isMoneyResult()) {
                        SoundShakeUtil.playSound(SoundShakeUtil.DEEP_SOUND);
                        MyToast myToast = new MyToast(mActivity);
                        myToast.showToast("请输入金额!");
                    } else {
                        SoundShakeUtil.playSound(SoundShakeUtil.DEEP_SWOOSH1_SOUND);
                        Map<String, String> map = KeyBoardDialog.getInputContent();
                        map.put("date", mViewHolder.tvDate.getText().toString());
                        mPresenter.setSave(map, true);
                    }
                } else {
                    SoundShakeUtil.playSound(SoundShakeUtil.DEEP_SOUND);
                    mUpdateKeyBoardDialog.inputContent("=");
                }
                break;
            case R.id.tv_sub:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SOUND);
                content = "-";
                mUpdateKeyBoardDialog.inputContent(content);
                break;
            case R.id.tv_add:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SOUND);
                content = "+";
                mUpdateKeyBoardDialog.inputContent(content);
                break;
            default:
        }

    }

    static
    class ViewHolder {
        @BindView(R.id.tv_nb1)
        TextView tvNb1;
        @BindView(R.id.tv_nb2)
        TextView tvNb2;
        @BindView(R.id.tv_nb3)
        TextView tvNb3;
        @BindView(R.id.iv_date)
        ImageView ivDate;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.layout_date)
        LinearLayout layoutDate;
        @BindView(R.id.tv_nb4)
        TextView tvNb4;
        @BindView(R.id.tv_nb5)
        TextView tvNb5;
        @BindView(R.id.tv_nb6)
        TextView tvNb6;
        @BindView(R.id.tv_sub)
        TextView tvSub;
        @BindView(R.id.tv_nb7)
        TextView tvNb7;
        @BindView(R.id.tv_nb8)
        TextView tvNb8;
        @BindView(R.id.tv_nb9)
        TextView tvNb9;
        @BindView(R.id.tv_add)
        TextView tvAdd;
        @BindView(R.id.tv_point)
        TextView tvPoint;
        @BindView(R.id.tv_nb0)
        TextView tvNb0;
        @BindView(R.id.layout_del)
        LinearLayout layoutDel;
        @BindView(R.id.tv_equal)
        TextView tvEqual;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public static void setmUpdateKeyBoardDialog(UpdateKeyBoardDialog updateKeyBoardDialog) {
        mUpdateKeyBoardDialog = updateKeyBoardDialog;
    }

    public interface UpdateKeyBoardDialog {
        void inputContent(String content);

        void softKeyBoardState(boolean isShow);

        void deleteContent();

        void clearContent();
    }

    @Override
    public void onDestroy() {
        if (ymdDatePicker != null) {
            ymdDatePicker.onDestroy();
        }
        if (mDialog.isShowing() && keyboardPopupWindow.isShowing()) {
            mDialog.dismiss();
            keyboardPopupWindow.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        softKeyBoardListener.setListener(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.checkUpdateRecord();
        if (softKeyBoardListener != null) {
            initKeyBoardListener();
        }
    }

    private void initKeyBoardListener() {
        softKeyBoardListener.setListener(new KeyBoardUtil.SoftKeyBoardListener.OnSoftBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                mPresenter.setUpInput();
                mUpdateKeyBoardDialog.softKeyBoardState(true);
            }

            @Override
            public void keyboardHide(int height) {
                mPresenter.setDownInput();
                mUpdateKeyBoardDialog.softKeyBoardState(false);
            }
        });
    }

}
