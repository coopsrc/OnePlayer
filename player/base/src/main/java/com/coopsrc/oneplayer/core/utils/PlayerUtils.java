package com.coopsrc.oneplayer.core.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-16 11:58
 */
public class PlayerUtils {
    private static final String TAG = "PlayerUtils";

    public static String formatPlayingTime(long timeStamp) {

        String timeStr = "00:00";

        if (timeStamp > 0) {

            long hours = TimeUnit.MILLISECONDS.toHours(timeStamp);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(timeStamp) % 60;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(timeStamp) % 60;
            if (hours > 0) {
                timeStr = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
            } else {
                timeStr = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
            }
        }

        LogUtils.v(TAG, "formatPlayingTime: timeStamp=" + timeStamp + " => " + timeStr);

        return timeStr;
    }

    /**
     * 获取Activity
     */
    public static Activity scanForActivity(Context context) {
        return context == null ? null : (context instanceof Activity ? (Activity) context : (context instanceof ContextWrapper ? scanForActivity(((ContextWrapper) context).getBaseContext()) : null));
    }

    /**
     * Get AppCompatActivity from context
     */
    public static AppCompatActivity getAppCompatActivity(Context context) {
        if (context == null) return null;
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getAppCompatActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    /**
     * 隐藏ActionBar
     */
    @SuppressLint("RestrictedApi")
    public static void hideActionBar(Context context) {
        AppCompatActivity appCompatActivity = getAppCompatActivity(context);
        if (appCompatActivity != null) {
            ActionBar actionBar = appCompatActivity.getSupportActionBar();
            if (actionBar != null && actionBar.isShowing()) {
                actionBar.setShowHideAnimationEnabled(false);
                actionBar.hide();
            }
        }
    }

    /**
     * 显示ActionBar
     */
    @SuppressLint("RestrictedApi")
    public static void showActionBar(final Context context) {
        AppCompatActivity appCompatActivity = getAppCompatActivity(context);
        if (appCompatActivity != null) {
            ActionBar ab = appCompatActivity.getSupportActionBar();
            if (ab != null && !ab.isShowing()) {
                ab.setShowHideAnimationEnabled(false);
                ab.show();
            }
        }
    }
}
