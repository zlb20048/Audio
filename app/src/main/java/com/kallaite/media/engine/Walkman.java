package com.kallaite.media.engine;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.kallaite.media.R;
import com.kallaite.media.data.struct.MusicEntry;
import com.kallaite.media.data.struct.MusicPlayList;
import com.kallaite.media.data.struct.MusicPlayMode;
import com.kallaite.media.helper.AudioLastMemoryHelper;
import com.kallaite.media.service.IMusicController;
import com.kallaite.media.util.WLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dengrui on 18-3-26.
 */

public class Walkman implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, IMusicController {

    private List<MusicEntry> mList = new ArrayList<>();

    private Context mContext;

    private MusicPlayList mCurrentMusicPlayList;

    private IPlayingStateListener mPlayingStateListener;

    private PlayMachine mPlayMachine;

    private Handler mHandler = new MyHandler(Looper.getMainLooper());

    private AudioLastMemoryHelper mAudioLastMemoryHelper;

    private static final int MSG = 1;

    private Action mAction;

    enum Action {
        PREVIOUS, NEXT
    }

    public Walkman(Context context, List<MusicEntry> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("list parameter cannot be null");
        }
        mContext = context;
        mAudioLastMemoryHelper = AudioLastMemoryHelper.getInstance(context);
        mList = Collections.unmodifiableList(list);
        powerOn();
    }

    public void rebuildPlayList(final List<MusicEntry> list) {
        mList = Collections.unmodifiableList(list);
        MusicEntry playingEntry = mCurrentMusicPlayList.getPlayingMusicEntry();
        mCurrentMusicPlayList = MusicPlayList.buildMusicPlayList(mList);
        if (playingEntry != null) {
            MusicEntry entry = mCurrentMusicPlayList.jump(playingEntry.getTrack().getPath());
            if (entry == null) {
                mHandler.removeMessages(MSG);
                letsRock(mCurrentMusicPlayList.jump(0));
            }
        }
    }

    public void setPlayingStateListener(IPlayingStateListener l) {
        mPlayingStateListener = l;
    }

    private void powerOn() {
        mCurrentMusicPlayList = MusicPlayList.buildMusicPlayList(mList);
        mPlayMachine = new PlayMachine(this, this, this);
    }

    public boolean play(String path) {
        if (TextUtils.isEmpty(path) || !new File(path).exists() || !isFileInList(path)) {
            return false;
        }
        MusicEntry entry = mCurrentMusicPlayList.jump(path);
        if (entry != null) {
            letsRock(entry);
            return true;
        } else {
            return false;
        }
    }

    private boolean isFileInList(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        for (MusicEntry entry : mList) {
            if (path.equals(entry.getTrack().getPath())) {
                return true;
            }
        }
        return false;
    }

    public boolean playIndex(int pos) {
        MusicEntry entry = mCurrentMusicPlayList.jump(pos);
        if (entry == null) {
            return false;
        } else {
            letsRock(entry);
            return true;
        }
    }

    private void letsRock(final MusicEntry entry) {
        mPlayMachine.play(entry);
        mAudioLastMemoryHelper.setFileMemory(entry, 0);
        if (mPlayingStateListener != null) {
            mPlayingStateListener.notifyPlaying(entry);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        letsRock(mCurrentMusicPlayList.next());
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        WLog.i("onError what:" + what + ",extra:" + extra);
        mHandler.removeMessages(MSG);
        MusicEntry entry = mCurrentMusicPlayList.getPlayingMusicEntry();
        if (entry != null) {
            WLog.i("onError path:" + entry.getUri());
            entry.increaseErrorCount();
        }
        if (mCurrentMusicPlayList.wholeListCannotPlay()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext,
                            R.string.current_playlist_error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if (mAction == Action.PREVIOUS) {
                previous();
            } else {
                next();
            }
            mAction = null;
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mHandler.removeMessages(MSG);
        Message msg = Message.obtain();
        msg.what = MSG;
        mHandler.sendMessage(msg);
        mPlayingStateListener.notifyResume();
    }

    @Override
    public void requestUpdateCurrentPlaying() {
        if (mPlayingStateListener != null && mCurrentMusicPlayList != null) {
            MusicEntry entry = mCurrentMusicPlayList.getPlayingMusicEntry();
            if (entry != null) {
                mPlayingStateListener.notifyPlaying(mCurrentMusicPlayList.getPlayingMusicEntry());
                mPlayingStateListener.notifyProgress(mPlayMachine.getCurrentPosition());
                if(mPlayMachine.isPlaying()){
                    mPlayingStateListener.notifyResume();
                }else{
                    mPlayingStateListener.notifyPause();
                }
            }
        }
    }

    @Override
    public boolean isPlaying() {
        return mPlayMachine.isPlaying();
    }

    @Override
    public void pause() {
        mPlayMachine.pause();
        mPlayingStateListener.notifyPause();
    }

    @Override
    public void resume() {
        mPlayMachine.resume();
        mPlayingStateListener.notifyResume();
    }

    public void stop() {
        mHandler.removeMessages(MSG);
        mPlayMachine.stop();
        mPlayingStateListener.notifyStop();
        mCurrentMusicPlayList.reset();
    }

    @Override
    public void seekTo(int position) {
        mPlayMachine.seekTo(position);
    }

    @Override
    public void previous() {
        mAction = Action.PREVIOUS;
        letsRock(mCurrentMusicPlayList.previous());
    }

    @Override
    public void next() {
        mAction = Action.NEXT;
        letsRock(mCurrentMusicPlayList.next());
    }

    @Override
    public void play(MusicEntry entry) {
        if (entry != null) {
            MusicEntry musicEntry = mCurrentMusicPlayList.jump(entry.getTrack().getPath());
            if (musicEntry != null) {
                letsRock(musicEntry);
            }
        }
    }

    @Override
    public MusicEntry getPlayingMusic() {
        return mCurrentMusicPlayList.getPlayingMusicEntry();
    }

    @Override
    public void switchPlayMode(MusicPlayMode mode) {
        mCurrentMusicPlayList.changeMode(mode);
    }


    class MyHandler extends Handler {

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            int position = mPlayMachine.getCurrentPosition();
            mPlayingStateListener.notifyProgress(position);
            Message newmsg = Message.obtain();
            newmsg.what = MSG;
            mHandler.sendMessageDelayed(newmsg, 1000);
        }
    }
}
