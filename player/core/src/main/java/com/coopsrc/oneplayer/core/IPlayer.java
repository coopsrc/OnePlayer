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
import java.util.Map;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-09 15:18
 */
public interface IPlayer extends IPlayerInfo {

    void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    void setDataSource(String path, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    void setDataSource(AssetFileDescriptor afd) throws IOException, IllegalArgumentException, IllegalStateException;

    void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException;

    void setDataSource(FileDescriptor fd, long offset, long length) throws IOException, IllegalArgumentException, IllegalStateException;

    void setDataSource(MediaDataSource dataSource) throws IllegalArgumentException, IllegalStateException;

    void setDisplay(SurfaceHolder holder);

    void setSurface(Surface surface);

    void prepareAsync() throws IllegalStateException;

    void prepare() throws IOException, IllegalStateException;

    void start() throws IllegalStateException;

    void stop() throws IllegalStateException;

    void pause() throws IllegalStateException;

    void replay();

    boolean release();

    void reset();

    void seekTo(long msec, int mode);

    void seekTo(int msec) throws IllegalStateException;

    void setVolume(float volume);

    void setVolume(float leftVolume, float rightVolume);

    long getCurrentPosition();

    long getDuration();

    boolean isPlaying();

    void setWakeMode(Context context, int mode);

    void setScreenOnWhilePlaying(boolean screenOn);

    int getVideoWidth();

    int getVideoHeight();

    void setLooping(boolean looping);

    boolean isLooping();

    void setOnBufferingUpdateListener(OnBufferingUpdateListener onBufferingUpdateListener);

    void setOnCompletionListener(OnCompletionListener onCompletionListener);

    void setOnErrorListener(OnErrorListener onErrorListener);

    void setOnInfoListener(OnInfoListener onInfoListener);

    void setOnPreparedListener(OnPreparedListener onPreparedListener);

    void setOnSeekCompleteListener(OnSeekCompleteListener onSeekCompleteListener);

    void setOnTimedTextListener(OnTimedTextListener onTimedTextListener);

    void setOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener);

    interface OnBufferingUpdateListener {
        void onBufferingUpdate(IPlayer player, int percent);
    }

    interface OnCompletionListener {
        void onCompletion(IPlayer player);
    }

    interface OnErrorListener {
        boolean onError(IPlayer player, int what, int extra);
    }

    interface OnInfoListener {
        boolean onInfo(IPlayer player, int what, int extra);
    }

    interface OnPreparedListener {
        void onPrepared(IPlayer player);
    }

    interface OnSeekCompleteListener {
        void onSeekComplete(IPlayer player);
    }

    interface OnTimedTextListener {
        void onTimedText(IPlayer player, TimedText text);
    }

    interface OnVideoSizeChangedListener {
        void onVideoSizeChanged(IPlayer player, int width, int height);
    }
}
