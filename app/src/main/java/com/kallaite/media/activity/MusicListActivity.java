package com.kallaite.media.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.kallaite.media.service.MusicServiceImpl;
import com.kallaite.media.util.WLog;
import com.kallaite.media.R;
import com.kallaite.media.fragment.list.table.DirectoryBrowserFragment;
import com.kallaite.media.fragment.list.table.MusicListFragment;
import com.kallaite.media.presenter.DirectoryBrowserPresenter;
import com.kallaite.media.presenter.MusicListPresenter;
import com.kallaite.media.util.ActivityUtils;

public class MusicListActivity extends Activity implements DirectoryBrowserFragment.OnQuitListener {

    private final static String TAG = MusicListActivity.class.getSimpleName();

    private RadioGroup rg = null;

    private Context mContext;

    private DirectoryBrowserFragment mDirectoryBrowserFragment;

    private MusicListFragment musicListFragment;

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener =
            new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.allsourcebtn:
                            showDirectoryBrowser();
                            break;
                        case R.id.localsourcebtn:
                            showListFragment();
                            break;
                    }
                }
            };

    private void showDirectoryBrowser() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        hideFragment(transaction);
        if(mDirectoryBrowserFragment == null){
            mDirectoryBrowserFragment = new DirectoryBrowserFragment();
            new DirectoryBrowserPresenter(mContext, mDirectoryBrowserFragment);
            ActivityUtils.addFragmentToActivity(
                    getFragmentManager(), mDirectoryBrowserFragment, R.id.panel);
        }else{
            transaction.show(mDirectoryBrowserFragment);
        }
        transaction.commit();
    }

    private void showListFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        hideFragment(transaction);
        if(musicListFragment == null){
            musicListFragment = new MusicListFragment();
            new MusicListPresenter(mContext, musicListFragment);
            ActivityUtils.addFragmentToActivity(
                    getFragmentManager(), musicListFragment, R.id.panel);
        }else{
            transaction.show(musicListFragment);
        }
        transaction.commit();
    }
    private void hideFragment(FragmentTransaction transaction){
        if(musicListFragment != null){
            transaction.hide(musicListFragment);
        }
        if(mDirectoryBrowserFragment != null){
            transaction.hide(mDirectoryBrowserFragment);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        WLog.d(TAG, "onCreate...");
        setContentView(R.layout.music_list_activity);
        this.mContext = getApplication();
        rg = (RadioGroup) findViewById(R.id.type_rg);
        rg.setOnCheckedChangeListener(onCheckedChangeListener);
        ((RadioButton) rg.getChildAt(0)).setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicServiceImpl.getInstance(this).handleResume();
    }

    public void onQuit() {
        finish();
    }
}
