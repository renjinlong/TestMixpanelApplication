package com.suixingpay.mobilehardware;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.suixingpay.mobilehardware.base.BaseData;

import java.util.HashMap;
import java.util.Map;

/**
 * Author:   renjinlong
 * Email:    chinarenlong@foxmail.com
 * Date:     19/10/8
 * Description:获取手机声音信息
 */
public class MobileAduioManage {
    private static final String TAG = MobileAduioManage.class.getSimpleName();

    private static MobileAduioManage _inst;
    private Context ctx;

    public static MobileAduioManage getInstance(Context ctx) {
        if (_inst != null) {
            return _inst;
        } else {
            _inst = new MobileAduioManage(ctx);
            return _inst;
        }
    }

    private MobileAduioManage(Context ctx) {
        init(ctx);
    }

    private void init(Context ctx) {
        this.ctx = ctx;
    }


    /**
     * 获取当前声音相关信息
     *
     * @return
     */
    public Map<String, Object> getAudioInfo() {
        Map<String, Object> map = new HashMap<>();
        try {
            AudioManager mAudioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
            if (mAudioManager == null) {
                return map;
            }

            map.put(BaseData.Aduio.CURRENT_VOICE_CALL, mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL) + "");
            map.put(BaseData.Aduio.CURRENT_SYSTEM, mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) + "");
            map.put(BaseData.Aduio.IS_HEADSET_EXISTS, mAudioManager.isWiredHeadsetOn());

//            mobAudioBean.setMaxVoiceCall(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL) + "");
//            mobAudioBean.setCurrentVoiceCall(mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL) + "");
//
//            mobAudioBean.setMaxSystem(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM) + "");
//            mobAudioBean.setCurrentSystem(mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) + "");
//
//            mobAudioBean.setMaxRing(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING) + "");
//            mobAudioBean.setCurrentRing(mAudioManager.getStreamVolume(AudioManager.STREAM_RING) + "");
//
//            mobAudioBean.setMaxMusic(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) + "");
//            mobAudioBean.setCurrentMusic(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + "");
//
//            mobAudioBean.setMaxAlarm(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM) + "");
//            mobAudioBean.setCurrentAlarm(mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM) + "");
//
//            mobAudioBean.setMaxNotifications(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION) + "");
//            mobAudioBean.setCurrentNotifications(mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) + "");
//
//            mobAudioBean.setMaxDTMF(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_DTMF) + "");
//            mobAudioBean.setCurrentDTMF(mAudioManager.getStreamVolume(AudioManager.STREAM_DTMF) + "");

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                mobAudioBean.setMaxAccessibility(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ACCESSIBILITY) + "");
//                mobAudioBean.setCurrentAccessibility(mAudioManager.getStreamVolume(AudioManager.STREAM_ACCESSIBILITY) + "");
//            }
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                mobAudioBean.setMinDTMF(mAudioManager.getStreamMinVolume(AudioManager.STREAM_DTMF) + "");
//                mobAudioBean.setMinNotifications(mAudioManager.getStreamMinVolume(AudioManager.STREAM_NOTIFICATION) + "");
//                mobAudioBean.setMinAlarm(mAudioManager.getStreamMinVolume(AudioManager.STREAM_ALARM) + "");
//                mobAudioBean.setMinMusic(mAudioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC) + "");
//                mobAudioBean.setMinRing(mAudioManager.getStreamMinVolume(AudioManager.STREAM_RING) + "");
//                mobAudioBean.setMinSystem(mAudioManager.getStreamMinVolume(AudioManager.STREAM_SYSTEM) + "");
//                mobAudioBean.setMinVoiceCall(mAudioManager.getStreamMinVolume(AudioManager.STREAM_VOICE_CALL) + "");
//                mobAudioBean.setMinAccessibility(mAudioManager.getStreamMinVolume(AudioManager.STREAM_ACCESSIBILITY) + "");
//            }

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return map;
    }

}
