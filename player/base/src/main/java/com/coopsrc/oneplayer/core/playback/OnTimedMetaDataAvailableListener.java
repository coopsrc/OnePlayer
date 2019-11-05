package com.coopsrc.oneplayer.core.playback;

import com.coopsrc.oneplayer.core.OnePlayer;
import com.coopsrc.oneplayer.core.misc.ITimedMetadata;

/**
 * @author tingkuo
 * <p>
 * Datetime: 2019-11-04 10:53
 */
public interface OnTimedMetaDataAvailableListener {
    default void onTimedMetaDataAvailable(OnePlayer player, ITimedMetadata timedMetadata) {
    }
}
