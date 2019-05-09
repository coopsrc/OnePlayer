package com.coopsrc.oneplayer.core;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaDataSource;
import android.media.TimedText;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-09 15:30
 */
public abstract class AbsPlayer<P> implements IPlayer {
    private OnBufferingUpdateListener mOnBufferingUpdateListener;
    private OnCompletionListener mOnCompletionListener;
    private OnErrorListener mOnErrorListener;
    private OnInfoListener mOnInfoListener;
    private OnPreparedListener mOnPreparedListener;
    private OnSeekCompleteListener mOnSeekCompleteListener;
    private OnTimedTextListener mOnTimedTextListener;
    private OnVideoSizeChangedListener mOnVideoSizeChangedListener;

    private final Context mContext;

    public AbsPlayer(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {

    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {

    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {

    }

    @Override
    public void setDataSource(String path, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {

    }

    @Override
    public void setDataSource(AssetFileDescriptor afd) throws IOException, IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void setDataSource(FileDescriptor fd, long offset, long length) throws IOException, IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void setDataSource(MediaDataSource dataSource) throws IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void setDisplay(SurfaceHolder holder) {

    }

    @Override
    public void setSurface(Surface surface) {

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

    protected final void notifyOnBufferingUpdate(int percent) {
        if (mOnBufferingUpdateListener != null) {
            mOnBufferingUpdateListener.onBufferingUpdate(this, percent);
        }
    }

    protected final void notifyOnCompletion() {
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onCompletion(this);
        }
    }

    protected final boolean notifyOnError(int what, int extra) {
        if (mOnErrorListener != null) {
            return mOnErrorListener.onError(this, what, extra);
        }
        return false;
    }

    protected final boolean notifyOnInfo(int what, int extra) {
        if (mOnInfoListener != null) {
            return mOnInfoListener.onInfo(this, what, extra);
        }
        return false;
    }

    protected final void notifyOnPrepared() {
        if (mOnPreparedListener != null) {
            mOnPreparedListener.onPrepared(this);
        }
    }

    protected final void notifyOnSeekComplete() {
        if (mOnSeekCompleteListener != null) {
            mOnSeekCompleteListener.onSeekComplete(this);
        }
    }

    protected final void notifyOnTimedText(TimedText text) {
        if (mOnTimedTextListener != null) {
            mOnTimedTextListener.onTimedText(this, text);
        }
    }

    protected final void notifyOnVideoSizeChanged(int width, int height) {
        if (mOnVideoSizeChangedListener != null) {
            mOnVideoSizeChangedListener.onVideoSizeChanged(this, width, height);
        }
    }

    protected abstract class PlayerListenerHolder<T extends AbsPlayer> {

        private final WeakReference<T> mPlayerReference;

        public PlayerListenerHolder(T player) {
            mPlayerReference = new WeakReference<>(player);
        }

        protected T getPlayer() {
            return mPlayerReference.get();
        }
    }
}
