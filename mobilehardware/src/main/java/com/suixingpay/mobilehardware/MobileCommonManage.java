package com.suixingpay.mobilehardware;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.suixingpay.mobilehardware.base.BaseData;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Author:   renjinlong
 * Email:    chinarenlong@foxmail.com
 * Date:     19/10/8
 * Description:获取app common信息
 */
public class MobileCommonManage {
    private static final String TAG = MobileCommonManage.class.getSimpleName();

    private static MobileCommonManage _inst;
    private Context ctx;

    public static MobileCommonManage getInstance(Context ctx) {
        if (_inst != null) {
            return _inst;
        } else {
            _inst = new MobileCommonManage(ctx);
            return _inst;
        }
    }

    private MobileCommonManage(Context ctx) {
        init(ctx);
    }

    private void init(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * 获取基站信息
     */
    @SuppressLint("MissingPermission")
    public Map<String, Object> getCellLocationInfo() {
        Map<String, Object> map = new HashMap<>();
        try {
            TelephonyManager mTelephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

            if (mTelephonyManager == null) {
                return map;
            }

            // 返回值MCC + MNC
            String operator = mTelephonyManager.getNetworkOperator();
            int mcc = Integer.parseInt(operator.length() >= 3 ? operator.substring(0, 3) : "0");
            int mnc = Integer.parseInt(operator.length() >= 4 ? operator.substring(3) : "0");

            CellLocation cellLocation = mTelephonyManager.getCellLocation();

            int lac = 0, cellId = 0;
            // 中国移动和中国联通获取LAC、CID的方式
            if (cellLocation instanceof GsmCellLocation) {
                GsmCellLocation location = (GsmCellLocation) cellLocation;
                lac = location.getLac();
                cellId = location.getCid();
            } else if (cellLocation instanceof CdmaCellLocation) { // 中国电信获取LAC、CID的方式
                CdmaCellLocation location = (CdmaCellLocation) cellLocation;
                lac = location.getNetworkId();
                cellId = location.getBaseStationId();
            }

            map.put(BaseData.CommonInfo.CELL_LOC_MCC, mcc);
            map.put(BaseData.CommonInfo.CELL_LOC_MNC, mnc);
            map.put(BaseData.CommonInfo.CELL_LOC_LAC, lac);
            map.put(BaseData.CommonInfo.CELL_LOC_CELLID, cellId);
            map.put(BaseData.CommonInfo.CELL_LOC_INFO, lac + ":" + mcc + ":" + mnc + ":" + cellId);

            // 获取邻区基站信息
//        List<NeighboringCellInfo> infos = mTelephonyManager.getNeighboringCellInfo();
//        StringBuffer sb = new StringBuffer("总数 : " + infos.size() + "\n");
//        for (NeighboringCellInfo info1 : infos) { // 根据邻区总数进行循环
//            sb.append(" LAC : " + info1.getLac()); // 取出当前邻区的LAC
//            sb.append(" CID : " + info1.getCid()); // 取出当前邻区的CID
//            sb.append(" BSSS : " + (-113 + 2 * info1.getRssi()) + "\n"); // 获取邻区基站信号强度
//        }
//
//        Log.i(TAG, " 获取邻区基站信息:" + sb.toString());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return map;

    }

    /**
     * 判断手机是否root
     *
     * @return
     */
    public Boolean isRooted() {
        if (android.os.Build.TAGS != null && android.os.Build.TAGS.contains("test-keys")) {
            return true;
        }

        String[] probableRootPaths = {
                "/data/local/bin/su",
                "/data/local/su",
                "/data/local/xbin/su",
                "/sbin/su",
                "/su/bin",
                "/su/bin/su",
                "/system/app/SuperSU",
                "/system/app/SuperSU.apk",
                "/system/app/Superuser",
                "/system/app/Superuser.apk",
                "/system/bin/failsafe/su",
                "/system/bin/su",
                "/system/sd/xbin/su",
                "/system/xbin/daemonsu",
                "/system/xbin/su"
        };

        for (String probableRootPath : probableRootPaths) {
            try {
                if (new File(probableRootPath).exists()) {
                    return true;
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception while attempting to detect whether the device is rooted", e);
            }
        }
        return false;
    }

    /**
     * 判断是否是模拟器
     *
     * @return
     */
    public Boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @return true 表示开启
     */
    public boolean isOPenGPS() {
        boolean temp = false;
        try {
            LocationManager locationManager
                    = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

            if (locationManager == null) {
                return temp;
            }

            // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
            boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
//            boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            return gps;

        } catch (Exception e) {
            Log.e(TAG, "Exception while attempting to detect whether the device is openGps", e);
        }

        return temp;
    }


}
