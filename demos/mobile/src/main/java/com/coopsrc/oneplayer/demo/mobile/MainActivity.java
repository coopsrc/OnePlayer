package com.coopsrc.oneplayer.demo.mobile;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.coopsrc.oneplayer.PlayerFactory;
import com.coopsrc.oneplayer.core.AbsOnePlayer;
import com.coopsrc.oneplayer.core.IOnePlayer;
import com.coopsrc.oneplayer.core.utils.LogUtils;
import com.coopsrc.oneplayer.kernel.ffmedia.OneMercuryPlayer;
import com.coopsrc.oneplayer.ui.PlayerView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String URL = "https://vfx.mtime.cn/Video/2018/02/05/mp4/180205170620160029.mp4";
    private static final String videoLocal = Environment.getExternalStorageDirectory() + "/Movies/Peru.mp4";

    private static final String RTSP = "rtsp://mpv.cdn3.bigCDN.com:554/bigCDN/definst/mp4:bigbuckbunnyiphone_400.mp4";

    private static final String DASH_HD = "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears_hd.mpd";
    private static final String DASH_UHD = "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears_uhd.mpd";
    private static final String DASH_SECURE = "https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears_sd.mpd";
    private Map<String, String> dashHeaders = new HashMap<>();

    private static final String SS = "https://playready.directtaps.net/smoothstreaming/SSWSS720H264/SuperSpeedway_720.ism";

    private static final String HLS = "https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_16x9/bipbop_16x9_variant.m3u8";
    private static final String HLS_PL_MP4 = "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_fmp4/master.m3u8";

    private static final String HLS_FAST_8 = "https://kakazy-yun.com/20170831/dY1xSKbG/index.m3u8";

    private static final String HLS_LIVE = "http://weblive.hebtv.com/live/hbgg_bq/index.m3u8";
    private static final String testURl2 = "https://video-dev.github.io/streams/x36xhzz/x36xhzz.m3u8";

    private static final String FLV = "https://vod.leasewebcdn.com/bbb.flv?ri=1024&rs=150&start=0";

    private PlayerView mPlayerView;

    private AbsOnePlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: " + OneMercuryPlayer.ffmpegVersion());

        mPlayerView = findViewById(R.id.player_view);

//        mPlayer = PlayerFactory.createPlayer(this, PlayerFactory.TypeMedia);
//        mPlayer = PlayerFactory.createPlayer(this, PlayerFactory.TypeIjk);
        mPlayer = PlayerFactory.createPlayer(this, PlayerFactory.TypeExo);

        mPlayer.setScreenOnWhilePlaying(true);
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
//                mPlayerView.setVideoPath(URL);

//                mPlayerView.setVideoURI(Uri.parse(videoLocal));

                // rtsp
//                mPlayerView.setVideoPath(RTSP);

                // dash h264 mp4
//                mPlayerView.setVideoPath(DASH_HD);

                // ss
//                mPlayerView.setVideoPath(SS);

                // hls
//                mPlayerView.setVideoPath(HLS);
//                mPlayerView.setVideoPath(HLS_PL_MP4);

                // hls live
//                mPlayerView.setVideoPath(HLS_LIVE);
//                mPlayerView.setVideoPath(HLS_FAST_8);

                // 360
//                mPlayerView.setVideoPath(FLV);

                // dash h264 mp4 drm
//                dashHeaders.put("contentId", "");
//                dashHeaders.put("provider", "widevine_test");
//                mPlayerView.setVideoURI(Uri.parse(DASH_SECURE), dashHeaders);
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
