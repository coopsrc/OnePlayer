package com.coopsrc.oneplayer.kernel.media;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;

import com.coopsrc.oneplayer.core.AbsOnePlayer;
import com.coopsrc.oneplayer.core.PlayerLibraryInfo;
import com.coopsrc.oneplayer.core.misc.IMediaDataSource;
import com.coopsrc.oneplayer.core.misc.ITrackInfo;
import com.coopsrc.oneplayer.core.misc.OneTimedText;
import com.coopsrc.oneplayer.core.utils.PlayerLogger;
import com.coopsrc.oneplayer.kernel.media.misc.AndroidMediaDataSource;
import com.coopsrc.oneplayer.kernel.media.misc.AndroidTrackInfo;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-07 17:16
 */
public final class OneMediaPlayer extends AbsOnePlayer<MediaPlayer> {
    private static final String TAG = "OneMediaPlayer";

    static {
        PlayerLibraryInfo.registerModule("one.player.media");
    }

    private final MediaPlayer mInternalPlayer;
    private final MediaPlayerListenerWrapper mInternalAdapterListener;

    private final Object mLock = new Object();

    public OneMediaPlayer(Context context) {
        super(context);

        synchronized (mLock) {
            mInternalPlayer = new MediaPlayer();
            mInternalPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mInternalPlayer.setScreenOnWhilePlaying(true);
        }
        mInternalAdapterListener = new MediaPlayerListenerWrapper(this);
        attachInternalListeners();
    }

    @Override
    public MediaPlayer getInternalPlayer() {
        return mInternalPlayer;
    }

    @Override
    protected PlayerListenerWrapper getInternalListener() {
        return mInternalAdapterListener;
    }

    @Override
    protected void initializePlayer() {

    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
        PlayerLogger.i(TAG, "setDataSource: %s", uri);
        if (mInternalPlayer != null) {
            mInternalPlayer.setDataSource(context, uri);
        }
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        PlayerLogger.i(TAG, "setDataSource: [%s] %s", uri, headers);
        if (mInternalPlayer != null) {
            mInternalPlayer.setDataSource(context, uri, headers);
        }
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        PlayerLogger.i(TAG, "setDataSource: %s", path);
        if (mInternalPlayer != null) {
            mInternalPlayer.setDataSource(path);
        }
    }

    @Override
    public void setDataSource(String path, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        PlayerLogger.i(TAG, "setDataSource: [%s] %s", path, headers);
        if (mInternalPlayer != null) {
            mInternalPlayer.setDataSource(path);
        }
    }

    @Override
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {
        PlayerLogger.i(TAG, "setDataSource: %s", fd);
        if (mInternalPlayer != null) {
            mInternalPlayer.setDataSource(fd);
        }
    }

    @Override
    public void setDataSource(FileDescriptor fd, long offset, long length) throws IOException, IllegalArgumentException, IllegalStateException {
        PlayerLogger.i(TAG, "setDataSource: [%s, %s, %s]", fd, offset, length);
        if (mInternalPlayer != null) {
            mInternalPlayer.setDataSource(fd, offset, length);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void setDataSource(IMediaDataSource dataSource) throws IllegalArgumentException, IllegalStateException {
        PlayerLogger.i(TAG, "setDataSource: %s", dataSource);
        if (mInternalPlayer != null) {
            mInternalPlayer.setDataSource(new AndroidMediaDataSource(dataSource));
        }
    }

    @Override
    protected void setSurface(Surface surface) {
        synchronized (mLock) {
            if (mInternalPlayer != null) {
                mInternalPlayer.setSurface(surface);
            }
        }
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        if (mInternalPlayer != null) {
            mInternalPlayer.prepareAsync();
        }
    }

    @Override
    public void start() throws IllegalStateException {
        if (mInternalPlayer != null) {
            mInternalPlayer.start();
        }
    }

    @Override
    public void stop() throws IllegalStateException {
        if (mInternalPlayer != null) {
            mInternalPlayer.stop();
        }
    }

    @Override
    public void pause() throws IllegalStateException {
        if (mInternalPlayer != null) {
            mInternalPlayer.pause();
        }
    }

    @Override
    public boolean release() {
        if (mInternalPlayer != null) {
            mInternalPlayer.release();
            return true;
        }

        return false;
    }

    @Override
    public void reset() {
        if (mInternalPlayer != null) {
            try {
                mInternalPlayer.reset();
            } catch (IllegalStateException e) {
                PlayerLogger.e(TAG, e.getMessage());
            }
        }

        resetInternalListeners();
        attachInternalListeners();
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void seekTo(long positionMs, int mode) {
        if (mInternalPlayer != null) {
            mInternalPlayer.seekTo(positionMs, mode);
        }
    }

    @Override
    public void seekTo(long positionMs) throws IllegalStateException {
        if (mInternalPlayer != null) {
            mInternalPlayer.seekTo((int) positionMs);
        }
    }

    @Override
    public void setVolume(float audioVolume) {
        super.setVolume(audioVolume);
        if (mInternalPlayer != null) {
            mInternalPlayer.setVolume(audioVolume, audioVolume);
        }
    }

    @Override
    public long getCurrentPosition() {
        if (mInternalPlayer != null) {
            try {
                return mInternalPlayer.getCurrentPosition();
            } catch (IllegalStateException e) {
                PlayerLogger.e(TAG, e.getMessage());
                return 0;
            }
        }
        return 0;
    }

    @Override
    public long getDuration() {
        if (mInternalPlayer != null) {
            try {
                return mInternalPlayer.getDuration();
            } catch (IllegalStateException e) {
                PlayerLogger.e(TAG, e.getMessage());
                return 0;
            }
        }
        return 0;
    }

    @Override
    public boolean isPlaying() {
        if (mInternalPlayer != null) {
            try {
                return mInternalPlayer.isPlaying();
            } catch (IllegalStateException e) {
                PlayerLogger.e(TAG, e.getMessage());
                return false;
            }
        }
        return false;
    }

    @Override
    public void setWakeMode(Context context, int mode) {

        if (mInternalPlayer != null) {
            mInternalPlayer.setWakeMode(context, mode);
        }
    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {

        if (mInternalPlayer != null) {
            mInternalPlayer.setScreenOnWhilePlaying(screenOn);
        }
    }

    @Override
    public int getVideoWidth() {
        if (mInternalPlayer != null) {
            return mInternalPlayer.getVideoWidth();
        }
        return super.getVideoWidth();
    }

    @Override
    public int getVideoHeight() {
        if (mInternalPlayer != null) {
            return mInternalPlayer.getVideoHeight();
        }
        return super.getVideoHeight();
    }

    @Override
    public void setLooping(boolean looping) {
        if (mInternalPlayer != null) {
            mInternalPlayer.setLooping(looping);
        }
    }

    @Override
    public boolean isLooping() {
        if (mInternalPlayer != null) {
            mInternalPlayer.isLooping();
        }
        return false;
    }

    @Override
    public ITrackInfo[] getTrackInfo() {
        return AndroidTrackInfo.fromMediaPlayer(mInternalPlayer);
    }

    @Override
    public void setAudioSessionId(int sessionId) {
        if (mInternalPlayer != null) {
            mInternalPlayer.setAudioSessionId(sessionId);
        }
    }

    @Override
    public int getAudioSessionId() {
        return mInternalPlayer.getAudioSessionId();
    }

    @Override
    protected void attachInternalListeners() {
        mInternalPlayer.setOnBufferingUpdateListener(mInternalAdapterListener);
        mInternalPlayer.setOnCompletionListener(mInternalAdapterListener);
        mInternalPlayer.setOnErrorListener(mInternalAdapterListener);
        mInternalPlayer.setOnInfoListener(mInternalAdapterListener);
        mInternalPlayer.setOnPreparedListener(mInternalAdapterListener);
        mInternalPlayer.setOnSeekCompleteListener(mInternalAdapterListener);
        mInternalPlayer.setOnTimedTextListener(mInternalAdapterListener);
        mInternalPlayer.setOnVideoSizeChangedListener(mInternalAdapterListener);
    }

    @Override
    protected void resetInternalListeners() {

    }

    private class MediaPlayerListenerWrapper extends PlayerListenerWrapper<OneMediaPlayer> implements
            MediaPlayer.OnBufferingUpdateListener,
            MediaPlayer.OnCompletionListener,
            MediaPlayer.OnErrorListener,
            MediaPlayer.OnInfoListener,
            MediaPlayer.OnPreparedListener,
            MediaPlayer.OnSeekCompleteListener,
            MediaPlayer.OnTimedTextListener,
            MediaPlayer.OnVideoSizeChangedListener {

        private MediaPlayerListenerWrapper(OneMediaPlayer player) {
            super(player);
        }

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            PlayerLogger.v(TAG, "onBufferingUpdate: %s", percent);
            if (getPlayer() != null) {
                getPlayer().notifyOnBufferingUpdate(percent);
            }
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            PlayerLogger.i(TAG, "onCompletion: ");
            if (getPlayer() != null) {
                getPlayer().notifyOnCompletion();
            }
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            PlayerLogger.i(TAG, "onError: [%s,%s]", what, extra);
            if (getPlayer() != null) {
                return getPlayer().notifyOnError(what, extra);
            }
            return false;
        }

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            PlayerLogger.i(TAG, "onInfo: [%s,%s]", what, extra);
            if (getPlayer() != null) {
                return getPlayer().notifyOnInfo(what, extra);
            }

            return false;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            PlayerLogger.i(TAG, "onPrepared: ");
            if (getPlayer() != null) {
                getPlayer().notifyOnPrepared();
            }
        }

        @Override
        public void onSeekComplete(MediaPlayer mp) {
            PlayerLogger.i(TAG, "onSeekComplete: ");
            if (getPlayer() != null) {
                getPlayer().notifyOnSeekComplete();
            }
        }

        @Override
        public void onTimedText(MediaPlayer mp, TimedText text) {
            PlayerLogger.i(TAG, "onTimedText: %s", text);
            if (getPlayer() != null && text != null) {
                getPlayer().notifyOnTimedText(new OneTimedText(text.getBounds(), text.getText()));
            }
        }

        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            PlayerLogger.i(TAG, "onVideoSizeChanged: [%s,%s]", width, height);
            if (getPlayer() != null) {
                getPlayer().notifyOnVideoSizeChanged(width, height);
            }
        }
    }
}
