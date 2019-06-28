package com.coopsrc.oneplayer.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.coopsrc.oneplayer.core.utils.PlayerLogger;
import com.coopsrc.oneplayer.ui.TimeBar;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-06-28 11:44
 */
public class ProgressTimeBar extends AppCompatSeekBar implements TimeBar {
    private static final String TAG = "ProgressTimeBar";

    private final CopyOnWriteArraySet<TimeBar.OnScrubListener> listeners;

    public ProgressTimeBar(Context context) {
        this(context, null);
    }

    public ProgressTimeBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressTimeBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        listeners = new CopyOnWriteArraySet<>();
    }

    @Override
    public void addListener(TimeBar.OnScrubListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(TimeBar.OnScrubListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void setKeyTimeIncrement(long time) {

    }

    @Override
    public void setKeyCountIncrement(int count) {

    }

    @Override
    public void setPosition(long position) {
        PlayerLogger.i(TAG, "setPosition: %s", position);
        setProgress((int) position);
    }

    @Override
    public void setBufferedPosition(long bufferedPosition) {
        PlayerLogger.i(TAG, "setBufferedPosition: %s", bufferedPosition);
        setSecondaryProgress((int) bufferedPosition);
    }

    @Override
    public void setDuration(long duration) {
        PlayerLogger.i(TAG, "setDuration: %s", duration);
        setMax((int) duration);
    }

    @Override
    public long getPreferredUpdateDelay() {
        return 0;
    }
}
