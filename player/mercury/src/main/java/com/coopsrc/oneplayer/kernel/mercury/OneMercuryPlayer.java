package com.coopsrc.oneplayer.kernel.mercury;

import com.coopsrc.oneplayer.core.PlayerLibraryInfo;
import com.coopsrc.oneplayer.core.utils.PlayerLogger;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-07 16:20
 */
public class OneMercuryPlayer {
    private static final String TAG = "OneMercuryPlayer";

    static {
        PlayerLibraryInfo.registerModule("one.player.mercury");
    }

    static {
        System.loadLibrary("mercury");
    }

    public static native String ffmpegVersion();

    public static void dump() {
        PlayerLogger.w(TAG, FFmpegConfig.getNativeFFmpegVersion());
        PlayerLogger.w(TAG, FFmpegConfig.getNativeVersion());
    }
}
