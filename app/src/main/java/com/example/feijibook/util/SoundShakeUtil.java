package com.example.feijibook.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.example.feijibook.R;
import com.example.feijibook.app.MyApplication;

/**
 * SoundShakeUtil
 *
 * @author PengFei Yue
 * @date 2019/11/2
 * @description 点击声音工具集
 */
public class SoundShakeUtil {
    /**
     * 默认打开按钮声音
     */
    private static boolean openSound = true;
    /**
     * 一般的点击声音
     */
    public static final int CLICK_SWOOSH1_SOUND = 0;
    /**
     * 点击添加记录、关闭添加记录界面 和 设置预算的点击声音
     */
    public static final int SELECT_SWOOSH1_SOUND = 1;
    /**
     * 选择记录类别 和 switch 的声音
     */
    public static final int TAP2_SOUND = 2;
    /**
     * 输入和滚动Picker的声音
     */
    public static final int CLICK_SOUND = 3;
    /**
     * 删除的声音
     */
    public static final int DELETE_SOUND = 4;
    /**
     * 完成添加记录的声音
     */
    public static final int DEEP_SWOOSH1_SOUND = 5;
    /**
     * 计算等于的声音
     */
    public static final int DEEP_SOUND = 6;
    /**
     * 拍照声
     */
    public static final int TAKE_PHOTO_SOUND = 7;
    public static final int SWOOSH1_SOUND = 8;
    private static SoundPool mSoundPool;
    /**
     * load的声音流Id
     */
    private static int click;
    private static int swoosh1;
    private static int select;
    private static int tap2;
    private static int delete;
    private static int deep;
    private static int takePhoto;
    /**
     * 当前音量
     */
    private static float curVolume;

    /**
     * 初始化声音资源
     */
    public static void initSoundRes() {
        if (mSoundPool == null) {
            SoundPool.Builder builder = new SoundPool.Builder();
            // 最大同时播放两个声音
            builder.setMaxStreams(2);
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适的属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_ALARM);
            builder.setAudioAttributes(attrBuilder.build());
            mSoundPool = builder.build();
            // 初始化加载声音资源
            click = mSoundPool.load(MyApplication.sContext,
                    R.raw.click, 1);
            swoosh1 = mSoundPool.load(MyApplication.sContext,
                    R.raw.swoosh1, 1);
            select = mSoundPool.load(MyApplication.sContext,
                    R.raw.select, 1);
            tap2 = mSoundPool.load(MyApplication.sContext,
                    R.raw.tap2, 1);
            deep = mSoundPool.load(MyApplication.sContext,
                    R.raw.deep, 1);
            delete = mSoundPool.load(MyApplication.sContext,
                    R.raw.delete, 1);
            takePhoto = mSoundPool.load(MyApplication.sContext,
                    R.raw.takephoto, 1);
        }
    }

    /**
     * 是否打开点击声音
     *
     * @param needOpen true打开
     */
    public static void OpenSound(boolean needOpen) {
        openSound = needOpen;
    }

    public static void playSound(int type) {
        if (!openSound) {
            return;
        }
        AudioManager am = (AudioManager) MyApplication.sContext.getSystemService(Context.AUDIO_SERVICE);
        //获取当前系统铃声音量（音量键默认控制的音量），SoundPlay声音范围为0-1,所以必须除以10
        curVolume = am.getStreamVolume(AudioManager.STREAM_RING) / (float) 10;
        switch (type) {
            case CLICK_SWOOSH1_SOUND:
                play(click);
                play(swoosh1);
                break;
            case SELECT_SWOOSH1_SOUND:
                play(select);
                play(swoosh1);
                break;
            case TAP2_SOUND:
                play(tap2);
                break;
            case CLICK_SOUND:
                play(click);
                break;
            case DELETE_SOUND:
                play(delete);
                break;
            case DEEP_SWOOSH1_SOUND:
                play(deep);
                play(swoosh1);
                break;
            case DEEP_SOUND:
                play(deep);
                break;
            case TAKE_PHOTO_SOUND:
                play(takePhoto);
            case SWOOSH1_SOUND:
                play(swoosh1);
                break;
            default:
        }
    }

    private static void play(int soundId) {
        if (soundId != 0) {
            mSoundPool.play(soundId, curVolume, curVolume, 1, 0, 1);
        }
    }

    /**
     * 震动手机
     */
    public static void shakePhone() {
        Vibrator vibrator = (Vibrator) MyApplication.sContext.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(250);
        }
    }
}
