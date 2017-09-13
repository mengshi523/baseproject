package com.leautolink.baseproject.utils;

import android.text.TextUtils;

/**
 * File description
 * Created by @author${shimeng}  on @date14/3/9.
 */

public class FirmwareUtil {
    private static final String TAG = FirmwareUtil.class.getSimpleName();
    public static final String DEFAULT_VERSION = "V5201RCN01C011002B03211S";
    public static final long REQUEST_INTERVAL = 1000*60*60*8;
    public static final String LAST_REQUEST_TIME_FIELD = "lastCheckedTime";
    public static final String MAC_FIELD = "mac";
    public static final String VERSIONCODE_FIELD = "versionCode";

    public static boolean versionGreater(String versionDownloaded, String versionDevice) {
        if (TextUtils.isEmpty(versionDownloaded) || TextUtils.isEmpty(versionDevice)) {
            return false;
        }
        int cIndex = versionDownloaded.lastIndexOf('C');
        int bIndex = versionDownloaded.lastIndexOf('B');
        if (cIndex < 0 || bIndex < 0) {
            return false;
        }
        int majorDownloaded = Integer.parseInt(versionDownloaded.substring(cIndex + 1, bIndex));
        int minorDownloaded = Integer.parseInt(versionDownloaded.substring(bIndex + 1, bIndex + 6));
        cIndex = versionDevice.lastIndexOf('C');
        bIndex = versionDevice.lastIndexOf('B');
        if (cIndex < 0 || bIndex < 0) {
            return false;
        }
        int majorDevice = Integer.parseInt(versionDevice.substring(cIndex + 1, bIndex));
        int minorDevice = Integer.parseInt(versionDevice.substring(bIndex + 1, bIndex + 6));
        if (majorDownloaded < majorDevice) {
            return false;
        }
        return !(majorDownloaded == majorDevice && minorDownloaded <= minorDevice);
    }

}
