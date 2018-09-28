package com.kallaite.media.engine;

import com.kallaite.media.data.struct.MusicEntry;

/**
 * Created by dengrui on 18-3-26.
 */

public interface IPlayMachine {

    void play(MusicEntry entry);

    int getCurrentPosition();

    int getDuration();

    void resume();

    void pause();

    boolean isPlaying();

    void seekTo(int position);

    void stop();

}
