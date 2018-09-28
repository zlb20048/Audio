package com.kallaite.media;

import android.content.Intent;

import com.kallaite.media.service.MusicService;
import com.kallaite.media.util.WLog;
import com.kallaite.media.helper.MediaReceiverHelper;

import kallaite.com.crash.CrashApplication;
import kallaite.com.util.SystemHelper;

public class AppMain extends CrashApplication {

    private final static String LOG_TAG = "music";

    @Override
    public void onCreate() {
        super.onCreate();
        WLog.initialize(LOG_TAG, 0);
        WLog.i("AppMain onCreate...");
        MediaReceiverHelper.getInstance(this).registerReceiver();
        SystemHelper.Attach(getApplicationContext());
        startService(new Intent(getApplicationContext(), MusicService.class));
    }
}
