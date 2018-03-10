package com.example.downloadfile.util.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Description：Permission封装类
 * Created by kang on 2017/10/26.
 */
public class KbPermission {
//    private Activity activity;
    //持有弱引用HandlerActivity,GC回收时会被回收掉.解决内存泄漏的问题
    private WeakReference<Activity> mWeakActivity;

    private int requestCode;

    private KbPermissionListener listener;

    private String[] permissions;

    private static KbPermission instance = new KbPermission();

    private static List<Integer> codes = new ArrayList<>();

    private KbPermission() {
    }

    /**
     * 关联上下文
     *
     * @param activity
     * @return
     */
    @NonNull
    public static KbPermission with(@NonNull Activity activity) {
        instance.setActivity(activity);
        return instance;
    }

    /**
     * 关联上下文
     *
     * @param fragment
     * @return
     */
    @NonNull
    public static KbPermission with(@NonNull android.app.Fragment fragment) {
        instance.setActivity(fragment.getActivity());
        return instance;
    }

    /**
     * 关联上下文
     *
     * @param fragment
     * @return
     */
    @NonNull
    public static KbPermission with(@NonNull android.support.v4.app.Fragment fragment) {
        instance.setActivity(fragment.getActivity());
        return instance;
    }

    /**
     * 设置权限请求码
     *
     * @param requestCode
     * @return
     */
    @NonNull
    public KbPermission requestCode(@NonNull int requestCode) {
        codes.add(requestCode);
        instance.setRequestCode(requestCode);
        return instance;
    }

    /**
     * 设置请求回调
     *
     * @param listener
     * @return
     */
    @NonNull
    public KbPermission callBack(@NonNull KbPermissionListener listener) {
        instance.setListener(listener);
        return instance;
    }

    /**
     * 请求项目
     *
     * @param permissions
     * @return
     */
    @NonNull
    public KbPermission permission(@NonNull String... permissions) {
        instance.setPermissions(permissions);
        return instance;
    }

    /**
     * 开始请求
     */
    @NonNull
    public void send() {
        if (instance == null || instance.getWeakActivity().get() == null || instance.getListener() == null
                || instance.getPermissions() == null) {
            return;
        }

        // 判断是否授权
        if (KbPermissionUtils.getInstance().checkPermission(instance.getWeakActivity().get(), instance.getPermissions())) {
            // 已经授权，执行授权回调
            instance.getListener().onPermit(instance.getRequestCode(), instance.getPermissions());
        } else {
            KbPermissionUtils.getInstance().requestPermission(instance.getWeakActivity().get(), instance.getRequestCode(), instance.getPermissions());
        }

    }

    /**
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public static void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (instance == null) {
            return;
        }
        for (int j = 0; j < codes.size(); j++) {
            if (requestCode == codes.get(j)) {
                // 遍历请求时的所有权限
                for (int i = 0; i < grantResults.length; i++) {
                    // 授权
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        instance.getListener().onPermit(codes.get(j), permissions);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        instance.getListener().onCancel(codes.get(j), permissions);
                    }

                }
                codes.remove(codes.get(j));
            }
        }
    }


    //==================================以下为get、set方法================================================

    public WeakReference<Activity> getWeakActivity() {
        return mWeakActivity;
    }

    public void setWeakActivity(WeakReference<Activity> mWeakActivity) {
        this.mWeakActivity = mWeakActivity;
    }

//    public Activity getActivity() {
//        return activity;
//    }

    public void setActivity(Activity activity) {
//        this.activity = activity;
        mWeakActivity = new WeakReference<Activity>(activity);
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public KbPermissionListener getListener() {
        return listener;
    }

    public void setListener(KbPermissionListener listener) {
        this.listener = listener;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }
}
