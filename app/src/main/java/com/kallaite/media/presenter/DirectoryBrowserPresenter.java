package com.kallaite.media.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.kallaite.media.data.struct.MusicEntry;
import com.kallaite.media.browser.INode;
import com.kallaite.media.browser.INodeListener;
import com.kallaite.media.browser.NodeHolder;
import com.kallaite.media.engine.DefaultPlayingStateListener;
import com.kallaite.media.service.MusicServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengrui on 18-3-27.
 */

public class DirectoryBrowserPresenter extends DefaultPlayingStateListener
        implements DirectoryBrowserContract.Presenter,INodeListener {

    private final DirectoryBrowserContract.View mView;

    private Context mContext;

    private INode mRootNode;

    private INode mSelectedNode;

    private MusicServiceImpl musicService;

    private NodeHolder mNodeHolder = NodeHolder.getInstance();

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private boolean mIdleState = false;

    public DirectoryBrowserPresenter(Context context,
                                     @NonNull DirectoryBrowserContract.View view) {
        mContext = context;
        mView = view;
        view.setPresenter(this);
        musicService = MusicServiceImpl.getInstance(context);
    }

    @Override
    public void subscribe() {
        musicService.registerPlayingStateListener(this);
        mNodeHolder.setNodeListener(this);
    }

    @Override
    public void unsubscribe() {
        musicService.unregisterPlayingStateListener(this);
        mNodeHolder.setNodeListener(null);
    }

    private INode getRootNode() {
        INode node = mNodeHolder.getRootNode();
        if (node.getChildrenNodes().size() > 0) {
            //skip root("/") node
            node = node.getChildrenNodes().get(0);
        }
        return node;
    }

    private INode getSelectedNode(MusicEntry playingMusic) {
        if (playingMusic == null) {
            return null;
        }
        INode node = mNodeHolder.getNode(playingMusic);
        if(node == null){
            return null;
        }
        return node.getParentNode();
    }

    private List<String> computeDirectoryList() {
        List<String> list = new ArrayList<>();
        INode node = mSelectedNode;
        while (node != mRootNode) {
            list.add(0, node.getName());
            node = node.getParentNode();
        }
        list.add(0, mRootNode.getName());
        return list;
    }

    @Override
    public void init() {
        MusicEntry playingMusic = musicService.getPlayingMusic();
        mRootNode = getRootNode();
        mSelectedNode = getSelectedNode(playingMusic);
        if (mSelectedNode == null) {
            mSelectedNode = mRootNode;
        }
        dispatchRefresh();
        scrollToPlayingPosition();
    }

    private void scrollToPlayingPosition() {
        MusicEntry playingMusic = musicService.getPlayingMusic();
        if (playingMusic != null) {
            for (int i = 0; i < mSelectedNode.getChildrenNodes().size(); i++) {
                INode node = mSelectedNode.getChildrenNodes().get(i);
                if (node.getAssociatedEntry() != null &&
                        node.getAssociatedEntry().equals(playingMusic)) {
                    mView.scrollToPosition(i);
                    break;
                }
            }
        }
    }

    @Override
    public void selectDir(int count, int index) {
        if (index < 0 || index >= count) {
            return;
        }
        if (count - 1 == index) {
            return;
        }
        if (index == 0) {
            mSelectedNode = mRootNode;
            dispatchRefresh();
            return;
        }
        INode node = mSelectedNode;
        for (int i = 0; i < (count - index - 1); i++) {
            node = node.getParentNode();
        }
        mSelectedNode = node;
        dispatchRefresh();
    }

    @Override
    public void selectContent(int index) {
        INode node = getSelectedNodeChild(index);
        if (node.isDirectoryNode()) {
            enterNode(getSelectedNodeChild(index));
        } else {
            musicService.play(node.getAssociatedEntry());
            mView.quit();
        }
    }

    private void enterNode(INode node) {
        mSelectedNode = node;
        dispatchRefresh();
    }

    private void dispatchRefresh() {
        mView.refreshDirectory(computeDirectoryList());
        refreshNodeContent();
    }

    private INode getSelectedNodeChild(int childIndex) {
        INode node;
        List<INode> children = mSelectedNode.getChildrenNodes();
        if (childIndex < 0 || childIndex >= children.size()) {
            node = null;
        } else {
            node = children.get(childIndex);
        }
        return node;
    }

    private void refreshNodeContent() {
        if (mSelectedNode.getChildrenNodes().isEmpty()) {
            mView.refreshContent(new ArrayList<DirectoryBrowserContract.Item>());
        } else {
            List<DirectoryBrowserContract.Item> list = new ArrayList<>();
            MusicEntry playingMusic = musicService.getPlayingMusic();
            for (INode node : mSelectedNode.getChildrenNodes()) {
                DirectoryBrowserContract.Item item = DirectoryBrowserContract.Item.obtain();
                if (node.isDirectoryNode()) {
                    item.itemType = DirectoryBrowserContract.ITEM_TYPE.DIR;
                    item.itemEntry = node.getName();
                    if (playingMusic != null &&
                            playingMusic.getTrack().getPath().startsWith(node.getPath())) {
                        item.isPlaying = true;
                    }
                } else {
                    item.itemType = DirectoryBrowserContract.ITEM_TYPE.FILE;
                    MusicEntry entry = node.getAssociatedEntry();
                    item.itemEntry = entry;
                    if (playingMusic != null) {
                        if (entry.getTrack().getPath().equals(playingMusic.getTrack().getPath())) {
                            item.isPlaying = true;
                        }
                    }
                }
                list.add(item);
            }
            mView.refreshContent(list);
        }
    }

    @Override
    public void notifyPlaying(MusicEntry entry) {
        if (mIdleState) {
            mSelectedNode = getSelectedNode(entry);
            mIdleState = false;
        } else {
            refreshNodeContent();
            scrollToPlayingPosition();
        }
    }

    @Override
    public void onClearNode() {
        returnToRootNode();
    }

    @Override
    public void onReloadNode() {
        mIdleState = true;
        returnToRootNode();
    }

    private void returnToRootNode() {
        mRootNode = getRootNode();
        mSelectedNode = mRootNode;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                dispatchRefresh();
            }
        });
    }

    @Override
    public void onMoreNodeLoaded() {
        mIdleState = false;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                dispatchRefresh();
            }
        });
    }
}
