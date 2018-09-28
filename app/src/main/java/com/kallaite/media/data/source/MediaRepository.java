package com.kallaite.media.data.source;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kallaite.media.data.struct.Album;
import com.kallaite.media.data.struct.Artist;
import com.kallaite.media.data.struct.IDataChangedNotifier;
import com.kallaite.media.data.struct.MediaStorage;
import com.kallaite.media.data.struct.MusicEntry;
import com.kallaite.media.util.WLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by dengrui on 18-3-23.
 */

public class MediaRepository implements MediaDataSource {

    @Nullable
    private static MediaRepository INSTANCE = null;

    private final MediaDataSource mExternalMediaDataSource;

    private final MediaDataSource mInternalMediaDataSource;

    private List<PriorityDataChangedNotifier> mNotifierList = new ArrayList<>();

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private NotifierComparator mNotifierComparator = new NotifierComparator();

    private MediaRepository(@NonNull MediaDataSource external, @NonNull MediaDataSource internal) {
        mExternalMediaDataSource = checkNotNull(external);
        mInternalMediaDataSource = checkNotNull(internal);
    }

    public static MediaRepository getInstance(@NonNull MediaDataSource external,
                                              @NonNull MediaDataSource internal) {
        if (INSTANCE == null) {
            INSTANCE = new MediaRepository(external, internal);
        }
        return INSTANCE;
    }

    public enum Priority {
        MAX, MEDIUM, NORMAL
    }

    static class PriorityDataChangedNotifier implements IDataChangedNotifier {

        private final int priority;

        private final IDataChangedNotifier notifier;

        public PriorityDataChangedNotifier(int priority, IDataChangedNotifier notifier) {
            this.priority = priority;
            this.notifier = notifier;
        }

        public int getPriority() {
            return priority;
        }

        public IDataChangedNotifier getNotifier() {
            return notifier;
        }

        @Override
        public void notifyReloadMediaData(List<MusicEntry> list) {
            this.notifier.notifyReloadMediaData(list);
        }

        @Override
        public void notifyNewDataComing(List<MusicEntry> list) {
            this.notifier.notifyNewDataComing(list);
        }
    }

    static class NotifierComparator implements Comparator<PriorityDataChangedNotifier> {

        @Override
        public int compare(PriorityDataChangedNotifier o1, PriorityDataChangedNotifier o2) {
            return o1.getPriority() - o2.getPriority();
        }
    }

    public synchronized void addDataChangedListener(int priority, IDataChangedNotifier notifier) {
        for (PriorityDataChangedNotifier element : mNotifierList) {
            if (element.getNotifier().equals(notifier)) {
                WLog.i("addDataChangedListener: notifier already added");
                return;
            }
        }
        mNotifierList.add(new PriorityDataChangedNotifier(priority, notifier));
        Collections.sort(mNotifierList, mNotifierComparator);
    }

    public synchronized void removeDataChangedListener(IDataChangedNotifier notifier) {
        PriorityDataChangedNotifier existNotifier = null;
        if (notifier != null) {
            for (PriorityDataChangedNotifier element : mNotifierList) {
                if (element.getNotifier().equals(notifier)) {
                    existNotifier = element;
                    break;
                }
            }
            if (existNotifier != null) {
                mNotifierList.remove(existNotifier);
            }
        }
    }

    @Override
    public synchronized List<MusicEntry> getMediaData() {
        List<MusicEntry> list = new ArrayList<>();
        list.addAll(mInternalMediaDataSource.getMediaData());
        list.addAll(mExternalMediaDataSource.getMediaData());
        return list;
    }

    @Override
    public List<Artist> getArtistData() {
        List<Artist> list = new ArrayList<>();
        list.addAll(mInternalMediaDataSource.getArtistData());
        list.addAll(mExternalMediaDataSource.getArtistData());
        return list;
    }

    @Override
    public List<Album> getAlbumData() {
        List<Album> list = new ArrayList<>();
        list.addAll(mInternalMediaDataSource.getAlbumData());
        list.addAll(mExternalMediaDataSource.getAlbumData());
        return list;
    }

    @Override
    public List<MusicEntry> getMediaDataByArtist(int artistID) {
        List<MusicEntry> list = new ArrayList<>();
        list.addAll(getMediaDataByArtist(MediaStorage.INTERNAL, artistID));
        list.addAll(getMediaDataByArtist(MediaStorage.USB, artistID));
        return list;
    }

    @Override
    public List<MusicEntry> getMediaDataByAlbum(int albumID) {
        List<MusicEntry> list = new ArrayList<>();
        list.addAll(getMediaDataByAlbum(MediaStorage.INTERNAL, albumID));
        list.addAll(getMediaDataByAlbum(MediaStorage.USB, albumID));
        return list;
    }

    public List<MusicEntry> getMediaDataByArtist(MediaStorage storage, int artistID) {
        List<MusicEntry> list = new ArrayList<>();
        if (storage == MediaStorage.INTERNAL) {
            list.addAll(mInternalMediaDataSource.getMediaDataByArtist(artistID));
        } else if (storage == MediaStorage.USB) {
            list.addAll(mExternalMediaDataSource.getMediaDataByArtist(artistID));
        }
        return list;
    }

    public List<MusicEntry> getMediaDataByAlbum(MediaStorage storage, int albumID) {
        List<MusicEntry> list = new ArrayList<>();
        if (storage == MediaStorage.INTERNAL) {
            list.addAll(mInternalMediaDataSource.getMediaDataByAlbum(albumID));
        } else if (storage == MediaStorage.USB) {
            list.addAll(mExternalMediaDataSource.getMediaDataByAlbum(albumID));
        }
        return list;
    }


    @Override
    public void dump() {
        mInternalMediaDataSource.dump();
        mExternalMediaDataSource.dump();
    }

    @Override
    public synchronized List<MusicEntry> reloadMediaData() {
        final List<MusicEntry> list = new ArrayList<>();
        list.addAll(mInternalMediaDataSource.getMediaData());
        list.addAll(mExternalMediaDataSource.reloadMediaData());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (IDataChangedNotifier notifier : mNotifierList) {
                    notifier.notifyReloadMediaData(list);
                }
            }
        });
        return list;

    }

    @Override
    public synchronized List<MusicEntry> requestMoreData() {
        final List<MusicEntry> list = new ArrayList<>();
        list.addAll(mInternalMediaDataSource.getMediaData());
        list.addAll(mExternalMediaDataSource.requestMoreData());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (IDataChangedNotifier notifier : mNotifierList) {
                    notifier.notifyNewDataComing(list);
                }
            }
        });
        return list;
    }


}
