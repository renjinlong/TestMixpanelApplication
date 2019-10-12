package com.suixingpay.mobilehardware;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.suixingpay.mobilehardware.base.BaseData;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Author:   renjinlong
 * Email:    chinarenlong@foxmail.com
 * Date:     19/10/8
 * Description:获取网络信号信息
 */
public class MobileSignalManage {
    private static final String TAG = MobileSignalManage.class.getSimpleName();

    private static MobileSignalManage _inst;
    private Context ctx;

    public static MobileSignalManage getInstance(Context ctx) {
        if (_inst != null) {
            return _inst;
        } else {
            _inst = new MobileSignalManage(ctx);
            return _inst;
        }
    }

    private MobileSignalManage(Context ctx) {
        init(ctx);
    }

    private void init(Context ctx) {
        this.ctx = ctx;
    }


    /**
     * 获取IPV4地址
     *
     * @return
     */
    public String getNetIPV4() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            Log.i(TAG, e.toString());
        }
        return null;
    }

    /**
     * 获取NetIPV6地址
     *
     * @return
     */
    public String getNetIPV6() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet6Address && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            Log.i(TAG, e.toString());
        }
        return null;
    }

    /**
     * Anything worse than or equal to this will show 0 bars.
     */
    private static final int MIN_RSSI = -100;

    /**
     * Anything better than or equal to this will show the max bars.
     */
    private static final int MAX_RSSI = -55;

    private static int calculateSignalLevel(int rssi) {

        if (rssi <= MIN_RSSI) {
            return 0;
        } else if (rssi >= MAX_RSSI) {
            return 4;
        } else {
            float inputRange = (MAX_RSSI - MIN_RSSI);
            float outputRange = (4);
            return (int) ((float) (rssi - MIN_RSSI) * outputRange / inputRange);
        }
    }

    /**
     * 获取WifiInfo
     *
     * @param mContext
     * @return
     */
    private WifiInfo getWifiInfo(Context mContext) {
        WifiManager mWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager != null) {
            return mWifiManager.getConnectionInfo();
        }
        return null;
    }

    /**
     * 是否使用代理上网
     *
     * @return
     */
    public Map<String, Object> getWifiProxyInfo() {

        Map<String, Object> map = new HashMap<>();

        try {
            // 是否大于等于4.0
            final boolean isIcsOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
            String proxyAddress;
            int proxyPort;
            if (isIcsOrLater) {
                proxyAddress = System.getProperty("http.proxyHost");
                String portStr = System.getProperty("http.proxyPort");
                proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
            } else {
                proxyAddress = android.net.Proxy.getHost(ctx);
                proxyPort = android.net.Proxy.getPort(ctx);
            }
            if ((!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1)) {
                map.put(BaseData.Signal.PROXY, true);
                map.put(BaseData.Signal.PROXY_ADDRESS, proxyAddress);
                map.put(BaseData.Signal.PROXY_PORT, proxyPort + "");
            } else {
                map.put(BaseData.Signal.PROXY, false);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return map;
    }

    /**
     * wifi
     *
     * @return
     */
    public Map<String, Object> getDetailsWifiInfo() {
        Map<String, Object> map = new HashMap<>();
        try {
            WifiInfo mWifiInfo = getWifiInfo(ctx);
            int ip = mWifiInfo.getIpAddress();
            String strIp = "" + (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);

            map.put(BaseData.Signal.BSSID, mWifiInfo.getBSSID());
            map.put(BaseData.Signal.SSID, mWifiInfo.getSSID().replace("\"", ""));
            map.put(BaseData.Signal.N_IP_ADDRESS, strIp);
            map.put(BaseData.Signal.MAC_ADDRESS, getMac());

//            map.put(BaseData.Signal.NETWORK_ID,mWifiInfo.getNetworkId() + "");
//            map.put(BaseData.Signal.LINK_SPEED, mWifiInfo.getLinkSpeed() + "Mbps");
//            int rssi = mWifiInfo.getRssi();
//            map.put(BaseData.Signal.RSSI, rssi + "");
//            map.put(BaseData.Signal.LEVEL, mWifiInfo.getLinkSpeed() + "Mbps");
//            map.put(BaseData.Signal.SUPPLICANT_STATE, mWifiInfo.getSupplicantState() + "");
//            signalBean.setLevel(calculateSignalLevel(rssi) + "");
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
        return map;

    }


    /**
     * >=22的sdk则进行如下算法 mac
     *
     * @return
     */
    private String getMacForBuild() {
        try {
            for (
                    Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                    networkInterfaces.hasMoreElements(); ) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if ("wlan0".equals(networkInterface.getName())) {
                    byte[] hardwareAddress = networkInterface.getHardwareAddress();
                    if (hardwareAddress == null || hardwareAddress.length == 0) {
                        continue;
                    }
                    StringBuilder buf = new StringBuilder();
                    for (byte b : hardwareAddress) {
                        buf.append(String.format("%02X:", b));
                    }
                    if (buf.length() > 0) {
                        buf.deleteCharAt(buf.length() - 1);
                    }
                    return buf.toString();
                }
            }
        } catch (SocketException e) {
            Log.i(TAG, e.toString());
        }
        return BaseData.UNKNOWN_PARAM;
    }

    /**
     * get macAddress
     *
     * @return
     */
    @SuppressLint("HardwareIds")
    private String getMac() {
        if (Build.VERSION.SDK_INT >= 23) {
            return getMacForBuild();
        } else {
            try {
                return getWifiInfo(ctx).getMacAddress();
            } catch (Exception e) {
                return BaseData.UNKNOWN_PARAM;
            }
        }
    }

}
