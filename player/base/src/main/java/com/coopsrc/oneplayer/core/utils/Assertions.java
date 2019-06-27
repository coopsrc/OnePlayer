package com.coopsrc.oneplayer.core.utils;

import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.coopsrc.oneplayer.core.PlayerLibraryInfo;

import org.checkerframework.checker.nullness.qual.EnsuresNonNull;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-06-27 16:26
 */
public final class Assertions {

    private Assertions() {
    }

    public static void checkArgument(boolean expression) {
        if (PlayerLibraryInfo.ASSERTIONS_ENABLED && !expression) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument(boolean expression, Object errorMessage) {
        if (PlayerLibraryInfo.ASSERTIONS_ENABLED && !expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    public static int checkIndex(int index, int start, int limit) {
        if (index < start || index >= limit) {
            throw new IndexOutOfBoundsException();
        }
        return index;
    }

    public static void checkState(boolean expression) {
        if (PlayerLibraryInfo.ASSERTIONS_ENABLED && !expression) {
            throw new IllegalStateException();
        }
    }

    public static void checkState(boolean expression, Object errorMessage) {
        if (PlayerLibraryInfo.ASSERTIONS_ENABLED && !expression) {
            throw new IllegalStateException(String.valueOf(errorMessage));
        }
    }

    @EnsuresNonNull({"#1"})
    public static <T> T checkNotNull(@Nullable T reference) {
        if (PlayerLibraryInfo.ASSERTIONS_ENABLED && reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    @EnsuresNonNull({"#1"})
    public static <T> T checkNotNull(@Nullable T reference, Object errorMessage) {
        if (PlayerLibraryInfo.ASSERTIONS_ENABLED && reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    @EnsuresNonNull({"#1"})
    public static String checkNotEmpty(@Nullable String string) {
        if (PlayerLibraryInfo.ASSERTIONS_ENABLED && TextUtils.isEmpty(string)) {
            throw new IllegalArgumentException();
        }
        return string;
    }

    @EnsuresNonNull({"#1"})
    public static String checkNotEmpty(@Nullable String string, Object errorMessage) {
        if (PlayerLibraryInfo.ASSERTIONS_ENABLED && TextUtils.isEmpty(string)) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
        return string;
    }

    public static void checkMainThread() {
        if (PlayerLibraryInfo.ASSERTIONS_ENABLED && Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("Not in applications main thread");
        }
    }

}
