package com.coopsrc.oneplayer.core.misc;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-10 10:35
 */
public interface IMediaDataSource extends Closeable {
    public int readAt(long position, byte[] buffer, int offset, int size) throws IOException;

    public long getSize() throws IOException;
}
