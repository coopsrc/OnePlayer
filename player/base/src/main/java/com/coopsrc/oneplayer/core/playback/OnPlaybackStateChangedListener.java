package com.coopsrc.oneplayer.core.playback;

/**
 * @author tingkuo
 * <p>
 * Datetime: 2019-11-04 10:53
 */
public interface OnPlaybackStateChangedListener {
    default void onPlaybackStateChanged(boolean playWhenReady, int playbackState) {
    }
}
