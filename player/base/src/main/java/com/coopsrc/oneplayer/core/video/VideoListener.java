package com.coopsrc.oneplayer.core.video;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-08-08 11:03
 */
public interface VideoListener {
    default void onVideoSizeChanged(int width, int height, int rotationDegrees, float pixelRatio) {
    }

    default void onSurfaceSizeChanged(int width, int height) {
    }

    default void onRenderedFirstFrame() {
    }
}
