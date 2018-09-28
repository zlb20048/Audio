package com.kallaite.media.engine;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.kallaite.media.data.struct.MusicEntry;

import java.io.IOException;

/**
 * Created by dengrui on 18-3-26.
 */

public class PlayMachine implements IPlayMachine, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener {

    private MediaPlayer mPlayer;

    private MediaPlayer.OnCompletionListener mOnCompletionListener;

    private MediaPlayer.OnErrorListener mOnErrorListener;

    private MediaPlayer.OnPreparedListener mOnPreparedListener;



    public PlayMachine(MediaPlayer.OnCompletionListener onCompletionListener,
                       MediaPlayer.OnErrorListener onErrorListener,
                       MediaPlayer.OnPreparedListener onPreparedListener) {
        mOnCompletionListener = onCompletionListener;
        mOnErrorListener = onErrorListener;
        mOnPreparedListener = onPreparedListener;
    }

    @Override
    public synchronized void play(MusicEntry entry) {
        try {
            if (mPlayer != null) {
                mPlayer.release();
                mPlayer = null;
            }
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(entry.getUri());
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnErrorListener(this);
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized int getCurrentPosition() {
        if (mPlayer != null) {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public synchronized int getDuration() {
        if (mPlayer != null) {
            return mPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public synchronized void resume() {
        if (mPlayer != null) {
            mPlayer.start();
        }
    }

    @Override
    public synchronized void pause() {
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }

    @Override
    public synchronized boolean isPlaying() {
        if (mPlayer != null) {
            return mPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public synchronized void seekTo(int position) {
        if (mPlayer != null) {
            mPlayer.seekTo(position);
        }
    }

    @Override
    public synchronized void stop() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public synchronized void onCompletion(MediaPlayer mp) {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onCompletion(mp);
        }
    }

    @Override
    public synchronized boolean onError(MediaPlayer mp, int what, int extra) {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        if (mOnErrorListener != null) {
            mOnErrorListener.onError(mp, what, extra);
        }
        return false;
    }

    @Override
    public synchronized void onPrepared(MediaPlayer mp) {
        if (mPlayer != null) {
            mPlayer.start();
        }
        if (mOnPreparedListener != null) {
            mOnPreparedListener.onPrepared(mp);
        }
    }
}
