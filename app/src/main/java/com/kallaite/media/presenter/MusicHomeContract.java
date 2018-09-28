package com.kallaite.media.presenter;

import com.kallaite.media.data.struct.MusicPlayMode;
import com.kallaite.media.view.BaseView;

/**
 * Created by dengrui on 18-3-22.
 */

public interface MusicHomeContract {

    interface View extends BaseView<Presenter> {

        void notifyResume();

        void notifyPause();

        void showLoading();

        void showLoadingFinished();

        void showIdleView();

        void updateMusicTitle(String title);

        void updateArtistName(String artist);

        void updateAlbumName(String album);

        void updateCoverImage(String musicPath);

        void updateProgress(int progess);

        void updateDuration(int duration);
    }

    interface Presenter extends BasePresenter {

        void startToPlay();

        void seekTo(int position);

        void previous();

        boolean isPlaying();

        void resume();

        void pause();

        void next();

        void switchPlayMode(MusicPlayMode mode);

    }
}
