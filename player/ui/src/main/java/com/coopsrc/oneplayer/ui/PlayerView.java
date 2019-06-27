package com.coopsrc.oneplayer.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.coopsrc.oneplayer.core.OnePlayer;
import com.coopsrc.oneplayer.core.misc.ITimedText;
import com.coopsrc.oneplayer.ui.widget.AspectRatioFrameLayout;

import java.io.IOException;
import java.util.Map;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-10 16:39
 */
public class PlayerView extends ConstraintLayout implements TextureView.SurfaceTextureListener {

    private static final String TAG = "PlayerView";

    @Nullable
    private final AspectRatioFrameLayout contentFrame;
    @Nullable
    private final TextureView textureView;
    @Nullable
    private final View bufferingView;
    @Nullable
    private final PlayerControlView controller;

    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;

    private OnePlayer mPlayer;

    // settable by the client
    private Uri mUri;
    private Map<String, String> mHeaders;

    private final PlayerEventListener mPlayerEventListener;

    public PlayerView(Context context) {
        this(context, null);
    }

    public PlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_player_view, this);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setBackgroundColor(Color.BLACK);

        // Content frame.
        contentFrame = findViewById(R.id.player_content_frame);
        if (contentFrame != null) {
            setResizeModeRaw(contentFrame, AspectRatioFrameLayout.RESIZE_MODE_FIT);
        }

        // Surface view
        textureView = new TextureView(context);
        View surfacePlaceholder = findViewById(R.id.player_surface_placeholder);
        textureView.setLayoutParams(surfacePlaceholder.getLayoutParams());
        ViewGroup surfacePlaceholderParent = (ViewGroup) surfacePlaceholder.getParent();
        int surfaceIndex = surfacePlaceholderParent.indexOfChild(surfacePlaceholder);
        surfacePlaceholderParent.removeView(surfacePlaceholder);
        surfacePlaceholderParent.addView(textureView, surfaceIndex);
        textureView.setSurfaceTextureListener(this);


        // Buffering view.
        bufferingView = findViewById(R.id.progress_buffering);
        if (bufferingView != null) {
            bufferingView.setVisibility(View.GONE);
        }

        controller = new PlayerControlView(context, null, 0);
        View controllerPlaceholder = findViewById(R.id.player_controller_placeholder);
        controller.setLayoutParams(controllerPlaceholder.getLayoutParams());
        ViewGroup controllerPlaceholderParent = ((ViewGroup) controllerPlaceholder.getParent());
        int controllerIndex = controllerPlaceholderParent.indexOfChild(controllerPlaceholder);
        controllerPlaceholderParent.removeView(controllerPlaceholder);
        controllerPlaceholderParent.addView(controller, controllerIndex);

        mPlayerEventListener = new PlayerEventListener();

    }

    public OnePlayer getPlayer() {
        return mPlayer;
    }

    public void setPlayer(OnePlayer player) {

        if (mPlayer == player) {
            return;
        }

        if (mPlayer != null) {
            mPlayer.removeListener(mPlayerEventListener);
        }

        mPlayer = player;

        if (player != null) {
            mPlayer.addListener(mPlayerEventListener);
        }

        controller.setPlayer(mPlayer);
    }


    /**
     * Sets video path.
     *
     * @param path the path of the video.
     */
    public void setVideoPath(String path) {
        Log.i(TAG, "setVideoPath: " + path);
        setVideoURI(Uri.parse(path));
    }

    /**
     * Sets video URI.
     *
     * @param uri the URI of the video.
     */
    public void setVideoURI(Uri uri) {
        Log.i(TAG, "setVideoURI: " + uri);
        setVideoURI(uri, null);
    }

    public void setVideoURI(Uri uri, Map<String, String> headers) {
        Log.i(TAG, "setVideoURI: [" + uri + "," + headers + "]");
        mUri = uri;
        mHeaders = headers;
        openVideo();
    }

    private void openVideo() {
        if (mUri == null) {
            // not ready for playback just yet, will try again later
            return;
        }

        if (mSurfaceTexture != null) {
            if (mSurface != null) {
                mSurface.release();
            }
            mSurface = new Surface(mSurfaceTexture);
            mPlayer.setSurface(mSurface);
        }

        try {
            mPlayer.setDataSource(getContext(), mUri, mHeaders);
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setResizeModeRaw(AspectRatioFrameLayout aspectRatioFrame, int resizeMode) {
        aspectRatioFrame.setResizeMode(resizeMode);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "onSurfaceTextureAvailable: [" + surface + "," + width + "," + height + "]");

        if (mSurfaceTexture == null) {
            mSurfaceTexture = surface;
            openVideo();
        } else {
            textureView.setSurfaceTexture(mSurfaceTexture);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "onSurfaceTextureSizeChanged: [" + surface + "," + width + "," + height + "]");

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.i(TAG, "onSurfaceTextureDestroyed: " + surface);
        return mSurfaceTexture == null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        Log.i(TAG, "onSurfaceTextureUpdated: " + surface);
    }

    private final class PlayerEventListener implements OnePlayer.EventListener {

        @Override
        public void onBufferingUpdate(OnePlayer player, int percent) {

        }

        @Override
        public void onCompletion(OnePlayer player) {

        }

        @Override
        public boolean onError(OnePlayer player, int what, int extra) {
            return false;
        }

        @Override
        public boolean onInfo(OnePlayer player, int what, int extra) {
            switch (what) {
                case OnePlayer.MEDIA_INFO_BUFFERING_START:
                    if (bufferingView!=null){
                        bufferingView.setVisibility(VISIBLE);
                    }
                    break;
                case OnePlayer.MEDIA_INFO_BUFFERING_END:
                    if (bufferingView!=null){
                        bufferingView.setVisibility(GONE);
                    }
                    break;
            }
            return false;
        }

        @Override
        public void onPrepared(OnePlayer player) {
            mPlayer.start();
        }

        @Override
        public void onSeekComplete(OnePlayer player) {

        }

        @Override
        public void onTimedText(OnePlayer player, ITimedText text) {

        }

        @Override
        public void onVideoSizeChanged(OnePlayer player, int width, int height) {

        }
    }
}
