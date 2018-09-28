package com.kallaite.media.data.source;

import android.content.Context;
import android.support.annotation.NonNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dengrui on 18-3-23.
 */

public class Injection {

    public static MediaRepository provideMediaRepository(@NonNull Context context) {
        checkNotNull(context);
        return MediaRepository.getInstance(ExternalMediaDataSource.getInstance(context),
                InternalMediaDataSource.getInstance(context));
//                new EmptyMediaDataSource());
    }

}
