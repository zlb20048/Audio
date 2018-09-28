/*
 * 文 件 名:  MusicShuffle.java
 * 版    权:  SmartAuto Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  beam
 * 修改时间:  2014-10-28
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.kallaite.media.data.struct;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @author beam
 * @version [版本号, 2014-10-28]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public enum MusicPlayMode {
    LOOP_NORMAL {
        @Override
        public MusicPlayMode next() {
            return SHUFFLE;
        }
    }, SHUFFLE {
        @Override
        public MusicPlayMode next() {
            return LOOP_ONE;
        }
    }, LOOP_ONE {
        @Override
        public MusicPlayMode next() {
            return LOOP_NORMAL;
        }
    };

    public abstract MusicPlayMode next();
}
