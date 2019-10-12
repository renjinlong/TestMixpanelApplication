package com.suixingpay.mobilehardware;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
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
public class MobileSimInfoManage {
    private static final String TAG = MobileSimInfoManage.class.getSimpleName();

    private static MobileSimInfoManage _inst;
    private Context ctx;

    public static MobileSimInfoManage getInstance(Context ctx) {
        if (_inst != null) {
            return _inst;
        } else {
            _inst = new MobileSimInfoManage(ctx);
            return _inst;
        }
    }

    private MobileSimInfoManage(Context ctx) {
        init(ctx);
    }

    private void init(Context ctx) {
        this.ctx = ctx;
    }


    /**
     * 判断是否包含SIM卡
     *
     * @param context 上下文
     * @return 状态 是否包含SIM卡
     */
    public boolean hasSimCard(Context context) {
        boolean result = true;
        try {
            TelephonyManager telMgr = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            int simState = telMgr.getSimState();
            switch (simState) {
                case TelephonyManager.SIM_STATE_ABSENT:
                    result = false;
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    result = false;
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return result;
    }


    /**
     * 获取sim卡序列号
     */
    public String getSimSerial() {

        String simSerialNumber = "";

        try {
            TelephonyManager manager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            } else {
                simSerialNumber = manager.getSimSerialNumber();
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return simSerialNumber;
    }
}
