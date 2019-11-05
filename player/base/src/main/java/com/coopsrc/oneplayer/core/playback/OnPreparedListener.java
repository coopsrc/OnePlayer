package com.coopsrc.oneplayer.core.playback;

import com.coopsrc.oneplayer.core.OnePlayer;

/**
 * @author tingkuo
 * <p>
 * Datetime: 2019-11-04 10:52
 */
public interface OnPreparedListener {
    default void onPrepared(OnePlayer player) {
    }
}
