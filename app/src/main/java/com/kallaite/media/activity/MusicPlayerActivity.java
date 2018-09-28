package com.kallaite.media.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.kallaite.media.util.WLog;
import com.kallaite.media.R;
import com.kallaite.media.fragment.music.MusicControlFragment;
import com.kallaite.media.presenter.MusicHomePresenter;
import com.kallaite.media.util.ActivityUtils;

import kallaite.com.util.SystemHelper;
import kallaite.com.util.TitleBarInfoUtils;

public class MusicPlayerActivity extends Activity {

    private final static String TAG = MusicPlayerActivity.class.getSimpleName();

    private Context mContext;

    private MusicHomePresenter mPresenter;

    @Override
    public void onBackPressed() {
        SystemHelper.GoHome();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WLog.d(TAG, "onCreate method was called");
        this.mContext = getApplicationContext();
        setContentView(R.layout.main_activity);
        showMusicControlFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        WLog.d(TAG, "onResume method was called");
        TitleBarInfoUtils.showAppName(this, getString(R.string.app_music));
    }

    private void showMusicControlFragment() {
        MusicControlFragment fragment =
                (MusicControlFragment) getFragmentManager().findFragmentById(R.id.contains_fragment);
        if (fragment == null) {
            fragment = new MusicControlFragment();
            ActivityUtils.addFragmentToActivity(getFragmentManager(),
                    fragment, R.id.contains_fragment);
        }
        mPresenter = new MusicHomePresenter(getApplicationContext(), fragment);
    }
}
