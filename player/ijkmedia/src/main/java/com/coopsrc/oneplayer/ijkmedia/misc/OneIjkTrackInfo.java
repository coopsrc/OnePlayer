package com.coopsrc.oneplayer.ijkmedia.misc;

import android.os.Bundle;

import com.coopsrc.oneplayer.core.misc.IMediaFormat;
import com.coopsrc.oneplayer.core.misc.ITrackInfo;

import tv.danmaku.ijk.media.player.IjkMediaMeta;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.IjkTrackInfo;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-10 11:12
 */
public class OneIjkTrackInfo implements ITrackInfo {

    private final IjkTrackInfo mIjkTrackInfo;

    public static OneIjkTrackInfo[] fromIjkMediaPlayer(IjkMediaPlayer player) {

        if (player == null) {
            return null;
        }

        Bundle bundle = player.getMediaMeta();
        if (bundle == null) {
            return null;
        }

        IjkMediaMeta ijkMediaMeta = IjkMediaMeta.parse(bundle);
        if (ijkMediaMeta == null || ijkMediaMeta.mStreams == null) {
            return null;
        }
        OneIjkTrackInfo[] ijkTrackInfos = new OneIjkTrackInfo[ijkMediaMeta.mStreams.size()];
        for (int i = 0; i < ijkMediaMeta.mStreams.size(); i++) {
            IjkMediaMeta.IjkStreamMeta streamMeta = ijkMediaMeta.mStreams.get(i);
            IjkTrackInfo trackInfo = new IjkTrackInfo(streamMeta);
            if (streamMeta.mType.equalsIgnoreCase(IjkMediaMeta.IJKM_VAL_TYPE__VIDEO)) {
                trackInfo.setTrackType(tv.danmaku.ijk.media.player.misc.ITrackInfo.MEDIA_TRACK_TYPE_VIDEO);
            } else if (streamMeta.mType.equalsIgnoreCase(IjkMediaMeta.IJKM_VAL_TYPE__AUDIO)) {
                trackInfo.setTrackType(tv.danmaku.ijk.media.player.misc.ITrackInfo.MEDIA_TRACK_TYPE_AUDIO);
            } else if (streamMeta.mType.equalsIgnoreCase(IjkMediaMeta.IJKM_VAL_TYPE__TIMEDTEXT)) {
                trackInfo.setTrackType(tv.danmaku.ijk.media.player.misc.ITrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT);
            }
            ijkTrackInfos[i] = new OneIjkTrackInfo(trackInfo);
        }


        return ijkTrackInfos;
    }

    private OneIjkTrackInfo(IjkTrackInfo ijkTrackInfo) {
        mIjkTrackInfo = ijkTrackInfo;
    }


    @Override
    public IMediaFormat getFormat() {
        if (mIjkTrackInfo != null && mIjkTrackInfo.getFormat() !=null) {
            return new OneIjkMediaFormat(mIjkTrackInfo.getFormat());
        }
        return null;
    }

    @Override
    public String getLanguage() {
        if (mIjkTrackInfo != null) {
            return mIjkTrackInfo.getLanguage();
        }
        return null;
    }

    @Override
    public int getTrackType() {
        if (mIjkTrackInfo != null) {
            return mIjkTrackInfo.getTrackType();
        }
        return 0;
    }

    @Override
    public String getInfoInline() {
        if (mIjkTrackInfo != null) {
            return mIjkTrackInfo.getInfoInline();
        }
        return null;
    }
}
