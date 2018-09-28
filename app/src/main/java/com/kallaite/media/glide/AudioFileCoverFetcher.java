package com.kallaite.media.glide;

/**
 * Created by dengrui on 18-3-30.
 */

import android.media.MediaMetadataRetriever;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class AudioFileCoverFetcher implements DataFetcher<InputStream> {
    private final AudioFileCover model;

    public AudioFileCoverFetcher(AudioFileCover model) {
        this.model = model;
    }

    @Override
    public String getId() {
        // makes sure we never ever return null here
        return String.valueOf(model.filePath);
    }

    @Override
    public InputStream loadData(Priority priority) throws Exception {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(model.filePath);
            byte[] picture = retriever.getEmbeddedPicture();
            if (picture != null) {
                return new ByteArrayInputStream(picture);
            } else {
                return null;
            }
        } finally {
            retriever.release();
        }
    }
    @Override
    public void cleanup() {

    }

    @Override
    public void cancel() {
        // cannot cancel
    }
}