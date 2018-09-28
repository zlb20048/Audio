package com.kallaite.media.data.source;

import com.kallaite.media.data.struct.Album;
import com.kallaite.media.data.struct.Artist;
import com.kallaite.media.data.struct.MusicEntry;

import java.util.List;

/**
 * Created by dengrui on 18-3-23.
 */

public interface MediaDataSource {

    List<MusicEntry> getMediaData();

    List<Artist> getArtistData();

    List<Album> getAlbumData();

    List<MusicEntry> getMediaDataByArtist(int artistID);

    List<MusicEntry> getMediaDataByAlbum(int albumID);

    void dump();

    List<MusicEntry> reloadMediaData();

    List<MusicEntry> requestMoreData();

}
