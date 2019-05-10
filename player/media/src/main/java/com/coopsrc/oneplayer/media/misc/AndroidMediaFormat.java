package com.coopsrc.oneplayer.media.misc;

import android.media.MediaFormat;

import com.coopsrc.oneplayer.core.misc.IMediaFormat;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-10 10:53
 */
public class AndroidMediaFormat implements IMediaFormat {
    private final MediaFormat mMediaFormat;

    public AndroidMediaFormat(MediaFormat mediaFormat) {
        mMediaFormat = mediaFormat;
    }

    @Override
    public String getString(String name) {
        if (mMediaFormat != null) {
            return mMediaFormat.getString(name);
        }

        return null;
    }

    @Override
    public int getInteger(String name) {
        if (mMediaFormat != null) {
            return mMediaFormat.getInteger(name);
        }
        return 0;
    }
}
