package com.coopsrc.oneplayer.kernel.ijkmedia;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Surface;

import com.coopsrc.oneplayer.core.AbsOnePlayer;
import com.coopsrc.oneplayer.core.PlayerLibraryInfo;
import com.coopsrc.oneplayer.core.misc.IMediaDataSource;
import com.coopsrc.oneplayer.core.misc.ITrackInfo;
import com.coopsrc.oneplayer.core.misc.OneTimedText;
import com.coopsrc.oneplayer.core.utils.PlayerLogger;
import com.coopsrc.oneplayer.kernel.ijkmedia.misc.IjkMediaDataSource;
import com.coopsrc.oneplayer.kernel.ijkmedia.misc.OneIjkTrackInfo;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;

/**
 * @author Tingkuo
 * <p>
 * Date: 2019-05-09 23:19
 */
public class OneIjkPlayer extends AbsOnePlayer<IjkMediaPlayer> {
    private static final String TAG = "OneIjkPlayer";

    static {
        PlayerLibraryInfo.registerModule("one.player.ijk");
    }

    private IjkMediaPlayer mInternalPlayer;
    private IjkMediaPlayerListenerWrapper mInternalAdapterListener;

    private final Object mLock = new Object();

    public OneIjkPlayer(Context context) {
        super(context);

        synchronized (mLock) {
            initializePlayer();
        }
        mInternalAdapterListener = new IjkMediaPlayerListenerWrapper(this);
        attachInternalListeners();
    }

    @Override
    public IjkMediaPlayer getInternalPlayer() {
        return mInternalPlayer;
    }

    @Override
    protected PlayerListenerWrapper getInternalListener() {
        return mInternalAdapterListener;
    }

    @Override
    protected void initializePlayer() {
        mInternalPlayer = new IjkMediaPlayer();
        mInternalPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mInternalPlayer.setScreenOnWhilePlaying(true);
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
        mInternalPlayer.setOnControlMessageListener(mInternalAdapterListener);
        mInternalPlayer.setOnMediaCodecSelectListener(mInternalAdapterListener);
        mInternalPlayer.setOnNativeInvokeListener(mInternalAdapterListener);
    }

    @Override
    protected void resetInternalListeners() {

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
    public void setDataSource(String path, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        if (mInternalPlayer != null) {
            mInternalPlayer.setDataSource(path, headers);
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
            mInternalPlayer.setDataSource(fd);
        }
    }

    @Override
    public void setDataSource(IMediaDataSource dataSource) throws IllegalArgumentException, IllegalStateException {
        if (mInternalPlayer != null) {
            mInternalPlayer.setDataSource(new IjkMediaDataSource(dataSource));
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
            mInternalPlayer.reset();
        }
        resetInternalListeners();
        attachInternalListeners();
    }

    @Override
    public void seekTo(long positionMs, int mode) {
        if (mInternalPlayer != null) {
            mInternalPlayer.seekTo(positionMs);
        }
    }

    @Override
    public void seekTo(long positionMs) throws IllegalStateException {
        if (mInternalPlayer != null) {
            mInternalPlayer.seekTo(positionMs);
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
    public float getVolume() {
        return super.getVolume();
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
        return OneIjkTrackInfo.fromIjkMediaPlayer(mInternalPlayer);
    }

    @Override
    public void setAudioSessionId(int sessionId) {

    }

    @Override
    public int getAudioSessionId() {
        return mInternalPlayer.getAudioSessionId();
    }

    private class IjkMediaPlayerListenerWrapper extends PlayerListenerWrapper<OneIjkPlayer> implements
            IjkMediaPlayer.OnBufferingUpdateListener,
            IjkMediaPlayer.OnCompletionListener,
            IjkMediaPlayer.OnErrorListener,
            IjkMediaPlayer.OnInfoListener,
            IjkMediaPlayer.OnPreparedListener,
            IjkMediaPlayer.OnSeekCompleteListener,
            IjkMediaPlayer.OnTimedTextListener,
            IjkMediaPlayer.OnVideoSizeChangedListener,
            IjkMediaPlayer.OnControlMessageListener,
            IjkMediaPlayer.OnMediaCodecSelectListener,
            IjkMediaPlayer.OnNativeInvokeListener {

        private IjkMediaPlayerListenerWrapper(OneIjkPlayer player) {
            super(player);
        }

        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            PlayerLogger.v(TAG, "onBufferingUpdate: %s", percent);
            if (getPlayer() != null) {
                notifyOnPlaybackStateChanged(STATE_BUFFERING);
                notifyOnBufferingUpdate(percent);
            }
        }

        @Override
        public void onCompletion(IMediaPlayer mp) {
            PlayerLogger.i(TAG, "onCompletion: ");
            if (getPlayer() != null) {
                notifyOnPlaybackStateChanged(STATE_COMPLETION);
                notifyOnCompletion();
            }
        }

        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            PlayerLogger.i(TAG, "onError: [%s,%s]", what, extra);
            if (getPlayer() != null) {
                notifyOnPlaybackStateChanged(STATE_ERROR);
                return notifyOnError(what, extra);
            }
            return false;
        }

        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            PlayerLogger.i(TAG, "onInfo: [%s,%s]", what, extra);
            if (getPlayer() != null) {
                return notifyOnInfo(what, extra);
            }

            return false;
        }

        @Override
        public void onPrepared(IMediaPlayer mp) {
            PlayerLogger.i(TAG, "onPrepared: ");
            if (getPlayer() != null) {
                notifyOnPlaybackStateChanged(STATE_PREPARED);
                notifyOnPrepared();
            }
        }

        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            PlayerLogger.i(TAG, "onSeekComplete: ");
            if (getPlayer() != null) {
                notifyOnSeekComplete();
            }
        }

        @Override
        public void onTimedText(IMediaPlayer mp, IjkTimedText text) {
            PlayerLogger.i(TAG, "onTimedText: %s", text);
            if (getPlayer() != null) {
                notifyOnTimedText(new OneTimedText(text.getBounds(), text.getText()));
            }
        }

        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
            PlayerLogger.i(TAG, "onVideoSizeChanged: [%s,%s] - [%s,%s]", width, height, sar_num, sar_den);
            if (getPlayer() != null) {
                notifyOnVideoSizeChanged(width, height);
            }
        }

        @Override
        public String onControlResolveSegmentUrl(int i) {
            PlayerLogger.i(TAG, "onControlResolveSegmentUrl: %s", i);
            return null;
        }

        @Override
        public String onMediaCodecSelect(IMediaPlayer iMediaPlayer, String s, int i, int i1) {
            PlayerLogger.i(TAG, "onMediaCodecSelect: [%s,%s,%s]", s, i, i1);
            return null;
        }

        @Override
        public boolean onNativeInvoke(int i, Bundle bundle) {
            PlayerLogger.d(TAG, "onNativeInvoke: [%s]%s", i, bundle);
            return false;
        }
    }
}
