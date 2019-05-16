package com.coopsrc.oneplayer.core;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.coopsrc.oneplayer.core.misc.IMediaDataSource;
import com.coopsrc.oneplayer.core.misc.ITimedText;
import com.coopsrc.oneplayer.core.misc.ITrackInfo;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-10 12:07
 */
public class MediaPlayerProxy implements IOnePlayer {
    private final IOnePlayer mBackendPlayer;

    public MediaPlayerProxy(IOnePlayer backendPlayer) {
        mBackendPlayer = backendPlayer;
    }

    public IOnePlayer getBackendPlayer() {
        return mBackendPlayer;
    }

    @Override
    public void addListener(EventListener listener) {
        mBackendPlayer.addListener(listener);
    }

    @Override
    public void removeListener(EventListener listener) {
        mBackendPlayer.removeListener(listener);
    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mBackendPlayer.setDataSource(context, uri);
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mBackendPlayer.setDataSource(context, uri, headers);
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mBackendPlayer.setDataSource(path);
    }

    @Override
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {
        mBackendPlayer.setDataSource(fd);
    }

    @Override
    public void setDataSource(FileDescriptor fd, long offset, long length) throws IOException, IllegalArgumentException, IllegalStateException {
        mBackendPlayer.setDataSource(fd, offset, length);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void setDataSource(IMediaDataSource dataSource) throws IllegalArgumentException, IllegalStateException {
        mBackendPlayer.setDataSource(dataSource);
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        mBackendPlayer.setDisplay(holder);
    }

    @Override
    public void setSurface(Surface surface) {
        mBackendPlayer.setSurface(surface);
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        mBackendPlayer.prepareAsync();
    }

    @Override
    public void start() throws IllegalStateException {
        mBackendPlayer.start();
    }

    @Override
    public void stop() throws IllegalStateException {
        mBackendPlayer.stop();
    }

    @Override
    public void pause() throws IllegalStateException {
        mBackendPlayer.pause();
    }

    @Override
    public boolean release() {
        return mBackendPlayer.release();
    }

    @Override
    public void reset() {
        mBackendPlayer.reset();
    }

    @Override
    public void seekTo(long msec, int mode) {
        mBackendPlayer.seekTo(msec, mode);
    }

    @Override
    public void seekTo(long msec) throws IllegalStateException {
        mBackendPlayer.seekTo(msec);
    }

    @Override
    public void setVolume(float volume) {
        mBackendPlayer.setVolume(volume);
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        mBackendPlayer.setVolume(leftVolume, rightVolume);
    }

    @Override
    public long getCurrentPosition() {
        return mBackendPlayer.getCurrentPosition();
    }

    @Override
    public long getBufferedPosition() {
        return mBackendPlayer.getBufferedPosition();
    }

    @Override
    public long getDuration() {
        return mBackendPlayer.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return mBackendPlayer.isPlaying();
    }

    @Override
    public void setWakeMode(Context context, int mode) {
        mBackendPlayer.setWakeMode(context, mode);
    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {
        mBackendPlayer.setScreenOnWhilePlaying(screenOn);
    }

    @Override
    public int getVideoWidth() {
        return mBackendPlayer.getVideoWidth();
    }

    @Override
    public int getVideoHeight() {
        return mBackendPlayer.getVideoHeight();
    }

    @Override
    public void setLooping(boolean looping) {
        mBackendPlayer.setLooping(looping);
    }

    @Override
    public boolean isLooping() {
        return mBackendPlayer.isLooping();
    }

    @Override
    public ITrackInfo[] getTrackInfo() {
        return mBackendPlayer.getTrackInfo();
    }

    @Override
    public int getAudioSessionId() {
        return mBackendPlayer.getAudioSessionId();
    }

    @Override
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener onBufferingUpdateListener) {
        if (onBufferingUpdateListener != null) {
            final OnBufferingUpdateListener listener = onBufferingUpdateListener;
            mBackendPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(IOnePlayer player, int percent) {
                    listener.onBufferingUpdate(MediaPlayerProxy.this, percent);
                }
            });
        } else {
            mBackendPlayer.setOnBufferingUpdateListener(null);
        }
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
        if (onCompletionListener != null) {
            final OnCompletionListener listener = onCompletionListener;
            mBackendPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(IOnePlayer player) {
                    listener.onCompletion(MediaPlayerProxy.this);
                }
            });
        } else {
            mBackendPlayer.setOnCompletionListener(null);
        }
    }

    @Override
    public void setOnErrorListener(OnErrorListener onErrorListener) {
        if (onErrorListener != null) {
            final OnErrorListener listener = onErrorListener;
            mBackendPlayer.setOnErrorListener(new OnErrorListener() {
                @Override
                public boolean onError(IOnePlayer player, int what, int extra) {
                    return listener.onError(MediaPlayerProxy.this, what, extra);
                }
            });
        } else {
            mBackendPlayer.setOnErrorListener(null);
        }
    }

    @Override
    public void setOnInfoListener(OnInfoListener onInfoListener) {
        if (onInfoListener != null) {
            final OnInfoListener listener = onInfoListener;
            mBackendPlayer.setOnInfoListener(new OnInfoListener() {
                @Override
                public boolean onInfo(IOnePlayer player, int what, int extra) {
                    return listener.onInfo(MediaPlayerProxy.this, what, extra);
                }
            });
        } else {
            mBackendPlayer.setOnInfoListener(null);
        }
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        if (onPreparedListener != null) {
            final OnPreparedListener listener = onPreparedListener;
            mBackendPlayer.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared(IOnePlayer player) {
                    listener.onPrepared(MediaPlayerProxy.this);
                }
            });
        } else {
            mBackendPlayer.setOnPreparedListener(null);
        }
    }

    @Override
    public void setOnSeekCompleteListener(OnSeekCompleteListener onSeekCompleteListener) {
        if (onSeekCompleteListener != null) {
            final OnSeekCompleteListener listener = onSeekCompleteListener;
            mBackendPlayer.setOnSeekCompleteListener(new OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(IOnePlayer player) {
                    listener.onSeekComplete(MediaPlayerProxy.this);
                }
            });
        } else {
            mBackendPlayer.setOnSeekCompleteListener(null);
        }
    }

    @Override
    public void setOnTimedTextListener(OnTimedTextListener onTimedTextListener) {
        if (onTimedTextListener != null) {
            final OnTimedTextListener listener = onTimedTextListener;
            mBackendPlayer.setOnTimedTextListener(new OnTimedTextListener() {
                @Override
                public void onTimedText(IOnePlayer player, ITimedText text) {
                    listener.onTimedText(MediaPlayerProxy.this, text);
                }
            });
        } else {
            mBackendPlayer.setOnTimedTextListener(null);
        }
    }

    @Override
    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener) {
        if (onVideoSizeChangedListener != null) {
            final OnVideoSizeChangedListener listener = onVideoSizeChangedListener;
            mBackendPlayer.setOnVideoSizeChangedListener(new OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(IOnePlayer player, int width, int height) {
                    listener.onVideoSizeChanged(MediaPlayerProxy.this, width, height);
                }
            });
        } else {
            mBackendPlayer.setOnVideoSizeChangedListener(null);
        }
    }
}
