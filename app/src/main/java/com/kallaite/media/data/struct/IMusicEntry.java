package com.kallaite.media.data.struct;

public abstract class IMusicEntry
{

    private int mPlayErrorCount;

    public int getPlayErrorCount() {
        return mPlayErrorCount;
    }

    public void increaseErrorCount() {
        this.mPlayErrorCount++;
    }

    /**
     *  索引序号<一句话功能简述>
     * <功能详细描述>
     * @return
     * @see [类、类#方法、类#成员]
     */
    public abstract String getIndex();

    /**
     *  歌曲名<一句话功能简述>
     * <功能详细描述>
     * @return
     * @see [类、类#方法、类#成员]
     */
    public abstract String getTitle();

    /**
     *  艺术家<一句话功能简述>
     * <功能详细描述>
     * @return
     * @see [类、类#方法、类#成员]
     */
    public abstract String getArtistName();

    /**
     *  专辑名称<一句话功能简述>
     * <功能详细描述>
     * @return
     * @see [类、类#方法、类#成员]
     */
    public abstract String getAlbumName();

    /**
     * 专辑封面
     */
    public abstract String getAlbumCover();

    /**
     *  播放时长
     * @return
     * @see [类、类#方法、类#成员]
     */
    public abstract int getDuration();

    /**
     *  路径
     * 可为路径，可以为url
     * @return
     * @see [类、类#方法、类#成员]
     */
    public abstract String getUri();

}
