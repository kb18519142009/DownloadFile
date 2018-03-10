# DownloadFile
关于实现Retrofit带进度下载文件的Demo

一 、效果图：

![效果图](http://img.blog.csdn.net/20180310151723267?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQva19iYl82NjY=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
 
 
二 、分别实现了下载图片和视频的效果，代码：
```
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
            public void onFailure() {
                Log.e(TAG, "onFailure: ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fl_circle_progress.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
```
