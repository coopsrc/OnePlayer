package com.coopsrc.oneplayer.kernel.exo;

import android.content.Context;
import android.media.MediaCodec;
import android.net.Uri;
import android.view.Surface;

import com.coopsrc.oneplayer.core.AbsOnePlayer;
import com.coopsrc.oneplayer.core.OnePlayer;
import com.coopsrc.oneplayer.core.PlayerLibraryInfo;
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
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.TimeRange;
import com.google.android.exoplayer.audio.AudioTrack;
import com.google.android.exoplayer.chunk.Format;
import com.google.android.exoplayer.metadata.id3.Id3Frame;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.util.Util;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-07 17:17
 */
public class OneExoPlayer extends AbsOnePlayer<DemoPlayer> {
    private static final String TAG = "OneExoPlayer";

    static {
        PlayerLibraryInfo.registerModule("one.player.exo");
    }

    private DemoPlayer mInternalPlayer;
    private ExoPlayerListenerWrapper mInternalAdapterListener;
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

        mInternalAdapterListener = new ExoPlayerListenerWrapper(this);

        mEventLogger = new EventLogger();
        mEventLogger.startSession();

        attachInternalListeners();
    }

    @Override
    public DemoPlayer getInternalPlayer() {
        return mInternalPlayer;
    }

    @Override
    protected PlayerListenerWrapper getInternalListener() {
        return mInternalAdapterListener;
    }

    @Override
    protected void initializePlayer() {
        mInternalPlayer = new DemoPlayer(mRendererBuilder);
        mInternalPlayer.addListener(mInternalAdapterListener);
        mInternalPlayer.addListener(mEventLogger);
        mInternalPlayer.setInfoListener(mEventLogger);
        mInternalPlayer.setInternalErrorListener(mEventLogger);
    }

    @Override
    protected void attachInternalListeners() {

    }

    @Override
    protected void resetInternalListeners() {

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
    public void setDataSource(String path, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        setDataSource(getContext(), Uri.parse(path), headers);
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
    protected void setSurface(Surface surface) {
        mSurface = surface;
        if (mInternalPlayer != null) {
            mInternalPlayer.setSurface(surface);
        }
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        initializePlayer();

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
    public void seekTo(long positionMs, int mode) {
        seekTo(positionMs);
    }

    @Override
    public void seekTo(long positionMs) throws IllegalStateException {
        if (mInternalPlayer != null) {
            mInternalPlayer.seekTo(positionMs);
        }
    }

    @Override
    public void setVolume(float audioVolume) {
    }

    @Override
    public float getVolume() {
        return 0;
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
    public void setAudioSessionId(int sessionId) {

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

    private class ExoPlayerListenerWrapper extends PlayerListenerWrapper<OneExoPlayer> implements
            DemoPlayer.Listener,
            DemoPlayer.InfoListener,
            DemoPlayer.InternalErrorListener,
            DemoPlayer.CaptionListener,
            DemoPlayer.Id3MetadataListener {

        private boolean mIsPreparing = false;
        private boolean mDidPrepare = false;
        private boolean mIsBuffering = false;

        private ExoPlayerListenerWrapper(OneExoPlayer player) {
            super(player);
        }

        @Override
        public void onStateChanged(boolean playWhenReady, int playbackState) {
            if (mIsBuffering) {
                switch (playbackState) {
                    case ExoPlayer.STATE_ENDED:
                    case ExoPlayer.STATE_READY:
                        notifyOnInfo(OnePlayer.MEDIA_INFO_BUFFERING_END, mInternalPlayer.getBufferedPercentage());
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
                    notifyOnInfo(OnePlayer.MEDIA_INFO_BUFFERING_START, mInternalPlayer.getBufferedPercentage());
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
            notifyOnError(OnePlayer.MEDIA_ERROR_UNKNOWN, OnePlayer.MEDIA_ERROR_UNKNOWN);
        }

        @Override
        public void onVideoSizeChanged(int width, int height, int rotationDegrees, float ratio) {

            mVideoWidth = width;
            mVideoHeight = height;
            notifyOnVideoSizeChanged(width, height);
        }

        @Override
        public void onRendererInitializationError(Exception e) {

        }

        @Override
        public void onAudioTrackInitializationError(AudioTrack.InitializationException e) {

        }

        @Override
        public void onAudioTrackWriteError(AudioTrack.WriteException e) {

        }

        @Override
        public void onAudioTrackUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {

        }

        @Override
        public void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException e) {

        }

        @Override
        public void onCryptoError(MediaCodec.CryptoException e) {

        }

        @Override
        public void onLoadError(int sourceId, IOException e) {

        }

        @Override
        public void onDrmSessionManagerError(Exception e) {

        }

        @Override
        public void onVideoFormatEnabled(Format format, int trigger, long mediaTimeMs) {

        }

        @Override
        public void onAudioFormatEnabled(Format format, int trigger, long mediaTimeMs) {

        }

        @Override
        public void onDroppedFrames(int count, long elapsed) {

        }

        @Override
        public void onBandwidthSample(int elapsedMs, long bytes, long bitrateEstimate) {

        }

        @Override
        public void onLoadStarted(int sourceId, long length, int type, int trigger, Format format, long mediaStartTimeMs, long mediaEndTimeMs) {

        }

        @Override
        public void onLoadCompleted(int sourceId, long bytesLoaded, int type, int trigger, Format format, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs) {

        }

        @Override
        public void onDecoderInitialized(String decoderName, long elapsedRealtimeMs, long initializationDurationMs) {

        }

        @Override
        public void onAvailableRangeChanged(int sourceId, TimeRange availableRange) {

        }

        @Override
        public void onCues(List<Cue> cues) {

        }

        @Override
        public void onId3Metadata(List<Id3Frame> id3Frames) {

        }
    }
}
