package com.coopsrc.oneplayer.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.coopsrc.oneplayer.core.ControlDispatcher;
import com.coopsrc.oneplayer.core.DefaultControlDispatcher;
import com.coopsrc.oneplayer.core.OnePlayer;
import com.coopsrc.oneplayer.core.PlaybackPreparer;
import com.coopsrc.oneplayer.core.utils.Assertions;
import com.coopsrc.oneplayer.core.utils.PlayerLogger;
import com.coopsrc.oneplayer.core.video.VideoListener;
import com.coopsrc.oneplayer.ui.widget.AspectRatioFrameLayout;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-10 16:39
 */
public class PlayerView extends ConstraintLayout {
    private static final String TAG = "PlayerView";

    // LINT.IfChange

    /**
     * Determines when the buffering view is shown. One of {@link #SHOW_BUFFERING_NEVER}, {@link
     * #SHOW_BUFFERING_WHEN_PLAYING} or {@link #SHOW_BUFFERING_ALWAYS}.
     */
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SHOW_BUFFERING_NEVER, SHOW_BUFFERING_WHEN_PLAYING, SHOW_BUFFERING_ALWAYS})
    @interface ShowBuffering {
    }

    /**
     * The buffering view is never shown.
     */
    public static final int SHOW_BUFFERING_NEVER = 0;
    /**
     * The buffering view is shown when the player is in the {@link OnePlayer#STATE_BUFFERING buffering}
     * state and {@link OnePlayer#getPlayWhenReady() playWhenReady} is {@code true}.
     */
    public static final int SHOW_BUFFERING_WHEN_PLAYING = 1;
    /**
     * The buffering view is always shown when the player is in the {@link OnePlayer#STATE_BUFFERING
     * buffering} state.
     */
    public static final int SHOW_BUFFERING_ALWAYS = 2;
    // LINT.ThenChange(../../../../../../res/values/attrs.xml)

    // LINT.IfChange
    private static final int SURFACE_TYPE_NONE = 0;
    private static final int SURFACE_TYPE_SURFACE_VIEW = 1;
    private static final int SURFACE_TYPE_TEXTURE_VIEW = 2;
    // LINT.ThenChange(../../../../../../res/values/attrs.xml)

    @Nullable
    private final AspectRatioFrameLayout contentFrame;
    @Nullable
    private final View surfaceView;
    @Nullable
    private final View bufferingView;
    @Nullable
    private final TextView errorMessageView;
    @Nullable
    private final PlayerControlView controller;
    private final ComponentListener componentListener;
    @Nullable
    private final FrameLayout overlayFrameLayout;

    private OnePlayer player;
    private boolean useController;
    private @ShowBuffering
    int showBuffering;
    private boolean keepContentOnPlayerReset;
    @Nullable
    private CharSequence customErrorMessage;
    private int controllerShowTimeoutMs;
    private boolean controllerAutoShow;
    private boolean controllerHideOnTouch;
    private int textureViewRotation;
    private boolean isTouching;

    public PlayerView(Context context) {
        this(context, null);
    }

    public PlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int playerLayoutId = R.layout.layout_player_view;
        boolean useController = true;
        int surfaceType = SURFACE_TYPE_SURFACE_VIEW;
        int resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT;
        int controllerShowTimeoutMs = PlayerControlView.DEFAULT_SHOW_TIMEOUT_MS;
        boolean controllerHideOnTouch = true;
        boolean controllerAutoShow = true;
        int showBuffering = SHOW_BUFFERING_NEVER;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PlayerView, 0, 0);
            try {
                playerLayoutId = a.getResourceId(R.styleable.PlayerView_player_layout_id, playerLayoutId);
                useController = a.getBoolean(R.styleable.PlayerView_use_controller, useController);
                surfaceType = a.getInt(R.styleable.PlayerView_surface_type, surfaceType);
                resizeMode = a.getInt(R.styleable.PlayerView_resize_mode, resizeMode);
                controllerShowTimeoutMs = a.getInt(R.styleable.PlayerView_show_timeout, controllerShowTimeoutMs);
                controllerHideOnTouch = a.getBoolean(R.styleable.PlayerView_hide_on_touch, controllerHideOnTouch);
                controllerAutoShow = a.getBoolean(R.styleable.PlayerView_auto_show, controllerAutoShow);
                showBuffering = a.getInteger(R.styleable.PlayerView_show_buffering, showBuffering);
                keepContentOnPlayerReset = a.getBoolean(R.styleable.PlayerView_keep_content_on_player_reset, keepContentOnPlayerReset);
            } finally {
                a.recycle();
            }
        }

        LayoutInflater.from(context).inflate(playerLayoutId, this);
        componentListener = new ComponentListener();
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);

        // Content frame.
        contentFrame = findViewById(R.id.player_content_frame);
        if (contentFrame != null) {
            setResizeModeRaw(contentFrame, resizeMode);
        }

        // Create a surface view and insert it into the content frame, if there is one.
        if (contentFrame != null && surfaceType != SURFACE_TYPE_NONE) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            switch (surfaceType) {
                case SURFACE_TYPE_TEXTURE_VIEW:
                    surfaceView = new TextureView(context);
                    break;
                default:
                    surfaceView = new SurfaceView(context);
                    break;
            }
            surfaceView.setLayoutParams(params);
            contentFrame.addView(surfaceView, 0);
        } else {
            surfaceView = null;
        }

        // Overlay frame layout.
        overlayFrameLayout = findViewById(R.id.player_overlay);


        // Buffering view.
        bufferingView = findViewById(R.id.player_buffering);
        if (bufferingView != null) {
            bufferingView.setVisibility(View.GONE);
        }
        this.showBuffering = showBuffering;

        // Error message view.
        errorMessageView = findViewById(R.id.player_error_message);
        if (errorMessageView != null) {
            errorMessageView.setVisibility(View.GONE);
        }

        // Playback control view.
        PlayerControlView customController = findViewById(R.id.playback_controller);
        View controllerPlaceholder = findViewById(R.id.playback_controller_placeholder);
        if (customController != null) {
            this.controller = customController;
        } else if (controllerPlaceholder != null) {
            // Propagate attrs as playbackAttrs so that PlayerControlView's custom attributes are
            // transferred, but standard attributes (e.g. background) are not.
            this.controller = new PlayerControlView(context, null, 0, attrs);
            controller.setId(R.id.playback_controller);
            controller.setLayoutParams(controllerPlaceholder.getLayoutParams());
            ViewGroup parent = ((ViewGroup) controllerPlaceholder.getParent());
            int controllerIndex = parent.indexOfChild(controllerPlaceholder);
            parent.removeView(controllerPlaceholder);
            parent.addView(controller, controllerIndex);
        } else {
            this.controller = null;
        }
        this.controllerShowTimeoutMs = controller != null ? controllerShowTimeoutMs : 0;
        this.controllerHideOnTouch = controllerHideOnTouch;
        this.controllerAutoShow = controllerAutoShow;
        this.useController = useController && controller != null;
        hideController();
    }

    /**
     * Switches the view targeted by a given {@link OnePlayer}.
     *
     * @param player        The player whose target view is being switched.
     * @param oldPlayerView The old view to detach from the player.
     * @param newPlayerView The new view to attach to the player.
     */
    public static void switchTargetView(OnePlayer player, @Nullable PlayerView oldPlayerView, @Nullable PlayerView newPlayerView) {
        if (oldPlayerView == newPlayerView) {
            return;
        }
        // We attach the new view before detaching the old one because this ordering allows the player
        // to swap directly from one surface to another, without transitioning through a state where no
        // surface is attached. This is significantly more efficient and achieves a more seamless
        // transition when using platform provided video decoders.
        if (newPlayerView != null) {
            newPlayerView.setPlayer(player);
        }
        if (oldPlayerView != null) {
            oldPlayerView.setPlayer(null);
        }
    }

    /**
     * Returns the player currently set on this view, or null if no player is set.
     */
    public OnePlayer getPlayer() {
        return player;
    }

    /**
     * Set the {@link OnePlayer} to use.
     *
     * <p>To transition a {@link OnePlayer} from targeting one view to another, it's recommended to use
     * {@link #switchTargetView(OnePlayer, PlayerView, PlayerView)} rather than this method. If you do
     * wish to use this method directly, be sure to attach the player to the new view <em>before</em>
     * calling {@code setPlayer(null)} to detach it from the old one. This ordering is significantly
     * more efficient and may allow for more seamless transitions.
     *
     * @param player The {@link OnePlayer} to use, or {@code null} to detach the current player. Only
     *               players which are accessed on the main thread are supported ({@code
     *               player.getApplicationLooper() == Looper.getMainLooper()}).
     */
    public void setPlayer(@Nullable OnePlayer player) {
        Assertions.checkState(Looper.myLooper() == Looper.getMainLooper());
        Assertions.checkArgument(player != null);
        if (this.player == player) {
            return;
        }
        if (this.player != null) {
            this.player.removeEventListener(componentListener);
            this.player.removeVideoListener(componentListener);
            if (surfaceView instanceof TextureView) {
                this.player.clearVideoTextureView((TextureView) surfaceView);
            } else if (surfaceView instanceof SurfaceView) {
                this.player.clearVideoSurfaceView((SurfaceView) surfaceView);
            }
        }
        this.player = player;
        if (useController && controller != null) {
            controller.setPlayer(player);
        }

        updateBuffering();
        updateErrorMessage();
        if (player != null) {

            if (surfaceView instanceof TextureView) {
                player.setVideoTextureView((TextureView) surfaceView);
            } else if (surfaceView instanceof SurfaceView) {
                player.setVideoSurfaceView((SurfaceView) surfaceView);

            }
            player.addVideoListener(componentListener);
            player.addEventListener(componentListener);
            maybeShowController(false);
        } else {
            hideController();
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (surfaceView instanceof SurfaceView) {
            surfaceView.setVisibility(visibility);
        }
    }

    /**
     * Sets the {@link AspectRatioFrameLayout.ResizeMode}.
     *
     * @param resizeMode The {@link AspectRatioFrameLayout.ResizeMode}.
     */
    public void setResizeMode(@AspectRatioFrameLayout.ResizeMode int resizeMode) {
        Assertions.checkState(contentFrame != null);
        if (contentFrame != null) {
            contentFrame.setResizeMode(resizeMode);
        }
    }

    /**
     * Returns the {@link AspectRatioFrameLayout.ResizeMode}.
     */
    @AspectRatioFrameLayout.ResizeMode
    public int getResizeMode() {
        Assertions.checkState(contentFrame != null);
        if (contentFrame != null) {

            return contentFrame.getResizeMode();
        } else {
            return AspectRatioFrameLayout.RESIZE_MODE_FIT;
        }
    }

    /**
     * Returns whether the playback controls can be shown.
     */
    public boolean getUseController() {
        return useController;
    }

    /**
     * Sets whether the playback controls can be shown. If set to {@code false} the playback controls
     * are never visible and are disconnected from the player.
     *
     * @param useController Whether the playback controls can be shown.
     */
    public void setUseController(boolean useController) {
        Assertions.checkState(!useController || controller != null);
        if (this.useController == useController) {
            return;
        }
        this.useController = useController;
        if (useController && controller != null) {
            controller.setPlayer(player);
        } else if (controller != null) {
            controller.hide();
            controller.setPlayer(null);
        }
    }

    /**
     * Sets whether a buffering spinner is displayed when the player is in the buffering state. The
     * buffering spinner is not displayed by default.
     *
     * @param showBuffering The mode that defines when the buffering spinner is displayed. One of
     *                      {@link #SHOW_BUFFERING_NEVER}, {@link #SHOW_BUFFERING_WHEN_PLAYING} and
     *                      {@link #SHOW_BUFFERING_ALWAYS}.
     */
    public void setShowBuffering(@ShowBuffering int showBuffering) {
        if (this.showBuffering != showBuffering) {
            this.showBuffering = showBuffering;
            updateBuffering();
        }
    }

    /**
     * Sets a custom error message to be displayed by the view. The error message will be displayed
     * permanently, unless it is cleared by passing {@code null} to this method.
     *
     * @param message The message to display, or {@code null} to clear a previously set message.
     */
    public void setCustomErrorMessage(@Nullable CharSequence message) {
        Assertions.checkState(errorMessageView != null);
        customErrorMessage = message;
        updateErrorMessage();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (player != null) {
            return super.dispatchKeyEvent(event);
        }

        boolean isDpadAndUseController = isDpadKey(event.getKeyCode()) && useController;
        boolean handled = false;

        if (controller == null) {
            return false;
        }

        if (isDpadAndUseController && !controller.isVisible()) {
            // Handle the key event by showing the controller.
            maybeShowController(true);
            handled = true;
        } else if (dispatchMediaKeyEvent(event) || super.dispatchKeyEvent(event)) {
            // The key event was handled as a media key or by the super class. We should also show the
            // controller, or extend its show timeout if already visible.
            maybeShowController(true);
            handled = true;
        } else if (isDpadAndUseController) {
            // The key event wasn't handled, but we should extend the controller's show timeout.
            maybeShowController(true);
        }
        return handled;
    }

    /**
     * Called to process media key events. Any {@link KeyEvent} can be passed but only media key
     * events will be handled. Does nothing if playback controls are disabled.
     *
     * @param event A key event.
     * @return Whether the key event was handled.
     */
    public boolean dispatchMediaKeyEvent(KeyEvent event) {
        return useController && controller != null && controller.dispatchMediaKeyEvent(event);
    }

    /**
     * Returns whether the controller is currently visible.
     */
    public boolean isControllerVisible() {
        return controller != null && controller.isVisible();
    }

    /**
     * Shows the playback controls. Does nothing if playback controls are disabled.
     *
     * <p>The playback controls are automatically hidden during playback after {{@link
     * #getControllerShowTimeoutMs()}}. They are shown indefinitely when playback has not started yet,
     * is paused, has ended or failed.
     */
    public void showController() {
        showController(shouldShowControllerIndefinitely());
    }

    /**
     * Hides the playback controls. Does nothing if playback controls are disabled.
     */
    public void hideController() {
        if (controller != null) {
            controller.hide();
        }
    }

    /**
     * Returns the playback controls timeout. The playback controls are automatically hidden after
     * this duration of time has elapsed without user input and with playback or buffering in
     * progress.
     *
     * @return The timeout in milliseconds. A non-positive value will cause the controller to remain
     * visible indefinitely.
     */
    public int getControllerShowTimeoutMs() {
        return controllerShowTimeoutMs;
    }

    /**
     * Sets the playback controls timeout. The playback controls are automatically hidden after this
     * duration of time has elapsed without user input and with playback or buffering in progress.
     *
     * @param controllerShowTimeoutMs The timeout in milliseconds. A non-positive value will cause the
     *                                controller to remain visible indefinitely.
     */
    public void setControllerShowTimeoutMs(int controllerShowTimeoutMs) {
        Assertions.checkState(controller != null);
        this.controllerShowTimeoutMs = controllerShowTimeoutMs;
        if (controller != null && controller.isVisible()) {
            // Update the controller's timeout if necessary.
            showController();
        }
    }

    /**
     * Returns whether the playback controls are hidden by touch events.
     */
    public boolean getControllerHideOnTouch() {
        return controllerHideOnTouch;
    }

    /**
     * Sets whether the playback controls are hidden by touch events.
     *
     * @param controllerHideOnTouch Whether the playback controls are hidden by touch events.
     */
    public void setControllerHideOnTouch(boolean controllerHideOnTouch) {
        Assertions.checkState(controller != null);
        this.controllerHideOnTouch = controllerHideOnTouch;
    }

    /**
     * Returns whether the playback controls are automatically shown when playback starts, pauses,
     * ends, or fails. If set to false, the playback controls can be manually operated with {@link
     * #showController()} and {@link #hideController()}.
     */
    public boolean getControllerAutoShow() {
        return controllerAutoShow;
    }

    /**
     * Sets whether the playback controls are automatically shown when playback starts, pauses, ends,
     * or fails. If set to false, the playback controls can be manually operated with {@link
     * #showController()} and {@link #hideController()}.
     *
     * @param controllerAutoShow Whether the playback controls are allowed to show automatically.
     */
    public void setControllerAutoShow(boolean controllerAutoShow) {
        this.controllerAutoShow = controllerAutoShow;
    }

    /**
     * Set the {@link PlayerControlView.VisibilityListener}.
     *
     * @param listener The listener to be notified about visibility changes.
     */
    public void setControllerVisibilityListener(PlayerControlView.VisibilityListener listener) {
        Assertions.checkState(controller != null);
        if (controller != null) {
            controller.setVisibilityListener(listener);
        }
    }

    /**
     * Sets the {@link PlaybackPreparer}.
     *
     * @param playbackPreparer The {@link PlaybackPreparer}.
     */
    public void setPlaybackPreparer(@Nullable PlaybackPreparer playbackPreparer) {
        Assertions.checkState(controller != null);
        if (controller != null) {
            controller.setPlaybackPreparer(playbackPreparer);
        }
    }

    /**
     * Sets the {@link ControlDispatcher}.
     *
     * @param controlDispatcher The {@link ControlDispatcher}, or null to use {@link  DefaultControlDispatcher}.
     */
    public void setControlDispatcher(@Nullable ControlDispatcher controlDispatcher) {
        Assertions.checkState(controller != null);
        if (controller != null) {
            controller.setControlDispatcher(controlDispatcher);
        }
    }

    /**
     * Sets the rewind increment in milliseconds.
     *
     * @param rewindMs The rewind increment in milliseconds. A non-positive value will cause the
     *                 rewind button to be disabled.
     */
    public void setRewindIncrementMs(int rewindMs) {
        Assertions.checkState(controller != null);
        if (controller != null) {
            controller.setRewindIncrementMs(rewindMs);
        }
    }

    /**
     * Sets the fast forward increment in milliseconds.
     *
     * @param fastForwardMs The fast forward increment in milliseconds. A non-positive value will
     *                      cause the fast forward button to be disabled.
     */
    public void setFastForwardIncrementMs(int fastForwardMs) {
        Assertions.checkState(controller != null);
        if (controller != null) {
            controller.setFastForwardIncrementMs(fastForwardMs);
        }
    }

    /**
     * Set the {@link AspectRatioFrameLayout.AspectRatioListener}.
     *
     * @param listener The listener to be notified about aspect ratios changes of the video content or
     *                 the content frame.
     */
    public void setAspectRatioListener(AspectRatioFrameLayout.AspectRatioListener listener) {
        Assertions.checkState(contentFrame != null);
        if (controller != null) {
            contentFrame.setAspectRatioListener(listener);
        }
    }

    /**
     * Gets the view onto which video is rendered. This is a:
     *
     * <ul>
     * <li>{@link SurfaceView} by default, or if the {@code surface_type} attribute is set to {@code
     * surface_view}.
     * <li>{@link TextureView} if {@code surface_type} is {@code texture_view}.
     * <li>{@code null} if {@code surface_type} is {@code none}.
     * </ul>
     *
     * @return The {@link SurfaceView}, {@link TextureView} or {@code
     * null}.
     */
    public View getVideoSurfaceView() {
        return surfaceView;
    }

    /**
     * Gets the overlay {@link FrameLayout}, which can be populated with UI elements to show on top of
     * the player.
     *
     * @return The overlay {@link FrameLayout}, or {@code null} if the layout has been customized and
     * the overlay is not present.
     */
    @Nullable
    public FrameLayout getOverlayFrameLayout() {
        return overlayFrameLayout;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouching = true;
                return true;
            case MotionEvent.ACTION_UP:
                if (isTouching) {
                    isTouching = false;
                    performClick();
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return toggleControllerVisibility();
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (!useController || player == null) {
            return false;
        }
        maybeShowController(true);
        return true;
    }


    /**
     * Called when there's a change in the aspect ratio of the content being displayed. The default
     * implementation sets the aspect ratio of the content frame to that of the content.
     *
     * @param contentAspectRatio The aspect ratio of the content.
     * @param contentFrame       The content frame, or {@code null}.
     */
    protected void onContentAspectRatioChanged(float contentAspectRatio, @Nullable AspectRatioFrameLayout contentFrame) {
        if (contentFrame != null) {
            contentFrame.setAspectRatio(contentAspectRatio);
        }
    }

    // Internal methods.

    private boolean toggleControllerVisibility() {
        if (!useController || player == null || controller == null) {
            return false;
        }
        if (!controller.isVisible()) {
            maybeShowController(true);
        } else if (controllerHideOnTouch) {
            controller.hide();
        }
        return true;
    }

    /**
     * Shows the playback controls, but only if forced or shown indefinitely.
     */
    private void maybeShowController(boolean isForced) {
        if (useController && controller != null) {
            boolean wasShowingIndefinitely = controller.isVisible() && controller.getShowTimeoutMs() <= 0;
            boolean shouldShowIndefinitely = shouldShowControllerIndefinitely();
            if (isForced || wasShowingIndefinitely || shouldShowIndefinitely) {
                showController(shouldShowIndefinitely);
            }
        }
    }

    private boolean shouldShowControllerIndefinitely() {
        if (player == null) {
            return true;
        }
        int playbackState = player.getPlaybackState();
        return controllerAutoShow
                && (playbackState == OnePlayer.STATE_IDLE
                || playbackState == OnePlayer.STATE_ENDED);
    }

    private void showController(boolean showIndefinitely) {
        if (!useController || controller == null) {
            return;
        }
        controller.setShowTimeoutMs(showIndefinitely ? 0 : controllerShowTimeoutMs);
        controller.show();
    }

    private void updateBuffering() {
        if (bufferingView != null) {
            boolean showBufferingSpinner =
                    player != null
                            && player.getPlaybackState() == OnePlayer.STATE_BUFFERING
                            && (showBuffering == SHOW_BUFFERING_ALWAYS
                            || (showBuffering == SHOW_BUFFERING_WHEN_PLAYING));
            bufferingView.setVisibility(showBufferingSpinner ? View.VISIBLE : View.GONE);
        }
    }

    private void updateErrorMessage() {
        if (errorMessageView != null) {
            if (customErrorMessage != null) {
                errorMessageView.setText(customErrorMessage);
                errorMessageView.setVisibility(View.VISIBLE);
            }
        }
    }

    private static void setResizeModeRaw(AspectRatioFrameLayout aspectRatioFrame, int resizeMode) {
        aspectRatioFrame.setResizeMode(resizeMode);
    }

    /**
     * Applies a texture rotation to a {@link TextureView}.
     */
    private static void applyTextureViewRotation(TextureView textureView, int textureViewRotation) {
        float textureViewWidth = textureView.getWidth();
        float textureViewHeight = textureView.getHeight();
        if (textureViewWidth == 0 || textureViewHeight == 0 || textureViewRotation == 0) {
            textureView.setTransform(null);
        } else {
            Matrix transformMatrix = new Matrix();
            float pivotX = textureViewWidth / 2;
            float pivotY = textureViewHeight / 2;
            transformMatrix.postRotate(textureViewRotation, pivotX, pivotY);

            // After rotation, scale the rotated texture to fit the TextureView size.
            RectF originalTextureRect = new RectF(0, 0, textureViewWidth, textureViewHeight);
            RectF rotatedTextureRect = new RectF();
            transformMatrix.mapRect(rotatedTextureRect, originalTextureRect);
            transformMatrix.postScale(
                    textureViewWidth / rotatedTextureRect.width(),
                    textureViewHeight / rotatedTextureRect.height(),
                    pivotX,
                    pivotY);
            textureView.setTransform(transformMatrix);
        }
    }

    @SuppressLint("InlinedApi")
    private boolean isDpadKey(int keyCode) {
        return keyCode == KeyEvent.KEYCODE_DPAD_UP
                || keyCode == KeyEvent.KEYCODE_DPAD_UP_RIGHT
                || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN_RIGHT
                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN
                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_UP_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_CENTER;
    }

    private final class ComponentListener implements OnePlayer.EventListener,
            VideoListener, OnLayoutChangeListener {


        @Override
        public void onVideoSizeChanged(OnePlayer player, int width, int height) {
            PlayerLogger.i(TAG, "onVideoSizeChanged: [%s,%s]", width, height);
        }

        @Override
        public void onVideoSizeChanged(int width, int height, int rotationDegrees, float ratio) {
            PlayerLogger.i(TAG, "onVideoSizeChanged: [%s,%s],%s,%s", width, height, rotationDegrees, ratio);
            float videoAspectRatio = (height == 0 || width == 0) ? 1 : (width * ratio) / height;

            if (surfaceView instanceof TextureView) {
                // Try to apply rotation transformation when our surface is a TextureView.
                if (rotationDegrees == 90 || rotationDegrees == 270) {
                    // We will apply a rotation 90/270 degree to the output texture of the TextureView.
                    // In this case, the output video's width and height will be swapped.
                    videoAspectRatio = 1 / videoAspectRatio;
                }
                if (textureViewRotation != 0) {
                    surfaceView.removeOnLayoutChangeListener(this);
                }
                textureViewRotation = rotationDegrees;
                if (textureViewRotation != 0) {
                    // The texture view's dimensions might be changed after layout step.
                    // So add an OnLayoutChangeListener to apply rotation after layout step.
                    surfaceView.addOnLayoutChangeListener(this);
                }
                applyTextureViewRotation((TextureView) surfaceView, textureViewRotation);
            }

            onContentAspectRatioChanged(videoAspectRatio, contentFrame);
        }

        @Override
        public void onSurfaceSizeChanged(int width, int height) {
            PlayerLogger.i(TAG, "onSurfaceSizeChanged: [%s,%s]", width, height);
        }

        @Override
        public void onRenderedFirstFrame() {
            PlayerLogger.i(TAG, "onRenderedFirstFrame: ");
        }


        @Override
        public void onPlaybackStateChanged(boolean playWhenReady, int playbackState) {
            PlayerLogger.i(TAG, "onPlaybackStateChanged: [%s, %s]", playWhenReady, playbackState);
            updateBuffering();
            updateErrorMessage();
            maybeShowController(false);
        }

        @Override
        public void onLayoutChange(
                View view,
                int left,
                int top,
                int right,
                int bottom,
                int oldLeft,
                int oldTop,
                int oldRight,
                int oldBottom) {
            PlayerLogger.i(TAG, "onLayoutChange: " + textureViewRotation);
            applyTextureViewRotation((TextureView) view, textureViewRotation);
        }
    }
}
