package com.coopsrc.oneplayer.core;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.coopsrc.oneplayer.core.misc.IMediaDataSource;
import com.coopsrc.oneplayer.core.misc.ITimedText;
import com.coopsrc.oneplayer.core.misc.ITrackInfo;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-09 15:18
 */
public interface OnePlayer extends IPlayerInfo, IPlayerSurface {

    void addListener(EventListener listener);

    void removeListener(EventListener listener);

    void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException;

    void setDataSource(FileDescriptor fd, long offset, long length) throws IOException, IllegalArgumentException, IllegalStateException;

    @RequiresApi(api = Build.VERSION_CODES.M)
    void setDataSource(IMediaDataSource dataSource) throws IllegalArgumentException, IllegalStateException;

    void prepareAsync() throws IllegalStateException;

    void start() throws IllegalStateException;

    void stop() throws IllegalStateException;

    void pause() throws IllegalStateException;

    boolean release();

    void reset();

    void seekTo(long msec, int mode);

    void seekTo(long msec) throws IllegalStateException;

    void setVolume(float volume);

    void setVolume(float leftVolume, float rightVolume);

    long getCurrentPosition();

    int getCurrentPercentage();

    long getBufferedPosition();

    int getBufferedPercentage();

    long getDuration();

    boolean isPlaying();

    void setWakeMode(Context context, int mode);

    void setScreenOnWhilePlaying(boolean screenOn);

    int getVideoWidth();

    int getVideoHeight();

    void setLooping(boolean looping);

    boolean isLooping();

    ITrackInfo[] getTrackInfo();

    int getAudioSessionId();

    int getPlaybackState();

    void setPlayWhenReady(boolean playWhenReady);

    boolean getPlayWhenReady();

    void setOnBufferingUpdateListener(OnBufferingUpdateListener onBufferingUpdateListener);

    void setOnCompletionListener(OnCompletionListener onCompletionListener);

    void setOnErrorListener(OnErrorListener onErrorListener);

    void setOnInfoListener(OnInfoListener onInfoListener);

    void setOnPreparedListener(OnPreparedListener onPreparedListener);

    void setOnSeekCompleteListener(OnSeekCompleteListener onSeekCompleteListener);

    void setOnTimedTextListener(OnTimedTextListener onTimedTextListener);

    void setOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener);

    void setOnPlayerStateChangedListener(OnPlaybackStateChangedListener onPlaybackStateChangedListener);

    interface OnBufferingUpdateListener {
        default void onBufferingUpdate(OnePlayer player, int percent) {
        }
    }

    interface OnCompletionListener {
        default void onCompletion(OnePlayer player) {
        }
    }

    interface OnErrorListener {
        default boolean onError(OnePlayer player, int what, int extra) {
            return true;
        }
    }

    interface OnInfoListener {
        default boolean onInfo(OnePlayer player, int what, int extra) {
            return true;
        }
    }

    interface OnPreparedListener {
        default void onPrepared(OnePlayer player) {
        }
    }

    interface OnSeekCompleteListener {
        default void onSeekComplete(OnePlayer player) {
        }
    }

    interface OnTimedTextListener {
        default void onTimedText(OnePlayer player, ITimedText text) {
        }
    }

    interface OnVideoSizeChangedListener {
        default void onVideoSizeChanged(OnePlayer player, int width, int height) {
        }
    }

    interface OnPlaybackStateChangedListener {
        default void onPlaybackStateChanged(boolean playWhenReady, int playbackState) {
        }
    }

    interface EventListener extends
            OnBufferingUpdateListener,
            OnCompletionListener,
            OnErrorListener,
            OnInfoListener,
            OnPreparedListener,
            OnSeekCompleteListener,
            OnTimedTextListener,
            OnVideoSizeChangedListener,
            OnPlaybackStateChangedListener,
            VideoSurfaceListener {

    }
}
