package com.kallaite.media.data.source;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kallaite.media.data.struct.MediaStorage;

/**
 * Created by dengrui on 18-3-23.
 */

public class InternalMediaDataSource extends LocalMediaDataSource{
    @Nullable
    private static InternalMediaDataSource INSTANCE;

    private InternalMediaDataSource(@NonNull Context context) {
        super(context);
    }

    @Override
    protected MediaStorage getMediaStorage() {
        return MediaStorage.INTERNAL;
    }

    public static InternalMediaDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new InternalMediaDataSource(context);
        }
        return INSTANCE;
    }
}
