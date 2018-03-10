package com.example.downloadfile.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.downloadfile.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_toolbar_title;
    private Button btn_download_picture;
    private Button btn_download_video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        tv_toolbar_title.setText(R.string.main_activity_title);
        btn_download_picture = findViewById(R.id.btn_download_picture);
        btn_download_picture.setOnClickListener(this);
        btn_download_video = findViewById(R.id.btn_download_video);
        btn_download_video.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_download_picture) {
            Intent downloadPictureIntent = new Intent(MainActivity.this, DownloadPictureActivity.class);
            startActivity(downloadPictureIntent);
        } else if (v.getId() == R.id.btn_download_video) {
            Intent downloadVideoIntent = new Intent(MainActivity.this, DownloadVideoActivity.class);
            startActivity(downloadVideoIntent);
        }
    }
}
