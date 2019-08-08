package com.coopsrc.oneplayer.core.audio;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-08-08 11:01
 */
public interface AudioListener {
    default void onAudioSessionId(int audioSessionId) {
    }

    default void onVolumeChanged(float audioVolume) {
    }
}
