package com.coopsrc.oneplayer.kernel.exo2;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import androidx.annotation.Nullable;

import com.coopsrc.oneplayer.core.AbsOnePlayer;
import com.coopsrc.oneplayer.core.PlayerLibraryInfo;
import com.coopsrc.oneplayer.core.misc.IMediaDataSource;
import com.coopsrc.oneplayer.core.misc.ITrackInfo;
import com.coopsrc.oneplayer.core.utils.PlayerLogger;
import com.coopsrc.oneplayer.kernel.exo2.utils.Exo2Utils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioListener;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-07 17:17
 */
public class OneExoPlayer2 extends AbsOnePlayer<SimpleExoPlayer> {
    private static final String TAG = "OneExoPlayer2";

    static {
        PlayerLibraryInfo.registerModule("one.player.exo2");
    }

    private SimpleExoPlayer mInternalPlayer;
    private InternalAdapterListener mInternalAdapterListener;

    private DataSource.Factory dataSourceFactory;

    private MediaSource mMediaSource;

    public OneExoPlayer2(Context context) {
        super(context);

        mInternalAdapterListener = new InternalAdapterListener(this);
        initializePlayer();
        attachInternalListeners();
    }

    @Override
    public SimpleExoPlayer getInternalPlayer() {
        return mInternalPlayer;
    }

    @Override
    protected PlayerListenerWrapper getInternalListener() {
        return mInternalAdapterListener;
    }

    @Override
    protected void initializePlayer() {
        dataSourceFactory = Exo2Utils.buildDataSourceFactory(getContext());
        mInternalPlayer = ExoPlayerFactory.newSimpleInstance(getContext());
    }

    @Override
    protected void attachInternalListeners() {
        mInternalPlayer.addListener(mInternalAdapterListener);
        mInternalPlayer.addAudioListener(mInternalAdapterListener);
        mInternalPlayer.addVideoListener(mInternalAdapterListener);
        mInternalPlayer.addTextOutput(mInternalAdapterListener);
        mInternalPlayer.addMetadataOutput(mInternalAdapterListener);
    }

    @Override
    protected void resetInternalListeners() {

    }

    @Override
    protected void setSurface(@Nullable Surface surface) {
        mInternalPlayer.setVideoSurface(surface);
    }

    @Override
    public void clearVideoSurface() {
//        super.clearVideoSurface();
        mInternalPlayer.clearVideoSurface();
    }

    @Override
    public void clearVideoSurface(Surface surface) {
//        super.clearVideoSurface(surface);
        mInternalPlayer.clearVideoSurface(surface);
    }

    @Override
    public void setVideoSurface(@Nullable Surface surface) {
//        super.setVideoSurface(surface);
        mInternalPlayer.setVideoSurface(surface);
    }

    @Override
    public void setVideoSurfaceHolder(SurfaceHolder surfaceHolder) {
//        super.setVideoSurfaceHolder(surfaceHolder);

        mInternalPlayer.setVideoSurfaceHolder(surfaceHolder);
    }

    @Override
    public void clearVideoSurfaceHolder(SurfaceHolder surfaceHolder) {
//        super.clearVideoSurfaceHolder(surfaceHolder);

        mInternalPlayer.clearVideoSurfaceHolder(surfaceHolder);
    }

    @Override
    public void setVideoSurfaceView(SurfaceView surfaceView) {
//        super.setVideoSurfaceView(surfaceView);

        mInternalPlayer.setVideoSurfaceView(surfaceView);
    }

    @Override
    public void clearVideoSurfaceView(SurfaceView surfaceView) {
//        super.clearVideoSurfaceView(surfaceView);
        mInternalPlayer.clearVideoSurfaceView(surfaceView);
    }

    @Override
    public void setVideoTextureView(TextureView textureView) {
//        super.setVideoTextureView(textureView);
        mInternalPlayer.setVideoTextureView(textureView);
    }

    @Override
    public void clearVideoTextureView(TextureView textureView) {
//        super.clearVideoTextureView(textureView);
        mInternalPlayer.clearVideoTextureView(textureView);
    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        PlayerLogger.i(TAG, "setDataSource: %s", uri);

        mMediaSource = buildMediaSource(uri);
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        PlayerLogger.i(TAG, "setDataSource: %s, %s", uri, headers);

        if (headers != null) {
            mMediaSource = buildMediaSource(uri, headers.get("extension"));
        } else {
            mMediaSource = buildMediaSource(uri);
        }
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        PlayerLogger.i(TAG, "setDataSource: %s", path);

        mMediaSource = buildMediaSource(Uri.parse(path));
    }

    @Override
    public void setDataSource(String path, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        PlayerLogger.i(TAG, "setDataSource: %s, %s", path, headers);

        if (headers != null) {
            mMediaSource = buildMediaSource(Uri.parse(path), headers.get("extension"));
        } else {
            mMediaSource = buildMediaSource(Uri.parse(path));
        }
    }


    @Override
    public void setDataSource(FileDescriptor fileDescriptor) throws IOException, IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void setDataSource(FileDescriptor fileDescriptor, long offset, long length) throws IOException, IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void setDataSource(IMediaDataSource dataSource) throws IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        PlayerLogger.i(TAG, "prepareAsync: %s", mMediaSource);
        if (mInternalPlayer != null) {
            mInternalPlayer.prepare(mMediaSource);
        }
    }

    @Override
    public void start() throws IllegalStateException {
        if (mInternalPlayer != null) {
            mInternalPlayer.setPlayWhenReady(true);
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
            mInternalPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public boolean release() {
        if (mInternalPlayer != null) {
            mInternalPlayer.release();
        }
        return super.release();
    }

    @Override
    public void reset() {
        if (mInternalPlayer != null) {
            mInternalPlayer.stop(true);
        }
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
            mInternalPlayer.setVolume(audioVolume);
        }
    }

    @Override
    public float getVolume() {
        return mInternalPlayer.getVolume();
    }

    @Override
    public long getCurrentPosition() {
        return mInternalPlayer.getCurrentPosition();
    }

    @Override
    public int getBufferedPercentage() {
        return mInternalPlayer.getBufferedPercentage();
    }

    @Override
    public long getBufferedPosition() {
        return mInternalPlayer.getBufferedPosition();
    }

    @Override
    public long getDuration() {
        return mInternalPlayer.getDuration();
    }

    @Override
    public boolean isPlaying() {
        if (mInternalPlayer != null) {
            int state = mInternalPlayer.getPlaybackState();
            switch (state) {
                case ExoPlayer.STATE_BUFFERING:
                case ExoPlayer.STATE_READY:
                    return mInternalPlayer.getPlayWhenReady();
                case ExoPlayer.STATE_IDLE:
                case ExoPlayer.STATE_ENDED:
                default:
                    return false;
            }
        }
        return false;
    }

    @Override
    public void setWakeMode(Context context, int mode) {

    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {

    }

    @Override
    public int getVideoWidth() {
        if (mInternalPlayer != null && mInternalPlayer.getVideoFormat() != null) {
            return mInternalPlayer.getVideoFormat().width;
        }

        return super.getVideoWidth();
    }

    @Override
    public int getVideoHeight() {
        if (mInternalPlayer != null && mInternalPlayer.getVideoFormat() != null) {
            return mInternalPlayer.getVideoFormat().height;
        }

        return super.getVideoHeight();
    }

    @Override
    public int getRotationDegrees() {
        if (mInternalPlayer != null && mInternalPlayer.getVideoFormat() != null) {
            return mInternalPlayer.getVideoFormat().rotationDegrees;
        }

        return super.getRotationDegrees();
    }

    @Override
    public float getPixelRatio() {
        if (mInternalPlayer != null && mInternalPlayer.getVideoFormat() != null) {
            return mInternalPlayer.getVideoFormat().pixelWidthHeightRatio;
        }

        return super.getPixelRatio();
    }

    @Override
    public void setLooping(boolean looping) {

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

    }

    @Override
    public int getAudioSessionId() {
        return mInternalPlayer.getAudioSessionId();
    }

    private MediaSource buildMediaSource(Uri uri) {
        return buildMediaSource(uri, null);
    }

    private MediaSource buildMediaSource(Uri uri, @Nullable String overrideExtension) {
        PlayerLogger.i(TAG, "buildMediaSource: [%s, %s]", uri, overrideExtension);
        @C.ContentType
        int type = Util.inferContentType(uri, overrideExtension);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }

    private class InternalAdapterListener extends PlayerListenerWrapper<OneExoPlayer2> implements
            Player.EventListener, AudioListener, VideoListener, TextOutput, MetadataOutput {

        InternalAdapterListener(OneExoPlayer2 player) {
            super(player);
        }

        @Override
        public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
            PlayerLogger.i(TAG, "onTimelineChanged: [%s,%s,%s]", timeline, manifest, reason);
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            PlayerLogger.i(TAG, "onTracksChanged: [%s,%s]", trackGroups, trackSelections);
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            PlayerLogger.i(TAG, "onLoadingChanged: %s", isLoading);
            notifyOnBufferingUpdate(getBufferedPercentage());
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            PlayerLogger.i(TAG, "onPlayerStateChanged: [%s,%s]", playWhenReady, playbackState);
            switch (playbackState) {
                case Player.STATE_IDLE:
                    notifyOnPlaybackStateChanged(STATE_IDLE);
                    break;
                case Player.STATE_BUFFERING:
                    notifyOnPlaybackStateChanged(STATE_BUFFERING);
                    break;
                case Player.STATE_READY:
                    if (getInternalPlayer().getPlayWhenReady()) {
                        notifyOnPlaybackStateChanged(STATE_PLAYING);
                    } else {
                        notifyOnPlaybackStateChanged(STATE_PAUSED);
                    }
                    break;
                case Player.STATE_ENDED:
                    notifyOnPlaybackStateChanged(STATE_STOPPED);
                    break;
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            PlayerLogger.i(TAG, "onRepeatModeChanged: %s", repeatMode);
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            PlayerLogger.i(TAG, "onShuffleModeEnabledChanged: %s", shuffleModeEnabled);
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            PlayerLogger.i(TAG, "onPlayerError: %s", error);
            notifyOnError(error.type, error.rendererIndex);
            notifyOnPlaybackStateChanged(STATE_ERROR);
        }

        @Override
        public void onPositionDiscontinuity(int reason) {
            PlayerLogger.i(TAG, "onPositionDiscontinuity: %s", reason);
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            PlayerLogger.i(TAG, "onPlaybackParametersChanged: %s", playbackParameters);
        }

        @Override
        public void onSeekProcessed() {
            PlayerLogger.i(TAG, "onSeekProcessed: ");
            notifyOnSeekComplete();
        }

        @Override
        public void onMetadata(Metadata metadata) {
            PlayerLogger.i(TAG, "onMetadata: %s", metadata);
        }

        @Override
        public void onCues(List<Cue> cues) {
            PlayerLogger.i(TAG, "onCues: %s", cues);
        }

        @Override
        public void onAudioSessionId(int audioSessionId) {
            PlayerLogger.i(TAG, "onAudioSessionId: %s", audioSessionId);
            notifyOnAudioSessionId(audioSessionId);
        }

        @Override
        public void onAudioAttributesChanged(AudioAttributes audioAttributes) {
            PlayerLogger.i(TAG, "onAudioAttributesChanged: %s", audioAttributes);
            notifyOnAudioAttributesChanged(audioAttributes.getAudioAttributesV21());
        }

        @Override
        public void onVolumeChanged(float volume) {
            PlayerLogger.i(TAG, "onVolumeChanged: %s", volume);
            notifyOnVolumeChanged(volume);
        }

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            PlayerLogger.i(TAG, "onVideoSizeChanged: [%s,%s][%s,%s]", width, height, unappliedRotationDegrees, pixelWidthHeightRatio);
            notifyOnVideoSizeChanged(width, height, unappliedRotationDegrees, pixelWidthHeightRatio);
        }

        @Override
        public void onSurfaceSizeChanged(int width, int height) {
            PlayerLogger.i(TAG, "onSurfaceSizeChanged: [%s,%s]", width, height);
            notifyOnSurfaceSizeChanged(width, height);
        }

        @Override
        public void onRenderedFirstFrame() {
            PlayerLogger.i(TAG, "onRenderedFirstFrame: ");
            notifyOnRenderedFirstFrame();
        }
    }
}
