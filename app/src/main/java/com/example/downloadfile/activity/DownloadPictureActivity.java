package com.example.downloadfile.activity;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.downloadfile.R;
import com.example.downloadfile.download.DownloadUtil;
import com.example.downloadfile.listener.DownloadListener;
import com.example.downloadfile.util.SystemUtil;
import com.example.downloadfile.util.permission.KbPermission;
import com.example.downloadfile.util.permission.KbPermissionListener;
import com.example.downloadfile.util.permission.KbPermissionUtils;
import com.example.downloadfile.view.KbWithWordsCircleProgressBar;

/**
 * Description：图片下载界面
 * Created by kang on 2018/3/9.
 */
public class DownloadPictureActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DownloadPictureActivity";
    private static final String PICTURE_URL = "http://small-bronze.oss-cn-shanghai.aliyuncs.com/" +
            "image/video/cover/2018/3/8/8BBC6C00DF78476C98AD9CA482DEF635.jpg";

    private FrameLayout mBackLayout;
    private ImageView mPicture;
    private FrameLayout mCircleProgressLayout;
    private KbWithWordsCircleProgressBar mCircleProgress;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_picture);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SystemUtil.setLightStatusBar(this, Color.WHITE);
        }
        mContext = this;

        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(R.string.download_picture);
        mBackLayout = findViewById(R.id.btn_back);
        mBackLayout.setOnClickListener(this);
        mPicture = findViewById(R.id.iv_picture);
        mCircleProgressLayout = findViewById(R.id.fl_circle_progress);
        mCircleProgress = findViewById(R.id.circle_progress);

        if (KbPermissionUtils.needRequestPermission()) {
            KbPermission.with(this)
                    .requestCode(100)
                    .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .callBack(new KbPermissionListener() {
                        @Override
                        public void onPermit(int requestCode, String... permission) {
                            downloadPicture();
                        }

                        @Override
                        public void onCancel(int requestCode, String... permission) {
                            KbPermissionUtils.goSetting(mContext);
                        }
                    })
                    .send();
        } else {
            downloadPicture();
        }
    }

    private void downloadPicture() {
        //下载相关
        DownloadUtil downloadUtil = new DownloadUtil();
        downloadUtil.downloadFile(PICTURE_URL, new DownloadListener() {
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
            public void onFinish(final String localPath) {
                Log.e(TAG, "onFinish: " + localPath);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCircleProgressLayout.setVisibility(View.GONE);
                        Glide.with(mContext).load(localPath).into(mPicture);
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

    @Override
    public void onClick(View v) {
        if (v == mBackLayout) {
            finish();
        }
    }

    //必须添加，否则第一次请求成功权限不会走回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        KbPermission.onRequestPermissionResult(requestCode, permissions, grantResults);
    }
}
