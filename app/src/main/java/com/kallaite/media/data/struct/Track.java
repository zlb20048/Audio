/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @author  zixiangliu
 * @version  [版本号, 2014-4-25]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */

package com.kallaite.media.data.struct;

import java.io.Serializable;

/**
 * <歌曲主要信息>
 * <存放歌曲的主要信息，方便在播放的时候得到相关值>
 * 
 * @author  zixiangliu
 * @version  [版本号, 2014-4-25]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class Track implements Serializable, Cloneable
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * numeric id of the track e.g. 108254
     */
    private int id;

    /**
     * path
     */
    private String path;

    /**
     * set pinyin
     */
    private String pinyin;

    /**
     * Name of the track e.g. "Tout se passera bien"
     */
    private String name;

    /**
     * Name of the file
     */
    private String displayName;

    /**
     * Length of the track (in seconds) e.g. 310
     */
    private int duration;

    /**
     * Music state about collect
     */
    private boolean isCollect;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public int getDuration()
    {
        return duration;
    }

    public void setDuration(int duration)
    {
        this.duration = duration;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getPath()
    {
        return path;
    }

    public void setPinyin(String pinyin)
    {
        this.pinyin = pinyin;
    }

    public String getPinyin()
    {
        return pinyin;
    }

    public boolean isCollect()
    {
        return isCollect;
    }

    public void setCollect(boolean isCollect)
    {
        this.isCollect = isCollect;
    }

    /**
     * 获取文件名，包括后缀
     * @return
     */
    public String getFileNameWithSuffix()
    {
        String pathName = null;
        if (null != path && path.startsWith("/"))
        {
            try
            {
                String[] s = path.split("/");
                pathName = s[s.length - 1];
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return pathName;
    }

    @Override
    public Object clone()
        throws CloneNotSupportedException
    {
        return (Track)super.clone();
    }

    @Override
    public boolean equals(Object o)
    {
        if (super.equals(o))
        {
            return true;
        }
        if (null != o)
        {
            Track track = (Track)o;
            if (null != path && this.path.equals(track.getPath()))
            {
                return true;
            }
        }
        return false;
    }
}
