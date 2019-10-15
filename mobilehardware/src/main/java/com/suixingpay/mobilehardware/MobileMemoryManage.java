package com.suixingpay.mobilehardware;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.support.annotation.RequiresApi;
import android.text.format.Formatter;
import android.util.Log;

import com.suixingpay.mobilehardware.base.BaseData;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Author:   renjinlong
 * Email:    chinarenlong@foxmail.com
 * Date:     19/10/8
 * Description:获取RAM ROM 信息
 */
public class MobileMemoryManage {
    private static final String TAG = MobileMemoryManage.class.getSimpleName();

    private static MobileMemoryManage _inst;
    private Context ctx;

    public static synchronized MobileMemoryManage getInstance(Context ctx) {
        if (_inst == null) {
            _inst = new MobileMemoryManage(ctx);
            return _inst;
        }
        return _inst;
    }

    private MobileMemoryManage(Context ctx) {
        init(ctx);
    }

    private void init(Context ctx) {
        this.ctx = ctx;
    }


    /**
     * info
     *
     * @return
     */
    public Map<String, String> memoryInfo() {
        Map<String, String> map = new HashMap<>();
        try {
            map.put(BaseData.Memory.RAM_MEMORY, getTotalMemory());
            map.put(BaseData.Memory.RAM_AVAIL_MEMORY, getAvailMemory());
            map.put(BaseData.Memory.ROM_MEMORY_AVAILABLE, getRomSpace());
            map.put(BaseData.Memory.ROM_MEMORY_TOTAL, getRomSpaceTotal());
            map.put(BaseData.Memory.RAM_AVAIL_MEMORY, getAvailMemory());
            map.put(BaseData.Memory.SDCARD_MEMORY_AVAILABLE, getSdcardSize());
            map.put(BaseData.Memory.SDCARD_MEMORY_TOTAL, getSdcardSizeTotal());
            map.put(BaseData.Memory.SDCARD_REAL_MEMORY_TOTAL, getRealStorage());

        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
        return map;
    }

    /**
     * total
     *
     * @return
     */
    public String getTotalMemory() {
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader;

            localFileReader = new FileReader(str1);

            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();

            arrayOfString = str2.split("\\s+");
            //noinspection StatementWithEmptyBody
            for (String num : arrayOfString) {
            }

            initial_memory = Long.valueOf(arrayOfString[1]) * 1024;
            localBufferedReader.close();

        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
        return Formatter.formatFileSize(ctx, initial_memory);
    }

    /**
     * 获取android当前可用内存大小
     *
     * @return
     */
    public String getAvailMemory() {

        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        if (am != null) {
            am.getMemoryInfo(mi);
        }
        //mi.availMem; 当前系统的可用内存
        return Formatter.formatFileSize(ctx, mi.availMem);
    }

    /**
     * rom
     *
     * @return
     */
    public String getRomSpace() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(ctx, availableBlocks * blockSize);
    }

    public String getRomSpaceTotal() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(ctx, totalBlocks * blockSize);
    }

    /**
     * sd is null ==rom
     *
     * @return
     */
    public String getSdcardSize() {

        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(ctx, availableBlocks * blockSize);
    }

    public String getSdcardSizeTotal() {

        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockCount = stat.getBlockCount();
        long blockSize = stat.getBlockSize();
        return Formatter.formatFileSize(ctx, blockCount * blockSize);
    }

    public String getRealStorage() {
        long total = 0L;
        try {
            StorageManager storageManager = (StorageManager) ctx.getSystemService(Context.STORAGE_SERVICE);
            int version = Build.VERSION.SDK_INT;
            float unit = version >= Build.VERSION_CODES.O ? 1000 : 1024;
            if (version < Build.VERSION_CODES.M) {
                Method getVolumeList = StorageManager.class.getDeclaredMethod("getVolumeList");
                StorageVolume[] volumeList = (StorageVolume[]) getVolumeList.invoke(storageManager);
                if (volumeList != null) {
                    Method getPathFile = null;
                    for (StorageVolume volume : volumeList) {
                        if (getPathFile == null) {
                            getPathFile = volume.getClass().getDeclaredMethod("getPathFile");
                        }
                        File file = (File) getPathFile.invoke(volume);
                        total += file.getTotalSpace();
                    }
                }
            } else {
                @SuppressLint("PrivateApi") Method getVolumes = StorageManager.class.getDeclaredMethod("getVolumes");
                List<Object> getVolumeInfo = (List<Object>) getVolumes.invoke(storageManager);
                for (Object obj : getVolumeInfo) {
                    Field getType = obj.getClass().getField("type");
                    int type = getType.getInt(obj);
                    if (type == 1) {
                        long totalSize = 0L;
                        if (version >= Build.VERSION_CODES.O) {
                            Method getFsUuid = obj.getClass().getDeclaredMethod("getFsUuid");
                            String fsUuid = (String) getFsUuid.invoke(obj);
                            totalSize = getTotalSize(ctx, fsUuid);
                        } else if (version >= Build.VERSION_CODES.N_MR1) {
                            Method getPrimaryStorageSize = StorageManager.class.getMethod("getPrimaryStorageSize");
                            totalSize = (long) getPrimaryStorageSize.invoke(storageManager);
                        }
                        Method isMountedReadable = obj.getClass().getDeclaredMethod("isMountedReadable");
                        boolean readable = (boolean) isMountedReadable.invoke(obj);
                        if (readable) {
                            Method file = obj.getClass().getDeclaredMethod("getPath");
                            File f = (File) file.invoke(obj);
                            if (totalSize == 0) {
                                totalSize = f.getTotalSpace();
                            }
                            total += totalSize;
                        }
                    } else if (type == 0) {
                        Method isMountedReadable = obj.getClass().getDeclaredMethod("isMountedReadable");
                        boolean readable = (boolean) isMountedReadable.invoke(obj);
                        if (readable) {
                            Method file = obj.getClass().getDeclaredMethod("getPath");
                            File f = (File) file.invoke(obj);
                            total += f.getTotalSpace();
                        }
                    }
                }
            }
            return getUnit(total, unit);
        } catch (Exception ignore) {

        }
        return null;
    }

    private String[] units = {"B", "KB", "MB", "GB", "TB"};

    /**
     * 进制转换
     */
    private String getUnit(float size, float base) {
        int index = 0;
        while (size > base && index < 4) {
            size = size / base;
            index++;
        }
        return String.format(Locale.getDefault(), "%.2f %s ", size, units[index]);
    }

    /**
     * API 26 android O
     * 获取总共容量大小，包括系统大小
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private long getTotalSize(Context context, String fsUuid) {
        try {
            UUID id;
            if (fsUuid == null) {
                id = StorageManager.UUID_DEFAULT;
            } else {
                id = UUID.fromString(fsUuid);
            }
            StorageStatsManager stats = context.getSystemService(StorageStatsManager.class);
            return stats.getTotalBytes(id);
        } catch (NoSuchFieldError | NoClassDefFoundError | NullPointerException | IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

}
