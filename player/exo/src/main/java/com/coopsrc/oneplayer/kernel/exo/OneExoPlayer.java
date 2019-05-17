package com.coopsrc.oneplayer.kernel.exo;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.coopsrc.oneplayer.core.AbsOnePlayer;
import com.coopsrc.oneplayer.core.IOnePlayer;
import com.coopsrc.oneplayer.core.misc.IMediaDataSource;
import com.coopsrc.oneplayer.core.misc.ITrackInfo;
import com.coopsrc.oneplayer.kernel.exo.drm.SmoothStreamingTestMediaDrmCallback;
import com.coopsrc.oneplayer.kernel.exo.drm.WidevineTestMediaDrmCallback;
import com.coopsrc.oneplayer.kernel.exo.player.DashRendererBuilder;
import com.coopsrc.oneplayer.kernel.exo.player.DemoPlayer;
import com.coopsrc.oneplayer.kernel.exo.player.ExtractorRendererBuilder;
import com.coopsrc.oneplayer.kernel.exo.player.HlsRendererBuilder;
import com.coopsrc.oneplayer.kernel.exo.player.SmoothStreamingRendererBuilder;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.util.Util;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-07 17:17
 */
public class OneExoPlayer extends AbsOnePlayer<DemoPlayer> {

    private DemoPlayer mInternalPlayer;
    private ExoPlayerListenerHolder mInternalAdapterListener;
    private EventLogger mEventLogger;

    private Surface mSurface;

    private Uri mUri;
    private Map<String, String> mHeaders;

    private int mVideoWidth;
    private int mVideoHeight;

    private DemoPlayer.RendererBuilder mRendererBuilder;

    private final Object mLock = new Object();

    public OneExoPlayer(Context context) {
        super(context);

        mInternalAdapterListener = new ExoPlayerListenerHolder(this);

        mEventLogger = new EventLogger();
        mEventLogger.startSession();

        attachInternalListeners();
    }

    @Override
    public DemoPlayer getInternalPlayer() {
        return mInternalPlayer;
    }

    @Override
    protected void attachInternalListeners() {

    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {

        setDataSource(context, uri, null);
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        // TODO: 19-5-16
        mUri = uri;
        mHeaders = headers;
        mRendererBuilder = generateRendererBuilder();
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        setDataSource(getContext(), Uri.parse(path));
    }

    @Override
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {
        // TODO: 19-5-16
    }

    @Override
    public void setDataSource(FileDescriptor fd, long offset, long length) throws IOException, IllegalArgumentException, IllegalStateException {
        // TODO: 19-5-16
    }

    @Override
    public void setDataSource(IMediaDataSource dataSource) throws IllegalArgumentException, IllegalStateException {
        // TODO: 19-5-16
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        if (holder == null) {
            setSurface(null);
        } else {
            setSurface(holder.getSurface());
        }

    }

    @Override
    public void setSurface(Surface surface) {
        mSurface = surface;
        if (mInternalPlayer != null) {
            mInternalPlayer.setSurface(surface);
        }
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        mInternalPlayer = new DemoPlayer(mRendererBuilder);
        mInternalPlayer.addListener(mInternalAdapterListener);
        mInternalPlayer.addListener(mEventLogger);
        mInternalPlayer.setInfoListener(mEventLogger);
        mInternalPlayer.setInternalErrorListener(mEventLogger);

        if (mSurface != null) {
            mInternalPlayer.setSurface(mSurface);
        }

        mInternalPlayer.prepare();
        mInternalPlayer.setPlayWhenReady(false);
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
            mInternalPlayer.release();
        }
    }

    @Override
    public void pause() throws IllegalStateException {
        if (mInternalPlayer != null) {
            mInternalPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public boolean release() {

        if (mInternalPlayer != null) {
            reset();
            mInternalAdapterListener = null;
            mEventLogger.endSession();
            mEventLogger = null;
        }

        return false;
    }

    @Override
    public void reset() {
        if (mInternalPlayer != null) {
            mInternalPlayer.release();
            mInternalPlayer.removeListener(mInternalAdapterListener);
            mInternalPlayer.removeListener(mEventLogger);
            mInternalPlayer.setInfoListener(null);
            mInternalPlayer.setInternalErrorListener(null);
            mInternalPlayer = null;
        }

        mSurface = null;
        mUri = null;
        mHeaders = null;
        mVideoHeight = 0;
        mVideoWidth = 0;
    }

    @Override
    public void seekTo(long msec, int mode) {
        seekTo(msec);
    }

    @Override
    public void seekTo(long msec) throws IllegalStateException {
        if (mInternalPlayer != null) {
            mInternalPlayer.seekTo(msec);
        }
    }

    @Override
    public void setVolume(float volume) {

    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {

    }

    @Override
    public long getCurrentPosition() {
        if (mInternalPlayer != null) {
            return mInternalPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public int getBufferedPercentage() {
        if (mInternalPlayer != null) {
            return mInternalPlayer.getBufferedPercentage();
        }
        return super.getBufferedPercentage();
    }

    @Override
    public long getDuration() {
        if (mInternalPlayer != null) {
            return mInternalPlayer.getDuration();
        }
        return 0;
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
                case ExoPlayer.STATE_PREPARING:
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
    public int getAudioSessionId() {
        return 0;
    }

    private DemoPlayer.RendererBuilder generateRendererBuilder() {
        Uri contentUri = mUri;
        String userAgent = Util.getUserAgent(getContext(), "OneExoPlayer");
        int contentType = inferContentType(contentUri);

        switch (contentType) {
            case Util.TYPE_SS:
                return new SmoothStreamingRendererBuilder(getContext(), userAgent, contentUri.toString(),
                        new SmoothStreamingTestMediaDrmCallback());
            case Util.TYPE_DASH:
                return new DashRendererBuilder(getContext(), userAgent, contentUri.toString(),
                        new WidevineTestMediaDrmCallback(getDrmContentId(), getDrmProvider()));
            case Util.TYPE_HLS:
                return new HlsRendererBuilder(getContext(), userAgent, contentUri.toString());
            case Util.TYPE_OTHER:
            default:
                return new ExtractorRendererBuilder(getContext(), userAgent, contentUri);
        }
    }

    private String getDrmContentId() {
        if (mHeaders != null && mHeaders.containsKey("contentId")) {
            return mHeaders.get("contentId");
        }
        return "";
    }

    private String getDrmProvider() {
        if (mHeaders != null && mHeaders.containsKey("provider")) {
            return mHeaders.get("provider");
        }
        return "";
    }

    private static int inferContentType(Uri uri) {
        String lastPathSegment = uri.getLastPathSegment();
        return Util.inferContentType(lastPathSegment);
    }

    private class ExoPlayerListenerHolder extends PlayerListenerHolder<OneExoPlayer> implements
            DemoPlayer.Listener {
        private boolean mIsPreparing = false;
        private boolean mDidPrepare = false;
        private boolean mIsBuffering = false;

        private ExoPlayerListenerHolder(OneExoPlayer player) {
            super(player);
        }

        @Override
        public void onStateChanged(boolean playWhenReady, int playbackState) {
            if (mIsBuffering) {
                switch (playbackState) {
                    case ExoPlayer.STATE_ENDED:
                    case ExoPlayer.STATE_READY:
                        notifyOnInfo(IOnePlayer.MEDIA_INFO_BUFFERING_END, mInternalPlayer.getBufferedPercentage());
                        notifyOnBufferingUpdate(mInternalPlayer.getBufferedPercentage());
                        mIsBuffering = false;
                        break;
                }
            }

            if (mIsPreparing) {
                switch (playbackState) {
                    case ExoPlayer.STATE_READY:
                        notifyOnPrepared();
                        mIsPreparing = false;
                        mDidPrepare = false;
                        break;
                }
            }

            switch (playbackState) {
                case ExoPlayer.STATE_IDLE:
                    notifyOnCompletion();
                    break;
                case ExoPlayer.STATE_PREPARING:
                    mIsPreparing = true;
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    notifyOnInfo(IOnePlayer.MEDIA_INFO_BUFFERING_START, mInternalPlayer.getBufferedPercentage());
                    notifyOnBufferingUpdate(mInternalPlayer.getBufferedPercentage());
                    mIsBuffering = true;
                    break;
                case ExoPlayer.STATE_READY:
                    break;
                case ExoPlayer.STATE_ENDED:
                    notifyOnCompletion();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onError(Exception e) {
            notifyOnError(IOnePlayer.MEDIA_ERROR_UNKNOWN, IOnePlayer.MEDIA_ERROR_UNKNOWN);
        }

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

            mVideoWidth = width;
            mVideoHeight = height;
            notifyOnVideoSizeChanged(width, height);
        }
    }
}
