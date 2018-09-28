/*
 * 文 件 名:  AudioLastMemoryHelper.java
 * 版    权:  SmartAuto Co., Ltd. Copyright 2013-2014,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  zixiangliu
 * 修改时间:  2014-5-4
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.kallaite.media.helper;

import android.content.Context;
import android.text.TextUtils;

import com.kallaite.media.data.struct.Album;
import com.kallaite.media.data.struct.Artist;
import com.kallaite.media.data.struct.IMusicEntry;
import com.kallaite.media.data.struct.MusicEntry;
import com.kallaite.media.data.struct.MusicPlayMode;
import com.kallaite.media.data.struct.Track;
import com.kallaite.media.util.WLog;

import kallaite.com.provider.SmartAutoMediaStore;
import kallaite.com.util.DataInterface;
import kallaite.frameworks.api.XmlOperator;

/**
 * <AudioLastMemory>
 * <record the audio helper>
 *
 * @author zixiangliu
 * @version [版本号, 2014-5-4]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class AudioLastMemoryHelper {
    private String TAG = AudioLastMemoryHelper.class.getSimpleName();

    private LastMemory mLastMemory = null;

    private static AudioLastMemoryHelper instance;

    private DataInterface mDataInterface = null;

    public static String AUDIOLASTMEMORY = "AudioFile";

    public static String PATH = "path";

    public static String LOOP = "loop";

    public static String EXTERNAL = "external";

    public static String SRC = "src";

    public static String PLAY_STATE = "play_state";

    public static String LASTTIME = "lasttime";

    public static String LAST_ALBUM = "last_album";

    public static String TYPE = "type";

    public static String TITLE = "title";
    public static String ARTIST = "artist";
    public static String DURATION = "duration";
    public static String ALBUM = "album";

    public static String CATEGORY = "category";

    public static String ID = "ID";


    /**
     * 当前的播放的介质
     */
    public final static String EXTERNAL_PATH = "external_path";

    private Context mContext;

    public static synchronized AudioLastMemoryHelper getInstance(Context context) {
        if (null == instance) {
            instance = new AudioLastMemoryHelper(context);
        }
        return instance;
    }

    /**
     * The construction method of this class.
     * This method initials most variables defined in this class.
     *
     * @param context Interface to global information about an application environment.
     **/
    private AudioLastMemoryHelper(Context context) {
        mContext = context;
        mLastMemory = new LastMemory();
        mDataInterface = new XmlOperator(mContext, AUDIOLASTMEMORY);
        mDataInterface.Create();
        loadLastMemory();
    }

    /**
     * a method to read the information stored in the file named "AudioFile" and assign the information
     * to the variables  in this class.
     **/
    public void loadLastMemory() {
        mLastMemory.mTitle = mDataInterface.ReadString(AUDIOLASTMEMORY, TITLE, "");
        mLastMemory.mArtist = mDataInterface.ReadString(AUDIOLASTMEMORY, ARTIST, "");
        mLastMemory.mPath = mDataInterface.ReadString(AUDIOLASTMEMORY, PATH, "");
        mLastMemory.mLastTime = mDataInterface.ReadInt(AUDIOLASTMEMORY, LASTTIME, 0);
        mLastMemory.mLastAlbum = mDataInterface.ReadString(AUDIOLASTMEMORY, ALBUM, "");
        mLastMemory.mExternal = mDataInterface.ReadInt(AUDIOLASTMEMORY, EXTERNAL, -1);
        mLastMemory.mSrc = mDataInterface.ReadInt(AUDIOLASTMEMORY, SRC, -1);
        mLastMemory.playState = mDataInterface.ReadInt(AUDIOLASTMEMORY, PLAY_STATE, 0);
        mLastMemory.album_path = mDataInterface.ReadString(AUDIOLASTMEMORY, LAST_ALBUM, "");
        mLastMemory.category = mDataInterface.ReadString(AUDIOLASTMEMORY, CATEGORY, "");
        mLastMemory.mDuration = mDataInterface.ReadInt(AUDIOLASTMEMORY, DURATION, 0);
        mLastMemory.mId = mDataInterface.ReadString(AUDIOLASTMEMORY, ID, "");
        getDeviceFromConfigure();
        getLoopFromConfigure();
        WLog.v(TAG,
                "mLastMemory.mPath = " + mLastMemory.mPath +
                        " mLastMemory.mLastTime = " + mLastMemory.mLastTime +
                        " mLastMemory.playState = " + mLastMemory.playState +
                        " mLastMemory.album_path = " + mLastMemory.album_path +
                        " mLastMemory.mDuration = " + mLastMemory.mDuration +
                        " mLastMemory.mExternal = " + mLastMemory.mExternal +
                        " mLastMemory.mSrc = " + mLastMemory.mSrc +
                        " mLastMemory.mId = " + mLastMemory.mId +
                        " mLastMemory.mLastAlbum =" + mLastMemory.mLastAlbum);
    }

    public MusicEntry createArtificialMusicEntry(){
        MusicEntry entry = new MusicEntry();
        entry.setArtificial(true);

        Track track = new Track();
        track.setId(-1);
        track.setName(mLastMemory.mTitle);
        track.setPath(mLastMemory.mPath);
        track.setDuration(mLastMemory.mDuration);

        Artist artist = new Artist(-1, mLastMemory.mArtist);

        Album album = new Album(-1, mLastMemory.mLastAlbum, "");

        entry.setTrack(track);
        entry.setArtist(artist);
        entry.setAlbum(album);
        return entry;
    }


    public String getTitle() {
        return mLastMemory.mTitle;
    }

    /**
     * a method to get the state of storage devices stored in the file named "AudioFile"
     * and assign it to the variable in this class.
     **/
    public void getDeviceFromConfigure() {
        mLastMemory.mExternal = mDataInterface.ReadInt(AUDIOLASTMEMORY, EXTERNAL, 0);
    }

    /**
     * a method to get the loop mode of player stored in the file named "AudioFile"
     * and assign it to the variable in this class.
     **/
    public void getLoopFromConfigure() {
        mLastMemory.mLoop = mDataInterface.ReadInt(AUDIOLASTMEMORY, LOOP, MusicPlayMode.LOOP_NORMAL.ordinal());
    }

    /**
     * a method  to write the path of song and the progress of player into the file named "AudioFile"
     *
     * @param entry    the path of song
     * @param lasttime the progress of player
     **/
    public void setFileMemory(IMusicEntry entry, int lasttime) {
        if (null == entry) {
            WLog.e(TAG, "setFileMemory music entry is null");
            return;
        }
        WLog.v(TAG, "entry.getTitle() = " + entry.getTitle());
        mDataInterface.WriteString(AUDIOLASTMEMORY, TITLE, entry.getTitle());
        mDataInterface.WriteString(AUDIOLASTMEMORY, ARTIST, entry.getArtistName());
        mDataInterface.WriteString(AUDIOLASTMEMORY, ALBUM, entry.getAlbumName());
        String path = entry.getUri();
        if (!TextUtils.isEmpty(path)) {
            mDataInterface.WriteString(AUDIOLASTMEMORY, PATH, entry.getUri());
        }
        mDataInterface.WriteInt(AUDIOLASTMEMORY, LASTTIME, lasttime);
        mDataInterface.WriteString(AUDIOLASTMEMORY, LAST_ALBUM, entry.getAlbumCover());
        mDataInterface.WriteInt(AUDIOLASTMEMORY, DURATION, entry.getDuration());
        mDataInterface.WriteString(AUDIOLASTMEMORY, ID, entry.getIndex());
        mLastMemory.mTitle = entry.getTitle();
        mLastMemory.mArtist = entry.getArtistName();
        mLastMemory.mPath = entry.getUri();
        if (lasttime > 1000) {
            mLastMemory.mLastTime = lasttime;
        }
        mLastMemory.mLastAlbum = entry.getAlbumName();
        mLastMemory.mDuration = entry.getDuration();
        mLastMemory.mId = entry.getIndex();

    }

    /**
     * a method  to write the state of device into the file named "AudioFile"
     *
     * @param deviceName the storage device used by player currently
     **/
    public void setSelectModule(int deviceName) {
        mDataInterface.WriteInt(AUDIOLASTMEMORY, EXTERNAL, deviceName);
        mLastMemory.mExternal = deviceName;
    }

    public void setSelectSrc(int src) {
        mDataInterface.WriteInt(AUDIOLASTMEMORY, SRC, src);
        mLastMemory.mSrc = src;
    }

    public void setNetPlayCategory(String category) {
        mDataInterface.WriteString(AUDIOLASTMEMORY, CATEGORY, category);
        mLastMemory.category = category;
    }

    /**
     * 设置当前的播放状态
     *
     * @param playState 当前的播放状态 0 播放 1 暂停
     */
    public void setPlayState(int playState) {
        mDataInterface.WriteInt(AUDIOLASTMEMORY, PLAY_STATE, playState);
        mLastMemory.playState = playState;
    }

    /**
     * a method  to write the loop mode of player into the file named "AudioFile"
     *
     * @param loop the loop mode of the player
     **/
    public void setAudioPlayType(int loop) {
        mDataInterface.WriteInt(AUDIOLASTMEMORY, LOOP, loop);
        mLastMemory.mLoop = loop;
    }

    public String getArtist() {
        return mLastMemory.mArtist;
    }

    public String getAlbum() {
        return mLastMemory.mLastAlbum;
    }

    public String getId() {
        return mLastMemory.mId;
    }

    public int getDuration() {
        return mLastMemory.mDuration;
    }

    public String getCategory() {
        return mLastMemory.category;
    }

    /**
     * a method to get the storage device state from the variable of this class.
     *
     * @return the value of the variable named "mLastMemory"
     **/
    public int getMemoryDevice() {
        return mLastMemory.mExternal;
    }

    /**
     * a method to get the storage music play state from the variable of this class.
     *
     * @return the value of the variable named "mLastMemory"
     */
    public int getMemoryPlayState() {
        return mLastMemory.playState;
    }

    public int getMemorySrcDevice() {
        return mLastMemory.mSrc;
    }

    /**
     * a method to get the loop mode of player from the variable of this class.
     *
     * @return the value of the variable named "mLoop"
     **/
    public int getLoop() {
        return mLastMemory.mLoop;
    }

    /**
     * a method to get the path of song from the variable of this class.
     *
     * @return the value of the variable named "mPath"
     **/
    public String getMusicPath() {
        return mLastMemory.mPath;
    }

    /**
     * a method to get the progress of player from the variable of this class.
     *
     * @return the value of the variable named "mLastTime"
     **/
    public int getLastTime() {
        return mLastMemory.mLastTime;
    }

    /**
     * a inner class to store the information of player temporarily
     * the ultimate information is saved in the permanent memory.
     **/
    class LastMemory {
        int mLastTime = 0;

        String mPath = "";

        String mLastAlbum = "";

        int mExternal = 0;

        int mSrc = 0;

        int playState = 0;

        int mLoop = MusicPlayMode.LOOP_NORMAL.ordinal();

        String album_path = "";

        String category = "";

        int mDuration = 0;
        public String mTitle = "";
        public String mArtist = "";

        String mId = "";
    }
}
