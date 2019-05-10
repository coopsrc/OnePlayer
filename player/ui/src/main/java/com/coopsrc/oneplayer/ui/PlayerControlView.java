package com.coopsrc.oneplayer.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import com.coopsrc.oneplayer.core.IOnePlayer;
import com.coopsrc.oneplayer.core.misc.ITimedText;
import com.coopsrc.oneplayer.core.utils.PlayerUtils;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-10 16:40
 */
public class PlayerControlView extends ConstraintLayout implements MediaController.MediaPlayerControl {

    private ImageButton mButtonPlayPause;
    private ImageButton mButtonFullscreen;
    private TextView mTextPosition;
    private TextView mTextDuration;
    private SeekBar mProgressBar;

    private boolean mCanPause = true;
    private boolean mCanSeekBackward = true;
    private boolean mCanSeekForward = true;

    private int mCurrentBufferPercentage;

    private IOnePlayer mPlayer;
    private PlayerControlListener mControlListener;
    private PlayerEventListener mPlayerEventListener;

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

        mButtonPlayPause = findViewById(R.id.button_play_pause);
        mButtonFullscreen = findViewById(R.id.button_fullscreen);
        mTextPosition = findViewById(R.id.text_position);
        mTextDuration = findViewById(R.id.text_duration);
        mProgressBar = findViewById(R.id.progress);

        mControlListener = new PlayerControlListener();
        mPlayerEventListener = new PlayerEventListener();

        mButtonPlayPause.setOnClickListener(mControlListener);
        mButtonFullscreen.setOnClickListener(mControlListener);

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
        } else {
            start();
        }
    }

    public void switchFullscreen() {

    }

    @Override
    public void start() {
        if (mPlayer != null) {
            mPlayer.start();
        }
    }

    @Override
    public void pause() {
        if (mPlayer != null) {
            mPlayer.pause();
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
        return mCurrentBufferPercentage;
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

    private final class PlayerControlListener implements OnClickListener {

        @Override
        public void onClick(View view) {
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
                case MSG_UPDATE_BUFFERING_POSITION:
                    mProgressBar.setSecondaryProgress((int) mPlayer.getBufferedPosition());
                    break;
                case MSG_UPDATE_CURRENT_POSITION:
                    mProgressBar.setProgress((int) mPlayer.getCurrentPosition());

                    mTextPosition.setText(PlayerUtils.formatPlayingTime(mPlayer.getCurrentPosition()));

                    mControlHandler.sendEmptyMessageDelayed(MSG_UPDATE_CURRENT_POSITION, 500);
                    break;
            }
        }
    }

    private final class PlayerEventListener implements IOnePlayer.EventListener {

        @Override
        public void onBufferingUpdate(IOnePlayer player, int percent) {
            mCurrentBufferPercentage = percent;
            mControlHandler.sendEmptyMessage(MSG_UPDATE_BUFFERING_POSITION);
        }

        @Override
        public void onCompletion(IOnePlayer player) {

        }

        @Override
        public boolean onError(IOnePlayer player, int what, int extra) {
            return false;
        }

        @Override
        public boolean onInfo(IOnePlayer player, int what, int extra) {
            return false;
        }

        @Override
        public void onPrepared(IOnePlayer player) {
            mProgressBar.setMax((int) mPlayer.getDuration());
            mProgressBar.setProgress((int) mPlayer.getCurrentPosition());

            mTextPosition.setText(PlayerUtils.formatPlayingTime(mPlayer.getCurrentPosition()));
            mTextDuration.setText(PlayerUtils.formatPlayingTime(mPlayer.getDuration()));

            mControlHandler.removeMessages(MSG_UPDATE_CURRENT_POSITION);
            mControlHandler.sendEmptyMessage(MSG_UPDATE_CURRENT_POSITION);
        }

        @Override
        public void onSeekComplete(IOnePlayer player) {

        }

        @Override
        public void onTimedText(IOnePlayer player, ITimedText text) {

        }

        @Override
        public void onVideoSizeChanged(IOnePlayer player, int width, int height) {

        }
    }
}
