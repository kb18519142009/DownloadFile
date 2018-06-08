package com.example.downloadfile.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.downloadfile.R;
import com.example.downloadfile.util.SystemUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private Button mDownloadPicture; //下载图片按钮
    private Button mDownloadVideo; //下载视频按钮

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SystemUtil.setLightStatusBar(this, Color.WHITE);
        }

        mContext = this;

        FrameLayout backLayout = findViewById(R.id.btn_back);
        backLayout.setVisibility(View.GONE);

        TextView toolbarTitle = findViewById(R.id.tv_toolbar_title);
        toolbarTitle.setText(R.string.main_activity_title);

        mDownloadPicture = findViewById(R.id.btn_download_picture);
        mDownloadPicture.setOnClickListener(this);
        mDownloadVideo = findViewById(R.id.btn_download_video);
        mDownloadVideo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mDownloadPicture) {
            Intent downloadPictureIntent = new Intent(mContext, DownloadPictureActivity.class);
            startActivity(downloadPictureIntent);
        } else if (v == mDownloadVideo) {
            Intent downloadVideoIntent = new Intent(mContext, DownloadVideoActivity.class);
            startActivity(downloadVideoIntent);
        }
    }
}
