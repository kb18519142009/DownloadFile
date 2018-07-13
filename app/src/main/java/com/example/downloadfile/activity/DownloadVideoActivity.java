package com.example.downloadfile.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.downloadfile.R;
import com.example.downloadfile.download.DownloadUtil;
import com.example.downloadfile.listener.DownloadListener;
import com.example.downloadfile.util.SystemUtil;
import com.example.downloadfile.util.permission.KbPermission;
import com.example.downloadfile.util.permission.KbPermissionListener;
import com.example.downloadfile.util.permission.KbPermissionUtils;
import com.example.downloadfile.view.KbWithWordsCircleProgressBar;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

/**
 * Description：视频下载界面
 * Created by kang on 2018/3/9.
 */
public class DownloadVideoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DownloadVideoActivity";
    private static final String PLAY_VIDEO_URL =
            "http://c1.daishumovie.com/7d2b85ce0799dacb96c3949e25d74678/5aa40100/video/client/2018/1/9/3F8F87CC040147F28AC97A4733491A8A_640x360_200_48_24.mp4";
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    private FrameLayout mBackLayout; //返回按钮
    private FrameLayout mCircleProgressLayout; //圆形进度条父布局
    private KbWithWordsCircleProgressBar mCircleProgress; //圆形进度条
    private SimpleExoPlayerView simpleExoPlayerView; //EXO播放器View

    private Context mContext;

    //ExoPlayer相关成员变量
    protected SimpleExoPlayer mPlayer;
    private DataSource.Factory mMediaDataSourceFactory;
    private ExtractorsFactory mExtractorsFactory;

    //下载相关
    private DownloadUtil mDownloadUtil;
    private String mVideoPath; //下载到本地的视频路径

    private boolean mIsBackground; //是否进入后台

    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_video);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SystemUtil.setLightStatusBar(this, Color.WHITE);
        }

        mContext = this;

        TextView toolbaritle = findViewById(R.id.tv_toolbar_title);
        toolbaritle.setText(R.string.download_video);
        mBackLayout = findViewById(R.id.btn_back);
        mBackLayout.setOnClickListener(this);
        mCircleProgressLayout = findViewById(R.id.fl_circle_progress);
        mCircleProgress = findViewById(R.id.circle_progress);
        simpleExoPlayerView = findViewById(R.id.simpleExoPlayerView);

        initVideo();

        if (KbPermissionUtils.needRequestPermission()) { //判断是否需要动态申请权限
            KbPermission.with(this)
                    .requestCode(100)
                    .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE) //需要申请的权限(支持不定长参数)
                    .callBack(new KbPermissionListener() {
                        @Override
                        public void onPermit(int requestCode, String... permission) { //允许权限的回调
                            downloadVideo(); //处理具体下载过程
                        }

                        @Override
                        public void onCancel(int requestCode, String... permission) { //拒绝权限的回调
                            KbPermissionUtils.goSetting(mContext); //跳转至当前app的权限设置界面
                        }
                    })
                    .send();
        } else {
            downloadVideo(); //处理具体下载过程
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsBackground && mPlayer != null && !TextUtils.isEmpty(mVideoPath)) {
            mIsBackground = false;
            mPlayer.prepare(createMediaSource(mVideoPath));
            simpleExoPlayerView.setPlayer(mPlayer);
            mPlayer.setPlayWhenReady(true);
        }
        if (mPlayer != null && mPlayer.getCurrentPosition() > 0) {
            mPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsBackground = true;
        if (mPlayer != null && mPlayer.getPlayWhenReady()) {
            mPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放播放器
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mBackLayout) {
            finish();
        }
    }

    //初始化视频
    private void initVideo() {
        if (mMediaDataSourceFactory == null) {
            mMediaDataSourceFactory = buildDataSourceFactory(this, false);
        }
        if (mExtractorsFactory == null) {
            mExtractorsFactory = new DefaultExtractorsFactory();
        }
        if (mPlayer == null) {
            mPlayer = createNewPlayer(this);
            mPlayer.addListener(eventListener);
        }
    }

    //播放事件监听
    private ExoPlayer.EventListener eventListener = new ExoPlayer.EventListener() {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            System.out.println("播放: onTimelineChanged 周期总数 " + timeline);
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            System.out.println("播放: TrackGroupArray  ");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case Player.STATE_ENDED:
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPlayer.prepare(createMediaSource(mVideoPath));
                            simpleExoPlayerView.setPlayer(mPlayer);
//                            simpleExoPlayerView.setUseController(false);
                            mPlayer.setPlayWhenReady(true);
                        }
                    }, 3000);
                    break;
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Log.e(TAG, "onPlayerError: " + "播放: onPlayerError");
        }

        @Override
        public void onPositionDiscontinuity() {
            Log.e(TAG, "onPositionDiscontinuity: ");
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        }
    };

    private void downloadVideo() {
        mDownloadUtil = new DownloadUtil();
        mDownloadUtil.downloadFile(PLAY_VIDEO_URL, new DownloadListener() {
            @Override
            public void onStart() {
                Log.e(TAG, "onStart: ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCircleProgressLayout.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onProgress(final int currentLength) {
                Log.e(TAG, "onLoading: " + currentLength);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCircleProgress.setProgress(currentLength);
                    }
                });
            }

            @Override
            public void onFinish(String localPath) {
                mVideoPath = localPath;
                Log.e(TAG, "onFinish: " + localPath);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCircleProgressLayout.setVisibility(View.GONE);
                        if (mPlayer == null) {
                            return;
                        }
                        mPlayer.prepare(createMediaSource(mVideoPath));
                        simpleExoPlayerView.setPlayer(mPlayer);
                        mPlayer.setPlayWhenReady(true);
                    }
                });
            }

            @Override
            public void onFailure(final String erroInfo) {
                Log.e(TAG, "onFailure: " + erroInfo);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCircleProgressLayout.setVisibility(View.GONE);
                        Toast.makeText(mContext, erroInfo, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private SimpleExoPlayer createNewPlayer(Context context) {
        TrackSelection.Factory adaptiveTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
        return ExoPlayerFactory.newSimpleInstance(context, trackSelector);
    }

    private DataSource.Factory buildDataSourceFactory(Context context, boolean useBandwidthMeter) {
        return buildDataSourceFactory(context, useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    private DataSource.Factory buildDataSourceFactory(Context context, DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(context, bandwidthMeter,
                buildHttpDataSourceFactory(context, bandwidthMeter));
    }

    private HttpDataSource.Factory buildHttpDataSourceFactory(Context context, DefaultBandwidthMeter bandwidthMeter) {
        String httpUserAgent = Util.getUserAgent(context, "ExoPlayer");
        return new DefaultHttpDataSourceFactory(httpUserAgent, bandwidthMeter);
    }

    private MediaSource createMediaSource(String... playUrls) {
        MediaSource[] ma = new MediaSource[playUrls.length];
        int i = 0;
        for (String url : playUrls) {
            ma[i++] = new ExtractorMediaSource(Uri.parse(url),
                    mMediaDataSourceFactory, mExtractorsFactory, null, null);
        }
        return new ConcatenatingMediaSource(ma);
    }
}
