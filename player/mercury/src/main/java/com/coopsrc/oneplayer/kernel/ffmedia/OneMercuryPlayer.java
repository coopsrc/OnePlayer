package com.coopsrc.oneplayer.kernel.ffmedia;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-07 16:20
 */
public class OneMercuryPlayer {

    static {
        System.loadLibrary("oneplayer");
    }

    public static native String ffmpegVersion();
}
