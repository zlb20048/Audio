package com.kallaite.media.engine;

import android.content.Context;

import com.kallaite.media.data.struct.Artist;
import com.kallaite.media.data.struct.MusicEntry;
import com.kallaite.media.data.source.Injection;
import com.kallaite.media.data.source.MediaRepository;
import com.kallaite.media.service.ForwardingMusicController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengrui on 18-4-9.
 */

public class ArtistPlayListWalkmanOperator implements IWalkmanOperator {

    private MediaRepository mediaRepository;

    private Walkman mWalkman;

    private Context mContext;

    private ForwardingMusicController mController;

    private IPlayingStateListener mListener;

    private Artist mArtist;

    private List<MusicEntry> mList = new ArrayList<>();

    public ArtistPlayListWalkmanOperator(Context context, ForwardingMusicController controller,
                                         IPlayingStateListener listener, Artist artist) {
        mContext = context;
        mediaRepository = Injection.provideMediaRepository(context);
        mController = controller;
        mListener = listener;
        mArtist = artist;
    }

    @Override
    public void rebuild() {
        mList = mediaRepository.getMediaDataByArtist(mArtist.getMediaStorage(), mArtist.getId());
        if (!mList.isEmpty()) {
            if (mWalkman == null) {
                mWalkman = new Walkman(mContext, mList);
                mController.setMusicController(mWalkman);
                mWalkman.setPlayingStateListener(mListener);
            } else {
                mWalkman.rebuildPlayList(mList);
            }
        }
    }

    @Override
    public boolean startToPlay() {
        if (mList != null && !mList.isEmpty() && mWalkman != null) {
            MusicEntry entry = mList.get(0);
            String path = entry.getUri();
            if (!mWalkman.play(path)) {
                mWalkman.playIndex(0);
            }
            return true;
        }
        return false;
    }

    @Override
    public void release() {
        if (mWalkman != null) {
            mWalkman.stop();
            mWalkman = null;
        }
        mController.setMusicController(null);
    }
}
