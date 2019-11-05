package com.coopsrc.oneplayer.core.video;

import com.coopsrc.oneplayer.core.OnePlayer;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-08-08 11:03
 */
public interface VideoListener {
    default void onVideoSizeChanged(OnePlayer player, int width, int height, int rotationDegrees, float pixelRatio) {
    }

    default void onSurfaceSizeChanged(OnePlayer player, int width, int height) {
    }

    default void onRenderedFirstFrame(OnePlayer player) {
    }
}
