package com.suixingpay.mobilehardware;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import com.suixingpay.mobilehardware.base.BaseData;
import com.suixingpay.mobilehardware.utils.DoubleUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Author:   renjinlong
 * Email:    chinarenlong@foxmail.com
 * Date:     19/10/8
 * Description:获取手机电量信息
 */
public class MobileBatteryManage {
    private static final String TAG = MobileBatteryManage.class.getSimpleName();

    private static MobileBatteryManage _inst;
    private Context ctx;

    public static synchronized MobileBatteryManage getInstance(Context ctx) {
        if (_inst == null) {
            _inst = new MobileBatteryManage(ctx);
            return _inst;
        }
        return _inst;
    }

    private MobileBatteryManage(Context ctx) {
        init(ctx);
    }

    private void init(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * 获取电池信息
     *
     * @return
     */
    public Map<String, Object> getBatteryInfo() {
        Map<String, Object> map = new HashMap<>();
        try {
            Intent batteryStatus = ctx.registerReceiver(null,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (batteryStatus != null) {
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                double batteryLevel = -1;
                if (level != -1 && scale != -1) {
                    batteryLevel = DoubleUtils.divide((double) level, (double) scale);
                }
                // ac=1, usb=2, wireless=4
                int plugState = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                // unknown=1, charging=2, discharging=3, not charging=4, full=5
//                int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                // unknown=1, good=2, overheat=3, dead=4, over voltage=5, unspecified failure=6, cold=7
//                int health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
//                boolean present = batteryStatus.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
//                String technology = batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
//                int temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
//                int voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                map.put(BaseData.Battery.BR, DoubleUtils.mul(batteryLevel, 100d) + "%");
                map.put(BaseData.Battery.IS_CHARGING, plugState != 0);
//                map.put(BaseData.Battery.STATUS, batteryStatus(status));
//                map.put(BaseData.Battery.PLUG_STATE, batteryPlugged(plugState));
//                map.put(BaseData.Battery.HEALTH, batteryHealth(health));
//                map.put(BaseData.Battery.PRESENT, present + "");
//                map.put(BaseData.Battery.TECHNOLOGY, technology);
//                map.put(BaseData.Battery.TEMPERATURE, temperature / 10 + "℃");
//
//                if (voltage > 1000) {
//                    map.put(BaseData.Battery.VOLTAGE, voltage / 1000f + "V");
//                } else {
//                    map.put(BaseData.Battery.VOLTAGE, voltage + "V");
//                }
//                batteryBean.setPower(getBatteryCapacity(context));
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return map;
    }

    /**
     * 获取电池总电量
     *
     * @return
     */
    @SuppressLint("PrivateApi")
    public String getBatteryCapacity() {
        Object mPowerProfile;
        double batteryCapacity = 0;
        final String powerProfileClass = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile = Class.forName(powerProfileClass)
                    .getConstructor(Context.class)
                    .newInstance(ctx);

            batteryCapacity = (double) Class.forName(powerProfileClass)
                    .getMethod("getBatteryCapacity")
                    .invoke(mPowerProfile);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return batteryCapacity + "mAh";
    }

    private String batteryHealth(int health) {
        String healthBat = BaseData.UNKNOWN_PARAM;
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_COLD:
                healthBat = "cold";
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                healthBat = "dead";
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                healthBat = "good";
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                healthBat = "overVoltage";
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                healthBat = "overheat";
                break;
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                healthBat = "unknown";
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                healthBat = "unspecified";
                break;
            default:
                break;
        }
        return healthBat;
    }

    private String batteryStatus(int status) {
        String healthBat = BaseData.UNKNOWN_PARAM;
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                healthBat = "charging";
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                healthBat = "disCharging";
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                healthBat = "full";
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                healthBat = "notCharging";
                break;
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                healthBat = "unknown";
                break;
            default:
                break;
        }
        return healthBat;
    }

    private String batteryPlugged(int status) {
        String healthBat = BaseData.UNKNOWN_PARAM;
        switch (status) {
            case BatteryManager.BATTERY_PLUGGED_AC://充电器充电
                healthBat = "ac";
                break;
            case BatteryManager.BATTERY_PLUGGED_USB://USB充电
                healthBat = "usb";
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS://无线充电
                healthBat = "wireless";
                break;
            default:
                break;
        }
        return healthBat;
    }
}
