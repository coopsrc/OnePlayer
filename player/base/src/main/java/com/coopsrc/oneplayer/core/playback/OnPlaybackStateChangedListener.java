package com.coopsrc.oneplayer.core.playback;

import com.coopsrc.oneplayer.core.OnePlayer;

/**
 * @author tingkuo
 * <p>
 * Datetime: 2019-11-04 10:53
 */
public interface OnPlaybackStateChangedListener {
    default void onPlaybackStateChanged(@OnePlayer.State int playbackState) {
    }
}
