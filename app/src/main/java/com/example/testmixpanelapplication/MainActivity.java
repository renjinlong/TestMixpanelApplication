package com.example.testmixpanelapplication;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.suixingpay.mobilehardware.MobileAduioManage;
import com.suixingpay.mobilehardware.MobileBatteryManage;
import com.suixingpay.mobilehardware.MobileCommonManage;
import com.suixingpay.mobilehardware.MobileCpuManage;
import com.suixingpay.mobilehardware.MobileDebugInfoManage;
import com.suixingpay.mobilehardware.MobileMemoryManage;
import com.suixingpay.mobilehardware.MobileNetWorkManage;
import com.suixingpay.mobilehardware.MobileSensorManage;
import com.suixingpay.mobilehardware.MobileSignalManage;
import com.sxfanalysis.android.mpmetrics.MPConfig;
import com.sxfanalysis.android.mpmetrics.MixpanelAPI;
import com.sxfanalysis.android.mpmetrics.SuperPropertyUpdate;
import com.sxfanalysis.android.util.MPConstants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    MixpanelAPI mixpanel;
    MobileSensorManage sensorManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mixpanel =
                MixpanelAPI.getDefaultInstance(getApplicationContext());

        Map<String, Object> map = new HashMap<>();

        try {
            sensorManage = MobileSensorManage.getInstance(getApplicationContext());
            MobileMemoryManage memoryManage = MobileMemoryManage.getInstance(getApplicationContext());
            MobileCpuManage cpuManage = MobileCpuManage.getInstance(getApplicationContext());
            MobileBatteryManage batteryManage = MobileBatteryManage.getInstance(getApplicationContext());
            MobileCommonManage commonManage = MobileCommonManage.getInstance(getApplicationContext());
            MobileAduioManage aduioManage = MobileAduioManage.getInstance(getApplicationContext());
            MobileDebugInfoManage debugInfoManage = MobileDebugInfoManage.getInstance(getApplicationContext());
            MobileNetWorkManage netWorkManage = MobileNetWorkManage.getInstance(getApplicationContext());
            MobileSignalManage signalManage = MobileSignalManage.getInstance(getApplicationContext());

//            int value = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
//
//            map.put("memory", memoryManage.getTotalMemory());
//            map.put("capacity", memoryManage.getRomSpaceTotal());
//            map.put("cpuCur", cpuManage.getCurCpuFreq());
//
//            Map<String, Object> batteryInfo = batteryManage.getBatteryInfo();
//            map.put("isCharging", batteryInfo.get(BaseData.Battery.IS_CHARGING));
//            map.put("batteryLevel", batteryInfo.get(BaseData.Battery.BR));
//
//            map.put("systemBrightness", value);
//            map.put("availMemory", memoryManage.getAvailMemory());
//            map.put("availCapacity", memoryManage.getRomSpace());
//
//            Map<String, Object> cellLocationInfo = commonManage.getCellLocationInfo();
//            map.put("cellLocInfo", cellLocationInfo.get(BaseData.CommonInfo.CELL_LOC_INFO));
//
//            Map<String, Object> audioInfo = aduioManage.getAudioInfo();
//            map.put("currentVoiceCall", audioInfo.get(BaseData.Aduio.CURRENT_VOICE_CALL));
//            map.put("currentSystem", audioInfo.get(BaseData.Aduio.CURRENT_SYSTEM));
//            map.put("isHeadsetExists", audioInfo.get(BaseData.Aduio.IS_HEADSET_EXISTS));
//
//            map.put("isRoot", commonManage.isRooted());
//            map.put("usbCharge", debugInfoManage.isOpenDebug());
//            map.put("isOpenGps", commonManage.isOPenGPS());
//            map.put("netType", netWorkManage.networkTypeALL());
//
//            Map<String, Object> wifiProxyInfo = signalManage.getWifiProxyInfo();
//            map.put("isAgent", wifiProxyInfo.get(BaseData.Signal.PROXY));
//            map.put("agentIp", wifiProxyInfo.get(BaseData.Signal.PROXY_ADDRESS));

            HashMap<String, String> stringStringHashMap = new HashMap<>();
            stringStringHashMap.put("phoneName", "ddd");

            map.put("baseAttr", stringStringHashMap);

        } catch (Exception e) {
            e.printStackTrace();
        }


        mixpanel.registerSuperPropertiesMap(map);
//        mixpanel.updateSuperProperties(new SuperPropertyUpdate() {
//            @Override
//            public JSONObject update(JSONObject oldValues) {
//                try {
//                    oldValues.put("uId", "12333");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return oldValues;
//            }
//        });

//        mixpanel.identify("userid111111111111");

//        Map<String, Object> peopleMap = new HashMap<>();
//        peopleMap.put("name", "renjl");
//        peopleMap.put("age", 18);
//
//        mixpanel.getPeople().setMap(peopleMap);

        Map<String, Object> track = new HashMap<>();
        track.put("netType", "WIFI");
//        track.put("LightValue", sensorManage.LightValue[0]);
//        track.put("gyroValue0", sensorManage.gyroValue[0]);
//        track.put("gyroValue1", sensorManage.gyroValue[1]);
//        track.put("gyroValue2", sensorManage.gyroValue[2]);
        mixpanel.trackMap("test", track);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                boolean testMode = MPConfig.getInstance(getApplicationContext()).getTestMode();
                System.out.println(testMode);

//                Map<String, Object> track = new HashMap<>();
//                track.put("s", 1);
//                track.put("LightValue", sensorManage.LightValue[0]);
//                track.put("gyroValue0", sensorManage.gyroValue[0]);
//                track.put("gyroValue1", sensorManage.gyroValue[1]);
//                track.put("gyroValue2", sensorManage.gyroValue[2]);
//                mixpanel.trackMap("test", track);
            }
        }, 2000);
    }


    @Override
    protected void onResume() {
        super.onResume();
        sensorManage.startSensor();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManage.stopSensor();
    }

    @Override
    protected void onDestroy() {
        mixpanel.flush();
        super.onDestroy();
    }
}
