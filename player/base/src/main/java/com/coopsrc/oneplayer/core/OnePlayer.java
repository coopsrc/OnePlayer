package com.coopsrc.oneplayer.core;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.coopsrc.oneplayer.core.audio.AudioComponent;
import com.coopsrc.oneplayer.core.audio.AudioListener;
import com.coopsrc.oneplayer.core.playback.OnBufferingUpdateListener;
import com.coopsrc.oneplayer.core.playback.OnCompletionListener;
import com.coopsrc.oneplayer.core.playback.OnErrorListener;
import com.coopsrc.oneplayer.core.playback.OnInfoListener;
import com.coopsrc.oneplayer.core.playback.OnPlaybackStateChangedListener;
import com.coopsrc.oneplayer.core.playback.OnPreparedListener;
import com.coopsrc.oneplayer.core.playback.OnSeekCompleteListener;
import com.coopsrc.oneplayer.core.playback.OnTimedMetaDataAvailableListener;
import com.coopsrc.oneplayer.core.playback.OnTimedTextListener;
import com.coopsrc.oneplayer.core.misc.IMediaDataSource;
import com.coopsrc.oneplayer.core.misc.ITrackInfo;
import com.coopsrc.oneplayer.core.video.VideoComponent;
import com.coopsrc.oneplayer.core.video.VideoListener;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-09 15:18
 */
public interface OnePlayer extends IPlayerInfo, AudioComponent, VideoComponent {
    String TAG = "OnePlayer";

    void addEventListener(EventListener listener);

    void removeEventListener(EventListener listener);

    void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    void setDataSource(String path, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

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

    void seekTo(long positionMs, int mode);

    void seekTo(long positionMs) throws IllegalStateException;

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

    int getRotationDegrees();

    float getPixelRatio();

    void setLooping(boolean looping);

    boolean isLooping();

    ITrackInfo[] getTrackInfo();

    int getPlaybackState();

    interface EventListener extends
            OnBufferingUpdateListener,
            OnCompletionListener,
            OnErrorListener,
            OnInfoListener,
            OnPreparedListener,
            OnSeekCompleteListener,
            OnTimedTextListener,
            OnTimedMetaDataAvailableListener,
            OnPlaybackStateChangedListener,
            AudioListener,
            VideoListener {
    }
}
