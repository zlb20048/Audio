package com.kallaite.media.fragment.list.table;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.kallaite.media.data.struct.Album;
import com.kallaite.media.data.struct.Artist;
import com.kallaite.media.data.struct.MusicEntry;
import com.kallaite.media.R;
import com.kallaite.media.fragment.adapter.AlbumListAdapter;
import com.kallaite.media.fragment.adapter.ArtistListAdapter;
import com.kallaite.media.fragment.adapter.SongListAdapter;
import com.kallaite.media.fragment.adapter.SourceAdapter;
import com.kallaite.media.fragment.bean.ModuleLabel;
import com.kallaite.media.presenter.MusicListContract;

import java.util.ArrayList;
import java.util.List;

public class MusicListFragment extends Fragment implements MusicListContract.View {

    private final static String TAG = MusicListFragment.class.getSimpleName();

    private ListView mLabelListView;

    private ListView mContentListView;

    private SourceAdapter mSourceAdapter;

    private SongListAdapter mSongListAdapter;

    private ArtistListAdapter mArtistListAdapter;

    private AlbumListAdapter mAlbumListAdapter;

    private SongListAdapter mSecondaryArtistListAdapter;

    private SongListAdapter mSecondaryAlbumListAdapter;

    private View mEmptyView;

    private MusicListContract.Presenter mPresenter;

    private MusicListContract.ListType mSelectedListType;

    private DirectoryBrowserFragment.OnQuitListener mOnQuitListener;

    private View mListHeaderView;

    private TextView mHeadBackView;

    private TextView mHeadPlayAllView;

    private View mDividerView;

    private Artist mCurrentArtist;

    private Album mCurrentAlbum;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.subscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    public void initLabelListView(Context context) {

        List<ModuleLabel> list = new ArrayList<>();
        for (MusicListContract.ListType type : MusicListContract.ListType.values()) {
            list.add(getModule(type));
        }
        mSourceAdapter = new SourceAdapter(context, list);
        mLabelListView.setAdapter(mSourceAdapter);
    }

    private ModuleLabel getModule(MusicListContract.ListType type) {
        ModuleLabel module = new ModuleLabel();
        module.lableName = getString(type.getName());
        module.lableIcon = type.getIcon();
        module.focusIcon = type.getFocusIcon();
        return module;
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MusicListContract.ListType selectedType = MusicListContract.ListType.values()[position];
            if (selectedType != mSelectedListType) {
                switchListType(selectedType);
                mPresenter.chooseList(mSelectedListType);
                mSourceAdapter.setCurrentPos(position);
                mSourceAdapter.notifyDataSetChanged();
            }
        }
    };

    private void switchListType(MusicListContract.ListType newType) {
        if (mSelectedListType == MusicListContract.ListType.SONG) {
            mSongListAdapter = null;
        } else if (mSelectedListType == MusicListContract.ListType.ARTIST) {
            mArtistListAdapter = null;
        } else {
            mAlbumListAdapter = null;
        }
        mSecondaryArtistListAdapter = null;
        mSecondaryAlbumListAdapter = null;
        mCurrentArtist = null;
        mCurrentAlbum = null;
        mSelectedListType = newType;
        mListHeaderView.setVisibility(View.GONE);
        mDividerView.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_list_common, container, false);
        mLabelListView = (ListView) view.findViewById(R.id.left_listview);
        mContentListView = (ListView) view.findViewById(R.id.content_listview);
        mEmptyView = view.findViewById(R.id.empty);
        mListHeaderView = view.findViewById(R.id.listhead);
        mHeadBackView = (TextView) view.findViewById(R.id.headbacktitle);
        mHeadPlayAllView = (TextView) view.findViewById(R.id.headplay);
        mDividerView = view.findViewById(R.id.divider);

        initLabelListView(view.getContext());
        mLabelListView.setOnItemClickListener(onItemClickListener);
        mSelectedListType = MusicListContract.ListType.SONG;
        mPresenter.chooseList(mSelectedListType);
        return view;
    }

    @Override
    public void setPresenter(MusicListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void refreshMusicList(final List<MusicEntry> list) {
        if (mSongListAdapter == null) {
            mSongListAdapter = new SongListAdapter(getActivity(), list);
            mContentListView.setAdapter(mSongListAdapter);
        } else {
            mSongListAdapter.refreshData(list);
        }
        mContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MusicEntry entry = list.get(position);
                mPresenter.playMusic(entry);
                mOnQuitListener.onQuit();
            }
        });
        handleEmpty(list.isEmpty());
    }

    @Override
    public void refreshArtistList(final List<Artist> list) {
        if (mArtistListAdapter == null) {
            mArtistListAdapter = new ArtistListAdapter(getActivity(), list);
            mContentListView.setAdapter(mArtistListAdapter);
        } else {
            mArtistListAdapter.refreshData(list);
        }
        mContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artist artist = list.get(position);
                mCurrentArtist = artist;
                mPresenter.chooseArtist(artist);
            }
        });
        handleEmpty(list.isEmpty());
    }

    @Override
    public void refreshAlbumList(final List<Album> list) {
        if (mAlbumListAdapter == null) {
            mAlbumListAdapter = new AlbumListAdapter(getActivity(), list);
            mContentListView.setAdapter(mAlbumListAdapter);
        } else {
            mAlbumListAdapter.refreshData(list);
        }
        mContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Album album = list.get(position);
                mCurrentAlbum = album;
                mPresenter.chooseAlbum(album);
            }
        });
        handleEmpty(list.isEmpty());
    }

    @Override
    public void refreshMusicOfArtist(final Artist artist, final List<MusicEntry> list) {
        if (list.isEmpty()) {
            mListHeaderView.setVisibility(View.GONE);
            mDividerView.setVisibility(View.GONE);
        } else {
            mListHeaderView.setVisibility(View.VISIBLE);
            mDividerView.setVisibility(View.VISIBLE);
        }
        mHeadBackView.setText(artist.getName());
        if (TextUtils.isEmpty(artist.getName())) {
            mHeadBackView.setText(getActivity().getString(R.string.unknown_artist_name));
        }
        if (mSecondaryArtistListAdapter == null) {
            mSecondaryArtistListAdapter = new SongListAdapter(getActivity(), list);
            mContentListView.setAdapter(mSecondaryArtistListAdapter);
        } else {
            mSecondaryArtistListAdapter.refreshData(list);
        }
        mHeadBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchListType(mSelectedListType);
                mPresenter.chooseList(mSelectedListType);
            }
        });
        mContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MusicEntry entry = list.get(position);
                mPresenter.playMusic(entry);
                mOnQuitListener.onQuit();
            }
        });
        mHeadPlayAllView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.playAllMusic(artist);
                mOnQuitListener.onQuit();
            }
        });
        handleEmpty(list.isEmpty());
    }

    @Override
    public void refreshMusicOfAlbum(final Album album, final List<MusicEntry> list) {
        if (list.isEmpty()) {
            mListHeaderView.setVisibility(View.GONE);
            mDividerView.setVisibility(View.GONE);
        } else {
            mListHeaderView.setVisibility(View.VISIBLE);
            mDividerView.setVisibility(View.VISIBLE);
        }
        mHeadBackView.setText(album.getName());
        if (TextUtils.isEmpty(album.getName())) {
            mHeadBackView.setText(getActivity().getString(R.string.unknown_album_name));
        }
        if (mSecondaryAlbumListAdapter == null) {
            mSecondaryAlbumListAdapter = new SongListAdapter(getActivity(), list);
            mContentListView.setAdapter(mSecondaryAlbumListAdapter);
        } else {
            mSecondaryAlbumListAdapter.refreshData(list);
        }
        mHeadBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchListType(mSelectedListType);
                mPresenter.chooseList(mSelectedListType);
            }
        });
        mContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MusicEntry entry = list.get(position);
                mPresenter.playMusic(entry);
                mOnQuitListener.onQuit();
            }
        });
        mHeadPlayAllView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.playAllMusic(album);
                mOnQuitListener.onQuit();
            }
        });
        handleEmpty(list.isEmpty());
    }

    @Override
    public void notifyPlaying(boolean needScroll, MusicEntry entry) {
        if (mCurrentArtist != null) {
            if (mSecondaryArtistListAdapter != null) {
                mSecondaryArtistListAdapter.setCurrentMusic(entry);
            }
            return;
        }
        if (mCurrentAlbum != null) {
            if (mSecondaryAlbumListAdapter != null) {
                mSecondaryAlbumListAdapter.setCurrentMusic(entry);
            }
            return;
        }
        if (mSelectedListType == MusicListContract.ListType.SONG) {
            notifyPlayingSong(needScroll, entry);
        } else if (mSelectedListType == MusicListContract.ListType.ARTIST) {
            notifyPlayingArtist(needScroll, entry);
        } else {
            notifyPlayingAlbum(needScroll, entry);
        }
    }

    private void notifyPlayingSong(boolean needScroll, MusicEntry entry) {
        mSongListAdapter.setCurrentMusic(entry);
        if (needScroll) {
            for (int i = 0; i < mSongListAdapter.getCount(); i++) {
                MusicEntry musicEntry = mSongListAdapter.getItem(i);
                if (entry.getUri().equals(musicEntry.getUri())) {
                    scrollToPosition(i);
                    break;
                }
            }
        }
    }

    private void notifyPlayingArtist(boolean needScroll, MusicEntry entry) {
        if (mArtistListAdapter != null) {
            mArtistListAdapter.setArtist(entry.getArtist());
        }
        if (needScroll) {
            for (int i = 0; i < mArtistListAdapter.getCount(); i++) {
                Artist artist = mArtistListAdapter.getItem(i);
                if (entry.getArtist().getId() == artist.getId()) {
                    scrollToPosition(i);
                    break;
                }
            }
        }
    }

    private void notifyPlayingAlbum(boolean needScroll, MusicEntry entry) {
        if (mAlbumListAdapter != null) {
            mAlbumListAdapter.setCurrentAlbum(entry.getAlbum());
        }
        if (needScroll) {
            for (int i = 0; i < mAlbumListAdapter.getCount(); i++) {
                Album album = mAlbumListAdapter.getItem(i);
                if (entry.getAlbum().getId() == album.getId()) {
                    scrollToPosition(i);
                    break;
                }
            }
        }
    }

    private void handleEmpty(boolean empty) {
        if (empty) {
            mContentListView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mContentListView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    private void scrollToPosition(int position) {
        if (position - 2 >= 0) {
            mContentListView.setSelection(position - 2);
        } else {
            mContentListView.setSelection(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOnQuitListener = (DirectoryBrowserFragment.OnQuitListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnQuitListener");
        }
    }
}
