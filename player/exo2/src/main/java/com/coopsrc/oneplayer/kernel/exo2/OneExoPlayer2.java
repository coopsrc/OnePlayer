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
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.util.Util;

import java.io.FileDescriptor;
import java.io.IOException;
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

    private int mVideoWidth;
    private int mVideoHeight;

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
    protected PlayerListenerHolder getInternalListener() {
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

        PlayerLogger.i(TAG, "setDataSource: %s", uri);

        mMediaSource = buildMediaSource(uri);
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {

        PlayerLogger.i(TAG, "setDataSource: %s", path);

        mMediaSource = buildMediaSource(Uri.parse(path));
    }

    @Override
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void setDataSource(FileDescriptor fd, long offset, long length) throws IOException, IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void setDataSource(IMediaDataSource dataSource) throws IllegalArgumentException, IllegalStateException {
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        PlayerLogger.i(TAG, "prepareAsync: %s", mMediaSource);
        mInternalPlayer.prepare(mMediaSource);
    }

    @Override
    public void start() throws IllegalStateException {
        mInternalPlayer.setPlayWhenReady(true);
    }

    @Override
    public void stop() throws IllegalStateException {
        mInternalPlayer.stop();
    }

    @Override
    public void pause() throws IllegalStateException {
        mInternalPlayer.setPlayWhenReady(true);
    }

    @Override
    public boolean release() {
        return false;
    }

    @Override
    public void reset() {
        mInternalPlayer.stop(true);
    }

    @Override
    public void seekTo(long msec, int mode) {
        mInternalPlayer.seekTo(msec);
    }

    @Override
    public void seekTo(long msec) throws IllegalStateException {
        mInternalPlayer.seekTo(msec);
    }

    @Override
    public void setVolume(float audioVolume) {
        mInternalPlayer.setVolume(audioVolume);
        super.setVolume(audioVolume);
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
        return mVideoWidth;
    }

    @Override
    public int getVideoHeight() {
        return mVideoHeight;
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

    @Override
    public void setPlayWhenReady(boolean playWhenReady) {
        mInternalPlayer.setPlayWhenReady(playWhenReady);
    }

    @Override
    public boolean getPlayWhenReady() {
        return mInternalPlayer.getPlayWhenReady();
    }


    private MediaSource buildMediaSource(Uri uri) {
        return buildMediaSource(uri, null);
    }

    private MediaSource buildMediaSource(Uri uri, @Nullable String overrideExtension) {
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

    private class InternalAdapterListener extends PlayerListenerHolder<OneExoPlayer2> implements Player.EventListener {

        public InternalAdapterListener(OneExoPlayer2 player) {
            super(player);
        }

        @Override
        public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }
    }
}
