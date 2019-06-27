package com.coopsrc.oneplayer.core;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import androidx.annotation.Nullable;

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
public class MediaPlayerProxy implements OnePlayer {
    private final OnePlayer mBackendPlayer;

    public MediaPlayerProxy(OnePlayer backendPlayer) {
        mBackendPlayer = backendPlayer;
    }

    public OnePlayer getBackendPlayer() {
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
    public void addVideoSurfaceListener(VideoSurfaceListener videoSurfaceListener) {
        mBackendPlayer.addVideoSurfaceListener(videoSurfaceListener);
    }

    @Override
    public void removeVideoSurfaceListener(VideoSurfaceListener videoSurfaceListener) {
        mBackendPlayer.removeVideoSurfaceListener(videoSurfaceListener);
    }

    @Override
    public void clearVideoSurface() {
        mBackendPlayer.clearVideoSurface();
    }

    @Override
    public void setVideoSurface(@Nullable Surface surface) {
        mBackendPlayer.setVideoSurface(surface);
    }

    @Override
    public void clearVideoSurface(Surface surface) {
        mBackendPlayer.clearVideoSurface(surface);
    }

    @Override
    public void setVideoSurfaceHolder(SurfaceHolder surfaceHolder) {
        mBackendPlayer.setVideoSurfaceHolder(surfaceHolder);
    }

    @Override
    public void clearVideoSurfaceHolder(SurfaceHolder surfaceHolder) {
        mBackendPlayer.clearVideoSurfaceHolder(surfaceHolder);
    }

    @Override
    public void setVideoSurfaceView(SurfaceView surfaceView) {
        mBackendPlayer.setVideoSurfaceView(surfaceView);
    }

    @Override
    public void clearVideoSurfaceView(SurfaceView surfaceView) {
        mBackendPlayer.setVideoSurfaceView(surfaceView);
    }

    @Override
    public void setVideoTextureView(TextureView textureView) {
        mBackendPlayer.setVideoTextureView(textureView);
    }

    @Override
    public void clearVideoTextureView(TextureView textureView) {
        mBackendPlayer.clearVideoTextureView(textureView);
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
    public int getCurrentPercentage() {
        return mBackendPlayer.getCurrentPercentage();
    }

    @Override
    public int getBufferedPercentage() {
        return mBackendPlayer.getBufferedPercentage();
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
    public long getBufferedPosition() {
        return mBackendPlayer.getBufferedPosition();
    }

    @Override
    public int getPlaybackState() {
        return mBackendPlayer.getPlaybackState();
    }

    @Override
    public void setPlayWhenReady(boolean playWhenReady) {
        mBackendPlayer.setPlayWhenReady(playWhenReady);
    }

    @Override
    public boolean getPlayWhenReady() {
        return mBackendPlayer.getPlayWhenReady();
    }

    @Override
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener onBufferingUpdateListener) {
        if (onBufferingUpdateListener != null) {
            final OnBufferingUpdateListener listener = onBufferingUpdateListener;
            mBackendPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(OnePlayer player, int percent) {
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
                public void onCompletion(OnePlayer player) {
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
                public boolean onError(OnePlayer player, int what, int extra) {
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
                public boolean onInfo(OnePlayer player, int what, int extra) {
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
                public void onPrepared(OnePlayer player) {
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
                public void onSeekComplete(OnePlayer player) {
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
                public void onTimedText(OnePlayer player, ITimedText text) {
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
                public void onVideoSizeChanged(OnePlayer player, int width, int height) {
                    listener.onVideoSizeChanged(MediaPlayerProxy.this, width, height);
                }
            });
        } else {
            mBackendPlayer.setOnVideoSizeChangedListener(null);
        }
    }

    @Override
    public void setOnPlayerStateChangedListener(OnPlaybackStateChangedListener onPlaybackStateChangedListener) {

    }
}
