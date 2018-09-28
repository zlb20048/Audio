package com.kallaite.media.browser;

/**
 * Created by dengrui on 18-3-27.
 */

class DirectoryNode extends BaseNode{

    public DirectoryNode(String name, INode parent) {
        super(name, parent);
    }

    @Override
    public boolean isDirectoryNode() {
        return true;
    }
}
