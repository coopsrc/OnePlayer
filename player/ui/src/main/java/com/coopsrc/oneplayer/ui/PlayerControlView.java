package com.coopsrc.oneplayer.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import com.coopsrc.oneplayer.core.IOnePlayer;
import com.coopsrc.oneplayer.core.misc.ITimedText;
import com.coopsrc.oneplayer.core.utils.LogUtils;
import com.coopsrc.oneplayer.core.utils.PlayerUtils;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-10 16:40
 */
public class PlayerControlView extends ConstraintLayout implements MediaController.MediaPlayerControl {
    private static final String TAG = "PlayerControlView";

    private ImageButton mButtonTinyWindow;
    private ImageButton mButtonAddPlaylist;
    private ImageButton mButtonSkipPrevious;
    private ImageButton mButtonFastRewind;
    private ImageButton mButtonPlayPause;
    private ImageButton mButtonFastForward;
    private ImageButton mButtonSkipNext;
    private ImageButton mButtonMenuMore;
    private TextView mTextPosition;
    private SeekBar mProgressBar;
    private TextView mTextDuration;
    private ImageButton mButtonFullscreen;

    private boolean mCanPause = true;
    private boolean mCanSeekBackward = true;
    private boolean mCanSeekForward = true;

    private boolean mIsFullscreen = false;

    private IOnePlayer mPlayer;
    private PlayerControlListener mControlListener;
    private PlayerEventListener mPlayerEventListener;

    private Group mGroupMenuBar;
    private Group mGroupActionBar;
    private Group mGroupProgressBar;

    private boolean isControlsShown = true;
    private boolean isControlsAutoDismiss = true;

    private static final int MSG_SHOW_CONTROLS = 100;
    private static final int MSG_DISMISS_CONTROLS = 101;
    private static final int MSG_UPDATE_BUFFERING_POSITION = 0;
    private static final int MSG_UPDATE_CURRENT_POSITION = 2;
    private PlayerControlHandler mControlHandler = new PlayerControlHandler();

    public PlayerControlView(Context context) {
        this(context, null);
    }

    public PlayerControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_player_control_view, this);

        mButtonTinyWindow = findViewById(R.id.button_tiny_window);
        mButtonAddPlaylist = findViewById(R.id.button_playlist_add);
        mButtonMenuMore = findViewById(R.id.button_menu_more);

        mButtonSkipPrevious = findViewById(R.id.button_skip_previous);
        mButtonFastRewind = findViewById(R.id.button_fast_rewind);
        mButtonPlayPause = findViewById(R.id.button_play_pause);
        mButtonFastForward = findViewById(R.id.button_fast_forward);
        mButtonSkipNext = findViewById(R.id.button_skip_next);

        mTextPosition = findViewById(R.id.text_position);
        mProgressBar = findViewById(R.id.progress);
        mTextDuration = findViewById(R.id.text_duration);
        mButtonFullscreen = findViewById(R.id.button_fullscreen);

        mControlListener = new PlayerControlListener();
        mPlayerEventListener = new PlayerEventListener();

        mButtonTinyWindow.setOnClickListener(mControlListener);
        mButtonAddPlaylist.setOnClickListener(mControlListener);
        mButtonMenuMore.setOnClickListener(mControlListener);

        mButtonSkipPrevious.setOnClickListener(mControlListener);
        mButtonFastRewind.setOnClickListener(mControlListener);
        mButtonPlayPause.setOnClickListener(mControlListener);
        mButtonFastForward.setOnClickListener(mControlListener);
        mButtonSkipNext.setOnClickListener(mControlListener);

        mButtonFullscreen.setOnClickListener(mControlListener);

        mGroupMenuBar = findViewById(R.id.group_menu);
        mGroupActionBar = findViewById(R.id.group_action);
        mGroupProgressBar = findViewById(R.id.group_progress);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.w(TAG, "onTouchEvent: ");

        if (isControlsShown) {
            mControlHandler.sendEmptyMessage(MSG_DISMISS_CONTROLS);
        } else {
            mControlHandler.sendEmptyMessage(MSG_SHOW_CONTROLS);
        }

        return super.onTouchEvent(event);
    }

    public IOnePlayer getPlayer() {
        return mPlayer;
    }

    public void setPlayer(IOnePlayer player) {

        if (mPlayer == player) {
            return;
        }

        if (mPlayer != null) {
            mPlayer.removeListener(mPlayerEventListener);
        }

        mPlayer = player;

        if (player != null) {
            player.addListener(mPlayerEventListener);
        }

        updateAll();
    }

    private void updateAll() {

    }

    public void togglePlayPause() {
        if (isPlaying()) {
            pause();
            mButtonPlayPause.setImageResource(R.drawable.ic_play_arrow_white_24dp);
            mButtonPlayPause.setContentDescription(getResources().getString(R.string.button_description_play_arrow));
        } else {
            start();
            mButtonPlayPause.setImageResource(R.drawable.ic_pause_white_24dp);
            mButtonPlayPause.setContentDescription(getResources().getString(R.string.button_description_pause));
        }
    }

    public void switchFullscreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null) {
            return;
        }
        if (mIsFullscreen) {
            PlayerUtils.showActionBar(getContext());
            mIsFullscreen = false;
        } else {
            PlayerUtils.hideActionBar(getContext());

            mIsFullscreen = true;
        }
    }

    @Override
    public void start() {
        if (mPlayer != null) {
            mPlayer.start();
            mControlHandler.removeMessages(MSG_UPDATE_CURRENT_POSITION);
            mControlHandler.sendEmptyMessage(MSG_UPDATE_CURRENT_POSITION);
        }
    }

    @Override
    public void pause() {
        if (mPlayer != null) {
            mPlayer.pause();
            mControlHandler.removeMessages(MSG_UPDATE_CURRENT_POSITION);
        }
    }

    @Override
    public int getDuration() {
        if (mPlayer != null) {
            return (int) mPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mPlayer != null) {
            return (int) mPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getBufferedPosition() {
        LogUtils.d(TAG, "getBufferedPosition: [%s,%s]", getDuration(), getBufferPercentage());
        if (mPlayer != null) {
            return getDuration() * getBufferPercentage() / 100;
        }

        return 0;
    }

    @Override
    public void seekTo(int pos) {
        if (mPlayer != null) {
            mPlayer.seekTo(pos);
        }

    }

    @Override
    public boolean isPlaying() {
        if (mPlayer != null) {
            return mPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public int getBufferPercentage() {
        if (mPlayer != null) {
            return mPlayer.getBufferedPercentage();
        }
        return 0;
    }

    @Override
    public boolean canPause() {
        return mCanPause;
    }

    @Override
    public boolean canSeekBackward() {
        return mCanSeekBackward;
    }

    @Override
    public boolean canSeekForward() {
        return mCanSeekForward;
    }

    @Override
    public int getAudioSessionId() {
        if (mPlayer != null) {
            mPlayer.getAudioSessionId();
        }
        return 0;
    }

    private void showControls() {
        Log.i(TAG, "showControls: ");

        mGroupMenuBar.setVisibility(VISIBLE);
        mGroupActionBar.setVisibility(VISIBLE);
        mGroupProgressBar.setVisibility(VISIBLE);

        isControlsShown = true;
    }

    private void dismissControls() {
        Log.i(TAG, "dismissControls: ");

        mGroupMenuBar.setVisibility(GONE);
        mGroupActionBar.setVisibility(GONE);
        mGroupProgressBar.setVisibility(GONE);

        isControlsShown = false;
    }

    private final class PlayerControlListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            mControlHandler.removeMessages(MSG_DISMISS_CONTROLS);
            mControlHandler.sendEmptyMessageDelayed(MSG_DISMISS_CONTROLS, 3000);
            if (mPlayer != null) {
                if (mButtonPlayPause == view) {
                    togglePlayPause();
                } else if (mButtonFullscreen == view) {
                    switchFullscreen();
                }
            }
        }
    }

    private final class PlayerControlHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_SHOW_CONTROLS:
                    showControls();
                    if (isControlsAutoDismiss) {
                        mControlHandler.removeMessages(MSG_DISMISS_CONTROLS);
                        mControlHandler.sendEmptyMessageDelayed(MSG_DISMISS_CONTROLS, 3000);
                    }
                    break;
                case MSG_DISMISS_CONTROLS:
                    dismissControls();
                    break;
                case MSG_UPDATE_BUFFERING_POSITION:
                    LogUtils.i(TAG, "BUFFERING: [%s,%s,%s]", getCurrentPosition(), getBufferedPosition(), getDuration());
                    mProgressBar.setSecondaryProgress(getBufferedPosition());
                    break;
                case MSG_UPDATE_CURRENT_POSITION:
                    mProgressBar.setProgress(getCurrentPosition());

                    mTextPosition.setText(PlayerUtils.formatPlayingTime(getCurrentPosition()));

                    mControlHandler.sendEmptyMessageDelayed(MSG_UPDATE_CURRENT_POSITION, 500);
                    break;
            }
        }
    }

    private final class PlayerEventListener implements IOnePlayer.EventListener {

        @Override
        public void onBufferingUpdate(IOnePlayer player, int percent) {
            LogUtils.i(TAG, "onBufferingUpdate: %s", percent);
            mControlHandler.sendEmptyMessage(MSG_UPDATE_BUFFERING_POSITION);
        }

        @Override
        public void onCompletion(IOnePlayer player) {
            LogUtils.i(TAG, "onCompletion: ");
        }

        @Override
        public boolean onError(IOnePlayer player, int what, int extra) {
            LogUtils.i(TAG, "onError: [%s,%s]", what, extra);
            return false;
        }

        @Override
        public boolean onInfo(IOnePlayer player, int what, int extra) {
            LogUtils.i(TAG, "onInfo: [%s,%s]", what, extra);
            return false;
        }

        @Override
        public void onPrepared(IOnePlayer player) {
            LogUtils.i(TAG, "onPrepared: ");
            mProgressBar.setMax(getDuration());
            mProgressBar.setProgress(getCurrentPosition());

            mTextPosition.setText(PlayerUtils.formatPlayingTime(getCurrentPosition()));
            mTextDuration.setText(PlayerUtils.formatPlayingTime(getDuration()));

            mControlHandler.removeMessages(MSG_UPDATE_CURRENT_POSITION);
            mControlHandler.sendEmptyMessage(MSG_UPDATE_CURRENT_POSITION);
        }

        @Override
        public void onSeekComplete(IOnePlayer player) {
            LogUtils.i(TAG, "onSeekComplete: ");

        }

        @Override
        public void onTimedText(IOnePlayer player, ITimedText text) {
            LogUtils.i(TAG, "onTimedText: %s", text);

        }

        @Override
        public void onVideoSizeChanged(IOnePlayer player, int width, int height) {
            LogUtils.i(TAG, "onVideoSizeChanged: [%s,%s]", width, height);

        }
    }
}
