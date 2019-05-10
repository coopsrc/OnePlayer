package com.coopsrc.oneplayer.media.misc;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;

import com.coopsrc.oneplayer.core.misc.IMediaFormat;
import com.coopsrc.oneplayer.core.misc.ITrackInfo;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-10 10:41
 */
public class AndroidTrackInfo implements ITrackInfo {
    private final MediaPlayer.TrackInfo mTrackInfo;

    public static AndroidTrackInfo[] fromMediaPlayer(MediaPlayer mp) {
        if (mp != null) {
            return fromTrackInfo(mp.getTrackInfo());
        }
        return null;
    }

    private static AndroidTrackInfo[] fromTrackInfo(MediaPlayer.TrackInfo[] trackInfos) {

        if (trackInfos != null) {
            AndroidTrackInfo[] androidTrackInfo = new AndroidTrackInfo[trackInfos.length];
            for (int i = 0; i < trackInfos.length; i++) {
                androidTrackInfo[i] = new AndroidTrackInfo(trackInfos[i]);
            }
            return androidTrackInfo;
        }

        return null;
    }

    public AndroidTrackInfo(MediaPlayer.TrackInfo trackInfo) {
        mTrackInfo = trackInfo;
    }

    @Override
    public IMediaFormat getFormat() {
        if (mTrackInfo != null && mTrackInfo.getFormat() != null) {
            return new AndroidMediaFormat(mTrackInfo.getFormat());
        }
        return null;
    }

    @Override
    public String getLanguage() {
        if (mTrackInfo != null) {
            return mTrackInfo.getLanguage();
        }
        return null;
    }

    @Override
    public int getTrackType() {
        if (mTrackInfo != null) {
            return mTrackInfo.getTrackType();
        }
        return MEDIA_TRACK_TYPE_UNKNOWN;
    }

    @Override
    public String getInfoInline() {
        return null;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
