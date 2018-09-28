package com.kallaite.media.presenter;

import com.kallaite.media.data.struct.Album;
import com.kallaite.media.data.struct.Artist;
import com.kallaite.media.data.struct.MusicEntry;
import com.kallaite.media.R;
import com.kallaite.media.view.BaseView;

import java.util.List;

/**
 * Created by dengrui on 18-4-8.
 */

public interface MusicListContract {

    enum ListType {

        SONG {
            @Override
            public int getName() {
                return R.string.tracks_title;
            }

            @Override
            public int getIcon() {
                return R.drawable.tracks_selector;
            }

            @Override
            public int getFocusIcon() {
                return R.drawable.list_music_focus;
            }
        }, ARTIST {
            @Override
            public int getName() {
                return R.string.artists_title;
            }

            @Override
            public int getIcon() {
                return R.drawable.artists_selector;
            }

            @Override
            public int getFocusIcon() {
                return R.drawable.list_artist_focus;
            }
        }, ALBUM {
            @Override
            public int getName() {
                return R.string.albums_title;
            }

            @Override
            public int getIcon() {
                return R.drawable.albums_selector;
            }

            @Override
            public int getFocusIcon() {
                return R.drawable.list_album_focus;
            }
        };

        public abstract int getName();

        public abstract int getIcon();

        public abstract int getFocusIcon();
    }


    interface View extends BaseView<Presenter> {

        void refreshMusicList(List<MusicEntry> list);

        void refreshArtistList(List<Artist> list);

        void refreshAlbumList(List<Album> list);

        void refreshMusicOfArtist(Artist artist, List<MusicEntry> list);

        void refreshMusicOfAlbum(Album album, List<MusicEntry> list);

        void notifyPlaying(boolean needScroll, MusicEntry entry);

    }

    interface Presenter extends BasePresenter {

        void chooseList(ListType listType);

        void chooseArtist(Artist artist);

        void chooseAlbum(Album album);

        void playMusic(MusicEntry entry);

        void playAllMusic(Artist artist);

        void playAllMusic(Album album);

    }
}
