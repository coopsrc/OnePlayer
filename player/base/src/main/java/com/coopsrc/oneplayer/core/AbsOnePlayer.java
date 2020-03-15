package com.coopsrc.oneplayer.core;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.media.AudioAttributes;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import androidx.annotation.Nullable;

import com.coopsrc.oneplayer.core.invoke.ListenerHolder;
import com.coopsrc.oneplayer.core.invoke.ListenerInvocation;
import com.coopsrc.oneplayer.core.misc.ITimedMetadata;
import com.coopsrc.oneplayer.core.misc.ITimedText;
import com.coopsrc.oneplayer.core.utils.Constants;
import com.coopsrc.oneplayer.core.utils.PlayerLogger;
import com.coopsrc.oneplayer.core.utils.PlayerUtils;
import com.coopsrc.xandroid.utils.AppTaskExecutors;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-09 15:30
 */
public abstract class AbsOnePlayer<P> implements OnePlayer {

    private final ArrayDeque<Runnable> mPendingListenerNotifications;
    private final CopyOnWriteArrayList<ListenerHolder<EventListener>> mEventListenerHolders;

    private final Context mContext;

    private int mBufferedPercentage;

    private float audioVolume;

    @State
    private int playbackState = STATE_IDLE;

    @Nullable
    private Surface surface;
    private boolean ownsSurface;
    @Nullable
    private SurfaceHolder surfaceHolder;
    @Nullable
    private TextureView textureView;
    private int surfaceWidth;
    private int surfaceHeight;

    private int videoWidth;
    private int videoHeight;
    private int rotationDegrees;
    private float pixelRatio;

    public AbsOnePlayer(Context context) {
        mContext = context;

        mEventListenerHolders = new CopyOnWriteArrayList<>();
        mPendingListenerNotifications = new ArrayDeque<>();

        printDebugInfo();
    }

    private void printDebugInfo() {
        String hexString = Integer.toHexString(System.identityHashCode(this));
        String deviceInfo = PlayerUtils.DEVICE_DEBUG_INFO;
        String libVersion = PlayerLibraryInfo.VERSION_SLASHY;
        PlayerLogger.i(TAG, "Init %s [%s] [%s]", hexString, libVersion, deviceInfo);
    }

    public Context getContext() {
        return mContext;
    }

    public abstract P getInternalPlayer();

    protected abstract PlayerListenerWrapper getInternalListener();

    protected abstract void initializePlayer();

    protected abstract void attachInternalListeners();

    protected abstract void resetInternalListeners();

    @Override
    public void addEventListener(EventListener listener) {
        mEventListenerHolders.addIfAbsent(new ListenerHolder<>(listener));
    }

    @Override
    public void removeEventListener(EventListener listener) {
        for (ListenerHolder listenerHolder : mEventListenerHolders) {
            if (listenerHolder.getListener().equals(listener)) {
                listenerHolder.release();
                mEventListenerHolders.remove(listenerHolder);
            }
        }
    }

    protected abstract void setSurface(@Nullable Surface surface);

    @Override
    public boolean release() {
        removeSurfaceCallbacks();
        if (surface != null) {
            if (ownsSurface) {
                surface.release();
            }
            surface = null;
        }

        return true;
    }

    @Override
    public void setVolume(float audioVolume) {
        if (this.audioVolume != audioVolume) {
            this.audioVolume = audioVolume;

            notifyOnVolumeChanged(audioVolume);
        }
    }

    @Override
    public float getVolume() {
        return audioVolume;
    }

    @Override
    public void clearVideoSurface() {
        setVideoSurface(null);
    }

    @Override
    public void clearVideoSurface(Surface surface) {
        if (surface != null && surface == this.surface) {
            setVideoSurface(null);
        }
    }

    @Override
    public void setVideoSurface(@Nullable Surface surface) {
        removeSurfaceCallbacks();
        setVideoSurfaceInternal(surface, false);
        int newSurfaceSize = surface == null ? 0 : Constants.LENGTH_UNSET;
        maybeNotifySurfaceSizeChanged(newSurfaceSize, newSurfaceSize);
    }

    @Override
    public void setVideoSurfaceHolder(SurfaceHolder surfaceHolder) {
        removeSurfaceCallbacks();
        this.surfaceHolder = surfaceHolder;
        if (surfaceHolder == null) {
            setVideoSurfaceInternal(null, false);
            maybeNotifySurfaceSizeChanged(0, 0);
        } else {
            surfaceHolder.addCallback(getInternalListener());
            Surface surface = surfaceHolder.getSurface();
            if (surface != null && surface.isValid()) {
                setVideoSurfaceInternal(surface, false);
                Rect surfaceSize = surfaceHolder.getSurfaceFrame();
                maybeNotifySurfaceSizeChanged(surfaceSize.width(), surfaceSize.height());
            } else {
                setVideoSurfaceInternal(null, false);
                maybeNotifySurfaceSizeChanged(0, 0);
            }
        }
    }

    @Override
    public void clearVideoSurfaceHolder(SurfaceHolder surfaceHolder) {
        if (surfaceHolder != null && surfaceHolder == this.surfaceHolder) {
            setVideoSurfaceHolder(null);
        }
    }

    @Override
    public void setVideoSurfaceView(SurfaceView surfaceView) {
        setVideoSurfaceHolder(surfaceView == null ? null : surfaceView.getHolder());
    }

    @Override
    public void clearVideoSurfaceView(SurfaceView surfaceView) {
        clearVideoSurfaceHolder(surfaceView == null ? null : surfaceView.getHolder());
    }

    @Override
    public void setVideoTextureView(TextureView textureView) {
        removeSurfaceCallbacks();
        this.textureView = textureView;
        if (textureView == null) {
            setVideoSurfaceInternal(null, true);
            maybeNotifySurfaceSizeChanged(0, 0);
        } else {
            if (textureView.getSurfaceTextureListener() != null) {
                PlayerLogger.w(TAG, "Replacing existing SurfaceTextureListener.");
            }
            textureView.setSurfaceTextureListener(getInternalListener());
            SurfaceTexture surfaceTexture = textureView.isAvailable() ? textureView.getSurfaceTexture() : null;
            if (surfaceTexture == null) {
                setVideoSurfaceInternal(null, true);
                maybeNotifySurfaceSizeChanged(0, 0);
            } else {
                setVideoSurfaceInternal(new Surface(surfaceTexture), true);
                maybeNotifySurfaceSizeChanged(textureView.getWidth(), textureView.getHeight());
            }
        }
    }

    @Override
    public void clearVideoTextureView(TextureView textureView) {
        if (textureView != null && textureView == this.textureView) {
            setVideoTextureView(null);
        }
    }

    @Override
    public int getBufferedPercentage() {
        return mBufferedPercentage;
    }

    @Override
    public long getBufferedPosition() {
        return mBufferedPercentage * getDuration();
    }

    @Override
    public int getCurrentPercentage() {
        return Long.valueOf(getCurrentPosition() / getDuration()).intValue();
    }

    @Override
    public int getVideoWidth() {
        return videoWidth;
    }

    @Override
    public int getVideoHeight() {
        return videoHeight;
    }

    @Override
    public int getRotationDegrees() {
        return rotationDegrees;
    }

    @Override
    public float getPixelRatio() {
        return pixelRatio;
    }

    private void removeSurfaceCallbacks() {
        if (textureView != null) {
            textureView.setSurfaceTextureListener(null);
            textureView = null;
        }
        if (surfaceHolder != null) {
            surfaceHolder.removeCallback(getInternalListener());
            surfaceHolder = null;
        }
    }

    private void setVideoSurfaceInternal(@Nullable Surface surface, boolean ownsSurface) {

        if (this.surface != null && this.surface != surface) {
            if (this.ownsSurface) {
                this.surface.release();
            }
        }

        this.surface = surface;
        this.ownsSurface = ownsSurface;

        setSurface(surface);
    }

    @Override
    public int getPlaybackState() {
        return playbackState;
    }

    protected Executor getIOThreadExecutor() {
        return AppTaskExecutors.diskIO();
    }

    protected Executor getMainThreadExecutor() {
        return AppTaskExecutors.mainThread();
    }

    /*
     * notify EventListener
     */
    protected final void notifyOnBufferingUpdate(final int percent) {
        mBufferedPercentage = percent;

        notifyEventListeners(new ListenerInvocation<EventListener>() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onBufferingUpdate(AbsOnePlayer.this, percent);
            }
        });
    }

    protected final void notifyOnCompletion() {

        notifyEventListeners(new ListenerInvocation<EventListener>() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onCompletion(AbsOnePlayer.this);
            }
        });
    }

    protected final boolean notifyOnError(final int what, final int extra) {

        notifyEventListeners(new ListenerInvocation<EventListener>() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onError(AbsOnePlayer.this, what, extra);
            }
        });

        return false;
    }

    protected final boolean notifyOnInfo(final int what, final int extra) {

        notifyEventListeners(new ListenerInvocation<EventListener>() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onInfo(AbsOnePlayer.this, what, extra);
            }
        });

        return false;
    }

    protected final void notifyOnPrepared() {

        notifyEventListeners(new ListenerInvocation<EventListener>() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onPrepared(AbsOnePlayer.this);
            }
        });
    }

    protected final void notifyOnSeekComplete() {

        notifyEventListeners(new ListenerInvocation<EventListener>() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onSeekComplete(AbsOnePlayer.this);
            }
        });
    }

    protected final void notifyOnTimedText(final ITimedText text) {

        notifyEventListeners(new ListenerInvocation<EventListener>() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onTimedText(AbsOnePlayer.this, text);
            }
        });
    }

    protected final void notifyOnTimedMetadata(final ITimedMetadata timedMetadata) {
        notifyEventListeners(new ListenerInvocation<EventListener>() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onTimedMetaDataAvailable(AbsOnePlayer.this, timedMetadata);
            }
        });
    }

    protected final void notifyOnPlaybackStateChanged(int playbackState) {
        this.playbackState = playbackState;
        notifyEventListeners(new ListenerInvocation<EventListener>() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onPlaybackStateChanged(playbackState);
            }
        });
    }

    /*
     * notify AudioListener
     */
    protected final void notifyOnAudioSessionId(int audioSessionId) {
        notifyEventListeners(new ListenerInvocation<EventListener>() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onAudioSessionId(AbsOnePlayer.this, audioSessionId);
            }
        });
    }

    protected final void notifyOnAudioAttributesChanged(AudioAttributes audioAttributes) {
        notifyEventListeners(new ListenerInvocation<EventListener>() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onAudioAttributesChanged(AbsOnePlayer.this, audioAttributes);
            }
        });
    }

    protected final void notifyOnVolumeChanged(float audioVolume) {
        notifyEventListeners(new ListenerInvocation<EventListener>() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onVolumeChanged(AbsOnePlayer.this, audioVolume);
            }
        });
    }

    /*
     * notify VideoListener
     */
    protected final void notifyOnVideoSizeChanged(int width, int height, int rotationDegrees, float pixelRatio) {
        this.videoWidth = width;
        this.videoHeight = height;
        this.rotationDegrees = rotationDegrees;
        this.pixelRatio = pixelRatio;

        notifyEventListeners(new ListenerInvocation<EventListener>() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onVideoSizeChanged(AbsOnePlayer.this, width, height, rotationDegrees, pixelRatio);
            }
        });
    }

    protected final void notifyOnVideoSizeChanged(final int width, final int height) {
        notifyOnVideoSizeChanged(width, height, ROTATION_0, PIXEL_RATIO_1);
    }

    protected final void notifyOnSurfaceSizeChanged(int width, int height) {
        notifyEventListeners(new ListenerInvocation<EventListener>() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onSurfaceSizeChanged(AbsOnePlayer.this, width, height);
            }
        });
    }

    protected final void notifyOnRenderedFirstFrame() {
        notifyEventListeners(new ListenerInvocation<EventListener>() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onRenderedFirstFrame(AbsOnePlayer.this);
            }
        });
    }

    private void notifyEventListeners(final ListenerInvocation<EventListener> listenerInvocation) {
        notifyListeners(mEventListenerHolders, listenerInvocation);
    }

    private <T> void notifyListeners(CopyOnWriteArrayList<ListenerHolder<T>> listenerHolders, final ListenerInvocation<T> listenerInvocation) {
        final CopyOnWriteArrayList<ListenerHolder<T>> listenerSnapshot = new CopyOnWriteArrayList<>(listenerHolders);
        notifyListeners(new Runnable() {
            @Override
            public void run() {
                invokeAll(listenerSnapshot, listenerInvocation);
            }
        });
    }

    private void notifyListeners(Runnable listenerNotificationRunnable) {
        boolean isRunningRecursiveListenerNotification = !mPendingListenerNotifications.isEmpty();
        mPendingListenerNotifications.addLast(listenerNotificationRunnable);
        if (isRunningRecursiveListenerNotification) {
            return;
        }

        while (!mPendingListenerNotifications.isEmpty()) {
            Runnable runnable = mPendingListenerNotifications.peekFirst();
            if (runnable != null) {
                runnable.run();
                mPendingListenerNotifications.remove(runnable);
            }
//            mPendingListenerNotifications.peekFirst().run();
//            mPendingListenerNotifications.removeFirst();
        }

    }

    private <T> void invokeAll(CopyOnWriteArrayList<ListenerHolder<T>> listenerHolders, ListenerInvocation<T> listenerInvocation) {
        for (ListenerHolder<T> listenerHolder : listenerHolders) {
            listenerHolder.invoke(listenerInvocation);
        }
    }

    private void maybeNotifySurfaceSizeChanged(int width, int height) {
        if (width != surfaceWidth || height != surfaceHeight) {
            surfaceWidth = width;
            surfaceHeight = height;
            notifyOnSurfaceSizeChanged(width, height);
        }
    }

    protected abstract class PlayerListenerWrapper<T extends AbsOnePlayer> implements
            SurfaceHolder.Callback,
            TextureView.SurfaceTextureListener {

        private final WeakReference<T> mPlayerReference;

        public PlayerListenerWrapper(T player) {
            mPlayerReference = new WeakReference<>(player);
        }

        protected final T getPlayer() {
            return mPlayerReference.get();
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            setVideoSurfaceInternal(surfaceHolder.getSurface(), false);
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            maybeNotifySurfaceSizeChanged(width, height);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            setVideoSurfaceInternal(null, false);
            maybeNotifySurfaceSizeChanged(0, 0);
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {

            setVideoSurfaceInternal(new Surface(surfaceTexture), true);
            maybeNotifySurfaceSizeChanged(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            maybeNotifySurfaceSizeChanged(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            setVideoSurfaceInternal(null, true);
            maybeNotifySurfaceSizeChanged(0, 0);
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    }
}
