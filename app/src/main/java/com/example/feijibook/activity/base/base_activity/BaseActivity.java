package com.example.feijibook.activity.base.base_activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.feijibook.R;
import com.example.feijibook.activity.base.todo_mvp_base_interface.BasePresenter;
import com.example.feijibook.activity.base.swipeback_interface.AnimUpdateListener;
import com.example.feijibook.activity.base.swipeback_interface.BindAdjacentLayer;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.util.GlobalHandler;
import com.example.feijibook.widget.SwipeBackLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseActivity
 *
 * @author yuepengfei
 * @date 2019/6/13
 * @description 自定义 右滑退出 的BaseActivity（退出和滑动当前层，下层活动会有同步动画，防IOS右滑退出）
 */
@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity implements BindAdjacentLayer,
        AnimUpdateListener, GlobalHandler.HandlerMsgListener {
    /**
     * 用于添加相邻层绑定接口，使用时是从最后添加的接口开始
     */
    public static List<BindAdjacentLayer> sBindAdjacentLayerList = new ArrayList<>();
    public SwipeBackLayout rootView;
    BasePresenter mPresenter;
    GlobalHandler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置任务栏颜色
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(MyApplication.sContext, R.color.sky_blue_like));
        }

        // 只有该层允许滑动时，才会 让该层 与 下层绑定（不能滑动，也说明底层下层没有与该层绑定过 或 没有下层）
        if (enableSliding()) {
            getBindAdjacentLayer().bindUpdate();
        }

        mHandler = GlobalHandler.getInstance();
        mHandler.setHandlerMsgListener(this);

    }

    /**
     * 打开新活动，同步动画
     *
     * @param frameLayout 新活动的主界面
     */
    public void anim(View frameLayout) {
        // 新界面从左侧进入动画
        Animation animation = AnimationUtils.loadAnimation(MyApplication.sContext, R.anim.anim_slide_in);
        animation.setFillAfter(true);
        frameLayout.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // 该层进入动画开始时，通过与下层的接口让下层动画同步更新
                BaseActivity.getBindAdjacentLayer().animToFront();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 当动画进入动画结束后，才会创建绑定该层的视图，
                // 否则在动画结束前绑定，会发生边进行动画时还能用手滑动该层
                mHandler.sendEmptyMessage(1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 初始化新活动的Fragment
     *
     * @param presenter 新活动的Presenter
     */
    public void init(BasePresenter presenter) {
        mPresenter = presenter;
        mHandler.sendEmptyMessage(0);
    }

    /**
     * 获得当前层 与 下层绑定的接口
     */
    public static BindAdjacentLayer getBindAdjacentLayer() {
        if (sBindAdjacentLayerList.size() != 0) {
            return sBindAdjacentLayerList.get(sBindAdjacentLayerList.size() - 1);
        }
        return null;
    }

    /**
     * 添加绑定相邻层的接口
     *
     * @param bindAdjacentLayer 绑定相邻层的接口
     */
    public static void addBindAdjacentLayer(BindAdjacentLayer bindAdjacentLayer) {
        sBindAdjacentLayerList.add(bindAdjacentLayer);
    }

    /**
     * 该层是否允许滑动，可进行重写控制
     */
    public boolean enableSliding() {
        return true;
    }

    @Override
    public void finish() {
        if (enableSliding()) {
            BindAdjacentLayer bindAdjacentLayer = getBindAdjacentLayer();
            // 先移除 该层的绑定相邻接口
            sBindAdjacentLayerList.remove(bindAdjacentLayer);
            // 同步动画时 重新获取最后添加的接口（即顶层绑定的接口）
            if (bindAdjacentLayer != null) {
                bindAdjacentLayer.exit();
            }
        }
        finishAct();
        overridePendingTransition(0, 0);
    }

    @Override
    public void moveAnim(int move) {
        rootView.move(move);
    }

    @Override
    public void finishAnim() {
        rootView.finish();
    }

    @Override
    public void initAnim() {
        rootView.init();
    }

    @Override
    public void exit() {
        rootView.finish();
        //关闭该层后， 重新绑定该层下层 与 其下层的接口，只有该层允许滑动，才会和下层绑定
        if (enableSliding()) {
            getBindAdjacentLayer().bindUpdate();
        }
    }

    @Override
    public void bindUpdate() {
        SwipeBackLayout.setUpdate(this);
    }

    @Override
    public void moveToFront() {
        rootView.moveToFront();
    }

    @Override
    public void animToFront() {
        final Animation animation = AnimationUtils.loadAnimation(MyApplication.sContext, R.anim.anim_slide_out_to_left);
        // 添加延迟，因为打开布局动画总是比下层布局动画慢
        rootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                rootView.startAnimation(animation);
            }
        }, 75);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 动画结束将界面Scroll到屏幕左侧外
                moveToFront();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void finishAct() {
        super.finish();
    }

    public void createRootView() {
        if (rootView == null) {
            rootView = new SwipeBackLayout(this);
            rootView.bindActivity(this, enableSliding());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacks(null);
        }
    }

    /**
     * 绑定MainActivity的界面和当前层的界面接口（用于当前层和下下层绑定，在当下层finish时）
     */
    public static void bingMainAct() {
        BindAdjacentLayer bindAdjacentLayer = getBindAdjacentLayer();
        bindAdjacentLayer.moveToFront();
        bindAdjacentLayer.bindUpdate();
    }

    /**
     * 移除最后一个绑定相邻层接口
     */
    public static void removeMainBind() {
        BindAdjacentLayer bindAdjacentLayer = getBindAdjacentLayer();
        sBindAdjacentLayerList.remove(bindAdjacentLayer);
    }

    /**
     * 移除下层的相邻层绑定接口同时关闭其活动
     */
    public static void removeBindAndFinish() {
        BindAdjacentLayer bindAdjacentLayer = getBindAdjacentLayer();
        sBindAdjacentLayerList.remove(bindAdjacentLayer);
        bindAdjacentLayer.finishAct();
    }



    @Override
    public void handleMsg(Message msg) {
        switch (msg.what) {
            case 0:
                mPresenter.setInit();
                break;
            case 1:
                createRootView();
                break;
            default:
        }
    }
}
