package com.example.feijibook.widget.my_dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.feijibook.R;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.util.SoundShakeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ChangePwDialog
 *
 * @author PengFei Yue
 * @date 2019/11/7
 * @description
 */
public class ChangePwDialog extends Dialog {
    @BindView(R.id.et_res_pw)
    EditText etResPw;
    @BindView(R.id.et_new_pw)
    EditText etNewPw;
    @BindView(R.id.et_again_pw)
    EditText etAgainPw;
    @BindView(R.id.tv_ok)
    TextView tvOk;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    public ChangePwDialog(@NonNull Context context) {
        super(context, R.style.SettingDialog);
        getWindow().setWindowAnimations(R.style.SettingDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_change_pw);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        etResPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkCanPressOk();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etNewPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkCanPressOk();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etAgainPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkCanPressOk();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void clearInput() {
        etResPw.getText().clear();
        etNewPw.getText().clear();
        etAgainPw.getText().clear();
    }

    /**
     * 检查是否能按确定
     */
    private void checkCanPressOk() {
        if (!"".equals(etResPw.getText().toString())
                && !"".equals(etNewPw.getText().toString())
                && !"".equals(etAgainPw.getText().toString())) {
            tvOk.setClickable(true);
            tvOk.setTextColor(ContextCompat.getColor(MyApplication.sContext, R.color.text_blue_color));
        } else {
            tvOk.setClickable(false);
            tvOk.setTextColor(ContextCompat.getColor(MyApplication.sContext, R.color.text_light_gray_color));
        }
    }

    private void checkNewPw() {
        if (!etNewPw.getText().toString().equals(etAgainPw.getText().toString())) {
            showTitle("两次输入的新密码不同！", Color.RED);
            etNewPw.getText().clear();
            etAgainPw.getText().clear();
        } else {
            mOnChangePwListener.onChangePw(etResPw.getText().toString(),
                    etNewPw.getText().toString());
        }
    }

    public void erroResPw() {
        clearInput();
        showTitle("原密码错误！", Color.RED);
    }

    private void showTitle(String content, int color) {
        tvTitle.setTextColor(color);
        tvTitle.setText(content);
    }

    @OnClick({R.id.tv_cancel, R.id.tv_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                SoundShakeUtil.playSound(SoundShakeUtil.SELECT_SWOOSH1_SOUND);
                dismiss();
                break;
            case R.id.tv_ok:
                SoundShakeUtil.playSound(SoundShakeUtil.SELECT_SWOOSH1_SOUND);
                checkNewPw();
                break;
            default:
        }
    }

    private OnChangePwListener mOnChangePwListener;

    public void setOnChangePwListener(OnChangePwListener onChangePwListener) {
        mOnChangePwListener = onChangePwListener;
    }

    public interface OnChangePwListener {
        void onChangePw(String resPw, String newPw);
    }
}
