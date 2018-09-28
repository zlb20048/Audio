package com.kallaite.media.presenter;

import android.support.v4.util.Pools;

import com.kallaite.media.view.BaseView;

import java.util.List;

/**
 * Created by dengrui on 18-3-27.
 */

public interface DirectoryBrowserContract {

    enum ITEM_TYPE {
        DIR, FILE
    }

    class Item {

        private static final Pools.SynchronizedPool<Item> sPool = new Pools.SynchronizedPool<>(50);

        public DirectoryBrowserContract.ITEM_TYPE itemType;

        public Object itemEntry;

        public boolean isPlaying;

        private Item() {
            init();
        }
        private void init(){
            itemType = null;
            itemEntry = null;
            isPlaying = false;
        }

        public static Item obtain() {
            Item instance = sPool.acquire();
            if(instance != null){
                instance.init();
            }
            return (instance != null) ? instance : new Item();
        }
        public void recycle(){
            sPool.release(this);
        }

    }


    interface View extends BaseView<Presenter> {

        void refreshDirectory(List<String> list);

        void refreshContent(List<DirectoryBrowserContract.Item> list);

        void quit();

        void scrollToPosition(int position);

    }

    interface Presenter extends BasePresenter {

        void init();

        void selectDir(int count, int index);

        void selectContent(int index);

    }
}
