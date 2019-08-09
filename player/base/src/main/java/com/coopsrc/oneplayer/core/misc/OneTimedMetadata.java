package com.coopsrc.oneplayer.core.misc;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-08-09 10:32
 */
public class OneTimedMetadata implements ITimedMetadata {

    private final long timestamp;
    private final byte[] metadata;

    public OneTimedMetadata(long timestamp, byte[] metadata) {
        this.timestamp = timestamp;
        this.metadata = metadata;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public byte[] getMetadata() {
        return metadata;
    }
}
