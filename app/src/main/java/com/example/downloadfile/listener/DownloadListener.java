package com.example.downloadfile.listener;

/**
 * Description：
 * Created by kang on 2018/3/9.
 */

public interface DownloadListener {
    void onStart();

    void onProgress(int currentLength);

    void onFinish(String localPath);

    void onFailure(String erroInfo);
}
