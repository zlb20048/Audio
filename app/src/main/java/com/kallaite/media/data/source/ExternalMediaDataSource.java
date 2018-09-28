package com.kallaite.media.data.source;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kallaite.media.data.struct.MediaStorage;

/**
 * Created by dengrui on 18-3-23.
 */

public class ExternalMediaDataSource extends LocalMediaDataSource{
    @Nullable
    private static ExternalMediaDataSource INSTANCE;

    private ExternalMediaDataSource(@NonNull Context context) {
        super(context);
    }

    @Override
    protected MediaStorage getMediaStorage() {
        return MediaStorage.USB;
    }

    public static ExternalMediaDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ExternalMediaDataSource(context);
        }
        return INSTANCE;
    }
}
