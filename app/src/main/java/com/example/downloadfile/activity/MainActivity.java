package com.example.downloadfile.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.downloadfile.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mDownloadPicture;
    private Button mDownloadVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            Intent downloadPictureIntent = new Intent(MainActivity.this, DownloadPictureActivity.class);
            startActivity(downloadPictureIntent);
        } else if (v == mDownloadVideo) {
            Intent downloadVideoIntent = new Intent(MainActivity.this, DownloadVideoActivity.class);
            startActivity(downloadVideoIntent);
        }
    }
}
