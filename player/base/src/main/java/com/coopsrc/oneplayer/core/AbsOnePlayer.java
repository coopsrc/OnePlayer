package com.coopsrc.oneplayer.core;

import android.content.Context;

import com.coopsrc.oneplayer.core.misc.ITimedText;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.concurrent.CopyOnWriteArrayList;

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

    private final Context mContext;

    private int mBufferedPercentage;

    public AbsOnePlayer(Context context) {
        mContext = context;

        mListenerHolders = new CopyOnWriteArrayList<>();
        mPendingListenerNotifications = new ArrayDeque<>();
    }

    public Context getContext() {
        return mContext;
    }

    public abstract P getInternalPlayer();

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

    public int getBufferedPercentage() {
        return mBufferedPercentage;
    }

    @Override
    public int getCurrentPercentage() {
        return (int) (getCurrentPosition() / getDuration());
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

    protected abstract class PlayerListenerHolder<T extends AbsOnePlayer> {

        private final WeakReference<T> mPlayerReference;

        public PlayerListenerHolder(T player) {
            mPlayerReference = new WeakReference<>(player);
        }

        protected T getPlayer() {
            return mPlayerReference.get();
        }
    }
}
