package com.kallaite.media.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import com.kallaite.media.util.WLog;

/**
 * Created by dengrui on 18-3-22.
 */

public class MusicService extends Service {

    private MusicServiceImpl musicService;

    private volatile Looper mServiceLooper;

    private volatile ServiceHandler mServiceHandler;

    private static final int INITIALIZE_MSG = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == INITIALIZE_MSG) {
                musicService.initialize();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        WLog.i("onCreate method was called.");
        musicService = MusicServiceImpl.getInstance(this);
        HandlerThread thread = new HandlerThread("MusicService");
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        initialize();
    }

    private void initialize() {
        Message msg = mServiceHandler.obtainMessage();
        msg.what = INITIALIZE_MSG;
        mServiceHandler.sendMessage(msg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mServiceLooper.quit();
    }
}
