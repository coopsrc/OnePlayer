package com.coopsrc.oneplayer.demo.mobile;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.coopsrc.oneplayer.PlayerFactory;
import com.coopsrc.oneplayer.core.AbsOnePlayer;
import com.coopsrc.oneplayer.core.IOnePlayer;
import com.coopsrc.oneplayer.core.utils.LogUtils;
import com.coopsrc.oneplayer.ffmedia.OneFFMedia;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {
    private static final String TAG = "MainActivity";

    private static final String URL = "https://vfx.mtime.cn/Video/2018/02/05/mp4/180205170620160029.mp4";
    private static final String videoLocal = Environment.getExternalStorageDirectory() + "/Movies/Peru.mp4";

    private SurfaceView mSurfaceView;

    private Button mButtonStart;
    private Button mButtonPlayPause;
    private Button mButtonStop;

    private AbsOnePlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: " + OneFFMedia.ffmpegVersion());

        mSurfaceView = findViewById(R.id.surface_view);
        mButtonStart = findViewById(R.id.button_start);
        mButtonPlayPause = findViewById(R.id.button_play_pause);
        mButtonStop = findViewById(R.id.button_stop);

        mButtonStart.setOnClickListener(this);
        mButtonPlayPause.setOnClickListener(this);
        mButtonStop.setOnClickListener(this);

        mSurfaceView.getHolder().addCallback(this);
//        mPlayer = PlayerFactory.createPlayer(this, PlayerFactory.TypeMedia);
        mPlayer = PlayerFactory.createPlayer(this, PlayerFactory.TypeIjk);
        mPlayer.setOnPreparedListener(new IOnePlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IOnePlayer player) {
                Log.i(TAG, "onPrepared: ");
                mPlayer.start();
                mPlayer.setLooping(true);
            }
        });
        mPlayer.setOnInfoListener(new IOnePlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IOnePlayer player, int what, int extra) {
                LogUtils.i(TAG, "onInfo: what=%s, extra=%s", what, extra);
                return false;
            }
        });
        mPlayer.setOnBufferingUpdateListener(new IOnePlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(IOnePlayer player, int percent) {
                LogUtils.i(TAG, "onBufferingUpdate: percent=%s", percent);
            }
        });
        mPlayer.setOnErrorListener(new IOnePlayer.OnErrorListener() {
            @Override
            public boolean onError(IOnePlayer player, int what, int extra) {
                LogUtils.i(TAG, "onError: what=%s, extra=%s", what, extra);
                return false;
            }
        });

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mPlayer.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mPlayer.release();
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.button_start:
                    mPlayer.setDataSource(URL);
//                mPlayer.setDataSource(this, Uri.parse(videoLocal));
                    mPlayer.prepareAsync();
                    break;
                case R.id.button_play_pause:
                    if (mPlayer.isPlaying()) {
                        mPlayer.pause();
                    } else {
                        mPlayer.start();
                    }
                    break;
                case R.id.button_stop:
                    mPlayer.stop();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
