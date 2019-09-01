package com.coopsrc.oneplayer.kernel.ijkmedia.misc;

import com.coopsrc.oneplayer.core.misc.IMediaFormat;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-10 11:32
 */
public class OneIjkMediaFormat implements IMediaFormat {
    private final tv.danmaku.ijk.media.player.misc.IMediaFormat mIjkMediaFormat;

    public OneIjkMediaFormat(tv.danmaku.ijk.media.player.misc.IMediaFormat ijkMediaFormat) {
        mIjkMediaFormat = ijkMediaFormat;
    }

    @Override
    public String getString(String name) {
        if (mIjkMediaFormat != null) {
            return mIjkMediaFormat.getString(name);
        }
        return null;
    }

    @Override
    public int getInteger(String name) {
        if (mIjkMediaFormat != null) {
            return mIjkMediaFormat.getInteger(name);
        }
        return 0;
    }
}
