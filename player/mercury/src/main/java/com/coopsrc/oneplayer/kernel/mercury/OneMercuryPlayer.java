package com.coopsrc.oneplayer.kernel.mercury;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;

import androidx.annotation.Nullable;

import com.coopsrc.oneplayer.core.AbsOnePlayer;
import com.coopsrc.oneplayer.core.PlayerLibraryInfo;
import com.coopsrc.oneplayer.core.misc.IMediaDataSource;
import com.coopsrc.oneplayer.core.misc.ITrackInfo;
import com.coopsrc.oneplayer.core.utils.PlayerLogger;
import com.coopsrc.xandroid.utils.AppTaskExecutors;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-07 16:20
 */
public class OneMercuryPlayer extends AbsOnePlayer<MercuryPlayer> {
    private static final String TAG = "OneMercuryPlayer";

    static {
        PlayerLibraryInfo.registerModule("one.player.mercury");
    }

    private final MercuryPlayer mMercuryPlayer;
    private final MercuryPlayerListenerWrapper mInternalAdapterListener;


    public OneMercuryPlayer(Context context) {
        super(context);

        mMercuryPlayer = new MercuryPlayer();
        mInternalAdapterListener = new MercuryPlayerListenerWrapper(this);
        attachInternalListeners();
    }

    @Override
    public MercuryPlayer getInternalPlayer() {
        return mMercuryPlayer;
    }

    @Override
    protected PlayerListenerWrapper<OneMercuryPlayer> getInternalListener() {
        return mInternalAdapterListener;
    }

    @Override
    protected void initializePlayer() {

    }

    @Override
    protected void attachInternalListeners() {
        mMercuryPlayer.setOnErrorListener(mInternalAdapterListener);
        mMercuryPlayer.setOnInfoListener(mInternalAdapterListener);
        mMercuryPlayer.setOnPrepareListener(mInternalAdapterListener);
    }

    @Override
    protected void resetInternalListeners() {

    }

    @Override
    protected void setSurface(@Nullable Surface surface) {
        AppTaskExecutors.mainThread().execute(new Runnable() {
            @Override
            public void run() {
                mMercuryPlayer.setSurface(surface);
            }
        });
    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mMercuryPlayer.setDataSource(uri.toString());
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mMercuryPlayer.setDataSource(uri.toString());
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mMercuryPlayer.setDataSource(path);
    }

    @Override
    public void setDataSource(String path, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mMercuryPlayer.setDataSource(path);
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
        if (null != mMercuryPlayer) {

            mMercuryPlayer.prepare();
        }
    }

    @Override
    public void start() throws IllegalStateException {
        if (null != mMercuryPlayer) {

            mMercuryPlayer.start();
        }
    }

    @Override
    public void stop() throws IllegalStateException {
        if (null != mMercuryPlayer) {
            mMercuryPlayer.stop();
        }

    }

    @Override
    public void pause() throws IllegalStateException {
        if (null != mMercuryPlayer) {

            mMercuryPlayer.pause();
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public void seekTo(long positionMs, int mode) {

    }

    @Override
    public void seekTo(long positionMs) throws IllegalStateException {

    }

    @Override
    public long getCurrentPosition() {
        return 0;
    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public void setWakeMode(Context context, int mode) {

    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {

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

    private class MercuryPlayerListenerWrapper extends PlayerListenerWrapper<OneMercuryPlayer> implements
            IMercuryPlayer.OnInfoListener,
            IMercuryPlayer.OnErrorListener,
            IMercuryPlayer.OnPrepareListener {

        public MercuryPlayerListenerWrapper(OneMercuryPlayer player) {
            super(player);
        }

        @Override
        public void onPrepared(IMercuryPlayer player) {
            PlayerLogger.i(TAG, "onPrepared: ");
            if (getPlayer() != null) {
                notifyOnPlaybackStateChanged(STATE_PREPARED);
                notifyOnPrepared();
            }
        }

        @Override
        public void onInfo(IMercuryPlayer player, int what, int extra) {
            PlayerLogger.i(TAG, "onInfo: [%s,%s]", what, extra);
            if (getPlayer() != null) {
                notifyOnInfo(what, extra);
            }
        }

        @Override
        public void onError(IMercuryPlayer player, int what, int extra) {
            PlayerLogger.i(TAG, "onError: [%s,%s]", what, extra);
            if (getPlayer() != null) {
                notifyOnPlaybackStateChanged(STATE_ERROR);
                notifyOnError(what, extra);
            }
        }
    }
}
