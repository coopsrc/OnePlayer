package com.coopsrc.oneplayer.core;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import androidx.annotation.Nullable;

import com.coopsrc.oneplayer.core.audio.AudioListener;
import com.coopsrc.oneplayer.core.invoke.ListenerHolder;
import com.coopsrc.oneplayer.core.invoke.ListenerInvocation;
import com.coopsrc.oneplayer.core.metadata.MetadataOutput;
import com.coopsrc.oneplayer.core.misc.ITimedText;
import com.coopsrc.oneplayer.core.text.TextOutput;
import com.coopsrc.oneplayer.core.utils.Constants;
import com.coopsrc.oneplayer.core.utils.PlayerLogger;
import com.coopsrc.oneplayer.core.utils.PlayerUtils;
import com.coopsrc.oneplayer.core.video.VideoListener;

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

    private final ArrayDeque<Runnable> mPendingListenerNotifications;
    private final CopyOnWriteArrayList<ListenerHolder<EventListener>> mListenerHolders;

    private final CopyOnWriteArraySet<AudioListener> mAudioListeners;
    private final CopyOnWriteArraySet<VideoListener> mVideoListeners;
    private final CopyOnWriteArraySet<TextOutput> mTextOutputs;
    private final CopyOnWriteArraySet<MetadataOutput> mMetadataOutputs;

    private final Context mContext;

    private int mBufferedPercentage;

    private float audioVolume;

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

        mAudioListeners = new CopyOnWriteArraySet<>();
        mVideoListeners = new CopyOnWriteArraySet<>();
        mTextOutputs = new CopyOnWriteArraySet<>();
        mMetadataOutputs = new CopyOnWriteArraySet<>();
    }

    public Context getContext() {
        return mContext;
    }

    public abstract P getInternalPlayer();

    protected abstract PlayerListenerHolder getInternalListener();

    protected abstract void initializePlayer();

    protected abstract void attachInternalListeners();

    protected abstract void resetInternalListeners();

    @Override
    public void addEventListener(EventListener listener) {
        mListenerHolders.addIfAbsent(new ListenerHolder<>(listener));
    }

    @Override
    public void removeEventListener(EventListener listener) {
        for (ListenerHolder listenerHolder : mListenerHolders) {
            if (listenerHolder.getListener().equals(listener)) {
                listenerHolder.release();
                mListenerHolders.remove(listenerHolder);
            }
        }
    }

    protected abstract void setSurface(@Nullable Surface surface);

    @Override
    public void setVolume(float audioVolume) {
        audioVolume = PlayerUtils.constrainValue(audioVolume, 0, 1);
        if (this.audioVolume == audioVolume) {
            return;
        }
        this.audioVolume = audioVolume;
        for (AudioListener audioListener : mAudioListeners) {
            audioListener.onVolumeChanged(audioVolume);
        }
    }

    @Override
    public float getVolume() {
        return audioVolume;
    }

    @Override
    public void addAudioListener(AudioListener audioListener) {
        mAudioListeners.add(audioListener);
    }

    @Override
    public void removeAudioListener(AudioListener audioListener) {
        mAudioListeners.remove(audioListener);
    }

    @Override
    public void addVideoListener(VideoListener videoListener) {
        mVideoListeners.add(videoListener);
    }

    @Override
    public void removeVideoListener(VideoListener videoListener) {
        mVideoListeners.add(videoListener);
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
    public void addTextOutput(TextOutput textOutput) {
        mTextOutputs.add(textOutput);
    }

    @Override
    public void removeTextOutput(TextOutput textOutput) {
        mTextOutputs.remove(textOutput);
    }

    @Override
    public void addMetadataOutput(MetadataOutput metadataOutput) {
        mMetadataOutputs.add(metadataOutput);
    }

    @Override
    public void removeMetadataOutput(MetadataOutput metadataOutput) {
        mMetadataOutputs.remove(metadataOutput);
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

    protected final void notifyOnVideoSizeChanged(final int width, final int height) {

        notifyEventListeners(new ListenerInvocation<EventListener>() {
            @Override
            public void invokeListener(EventListener listener) {
                listener.onVideoSizeChanged(AbsOnePlayer.this, width, height);
            }
        });
    }

    private void notifyEventListeners(final ListenerInvocation<EventListener> listenerInvocation) {
        notifyListeners(mListenerHolders, listenerInvocation);
    }

    /*
     * notify AudioListener
     */
    // TODO: 19-8-8 notify AudioListener

    /*
     * notify VideoListener
     */
    private void maybeNotifySurfaceSizeChanged(int width, int height) {
        if (width != surfaceWidth || height != surfaceHeight) {
            surfaceWidth = width;
            surfaceHeight = height;
            for (VideoListener videoSurfaceListener : mVideoListeners) {
                videoSurfaceListener.onSurfaceSizeChanged(width, height);
            }
        }
    }

    /*
     * notify TextOutput
     */
    // TODO: 19-8-8 notify TextOutput

    /*
     * notify MetadataOutput
     */
    // TODO: 19-8-8 notify MetadataOutput

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
            mPendingListenerNotifications.peekFirst().run();
            mPendingListenerNotifications.removeFirst();
        }

    }

    private <T> void invokeAll(CopyOnWriteArrayList<ListenerHolder<T>> listenerHolders, ListenerInvocation<T> listenerInvocation) {
        for (ListenerHolder<T> listenerHolder : listenerHolders) {
            listenerHolder.invoke(listenerInvocation);
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
            maybeNotifySurfaceSizeChanged(0, 0);
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    }
}
