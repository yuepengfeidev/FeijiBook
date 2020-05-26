package com.example.feijibook.widget.my_dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.feijibook.R;
import com.example.feijibook.activity.main_act.find_frag.FindContract;
import com.example.feijibook.util.SoundShakeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * SettingChooseDialog
 *
 * @author yuepengfei
 * @date 2019/7/30
 * @description 账单编辑选项Dialog
 */
public class SettingChooseDialog extends Dialog {
    @BindView(R.id.tv_first_item)
    TextView tvFirstItem;
    @BindView(R.id.tv_second_item)
    TextView tvSecondItem;
    private String mType;
    public final static String SEX = "Sex";
    public final static String BILL = "Bill";
    public final static String PORTRAIT = "Portrait";

    public SettingChooseDialog(@NonNull Context context, String type) {
        super(context, R.style.SettingChooseDialog);
        mType= type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.dialog_setting_choose);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = displayMetrics.widthPixels;
        // 由于 <item name="android:windowIsFloating">true</item>，所以xml中设置match_parent无效，只能设置确定宽度
        getWindow().setAttributes(layoutParams);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        switch (mType) {
            case BILL:
                tvFirstItem.setText(R.string.bianjizongyusuan);
                tvSecondItem.setText(R.string.qingchuzongyusuan);
                break;
            case PORTRAIT:
                tvFirstItem.setText(R.string.paizhao);
                tvSecondItem.setText(R.string.congxiangcexuanze);
                break;
            case SEX:
                tvFirstItem.setText(R.string.nan);
                tvSecondItem.setText(R.string.nv);
                break;
            default:
        }
    }


    @OnClick({R.id.tv_first_item, R.id.tv_second_item, R.id.tv_cancel})
    public void onViewClicked(View view) {
        SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
        switch (view.getId()) {
            case R.id.tv_first_item:
                dismiss();
                mOnItemClickListener.onFirstItemClick();
                break;
            case R.id.tv_second_item:
                mOnItemClickListener.onSecondItemClick();
                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
            default:
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        /**
         * 点击第一个选择
         */
        void onFirstItemClick();

        /**
         * 点击第二个选择
         */
        void onSecondItemClick();
    }
}
