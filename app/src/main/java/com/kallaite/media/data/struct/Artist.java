package com.kallaite.media.data.struct;

import java.io.Serializable;

/**
 * <歌手信息>
 * <歌手信息>
 *
 * @author zixiangliu
 * @version [版本号, 2014-4-30]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class Artist implements Serializable {

    /**
     * TAG
     */
    private static final long serialVersionUID = 1L;

    /**
     * numeric id of the artist
     */
    private int id;

    /**
     * Display name of the artist. different of idstr
     */
    private String name;

    /**
     * pinyin
     */
    private String pinyin;

    private MediaStorage mediaStorage;

    public Artist(int artistId, String artistName) {
        this.id = artistId;
        this.name = artistName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtistPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getArtistPinyin() {
        return pinyin;
    }

    public MediaStorage getMediaStorage() {
        return mediaStorage;
    }

    public void setMediaStorage(MediaStorage mediaStorage) {
        this.mediaStorage = mediaStorage;
    }
}
