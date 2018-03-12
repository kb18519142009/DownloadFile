package com.example.downloadfile.activity;

import android.Manifest;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private TextView tv_toolbar_title;
    private FrameLayout btn_back;
    private ImageView iv_picture;
    private FrameLayout fl_circle_progress;
    private KbWithWordsCircleProgressBar circle_progress;

    private Context mContext;

    //下载相关
    private DownloadUtil mDownloadUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_picture);

        mContext = this;

        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        tv_toolbar_title.setText(R.string.download_picture);
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        iv_picture = findViewById(R.id.iv_picture);
        fl_circle_progress = findViewById(R.id.fl_circle_progress);
        circle_progress = findViewById(R.id.circle_progress);

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
        mDownloadUtil = new DownloadUtil();
        mDownloadUtil.downloadFile(PICTURE_URL, new DownloadListener() {
            @Override
            public void onStart() {
                Log.e(TAG, "onStart: ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fl_circle_progress.setVisibility(View.VISIBLE);
                    }
                });

            }

            @Override
            public void onProgress(final int currentLength) {
                Log.e(TAG, "onLoading: " + currentLength);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        circle_progress.setProgress(currentLength);
                    }
                });

            }

            @Override
            public void onFinish(final String localPath) {
                Log.e(TAG, "onFinish: " + localPath);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fl_circle_progress.setVisibility(View.GONE);
                        Glide.with(mContext).load(localPath).into(iv_picture);
                    }
                });
            }

            @Override
            public void onFailure(final String erroInfo) {
                Log.e(TAG, "onFailure: " + erroInfo);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fl_circle_progress.setVisibility(View.GONE);
                        Toast.makeText(mContext, erroInfo, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back) {
            finish();
        }
    }
}
