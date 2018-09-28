package com.kallaite.media.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.text.TextUtils;

import com.kallaite.media.util.WLog;
import com.kallaite.media.data.source.Injection;
import com.kallaite.media.data.source.MediaRepository;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import kallaite.com.provider.SmartAutoMediaStore;

/**
 * Created by dengrui on 18-3-28.
 */

public class MediaReceiverHelper {

    private Context mContext;

    private static MediaReceiverHelper sInstance;

    private boolean mNeedReloadData = false;

    private MediaRepository mediaRepository;

    private Set<String> mUnmountedSet = new HashSet<>();

    private MediaReceiverHelper(Context ctx) {
        this.mContext = ctx;
        mediaRepository = Injection.provideMediaRepository(ctx);
    }

    public static MediaReceiverHelper getInstance(Context ctx) {
        if (sInstance == null) {
            sInstance = new MediaReceiverHelper(ctx);
        }
        return sInstance;
    }

    public synchronized boolean unmounted(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }
        for (String unmount : mUnmountedSet) {
            if (path.startsWith(unmount)) {
                return true;
            }
        }
        return false;
    }

    public void registerReceiver() {
        IntentFilter f = new IntentFilter();
        f.addAction(SmartAutoMediaStore.ACTION_MEDIA_SCANNER_STARTED);
        f.addAction(SmartAutoMediaStore.ACTION_MEDIA_SCANNER_FINISHED);
        f.addAction(Intent.ACTION_MEDIA_EJECT);
        f.addAction(Intent.ACTION_MEDIA_MOUNTED);
        f.addAction(Intent.ACTION_MEDIA_CHECKING);
        f.addAction(Intent.ACTION_MEDIA_REMOVED);
        f.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        f.addDataScheme("file");
        f.addDataScheme("content");
        f.setPriority(Integer.MAX_VALUE);
        mContext.registerReceiver(mBroadcastReceiver, f);
        mContext.getContentResolver().registerContentObserver(
                SmartAutoMediaStore.Audio.Media.getContentUri(
                        SmartAutoMediaStore.EXTERNAL_VOLUME), true, mContentObserver);
    }
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            final String path = intent.getData().getPath();
            WLog.d(String.format("action=%s,path=%s", action, path));
            Observable.just(true).observeOn(Schedulers.single())
                    .subscribeOn(Schedulers.single()).subscribe(new Consumer<Object>() {
                @Override
                public void accept(Object o) throws Exception {
                    if (Intent.ACTION_MEDIA_EJECT.equals(action)
                            || Intent.ACTION_MEDIA_UNMOUNTED.equals(action)
                            || Intent.ACTION_MEDIA_REMOVED.equals(action)) {
                        mUnmountedSet.add(path);
                        mediaRepository.reloadMediaData();
                    }
                    if (SmartAutoMediaStore.ACTION_MEDIA_SCANNER_STARTED.equals(action)) {
                        mNeedReloadData = true;
                    }
                    if(Intent.ACTION_MEDIA_MOUNTED.equals(action)){
                        mUnmountedSet.remove(path);
                    }
                }
            });
        }
    };
    private ContentObserver mContentObserver = new ContentObserver(
            new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Observable.just(true).observeOn(Schedulers.single())
                    .subscribeOn(Schedulers.single()).subscribe(new Consumer<Object>() {
                @Override
                public void accept(Object o) throws Exception {
                    WLog.d("EXTERNAL_VOLUME update ");
                    if (mNeedReloadData) {
                        mediaRepository.reloadMediaData();
                        mNeedReloadData = false;
                    } else {
                        mediaRepository.requestMoreData();
                    }

                }
            });
        }

    };
}
