package com.kallaite.media.data.struct;

import java.util.List;

/**
 * Created by dengrui on 18-3-28.
 */

public interface IDataChangedNotifier {

    /**
     * 通知重新加载所有的音频数据,list中包含所有音频数据
     */
    void notifyReloadMediaData(List<MusicEntry> list);

    /**
     * 通知新的音频数据到来，此时新的数据已经加载完成,list中包含了加载到的所有音频数据
     */
    void notifyNewDataComing(List<MusicEntry> list);

}
