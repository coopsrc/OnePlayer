package com.coopsrc.oneplayer.kernel.ffmedia;

import com.coopsrc.oneplayer.core.PlayerLibraryInfo;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-07 16:20
 */
public class OneMercuryPlayer {

    static {
        PlayerLibraryInfo.registerModule("one.player.mercury");
    }

    static {
        System.loadLibrary("mercury");
    }

    public static native String ffmpegVersion();
}
