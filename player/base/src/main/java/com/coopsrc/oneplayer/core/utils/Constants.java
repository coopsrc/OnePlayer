package com.coopsrc.oneplayer.core.utils;

import android.os.Build;

import com.coopsrc.oneplayer.core.PlayerLibraryInfo;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-09 14:24
 */
public class Constants {
    public static final String LOG_TAG = PlayerLibraryInfo.TAG;

    /**
     * Special constant representing a time corresponding to the end of a source. Suitable for use in
     * any time base.
     */
    public static final long TIME_END_OF_SOURCE = Long.MIN_VALUE;

    /**
     * Special constant representing an unset or unknown time or duration. Suitable for use in any
     * time base.
     */
    public static final long TIME_UNSET = Long.MIN_VALUE + 1;

    /**
     * Represents an unset or unknown index.
     */
    public static final int INDEX_UNSET = -1;

    /**
     * Represents an unset or unknown position.
     */
    public static final int POSITION_UNSET = -1;

    /**
     * Represents an unset or unknown length.
     */
    public static final int LENGTH_UNSET = -1;

    /**
     * Represents an unset or unknown percentage.
     */
    public static final int PERCENTAGE_UNSET = -1;

    /**
     * The number of microseconds in one second.
     */
    public static final long MICROS_PER_SECOND = 1000000L;

    /**
     * The number of nanoseconds in one second.
     */
    public static final long NANOS_PER_SECOND = 1000000000L;

    /**
     * The number of bits per byte.
     */
    public static final int BITS_PER_BYTE = 8;

    /**
     * The number of bytes per float.
     */
    public static final int BYTES_PER_FLOAT = 4;

    /**
     * The name of the ASCII charset.
     */
    public static final String ASCII_NAME = "US-ASCII";
    /**
     * The name of the UTF-8 charset.
     */
    public static final String UTF8_NAME = "UTF-8";

    /**
     * The name of the UTF-16 charset.
     */
    public static final String UTF16_NAME = "UTF-16";

    /**
     * The name of the UTF-16 little-endian charset.
     */
    public static final String UTF16LE_NAME = "UTF-16LE";

    /**
     * The name of the serif font family.
     */
    public static final String SERIF_NAME = "serif";

    /**
     * The name of the sans-serif font family.
     */
    public static final String SANS_SERIF_NAME = "sans-serif";

    /**
     * Converts a time in microseconds to the corresponding time in milliseconds, preserving
     * {@link #TIME_UNSET} and {@link #TIME_END_OF_SOURCE} values.
     *
     * @param timeUs The time in microseconds.
     * @return The corresponding time in milliseconds.
     */
    public static long usToMs(long timeUs) {
        return (timeUs == TIME_UNSET || timeUs == TIME_END_OF_SOURCE) ? timeUs : (timeUs / 1000);
    }

    /**
     * Converts a time in milliseconds to the corresponding time in microseconds, preserving
     * {@link #TIME_UNSET} values and {@link #TIME_END_OF_SOURCE} values.
     *
     * @param timeMs The time in milliseconds.
     * @return The corresponding time in microseconds.
     */
    public static long msToUs(long timeMs) {
        return (timeMs == TIME_UNSET || timeMs == TIME_END_OF_SOURCE) ? timeMs : (timeMs * 1000);
    }


    public static final String DEVICE_DEBUG_INFO = String.format("%s, %s, %s, %s", Build.DEVICE, Build.MODEL, Build.MANUFACTURER, Build.VERSION.SDK_INT);
}
