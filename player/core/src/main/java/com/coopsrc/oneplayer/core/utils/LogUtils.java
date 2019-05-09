package com.coopsrc.oneplayer.core.utils;

import android.support.annotation.NonNull;

import timber.log.Timber;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-09 11:52
 */
public class LogUtils {

    private LogUtils() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " cannot be instantiated");
    }

    static {
        Timber.tag(Constants.LOG_TAG);
        Timber.plant(new Timber.DebugTree());
    }

    public static void i(String message, Object... args) {
        Timber.tag(Constants.LOG_TAG).i(message, args);
    }

    public static void d(String message, Object... args) {
        Timber.tag(Constants.LOG_TAG).d(message, args);
    }

    public static void w(String message, Object... args) {
        Timber.tag(Constants.LOG_TAG).w(message, args);
    }

    public static void e(String message, Object... args) {
        Timber.tag(Constants.LOG_TAG).e(message, args);
    }

    public static void v(String message, Object... args) {
        Timber.tag(Constants.LOG_TAG).v(message, args);
    }

    public static void wtf(String message, Object... args) {
        Timber.tag(Constants.LOG_TAG).wtf(message, args);
    }

    public static void i(@NonNull String tag, String message, Object... args) {
        Timber.tag(tag).i(message, args);
    }

    public static void d(@NonNull String tag, String message, Object... args) {
        Timber.tag(tag).d(message, args);
    }

    public static void w(@NonNull String tag, String message, Object... args) {
        Timber.tag(tag).w(message, args);
    }

    public static void e(@NonNull String tag, String message, Object... args) {
        Timber.tag(tag).e(message, args);
    }

    public static void v(@NonNull String tag, String message, Object... args) {
        Timber.tag(tag).v(message, args);
    }

    public static void wtf(@NonNull String tag, String message, Object... args) {
        Timber.tag(tag).wtf(message, args);
    }

}