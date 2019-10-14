package com.suixingpay.mobilehardware;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.suixingpay.mobilehardware.base.BaseData;
import com.suixingpay.mobilehardware.utils.MobileHardwareUtils;

import java.lang.reflect.Method;

/**
 * Author:   renjinlong
 * Email:    chinarenlong@foxmail.com
 * Date:     19/10/8
 * Description:获取网络相关信息
 */
public class MobileNetWorkManage {
    private static final String TAG = MobileNetWorkManage.class.getSimpleName();

    private static MobileNetWorkManage _inst;
    private Context ctx;

    public static MobileNetWorkManage getInstance(Context ctx) {
        if (_inst != null) {
            return _inst;
        } else {
            _inst = new MobileNetWorkManage(ctx);
            return _inst;
        }
    }

    private MobileNetWorkManage(Context ctx) {
        init(ctx);
    }

    private void init(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * 拿到具体的网络类型
     *
     * @return
     */
    public String networkTypeALL() {

        try {
            // 检测权限
            if (!MobileHardwareUtils.checkHasPermission(ctx, Manifest.permission.ACCESS_NETWORK_STATE)) {
                return BaseData.UNKNOWN_PARAM;
            }

            // Wifi
            ConnectivityManager manager = (ConnectivityManager)
                    ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    Network network = manager.getActiveNetwork();
                    if (network != null) {
                        NetworkCapabilities capabilities = manager.getNetworkCapabilities(network);
                        if (capabilities != null) {
                            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                                return "WIFI";
                            } else if (!capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                                    && !capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                                    && !capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                                return BaseData.UNKNOWN_PARAM;
                            }
                        }
                    } else {
                        return BaseData.UNKNOWN_PARAM;
                    }
                } else {
                    NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                    if (networkInfo == null || !networkInfo.isConnected()) {
                        return BaseData.UNKNOWN_PARAM;
                    }

                    networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                        return "WIFI";
                    }
                }
            }

            // Mobile network
            TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context
                    .TELEPHONY_SERVICE);

            if (telephonyManager == null) {
                return BaseData.UNKNOWN_PARAM;
            }

            int networkType = telephonyManager.getNetworkType();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return "2G";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return "4G";
                case 20:
                    return "5G";
                default:
                    return BaseData.UNKNOWN_PARAM;
            }
        } catch (Exception e) {
            return BaseData.UNKNOWN_PARAM;
        }
    }

    /**
     * 网络是否可用
     */
    public boolean isNetworkAvailable() {
        try {
            ConnectivityManager mgr = (ConnectivityManager) ctx
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (mgr == null) {
                return false;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Network[] networks = mgr.getAllNetworks();
                NetworkInfo networkInfo;
                for (Network mNetwork : networks) {
                    networkInfo = mgr.getNetworkInfo(mNetwork);
                    if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                        return true;
                    }
                }
            } else {
                NetworkInfo[] info = mgr.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    /**
     * 是否开启飞行模式
     *
     * @param context
     * @return
     */
    public boolean getAirplaneMode(Context context) {
        int isAirplaneMode = Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0);
        return isAirplaneMode == 1;
    }

    /**
     * 是否具有nfc
     *
     * @param context
     * @return
     */
    public boolean hasNfc(Context context) {
        boolean bRet = false;
        if (context == null) {
            return false;
        }
        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        if (manager == null) {
            return false;
        }
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            // adapter存在，能启用
            bRet = true;
        }
        return bRet;
    }

    /**
     * 热点开关是否打开
     */
    private boolean isWifiApEnabled(Context context) {
        try {
            WifiManager mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(mWifiManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 获取当前热点配置
     *
     * @return
     */
    private WifiConfiguration getHotPotConfig(WifiManager mWifiManager) {
        try {
            @SuppressLint("PrivateApi") Method method = WifiManager.class.getDeclaredMethod("getWifiApConfiguration");
            method.setAccessible(true);
            return (WifiConfiguration) method.invoke(mWifiManager);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 是否有数据网络接入
     *
     * @param context
     * @return
     */
    public boolean haveIntent(Context context) {
        boolean mobileDataEnabled = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true);
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }
        return mobileDataEnabled;
    }

}
