package com.kallaite.media.data.struct;

import android.net.Uri;

import java.io.Serializable;

import kallaite.com.provider.SmartAutoMediaStore;
import kallaite.com.provider.SmartAutoMediaStore.Audio;
import kallaite.com.provider.SmartAutoMediaStore.Audio.Albums;
import kallaite.com.provider.SmartAutoMediaStore.Audio.Artists;

public enum MediaStorage implements Serializable {
    USB {
        @Override
        public String getVolume() {
            return SmartAutoMediaStore.EXTERNAL_VOLUME;
        }

    }, INTERNAL {
        @Override
        public String getVolume() {
            return SmartAutoMediaStore.INTERNAL_VOLUME;
        }

    };

    /**
     * 获取volume
     *
     * @return
     */
    public abstract String getVolume();

    /**
     * 歌曲列表uri
     *
     * @return
     */
    public Uri getUri() {
        return Audio.Media.getContentUri(getVolume());
    }

    /**
     * 专辑列表uri
     *
     * @return
     */
    public Uri getAlbumUri() {
        return Albums.getContentUri(getVolume());
    }

    /**
     * 歌手列表uri
     *
     * @return
     */
    public Uri getArtistUri() {
        return Artists.getContentUri(getVolume());
    }


}
