package com.kallaite.media.util;

/**
 * Created by liuzixiang on 15-12-2.
 */
public class Utils
{
    /**
     * format the duration
     *
     * @param length current duration
     * @return hh:mm:ss11
     */
    public static String getDurationString(int length)
    {
        length /= 1000L;
        long minute = length / 60L;
        long hour = minute / 60L;
        long second = length % 60L;
        minute %= 60L;
        if (hour > 0)
        {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        }
        else
        {
            return String.format("%02d:%02d", minute, second);
        }
    }
}
