package com.kallaite.media.service;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.kallaite.media.MusicFan;
import com.kallaite.media.data.source.Injection;
import com.kallaite.media.data.source.MediaRepository;
import com.kallaite.media.data.struct.Album;
import com.kallaite.media.data.struct.Artist;
import com.kallaite.media.data.struct.IDataChangedNotifier;
import com.kallaite.media.data.struct.MusicEntry;
import com.kallaite.media.data.struct.MusicPlayMode;
import com.kallaite.media.engine.IPlayingStateListener;
import com.kallaite.media.util.WLog;

import java.util.ArrayList;
import java.util.List;

import kallaite.com.ApplicationApi.MediaPlayApi;
import kallaite.com.global.Communication.AppDefine;
import kallaite.com.iKallVR.VoiceActionID;
import kallaite.com.util.SystemHelper;

/**
 * Created by dengrui on 18-3-23.
 */

public class MusicServiceImpl extends ForwardingMusicController implements IPlayingStateListener,
        IDataChangedNotifier, AudioManager.OnAudioFocusChangeListener {

    private static MusicServiceImpl sInstance;

    public static final String TAG = "MusicServiceImpl";

    private Context mContext;

    private MediaPlayApi mMediaPlayApi;

    private MusicFan musicFan;

    private volatile State mCurrentState = State.UNINITIALIZED;

    private volatile boolean mGainAudioFocus = false;

    private List<IPlayingStateListener> mPlayingStateListenerList = new ArrayList<>();

    private Handler mMainLooperHandler = new Handler(Looper.getMainLooper());

    private MediaRepository mediaRepository;

    enum State {
        UNINITIALIZED, //尚未初始化
        INITIALIZING, //正在初始化中
        IDLE, //空闲状态:已经初始化但还没有播放歌曲
        PLAYING, //正在播放某歌曲
        PAUSE //暂停中
    }

    private MusicServiceImpl(Context context) {
        mContext = context;
        bindMain();
        musicFan = new MusicFan(context, this);
        setMusicController(musicFan);
        mediaRepository = Injection.provideMediaRepository(context);
        mediaRepository.addDataChangedListener(MediaRepository.Priority.MEDIUM.ordinal(), this);
    }

    private void bindMain() {
        WLog.d(TAG, "bindMain...");
        mMediaPlayApi = new MediaPlayApi(mContext, mMediaPlayCallback, null);
        mMediaPlayApi.Open(AppDefine.eAppType.AppAudio);
    }

    public static MusicServiceImpl getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MusicServiceImpl(context.getApplicationContext());
        }
        return sInstance;
    }

    public void initialize() {
        mCurrentState = State.INITIALIZING;
        musicFan.initialize();
        mCurrentState = State.IDLE;
        if (!mGainAudioFocus) {
            requestAudioFocus();
        }
        startToPlayIfIdle();
    }

    public void startToPlay() {
        if (mCurrentState == State.PLAYING || mCurrentState == State.PAUSE) {
            musicFan.requestUpdateCurrentPlaying();
        }
        if (mCurrentState == State.IDLE) {
            for (IPlayingStateListener l : mPlayingStateListenerList) {
                l.notifyStop();
            }
        }
        handleResume();
    }

    public void handleResume() {
        boolean isPhone = SystemHelper.PhoneIsAction();
        boolean isCarPlayPhone = SystemHelper.CarplayPhoneIsAction();
        WLog.i("handleResume state :" + mCurrentState +
                ",mGainAudioFocus:" + mGainAudioFocus +
                ",isPhone:" + isPhone + ",isCarPlayPhone:" + isCarPlayPhone);
        if (SystemHelper.PhoneIsAction() || isCarPlayPhone) {
            return;
        }
        startToPlayIfIdle();
        if (!mGainAudioFocus && mCurrentState == State.PAUSE) {
            musicFan.resume();
        }
        if (!mGainAudioFocus) {
            requestAudioFocus();
        }
    }

    private MediaPlayApi.MediaPlayCallback mMediaPlayCallback = new MediaPlayApi.MediaPlayCallback() {

        @Override
        public void OnParkingState(boolean isParking) {

        }

        @Override
        public void onDeviceConnected(String path) {

        }

        @Override
        public void onDeviceDisconnected(String path) {

        }

        @Override
        public void OniKallVRHandler(Message msg) {
            WLog.i("OniKallVRHandler msg :" + msg);
            switch (msg.what) {
                case VoiceActionID.ACTION_CMD_NEXT:
                    next();
                    break;
                case VoiceActionID.ACTION_CMD_PREV:
                    previous();
                    break;
            }
        }

        @Override
        public void OniKallVRResult(int result) {

        }

        @Override
        public void OnGeneralProc(int wParam, int lParam) {
            WLog.i("OnGeneralProc wParam:" + wParam + ",lParam:" + lParam);
        }

        @Override
        public void onAudioPause() {

        }

        @Override
        public void onAudioResume() {

        }

        @Override
        public void OnDestroy() {

        }

        @Override
        public void OnPowerOff(AppDefine.eAppServiceType appServiceType) {

        }

        @Override
        public void OnKeyEvent(byte keycode) {
            WLog.i("OnKeyEvent keycode:" + keycode);
            switch (keycode) {
                case AppDefine.LPARAM_KEYCODE.KEY_PLAY:
                    if (mCurrentState == State.PAUSE) {
                        resume();
                    }
                    break;
                case AppDefine.LPARAM_KEYCODE.KEY_STOPBAND:
                    if (mCurrentState == State.PLAYING) {
                        pause();
                    }
                    break;
                case AppDefine.LPARAM_KEYCODE.KEY_NEXT:
                    next();
                    break;
                case AppDefine.LPARAM_KEYCODE.KEY_PREVIOUS:
                    previous();
                    break;
                case AppDefine.LPARAM_KEYCODE.KEY_PLAY_PUASE:
                    if (mCurrentState == State.PLAYING) {
                        pause();
                    } else if (mCurrentState == State.PAUSE) {
                        resume();
                    }
                    break;
            }
        }
    };

    @Override
    public void pause() {
        if (mCurrentState == State.INITIALIZING) {
            return;
        }
        super.pause();
    }

    @Override
    public void resume() {
        if (mCurrentState == State.INITIALIZING) {
            return;
        }
        super.resume();
    }

    @Override
    public void seekTo(int position) {
        if (mCurrentState == State.INITIALIZING) {
            return;
        }
        super.seekTo(position);
    }

    @Override
    public void previous() {
        if (mCurrentState == State.INITIALIZING) {
            return;
        }
        super.previous();
    }

    @Override
    public void next() {
        if (mCurrentState == State.INITIALIZING) {
            return;
        }
        super.next();
    }

    @Override
    public void play(MusicEntry entry) {
        if (mCurrentState == State.INITIALIZING) {
            return;
        }
        super.play(entry);
    }

    public void playAll(Artist artist) {
        if (mCurrentState == State.INITIALIZING) {
            return;
        }
        musicFan.playAll(artist);
    }

    public void playAll(Album album) {
        if (mCurrentState == State.INITIALIZING) {
            return;
        }
        musicFan.playAll(album);
    }

    public void switchPlayMode(MusicPlayMode mode) {
        if (mCurrentState == State.INITIALIZING) {
            return;
        }
        super.switchPlayMode(mode);
    }

    public void registerPlayingStateListener(IPlayingStateListener listener) {
        if (listener != null) {
            mPlayingStateListenerList.add(listener);
        }
    }

    public void unregisterPlayingStateListener(IPlayingStateListener listener) {
        if (listener != null) {
            mPlayingStateListenerList.remove(listener);
        }
    }

    @Override
    public void notifyPlaying(final MusicEntry entry) {
        mCurrentState = State.PLAYING;
        mMainLooperHandler.post(new Runnable() {
            @Override
            public void run() {
                for (IPlayingStateListener l : mPlayingStateListenerList) {
                    l.notifyPlaying(entry);
                }
            }
        });
    }

    @Override
    public void notifyProgress(final int position) {
        mMainLooperHandler.post(new Runnable() {
            @Override
            public void run() {
                for (IPlayingStateListener l : mPlayingStateListenerList) {
                    l.notifyProgress(position);
                }
            }
        });
    }

    @Override
    public void notifyResume() {
        mCurrentState = State.PLAYING;
        mMainLooperHandler.post(new Runnable() {
            @Override
            public void run() {
                for (IPlayingStateListener l : mPlayingStateListenerList) {
                    l.notifyResume();
                }
            }
        });
    }

    @Override
    public void notifyPause() {
        mCurrentState = State.PAUSE;
        mMainLooperHandler.post(new Runnable() {
            @Override
            public void run() {
                for (IPlayingStateListener l : mPlayingStateListenerList) {
                    l.notifyPause();
                }
            }
        });
    }

    @Override
    public void notifyStop() {
        mCurrentState = State.IDLE;
        mMainLooperHandler.post(new Runnable() {
            @Override
            public void run() {
                for (IPlayingStateListener l : mPlayingStateListenerList) {
                    l.notifyStop();
                }
            }
        });
    }

    @Override
    public void notifyReloadMediaData(List<MusicEntry> list) {
        if (mGainAudioFocus) {
            startToPlayIfIdle();
        }
    }

    private void startToPlayIfIdle() {
        if (mCurrentState == State.IDLE) {
            musicFan.startToPlay();
        }
    }

    @Override
    public void notifyNewDataComing(List<MusicEntry> list) {

    }

    private void requestAudioFocus() {
        AudioManager audioManager = (AudioManager)
                mContext.getSystemService(Context.AUDIO_SERVICE);
        int status = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        WLog.i("requestAudioFocus status:" + status);
        mGainAudioFocus = true;
//        if (status == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//            mGainAudioFocus = true;
//        } else {
//            mGainAudioFocus = false;
//        }
        mMediaPlayApi.SendMediaStateToHost((byte) 1);
    }

    private void abandonFocus() {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(
                Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(this);
    }

    private String getFocusString(int focusChange) {
        String focusStr;
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                focusStr = "AUDIOFOCUS_GAIN";
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                focusStr = "AUDIOFOCUS_LOSS";
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                focusStr = "AUDIOFOCUS_LOSS_TRANSIENT";
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                focusStr = "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK";
                break;
            default:
                focusStr = String.valueOf(focusChange);
        }
        return focusStr;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        WLog.i("onAudioFocusChange focusChange:" + getFocusString(focusChange) +
                ",mCurrentState:" + mCurrentState + ",mGainAudioFocus:" + mGainAudioFocus);
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                mGainAudioFocus = true;
                if (mCurrentState == State.PAUSE) {
                    resume();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                abandonFocus();
                mGainAudioFocus = false;
                if (mCurrentState == State.PLAYING) {
                    pause();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                mGainAudioFocus = false;
                if (mCurrentState == State.PLAYING) {
                    pause();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                break;
        }
    }

}
