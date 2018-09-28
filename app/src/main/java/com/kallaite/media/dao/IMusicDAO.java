package com.kallaite.media.dao;

import com.kallaite.media.data.struct.Album;
import com.kallaite.media.data.struct.Artist;
import com.kallaite.media.data.struct.MediaStorage;
import com.kallaite.media.data.struct.MusicEntry;

import java.util.List;

/**
 * <音乐的列表接口>
 * <获取当前的音乐列表的接口>
 * 
 * @author  zixiangliu
 * @version  [版本号, 2014-4-30]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface IMusicDAO
{

    /**
     * 根据介质获取该介质下所有歌曲
     * 
     * @param external
     *            介质类型
     * @return 歌曲列表
     */
    public List<MusicEntry> getAllMusicList(MediaStorage external);

    /**
     * 根据介质分段加载歌曲
     * @param external
     * @return
     */
    public List<MusicEntry> getMusicListByOffset(MediaStorage external,
                                                 int offset);

    /**
     * 根据介质获取该介质下所有专辑列表
     * 
     * @param external
     *            介质类型
     * @return 专辑列表
     */
    public List<Album> getAlbumList(MediaStorage external);

    /**
     * 根据介质获取该介质下所有艺术家列表
     * 
     * @param external
     *            介质类型
     * @return 艺术家列表
     */
    public List<Artist> getArtistList(MediaStorage external);

    /**
     * 根据专辑ID获取专辑下歌曲列表
     * 
     * @param external
     *            介质类型
     * @param albumId
     *            专辑ID
     * @return 歌曲列表
     */
    public List<MusicEntry> getMusicListByAlbumId(MediaStorage external,
                                                  int albumId);

    /**
     * 根据艺术家ID获取歌曲列表
     * 
     * @param external
     * @param artistId
     * @return
     */
    public List<MusicEntry> getMusicListByArtistId(MediaStorage external,
                                                   int artistId);

    /**
     * 根据关键字搜索专辑，需支持模糊查询
     * 
     * @param external
     * @param album
     * @return
     */
    public List<MusicEntry> searchByAlbum(MediaStorage external, String album);

    /**
     * 根据关键字搜索歌手，需支持模糊查询
     * 
     * @param external
     * @param artist
     * @return
     */
    public List<MusicEntry> searchByArtist(MediaStorage external, String artist);

    /**
     * 根据关键字搜索歌曲
     * 
     * @param external
     * @param musicName
     * @return
     */
    public List<MusicEntry> searchByTrack(MediaStorage external, String musicName);

    /**
     * 根据专辑ID获取专辑图片path
     * 
     * @param external
     * @param id
     * @return
     */
    public String getAlbumImageById(MediaStorage external, int id);

    /**
     * 根据歌曲ID删除歌曲
     * 
     * @param external
     * @param id
     * @return
     */
    public boolean removeMusicById(MediaStorage external, int id);

    /**
     * 根据ID更新歌曲信息
     * 
     * @param external
     * @param id
     * @return
     */
    public boolean updateCollectStatusById(MediaStorage external, int id,
                                           boolean isCollect);
    
    /**
     * 根据多媒体的path，进行删除操作
     * <功能详细描述>
     * @param external
     * @param url
     * @return
     * @see [类、类#方法、类#成员]
     */
    public boolean removeMusicByUrl(MediaStorage external, String url);

    /**
     * 根据多媒体的path，进行批量删除操作
     * <功能详细描述>
     * @param external
     * @param urls
     * @return
     * @see [类、类#方法、类#成员]
     */
    public boolean removeMusicBulkByUrls(MediaStorage external, List<String> urls);

}
