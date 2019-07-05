package com.coopsrc.oneplayer.kernel.media2;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.Nullable;
import androidx.media2.player.MediaPlayer2;
import androidx.media2.player.MediaPlayer2.DrmEventCallback;
import androidx.media2.player.MediaPlayer2.EventCallback;

import com.coopsrc.oneplayer.core.AbsOnePlayer;
import com.coopsrc.oneplayer.core.OneExecutors;
import com.coopsrc.oneplayer.core.PlayerLibraryInfo;
import com.coopsrc.oneplayer.core.misc.IMediaDataSource;
import com.coopsrc.oneplayer.core.misc.ITrackInfo;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-06-21 15:27
 */
public final class OneMediaPlayer2 extends AbsOnePlayer<MediaPlayer2> {

    static {
        PlayerLibraryInfo.registerModule("one.player.media2");
    }

    private MediaPlayer2 mInternalPlayer;

    private InternalAdapterListener mInternalAdapterListener;

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
    protected PlayerListenerHolder getInternalListener() {
        return null;
    }

    @Override
    protected void initializePlayer() {

    }

    @Override
    protected void attachInternalListeners() {
        mInternalPlayer.setEventCallback(OneExecutors.getInstance().diskIO(), mInternalAdapterListener.getEventCallback());
        mInternalPlayer.setDrmEventCallback(OneExecutors.getInstance().diskIO(), mInternalAdapterListener.getDrmEventCallback());
    }

    @Override
    protected void setSurface(@Nullable Surface surface) {
        if (mInternalPlayer != null) {
            mInternalPlayer.setSurface(surface);
        }
    }

    @Override
    protected void setDisplay(SurfaceHolder holder) {
//        if (mInternalPlayer != null) {
//            mInternalPlayer.setSurface(holder.getSurface());
//        }

    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {

    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {

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
        return false;
    }

    @Override
    public void reset() {
        if (mInternalPlayer != null) {
            mInternalPlayer.reset();
        }
    }

    @Override
    public void seekTo(long msec, int mode) {
        if (mInternalPlayer != null) {
            mInternalPlayer.seekTo(msec, mode);
        }
    }

    @Override
    public void seekTo(long msec) throws IllegalStateException {
        mInternalPlayer.seekTo(msec);
    }

    @Override
    public void setVolume(float volume) {
        mInternalPlayer.setPlayerVolume(volume);
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {

        mInternalPlayer.setPlayerVolume((leftVolume + rightVolume) / 2);
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
    public int getAudioSessionId() {
        return mInternalPlayer.getAudioSessionId();
    }

    @Override
    public void setPlayWhenReady(boolean playWhenReady) {

    }

    @Override
    public boolean getPlayWhenReady() {
        return false;
    }

    private class InternalAdapterListener extends PlayerListenerHolder<OneMediaPlayer2> {

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

        }

        private class OneDrmEventCallback extends DrmEventCallback {

        }
    }

}
