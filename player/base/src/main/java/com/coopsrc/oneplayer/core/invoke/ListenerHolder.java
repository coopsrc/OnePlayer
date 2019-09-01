package com.coopsrc.oneplayer.core.invoke;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-08-08 14:15
 */
public final class ListenerHolder<T> {
    private final T mListener;

    private boolean released;

    public ListenerHolder(T listener) {
        mListener = listener;
    }

    public void release() {
        released = true;
    }

    public T getListener() {
        return mListener;
    }

    public void invoke(ListenerInvocation<T> invocation) {
        if (!released) {
            invocation.invokeListener(mListener);
        }
    }
}
