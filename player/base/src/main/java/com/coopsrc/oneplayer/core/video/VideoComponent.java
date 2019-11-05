package com.coopsrc.oneplayer.core.video;

import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import androidx.annotation.Nullable;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-08-08 15:07
 */
public interface VideoComponent {

    void clearVideoSurface();

    void setVideoSurface(@Nullable Surface surface);

    void clearVideoSurface(Surface surface);

    void setVideoSurfaceHolder(SurfaceHolder surfaceHolder);

    void clearVideoSurfaceHolder(SurfaceHolder surfaceHolder);

    void setVideoSurfaceView(SurfaceView surfaceView);

    void clearVideoSurfaceView(SurfaceView surfaceView);

    void setVideoTextureView(TextureView textureView);

    void clearVideoTextureView(TextureView textureView);
}
