package com.kallaite.media.engine;

import android.content.Context;

import com.kallaite.media.data.struct.Album;
import com.kallaite.media.data.struct.Artist;
import com.kallaite.media.service.ForwardingMusicController;

/**
 * Created by dengrui on 18-4-9.
 */

public class WalkmanOperatorFactory {

    //正在播放的播放列表类型
    public enum PlayListType {
        //在所有歌曲列表中播放
        ALL,
        //在某个艺术家的歌曲列表中播放
        ARTIST,
        //在某个专辑的歌曲列表中播放
        ALBUM;
    }

    public static IWalkmanOperator getWalkmanOperator(PlayListType type, Context context,
                                                      ForwardingMusicController controller,
                                                      IPlayingStateListener listener,
                                                      Artist artist, Album album) {
        if (type == PlayListType.ALL) {
            return new AllPlayListWalkmanOperator(context, controller, listener);
        } else if (type == PlayListType.ARTIST) {
            return new ArtistPlayListWalkmanOperator(context, controller, listener, artist);
        } else {
            return new AlbumPlayListWalkmanOperator(context, controller, listener, album);
        }
    }
}
