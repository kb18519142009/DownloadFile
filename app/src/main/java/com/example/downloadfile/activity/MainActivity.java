package com.example.downloadfile.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.downloadfile.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private Button mDownloadPicture;
    private Button mDownloadVideo;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
