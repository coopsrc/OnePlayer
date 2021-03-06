package com.coopsrc.oneplayer.core.playback;

import com.coopsrc.oneplayer.core.OnePlayer;

/**
 * @author tingkuo
 * <p>
 * Datetime: 2019-11-04 10:49
 */
public interface OnBufferingUpdateListener {
    default void onBufferingUpdate(OnePlayer player, int percent) {
    }
}
