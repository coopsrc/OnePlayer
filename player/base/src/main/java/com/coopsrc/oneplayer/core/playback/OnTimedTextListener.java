package com.coopsrc.oneplayer.core.playback;

import com.coopsrc.oneplayer.core.OnePlayer;
import com.coopsrc.oneplayer.core.misc.ITimedText;

/**
 * @author tingkuo
 * <p>
 * Datetime: 2019-11-04 10:52
 */
public interface OnTimedTextListener {
    default void onTimedText(OnePlayer player, ITimedText text) {
    }
}
