package com.suixingpay.mobilehardware;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.suixingpay.mobilehardware.base.BaseData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Author:   renjinlong
 * Email:    chinarenlong@foxmail.com
 * Date:     19/10/8
 * Description:获取cpu信息
 */
public class MobileCpuManage {
    private static final String TAG = MobileCpuManage.class.getSimpleName();

    private static MobileCpuManage _inst;
    private Context ctx;

    public static synchronized MobileCpuManage getInstance(Context ctx) {
        if (_inst == null) {
            _inst = new MobileCpuManage(ctx);
            return _inst;
        }
        return _inst;
    }

    private MobileCpuManage(Context ctx) {
        init(ctx);
    }

    private void init(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * 获取cpu信息
     *
     * @return
     */
    public Map<String, String> getCpuInfo() {
        Map<String, String> map = new HashMap<>();
        try {
            map.put(BaseData.Cpu.CPU_NAME, getCpuName());
            map.put(BaseData.Cpu.CPU_FREQ, getCurCpuFreq() + "KHZ");
            map.put(BaseData.Cpu.CPU_MAX_FREQ, getMaxCpuFreq() + "KHZ");
            map.put(BaseData.Cpu.CPU_MIN_FREQ, getMinCpuFreq() + "KHZ");
            map.put(BaseData.Cpu.CPU_HARDWARE, Build.HARDWARE);
            map.put(BaseData.Cpu.CPU_CORES, getHeart() + "");
            map.put(BaseData.Cpu.CPU_TEMP, getCpuTemp() + "℃");
            map.put(BaseData.Cpu.CPU_ABI, putCpuAbi());
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
        return map;
    }

    /**
     * 获取cpu架构
     *
     * @return
     */
    public String putCpuAbi() {
        String[] abis;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            abis = Build.SUPPORTED_ABIS;
        } else {
            abis = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String abi : abis) {
            stringBuilder.append(abi);
            stringBuilder.append(",");
        }

        try {
            return stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
        return null;

    }

    /**
     * CPU温度
     *
     * @return
     */
    public String getCpuTemp() {
        String temp = null;
        try {
            FileReader fr = new FileReader("/sys/class/thermal/thermal_zone9/subsystem/thermal_zone9/temp");
            BufferedReader br = new BufferedReader(fr);
            temp = br.readLine();
            br.close();
        } catch (IOException e) {
            Log.i(TAG, e.toString());
        }
        return temp;
    }

    /**
     * cpu核心数
     *
     * @return
     */
    public int getHeart() {

        int cores;
        try {
            cores = new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER).length;
        } catch (SecurityException e) {
            cores = 0;
        }
        return cores;
    }

    private final FileFilter CPU_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return Pattern.matches("cpu[0-9]", pathname.getName());
        }
    };

    /**
     * 获取CPU的名字
     *
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    public String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
//            //noinspection StatementWithEmptyBody
//            for (String anArray : array) {
//            }
            return array[1];
        } catch (IOException e) {
            Log.i(TAG, e.toString());
        }
        return "";
    }

    /**
     * 获取当前cpu频率
     *
     * @return
     */
    public String getCurCpuFreq() {

        String result = "N/A";

        try {

            FileReader fr = new FileReader(

                    "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");

            BufferedReader br = new BufferedReader(fr);

            String text = br.readLine();

            result = text.trim();

        } catch (FileNotFoundException e) {

            Log.i(TAG, e.toString());

        } catch (IOException e) {
            Log.i(TAG, e.toString());

        }

        return result;

    }


    /**
     * 获取最大cpu频率
     *
     * @return
     */
    public String getMaxCpuFreq() {

        String result = "";

        ProcessBuilder cmd;

        try {

            String[] args = {"/system/bin/cat",

                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};

            cmd = new ProcessBuilder(args);

            Process process = cmd.start();

            InputStream in = process.getInputStream();

            byte[] re = new byte[24];

            while (in.read(re) != -1) {

                result = result + new String(re);

            }

            in.close();

        } catch (IOException ex) {

            Log.i(TAG, ex.toString());

            result = "N/A";

        }

        return result.trim();

    }


    /**
     * 获取最小cpu频率
     *
     * @return
     */
    public String getMinCpuFreq() {

        String result = "";

        ProcessBuilder cmd;

        try {

            String[] args = {"/system/bin/cat",

                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"};

            cmd = new ProcessBuilder(args);

            Process process = cmd.start();

            InputStream in = process.getInputStream();

            byte[] re = new byte[24];

            while (in.read(re) != -1) {

                result = result + new String(re);

            }

            in.close();

        } catch (IOException ex) {

            Log.i(TAG, ex.toString());

            result = "N/A";

        }

        return result.trim();

    }
}
