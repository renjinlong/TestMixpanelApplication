package com.suixingpay.mobilehardware;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Author:   renjinlong
 * Email:    chinarenlong@foxmail.com
 * Date:     19/10/8
 * Description:获取传感器数据信息
 */
public class MobileSensorManage {
    private static final String TAG = MobileSensorManage.class.getSimpleName();

    /**
     * 陀螺仪数据
     */
    public Float[] gyroValue = new Float[3];

    /**
     * 光强度
     */
    public Float[] LightValue = new Float[1];

    private SensorManager mSensorManager;
    /* 传感器等级
     *
     *             SensorManager.SENSOR_DELAY_FASTEST = 0：对应0微秒的更新间隔，最快，1微秒 = 1 % 1000000秒
     *              SensorManager.SENSOR_DELAY_GAME = 1：对应20000微秒的更新间隔，游戏中常用
     *              SensorManager.SENSOR_DELAY_UI = 2：对应60000微秒的更新间隔
     *              SensorManager.SENSOR_DELAY_NORMAL = 3：对应200000微秒的更新
     */

    private int mRate = 1 * 1000 * 1000;

    private static MobileSensorManage _inst;
    private Context ctx;

    public static synchronized MobileSensorManage getInstance(Context ctx) {
        if (_inst == null) {
            _inst = new MobileSensorManage(ctx);
            return _inst;
        }
        return _inst;
    }


    private MobileSensorManage(Context ctx) {
        init(ctx);
    }

    /**
     * 初始化
     */
    private void init(Context ctx) {
        this.ctx = ctx;
        mSensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
    }

    /**
     * 开始光传感器
     */
    public void startSensor() {
        if (mSensorManager != null) {
            Sensor lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            Sensor gyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            if (lightSensor != null) {
                mSensorManager.registerListener(sensorEventListener, lightSensor, mRate);
            }

            if (gyroSensor != null) {
                mSensorManager.registerListener(sensorEventListener, gyroSensor, mRate);
            }
        }
    }

    public void stopSensor() {
        if (mSensorManager != null && sensorEventListener != null) {
            mSensorManager.unregisterListener(sensorEventListener);
        }
    }

    /**
     * 传感器监听
     */
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            int type = event.sensor.getType();
            float[] values = event.values;
            switch (type) {
                case Sensor.TYPE_LIGHT:
                    //当前光的强度为
                    LightValue[0] = values[0];
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    //绕X轴旋转的角速度：
                    gyroValue[0] = values[0];
                    //绕Y轴旋转的角速度：
                    gyroValue[1] = values[1];
                    //绕Z轴旋转的角速度：
                    gyroValue[2] = values[2];
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


}
