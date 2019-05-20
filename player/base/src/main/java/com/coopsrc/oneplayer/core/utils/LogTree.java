package com.coopsrc.oneplayer.core.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import java.util.HashMap;
import java.util.Locale;

import timber.log.Timber;


/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-09 12:15
 */
public class LogTree extends Timber.Tree {

    private static HashMap<String, String> sCachedTag = new HashMap<>();

    @Override
    protected boolean isLoggable(@Nullable String tag, int priority) {
        return super.isLoggable(tag, priority);
    }

    @Override
    protected void log(int priority, @Nullable String tag, @NonNull String message, @Nullable Throwable t) {

    }

    private static String buildTag(@NonNull String tag) {
        String key = String.format(Locale.getDefault(), "%s@%s", tag, Thread.currentThread().getName());

        if (!sCachedTag.containsKey(key)) {
            if (Constants.LOG_TAG.equals(tag)) {
                sCachedTag.put(key, String.format(Locale.getDefault(), "|%s|%s|",
                        tag,
                        Thread.currentThread().getName()
                ));
            } else {
                sCachedTag.put(key, String.format(Locale.getDefault(), "|%s_%s|%s|",
                        Constants.LOG_TAG,
                        tag,
                        Thread.currentThread().getName()
                ));
            }
        }

        return sCachedTag.get(key);
    }

    private static String getCaller() {
        StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();

        if (traceElements == null || traceElements.length < 4) {
            return "";
        }
        StackTraceElement traceElement = traceElements[4];

        return String.format(Locale.getDefault(), "%s.%s(%s:%d)",
                traceElement.getClassName().substring(traceElement.getClassName().lastIndexOf(".") + 1),
                traceElement.getMethodName(),
                traceElement.getFileName(),
                traceElement.getLineNumber()
        );
    }
}