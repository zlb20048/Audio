package com.kallaite.media.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kallaite.media.data.struct.MusicEntry;
import com.kallaite.media.data.struct.MusicPlayMode;
import com.kallaite.media.engine.IPlayingStateListener;
import com.kallaite.media.service.MusicServiceImpl;

/**
 * Created by dengrui on 18-3-22.
 */

public class MusicHomePresenter implements MusicHomeContract.Presenter, IPlayingStateListener {

    private final MusicHomeContract.View mView;

    private Context mContext;

    private MusicServiceImpl musicServiceImpl;

    public MusicHomePresenter(Context context, @NonNull MusicHomeContract.View view) {
        mView = view;
        mContext = context;
        view.setPresenter(this);
        musicServiceImpl = MusicServiceImpl.getInstance(context);
    }

    @Override
    public void subscribe() {
        musicServiceImpl.registerPlayingStateListener(this);
    }

    @Override
    public void unsubscribe() {
        musicServiceImpl.unregisterPlayingStateListener(this);
    }

    @Override
    public void startToPlay() {
        musicServiceImpl.startToPlay();
    }

    @Override
    public void seekTo(int position) {
        musicServiceImpl.seekTo(position);
    }

    @Override
    public void previous() {
        musicServiceImpl.previous();
    }

    @Override
    public boolean isPlaying() {
        return musicServiceImpl.isPlaying();
    }

    @Override
    public void resume() {
        musicServiceImpl.resume();
    }

    @Override
    public void pause() {
        musicServiceImpl.pause();
    }

    @Override
    public void next() {
        musicServiceImpl.next();
    }

    @Override
    public void switchPlayMode(MusicPlayMode mode) {
        musicServiceImpl.switchPlayMode(mode);
    }

    @Override
    public void notifyPlaying(MusicEntry entry) {
        if (mView != null && entry != null) {
            mView.updateMusicTitle(entry.getTitle());
            mView.updateAlbumName(entry.getAlbumName());
            mView.updateArtistName(entry.getArtistName());
            mView.updateDuration(entry.getDuration());
            mView.updateCoverImage(entry.getTrack().getPath());
        }
    }

    @Override
    public void notifyProgress(int position) {
        if (mView != null) {
            mView.updateProgress(position);
        }
    }

    @Override
    public void notifyResume() {
        if (mView != null) {
            mView.notifyResume();
        }
    }

    @Override
    public void notifyPause() {
        if (mView != null) {
            mView.notifyPause();
        }
    }

    @Override
    public void notifyStop() {
        if (mView != null) {
            mView.showIdleView();
        }
    }
}
