package com.kallaite.media.data.struct;

import android.text.TextUtils;

import com.kallaite.media.util.WLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dengrui on 18-3-26.
 */

public class MusicPlayList {

    private List<MusicEntry> musicEntries = new ArrayList<>();

    private int mPlayingIndex = -1;

    private MusicPlayMode musicPlayMode = MusicPlayMode.LOOP_NORMAL;

    public static final String TAG = "MusicPlayList";

    private static final int ERROR_COUNT_LIMIT = 3;

    private MusicPlayList(List<MusicEntry> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("MusicPlayList cannot be null or empty");
        }
        musicEntries = Collections.unmodifiableList(list);
    }

    public static MusicPlayList buildMusicPlayList(List<MusicEntry> list) {
        return new MusicPlayList(list);
    }

    public MusicEntry previous() {
        if (musicPlayMode == MusicPlayMode.LOOP_NORMAL) {
            handleLoopNormalPrevious();
        } else if (musicPlayMode == MusicPlayMode.SHUFFLE) {
            handleShuffle();
        }
        return musicEntries.get(mPlayingIndex);
    }

    public MusicEntry next() {
        if (musicPlayMode == MusicPlayMode.LOOP_NORMAL) {
            handleLoopNormalNext();
        } else if (musicPlayMode == MusicPlayMode.SHUFFLE) {
            handleShuffle();
        }
        return musicEntries.get(mPlayingIndex);
    }

    private void handleLoopNormalPrevious() {
        mPlayingIndex--;
        if (mPlayingIndex < 0)
            mPlayingIndex = musicEntries.size() - 1;
    }

    private void handleLoopNormalNext() {
        mPlayingIndex++;
        mPlayingIndex %= musicEntries.size();
    }

    private void handleShuffle() {
        mPlayingIndex = (int) (Math.random() * musicEntries.size());
    }

    public MusicEntry jump(int index) {
        if (index < 0 || index >= musicEntries.size()) {
            WLog.d("oops! jump error,index:" + index + ",size:" + musicEntries.size());
            return null;
        }
        mPlayingIndex = index;
        return musicEntries.get(mPlayingIndex);
    }

    public MusicEntry jump(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        WLog.v(TAG, "play list size is : " + musicEntries.size());
        for (int i = 0; i < musicEntries.size(); i++) {
            if (path.equals(musicEntries.get(i).getUri())) {
                mPlayingIndex = i;
                return musicEntries.get(i);
            }
        }
        return null;
    }

    public MusicEntry getPlayingMusicEntry() {
        if (mPlayingIndex < 0 || mPlayingIndex >= musicEntries.size()) {
            return null;
        }
        return musicEntries.get(mPlayingIndex);
    }

    public void changeMode(MusicPlayMode mode) {
        this.musicPlayMode = mode;
    }


    public boolean wholeListCannotPlay() {
        for (MusicEntry entry : musicEntries) {
            if (entry.getPlayErrorCount() <= ERROR_COUNT_LIMIT) {
                return false;
            }
        }
        return true;
    }


    public void reset() {
        mPlayingIndex = -1;
    }
}
