package com.example.feijibook.util;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by 你是我的 on 2019/3/20
 */

// 封装的Handler，可供全局使用
public class GlobalHandler extends Handler {
    private HandlerMsgListener mHandlerMsgListener;
    private String Tag = GlobalHandler.class.getSimpleName();

    private GlobalHandler() {
    }

    private static class Holder {
        private static final GlobalHandler HANDLER = new GlobalHandler();
    }

    public static GlobalHandler getInstance() {
        return Holder.HANDLER;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (getHandlerMsgListener() != null) {
            mHandlerMsgListener.handleMsg(msg);
        }else {
            Log.d(Tag, "请传入HandlerMsgListener对象");
        }
    }

    private HandlerMsgListener getHandlerMsgListener() {
        return mHandlerMsgListener;
    }

    public void setHandlerMsgListener(HandlerMsgListener handlerMsgListener) {
        mHandlerMsgListener = handlerMsgListener;
    }

    public interface HandlerMsgListener {
        void handleMsg(Message msg);
    }
}
