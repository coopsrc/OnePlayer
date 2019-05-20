package com.coopsrc.oneplayer.core;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-10 18:04
 */
public final class EventListenerHolder {
    private final IOnePlayer.EventListener mListener;

    private boolean released;

    public EventListenerHolder(IOnePlayer.EventListener listener) {
        mListener = listener;
    }

    public void release() {
        released = true;
    }

    public IOnePlayer.EventListener getListener() {
        return mListener;
    }

    public void invoke(ListenerInvocation listenerInvocation) {
        if (!released) {
            listenerInvocation.invokeListener(mListener);
        }
    }

    public interface ListenerInvocation {
        void invokeListener(IOnePlayer.EventListener listener);
    }
}
