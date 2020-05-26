package com.example.feijibook.entity;

import android.text.SpannableString;

/**
 * BillBean
 *
 * @author yuepengfei
 * @date 2019/7/24
 * @description 账单界面RecyclerView的Item的数据Bean
 */
public class BillBean {
    /**
     * 月份字符串
     */
    private SpannableString monthStr;
    /**
     * 月总收入
     */
    private SpannableString monthIncome;
    /**
     * 月总支出
     */
    private SpannableString monthExpend;
    /**
     * 月结余
     */
    private SpannableString monthSurplus;

    public BillBean(SpannableString monthStr, SpannableString monthIncome, SpannableString monthExpend, SpannableString monthSurplus) {
        this.monthStr = monthStr;
        this.monthIncome = monthIncome;
        this.monthExpend = monthExpend;
        this.monthSurplus = monthSurplus;
    }

    public SpannableString getMonthStr() {
        return monthStr;
    }

    public SpannableString getMonthIncome() {
        return monthIncome;
    }

    public SpannableString getMonthExpend() {
        return monthExpend;
    }

    public SpannableString getMonthSurplus() {
        return monthSurplus;
    }
}
