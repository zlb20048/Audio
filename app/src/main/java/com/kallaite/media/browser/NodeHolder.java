package com.kallaite.media.browser;

import android.support.annotation.NonNull;

import com.kallaite.media.data.struct.MusicEntry;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dengrui on 18-4-3.
 */

public class NodeHolder {

    private static NodeHolder sInstance = new NodeHolder();

    private INode mRootNode = BaseNode.generateRootNode();

    private Map<String, INode> map = new HashMap<>();

    private INodeListener mNodeListener;

    private NodeHolder() {

    }

    public static NodeHolder getInstance() {
        return sInstance;
    }

    public synchronized INode getRootNode() {
        return mRootNode;
    }

    public void setNodeListener(INodeListener listener) {
        this.mNodeListener = listener;
    }

    public synchronized void clear() {
        mRootNode.getChildrenNodes().clear();
        map.clear();
        if (mNodeListener != null) {
            mNodeListener.onClearNode();
        }
    }

    public INode getNode(MusicEntry entry) {
        if (entry == null) {
            return null;
        }
        return map.get(entry.getTrack().getPath());
    }

    public synchronized void appendNodeChain(@NonNull Collection<? extends MusicEntry> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        boolean reload = mRootNode.getChildrenNodes().isEmpty();
        for (MusicEntry entry : list) {
            String path = entry.getTrack().getPath();
            String[] subPath = path.split("/");
            INode parentNode = null;
            for (int i = 1; i < subPath.length; i++) {
                if (i == 1) {
                    parentNode = mRootNode;
                }
                boolean isFile = (i == subPath.length - 1);
                parentNode = addToNode(parentNode, subPath[i], entry, isFile);
            }
        }
        if (mNodeListener != null) {
            if (reload) {
                mNodeListener.onReloadNode();
            } else {
                mNodeListener.onMoreNodeLoaded();
            }
        }

    }

    public synchronized void appendNodeChain(MusicEntry musicEntry) {
        appendNodeChain(Arrays.asList(musicEntry));
    }

    private synchronized INode addToNode(INode parent, String path,
                                         MusicEntry entry, boolean isFile) {
        List<INode> children = parent.getChildrenNodes();
        INode node = null;
        boolean exist = false;
        for (INode child : children) {
            if (path.equals(child.getName())) {
                node = child;
                exist = true;
                break;
            }
        }
        if (!exist) {
            if (isFile) {
                node = new FileNode(path, parent, entry);
            } else {
                node = new DirectoryNode(path, parent);
            }
            appendPath(node, parent);
            parent.addChildren(node);
        }
        return node;
    }

    private synchronized void appendPath(INode node, INode parentNode) {
        if (node != null && parentNode != null) {
            String parentPath = parentNode.getPath();
            StringBuffer sb = new StringBuffer();
            sb.append(parentPath + node.getName());
            if (node.isDirectoryNode()) {
                sb.append("/");
            }
            node.setPath(sb.toString());
            map.put(sb.toString(), node);
        }
    }


}
