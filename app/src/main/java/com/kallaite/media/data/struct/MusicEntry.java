/*
 * Copyright (C) 2009 Teleca Poland Sp. z o.o. <android@teleca.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kallaite.media.data.struct;

import java.io.Serializable;

/**
 * <播放列表实体类>
 * <播放列表存放的实体，能够得到歌曲的所有的信息>
 *
 * @author  zixiangliu
 * @version  [版本号, 2014-4-25]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class MusicEntry extends IMusicEntry implements Serializable, Cloneable
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * audioSessionId
     */
    private int audioSessionId = 0;

    /**
     * 专辑
     */
    private Album album;

    /**
     * 歌曲信息
     */
    private Track track;

    /**
     * 歌手
     */
    private Artist artist;

    private boolean artificial = false;

    /**
     * 类型
     */
    private MediaStorage external = MediaStorage.USB;

    public Album getAlbum()
    {
        return album;
    }

    public void setAlbum(Album album)
    {
        this.album = album;
    }

    public Track getTrack()
    {
        return track;
    }

    public void setTrack(Track track)
    {
        this.track = track;
    }

    public Artist getArtist()
    {
        return artist;
    }

    public void setArtist(Artist artist)
    {
        this.artist = artist;
    }

    public MediaStorage getExternal()
    {
        return external;
    }

    public void setExternal(MediaStorage external)
    {
        this.external = external;
    }

    public void setAudioSessionId(int audioSessionId)
    {
        this.audioSessionId = audioSessionId;
    }

    public boolean isArtificial() {
        return artificial;
    }

    public void setArtificial(boolean artificial) {
        this.artificial = artificial;
    }

    @Override
    public Object clone()
        throws CloneNotSupportedException
    {
        MusicEntry o = (MusicEntry)super.clone();
        o.track = (Track)track.clone();
        return o;
    }

    /** {@inheritDoc} */

    @Override
    public boolean equals(Object o)
    {
        if (super.equals(o))
        {
            return true;
        }
        if (null != o && o instanceof MusicEntry)
        {
            MusicEntry entry = (MusicEntry)o;
            if (null != track && this.track.equals(entry.getTrack()))
            {
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */

    @Override
    public String getIndex()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */

    @Override
    public String getTitle()
    {
        return getTrack().getName();
    }

    /** {@inheritDoc} */

    @Override
    public String getArtistName()
    {
        return getArtist().getName();
    }

    /** {@inheritDoc} */

    @Override
    public String getAlbumName()
    {
        return getAlbum().getName();
    }

    @Override
    public String getAlbumCover()
    {
        return getAlbum().getImage();
    }

    @Override
    public int getDuration()
    {
        return getTrack().getDuration();
    }

    /** {@inheritDoc} */

    @Override
    public String getUri()
    {
        if (null != getTrack())
        {
            return getTrack().getPath();
        }
        return "";
    }
}
