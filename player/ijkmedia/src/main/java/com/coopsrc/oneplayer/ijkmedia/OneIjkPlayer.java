package com.coopsrc.oneplayer.ijkmedia;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaDataSource;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.coopsrc.oneplayer.core.AbsPlayer;
import com.coopsrc.oneplayer.core.utils.LogUtils;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;

/**
 * @author Tingkuo
 * <p>
 * Date: 2019-05-09 23:19
 */
public class OneIjkPlayer extends AbsPlayer<IjkMediaPlayer> {
    private static final String TAG = "OneIjkPlayer";

    private final IjkMediaPlayer mInternalPlayer;
    private final IjkMediaPlayerListenerHolder mInternalAdapterListener;

    private final Object mLock = new Object();

    public OneIjkPlayer(Context context) {
        super(context);

        synchronized (mLock) {
            mInternalPlayer = new IjkMediaPlayer();
            mInternalPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        mInternalAdapterListener = new IjkMediaPlayerListenerHolder(this);
        attachInternalListeners();
    }

    @Override
    public IjkMediaPlayer getInternalPlayer() {
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
    public void setDataSource(String path) {
        if (mInternalPlayer != null) {
            try {
                mInternalPlayer.setDataSource(path);
            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.e(TAG, e.getMessage());
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void setDataSource(AssetFileDescriptor afd) throws IOException, IllegalArgumentException, IllegalStateException {
        if (mInternalPlayer != null) {
//            mInternalPlayer.setDataSource(afd);
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
//            mInternalPlayer.setDataSource(fd, offset, length);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void setDataSource(final MediaDataSource dataSource) throws IllegalArgumentException, IllegalStateException {
        if (mInternalPlayer != null) {
            mInternalPlayer.setDataSource(new IMediaDataSource() {
                @Override
                public int readAt(long position, byte[] buffer, int offset, int size) throws IOException {
                    return dataSource.readAt(position, buffer, offset, size);
                }

                @Override
                public long getSize() throws IOException {
                    return dataSource.getSize();
                }

                @Override
                public void close() throws IOException {
                    dataSource.close();
                }
            });
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
    public void prepare() throws IOException, IllegalStateException {
        if (mInternalPlayer != null) {
//            mInternalPlayer.prepareAsync();
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
    public void replay() {

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
            mInternalPlayer.reset();
        }
        resetInternalListeners();
        attachInternalListeners();
    }

    @Override
    public void seekTo(long msec, int mode) {
        if (mInternalPlayer != null) {
            mInternalPlayer.seekTo(msec);
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

    private class IjkMediaPlayerListenerHolder extends PlayerListenerHolder<OneIjkPlayer> implements
            IjkMediaPlayer.OnBufferingUpdateListener,
            IjkMediaPlayer.OnCompletionListener,
            IjkMediaPlayer.OnErrorListener,
            IjkMediaPlayer.OnInfoListener,
            IjkMediaPlayer.OnPreparedListener,
            IjkMediaPlayer.OnSeekCompleteListener,
            IjkMediaPlayer.OnTimedTextListener,
            IjkMediaPlayer.OnVideoSizeChangedListener {

        private IjkMediaPlayerListenerHolder(OneIjkPlayer player) {
            super(player);
        }

        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            if (getPlayer() != null) {
                notifyOnBufferingUpdate(percent);
            }
        }

        @Override
        public void onCompletion(IMediaPlayer mp) {
            if (getPlayer() != null) {
                notifyOnCompletion();
            }
        }

        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            if (getPlayer() != null) {
                return notifyOnError(what, extra);
            }
            return false;
        }

        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            if (getPlayer() != null) {
                return notifyOnInfo(what, extra);
            }

            return false;
        }

        @Override
        public void onPrepared(IMediaPlayer mp) {
            if (getPlayer() != null) {
                notifyOnPrepared();
            }
        }

        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            if (getPlayer() != null) {
                notifyOnSeekComplete();
            }
        }

        @Override
        public void onTimedText(IMediaPlayer mp, IjkTimedText text) {
            if (getPlayer() != null) {
//                notifyOnTimedText(text);
            }
        }

        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
            if (getPlayer() != null) {
                notifyOnVideoSizeChanged(width, height);
            }
        }
    }
}
