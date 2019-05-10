package com.coopsrc.oneplayer.media;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.coopsrc.oneplayer.core.AbsOnePlayer;
import com.coopsrc.oneplayer.core.misc.IMediaDataSource;
import com.coopsrc.oneplayer.core.misc.ITrackInfo;
import com.coopsrc.oneplayer.core.misc.OneTimedText;
import com.coopsrc.oneplayer.core.utils.LogUtils;
import com.coopsrc.oneplayer.media.misc.AndroidMediaDataSource;
import com.coopsrc.oneplayer.media.misc.AndroidTrackInfo;

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

    private final MediaPlayer mInternalPlayer;
    private final MediaPlayerListenerHolder mInternalAdapterListener;

    private final Object mLock = new Object();

    public OneMediaPlayer(Context context) {
        super(context);

        synchronized (mLock) {
            mInternalPlayer = new MediaPlayer();
            mInternalPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        mInternalAdapterListener = new MediaPlayerListenerHolder(this);
        attachInternalListeners();
    }


    @Override
    public void setDataSource(Context context, Uri uri) {
        if (mInternalPlayer != null) {
            try {
                mInternalPlayer.setDataSource(context, uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        if (mInternalPlayer != null) {
            mInternalPlayer.setDataSource(context, uri, headers);
        }
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        if (mInternalPlayer != null) {
            mInternalPlayer.setDataSource(path);
        }
    }

    @Override
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {
        if (mInternalPlayer != null) {
            mInternalPlayer.setDataSource(fd);
        }
    }

    @Override
    public void setDataSource(FileDescriptor fd, long offset, long length) throws IOException, IllegalArgumentException, IllegalStateException {
        if (mInternalPlayer != null) {
            mInternalPlayer.setDataSource(fd, offset, length);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void setDataSource(IMediaDataSource dataSource) throws IllegalArgumentException, IllegalStateException {
        if (mInternalPlayer != null) {
            mInternalPlayer.setDataSource(new AndroidMediaDataSource(dataSource));
        }
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        synchronized (mLock) {
            if (mInternalPlayer != null) {
                mInternalPlayer.setDisplay(holder);
            }
        }
    }

    @Override
    public void setSurface(Surface surface) {
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
                LogUtils.e(TAG, e.getMessage());
            }
        }

        resetInternalListeners();
        attachInternalListeners();
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void seekTo(long msec, int mode) {
        if (mInternalPlayer != null) {
            mInternalPlayer.seekTo(msec, mode);
        }
    }

    @Override
    public void seekTo(int msec) throws IllegalStateException {
        if (mInternalPlayer != null) {
            mInternalPlayer.seekTo(msec);
        }
    }

    @Override
    public void setVolume(float volume) {
        if (mInternalPlayer != null) {
            mInternalPlayer.setVolume(volume, volume);
        }
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        if (mInternalPlayer != null) {
            mInternalPlayer.setVolume(leftVolume, rightVolume);
        }
    }

    @Override
    public long getCurrentPosition() {
        if (mInternalPlayer != null) {
            try {
                return mInternalPlayer.getCurrentPosition();
            } catch (IllegalStateException e) {
                LogUtils.e(TAG, e.getMessage());
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
                LogUtils.e(TAG, e.getMessage());
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
                LogUtils.e(TAG, e.getMessage());
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
        return 0;
    }

    @Override
    public int getVideoHeight() {
        if (mInternalPlayer != null) {
            return mInternalPlayer.getVideoHeight();
        }
        return 0;
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
    public MediaPlayer getInternalPlayer() {
        return mInternalPlayer;
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

    private class MediaPlayerListenerHolder extends PlayerListenerHolder<OneMediaPlayer> implements
            MediaPlayer.OnBufferingUpdateListener,
            MediaPlayer.OnCompletionListener,
            MediaPlayer.OnErrorListener,
            MediaPlayer.OnInfoListener,
            MediaPlayer.OnPreparedListener,
            MediaPlayer.OnSeekCompleteListener,
            MediaPlayer.OnTimedTextListener,
            MediaPlayer.OnVideoSizeChangedListener {

        private MediaPlayerListenerHolder(OneMediaPlayer player) {
            super(player);
        }

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (getPlayer() != null) {
                notifyOnBufferingUpdate(percent);
            }
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            if (getPlayer() != null) {
                notifyOnCompletion();
            }
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            if (getPlayer() != null) {
                return notifyOnError(what, extra);
            }
            return false;
        }

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (getPlayer() != null) {
                return notifyOnInfo(what, extra);
            }

            return false;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            if (getPlayer() != null) {
                notifyOnPrepared();
            }
        }

        @Override
        public void onSeekComplete(MediaPlayer mp) {
            if (getPlayer() != null) {
                notifyOnSeekComplete();
            }
        }

        @Override
        public void onTimedText(MediaPlayer mp, TimedText text) {
            if (getPlayer() != null && text != null) {
                notifyOnTimedText(new OneTimedText(text.getBounds(), text.getText()));
            }
        }

        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            if (getPlayer() != null) {
                notifyOnVideoSizeChanged(width, height);
            }
        }
    }
}
