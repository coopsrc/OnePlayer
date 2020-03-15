package com.coopsrc.oneplayer.core;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import androidx.annotation.Nullable;

import com.coopsrc.oneplayer.core.misc.IMediaDataSource;
import com.coopsrc.oneplayer.core.misc.ITrackInfo;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-10 12:07
 */
public class MediaPlayerProxy implements OnePlayer {

    private final OnePlayer mBackendPlayer;

    public MediaPlayerProxy(OnePlayer backendPlayer) {
        mBackendPlayer = backendPlayer;
    }

    public OnePlayer getBackendPlayer() {
        return mBackendPlayer;
    }

    @Override
    public void addEventListener(EventListener listener) {
        mBackendPlayer.addEventListener(listener);
    }

    @Override
    public void removeEventListener(EventListener listener) {
        mBackendPlayer.removeEventListener(listener);
    }

    @Override
    public void clearVideoSurface() {
        mBackendPlayer.clearVideoSurface();
    }

    @Override
    public void setVideoSurface(@Nullable Surface surface) {
        mBackendPlayer.setVideoSurface(surface);
    }

    @Override
    public void clearVideoSurface(Surface surface) {
        mBackendPlayer.clearVideoSurface(surface);
    }

    @Override
    public void setVideoSurfaceHolder(SurfaceHolder surfaceHolder) {
        mBackendPlayer.setVideoSurfaceHolder(surfaceHolder);
    }

    @Override
    public void clearVideoSurfaceHolder(SurfaceHolder surfaceHolder) {
        mBackendPlayer.clearVideoSurfaceHolder(surfaceHolder);
    }

    @Override
    public void setVideoSurfaceView(SurfaceView surfaceView) {
        mBackendPlayer.setVideoSurfaceView(surfaceView);
    }

    @Override
    public void clearVideoSurfaceView(SurfaceView surfaceView) {
        mBackendPlayer.setVideoSurfaceView(surfaceView);
    }

    @Override
    public void setVideoTextureView(TextureView textureView) {
        mBackendPlayer.setVideoTextureView(textureView);
    }

    @Override
    public void clearVideoTextureView(TextureView textureView) {
        mBackendPlayer.clearVideoTextureView(textureView);
    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mBackendPlayer.setDataSource(context, uri);
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mBackendPlayer.setDataSource(context, uri, headers);
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mBackendPlayer.setDataSource(path);
    }

    @Override
    public void setDataSource(String path, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mBackendPlayer.setDataSource(path, headers);
    }

    @Override
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {
        mBackendPlayer.setDataSource(fd);
    }

    @Override
    public void setDataSource(FileDescriptor fd, long offset, long length) throws IOException, IllegalArgumentException, IllegalStateException {
        mBackendPlayer.setDataSource(fd, offset, length);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void setDataSource(IMediaDataSource dataSource) throws IllegalArgumentException, IllegalStateException {
        mBackendPlayer.setDataSource(dataSource);
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        mBackendPlayer.prepareAsync();
    }

    @Override
    public void start() throws IllegalStateException {
        mBackendPlayer.start();
    }

    @Override
    public void stop() throws IllegalStateException {
        mBackendPlayer.stop();
    }

    @Override
    public void pause() throws IllegalStateException {
        mBackendPlayer.pause();
    }

    @Override
    public boolean release() {
        return mBackendPlayer.release();
    }

    @Override
    public void reset() {
        mBackendPlayer.reset();
    }

    @Override
    public void seekTo(long positionMs, int mode) {
        mBackendPlayer.seekTo(positionMs, mode);
    }

    @Override
    public void seekTo(long positionMs) throws IllegalStateException {
        mBackendPlayer.seekTo(positionMs);
    }

    @Override
    public void setVolume(float audioVolume) {
        mBackendPlayer.setVolume(audioVolume);
    }

    @Override
    public float getVolume() {
        return mBackendPlayer.getVolume();
    }

    @Override
    public long getCurrentPosition() {
        return mBackendPlayer.getCurrentPosition();
    }

    @Override
    public int getCurrentPercentage() {
        return mBackendPlayer.getCurrentPercentage();
    }

    @Override
    public int getBufferedPercentage() {
        return mBackendPlayer.getBufferedPercentage();
    }

    @Override
    public long getDuration() {
        return mBackendPlayer.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return mBackendPlayer.isPlaying();
    }

    @Override
    public void setWakeMode(Context context, int mode) {
        mBackendPlayer.setWakeMode(context, mode);
    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {
        mBackendPlayer.setScreenOnWhilePlaying(screenOn);
    }

    @Override
    public int getVideoWidth() {
        return mBackendPlayer.getVideoWidth();
    }

    @Override
    public int getVideoHeight() {
        return mBackendPlayer.getVideoHeight();
    }

    @Override
    public int getRotationDegrees() {
        return mBackendPlayer.getRotationDegrees();
    }

    @Override
    public float getPixelRatio() {
        return mBackendPlayer.getPixelRatio();
    }

    @Override
    public void setLooping(boolean looping) {
        mBackendPlayer.setLooping(looping);
    }

    @Override
    public boolean isLooping() {
        return mBackendPlayer.isLooping();
    }

    @Override
    public ITrackInfo[] getTrackInfo() {
        return mBackendPlayer.getTrackInfo();
    }


    @Override
    public void setAudioSessionId(int sessionId) {
        mBackendPlayer.setAudioSessionId(sessionId);
    }

    @Override
    public int getAudioSessionId() {
        return mBackendPlayer.getAudioSessionId();
    }

    @Override
    public long getBufferedPosition() {
        return mBackendPlayer.getBufferedPosition();
    }

    @Override
    public int getPlaybackState() {
        return mBackendPlayer.getPlaybackState();
    }

}
