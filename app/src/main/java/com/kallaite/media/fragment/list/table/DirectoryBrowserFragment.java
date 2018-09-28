package com.kallaite.media.fragment.list.table;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kallaite.media.R;
import com.kallaite.media.fragment.adapter.DirectoryAdapter;
import com.kallaite.media.fragment.adapter.DirectoryContentAdapter;
import com.kallaite.media.presenter.DirectoryBrowserContract;

import java.util.List;

public class DirectoryBrowserFragment extends Fragment
        implements DirectoryBrowserContract.View {
    private final static String TAG = DirectoryBrowserFragment.class.getSimpleName();

    private DirectoryBrowserContract.Presenter mPresenter;

    private ListView mDirListView;

    private ListView mContentListView;

    private DirectoryAdapter mDirListAdapter;

    private DirectoryContentAdapter mContentAdapter;

    private OnQuitListener mOnQuitListener;

    private View mEmptyView;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_list_common, container, false);
        mDirListView = (ListView) view.findViewById(R.id.left_listview);
        mContentListView = (ListView) view.findViewById(R.id.content_listview);
        mEmptyView = view.findViewById(R.id.empty);
        mPresenter.init();
        return view;
    }

    @Override
    public void setPresenter(DirectoryBrowserContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void refreshDirectory(List<String> list) {
        if (mDirListAdapter == null) {
            mDirListAdapter = new DirectoryAdapter(getActivity(), list);
            mDirListView.setAdapter(mDirListAdapter);
            mDirListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mPresenter.selectDir(mDirListAdapter.getCount(), position);
                }
            });
        } else {
            mDirListAdapter.refreshData(list);
        }
    }

    @Override
    public void refreshContent(List<DirectoryBrowserContract.Item> list) {
        if (mContentAdapter == null) {
            mContentAdapter = new DirectoryContentAdapter(getActivity(), list);
            mContentListView.setAdapter(mContentAdapter);
            mContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mPresenter.selectContent(position);
                }
            });
        } else {
            mContentAdapter.refreshData(list);
        }
        if (list.isEmpty()) {
            mContentListView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mContentListView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void quit() {
        if (mOnQuitListener != null) {
            mOnQuitListener.onQuit();
        }
    }

    @Override
    public void scrollToPosition(final int position) {
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
            mOnQuitListener = (OnQuitListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnQuitListener");
        }
    }

    public interface OnQuitListener {
        void onQuit();
    }
}
