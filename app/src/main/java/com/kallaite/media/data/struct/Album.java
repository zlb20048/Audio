package com.kallaite.media.data.struct;

import java.io.Serializable;

/**
 * <专辑信息>
 * <歌曲专辑信息>
 *
 * @author  zixiangliu
 * @version  [版本号, 2014-4-25]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class Album implements Serializable
{
    private static final long serialVersionUID = 8517633545835124349L;

    /**
     * numeric id of the album
     */
    private int id;

    /**
     * link to the cover of the album
     */
    private String image;

    /**
     * name of the album
     */
    private String name;

    /**
     * 拼音
     */
    private String pinyin;

    /**
     * Display name of the artist.
     */
    private String artistName;

    private MediaStorage mediaStorage;

    public Album(int albumId, String albumName)
    {
        this.id = albumId;
        this.name = albumName;
    }

    public Album(int albumId, String albumName, String artistName)
    {
        this.id = albumId;
        this.artistName = artistName;
        this.name = albumName;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getArtistName()
    {
        return artistName;
    }

    public void setArtistName(String artistName)
    {
        this.artistName = artistName;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public void setAlbumPinyin(String pinyin)
    {
        this.pinyin = pinyin;
    }

    public String getAlbumPinyin()
    {
        return pinyin;
    }

    public MediaStorage getMediaStorage() {
        return mediaStorage;
    }

    public void setMediaStorage(MediaStorage mediaStorage) {
        this.mediaStorage = mediaStorage;
    }
}
