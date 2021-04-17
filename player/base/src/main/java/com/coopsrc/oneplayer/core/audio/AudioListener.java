package com.coopsrc.oneplayer.core.audio;

import android.media.AudioAttributes;

import com.coopsrc.oneplayer.core.OnePlayer;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-08-08 11:01
 */
public interface AudioListener {
    default void onAudioSessionIdChanged(OnePlayer player, int audioSessionId) {
    }

    default void onAudioAttributesChanged(OnePlayer player, AudioAttributes audioAttributes) {
    }

    default void onVolumeChanged(OnePlayer player, float audioVolume) {
    }
}
