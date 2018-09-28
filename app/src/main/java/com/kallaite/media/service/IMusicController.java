package com.kallaite.media.service;

import com.kallaite.media.data.struct.MusicPlayMode;
import com.kallaite.media.data.struct.Album;
import com.kallaite.media.data.struct.Artist;
import com.kallaite.media.data.struct.MusicEntry;

/**
 * Created by dengrui on 18-3-23.
 */

public interface IMusicController {

    void requestUpdateCurrentPlaying();

    boolean isPlaying();

    void pause();

    void resume();

    void seekTo(int position);

    void previous();

    void next();

    void play(MusicEntry entry);

    MusicEntry getPlayingMusic();

    void switchPlayMode(MusicPlayMode mode);

}
