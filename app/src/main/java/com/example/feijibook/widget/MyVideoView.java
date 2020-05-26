package com.example.feijibook.widget;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

/**
 * MyVideoView
 *
 * @author PengFei Yue
 * @date 2019/10/2
 * @description 使用了饺子播放器，修改了一下UI，添加了双击全屏
 */
public class MyVideoView extends JzvdStd {
    private long firstClickTime;

    public MyVideoView(Context context) {
        this(context, null);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 全屏状态为竖屏
        Jzvd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    @Override
    public void gotoScreenNormal() {
        super.gotoScreenNormal();
        if (screen == SCREEN_NORMAL) {
            bottomContainer.setVisibility(GONE);
        }
    }

    @Override
    public void onClickUiToggle() {
        super.onClickUiToggle();
        if (screen == SCREEN_NORMAL) {
            if (firstClickTime == 0) {
                firstClickTime = System.currentTimeMillis();
            } else {
                if (System.currentTimeMillis() - firstClickTime < 500) {
                    startButton.setVisibility(GONE);
                    // 双击
                    gotoScreenFullscreen();
                }
                firstClickTime = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
        if (screen == SCREEN_NORMAL) {
            bottomContainer.setVisibility(GONE);
        }
    }

    @Override
    public void changeUiToPauseShow() {
        super.changeUiToPauseShow();
        if (screen == SCREEN_NORMAL) {
            bottomContainer.setVisibility(GONE);
        }
    }

    @Override
    public void changeUiToComplete() {
        super.changeUiToComplete();
        if (state == STATE_AUTO_COMPLETE) {
            startButton.setVisibility(VISIBLE);
            startButton.setImageResource(cn.jzvd.R.drawable.jz_click_play_selector);
            replayTextView.setVisibility(GONE);
        }
    }

}
