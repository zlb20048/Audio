package com.kallaite.media.data.source;

import com.kallaite.media.data.struct.Album;
import com.kallaite.media.data.struct.Artist;
import com.kallaite.media.data.struct.MusicEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengrui on 18-4-17.
 */

//This class is simply for Debug
public class EmptyMediaDataSource implements MediaDataSource{
    @Override
    public List<MusicEntry> getMediaData() {
        return new ArrayList<>();
    }

    @Override
    public List<Artist> getArtistData() {
        return new ArrayList<>();
    }

    @Override
    public List<Album> getAlbumData() {
        return new ArrayList<>();
    }

    @Override
    public List<MusicEntry> getMediaDataByArtist(int artistID) {
        return new ArrayList<>();
    }

    @Override
    public List<MusicEntry> getMediaDataByAlbum(int albumID) {
        return new ArrayList<>();
    }

    @Override
    public void dump() {

    }

    @Override
    public List<MusicEntry> reloadMediaData() {
        return new ArrayList<>();
    }

    @Override
    public List<MusicEntry> requestMoreData() {
        return new ArrayList<>();
    }
}
