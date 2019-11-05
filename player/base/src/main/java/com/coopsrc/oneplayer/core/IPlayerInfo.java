package com.coopsrc.oneplayer.core;

import androidx.annotation.IntDef;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-09 16:21
 */
interface IPlayerInfo {
    public static final int CALL_COMPLETED_ATTACH_AUX_EFFECT = 1;
    public static final int CALL_COMPLETED_CLEAR_NEXT_DATA_SOURCES = 30;
    public static final int CALL_COMPLETED_DESELECT_TRACK = 2;
    public static final int CALL_COMPLETED_LOOP_CURRENT = 3;
    public static final int CALL_COMPLETED_PAUSE = 4;
    public static final int CALL_COMPLETED_PLAY = 5;
    public static final int CALL_COMPLETED_PREPARE = 6;
    public static final int CALL_COMPLETED_SEEK_TO = 14;
    public static final int CALL_COMPLETED_SELECT_TRACK = 15;
    public static final int CALL_COMPLETED_SET_AUDIO_ATTRIBUTES = 16;
    public static final int CALL_COMPLETED_SET_AUDIO_SESSION_ID = 17;
    public static final int CALL_COMPLETED_SET_AUX_EFFECT_SEND_LEVEL = 18;
    public static final int CALL_COMPLETED_SET_DATA_SOURCE = 19;
    public static final int CALL_COMPLETED_SET_DISPLAY = 33;
    public static final int CALL_COMPLETED_SET_NEXT_DATA_SOURCE = 22;
    public static final int CALL_COMPLETED_SET_NEXT_DATA_SOURCES = 23;
    public static final int CALL_COMPLETED_SET_PLAYBACK_PARAMS = 24;
    public static final int CALL_COMPLETED_SET_PLAYER_VOLUME = 26;
    public static final int CALL_COMPLETED_SET_SCREEN_ON_WHILE_PLAYING = 35;
    public static final int CALL_COMPLETED_SET_SURFACE = 27;
    public static final int CALL_COMPLETED_SET_SYNC_PARAMS = 28;
    public static final int CALL_COMPLETED_SET_WAKE_LOCK = 34;
    public static final int CALL_COMPLETED_SKIP_TO_NEXT = 29;
    public static final int CALL_STATUS_BAD_VALUE = 2;
    public static final int CALL_STATUS_ERROR_IO = 4;
    public static final int CALL_STATUS_ERROR_UNKNOWN = -2147483648;
    public static final int CALL_STATUS_INVALID_OPERATION = 1;
    public static final int CALL_STATUS_NO_DRM_SCHEME = 6;
    public static final int CALL_STATUS_NO_ERROR = 0;
    public static final int CALL_STATUS_PERMISSION_DENIED = 3;
    public static final int CALL_STATUS_SKIPPED = 5;
    public static final int MEDIA_ERROR_IO = -1004;
    public static final int MEDIA_ERROR_MALFORMED = -1007;
    public static final int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;
    public static final int MEDIA_ERROR_TIMED_OUT = -110;
    public static final int MEDIA_ERROR_UNKNOWN = 1;
    public static final int MEDIA_ERROR_UNSUPPORTED = -1010;
    public static final int MEDIA_INFO_AUDIO_NOT_PLAYING = 804;
    public static final int MEDIA_INFO_AUDIO_RENDERING_START = 4;
    public static final int MEDIA_INFO_BAD_INTERLEAVING = 800;
    public static final int MEDIA_INFO_BUFFERING_END = 702;
    public static final int MEDIA_INFO_BUFFERING_START = 701;
    public static final int MEDIA_INFO_BUFFERING_UPDATE = 704;
    public static final int MEDIA_INFO_DATA_SOURCE_END = 5;
    public static final int MEDIA_INFO_DATA_SOURCE_LIST_END = 6;
    public static final int MEDIA_INFO_DATA_SOURCE_REPEAT = 7;
    public static final int MEDIA_INFO_DATA_SOURCE_START = 2;
    public static final int MEDIA_INFO_METADATA_UPDATE = 802;
    public static final int MEDIA_INFO_NOT_SEEKABLE = 801;
    public static final int MEDIA_INFO_PREPARED = 100;
    public static final int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902;
    public static final int MEDIA_INFO_UNKNOWN = 1;
    public static final int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901;
    public static final int MEDIA_INFO_VIDEO_NOT_PLAYING = 805;
    public static final int MEDIA_INFO_VIDEO_RENDERING_START = 3;
    public static final int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700;
    public static final int STATE_IDLE = 1001;
    public static final int STATE_PREPARED = 1002;
    public static final int STATE_PAUSED = 1003;
    public static final int STATE_PLAYING = 1004;
    public static final int PLAYER_STATE_ERROR = 1005;
    public static final int STATE_BUFFERING = 1006;
    public static final int STATE_ENDED = 1007;
    public static final int PREPARE_DRM_STATUS_KEY_EXCHANGE_ERROR = 7;
    public static final int PREPARE_DRM_STATUS_PREPARATION_ERROR = 3;
    public static final int PREPARE_DRM_STATUS_PROVISIONING_NETWORK_ERROR = 1;
    public static final int PREPARE_DRM_STATUS_PROVISIONING_SERVER_ERROR = 2;
    public static final int PREPARE_DRM_STATUS_RESOURCE_BUSY = 5;
    public static final int PREPARE_DRM_STATUS_RESTORE_ERROR = 6;
    public static final int PREPARE_DRM_STATUS_SUCCESS = 0;
    public static final int PREPARE_DRM_STATUS_UNSUPPORTED_SCHEME = 4;
    public static final int SEEK_CLOSEST = 3;
    public static final int SEEK_CLOSEST_SYNC = 2;
    public static final int SEEK_NEXT_SYNC = 1;
    public static final int SEEK_PREVIOUS_SYNC = 0;


    public static final int ROTATION_0 = 0;
    public static final int ROTATION_90 = 90;
    public static final int ROTATION_180 = 180;
    public static final int ROTATION_270 = 270;
    public static final float PIXEL_RATIO_1 = 1.0f;


    /**
     * Reasons for timeline and/or manifest changes. One of {@link #TIMELINE_CHANGE_REASON_PREPARED},
     * {@link #TIMELINE_CHANGE_REASON_RESET} or {@link #TIMELINE_CHANGE_REASON_DYNAMIC}.
     */
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            TIMELINE_CHANGE_REASON_PREPARED,
            TIMELINE_CHANGE_REASON_RESET,
            TIMELINE_CHANGE_REASON_DYNAMIC
    })
    @interface TimelineChangeReason {
    }

    /**
     * Timeline and manifest changed as a result of a player initialization with new media.
     */
    int TIMELINE_CHANGE_REASON_PREPARED = 0;
    /**
     * Timeline and manifest changed as a result of a player reset.
     */
    int TIMELINE_CHANGE_REASON_RESET = 1;
    /**
     * Timeline or manifest changed as a result of an dynamic update introduced by the played media.
     */
    int TIMELINE_CHANGE_REASON_DYNAMIC = 2;


    /**
     * Reasons for position discontinuities. One of {@link #DISCONTINUITY_REASON_PERIOD_TRANSITION},
     * {@link #DISCONTINUITY_REASON_SEEK}, {@link #DISCONTINUITY_REASON_SEEK_ADJUSTMENT}, {@link
     * #DISCONTINUITY_REASON_AD_INSERTION} or {@link #DISCONTINUITY_REASON_INTERNAL}.
     */
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            DISCONTINUITY_REASON_PERIOD_TRANSITION,
            DISCONTINUITY_REASON_SEEK,
            DISCONTINUITY_REASON_SEEK_ADJUSTMENT,
            DISCONTINUITY_REASON_AD_INSERTION,
            DISCONTINUITY_REASON_INTERNAL
    })
    @interface DiscontinuityReason {
    }

    /**
     * Automatic playback transition from one period in the timeline to the next. The period index may
     * be the same as it was before the discontinuity in case the current period is repeated.
     */
    int DISCONTINUITY_REASON_PERIOD_TRANSITION = 0;
    /**
     * Seek within the current period or to another period.
     */
    int DISCONTINUITY_REASON_SEEK = 1;
    /**
     * Seek adjustment due to being unable to seek to the requested position or because the seek was
     * permitted to be inexact.
     */
    int DISCONTINUITY_REASON_SEEK_ADJUSTMENT = 2;
    /**
     * Discontinuity to or from an ad within one period in the timeline.
     */
    int DISCONTINUITY_REASON_AD_INSERTION = 3;
    /**
     * Discontinuity introduced internally by the source.
     */
    int DISCONTINUITY_REASON_INTERNAL = 4;
}
