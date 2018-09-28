package com.kallaite.media.service;

import com.kallaite.media.data.struct.MusicEntry;
import com.kallaite.media.data.struct.MusicPlayMode;

/**
 * Created by dengrui on 18-3-27.
 */

public class ForwardingMusicController implements IMusicController {

    private IMusicController musicController;

    public void setMusicController(IMusicController mc) {
        musicController = mc;
    }

    @Override
    public void requestUpdateCurrentPlaying() {
        if (musicController != null) {
            musicController.requestUpdateCurrentPlaying();
        }
    }

    @Override
    public boolean isPlaying() {
        if (musicController != null) {
            return musicController.isPlaying();
        }
        return false;
    }

    @Override
    public void pause() {
        if (musicController != null) {
            musicController.pause();
        }
    }

    @Override
    public void resume() {
        if (musicController != null) {
            musicController.resume();
        }
    }

    @Override
    public void seekTo(int position) {
        if (musicController != null) {
            musicController.seekTo(position);
        }
    }

    @Override
    public void previous() {
        if (musicController != null) {
            musicController.previous();
        }
    }

    @Override
    public void next() {
        if (musicController != null) {
            musicController.next();
        }
    }

    @Override
    public void play(MusicEntry entry) {
        if (musicController != null) {
            musicController.play(entry);
        }
    }

    @Override
    public MusicEntry getPlayingMusic() {
        if (musicController != null) {
            return musicController.getPlayingMusic();
        }
        return null;
    }

    @Override
    public void switchPlayMode(MusicPlayMode mode) {
        if (musicController != null) {
            musicController.switchPlayMode(mode);
        }
    }
}
