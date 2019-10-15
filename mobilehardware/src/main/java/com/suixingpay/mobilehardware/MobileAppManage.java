package com.suixingpay.mobilehardware;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.Log;

import com.suixingpay.mobilehardware.base.BaseData;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * Author:   renjinlong
 * Email:    chinarenlong@foxmail.com
 * Date:     19/10/8
 * Description:获取app信息
 */
public class MobileAppManage {
    private static final String TAG = MobileAppManage.class.getSimpleName();

    private static MobileAppManage _inst;
    private Context ctx;

    public static synchronized MobileAppManage getInstance(Context ctx) {
        if (_inst == null) {
            _inst = new MobileAppManage(ctx);
            return _inst;
        }
        return _inst;
    }

    private MobileAppManage(Context ctx) {
        init(ctx);
    }

    private void init(Context ctx) {
        this.ctx = ctx;
    }


    /**
     * 获取安装包信息
     */
    public Map<String, String> getPackageInfo() {
        Map<String, String> map = new HashMap<>();
        try {
            PackageManager packageManager = ctx.getPackageManager();
            ApplicationInfo applicationInfo = ctx.getApplicationInfo();
            map.put(BaseData.App.APP_NAME, packageManager.getApplicationLabel(applicationInfo).toString());
            android.content.pm.PackageInfo packageInfo = packageManager.getPackageInfo(ctx.getPackageName(), 0);
            String packageName = packageInfo.packageName;
            map.put(BaseData.App.PACKAGE_NAME, packageName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                map.put(BaseData.App.APP_VERSION_CODE, packageInfo.getLongVersionCode() + "");
            } else {
                map.put(BaseData.App.APP_VERSION_CODE, packageInfo.versionCode + "");
            }
            map.put(BaseData.App.APP_VERSION_NAME, packageInfo.versionName);
            map.put(BaseData.App.APP_TARGET_SDK_VERSION, applicationInfo.targetSdkVersion + "");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                map.put(BaseData.App.APP_MIN_SDK_VERSION, applicationInfo.minSdkVersion + "");
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return map;
    }

    /**
     * 获取安装包签名信息
     *
     * @return
     */
    public String getAppSign() {
        String appSign = "";
        try {
            PackageManager packageManager = ctx.getPackageManager();
            android.content.pm.PackageInfo packageInfo = packageManager.getPackageInfo(ctx.getPackageName(), 0);
            appSign = getSign(ctx, packageInfo.packageName);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return appSign;
    }

    /**
     * 注销
     */
    public void destroy() {
        _inst = null;
    }


    @SuppressLint("PackageManagerGetSignatures")
    private Signature[] getRawSignature(Context paramContext, String paramString) {
        if ((paramString == null) || (paramString.length() == 0)) {
            return null;
        }
        PackageManager localPackageManager = paramContext.getPackageManager();
        android.content.pm.PackageInfo localPackageInfo;
        try {
            localPackageInfo = localPackageManager.getPackageInfo(paramString, PackageManager.GET_SIGNATURES);
            if (localPackageInfo == null) {
                return null;
            }
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            return null;
        }
        return localPackageInfo.signatures;
    }

    private String getSign(Context context, String packageName) {
        Signature[] arrayOfSignature = getRawSignature(context, packageName);
        if ((arrayOfSignature == null) || (arrayOfSignature.length == 0)) {
            return null;
        }
        return getMessageDigest(arrayOfSignature[0].toByteArray());
    }

    private String getMessageDigest(byte[] paramArrayOfByte) {
        char[] arrayOfChar1 = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramArrayOfByte);
            byte[] arrayOfByte = localMessageDigest.digest();
            int i = arrayOfByte.length;
            char[] arrayOfChar2 = new char[i * 2];
            int j = 0;
            int k = 0;
            while (true) {
                if (j >= i) {
                    return new String(arrayOfChar2);
                }
                int m = arrayOfByte[j];
                int n = k + 1;
                arrayOfChar2[k] = arrayOfChar1[(0xF & m >>> 4)];
                k = n + 1;
                arrayOfChar2[n] = arrayOfChar1[(m & 0xF)];
                j++;
            }
        } catch (Exception e) {
        }
        return null;
    }

}
