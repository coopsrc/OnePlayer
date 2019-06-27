package com.coopsrc.oneplayer.core;

import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import androidx.annotation.Nullable;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-06-28 15:52
 */
public interface IPlayerSurface {
    void addVideoSurfaceListener(VideoSurfaceListener videoSurfaceListener);

    void removeVideoSurfaceListener(VideoSurfaceListener videoSurfaceListener);

    void clearVideoSurface();

    void setVideoSurface(@Nullable Surface surface);

    void clearVideoSurface(Surface surface);

    void setVideoSurfaceHolder(SurfaceHolder surfaceHolder);

    void clearVideoSurfaceHolder(SurfaceHolder surfaceHolder);

    void setVideoSurfaceView(SurfaceView surfaceView);

    void clearVideoSurfaceView(SurfaceView surfaceView);

    void setVideoTextureView(TextureView textureView);

    void clearVideoTextureView(TextureView textureView);


    interface VideoSurfaceListener {
        default void onVideoSizeChanged(int width, int height, int rotationDegrees, float ratio) {
        }


        default void onSurfaceSizeChanged(int width, int height) {
        }

        default void onRenderedFirstFrame() {
        }
    }
}
