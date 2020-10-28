package com.coopsrc.oneplayer.demo.mobile;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.coopsrc.oneplayer.PlayerFactory;
import com.coopsrc.oneplayer.core.OnePlayer;
import com.coopsrc.oneplayer.core.PlaybackPreparer;
import com.coopsrc.oneplayer.core.misc.ITimedMetadata;
import com.coopsrc.oneplayer.core.misc.ITimedText;
import com.coopsrc.oneplayer.core.utils.PlayerLogger;
import com.coopsrc.oneplayer.kernel.mercury.OneMercuryPlayer;
import com.coopsrc.oneplayer.ui.VideoView;

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
    private Map<String, String> headersMap = new HashMap<>();

    private static final String SS = "https://playready.directtaps.net/smoothstreaming/SSWSS720H264/SuperSpeedway_720.ism";

    private static final String HLS = "https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_16x9/bipbop_16x9_variant.m3u8";
    private static final String HLS_PL_MP4 = "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_fmp4/master.m3u8";

    private static final String HLS_FAST_8 = "https://kakazy-yun.com/20170831/dY1xSKbG/index.m3u8";

    private static final String HLS_LIVE_hbws_bq = "http://weblive.hebtv.com/live/hbws_bq/index.m3u8";
    private static final String HLS_LIVE_hbjj_bq = "http://weblive.hebtv.com/live/hbjj_bq/index.m3u8";
    private static final String HLS_LIVE_hbds_bq = "http://weblive.hebtv.com/live/hbds_bq/index.m3u8";
    private static final String HLS_LIVE_hbys_bq = "http://weblive.hebtv.com/live/hbys_bq/index.m3u8";
    private static final String HLS_LIVE_hbse_bq = "http://weblive.hebtv.com/live/hbse_bq/index.m3u8";
    private static final String HLS_LIVE_hbgg_bq = "http://weblive.hebtv.com/live/hbgg_bq/index.m3u8";
    private static final String HLS_LIVE_nmpd_bq = "http://weblive.hebtv.com/live/nmpd_bq/index.m3u8";
    private static final String testURl2 = "https://video-dev.github.io/streams/x36xhzz/x36xhzz.m3u8";

    private static final String FLV = "https://vod.leasewebcdn.com/bbb.flv?ri=1024&rs=150&start=0";
    private static final String _360_congo = "https://storage.googleapis.com/exoplayer-test-media-1/360/congo.mp4";
    private static final String _360_sphericalv2 = "https://storage.googleapis.com/exoplayer-test-media-1/360/sphericalv2.mp4";
    private static final String _360_iceland0 = "https://storage.googleapis.com/exoplayer-test-media-1/360/iceland0.ts";

    private static final String MPD = "https://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/youtube?as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,source,id,as&ip=0.0.0.0&ipbits=0&expire=19000000000&signature=51AF5F39AB0CEC3E5497CD9C900EBFEAECCCB5C7.8506521BFC350652163895D4C26DEE124209AA9E&key=ik0";

    private VideoView mVideoView;

    private OnePlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: " + OneMercuryPlayer.ffmpegVersion());

        mVideoView = findViewById(R.id.player_view);

//        mPlayer = PlayerFactory.createPlayer(this, PlayerFactory.TypeMedia);
//        mPlayer = PlayerFactory.createPlayer(this, PlayerFactory.TypeMedia2);
//        mPlayer = PlayerFactory.createPlayer(this, PlayerFactory.TypeIjk);
//        mPlayer = PlayerFactory.createPlayer(this, PlayerFactory.TypeExo);
        mPlayer = PlayerFactory.createPlayer(this, PlayerFactory.TypeExo2);

        mPlayer.setScreenOnWhilePlaying(true);
        mPlayer.addEventListener(new OnePlayer.EventListener() {

            @Override
            public void onBufferingUpdate(OnePlayer player, int percent) {
                PlayerLogger.i(TAG, "onBufferingUpdate: percent=%s", percent);
            }

            @Override
            public void onCompletion(OnePlayer player) {
                PlayerLogger.i(TAG, "onCompletion: ");
            }

            @Override
            public boolean onError(OnePlayer player, int what, int extra) {
                PlayerLogger.i(TAG, "onError: what=%s, extra=%s", what, extra);
                return false;
            }

            @Override
            public boolean onInfo(OnePlayer player, int what, int extra) {
                PlayerLogger.i(TAG, "onInfo: what=%s, extra=%s", what, extra);
                return false;
            }

            @Override
            public void onPrepared(OnePlayer player) {
                Log.i(TAG, "onPrepared: ");
                mPlayer.start();
                mPlayer.setLooping(true);
            }

            @Override
            public void onSeekComplete(OnePlayer player) {
                PlayerLogger.i(TAG, "onSeekComplete: ");
            }

            @Override
            public void onTimedText(OnePlayer player, ITimedText text) {
                PlayerLogger.i(TAG, "onTimedText: ");
            }

            @Override
            public void onTimedMetaDataAvailable(OnePlayer player, ITimedMetadata timedMetadata) {
                PlayerLogger.i(TAG, "onTimedMetaDataAvailable: ");
            }

            @Override
            public void onVideoSizeChanged(OnePlayer player, int width, int height, int rotationDegrees, float pixelRatio) {
                PlayerLogger.i(TAG, "onVideoSizeChanged: [%s, %s]", width, height);
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                PlayerLogger.i(TAG, "onPlaybackStateChanged: %s", playbackState);
            }
        });

        mVideoView.setPlayer(mPlayer);
        mVideoView.setPlaybackPreparer(new PlaybackPreparer() {
            @Override
            public void preparePlayback() {
                if (mPlayer.getPlaybackState() == OnePlayer.STATE_IDLE) {
                    mPlayer.prepareAsync();
                }
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
//            mPlayer.setDataSource(DASH_SD_HD);

            // ss
//            mPlayer.setDataSource(SS);

            // hls
//                mPlayer.setDataSource(HLS);
//                mPlayer.setDataSource(HLS_PL_MP4);

            // hls live
//            mPlayer.setDataSource(HLS_LIVE_hbws_bq);
            mPlayer.setDataSource(HLS_FAST_8);

            // 360
//            mPlayer.setDataSource(_360_congo);

            // dash h264 mp4 drm
//            headersMap.put("contentId", "");
//            headersMap.put("provider", "widevine_test");
//            headersMap.put("extension", "mpd");
//            mPlayer.setDataSource(DASH_SECURE_SD, headersMap);


//            headersMap.put("extension", "mpd");
//            mPlayer.setDataSource(MPD, headersMap);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mPlayer != null) {
            mPlayer.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mPlayer.isPlaying()) {
            mPlayer.stop();
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
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "onConfigurationChanged: " + newConfig.orientation);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
//            PlayerUtils.hideActionBar(this);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏
//            PlayerUtils.showActionBar(this);
        }
    }

    @Override
    public void onBackPressed() {
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
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
