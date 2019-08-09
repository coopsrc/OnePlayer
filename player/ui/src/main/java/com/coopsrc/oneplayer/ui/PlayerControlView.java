package com.coopsrc.oneplayer.ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.coopsrc.oneplayer.core.ControlDispatcher;
import com.coopsrc.oneplayer.core.DefaultControlDispatcher;
import com.coopsrc.oneplayer.core.OnePlayer;
import com.coopsrc.oneplayer.core.PlaybackPreparer;
import com.coopsrc.oneplayer.core.PlayerLibraryInfo;
import com.coopsrc.oneplayer.core.misc.ITimedMetadata;
import com.coopsrc.oneplayer.core.utils.Constants;
import com.coopsrc.oneplayer.core.utils.PlayerLogger;
import com.coopsrc.oneplayer.core.utils.PlayerUtils;
import com.coopsrc.oneplayer.ui.widget.ProgressTimeBar;

import java.util.Formatter;
import java.util.Locale;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-10 16:40
 */
public class PlayerControlView extends ConstraintLayout {
    private static final String TAG = "PlayerControlView";

    static {
        PlayerLibraryInfo.registerModule("one.player.ui");
    }

    /**
     * Listener to be notified about changes of the visibility of the UI control.
     */
    public interface VisibilityListener {

        /**
         * Called when the visibility changes.
         *
         * @param visibility The new visibility. Either {@link View#VISIBLE} or {@link View#GONE}.
         */
        void onVisibilityChange(int visibility);
    }

    /**
     * Listener to be notified when progress has been updated.
     */
    public interface ProgressUpdateListener {

        /**
         * Called when progress needs to be updated.
         *
         * @param position         The current position.
         * @param bufferedPosition The current buffered position.
         */
        void onProgressUpdate(long position, long bufferedPosition);
    }

    /**
     * The default fast forward increment, in milliseconds.
     */
    public static final int DEFAULT_FAST_FORWARD_MS = 15000;
    /**
     * The default rewind increment, in milliseconds.
     */
    public static final int DEFAULT_REWIND_MS = 5000;
    /**
     * The default show timeout, in milliseconds.
     */
    public static final int DEFAULT_SHOW_TIMEOUT_MS = 5000;
    /**
     * The default minimum interval between time bar position updates.
     */
    public static final int DEFAULT_TIME_BAR_MIN_UPDATE_INTERVAL_MS = 200;
    /**
     * The maximum interval between time bar position updates.
     */
    private static final int MAX_UPDATE_INTERVAL_MS = 1000;

    private final ComponentListener componentListener;
    private final View previousButton;
    private final View nextButton;
    private final View playPauseButton;
    private final View fastForwardButton;
    private final View rewindButton;
    private final View fullScreenButton;
    private final TextView durationView;
    private final TextView positionView;
    private final TimeBar timeBar;
    private final StringBuilder formatBuilder;
    private final Formatter formatter;
    private final Runnable updateProgressAction;
    private final Runnable hideAction;

    @Nullable
    private OnePlayer player;
    private ControlDispatcher controlDispatcher;
    @Nullable
    private VisibilityListener visibilityListener;
    @Nullable
    private ProgressUpdateListener progressUpdateListener;
    @Nullable
    private PlaybackPreparer playbackPreparer;

    private boolean isAttachedToWindow;
    private boolean scrubbing;
    private int rewindMs;
    private int fastForwardMs;
    private int showTimeoutMs;
    private int timeBarMinUpdateIntervalMs;
    private long hideAtMs;

    public PlayerControlView(Context context) {
        this(context, null);
    }

    public PlayerControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, attrs);
    }

    public PlayerControlView( Context context, AttributeSet attrs, int defStyleAttr, AttributeSet playbackAttrs) {
        super(context, attrs, defStyleAttr);
        int controllerLayoutId = R.layout.layout_playback_control_view;
        rewindMs = DEFAULT_REWIND_MS;
        fastForwardMs = DEFAULT_FAST_FORWARD_MS;
        showTimeoutMs = DEFAULT_SHOW_TIMEOUT_MS;
        timeBarMinUpdateIntervalMs = DEFAULT_TIME_BAR_MIN_UPDATE_INTERVAL_MS;
        hideAtMs = Constants.TIME_UNSET;
        if (playbackAttrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(playbackAttrs, R.styleable.PlayerControlView, 0, 0);
            try {
                rewindMs = a.getInt(R.styleable.PlayerControlView_rewind_increment, rewindMs);
                fastForwardMs = a.getInt(R.styleable.PlayerControlView_fastforward_increment, fastForwardMs);
                showTimeoutMs = a.getInt(R.styleable.PlayerControlView_show_timeout, showTimeoutMs);
                controllerLayoutId = a.getResourceId(R.styleable.PlayerControlView_controller_layout_id, controllerLayoutId);
                setTimeBarMinUpdateInterval(a.getInt(R.styleable.PlayerControlView_time_bar_min_update_interval, timeBarMinUpdateIntervalMs));
            } finally {
                a.recycle();
            }
        }
        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());
        componentListener = new ComponentListener();
        controlDispatcher = new DefaultControlDispatcher();
        updateProgressAction = this::updateProgress;
        hideAction = this::hide;

        LayoutInflater.from(context).inflate(controllerLayoutId, this);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);

        TimeBar customTimeBar = findViewById(R.id.progress_bar);
        View timeBarPlaceholder = findViewById(R.id.progress_bar_placeholder);
        if (customTimeBar != null) {
            timeBar = customTimeBar;
        } else if (timeBarPlaceholder != null) {
            // Propagate attrs as timebarAttrs so that DefaultTimeBar's custom attributes are transferred,
            // but standard attributes (e.g. background) are not.
            ProgressTimeBar defaultTimeBar = new ProgressTimeBar(context, null, 0);
            defaultTimeBar.setId(R.id.progress_bar);
            defaultTimeBar.setLayoutParams(timeBarPlaceholder.getLayoutParams());
            ViewGroup parent = ((ViewGroup) timeBarPlaceholder.getParent());
            int timeBarIndex = parent.indexOfChild(timeBarPlaceholder);
            parent.removeView(timeBarPlaceholder);
            parent.addView(defaultTimeBar, timeBarIndex);
            timeBar = defaultTimeBar;
        } else {
            timeBar = null;
        }
        durationView = findViewById(R.id.text_duration);
        positionView = findViewById(R.id.text_position);

        if (timeBar != null) {
            timeBar.addListener(componentListener);
        }
        playPauseButton = findViewById(R.id.button_play_pause);
        if (playPauseButton != null) {
            playPauseButton.setOnClickListener(componentListener);
        }
        previousButton = findViewById(R.id.button_skip_previous);
        if (previousButton != null) {
            previousButton.setOnClickListener(componentListener);
        }
        nextButton = findViewById(R.id.button_skip_next);
        if (nextButton != null) {
            nextButton.setOnClickListener(componentListener);
        }
        rewindButton = findViewById(R.id.button_fast_rewind);
        if (rewindButton != null) {
            rewindButton.setOnClickListener(componentListener);
        }
        fastForwardButton = findViewById(R.id.button_fast_forward);
        if (fastForwardButton != null) {
            fastForwardButton.setOnClickListener(componentListener);
        }
        fullScreenButton = findViewById(R.id.button_fullscreen);
        if (fullScreenButton != null) {
            fullScreenButton.setOnClickListener(componentListener);
        }

        setClipChildren(false);
        setClipToPadding(false);
    }


    /**
     * Returns the {@link OnePlayer} currently being controlled by this view, or null if no player is
     * set.
     */
    @Nullable
    public OnePlayer getPlayer() {
        return player;
    }

    /**
     * Sets the {@link OnePlayer} to control.
     *
     * @param player The {@link OnePlayer} to control, or {@code null} to detach the current player. Only
     *               players which are accessed on the main thread are supported ({@code
     *               player.getApplicationLooper() == Looper.getMainLooper()}).
     */
    public void setPlayer(@Nullable OnePlayer player) {
        if (this.player == player) {
            return;
        }
        if (this.player != null) {
            this.player.removeEventListener(componentListener);
        }
        this.player = player;
        if (player != null) {
            player.addEventListener(componentListener);
        }
//        updateAll();
    }

    /**
     * Sets the {@link VisibilityListener}.
     *
     * @param listener The listener to be notified about visibility changes.
     */
    public void setVisibilityListener(@Nullable VisibilityListener listener) {
        this.visibilityListener = listener;
    }

    /**
     * Sets the {@link ProgressUpdateListener}.
     *
     * @param listener The listener to be notified about when progress is updated.
     */
    public void setProgressUpdateListener(@Nullable ProgressUpdateListener listener) {
        this.progressUpdateListener = listener;
    }

    /**
     * Sets the {@link PlaybackPreparer}.
     *
     * @param playbackPreparer The {@link PlaybackPreparer}.
     */
    public void setPlaybackPreparer(@Nullable PlaybackPreparer playbackPreparer) {
        this.playbackPreparer = playbackPreparer;
    }

    /**
     * Sets the {@link ControlDispatcher}.
     *
     * @param controlDispatcher The {@link ControlDispatcher}, or null to use {@link DefaultControlDispatcher}.
     */
    public void setControlDispatcher(@Nullable ControlDispatcher controlDispatcher) {
        this.controlDispatcher = controlDispatcher == null ? new DefaultControlDispatcher() : controlDispatcher;
    }

    /**
     * Sets the rewind increment in milliseconds.
     *
     * @param rewindMs The rewind increment in milliseconds. A non-positive value will cause the
     *                 rewind button to be disabled.
     */
    public void setRewindIncrementMs(int rewindMs) {
        this.rewindMs = rewindMs;
        updateNavigation();
    }

    /**
     * Sets the fast forward increment in milliseconds.
     *
     * @param fastForwardMs The fast forward increment in milliseconds. A non-positive value will
     *                      cause the fast forward button to be disabled.
     */
    public void setFastForwardIncrementMs(int fastForwardMs) {
        this.fastForwardMs = fastForwardMs;
        updateNavigation();
    }

    /**
     * Returns the playback controls timeout. The playback controls are automatically hidden after
     * this duration of time has elapsed without user input.
     *
     * @return The duration in milliseconds. A non-positive value indicates that the controls will
     * remain visible indefinitely.
     */
    public int getShowTimeoutMs() {
        return showTimeoutMs;
    }

    /**
     * Sets the playback controls timeout. The playback controls are automatically hidden after this
     * duration of time has elapsed without user input.
     *
     * @param showTimeoutMs The duration in milliseconds. A non-positive value will cause the controls
     *                      to remain visible indefinitely.
     */
    public void setShowTimeoutMs(int showTimeoutMs) {
        this.showTimeoutMs = showTimeoutMs;
        if (isVisible()) {
            // Reset the timeout.
            hideAfterTimeout();
        }
    }

    /**
     * Sets the minimum interval between time bar position updates.
     *
     * <p>Note that smaller intervals, e.g. 33ms, will result in a smooth movement but will use more
     * CPU resources while the time bar is visible, whereas larger intervals, e.g. 200ms, will result
     * in a step-wise update with less CPU usage.
     *
     * @param minUpdateIntervalMs The minimum interval between time bar position updates, in
     *                            milliseconds.
     */
    public void setTimeBarMinUpdateInterval(int minUpdateIntervalMs) {
        // Do not accept values below 16ms (60fps) and larger than the maximum update interval.
        timeBarMinUpdateIntervalMs = PlayerUtils.constrainValue(minUpdateIntervalMs, 16, MAX_UPDATE_INTERVAL_MS);
    }

    /**
     * Shows the playback controls. If {@link #getShowTimeoutMs()} is positive then the controls will
     * be automatically hidden after this duration of time has elapsed without user input.
     */
    public void show() {
        if (!isVisible()) {
            setVisibility(VISIBLE);
            if (visibilityListener != null) {
                visibilityListener.onVisibilityChange(getVisibility());
            }
//            updateAll();
            requestPlayPauseFocus();
        }
        // Call hideAfterTimeout even if already visible to reset the timeout.
        hideAfterTimeout();
    }

    /**
     * Hides the controller.
     */
    public void hide() {
        if (isVisible()) {
            setVisibility(GONE);
            if (visibilityListener != null) {
                visibilityListener.onVisibilityChange(getVisibility());
            }
            removeCallbacks(updateProgressAction);
            removeCallbacks(hideAction);
            hideAtMs = Constants.TIME_UNSET;
        }
    }

    /**
     * Returns whether the controller is currently visible.
     */
    public boolean isVisible() {
        return getVisibility() == VISIBLE;
    }

    private void hideAfterTimeout() {
        removeCallbacks(hideAction);
        if (showTimeoutMs > 0) {
            hideAtMs = SystemClock.uptimeMillis() + showTimeoutMs;
            if (isAttachedToWindow) {
                postDelayed(hideAction, showTimeoutMs);
            }
        } else {
            hideAtMs = Constants.TIME_UNSET;
        }
    }

    private void updateAll() {
        updatePlayPauseButton();
        updateNavigation();
        updateTimeline();
    }

    private void updatePlayPauseButton() {
        if (!isVisible() || !isAttachedToWindow) {
            return;
        }
        boolean requestPlayPauseFocus = false;
        boolean playing = isPlaying();
        if (playPauseButton != null) {
            requestPlayPauseFocus |= playing && playPauseButton.isFocused();
            playPauseButton.setVisibility(playing ? GONE : VISIBLE);
        }
        if (requestPlayPauseFocus) {
            requestPlayPauseFocus();
        }
    }

    private void updateNavigation() {
        if (!isVisible() || !isAttachedToWindow) {
            return;
        }
        boolean enableSeeking = true;
        boolean enablePrevious = false;
        boolean enableRewind = false;
        boolean enableFastForward = false;
        boolean enableNext = false;

        setButtonEnabled(enablePrevious, previousButton);
        setButtonEnabled(enableRewind, rewindButton);
        setButtonEnabled(enableFastForward, fastForwardButton);
        setButtonEnabled(enableNext, nextButton);
        if (timeBar != null) {
            timeBar.setEnabled(enableSeeking);
        }
    }


    private void updateTimeline() {
        if (player == null) {
            return;
        }
        long duration = player.getDuration();
        PlayerLogger.i(TAG, "updateTimeline: [%s]", duration);
        if (durationView != null) {
            durationView.setText(PlayerUtils.formatPlayingTime(formatBuilder, formatter, duration));
        }
        if (timeBar != null) {
            timeBar.setDuration(duration);
        }
//        updateProgress();
    }

    private void updateProgress() {
        PlayerLogger.i(TAG, "updateProgress: ");
        if (!isVisible() || !isAttachedToWindow) {
            return;
        }

        long position = 0;
        long bufferedPosition = 0;
        if (player != null) {
            position = player.getCurrentPosition();
            bufferedPosition = player.getBufferedPosition();
        }
        PlayerLogger.i(TAG, "updateProgress: [%s -- %s]", position, bufferedPosition);
        if (positionView != null && !scrubbing) {
            positionView.setText(PlayerUtils.formatPlayingTime(formatBuilder, formatter, position));
        }
        if (timeBar != null) {
            timeBar.setPosition(position);
            timeBar.setBufferedPosition(bufferedPosition);
        }
        if (progressUpdateListener != null) {
            progressUpdateListener.onProgressUpdate(position, bufferedPosition);
        }

        // Cancel any pending updates and schedule a new one if necessary.
        removeCallbacks(updateProgressAction);
        int playbackState = player == null ? OnePlayer.STATE_IDLE : player.getPlaybackState();
        if (playbackState == OnePlayer.STATE_PREPARED ) {
            long mediaTimeDelayMs = timeBar != null ? timeBar.getPreferredUpdateDelay() : MAX_UPDATE_INTERVAL_MS;

            // Limit delay to the start of the next full second to ensure position display is smooth.
            long mediaTimeUntilNextFullSecondMs = 1000 - position % 1000;
            mediaTimeDelayMs = Math.min(mediaTimeDelayMs, mediaTimeUntilNextFullSecondMs);

            // Calculate the delay until the next update in real time, taking playbackSpeed into account.
            long delayMs = mediaTimeDelayMs;

            // Constrain the delay to avoid too frequent / infrequent updates.
            delayMs = PlayerUtils.constrainValue(delayMs, timeBarMinUpdateIntervalMs, MAX_UPDATE_INTERVAL_MS);
//            postDelayed(updateProgressAction, delayMs);
        } else if (playbackState != OnePlayer.STATE_ENDED && playbackState != OnePlayer.STATE_IDLE) {
//            postDelayed(updateProgressAction, MAX_UPDATE_INTERVAL_MS);
        }
//        postDelayed(updateProgressAction, MAX_UPDATE_INTERVAL_MS);
    }

    private void requestPlayPauseFocus() {
        if (playPauseButton != null) {
            playPauseButton.requestFocus();
        }
    }

    private void setButtonEnabled(boolean enabled, View view) {
        if (view == null) {
            return;
        }
        view.setEnabled(enabled);
        view.setAlpha(enabled ? 1f : 0.3f);
        view.setVisibility(VISIBLE);
    }

    private void previous(OnePlayer player) {

    }

    private void next(OnePlayer player) {

    }

    private void rewind(OnePlayer player) {
        if (rewindMs > 0) {
            seekTo(player, player.getCurrentPosition() - rewindMs);
        }
    }

    private void fastForward(OnePlayer player) {
        if (fastForwardMs > 0) {
            seekTo(player, player.getCurrentPosition() + fastForwardMs);
        }
    }


    private boolean seekTo(OnePlayer player, long positionMs) {
        long durationMs = player.getDuration();
        if (durationMs != Constants.TIME_UNSET) {
            positionMs = Math.min(positionMs, durationMs);
        }
        positionMs = Math.max(positionMs, 0);
        return controlDispatcher.dispatchSeekTo(player, positionMs);
    }

    private void seekToTimeBarPosition(OnePlayer player, long positionMs) {
        boolean dispatched = seekTo(player, positionMs);
        if (!dispatched) {
            // The seek wasn't dispatched then the progress bar scrubber will be in the wrong position.
            // Trigger a progress update to snap it back.
            updateProgress();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttachedToWindow = true;
        if (hideAtMs != Constants.TIME_UNSET) {
            long delayMs = hideAtMs - SystemClock.uptimeMillis();
            if (delayMs <= 0) {
                hide();
            } else {
                postDelayed(hideAction, delayMs);
            }
        } else if (isVisible()) {
            hideAfterTimeout();
        }
//        updateAll();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttachedToWindow = false;
        removeCallbacks(updateProgressAction);
        removeCallbacks(hideAction);
    }

    @Override
    public final boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            removeCallbacks(hideAction);
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            hideAfterTimeout();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return dispatchMediaKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    /**
     * Called to process media key events. Any {@link KeyEvent} can be passed but only media key
     * events will be handled.
     *
     * @param event A key event.
     * @return Whether the key event was handled.
     */
    public boolean dispatchMediaKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (player == null || !isHandledMediaKey(keyCode)) {
            return false;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
                fastForward(player);
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND) {
                rewind(player);
            } else if (event.getRepeatCount() == 0) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                        controlDispatcher.dispatchSetPlayWhenReady(player, true);
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PAUSE:
                        controlDispatcher.dispatchSetPlayWhenReady(player, false);
                        break;
                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        next(player);
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        previous(player);
                        break;
                    default:
                        break;
                }
            }
        }
        return true;
    }

    private boolean isPlaying() {
        return player != null
                && player.getPlaybackState() != OnePlayer.STATE_ENDED
                && player.getPlaybackState() != OnePlayer.STATE_IDLE;
    }

    private static boolean isHandledMediaKey(int keyCode) {
        return keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
                || keyCode == KeyEvent.KEYCODE_MEDIA_REWIND
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY
                || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE
                || keyCode == KeyEvent.KEYCODE_MEDIA_NEXT
                || keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS;
    }


    private final class ComponentListener implements OnePlayer.EventListener, TimeBar.OnScrubListener, OnClickListener {

        @Override
        public boolean onInfo(OnePlayer player, int what, int extra) {
            PlayerLogger.i(TAG, "onInfo: [%s, %s]", what, extra);

            switch (what) {
                case OnePlayer.MEDIA_INFO_BUFFERING_START:
                    break;
                case OnePlayer.MEDIA_INFO_BUFFERING_END:
                    updateTimeline();
                    break;
            }

            return false;
        }

        @Override
        public void onScrubStart(TimeBar timeBar, long position) {
            PlayerLogger.i(TAG, "onScrubStart: [%s: %s]", timeBar, position);
            scrubbing = true;
            if (positionView != null) {
                positionView.setText(PlayerUtils.formatPlayingTime(formatBuilder, formatter, position));
            }
        }

        @Override
        public void onScrubMove(TimeBar timeBar, long position) {
            PlayerLogger.i(TAG, "onScrubMove: [%s: %s]", timeBar, position);
            if (positionView != null) {
                positionView.setText(PlayerUtils.formatPlayingTime(formatBuilder, formatter, position));
            }
        }

        @Override
        public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
            PlayerLogger.i(TAG, "onScrubStop: [%s: %s] %s", timeBar, position, canceled);
            scrubbing = false;
            if (!canceled && player != null) {
                seekToTimeBarPosition(player, position);
            }
        }

        @Override
        public void onPlaybackStateChanged(boolean playWhenReady, int playbackState) {
            PlayerLogger.i(TAG, "onPlaybackStateChanged: [%s: %s]", playWhenReady, playbackState);
            updatePlayPauseButton();
            updateProgress();
        }

        @Override
        public void onClick(View view) {
            PlayerLogger.i(TAG, "onClick: %s", view.getId());
            OnePlayer player = PlayerControlView.this.player;
            if (player == null) {
                return;
            }
            if (nextButton == view) {
                next(player);
            } else if (previousButton == view) {
                previous(player);
            } else if (fastForwardButton == view) {
                fastForward(player);
            } else if (rewindButton == view) {
                rewind(player);
            } else if (playPauseButton == view) {
                if (player.getPlaybackState() == OnePlayer.STATE_IDLE) {
                    if (playbackPreparer != null) {
                        playbackPreparer.preparePlayback();
                    }
                } else if (player.getPlaybackState() == OnePlayer.STATE_ENDED) {
                    controlDispatcher.dispatchSeekTo(player, Constants.TIME_UNSET);
                }
                if (player.isPlaying()) {
                    controlDispatcher.dispatchSetPlayWhenReady(player, false);
                } else {
                    controlDispatcher.dispatchSetPlayWhenReady(player, true);
                }
            } else if (fullScreenButton == view) {
                PlayerLogger.i(TAG, "onClick: " + getContext());
                if (getContext() instanceof Activity) {
                    int orientation = getContext().getResources().getConfiguration().orientation;

                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        ((Activity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    } else {
                        ((Activity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                }

            }
        }
    }
}
