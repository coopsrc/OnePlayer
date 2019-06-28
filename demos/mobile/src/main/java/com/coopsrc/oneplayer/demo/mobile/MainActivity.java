package com.coopsrc.oneplayer.demo.mobile;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.coopsrc.oneplayer.PlayerFactory;
import com.coopsrc.oneplayer.core.OnePlayer;
import com.coopsrc.oneplayer.core.PlaybackPreparer;
import com.coopsrc.oneplayer.core.utils.PlayerLogger;
import com.coopsrc.oneplayer.core.utils.PlayerUtils;
import com.coopsrc.oneplayer.kernel.ffmedia.OneMercuryPlayer;
import com.coopsrc.oneplayer.ui.PlayerView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String URL = "http://vfx.mtime.cn/Video/2018/02/05/mp4/180205170620160029.mp4";
    private static final String videoLocal = Environment.getExternalStorageDirectory() + "/Movies/Peru.mp4";

    private static final String RTSP = "rtsp://mpv.cdn3.bigCDN.com:554/bigCDN/definst/mp4:bigbuckbunnyiphone_400.mp4";

    private static final String DASH_SD_HD = "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd";
    private static final String DASH_SD = "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears_sd.mpd";
    private static final String DASH_HD = "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears_hd.mpd";
    private static final String DASH_UHD = "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears_uhd.mpd";
    private static final String DASH_SECURE_SD_DH = "https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd";
    private static final String DASH_SECURE_SD = "https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears_sd.mpd";
    private static final String DASH_SECURE_HD = "https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears_hd.mpd";
    private static final String DASH_SECURE_UHD = "https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears_uhd.mpd";
    private Map<String, String> dashHeaders = new HashMap<>();

    private static final String SS = "https://playready.directtaps.net/smoothstreaming/SSWSS720H264/SuperSpeedway_720.ism";

    private static final String HLS = "https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_16x9/bipbop_16x9_variant.m3u8";
    private static final String HLS_PL_MP4 = "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_fmp4/master.m3u8";

    private static final String HLS_FAST_8 = "https://kakazy-yun.com/20170831/dY1xSKbG/index.m3u8";

    private static final String HLS_LIVE = "http://weblive.hebtv.com/live/hbgg_bq/index.m3u8";
    private static final String testURl2 = "https://video-dev.github.io/streams/x36xhzz/x36xhzz.m3u8";

    private static final String FLV = "https://vod.leasewebcdn.com/bbb.flv?ri=1024&rs=150&start=0";
    private static final String _360_congo = "https://storage.googleapis.com/exoplayer-test-media-1/360/congo.mp4";
    private static final String _360_sphericalv2 = "https://storage.googleapis.com/exoplayer-test-media-1/360/sphericalv2.mp4";
    private static final String _360_iceland0 = "https://storage.googleapis.com/exoplayer-test-media-1/360/iceland0.ts";

    private PlayerView mPlayerView;

    private OnePlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: " + OneMercuryPlayer.ffmpegVersion());

        mPlayerView = findViewById(R.id.player_view);

//        mPlayer = PlayerFactory.createPlayer(this, PlayerFactory.TypeMedia);
        mPlayer = PlayerFactory.createPlayer(this, PlayerFactory.TypeIjk);
//        mPlayer = PlayerFactory.createPlayer(this, PlayerFactory.TypeExo);
//        mPlayer = PlayerFactory.createPlayer(this, PlayerFactory.TypeExo2);

        mPlayer.setScreenOnWhilePlaying(true);
        mPlayer.setOnPreparedListener(new OnePlayer.OnPreparedListener() {
            @Override
            public void onPrepared(OnePlayer player) {
                Log.i(TAG, "onPrepared: ");
                mPlayer.start();
                mPlayer.setLooping(true);
            }
        });
        mPlayer.setOnInfoListener(new OnePlayer.OnInfoListener() {
            @Override
            public boolean onInfo(OnePlayer player, int what, int extra) {
                PlayerLogger.i(TAG, "onInfo: what=%s, extra=%s", what, extra);
                return false;
            }
        });
        mPlayer.setOnBufferingUpdateListener(new OnePlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(OnePlayer player, int percent) {
                PlayerLogger.i(TAG, "onBufferingUpdate: percent=%s", percent);
            }
        });
        mPlayer.setOnErrorListener(new OnePlayer.OnErrorListener() {
            @Override
            public boolean onError(OnePlayer player, int what, int extra) {
                PlayerLogger.i(TAG, "onError: what=%s, extra=%s", what, extra);
                return false;
            }
        });

        mPlayerView.setPlayer(mPlayer);
        mPlayerView.setPlaybackPreparer(new PlaybackPreparer() {
            @Override
            public void preparePlayback() {
                mPlayer.prepareAsync();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
//            mPlayer.setDataSource(URL);

//            mPlayer.setDataSource(this, Uri.parse(videoLocal));

            // rtsp
//                mPlayer.setDataSource(RTSP);

            // dash h264 mp4
//            mPlayer.setDataSource(DASH_HD);

            // ss
//            mPlayer.setDataSource(SS);

            // hls
//                mPlayer.setDataSource(HLS);
//                mPlayer.setDataSource(HLS_PL_MP4);

            // hls live
//            mPlayer.setDataSource(HLS_LIVE);
            mPlayer.setDataSource(HLS_FAST_8);

            // 360
//            mPlayer.setDataSource(_360_congo);

            // dash h264 mp4 drm
//                dashHeaders.put("contentId", "");
//                dashHeaders.put("provider", "widevine_test");
//                mPlayer.setVideoURI(Uri.parse(DASH_SECURE_SD), dashHeaders);


        } catch (Exception e) {
            e.printStackTrace();
        }

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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "onConfigurationChanged: " + newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
            PlayerUtils.hideActionBar(this);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏
            PlayerUtils.showActionBar(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            switchToPortrait();
        } else {
            super.onBackPressed();
        }
    }

    private void switchToLandscape() {
        Log.i(TAG, "switchToLandscape: ");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    private void switchToPortrait() {
        Log.i(TAG, "switchToPortrait: ");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }
}
