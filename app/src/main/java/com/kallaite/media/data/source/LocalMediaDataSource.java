package com.kallaite.media.data.source;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kallaite.media.util.WLog;
import com.kallaite.media.data.struct.Album;
import com.kallaite.media.data.struct.Artist;
import com.kallaite.media.data.struct.MediaStorage;
import com.kallaite.media.data.struct.MusicEntry;
import com.kallaite.media.dao.IMusicDAO;
import com.kallaite.media.dao.MusicDAOImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dengrui on 18-3-23.
 */

public abstract class LocalMediaDataSource implements MediaDataSource {

    private Context mContext;

    private IMusicDAO musicDAO;

    private List<MusicEntry> mLocalMediaList = new CustomList(new ArrayList<MusicEntry>());

    private List<Artist> mArtistList = new ArrayList<>();

    private List<Album> mAlbumList = new ArrayList<>();

    private boolean mLoadMediaData = false;

    public LocalMediaDataSource(@NonNull Context context) {
        mContext = context;
    }

    protected abstract MediaStorage getMediaStorage();

    @Override
    public List<MusicEntry> getMediaData() {
        if (!mLoadMediaData) {
            reloadMediaData();
            mLoadMediaData = true;
        }
        return Collections.unmodifiableList(mLocalMediaList);
    }


    @Override
    public List<Artist> getArtistData() {
        return Collections.unmodifiableList(mArtistList);
    }

    @Override
    public List<Album> getAlbumData() {
        return Collections.unmodifiableList(mAlbumList);
    }


    @Override
    public List<MusicEntry> getMediaDataByArtist(int artistID) {
        return musicDAO.getMusicListByArtistId(getMediaStorage(), artistID);
    }

    @Override
    public List<MusicEntry> getMediaDataByAlbum(int albumID) {
        return musicDAO.getMusicListByAlbumId(getMediaStorage(), albumID);
    }

    @Override
    public List<MusicEntry> reloadMediaData() {
        if (musicDAO == null) {
            musicDAO = new MusicDAOImpl(mContext);
        }
        mLocalMediaList.clear();
        mLocalMediaList.addAll(musicDAO.getAllMusicList(getMediaStorage()));
        reloadArtistAndAlbum();
        mLoadMediaData = true;
        return Collections.unmodifiableList(mLocalMediaList);
    }

    private void reloadArtistAndAlbum() {
        mArtistList.clear();
        mAlbumList.clear();
        mArtistList.addAll(musicDAO.getArtistList(getMediaStorage()));
        mAlbumList.addAll(musicDAO.getAlbumList(getMediaStorage()));
    }

    @Override
    public void dump() {
        List<MusicEntry> list = getMediaData();
        WLog.i("==============" + getMediaStorage() + " Data dump start==============");
        for (MusicEntry entry : list) {
            WLog.i("Path:" + entry.getTrack().getPath() + ",Name:" + entry.getTrack().getName());
        }
        WLog.i("==============" + getMediaStorage() + " Data dump end==============");
    }

    @Override
    public List<MusicEntry> requestMoreData() {
        mLocalMediaList.addAll(
                musicDAO.getMusicListByOffset(getMediaStorage(), mLocalMediaList.size()));
        reloadArtistAndAlbum();
        return Collections.unmodifiableList(mLocalMediaList);
    }
}
