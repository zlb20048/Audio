package com.kallaite.media.browser;

import com.kallaite.media.data.struct.MusicEntry;

/**
 * Created by dengrui on 18-3-27.
 */

class FileNode extends BaseNode {

    private MusicEntry musicEntry;

    public FileNode(String name, INode parent, MusicEntry entry) {
        super(name, parent);
        musicEntry = entry;
    }

    @Override
    public MusicEntry getAssociatedEntry() {
        return musicEntry;
    }

    @Override
    public boolean isDirectoryNode() {
        return false;
    }
}
