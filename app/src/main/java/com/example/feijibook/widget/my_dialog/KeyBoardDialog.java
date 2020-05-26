package com.example.feijibook.widget.my_dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.feijibook.R;
import com.example.feijibook.activity.add_record_act_from_add_icon_act.AddRecordFromAddIconFragment;
import com.example.feijibook.entity.record_bean.RecordDetail;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 你是我的 on 2019/3/23
 */

// 这个简易计算器写的还是那么烂，自己都不想看
@SuppressWarnings("AlibabaSwitchStatement")
public class KeyBoardDialog extends Dialog implements AddRecordFromAddIconFragment.UpdateKeyBoardDialog,
        View.OnClickListener {
    private static ViewHolder mViewHolder;
    private DialogListener mDialogListener;
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");//无限小数限制在小数点后两位

    public KeyBoardDialog(@NonNull Context context) {
        super(context, R.style.KeyBoardDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        setContentView(R.layout.dialog_layout);
        mViewHolder = new ViewHolder(getWindow().getDecorView());
        init();
    }

    private void init() {

        mViewHolder.tvResult.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = String.valueOf(s);

                String[] strings = new String[]{};
                if (content.contains("+")) {
                    strings = content.split("\\+");
                } else if (content.contains("-")) {
                    strings = content.split("-");
                }

                //长度为2表示加减符号后面有数字，可以显示 = 符号
                if (strings.length == 2) {
                    mDialogListener.buttonText(true);
                } else {
                    mDialogListener.buttonText(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mViewHolder.editText.setOnClickListener(this);
        mViewHolder.ivRemarkDetail.setOnClickListener(this);
        AddRecordFromAddIconFragment.setmUpdateKeyBoardDialog(this);
    }

    @Override
    public void inputContent(String content) {
        String[] data;
        String string = mViewHolder.tvResult.getText().toString();

        if ("0.00".equals(string) || "0".equals(string)) {
            string = "";
        }

        if (!"+".equals(content) && !"-".equals(content)
                && !"=".equals(content)) {
            if (string.contains("+")) {
                data = string.split("\\+");
                if (data.length == 2) {
                    if (data[1].length() == 8) {
                        return;
                    }
                }
            } else if (string.contains("-")) {
                data = string.split("-");
                if (data.length == 2) {
                    if (data[1].length() == 8) {
                        return;
                    }
                }
            } else {
                if (string.length() > 8) {
                    return;
                } else if (string.length() == 8) {
                    if (!"+".equals(content) && !"-".equals(content)) {
                        return;
                    }
                }
            }
        }

        switch (content) {
            case "+":
            case "-":
                if ((!string.contains("+") && !string.contains("-"))) {// 前面没有加减符号
                    string = string + content;
                } else if (!"+".equals(string.substring(string.length() - 1))
                        && !"-".equals(string.substring(string.length() - 1))) {// 如果有加减，且不在该字符前，就计算结果
                    string = compute(string) + content;
                } else if (string.charAt(0) == '-') {// 为负数,加上符号
                    string = string + content;
                } else {
                    string = string.substring(0, string.length() - 1) + content;
                }
                break;
            case ".":
                if (string.contains("+")) {// 有加号
                    data = string.split("\\+");
                    if (data.length == 2) {// 加数有"."则不添加”."
                        if (data[1].contains(".")) {
                            return;
                        }
                    }
                } else if (string.contains("-")) {// 有减号
                    data = string.split("-");
                    if (data.length == 2) {// 减数有"."则不添加”."
                        if (data[1].contains(".")) {
                            return;
                        }
                    }
                } else if (string.contains(".")) {// 没有加减号,被加数有"."则不添加“.”
                    return;
                }
                string = string + content;

                break;
            case "=":
                string = compute(string);
                break;
            default:
                String[] data1;
                if (string.contains("+")) {
                    data = string.split("\\+");
                    if (data.length == 2) {
                        if (data[1].contains(".")) {
                            data1 = data[1].split("\\.");// 被加数有两位小数
                            if (data1.length == 2) {
                                if (data1[1].length() == 2) {
                                    return;
                                }
                            }
                        }
                    }
                } else if (string.contains("-")) {
                    data = string.split("-");
                    if (data.length == 2) {
                        if (data[1].contains(".")) {
                            data1 = data[1].split("\\.");// 被减数有两位小数
                            if (data1.length == 2) {
                                if (data1[1].length() == 2) {
                                    return;
                                }
                            }
                        }
                    }
                } else if (string.contains(".")) {
                    data = string.split("\\.");
                    if (data.length == 2) {// 没有加减号，加数有两位小数
                        if (data[1].length() == 2) {
                            return;
                        }
                    }
                }
                string = string + content;

                break;
        }
        mViewHolder.tvResult.setText(string);
    }

    @Override
    public void softKeyBoardState(boolean isShow) {
        if (isShow) {
            mViewHolder.editText.setCursorVisible(true);
        } else {
            mViewHolder.editText.setCursorVisible(false);
        }
    }

    @Override
    public void deleteContent() {
        String content = mViewHolder.tvResult.getText().toString();
        if (content.length() > 1) {
            content = content.substring(0, content.length() - 1);
            mViewHolder.tvResult.setText(content);
        } else {
            mViewHolder.tvResult.setText("0");
        }
    }

    @Override
    public void clearContent() {
        String zero = "0.00";
        mViewHolder.tvResult.setText(zero);
    }

    @Override
    public void onClick(View v) {
        //noinspection AlibabaSwitchStatement
        switch (v.getId()) {
            case R.id.edit_text:
                break;
            case R.id.iv_remark_detail:
                mDialogListener.onIconClick();
                break;
            default:
        }
    }

    public static Map<String, String> getInputContent() {
        Map<String, String> map = new HashMap<>();
        map.put("remark", mViewHolder.editText.getText().toString());
        String result = mViewHolder.tvResult.getText().toString();
        // 如果输入金额中有符号，则去除符号
        if (result.contains("+")) {
            result = result.replace("+", "");
        }
        if (result.contains("-")) {
            result = result.replace("-", "");
        }
        map.put("money", result);
        return map;
    }

    static
    class ViewHolder {
        @BindView(R.id.iv_remark_detail)
        ImageView ivRemarkDetail;
        @BindView(R.id.edit_text)
        EditText editText;
        @BindView(R.id.tv_result)
        TextView tvResult;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private String compute(String content) {
        String result = null;
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine engine = scriptEngineManager.getEngineByName("javascript");
        try {
            String[] data;
            result = String.valueOf(engine.eval(content));
            if (result.contains(".")) {
                data = result.split("\\.");
                if (data[1].length() > 2) {// 小数大于两位
                    result = decimalFormat.format(Double.valueOf(result));
                    String[] data1 = result.split("\\.");
                    if (data1[1].charAt(0) == '0' && data1[1].charAt(1) == '0') {
                        result = data1[0];
                    }
                } else if (data[1].charAt(0) == '0') {// 小数后第一位为 0
                    result = data[0];
                }
            }
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void showMoneyAndRemark(RecordDetail recordDetail) {
        mViewHolder.editText.setText(recordDetail.getRemark());
        mViewHolder.tvResult.setText(recordDetail.getMoney());
    }

    public static boolean isMoneyResult() {
        return mViewHolder.tvResult.getText().toString().equals("0")
                || mViewHolder.tvResult.getText().toString().equals("0.00")
                || mViewHolder.tvResult.getText().toString().equals("");
    }

    public void setDialogListener(DialogListener dialogListener) {
        mDialogListener = dialogListener;
    }

    public interface DialogListener {
        /**
         * 更新等于按钮的文字，
         *
         * @param isEqual 是否显示 “=“符号
         */
        void buttonText(boolean isEqual);

        /**
         * 详情编辑icon点击监听
         */
        void onIconClick();
    }
}
