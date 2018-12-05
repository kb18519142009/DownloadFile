package com.example.downloadfile.util.permission;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.AppOpsManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.example.downloadfile.util.DeviceUtils;


/**
 * Description：
 * Created by kang on 2017/10/26.
 */
public class KbPermissionUtils {
    private volatile static KbPermissionUtils instance;

    private KbPermissionUtils() {
    }

    public static KbPermissionUtils getInstance() {
        if (instance == null) {
            synchronized (KbPermissionUtils.class) {
                if (instance == null) instance = new KbPermissionUtils();
            }
        }
        return instance;
    }
    /**
     * 检查权限检查
     *
     * @param context
     * @param permissions
     * @return
     */
    public boolean checkPermission(Context context, String... permissions) {
        for (String permission : permissions) {
            // 判断当前该权限是否允许
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        for (String permission : permissions) {
            String op = AppOpsManagerCompat.permissionToOp(permission);
            if (TextUtils.isEmpty(op)) continue;
            int result = AppOpsManagerCompat.noteProxyOp(context, op, context.getPackageName());
            if (result == AppOpsManagerCompat.MODE_IGNORED) {
                return false;
            }
            result = ContextCompat.checkSelfPermission(context, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 权限请求方法
     *
     * @param activity
     * @param code
     * @param permissions
     */
    public void requestPermission(Activity activity, int code, String... permissions) {
        ActivityCompat.requestPermissions(activity, permissions, code);
    }

    /**
     * 判断是否需要动态申请权限
     *
     * @return
     */
    public static boolean needRequestPermission() {
        return Build.VERSION.SDK_INT >= 23;
    }

    /**
     * 去设置界面
     */
    public static void goSetting(final Context context) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("您已禁止了权限，是否要去开启？")
                .setPositiveButton("立即前往", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        getAppDetailSettingIntent(context);
                    }
                })
                .setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 跳转到权限设置界面
     */
    private static void getAppDetailSettingIntent(Context context) {
//        Intent intent = new Intent(Settings.ACTION_SETTINGS);
//        context.startActivity(intent);
//        return;

        // vivo 点击设置图标>加速白名单>我的app
        //      点击软件管理>软件管理权限>软件>我的app>信任该软件
//        Intent appIntent = context.getPackageManager().getLaunchIntentForPackage("com.iqoo.secure");
//        if (appIntent != null) {
//            context.startActivity(appIntent);
//            return;
//        }
//
//        // oppo 点击设置图标>应用权限管理>按应用程序管理>我的app>我信任该应用
//        //      点击权限隐私>自启动管理>我的app
//        appIntent = context.getPackageManager().getLaunchIntentForPackage("com.oppo.safe");
//        if (appIntent != null) {
//            context.startActivity(appIntent);
//            return;
//        }
//
//        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (Build.VERSION.SDK_INT >= 9) {
//            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
//        } else if (Build.VERSION.SDK_INT <= 8) {
//            intent.setAction(Intent.ACTION_VIEW);
//            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
//            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
//        }
//        context.startActivity(intent);
        Intent intent = null;
        if (DeviceUtils.getManufacturer().equals("huawei")) {
            intent = huaweiApi(context);
        } else if (DeviceUtils.getManufacturer().equals("xiaomi")) {
            intent = xiaomiApi(context);
        } else if (DeviceUtils.getManufacturer().equals("oppo")) {
            intent = oppoApi(context);
        } else if (DeviceUtils.getManufacturer().equals("vivo")) {
            intent = vivoApi(context);
        } else if (DeviceUtils.getManufacturer().equals("samsung")) {
            intent = samsungApi(context);
        } else if (DeviceUtils.getManufacturer().equals("meizu")) {
            intent = meizuApi(context);
        } else if (DeviceUtils.getManufacturer().equals("smartisan")) {
            intent = smartisanApi(context);
        }
        intent = defaultApi(context);
        context.startActivity(intent);
    }

    /**
     * App details page.
     */
    private static Intent defaultApi(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return intent;
    }

    /**
     * Huawei cell phone Api23 the following method.
     */
    private static Intent huaweiApi(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return defaultApi(context);
        }
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity"));
        return intent;
    }

    /**
     * Xiaomi phone to achieve the method.
     */
    private static Intent xiaomiApi(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("extra_pkgname", context.getPackageName());
        return intent;
    }

    /**
     * Vivo phone to achieve the method.
     */
    private static Intent vivoApi(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packagename", context.getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity"));
        } else {
            intent.setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity"));
        }
        return intent;
    }

    /**
     * Oppo phone to achieve the method.
     */
    private static Intent oppoApi(Context context) {
        return defaultApi(context);
    }

    /**
     * Meizu phone to achieve the method.
     */
    private static Intent meizuApi(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            return defaultApi(context);
        }
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", context.getPackageName());
        intent.setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity"));
        return intent;
    }

    /**
     * Smartisan phone to achieve the method.
     */
    private static Intent smartisanApi(Context context) {
        return defaultApi(context);
    }

    /**
     * Samsung phone to achieve the method.
     */
    private static Intent samsungApi(Context context) {
        return defaultApi(context);
    }
}
