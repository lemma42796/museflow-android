package com.ixuea.superui.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * Package(应用)相关工具方法
 */
public class SuperPackageUtil {

    /**
     * 版本名称
     * 一般是用来显示给人类阅读的
     *
     * @param context
     * @return 一般都是3位：例如：2.0.1
     */
    public static String getVersionName(Context context) {
        try {
            //获取应用信息
            PackageInfo packageInfo = getPackageInfo(context, context.getPackageName());
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 版本号
     *
     * @param context
     * @return 一般是整形，例如：100；使用int更方便判断大小
     */
    public static long getVersionCode(Context context) {
        //获取应用信息
        try {
            PackageInfo packageInfo = getPackageInfo(context, context.getPackageName());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return packageInfo.getLongVersionCode();
            }
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 获取应用信息
     *
     * @param context
     * @param packageName
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    private static PackageInfo getPackageInfo(Context context, String packageName) throws PackageManager.NameNotFoundException {
        //获取包管理器
        PackageManager packageManager = context.getPackageManager();

        return packageManager.getPackageInfo(packageName, 0);
    }

    /**
     * 获取md5签名
     *
     * @param context
     * @return
     */
    public static String getMD5Signature(Context context) {
        return SuperPackageUtil.getSignature(context, "MD5");
    }

    /**
     * 获取SHA1签名
     *
     * @param context
     * @return
     */
    public static String getSHA1Signature(Context context) {
        return SuperPackageUtil.getSignature(context, "SHA1");
    }

    /**
     * 获取签名
     *
     * @param context
     * @return
     */
    public static String getSignature(Context context, String algorithm) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);

            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] publicKey = md.digest(cert);
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString.toLowerCase());

                if (i != publicKey.length - 1) {
                    hexString.append(":");
                }
            }
            return hexString.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
