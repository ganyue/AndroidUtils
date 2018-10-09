package com.gy.utils.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * created by yue.gan 18-9-28
 */
public class PermissionUtil {

    private static final String[] CALENDAR = {
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
    };

    private static final String[] CAMERA = {
            Manifest.permission.CAMERA,
    };

    private static final String[] CONTACTS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS,
    };

    private static final String[] LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private static final String[] MICROPHONE = {
            Manifest.permission.RECORD_AUDIO,
    };

    private static final String[] PHONE = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.ADD_VOICEMAIL,
            Manifest.permission.USE_SIP,
    };

    private static final String[] SENSORS = {
            Manifest.permission.BODY_SENSORS,
    };

    private static final String[] SMS = {
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.RECEIVE_MMS,
    };

    private static final String[] STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private static boolean contains (String[] permissions, String permission) {
        for (String p: permissions) {
            if (p.equals(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * <P>查找制定dangers权限对应的权限group</P>
     * @param permission 权限
     * @return 权限所在权限组，如果非dangers权限返回空字符串
     */
    public static String getPermissionGroup (String permission) {
        if (Build.VERSION.SDK_INT < 23) return "";
        if (contains(CALENDAR, permission)) return Manifest.permission_group.CALENDAR;
        if (contains(CAMERA, permission)) return Manifest.permission_group.CAMERA;
        if (contains(CONTACTS, permission)) return Manifest.permission_group.CONTACTS;
        if (contains(LOCATION, permission)) return Manifest.permission_group.LOCATION;
        if (contains(MICROPHONE, permission)) return Manifest.permission_group.MICROPHONE;
        if (contains(PHONE, permission)) return Manifest.permission_group.PHONE;
        if (contains(SENSORS, permission)) return Manifest.permission_group.SENSORS;
        if (contains(SMS, permission)) return Manifest.permission_group.SMS;
        if (contains(STORAGE, permission)) return Manifest.permission_group.STORAGE;

        if (Manifest.permission_group.CALENDAR.equals(permission) ||
                Manifest.permission_group.CAMERA.equals(permission) ||
                Manifest.permission_group.CONTACTS.equals(permission) ||
                Manifest.permission_group.LOCATION.equals(permission) ||
                Manifest.permission_group.MICROPHONE.equals(permission) ||
                Manifest.permission_group.PHONE.equals(permission) ||
                Manifest.permission_group.SENSORS.equals(permission) ||
                Manifest.permission_group.SMS.equals(permission) ||
                Manifest.permission_group.STORAGE.equals(permission)) {
            return permission;
        }

        return "";
    }

    /**
     * <p>通过PackageInfo获取所有权限，从中筛选出所有未获取到的权限并返回</p>
     * <P>注：requestPermissionFlags如下： </P>
     * <p>1:REQUESTED_PERMISSION_REQUIRED </P>
     * <p>2：REQUESTED_PERMISSION_GRANTED </p>
     * <p>3:REQUESTED_PERMISSION_REQUIRED | REQUESTED_PERMISSION_GRANTED </p>
     * @param context 获取PackageInfo用
     * @return 所有未获取的权限
     */
    public static List<String> getAllUnPermitedPermissions (Context context) {
        ArrayList<String> permissions = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pkgInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] requestPermissions = pkgInfo.requestedPermissions;
            int[] requestPermissionFlags = pkgInfo.requestedPermissionsFlags;

            for (int i = 0; i < requestPermissionFlags.length; i++) {
                if ((requestPermissionFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED)
                        != PackageInfo.REQUESTED_PERMISSION_GRANTED) {
                    permissions.add(requestPermissions[i]);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return permissions;
    }

    public static void requestAllPermissions (Activity activity, int requestCode) {
        List<String> permissions = getAllUnPermitedPermissions(activity);
        List<String> groups = new ArrayList<>();
        for (String permission: permissions) {
            String group = getPermissionGroup(permission);
            if (TextUtils.isEmpty(group)) continue;
            groups.add(group);
        }

        String[] requestGroups = new String[groups.size()];
        for (int i = 0; i < groups.size(); i++) {
            requestGroups[i] = groups.get(i);
        }

        if (groups.size() <= 0) return;
        ActivityCompat.requestPermissions(activity, requestGroups, requestCode);
    }

    public static void requestPermissonByGroup (Activity activity, int requestCode, String ...permissions) {
        List<String> groups = new ArrayList<>();
        for (String permission: permissions) {
            String group = getPermissionGroup(permission);
            if (TextUtils.isEmpty(group)) continue;
            groups.add(group);
        }

        String[] requestGroups = new String[groups.size()];
        for (int i = 0; i < groups.size(); i++) {
            requestGroups[i] = groups.get(i);
        }

        if (groups.size() <= 0) return;
        ActivityCompat.requestPermissions(activity, requestGroups, requestCode);
    }
}
