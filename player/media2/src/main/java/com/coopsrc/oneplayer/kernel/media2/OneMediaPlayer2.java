package com.coopsrc.oneplayer.kernel.media2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media2.common.FileMediaItem;
import androidx.media2.common.MediaItem;
import androidx.media2.common.SubtitleData;
import androidx.media2.common.UriMediaItem;
import androidx.media2.player.MediaPlayer2;
import androidx.media2.player.MediaPlayer2.DrmEventCallback;
import androidx.media2.player.MediaPlayer2.EventCallback;
import androidx.media2.player.MediaTimestamp;
import androidx.media2.player.TimedMetaData;

import com.coopsrc.oneplayer.core.AbsOnePlayer;
import com.coopsrc.oneplayer.core.PlayerLibraryInfo;
import com.coopsrc.oneplayer.core.misc.IMediaDataSource;
import com.coopsrc.oneplayer.core.misc.ITrackInfo;
import com.coopsrc.oneplayer.core.misc.OneTimedMetadata;
import com.coopsrc.oneplayer.core.utils.PlayerLogger;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-06-21 15:27
 */
@SuppressLint("RestrictedApi")
public final class OneMediaPlayer2 extends AbsOnePlayer<MediaPlayer2> {
    private static final String TAG = "OneMediaPlayer2";

    static {
        PlayerLibraryInfo.registerModule("one.player.media2");
    }

    private final MediaPlayer2 mInternalPlayer;

    private final InternalAdapterListener mInternalAdapterListener;

    public OneMediaPlayer2(Context context) {
        super(context);

        mInternalPlayer = MediaPlayer2.create(context);
        mInternalAdapterListener = new InternalAdapterListener(this);
        attachInternalListeners();
    }

    @Override
    public MediaPlayer2 getInternalPlayer() {
        return mInternalPlayer;
    }

    @Override
    protected PlayerListenerWrapper<OneMediaPlayer2> getInternalListener() {
        return mInternalAdapterListener;
    }

    @Override
    protected void initializePlayer() {

    }

    @Override
    protected void attachInternalListeners() {
        mInternalPlayer.setEventCallback(getIOThreadExecutor(), mInternalAdapterListener.getEventCallback());
        mInternalPlayer.setDrmEventCallback(getIOThreadExecutor(), mInternalAdapterListener.getDrmEventCallback());
    }

    @Override
    protected void resetInternalListeners() {

    }

    @Override
    protected void setSurface(@Nullable Surface surface) {
        if (mInternalPlayer != null) {
            mInternalPlayer.setSurface(surface);
        }
    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        UriMediaItem.Builder mediaItemBuilder = new UriMediaItem.Builder(uri);

        MediaItem mediaItem = mediaItemBuilder.build();
        mInternalPlayer.setMediaItem(mediaItem);
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        UriMediaItem.Builder mediaItemBuilder = new UriMediaItem.Builder(uri, headers, null);

        MediaItem mediaItem = mediaItemBuilder.build();
        mInternalPlayer.setMediaItem(mediaItem);
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        UriMediaItem.Builder mediaItemBuilder = new UriMediaItem.Builder(Uri.parse(path));

        MediaItem mediaItem = mediaItemBuilder.build();
        mInternalPlayer.setMediaItem(mediaItem);
    }

    @Override
    public void setDataSource(String path, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {

        UriMediaItem.Builder mediaItemBuilder = new UriMediaItem.Builder(Uri.parse(path), headers, null);

        MediaItem mediaItem = mediaItemBuilder.build();

        mInternalPlayer.setMediaItem(mediaItem);
    }

    @Override
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {
        FileMediaItem.Builder mediaItemBuilder = new FileMediaItem.Builder(ParcelFileDescriptor.dup(fd));
        MediaItem mediaItem = mediaItemBuilder.build();
        mInternalPlayer.setMediaItem(mediaItem);
    }

    @Override
    public void setDataSource(FileDescriptor fd, long offset, long length) throws IOException, IllegalArgumentException, IllegalStateException {
        FileMediaItem.Builder mediaItemBuilder = new FileMediaItem.Builder(ParcelFileDescriptor.dup(fd));

        mediaItemBuilder.setStartPosition(offset);
        MediaItem mediaItem = mediaItemBuilder.build();
        mInternalPlayer.setMediaItem(mediaItem);
    }

    @Override
    public void setDataSource(IMediaDataSource dataSource) throws IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        if (mInternalPlayer != null) {
            mInternalPlayer.prepare();
        }
    }

    @Override
    public void start() throws IllegalStateException {
        if (mInternalPlayer != null) {
            mInternalPlayer.play();
        }
    }

    @Override
    public void stop() throws IllegalStateException {
        if (mInternalPlayer != null) {
            mInternalPlayer.pause();
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
            mInternalPlayer.close();
        }
        return super.release();
    }

    @Override
    public void reset() {
        if (mInternalPlayer != null) {
            mInternalPlayer.reset();
        }
    }

    @Override
    public void seekTo(long positionMs, int mode) {
        if (mInternalPlayer != null) {
            mInternalPlayer.seekTo(positionMs, mode);
        }
    }

    @Override
    public void seekTo(long positionMs) throws IllegalStateException {
        mInternalPlayer.seekTo(positionMs);
    }

    @Override
    public void setVolume(float audioVolume) {
        super.setVolume(audioVolume);
        mInternalPlayer.setPlayerVolume(audioVolume);
    }

    @Override
    public long getCurrentPosition() {
        if (mInternalPlayer != null && isPlaying()) {
            return mInternalPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public long getDuration() {
        if (mInternalPlayer != null && isPlaying()) {
            return mInternalPlayer.getDuration();
        }

        return 0;
    }

    @Override
    public boolean isPlaying() {
        return mInternalPlayer.getState() == MediaPlayer2.PLAYER_STATE_PLAYING;
    }

    @Override
    public int getPlaybackState() {
        switch (mInternalPlayer.getState()) {
            case MediaPlayer2.PLAYER_STATE_ERROR:
                return STATE_ERROR;
            case MediaPlayer2.PLAYER_STATE_IDLE:
                return STATE_IDLE;
            case MediaPlayer2.PLAYER_STATE_PAUSED:
                return STATE_PAUSED;
            case MediaPlayer2.PLAYER_STATE_PLAYING:
                return STATE_PLAYING;
            case MediaPlayer2.PLAYER_STATE_PREPARED:
            default:
                return super.getPlaybackState();
        }
    }

    @Override
    public void setWakeMode(Context context, int mode) {

    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {

    }

    @Override
    public int getVideoWidth() {
        return mInternalPlayer.getVideoWidth();
    }

    @Override
    public int getVideoHeight() {
        return mInternalPlayer.getVideoHeight();
    }

    @Override
    public void setLooping(boolean looping) {
        mInternalPlayer.loopCurrent(looping);
    }

    @Override
    public boolean isLooping() {
        return false;
    }

    @Override
    public ITrackInfo[] getTrackInfo() {
        return new ITrackInfo[0];
    }

    @Override
    public void setAudioSessionId(int sessionId) {
        mInternalPlayer.setAudioSessionId(sessionId);
    }

    @Override
    public int getAudioSessionId() {
        return mInternalPlayer.getAudioSessionId();
    }

    private class InternalAdapterListener extends PlayerListenerWrapper<OneMediaPlayer2> {

        private final EventCallback mEventCallback;

        private final DrmEventCallback mDrmEventCallback;

        private InternalAdapterListener(OneMediaPlayer2 player) {
            super(player);

            mEventCallback = new OneEventCallback();
            mDrmEventCallback = new OneDrmEventCallback();
        }

        private EventCallback getEventCallback() {
            return mEventCallback;
        }

        private DrmEventCallback getDrmEventCallback() {
            return mDrmEventCallback;
        }

        private class OneEventCallback extends EventCallback {
            @Override
            public void onVideoSizeChanged(MediaPlayer2 mediaPlayer2, MediaItem item, int width, int height) {
                super.onVideoSizeChanged(mediaPlayer2, item, width, height);
                PlayerLogger.i(TAG, "onVideoSizeChanged: [%s, %s]", width, height);
                notifyOnVideoSizeChanged(width, height);
            }

            @Override
            public void onTimedMetaDataAvailable(MediaPlayer2 mediaPlayer2, MediaItem item, TimedMetaData data) {
                super.onTimedMetaDataAvailable(mediaPlayer2, item, data);
                PlayerLogger.i(TAG, "onTimedMetaDataAvailable: ");
                notifyOnTimedMetadata(new OneTimedMetadata(data.getTimestamp(), data.getMetaData()));
            }

            @Override
            public void onError(MediaPlayer2 mediaPlayer2, MediaItem item, int what, int extra) {
                super.onError(mediaPlayer2, item, what, extra);
                PlayerLogger.i(TAG, "onError: [%s, %s]", what, extra);
                notifyOnError(what, extra);
                notifyOnPlaybackStateChanged(STATE_ERROR);
            }

            @Override
            public void onInfo(MediaPlayer2 mediaPlayer2, MediaItem item, int what, int extra) {
                super.onInfo(mediaPlayer2, item, what, extra);
                PlayerLogger.i(TAG, "onInfo: [%s, %s]", what, extra);
                notifyOnInfo(what, extra);
            }

            @Override
            public void onCallCompleted(MediaPlayer2 mediaPlayer2, MediaItem item, int what, int status) {
                super.onCallCompleted(mediaPlayer2, item, what, status);
                PlayerLogger.i(TAG, "onCallCompleted: [%s, %s]", what, status);
            }

            @Override
            public void onMediaTimeDiscontinuity(MediaPlayer2 mediaPlayer2, MediaItem item, MediaTimestamp timestamp) {
                super.onMediaTimeDiscontinuity(mediaPlayer2, item, timestamp);
                PlayerLogger.i(TAG, "onMediaTimeDiscontinuity: ");
            }

            @Override
            public void onCommandLabelReached(MediaPlayer2 mediaPlayer2, @NonNull Object label) {
                super.onCommandLabelReached(mediaPlayer2, label);
                PlayerLogger.i(TAG, "onCommandLabelReached: ");
            }

            @Override
            public void onSubtitleData(@NonNull MediaPlayer2 mediaPlayer2, @NonNull MediaItem item, int trackIdx, @NonNull SubtitleData data) {
                super.onSubtitleData(mediaPlayer2, item, trackIdx, data);
                PlayerLogger.i(TAG, "onSubtitleData: ");
            }
        }

        private class OneDrmEventCallback extends DrmEventCallback {
            @Override
            public void onDrmInfo(MediaPlayer2 mediaPlayer2, MediaItem item, MediaPlayer2.DrmInfo drmInfo) {
                super.onDrmInfo(mediaPlayer2, item, drmInfo);
                PlayerLogger.i(TAG, "onDrmInfo: ");
            }

            @Override
            public void onDrmPrepared(MediaPlayer2 mediaPlayer2, MediaItem item, int status) {
                super.onDrmPrepared(mediaPlayer2, item, status);
                PlayerLogger.i(TAG, "onDrmPrepared: [%s]", status);
            }
        }
    }

}
