package com.coopsrc.oneplayer.core.playback;

import com.coopsrc.oneplayer.core.OnePlayer;

/**
 * @author tingkuo
 * <p>
 * Datetime: 2019-11-04 10:51
 */
public interface OnInfoListener {
    default boolean onInfo(OnePlayer player, int what, int extra) {
        return true;
    }
}
