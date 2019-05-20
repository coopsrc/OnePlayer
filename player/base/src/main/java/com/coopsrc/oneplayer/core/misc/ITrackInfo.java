package com.coopsrc.oneplayer.core.misc;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-10 10:18
 */
public interface ITrackInfo {
    public static final int MEDIA_TRACK_TYPE_UNKNOWN = 0;
    public static final int MEDIA_TRACK_TYPE_VIDEO = 1;
    public static final int MEDIA_TRACK_TYPE_AUDIO = 2;
    public static final int MEDIA_TRACK_TYPE_TIMEDTEXT = 3;
    public static final int MEDIA_TRACK_TYPE_SUBTITLE = 4;
    public static final int MEDIA_TRACK_TYPE_METADATA = 5;

    IMediaFormat getFormat();

    String getLanguage();

    int getTrackType();

    String getInfoInline();
}
