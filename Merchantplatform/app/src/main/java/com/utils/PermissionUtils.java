package com.utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.merchantplatform.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.permission;

/**
 * Created by 58 on 2016/11/28.
 * ---->checkSelfPermission(@NonNull Context context, @NonNull String permission) ，
 *                                         主要用于检测某个权限是否已经被授予，
 *                                        方法返回值为PackageManager.PERMISSION_DENIED或者PackageManager.PERMISSION_GRANTED。
 *                                        当返回DENIED就需要进行申请授权了。
 *
 * ----> requestPermissions(final @NonNull Activity activity,final @NonNull String[] permissions, final int requestCode),
 *                                        主要用于申请某个权限
 *                                        该方法是异步的，第一个参数是Context；
 *                                        第二个参数是需要申请的权限的字符串数组；
 *                                        第三个参数为requestCode，主要用于回调的时候检测。
 *
 * --->shouldShowRequestPermissionRationale(@NonNull Activity activity,@NonNull String permission)
 *                                        主要用于给用户一个申请权限的解释，
 *                                        该方法只有在用户在上一次已经拒绝过你的这个权限申请。
 *                                        也就是说，用户已经拒绝一次了，你又弹个授权框，你需要给用户一个解释，为什么要授权，则使用该方法。
 *
 * ---->onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults)
 *                                       首先验证requestCode定位到你的申请，
 *                                       然后验证grantResults对应于申请的结果，这里的数组对应于申请时的第二个权限字符串数组。
 *                                       如果你同时申请两个权限，那么grantResults的length就为2，分别记录你两个权限的申请结果
 *
 */

public class PermissionUtils {
    public static final int CODE_RECORD_AUDIO = 0;
    public static final int CODE_GET_ACCOUNTS = 1;
    public static final int CODE_READ_PHONE_STATE = 2;
    public static final int CODE_CALL_PHONE = 3;
    public static final int CODE_CAMERA = 4;
    public static final int CODE_ACCESS_FINE_LOCATION = 5;
    public static final int CODE_ACCESS_COARSE_LOCATION = 6;
    public static final int CODE_READ_EXTERNAL_STORAGE = 7;
    public static final int CODE_WRITE_EXTERNAL_STORAGE = 8;
    public static final int CODE_READ_CALL_LOG = 9;
    public static final int CODE_MULTI_PERMISSION = 100;

    public static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String PERMISSION_GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;
    public static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String PERMISSION_CALL_PHONE = Manifest.permission.CALL_PHONE;
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String PERMISSION_READ_CALL_LOG = Manifest.permission.READ_CALL_LOG;

    private static final String[] requestPermissions = {
            PERMISSION_RECORD_AUDIO,
            PERMISSION_GET_ACCOUNTS,
            PERMISSION_READ_PHONE_STATE,
            PERMISSION_CALL_PHONE,
            PERMISSION_CAMERA,
            PERMISSION_ACCESS_FINE_LOCATION,
            PERMISSION_ACCESS_COARSE_LOCATION,
            PERMISSION_READ_EXTERNAL_STORAGE,
            PERMISSION_WRITE_EXTERNAL_STORAGE,
            PERMISSION_READ_CALL_LOG
    };

    public interface PermissionGrant {
        void onPermissionGranted(int requestCode);
    }

    /**
     * 请求一个权限
     *
     * @param activity
     * @param requestCode 请求码, 例如：要获取相机权限，参数是PermissionUtils.CODE_CAMERA
     */
    public static void requestPermission(final Activity activity, final int requestCode, PermissionGrant permissionGrant) {
        if (activity == null) {
            return;
        }
        if (requestCode < 0 || requestCode >= requestPermissions.length) {
            return;
        }
        final String requestPermission = requestPermissions[requestCode];
        int checkSelfPermission;
        try {
            checkSelfPermission = ContextCompat.checkSelfPermission(activity, requestPermission);
        } catch (RuntimeException e) {
            Toast.makeText(activity, "请开启此权限", Toast.LENGTH_SHORT).show();
            return;
        }
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                shouldShowRationale(activity, requestCode, requestPermission);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
            }
        } else {
            permissionGrant.onPermissionGranted(requestCode);
        }
    }

    /**
     * 在系统的回调中传入，可以获取到获取权限结果
     * onRequestPermissionsResult
     *
     * @param activity
     * @param requestCode  Need consistent with requestPermission
     * @param permissions
     * @param grantResults
     */
    public static void requestPermissionsResult(final Activity activity, final int requestCode, @NonNull String[] permissions,
                                                @NonNull int[] grantResults, PermissionGrant permissionGrant) {
        if (activity == null) {
            return;
        }
        if (requestCode == CODE_MULTI_PERMISSION) {
            requestMultiResult(activity, permissions, grantResults, permissionGrant);
            return;
        }
        if (requestCode < 0 || requestCode >= requestPermissions.length) {
            Toast.makeText(activity, "非法请求:" + requestCode, Toast.LENGTH_SHORT).show();
            return;
        }
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionGrant.onPermissionGranted(requestCode);
        } else {
            String[] permissionsHint = activity.getResources().getStringArray(R.array.permissions);
            openSettingActivity(activity, "结果：" + permissionsHint[requestCode]);
        }
    }

    /**
     * 一次申请多个权限，多个权限指的是在Utils里指定好的这些权限
     */
    public static void requestMultiPermissions(final Activity activity, PermissionGrant grant) {
        final List<String> permissionsList = getNoGrantedPermission(activity, false);
        final List<String> shouldRationalePermissionsList = getNoGrantedPermission(activity, true);
        if (permissionsList == null || shouldRationalePermissionsList == null) {
            return;
        }
        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), CODE_MULTI_PERMISSION);
        } else if (shouldRationalePermissionsList.size() > 0) {
            showMessageOKCancel(activity, "需要开启这些权限",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, shouldRationalePermissionsList.toArray(new String[shouldRationalePermissionsList.size()]), CODE_MULTI_PERMISSION);
                        }
                    });
        } else {
            grant.onPermissionGranted(CODE_MULTI_PERMISSION);
        }
    }

    /**
     * 申请多个权限结果回调
     */
    private static void requestMultiResult(Activity activity, String[] permissions, int[] grantResults, PermissionGrant permissionGrant) {
        if (activity == null) {
            return;
        }
        Map<String, Integer> perms = new HashMap<>();
        ArrayList<String> notGranted = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            perms.put(permissions[i], grantResults[i]);
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                notGranted.add(permissions[i]);
            }
        }
        if (notGranted.size() == 0) {
            Toast.makeText(activity, "获取权限成功" + notGranted, Toast.LENGTH_SHORT).show();
            permissionGrant.onPermissionGranted(CODE_MULTI_PERMISSION);
        } else {
            openSettingActivity(activity, "需要获取这些权限");
        }
    }

    private static void shouldShowRationale(final Activity activity, final int requestCode, final String requestPermission) {
        String[] permissionsHint = activity.getResources().getStringArray(R.array.permissions);
        showMessageOKCancel(activity, "说明：" + permissionsHint[requestCode], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
            }
        });
    }

    private static void showMessageOKCancel(final Activity activity, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("确认", okListener)
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    private static void openSettingActivity(final Activity activity, String message) {
        showMessageOKCancel(activity, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
            }
        });
    }

    /**
     * @param activity
     * @param isShouldRationale true: 返回没有获取并且需要显示请求说明的权限, false:返回没有获取并且不需要显示说明的权限
     * @return
     */
    public static ArrayList<String> getNoGrantedPermission(Activity activity, boolean isShouldRationale) {
        ArrayList<String> permissions = new ArrayList<>();
        for (int i = 0; i < requestPermissions.length; i++) {
            String requestPermission = requestPermissions[i];
            int checkSelfPermission = -1;
            try {
                checkSelfPermission = ContextCompat.checkSelfPermission(activity, requestPermission);
            } catch (RuntimeException e) {
                Toast.makeText(activity, "请开启这些权限：", Toast.LENGTH_SHORT).show();
                return null;
            }
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                    if (isShouldRationale) {
                        permissions.add(requestPermission);
                    }
                } else {
                    if (!isShouldRationale) {
                        permissions.add(requestPermission);
                    }
                }
            }
        }
        return permissions;
    }


    /**
     * 获取手机IMEI设备号
     * @param activity
     */
    public static void getIMEIPermission(Activity activity){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(activity, PERMISSION_READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(activity, new String[]{PERMISSION_READ_PHONE_STATE}, CODE_READ_PHONE_STATE);
            }else{
                AppInfoUtils.getIMEI(activity);
            }
        }else{
            AppInfoUtils.getIMEI(activity);
        }
    }

}