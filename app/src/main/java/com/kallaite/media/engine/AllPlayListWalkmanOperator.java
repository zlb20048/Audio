package com.kallaite.media.engine;

import android.content.Context;
import android.text.TextUtils;

import com.kallaite.media.browser.NodeHolder;
import com.kallaite.media.helper.AudioLastMemoryHelper;
import com.kallaite.media.data.struct.MusicEntry;
import com.kallaite.media.data.source.Injection;
import com.kallaite.media.data.source.MediaRepository;
import com.kallaite.media.helper.MediaReceiverHelper;
import com.kallaite.media.service.ForwardingMusicController;
import com.kallaite.media.util.WLog;

import java.io.File;
import java.util.List;

/**
 * Created by dengrui on 18-4-9.
 */

public class AllPlayListWalkmanOperator implements IWalkmanOperator {

    private MediaRepository mediaRepository;

    private AudioLastMemoryHelper mAudioLastMemoryHelper;

    private Walkman mWalkman;

    private Context mContext;

    private ForwardingMusicController mController;

    private IPlayingStateListener mListener;

    public AllPlayListWalkmanOperator(Context context, ForwardingMusicController controller,
                                      IPlayingStateListener listener) {
        mContext = context;
        mediaRepository = Injection.provideMediaRepository(context);
        mAudioLastMemoryHelper = AudioLastMemoryHelper.getInstance(context);
        mController = controller;
        mListener = listener;
    }

    @Override
    public void rebuild() {
        List<MusicEntry> list = mediaRepository.getMediaData();
        if (!list.isEmpty()) {
            ensureLastMemory(list);
            if (mWalkman == null) {
                mWalkman = new Walkman(mContext, list);
                mController.setMusicController(mWalkman);
                mWalkman.setPlayingStateListener(mListener);
            } else {
                mWalkman.rebuildPlayList(list);
            }
        }
    }

    @Override
    public boolean startToPlay() {
        if (mWalkman != null) {
            String path = mAudioLastMemoryHelper.getMusicPath();
            WLog.i("startToPlay lastMemory path is :" + path);
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

    private void ensureLastMemory(List<MusicEntry> list) {
        String lastPath = mAudioLastMemoryHelper.getMusicPath();
        boolean isUnmounted = MediaReceiverHelper.getInstance(mContext).unmounted(lastPath);
        if (isUnmounted) {
            WLog.i("Path :" + lastPath + " is unmounted");
            return;
        }
        boolean fileExist = new File(lastPath).exists();
        WLog.i("------------ensureLastMemory path:" + lastPath + ",fileExist:" + fileExist);
        if (!TextUtils.isEmpty(lastPath) && fileExist) {
            boolean artificialExist = false;
            boolean normalExist = false;
            MusicEntry artificialEntry = null;
            for (MusicEntry entry : list) {
                if (entry.getUri().equals(lastPath)) {
                    if (entry.isArtificial()) {
                        artificialExist = true;
                        artificialEntry = entry;
                    } else {
                        normalExist = true;
                        break;
                    }
                }
            }
            WLog.i("ensureLastMemory path:" + lastPath +
                    ",artificialExist:" + artificialExist + ",normalExist:" + normalExist);
            if (artificialExist && normalExist) {
                if (artificialEntry != null) {
                    list.remove(artificialEntry);
                }
            } else if (!artificialExist && !normalExist) {
                MusicEntry newEntry = mAudioLastMemoryHelper.createArtificialMusicEntry();
                list.add(0, newEntry);
                NodeHolder.getInstance().appendNodeChain(newEntry);
            }
        }
    }
}
