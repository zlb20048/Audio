package com.kallaite.media;

import android.content.Context;

import com.kallaite.media.util.WLog;
import com.kallaite.media.data.struct.Album;
import com.kallaite.media.data.struct.Artist;
import com.kallaite.media.data.struct.MusicEntry;
import com.kallaite.media.data.source.Injection;
import com.kallaite.media.data.source.MediaRepository;
import com.kallaite.media.data.struct.IDataChangedNotifier;
import com.kallaite.media.engine.IPlayingStateListener;
import com.kallaite.media.engine.IWalkmanOperator;
import com.kallaite.media.engine.WalkmanOperatorFactory;
import com.kallaite.media.engine.WalkmanOperatorFactory.PlayListType;
import com.kallaite.media.service.ForwardingMusicController;

import java.util.List;

/**
 * Created by dengrui on 18-3-26.
 */

public class MusicFan extends ForwardingMusicController implements IDataChangedNotifier {

    private Context mContext;

    private MediaRepository mediaRepository;

    private IPlayingStateListener mPlayingStateListener;

    private IWalkmanOperator mWalkmanOperator;

    private PlayListType mPlayListType = PlayListType.ALL;

    public MusicFan(Context context, IPlayingStateListener listener) {
        mContext = context;
        mediaRepository = Injection.provideMediaRepository(context);
        mPlayingStateListener = listener;
        mediaRepository.addDataChangedListener(MediaRepository.Priority.MAX.ordinal(), this);
        mWalkmanOperator = WalkmanOperatorFactory.getWalkmanOperator(mPlayListType, context, this,
                mPlayingStateListener, null, null);
    }

    public void initialize() {
        mWalkmanOperator.rebuild();
    }
    public boolean startToPlay(){
        return mWalkmanOperator.startToPlay();
    }

    @Override
    public void play(MusicEntry entry) {
        changePlayListType(null, null, PlayListType.ALL);
        super.play(entry);
    }

    public void playAll(Artist artist) {
        changePlayListType(artist, null, PlayListType.ARTIST);
        mWalkmanOperator.startToPlay();
    }


    public void playAll(Album album) {
        changePlayListType(null, album, PlayListType.ALBUM);
        mWalkmanOperator.startToPlay();
    }

    private void changePlayListType(Artist artist, Album album, PlayListType type) {
        if (mPlayListType != type) {
            mPlayListType = type;
            mWalkmanOperator.release();
            mWalkmanOperator = WalkmanOperatorFactory.getWalkmanOperator(mPlayListType, mContext,
                    this, mPlayingStateListener, artist, album);
            mWalkmanOperator.rebuild();
        }
    }

    @Override
    public void notifyReloadMediaData(final List<MusicEntry> list) {
        WLog.i("notifyReloadMediaData list size is:" + list.size());
        if (list.isEmpty()) {
            mWalkmanOperator.release();
            changePlayListType(null, null, PlayListType.ALL);
        }
        mWalkmanOperator.rebuild();
    }

    @Override
    public void notifyNewDataComing(final List<MusicEntry> list) {
        WLog.i("MusicFan notifyNewDataComing start :" + list.size());
        mWalkmanOperator.rebuild();
    }
}
