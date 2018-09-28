package com.kallaite.media.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kallaite.media.data.source.Injection;
import com.kallaite.media.data.source.MediaRepository;
import com.kallaite.media.data.struct.Album;
import com.kallaite.media.data.struct.Artist;
import com.kallaite.media.data.struct.IDataChangedNotifier;
import com.kallaite.media.data.struct.MusicEntry;
import com.kallaite.media.engine.DefaultPlayingStateListener;
import com.kallaite.media.service.MusicServiceImpl;

import java.util.List;

/**
 * Created by dengrui on 18-4-8.
 */

public class MusicListPresenter extends DefaultPlayingStateListener
        implements MusicListContract.Presenter, IDataChangedNotifier {

    private MusicListContract.View mView;

    private Context mContext;

    private MediaRepository mediaRepository;

    private MusicServiceImpl musicService;

    private MusicListContract.ListType mSelectedListType;

    private Artist mCurrentArtist;

    private Album mCurrentAlbum;

    public MusicListPresenter(Context context, @NonNull MusicListContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mediaRepository = Injection.provideMediaRepository(context);
        musicService = MusicServiceImpl.getInstance(context);
    }

    @Override
    public void subscribe() {
        mediaRepository.addDataChangedListener(MediaRepository.Priority.NORMAL.ordinal(), this);
        musicService.registerPlayingStateListener(this);
    }

    @Override
    public void unsubscribe() {
        mediaRepository.removeDataChangedListener(this);
        musicService.unregisterPlayingStateListener(this);
    }

    @Override
    public void chooseList(MusicListContract.ListType listType) {
        mSelectedListType = listType;
        mCurrentArtist = null;
        mCurrentAlbum = null;
        if (listType == MusicListContract.ListType.SONG) {
            updateSongData(true, mediaRepository.getMediaData());
        } else if (listType == MusicListContract.ListType.ARTIST) {
            updateArtistData(true, mediaRepository.getArtistData());
        } else {
            updateAlbumData(true, mediaRepository.getAlbumData());
        }
    }

    @Override
    public void chooseArtist(Artist artist) {
        mCurrentArtist = artist;
        updateArtist(artist);
    }

    private void updateArtist(Artist artist) {
        mView.refreshMusicOfArtist(artist,
                mediaRepository.getMediaDataByArtist(artist.getMediaStorage(), artist.getId()));
        MusicEntry entry = musicService.getPlayingMusic();
        if (entry != null) {
            mView.notifyPlaying(false, entry);
        }
    }

    @Override
    public void chooseAlbum(Album album) {
        mCurrentAlbum = album;
        updateAlbum(album);
    }

    private void updateAlbum(Album album) {
        mView.refreshMusicOfAlbum(album,
                mediaRepository.getMediaDataByAlbum(album.getMediaStorage(), album.getId()));
        MusicEntry entry = musicService.getPlayingMusic();
        if (entry != null) {
            mView.notifyPlaying(false, entry);
        }
    }

    private void updateSongData(boolean needScroll, List<MusicEntry> list) {
        mView.refreshMusicList(list);
        MusicEntry entry = musicService.getPlayingMusic();
        if (entry != null) {
            mView.notifyPlaying(needScroll, entry);
        }
    }

    private void updateArtistData(boolean needScroll, List<Artist> list) {
        mView.refreshArtistList(list);
        MusicEntry entry = musicService.getPlayingMusic();
        if (entry != null) {
            mView.notifyPlaying(needScroll, entry);
        }
    }

    private void updateAlbumData(boolean needScroll, List<Album> list) {
        mView.refreshAlbumList(list);
        MusicEntry entry = musicService.getPlayingMusic();
        if (entry != null) {
            mView.notifyPlaying(needScroll, entry);
        }
    }

    @Override
    public void playMusic(MusicEntry entry) {
        musicService.play(entry);
    }

    @Override
    public void playAllMusic(Artist artist) {
        musicService.playAll(artist);
    }

    @Override
    public void playAllMusic(Album album) {
        musicService.playAll(album);
    }

    @Override
    public void notifyReloadMediaData(List<MusicEntry> list) {
        if (mCurrentArtist != null) {
            updateArtist(mCurrentArtist);
            return;
        }
        if (mCurrentAlbum != null) {
            updateAlbum(mCurrentAlbum);
            return;
        }
        if (mSelectedListType == MusicListContract.ListType.SONG) {
            updateSongData(true, list);
        } else if (mSelectedListType == MusicListContract.ListType.ARTIST) {
            updateArtistData(true, mediaRepository.getArtistData());
        } else {
            updateAlbumData(true, mediaRepository.getAlbumData());
        }
    }

    @Override
    public void notifyNewDataComing(List<MusicEntry> list) {
        if (mCurrentArtist != null) {
            updateArtist(mCurrentArtist);
            return;
        }
        if (mCurrentAlbum != null) {
            updateAlbum(mCurrentAlbum);
            return;
        }
        if (mSelectedListType == MusicListContract.ListType.SONG) {
            updateSongData(false, list);
        } else if (mSelectedListType == MusicListContract.ListType.ARTIST) {
            updateArtistData(false, mediaRepository.getArtistData());
        } else {
            updateAlbumData(false, mediaRepository.getAlbumData());
        }
    }

    @Override
    public void notifyPlaying(MusicEntry entry) {
        mView.notifyPlaying(false, entry);
    }
}
