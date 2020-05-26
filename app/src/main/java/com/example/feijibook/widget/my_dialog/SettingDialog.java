package com.example.feijibook.widget.my_dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.feijibook.R;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.util.SoundShakeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * SettingDialog
 *
 * @author yuepengfei
 * @date 2019/7/27
 * @description 账单设置的Dialog
 */
public class SettingDialog extends Dialog {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_res_pw)
    EditText etSettingInput;
    @BindView(R.id.tv_ok)
    TextView tvOk;
    private String mType;
    public final static String BILL = "Bill";
    public final static String NICKNAME = "NickName";

    public SettingDialog(@NonNull Context context, String type) {
        super(context, R.style.SettingDialog);
        getWindow().setWindowAnimations(R.style.SettingDialog);
        mType = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_setting);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        if (mType.equals(BILL)) {
            tvTitle.setText("每月总预算");
            etSettingInput.setHint(R.string.qingshurujinge);
            etSettingInput.setEms(10);
            etSettingInput.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else if (mType.equals(NICKNAME)) {
            tvTitle.setText("昵称");
            etSettingInput.setHint(R.string.qingshurunicheng);
            // 昵称最长位12个字符
            etSettingInput.setEms(12);
            etSettingInput.setInputType(InputType.TYPE_CLASS_TEXT);
        }

        // 初始化无法点击“确定”按钮
        tvOk.setClickable(false);

        etSettingInput.setFocusable(true);
        etSettingInput.setFocusableInTouchMode(true);
        etSettingInput.requestFocus();
        waitPop();
        etSettingInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mType.equals(BILL)) {
                    if ("".equals(s.toString())) {
                        tvOk.setClickable(false);
                        tvOk.setTextColor(ContextCompat.getColor(MyApplication.sContext, R.color.text_light_gray_color));
                    } else {
                        tvOk.setClickable(true);
                        tvOk.setTextColor(ContextCompat.getColor(MyApplication.sContext, R.color.text_blue_color));
                    }

                    //删除“.”后面超过2位后的数据
                    if (s.toString().contains(".")) {
                        if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                            s = s.toString().subSequence(0,
                                    s.toString().indexOf(".") + 2 + 1);
                            etSettingInput.setText(s);
                            //光标移到最后
                            etSettingInput.setSelection(s.length());
                        }
                    }
                    //如果"."在起始位置,则起始位置自动补0
                    if (".".equals(s.toString().trim())) {
                        s = "0" + s;
                        etSettingInput.setText(s);
                        etSettingInput.setSelection(2);
                    }

                    //如果起始位置为0,且第二位跟的不是".",则替换第一位为当前输入数字
                    if (s.toString().startsWith("0")
                            && s.toString().trim().length() > 1) {
                        String secondStr = s.toString().substring(1, 2);
                        if (!".".equals(secondStr)) {
                            etSettingInput.setText(secondStr);
                            etSettingInput.setSelection(1);
                        }
                    }

                    // 限制整数部分为9位
                    if (!s.toString().contains(".") && s.length() > 9) {
                        etSettingInput.setText(s.toString().substring(0, 9));
                        etSettingInput.setSelection(9);
                    }
                } else if (mType.equals(NICKNAME)) {
                    if ("".equals(s.toString())) {
                        tvOk.setClickable(false);
                        tvOk.setTextColor(ContextCompat.getColor(MyApplication.sContext, R.color.text_light_gray_color));
                    } else if (s.length() > 2 && s.length() < 13) {
                        // 只允许取2-12位的昵称
                        tvOk.setClickable(true);
                        tvOk.setTextColor(ContextCompat.getColor(MyApplication.sContext, R.color.text_blue_color));
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 等待弹出键盘
     */
    private void waitPop() {
        new Handler().postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) etSettingInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(etSettingInput, 0);
        }, 40);
    }


    @OnClick({R.id.tv_cancel, R.id.tv_ok})
    public void onViewClicked(View view) {
        SoundShakeUtil.playSound(SoundShakeUtil.SELECT_SWOOSH1_SOUND);
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_ok:
                mOnOkListener.onOk(etSettingInput.getText().toString());
                dismiss();
                break;
            default:
        }
    }

    private OnOkListener mOnOkListener;

    public void setOnOkListener(OnOkListener onOkListener) {
        mOnOkListener = onOkListener;
    }

    public interface OnOkListener {
        /**
         * 点击确定，回调输入框内容
         */
        void onOk(String content);
    }
}
