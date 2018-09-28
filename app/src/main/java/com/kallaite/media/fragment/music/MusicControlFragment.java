package com.kallaite.media.fragment.music;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kallaite.media.R;
import com.kallaite.media.activity.MusicListActivity;
import com.kallaite.media.data.struct.MusicPlayMode;
import com.kallaite.media.glide.AudioFileCover;
import com.kallaite.media.presenter.MusicHomeContract;
import com.kallaite.media.util.Utils;
import com.kallaite.media.util.WLog;

public class MusicControlFragment extends Fragment implements MusicHomeContract.View {
    private final static String TAG = MusicControlFragment.class.getSimpleName();

    /**
     * 列表展开的按钮
     */
    private ImageButton folderbtn = null;

    /**
     * 封套图片
     */
    private ImageView album;

    /**
     * 歌手名称
     */
    private TextView artistname;

    /**
     * 歌曲名称
     */
    private TextView trackname;

    /**
     * 专辑名称
     */
    private TextView albumname;

    /**
     * 当前的时间进度
     */
    private SeekBar mediacontrollerProgress;

    /**
     * 当前时间进度
     */
    private TextView timeCurrent;

    /**
     * 总时间长度
     */
    private TextView timeTotal;

    /**
     * 暂停
     */
    private ImageButton mPlayPauseBtn;

    /**
     * 下一首
     */
    private ImageButton prev;

    /**
     * 上一首
     */
    private ImageButton next;

    /**
     * 重复选项
     */
    private ImageButton loopImage;

    /**
     * 长按事件
     */
    private boolean isLongClick;
    /**
     * 判断是否在拖动
     */
    private boolean isDrag;

    private RelativeLayout loadingLayout = null;

    private ProgressDialog mProgressDialog;

    private MusicHomeContract.Presenter mPresenter;

    private MusicPlayMode musicPlayMode = MusicPlayMode.LOOP_NORMAL;


    @Override
    public void setPresenter(MusicHomeContract.Presenter presenter) {
        mPresenter = presenter;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDialog = new ProgressDialog(getActivity());
    }


    @Override
    public void onResume() {
        super.onResume();
        WLog.d(TAG, "onResume...");
        mPresenter.subscribe();
        mPresenter.startToPlay();
    }

    @Override
    public void onPause() {
        super.onPause();
        WLog.d(TAG, "onPause...");
        mPresenter.unsubscribe();
    }

    private void showDialog() {
        mProgressDialog.setMessage(getActivity().getResources().getString(R.string.is_seeking_music));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    public void showIdleView() {
        trackname.setText(R.string.unknown_track_name);
        artistname.setText(R.string.unknown_artist_name);
        albumname.setText(R.string.unknown_album_name);
        album.setImageResource(R.drawable.photo_album_default);
        timeTotal.setText(Utils.getDurationString(0));
        timeCurrent.setText(Utils.getDurationString(0));
        mediacontrollerProgress.setMax(100);
        mediacontrollerProgress.setProgress(0);
        updatePlayPauseButtonImage(false);
    }

    @Override
    public void updateMusicTitle(String title) {
        if (trackname != null) {
            if (TextUtils.isEmpty(title)) {
                title = getResources().getString(R.string.unknown_track_name);
            }
            trackname.setText(title);
        }
    }

    @Override
    public void updateArtistName(String artist) {
        if (artistname != null) {
            if (TextUtils.isEmpty(artist)) {
                artist = getResources().getString(R.string.unknown_artist_name);
            }
            artistname.setText(artist);
        }
    }

    @Override
    public void updateAlbumName(String album) {
        if (albumname != null) {
            if (TextUtils.isEmpty(album)) {
                album = getResources().getString(R.string.unknown_album_name);
            }
            albumname.setText(album);
        }
    }

    @Override
    public void updateCoverImage(String imagePath) {
        if (album != null && imagePath != null) {
            Glide.with(getActivity())
                    .load(new AudioFileCover(imagePath))
                    .dontAnimate()
                    .into(album);
        }
    }

    @Override
    public void updateProgress(int progress) {
        if (mediacontrollerProgress != null && !isDrag) {
            mediacontrollerProgress.setProgress(progress);
        }
        if (timeCurrent != null) {
            timeCurrent.setText(Utils.getDurationString(progress));
        }
    }

    @Override
    public void updateDuration(int duration) {
        if (timeTotal != null) {
            timeTotal.setText(Utils.getDurationString(duration));
        }
        if (mediacontrollerProgress != null) {
            mediacontrollerProgress.setMax(duration);
        }
    }

    @Override
    public void notifyResume() {
        if (mPlayPauseBtn != null) {
            mPlayPauseBtn.setImageResource(R.drawable.pause_selector);
        }
    }

    @Override
    public void showLoading() {
        showDialog();
    }

    @Override
    public void showLoadingFinished() {
        mProgressDialog.dismiss();
    }

    @Override
    public void notifyPause() {
        if (mPlayPauseBtn != null) {
            mPlayPauseBtn.setImageResource(R.drawable.play_selector);
        }
    }

    private OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            WLog.v(TAG, "onStopTrackingTouch...");
            isDrag = false;
            mPresenter.seekTo(seekBar.getProgress());
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            WLog.v(TAG, "onStartTrackingTouch...");
            isDrag = true;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            timeCurrent.setText(Utils.getDurationString(progress));
        }
    };

    /**
     * 更新当前的播放的顺序
     */
    private void updateCurrentLoop(MusicPlayMode musicPlayMode) {
        switch (musicPlayMode) {
            case LOOP_NORMAL:
                loopImage.setImageResource(R.drawable.repeatall_selector);
                break;
            case LOOP_ONE:
                loopImage.setImageResource(R.drawable.repeatone_selector);
                break;
            case SHUFFLE:
                loopImage.setImageResource(R.drawable.shuffle_enable_selector);
                break;
        }
    }

    private View.OnClickListener onButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.folderbtn:
                    doActivityChange();
                    break;
                case R.id.prev:
                    mPresenter.previous();
                    break;
                case R.id.next:
                    mPresenter.next();
                    break;
                case R.id.pause:
                    if(mPresenter.isPlaying()){
                        mPresenter.pause();
                    }else{
                        mPresenter.resume();
                    }
                    break;
                case R.id.repeat:
                    musicPlayMode = musicPlayMode.next();
                    updateCurrentLoop(musicPlayMode);
                    mPresenter.switchPlayMode(musicPlayMode);
                    break;
            }
        }
    };

    private OnTouchListener mOnTouchListener = new OnTouchListener() {
        float lastX = -1;

        float lastY = -1;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int distanceX = Math.abs((int) (event.getRawX() - lastX));
                    int distanceY = Math.abs((int) (event.getRawY() - lastY));
                    if ((distanceX >= 20 || distanceY >= 20) && (v.getId() == R.id.next || v.getId() == R.id.prev)) {
                        cancelRW_FR();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (v.getId() == R.id.next || v.getId() == R.id.prev) {
                        cancelRW_FR();
                    }
            }
            return false;
        }
    };

    /**
     * 停止快进快退动作。
     * <功能详细描述>
     *
     * @see [类、类#方法、类#成员]
     */
    private void cancelRW_FR() {
        if (!isLongClick) {
            return;
        }
        isLongClick = false;
    }

    /**
     * 更新当前的播放的按钮
     *
     * @param isPlaying
     */
    private void updatePlayPauseButtonImage(boolean isPlaying) {
        if (isPlaying) {
            mPlayPauseBtn.setImageResource(R.drawable.pause_selector);
        } else {
            mPlayPauseBtn.setImageResource(R.drawable.play_selector);
        }
    }

    /**
     * change activity to MusicListActivity
     */
    public void doActivityChange() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), MusicListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View playView = inflater.inflate(R.layout.audio_control_fragment, container, false);
        folderbtn = (ImageButton) playView.findViewById(R.id.folderbtn);
        loadingLayout = (RelativeLayout) playView.findViewById(R.id.loading_root);
        album = (ImageView) playView.findViewById(R.id.album);
        artistname = (TextView) playView.findViewById(R.id.artistname);
        trackname = (TextView) playView.findViewById(R.id.trackname);
        albumname = (TextView) playView.findViewById(R.id.albumname);
        mediacontrollerProgress = (SeekBar) playView.findViewById(R.id.mediacontroller_progress);
        timeCurrent = (TextView) playView.findViewById(R.id.time_current);
        timeTotal = (TextView) playView.findViewById(R.id.time_total);

        mPlayPauseBtn = (ImageButton) playView.findViewById(R.id.pause);
        prev = (ImageButton) playView.findViewById(R.id.prev);
        next = (ImageButton) playView.findViewById(R.id.next);

        loopImage = (ImageButton) playView.findViewById(R.id.repeat);

        // add the button onclick listener
        folderbtn.setOnClickListener(onButtonOnClickListener);
        mPlayPauseBtn.setOnClickListener(onButtonOnClickListener);
        prev.setOnClickListener(onButtonOnClickListener);
        next.setOnClickListener(onButtonOnClickListener);
        loopImage.setOnClickListener(onButtonOnClickListener);

        updateCurrentLoop(musicPlayMode);
//        // 设置长按事件，用来快进快退歌曲
//        prev.setOnLongClickListener(mPrevLongClickListener);
//        next.setOnLongClickListener(mNextLongClickListener);
        prev.setOnTouchListener(mOnTouchListener);
        next.setOnTouchListener(mOnTouchListener);

        mediacontrollerProgress.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        showIdleView();
        return playView;
    }
}
