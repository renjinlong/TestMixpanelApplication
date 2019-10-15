package com.suixingpay.mobilehardware;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.suixingpay.mobilehardware.base.BaseData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * Author:   renjinlong
 * Email:    chinarenlong@foxmail.com
 * Date:     19/10/8
 * Description:获取app信息
 */
public class MobileDebugInfoManage {
    private static final String TAG = MobileDebugInfoManage.class.getSimpleName();

    private static MobileDebugInfoManage _inst;
    private Context ctx;

    public static synchronized MobileDebugInfoManage getInstance(Context ctx) {
        if (_inst == null) {
            _inst = new MobileDebugInfoManage(ctx);
            return _inst;
        }
        return _inst;
    }

    private MobileDebugInfoManage(Context ctx) {
        init(ctx);
    }

    private void init(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * 判断是否打开了允许虚拟位置,如果打开了 则弹窗让他去关闭
     */
    public boolean isAllowMockLocation() {
        try {
            boolean isOpen = Settings.Secure.getInt(ctx.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0;
            /**
             * 该判断API是androidM以下的API,由于Android M中已经没有了关闭允许模拟位置的入口,所以这里一旦检测到开启了模拟位置,并且是android M以上,则
             * 默认设置为未有开启模拟位置
             */
            if (isOpen && Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                isOpen = false;
            }
            return isOpen;
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * 开启了调试模式
     *
     * @return
     */
    public boolean isOpenDebug() {
        try {
            return (Settings.Secure.getInt(ctx.getContentResolver(), Settings.Secure.ADB_ENABLED, 0) > 0);
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * 判斷是debug版本
     *
     * @return
     */
    public boolean checkIsDebugVersion() {
        try {
            return (ctx.getApplicationInfo().flags
                    & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * 是否正在调试
     *
     * @return
     */
    public boolean checkIsDebuggerConnected() {
        try {
            return android.os.Debug.isDebuggerConnected();
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * 读取TracePid
     *
     * @return
     */
    public boolean readProcStatus() {
        try {
            BufferedReader localBufferedReader =
                    new BufferedReader(new FileReader("/proc/" + android.os.Process.myPid() + "/status"));
            String tracerPid = "";
            for (; ; ) {
                String str = localBufferedReader.readLine();
                if (str.contains("TracerPid")) {
                    tracerPid = str.substring(str.indexOf(":") + 1, str.length()).trim();
                    break;
                }
                if (str == null) {
                    break;
                }
            }
            localBufferedReader.close();
            if ("0".equals(tracerPid)) {
                return false;
            } else {
                return true;
            }
        } catch (Exception fuck) {
            return false;
        }
    }
}
