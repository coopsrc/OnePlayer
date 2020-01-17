package com.coopsrc.oneplayer.core.utils;

import androidx.annotation.NonNull;

import com.coopsrc.xandroid.utils.LogUtils;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-09 11:52
 */
public class PlayerLogger {

    private PlayerLogger() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " cannot be instantiated");
    }

    public static void i(String message, Object... args) {
        LogUtils.tag(Constants.LOG_TAG).i(message, args);
    }

    public static void d(String message, Object... args) {
        LogUtils.tag(Constants.LOG_TAG).d(message, args);
    }

    public static void w(String message, Object... args) {
        LogUtils.tag(Constants.LOG_TAG).w(message, args);
    }

    public static void e(String message, Object... args) {
        LogUtils.tag(Constants.LOG_TAG).e(message, args);
    }

    public static void v(String message, Object... args) {
        LogUtils.tag(Constants.LOG_TAG).v(message, args);
    }

    public static void wtf(String message, Object... args) {
        LogUtils.tag(Constants.LOG_TAG).wtf(message, args);
    }

    public static void i(@NonNull String tag, String message, Object... args) {
        LogUtils.tag(tag).i(message, args);
    }

    public static void d(@NonNull String tag, String message, Object... args) {
        LogUtils.tag(tag).d(message, args);
    }

    public static void w(@NonNull String tag, String message, Object... args) {
        LogUtils.tag(tag).w(message, args);
    }

    public static void e(@NonNull String tag, String message, Object... args) {
        LogUtils.tag(tag).e(message, args);
    }

    public static void v(@NonNull String tag, String message, Object... args) {
        LogUtils.tag(tag).v(message, args);
    }

    public static void wtf(@NonNull String tag, String message, Object... args) {
        LogUtils.tag(tag).wtf(message, args);
    }

}