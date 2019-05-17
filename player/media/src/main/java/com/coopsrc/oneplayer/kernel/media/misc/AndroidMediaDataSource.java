package com.coopsrc.oneplayer.kernel.media.misc;

import android.media.MediaDataSource;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.coopsrc.oneplayer.core.misc.IMediaDataSource;

import java.io.IOException;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-10 10:39
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class AndroidMediaDataSource extends MediaDataSource {
    private final IMediaDataSource mMediaDataSource;

    public AndroidMediaDataSource(IMediaDataSource mediaDataSource) {
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
