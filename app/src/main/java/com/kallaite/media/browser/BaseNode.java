package com.kallaite.media.browser;

import com.kallaite.media.data.struct.MusicEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by dengrui on 18-3-27.
 */

class BaseNode implements INode {

    private final String name;

    private final INode parent;

    private List<INode> childrenList = new ArrayList<>();

    private MyComparator myComparable = new MyComparator();

    private String path;

    public static final String ROOT_PATH = "/";

    public BaseNode(String name, INode parent) {
        this.name = name;
        this.parent = parent;
    }

    public static BaseNode generateRootNode() {
        BaseNode node = new BaseNode(ROOT_PATH, null);
        node.path = ROOT_PATH;
        return node;
    }

    @Override
    public synchronized void addChildren(INode children) {
        childrenList.add(children);
        Collections.sort(childrenList, myComparable);
    }

    @Override
    public MusicEntry getAssociatedEntry() {
        return null;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isDirectoryNode() {
        return false;
    }

    @Override
    public synchronized List<INode> getChildrenNodes() {
        return childrenList;
    }

    @Override
    public INode getParentNode() {
        return parent;
    }

    public static class MyComparator implements Comparator<INode> {

        @Override
        public int compare(INode o1, INode o2) {
            if (o1.isDirectoryNode() && !o2.isDirectoryNode()) {
                return -1;
            }
            if (!o1.isDirectoryNode() && o2.isDirectoryNode()) {
                return 1;
            }
            return 0;
        }
    }
}
