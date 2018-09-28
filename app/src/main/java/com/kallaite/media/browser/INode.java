package com.kallaite.media.browser;

import com.kallaite.media.data.struct.MusicEntry;

import java.util.List;

/**
 * Created by dengrui on 18-3-27.
 */

public interface INode {

    String getName();

    boolean isDirectoryNode();

    List<INode> getChildrenNodes();

    INode getParentNode();

    void addChildren(INode children);

    MusicEntry getAssociatedEntry();

    String getPath();

    void setPath(String path);
}
