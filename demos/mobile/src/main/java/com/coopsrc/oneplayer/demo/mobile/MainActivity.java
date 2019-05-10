package com.coopsrc.oneplayer.demo.mobile;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.coopsrc.oneplayer.PlayerFactory;
import com.coopsrc.oneplayer.core.AbsOnePlayer;
import com.coopsrc.oneplayer.core.IOnePlayer;
import com.coopsrc.oneplayer.core.utils.LogUtils;
import com.coopsrc.oneplayer.ffmedia.OneFFMedia;
import com.coopsrc.oneplayer.ui.PlayerView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String URL = "https://vfx.mtime.cn/Video/2018/02/05/mp4/180205170620160029.mp4";
    private static final String videoLocal = Environment.getExternalStorageDirectory() + "/Movies/Peru.mp4";

    private PlayerView mPlayerView;

    private AbsOnePlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: " + OneFFMedia.ffmpegVersion());

        mPlayerView = findViewById(R.id.surface_view);

        mPlayer = PlayerFactory.createPlayer(this, PlayerFactory.TypeMedia);
//        mPlayer = PlayerFactory.createPlayer(this, PlayerFactory.TypeIjk);
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

        mPlayerView.setPlayer(mPlayer);

    }

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPlayerView.setVideoPath(URL);
//                mPlayerView.setVideoURI(Uri.parse(videoLocal));
            }
        }, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mPlayer.release();
    }

}
