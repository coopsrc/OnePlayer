package com.coopsrc.oneplayer.core;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import androidx.annotation.Nullable;

import com.coopsrc.oneplayer.core.misc.ITimedText;
import com.coopsrc.oneplayer.core.utils.Constants;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-09 15:30
 */
public abstract class AbsOnePlayer<P> implements OnePlayer {
    private OnBufferingUpdateListener mOnBufferingUpdateListener;
    private OnCompletionListener mOnCompletionListener;
    private OnErrorListener mOnErrorListener;
    private OnInfoListener mOnInfoListener;
    private OnPreparedListener mOnPreparedListener;
    private OnSeekCompleteListener mOnSeekCompleteListener;
    private OnTimedTextListener mOnTimedTextListener;
    private OnVideoSizeChangedListener mOnVideoSizeChangedListener;

    private final CopyOnWriteArrayList<EventListenerHolder> mListenerHolders;
    private final ArrayDeque<Runnable> mPendingListenerNotifications;

    private final CopyOnWriteArraySet<VideoSurfaceListener> mVideoSurfaceListeners;

    private final Context mContext;

    private int mBufferedPercentage;

    @Nullable
    private Surface surface;
    @Nullable
    private SurfaceHolder surfaceHolder;
    @Nullable
    private TextureView textureView;
    private int surfaceWidth;
    private int surfaceHeight;

    public AbsOnePlayer(Context context) {
        mContext = context;

        mListenerHolders = new CopyOnWriteArrayList<>();
        mPendingListenerNotifications = new ArrayDeque<>();

        mVideoSurfaceListeners = new CopyOnWriteArraySet<>();
    }

    public Context getContext() {
        return mContext;
    }

    public abstract P getInternalPlayer();

    protected abstract PlayerListenerHolder getInternalListener();

    protected abstract void initializePlayer();

    protected abstract void attachInternalListeners();

    protected void resetInternalListeners() {
        mOnBufferingUpdateListener = null;
        mOnCompletionListener = null;
        mOnErrorListener = null;
        mOnInfoListener = null;
        mOnPreparedListener = null;
        mOnSeekCompleteListener = null;
        mOnTimedTextListener = null;
        mOnVideoSizeChangedListener = null;
    }

    @Override
    public void addListener(EventListener listener) {
        mListenerHolders.addIfAbsent(new EventListenerHolder(listener));
    }

    @Override
    public void removeListener(EventListener listener) {
        for (EventListenerHolder listenerHolder : mListenerHolders) {
            if (listenerHolder.getListener().equals(listener)) {
                listenerHolder.release();
                mListenerHolders.remove(listenerHolder);
            }
        }
    }

    protected abstract void setSurface(@Nullable Surface surface);

    protected abstract void setDisplay(SurfaceHolder holder);

    @Override
    public void addVideoSurfaceListener(VideoSurfaceListener videoSurfaceListener) {
        mVideoSurfaceListeners.add(videoSurfaceListener);
    }

    @Override
    public void removeVideoSurfaceListener(VideoSurfaceListener videoSurfaceListener) {
        mVideoSurfaceListeners.remove(videoSurfaceListener);
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
        return (int) (getCurrentPosition() / getDuration());
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
        setSurface(surface);
    }

    @Override
    public int getPlaybackState() {
        return OnePlayer.STATE_IDLE;
    }

    @Override
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener onBufferingUpdateListener) {
        mOnBufferingUpdateListener = onBufferingUpdateListener;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
        mOnCompletionListener = onCompletionListener;
    }

    @Override
    public void setOnErrorListener(OnErrorListener onErrorListener) {
        mOnErrorListener = onErrorListener;
    }

    @Override
    public void setOnInfoListener(OnInfoListener onInfoListener) {
        mOnInfoListener = onInfoListener;
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        mOnPreparedListener = onPreparedListener;
    }

    @Override
    public void setOnSeekCompleteListener(OnSeekCompleteListener onSeekCompleteListener) {
        mOnSeekCompleteListener = onSeekCompleteListener;
    }

    @Override
    public void setOnTimedTextListener(OnTimedTextListener onTimedTextListener) {
        mOnTimedTextListener = onTimedTextListener;
    }

    @Override
    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener) {
        mOnVideoSizeChangedListener = onVideoSizeChangedListener;
    }

    @Override
    public void setOnPlayerStateChangedListener(OnPlaybackStateChangedListener onPlaybackStateChangedListener) {

    }

    protected final void notifyOnBufferingUpdate(final int percent) {
        mBufferedPercentage = percent;
        if (mOnBufferingUpdateListener != null) {
            mOnBufferingUpdateListener.onBufferingUpdate(this, percent);
        }

        notifyEventListeners(new EventListenerHolder.ListenerInvocation() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onBufferingUpdate(AbsOnePlayer.this, percent);
            }
        });
    }

    protected final void notifyOnCompletion() {
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onCompletion(this);
        }

        notifyEventListeners(new EventListenerHolder.ListenerInvocation() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onCompletion(AbsOnePlayer.this);
            }
        });
    }

    protected final boolean notifyOnError(final int what, final int extra) {
        if (mOnErrorListener != null) {
            mOnErrorListener.onError(this, what, extra);
        }

        notifyEventListeners(new EventListenerHolder.ListenerInvocation() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onError(AbsOnePlayer.this, what, extra);
            }
        });

        return false;
    }

    protected final boolean notifyOnInfo(final int what, final int extra) {
        if (mOnInfoListener != null) {
            mOnInfoListener.onInfo(this, what, extra);
        }

        notifyEventListeners(new EventListenerHolder.ListenerInvocation() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onInfo(AbsOnePlayer.this, what, extra);
            }
        });

        return false;
    }

    protected final void notifyOnPrepared() {
        if (mOnPreparedListener != null) {
            mOnPreparedListener.onPrepared(this);
        }

        notifyEventListeners(new EventListenerHolder.ListenerInvocation() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onPrepared(AbsOnePlayer.this);
            }
        });
    }

    protected final void notifyOnSeekComplete() {
        if (mOnSeekCompleteListener != null) {
            mOnSeekCompleteListener.onSeekComplete(this);
        }

        notifyEventListeners(new EventListenerHolder.ListenerInvocation() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onSeekComplete(AbsOnePlayer.this);
            }
        });
    }

    protected final void notifyOnTimedText(final ITimedText text) {
        if (mOnTimedTextListener != null) {
            mOnTimedTextListener.onTimedText(this, text);
        }

        notifyEventListeners(new EventListenerHolder.ListenerInvocation() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onTimedText(AbsOnePlayer.this, text);
            }
        });
    }

    protected final void notifyOnVideoSizeChanged(final int width, final int height) {
        if (mOnVideoSizeChangedListener != null) {
            mOnVideoSizeChangedListener.onVideoSizeChanged(this, width, height);
        }

        notifyEventListeners(new EventListenerHolder.ListenerInvocation() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onVideoSizeChanged(AbsOnePlayer.this, width, height);
            }
        });
    }

    private void notifyEventListeners(final EventListenerHolder.ListenerInvocation listenerInvocation) {
        final CopyOnWriteArrayList<EventListenerHolder> listenerSnapshot = new CopyOnWriteArrayList<>(mListenerHolders);
        notifyEventListeners(new Runnable() {
            @Override
            public void run() {
                invokeAll(listenerSnapshot, listenerInvocation);
            }
        });
    }

    private static void invokeAll(CopyOnWriteArrayList<EventListenerHolder> listenerHolders,
                                  EventListenerHolder.ListenerInvocation listenerInvocation) {
        for (EventListenerHolder listenerHolder : listenerHolders) {
            listenerHolder.invoke(listenerInvocation);
        }
    }

    private void notifyEventListeners(Runnable listenerNotificationRunnable) {
        boolean isRunningRecursiveListenerNotification = !mPendingListenerNotifications.isEmpty();
        mPendingListenerNotifications.addLast(listenerNotificationRunnable);
        if (isRunningRecursiveListenerNotification) {
            return;
        }

        while (!mPendingListenerNotifications.isEmpty()) {
            mPendingListenerNotifications.peekFirst().run();
            mPendingListenerNotifications.removeFirst();
        }

    }

    private void maybeNotifySurfaceSizeChanged(int width, int height) {
        if (width != surfaceWidth || height != surfaceHeight) {
            surfaceWidth = width;
            surfaceHeight = height;
            for (VideoSurfaceListener videoSurfaceListener : mVideoSurfaceListeners) {
                videoSurfaceListener.onSurfaceSizeChanged(width, height);
            }
        }
    }

    protected abstract class PlayerListenerHolder<T extends AbsOnePlayer> implements
            SurfaceHolder.Callback,
            TextureView.SurfaceTextureListener {

        private final WeakReference<T> mPlayerReference;

        public PlayerListenerHolder(T player) {
            mPlayerReference = new WeakReference<>(player);
        }

        protected T getPlayer() {
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
            maybeNotifySurfaceSizeChanged(0,0);
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    }
}
