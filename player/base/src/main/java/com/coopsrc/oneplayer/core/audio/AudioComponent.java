package com.coopsrc.oneplayer.core.audio;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-08-08 15:06
 */
public interface AudioComponent {

    void setAudioSessionId(int sessionId);

    int getAudioSessionId();

    void setVolume(float audioVolume);

    float getVolume();
}
