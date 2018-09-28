package com.kallaite.media.engine;

import com.kallaite.media.data.struct.MusicEntry;

/**
 * Created by dengrui on 18-3-26.
 */

public interface IPlayingStateListener {

    void notifyPlaying(MusicEntry entry);

    void notifyProgress(int position);

    void notifyResume();

    void notifyPause();

    void notifyStop();

}
