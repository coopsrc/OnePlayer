package com.coopsrc.oneplayer.core.utils;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-16 11:58
 */
public class PlayerUtils {
    private static final String TAG = "PlayerUtils";

    public static String formatPlayingTime(long timeStamp) {

        String timeStr = "00:00";

        if (timeStamp > 0) {

            long hours = TimeUnit.MILLISECONDS.toHours(timeStamp);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(timeStamp) % 60;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(timeStamp) % 60;
            if (hours > 0) {
                timeStr = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
            } else {
                timeStr = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
            }
        }

        LogUtils.i(TAG, "formatPlayingTime: timeStamp=" + timeStamp + " => " + timeStr);

        return timeStr;
    }
}
