package com.coopsrc.oneplayer.ijkmedia.misc;

import com.coopsrc.oneplayer.core.misc.IMediaDataSource;

import java.io.IOException;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-10 11:07
 */
public class IjkMediaDataSource implements tv.danmaku.ijk.media.player.misc.IMediaDataSource {
    private final IMediaDataSource mMediaDataSource;

    public IjkMediaDataSource(IMediaDataSource mediaDataSource) {
        mMediaDataSource = mediaDataSource;
    }

    @Override
    public int readAt(long position, byte[] buffer, int offset, int size) throws IOException {
        return mMediaDataSource.readAt(position, buffer, offset, size);
    }

    @Override
    public long getSize() throws IOException {
        return mMediaDataSource.getSize();
    }

    @Override
    public void close() throws IOException {
        mMediaDataSource.close();
    }
}
