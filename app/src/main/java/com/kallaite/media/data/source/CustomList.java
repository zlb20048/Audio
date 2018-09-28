package com.kallaite.media.data.source;

import android.support.annotation.NonNull;

import com.kallaite.media.data.struct.MusicEntry;
import com.kallaite.media.browser.NodeHolder;

import java.util.Collection;
import java.util.List;

/**
 * Created by dengrui on 18-4-3.
 */

public class CustomList extends ForwardingList<MusicEntry>{

    private NodeHolder mNodeHolder = NodeHolder.getInstance();

    public CustomList(List<MusicEntry> list) {
        super(list);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends MusicEntry> c) {
        mNodeHolder.appendNodeChain(c);
        return super.addAll(c);
    }

    @Override
    public void clear() {
        mNodeHolder.clear();
        super.clear();
    }
}
