package com.kallaite.media.dao;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.kallaite.media.data.struct.Album;
import com.kallaite.media.data.struct.Artist;
import com.kallaite.media.data.struct.MediaStorage;
import com.kallaite.media.data.struct.MusicEntry;
import com.kallaite.media.data.struct.Track;
import com.kallaite.media.util.WLog;

import java.util.ArrayList;
import java.util.List;

import kallaite.com.provider.SmartAutoMediaStore.Audio.AlbumColumns;
import kallaite.com.provider.SmartAutoMediaStore.Audio.ArtistColumns;
import kallaite.com.provider.SmartAutoMediaStore.Audio.Media;

/**
 * <获取到当前的歌曲的信息>
 * <和数据库交互，完成当前的数据管理的功能>
 *
 * @author zixiangliu
 * @version [版本号, 2014-4-30]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class MusicDAOImpl implements IMusicDAO {

    private static final String CLASSNAME = "MusicDAOImpl";

    private static final int LIMIT = 20;

    private final String[] audioMessage =
            {Media.ALBUM, Media.ARTIST, Media.DATA, Media.TITLE, Media.MIME_TYPE,
                    Media._ID, Media.DURATION, Media.ALBUM_ID, Media.ARTIST_ID,
                    Media.SIZE, Media.DISPLAY_NAME, Media.DATE_MODIFIED,
                    Media.TITLE_PINYIN, AlbumColumns.ALBUM_PINYIN,
                    ArtistColumns.ARTIST_PINYIN, AlbumColumns.ALBUM_ART};

    private final String[] albumColumns =
            {Media._ID, AlbumColumns.ALBUM_ART, Media.ALBUM, Media.ARTIST,
                    AlbumColumns.ALBUM_PINYIN};

    private final String[] artistColumns =
            {Media._ID, Media.ARTIST, ArtistColumns.ARTIST_PINYIN};

    private Context mContext;

    public MusicDAOImpl(Context context) {
        mContext = context;
    }

    @Override
    public List<MusicEntry> getAllMusicList(MediaStorage type) {
        return getAudioData(type);
    }

    @Override
    public List<MusicEntry> getMusicListByOffset(MediaStorage external,
                                                 int offset) {
        String orderBy =
                Media._ID + " ASC LIMIT " + LIMIT + " OFFSET " + offset;
        WLog.d(CLASSNAME, "getMusicListByOffset:" + orderBy);
        return getAudioData(external, external.getUri(), null, null, orderBy);
    }

    @Override
    public List<Album> getAlbumList(MediaStorage type) {
        return getAlbumData(type, type.getAlbumUri(), null, null, Media._ID);
    }

    @Override
    public List<Artist> getArtistList(MediaStorage type) {
        return getArtistData(type, type.getArtistUri(), null, null, Media._ID);
    }

    @Override
    public List<MusicEntry> getMusicListByAlbumId(MediaStorage type,
                                                  int albumId) {
        String selection = Media.ALBUM_ID + " = " + albumId;
        WLog.d("selection = " + selection);
        return getAudioData(type, type.getUri(), selection, null, null);
    }

    @Override
    public List<MusicEntry> getMusicListByArtistId(MediaStorage type,
                                                   int artistId) {
        String selection = Media.ARTIST_ID + " = " + artistId;
        return getAudioData(type, type.getUri(), selection, null, null);
    }

    @Override
    public List<MusicEntry> searchByAlbum(MediaStorage external,
                                          String album) {
        String selection = Media.ALBUM + " like ?";
        return getAudioData(external,
                external.getUri(),
                selection,
                new String[]{album},
                null);
    }

    @Override
    public List<MusicEntry> searchByArtist(MediaStorage external,
                                           String artist) {
        String selection = Media.ARTIST + " like ?";
        return getAudioData(external,
                external.getUri(),
                selection,
                new String[]{artist},
                null);
    }

    @Override
    public List<MusicEntry> searchByTrack(MediaStorage external,
                                          String musicName) {
        String selection = Media.TITLE + " like ?";
        return getAudioData(external,
                external.getUri(),
                selection,
                new String[]{musicName},
                null);
    }

    @Override
    public String getAlbumImageById(MediaStorage type, int id) {
        String selection = Media._ID + " = " + id;
        return getAlbumImage(type, type.getAlbumUri(), selection, null, null);
    }

    @Override
    public boolean updateCollectStatusById(MediaStorage external, int id,
                                           boolean isCollect) {
        String where = Media._ID + " = " + id;
        try {
            ContentResolver resolver = mContext.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(audioMessage[audioMessage.length - 1],
                    isCollect ? 1 : 0);
            int num = resolver.update(external.getUri(), values, where, null);
            WLog.d(CLASSNAME, "[update : " + num + "][" + where + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean removeMusicById(MediaStorage external, int id) {
        String where = Media._ID + " = " + id;
        try {
            Uri uri = Uri.parse(external.getUri() + "/" + id);
            ContentResolver resolver = mContext.getContentResolver();
            int num = resolver.delete(uri, null, null);
            WLog.d(CLASSNAME, "[removeMusicById : " + num + "][" + where + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean removeMusicByUrl(MediaStorage external, String url) {
        String where = Media.DATA + " = \"" + url + "\"";
        try {
            ContentResolver resolver = mContext.getContentResolver();
            int num = resolver.delete(external.getUri(), where, null);
            WLog.i(CLASSNAME, "[removeMusicByUrl : " + num + "][" + where + "]");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean removeMusicBulkByUrls(MediaStorage external,
                                         List<String> urls) {
        if (urls == null || urls.isEmpty())
            return false;
        try {
            ContentResolver resolver = mContext.getContentResolver();
            StringBuffer sb = new StringBuffer();
            for (String url : urls) {
                sb.append(" ")
                        .append(Media.DATA)
                        .append(" = ")
                        .append("\"")
                        .append(url)
                        .append("\"")
                        .append(" or ");
            }
            String where = sb.substring(0, sb.lastIndexOf("or"));
            int num = resolver.delete(external.getUri(), where, null);
            WLog.i(CLASSNAME, "[removeMusicByUrl : " + num + "][" + where + "]");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //
    public List<MusicEntry> getAudioData(MediaStorage type) {
        return getAudioData(type, type.getUri(), null, null, Media._ID);
    }

    public MusicEntry getAudioById(MediaStorage type, int id) {
        Uri uri = ContentUris.withAppendedId(type.getUri(), id);
        List<MusicEntry> list = getAudioData(type, uri, null, null, null);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    private List<MusicEntry> getAudioData(MediaStorage type, Uri uri,
                                          String selection, String[] selectionArgs, String orderBy) {
        List<MusicEntry> list = new ArrayList<MusicEntry>();
        Cursor cursor = null;
        try {
            WLog.i("getAudioData type:" + type + ",uri:" + uri +
                    ",selection:" + selection +
                    ",selectionArgs:" + selectionArgs + ",orderBy:" + orderBy);
            ContentResolver resolver = mContext.getContentResolver();
            cursor = resolver.query(uri,
                    audioMessage,
                    selection,
                    selectionArgs,
                    orderBy);
            if (cursor == null) {
                WLog.d(CLASSNAME, "cursor is null");
                return list;
            }
            WLog.d(CLASSNAME,
                    type + "[corsor.getCount=" + cursor.getCount()
                            + "][start create musiclist]");
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                MusicEntry entry = new MusicEntry();
                String path = cursor.getString(cursor.getColumnIndex(Media.DATA));
                // 判断当前的路径下是否有cue文件
//                CueFileBean bean =
//                    CueFileDecodeHelper.getInstance().decodeFile(path);
//                List<CueSongBean> songBeans = bean.getSongs();
//                String startIndex = "00:00:00";
//                if (null != songBeans)
//                {
//                    List<PlayListCueEntry> playCueEntryList =
//                        new ArrayList<PlayListCueEntry>();
//                    for (CueSongBean songBean : songBeans)
//                    {
//                        PlayListCueEntry playListCueEntry =
//                            new PlayListCueEntry();
//                        playListCueEntry.setSrc(MusicSrc.USB);
//                        Track track = new Track();
//                        track.setPath(path);
//                        int duration =
//                            DateFormatUtil.formatDate(songBean.getIndexBegin())
//                                - DateFormatUtil.formatDate(startIndex);
//                        // 记录为上一个歌曲的开始时间
//                        startIndex = songBean.getIndexBegin();
//                        track.setDisplayName(songBean.getTitle());
//                        track.setName(songBean.getTitle());
//                        Album album =
//                            new Album(cursor.getInt(cursor.getColumnIndex(Media.ALBUM_ID)),
//                                songBean.getPerformer());
//                        Artist artist =
//                            new Artist(cursor.getInt(cursor.getColumnIndex(Media.ARTIST_ID)),
//                                TextUtils.isEmpty(songBean.getPerformer()) ?
//                                    bean.getPerformer() :
//                                    songBean.getPerformer());
//                        playListCueEntry.setAlbum(album);
//                        playListCueEntry.setArtist(artist);
//                        playListCueEntry.setTrack(track);
//                        playListCueEntry.setExternal(type);
//                        playListCueEntry.setStartPosition(startIndex);
//                        playCueEntryList.add(playListCueEntry);
//                    }
//                    for (int k = 0; k < playCueEntryList.size() - 1; k++)
//                    {
//                        playCueEntryList.get(k)
//                            .setDuration(
//                                playCueEntryList.get(k + 1).getStartPosition()
//                                    - playCueEntryList.get(k)
//                                    .getStartPosition());
//                    }
//                    list.addAll(playCueEntryList);
//                }
//                else
//                {
                Track track = new Track();
                track.setId(cursor.getInt(cursor.getColumnIndex(Media._ID)));
                track.setDuration(cursor.getInt(cursor.getColumnIndex(Media.DURATION)));
                track.setName(cursor.getString(cursor.getColumnIndex(Media.TITLE)));
                track.setPath(path);
                track.setDisplayName(cursor.getString(cursor.getColumnIndex(
                        Media.DISPLAY_NAME)));
                track.setPinyin(cursor.getString(cursor.getColumnIndex(Media.TITLE_PINYIN)));
                int temp =
                        cursor.getInt(cursor.getColumnIndex(audioMessage[
                                audioMessage.length - 1]));
                track.setCollect(temp == 1 ? true : false);

                int albumId = cursor.getInt(cursor.getColumnIndex(Media.ALBUM_ID));
                Album album =
                        new Album(albumId,
                                cursor.getString(cursor.getColumnIndex(Media.ALBUM)),
                                cursor.getString(cursor.getColumnIndex(Media.ARTIST)));
                album.setAlbumPinyin(cursor.getString(cursor.getColumnIndex(
                        AlbumColumns.ALBUM_PINYIN)));
                String strImg =
                        cursor.getString(cursor.getColumnIndex(AlbumColumns.ALBUM_ART));
                album.setImage(strImg);

                album.setMediaStorage(type);

                int artistId = cursor.getInt(cursor.getColumnIndex(Media.ARTIST_ID));
                Artist artist =
                        new Artist(artistId,
                                cursor.getString(cursor.getColumnIndex(Media.ARTIST)));
                artist.setArtistPinyin(cursor.getString(cursor.getColumnIndex(
                        ArtistColumns.ARTIST_PINYIN)));
                artist.setMediaStorage(type);

                entry.setAlbum(album);
                entry.setArtist(artist);
                entry.setTrack(track);
                entry.setExternal(type);

                list.add(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
            WLog.i("getAudioData exception:" + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    private List<Album> getAlbumData(MediaStorage type, Uri uri, String selection,
                                     String[] selectionArgs, String softOrder) {
        List<Album> list = new ArrayList<Album>();
        Cursor cursor = null;
        try {
            ContentResolver resolver = mContext.getContentResolver();
            cursor = resolver.query(uri,
                    albumColumns,
                    selection,
                    selectionArgs,
                    softOrder);
            if (cursor == null) {
                return list;
            }
            WLog.d(CLASSNAME,
                    "[corsor.getCount=" + cursor.getCount()
                            + "][start create albumlist]");
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();

                Album album =
                        new Album(cursor.getInt(cursor.getColumnIndex(Media._ID)),
                                cursor.getString(cursor.getColumnIndex(Media.ALBUM)),
                                cursor.getString(cursor.getColumnIndex(Media.ARTIST)));
                album.setImage(cursor.getString(cursor.getColumnIndex(
                        albumColumns[1])));
                album.setAlbumPinyin(cursor.getString(cursor.getColumnIndex(
                        "album_pinyin")));
                album.setMediaStorage(type);
                list.add(album);

            }
            WLog.d(CLASSNAME, "[end create albumlist]");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    private List<Artist> getArtistData(MediaStorage type, Uri uri, String selection,
                                       String[] selectionArgs, String softOrder) {
        List<Artist> list = new ArrayList<Artist>();
        Cursor cursor = null;
        try {
            ContentResolver resolver = mContext.getContentResolver();
            cursor = resolver.query(uri,
                    artistColumns,
                    selection,
                    selectionArgs,
                    softOrder);
            if (cursor == null) {
                return list;
            }
            WLog.d(CLASSNAME,
                    "[corsor.getCount=" + cursor.getCount()
                            + "][start create artistlist]");
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();

                Artist artist =
                        new Artist(cursor.getInt(cursor.getColumnIndex(Media._ID)),
                                cursor.getString(cursor.getColumnIndex(Media.ARTIST)));
                artist.setArtistPinyin(cursor.getString(cursor.getColumnIndex(
                        "artist_pinyin")));
                artist.setMediaStorage(type);
                list.add(artist);
            }
            WLog.d(CLASSNAME, "[end create artistlist]");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public String getAlbumImage(MediaStorage type, Uri uri, String selection,
                                String[] selectionArgs, String softOrder) {
        String image = null;
        Cursor cursor = null;
        try {
            ContentResolver resolver = mContext.getContentResolver();
            cursor = resolver.query(uri,
                    new String[]{albumColumns[1]},
                    selection,
                    selectionArgs,
                    softOrder);
            if (cursor == null || cursor.getCount() < 1) {
                return image;
            }
            cursor.moveToNext();
            image = cursor.getString(cursor.getColumnIndex(albumColumns[1]));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return image;
    }
}
