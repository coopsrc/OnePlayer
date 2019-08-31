package com.coopsrc.oneplayer.core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import org.checkerframework.checker.nullness.compatqual.NullableType;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;

import java.util.Formatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-16 11:58
 */
public final class PlayerUtils {
    private static final String TAG = "PlayerUtils";


    /**
     * Like {@link android.os.Build.VERSION#SDK_INT}, but in a place where it can be conveniently
     * overridden for local testing.
     */
    public static final int SDK_INT = Build.VERSION.SDK_INT;

    /**
     * Like {@link Build#DEVICE}, but in a place where it can be conveniently overridden for local
     * testing.
     */
    public static final String DEVICE = Build.DEVICE;

    /**
     * Like {@link Build#MANUFACTURER}, but in a place where it can be conveniently overridden for
     * local testing.
     */
    public static final String MANUFACTURER = Build.MANUFACTURER;

    /**
     * Like {@link Build#MODEL}, but in a place where it can be conveniently overridden for local
     * testing.
     */
    public static final String MODEL = Build.MODEL;

    /**
     * A concise description of the device that it can be useful to log for debugging purposes.
     */
    public static final String DEVICE_DEBUG_INFO = String.format("%s,%s,%s,%s", DEVICE, MODEL, MANUFACTURER, SDK_INT);


    public static String formatPlayingTime(StringBuilder builder, Formatter formatter, long timeStamp) {


        String timeStr = "00:00";

        if (timeStamp > 0) {

            long hours = TimeUnit.MILLISECONDS.toHours(timeStamp);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(timeStamp) % 60;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(timeStamp) % 60;
            if (hours > 0) {
                timeStr = formatter.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds).toString();
            } else {
                timeStr = formatter.format(Locale.getDefault(), "%02d:%02d", minutes, seconds).toString();
            }
        }

        PlayerLogger.v(TAG, "formatPlayingTime: timeStamp=" + timeStamp + " => " + timeStr);

        builder.setLength(0);
        return timeStr;
    }

    /**
     * 获取Activity
     */
    public static Activity scanForActivity(Context context) {
        return context == null ? null : (context instanceof Activity ? (Activity) context : (context instanceof ContextWrapper ? scanForActivity(((ContextWrapper) context).getBaseContext()) : null));
    }

    /**
     * Constrains a value to the specified bounds.
     *
     * @param value The value to constrain.
     * @param min   The lower bound.
     * @param max   The upper bound.
     * @return The constrained value {@code Math.max(min, Math.min(value, max))}.
     */
    public static int constrainValue(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    /**
     * Constrains a value to the specified bounds.
     *
     * @param value The value to constrain.
     * @param min   The lower bound.
     * @param max   The upper bound.
     * @return The constrained value {@code Math.max(min, Math.min(value, max))}.
     */
    public static long constrainValue(long value, long min, long max) {
        return Math.max(min, Math.min(value, max));
    }

    /**
     * Constrains a value to the specified bounds.
     *
     * @param value The value to constrain.
     * @param min   The lower bound.
     * @param max   The upper bound.
     * @return The constrained value {@code Math.max(min, Math.min(value, max))}.
     */
    public static float constrainValue(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }

    /**
     * Returns the specified millisecond time formatted as a string.
     *
     * @param builder   The builder that {@code formatter} will write to.
     * @param formatter The formatter.
     * @param timeMs    The time to format as a string, in milliseconds.
     * @return The time formatted as a string.
     */
    public static String getStringForTime(StringBuilder builder, Formatter formatter, long timeMs) {
        if (timeMs == Constants.TIME_UNSET) {
            timeMs = 0;
        }
        long totalSeconds = (timeMs + 500) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        builder.setLength(0);
        return hours > 0 ? formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
                : formatter.format("%02d:%02d", minutes, seconds).toString();
    }


    /**
     * Casts a nullable type array to a non-null type array without runtime null check.
     */
    @SuppressWarnings({"contracts.postcondition.not.satisfied", "return.type.incompatible"})
    @EnsuresNonNull("#1")
    public static <T> T[] castNonNullTypeArray(@NullableType T[] value) {
        return value;
    }
}
