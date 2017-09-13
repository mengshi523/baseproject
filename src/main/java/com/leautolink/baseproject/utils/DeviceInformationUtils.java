package com.leautolink.baseproject.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.Calendar;

/**
 * File description
 * Created by @author${shimeng}  on @date14/3/8.
 */

public class DeviceInformationUtils {
    /**
     * 获取设备当前时间 ms
     *
     * @return 返回毫秒级时间
     */
    public static Long getCurrantTime() {
        Calendar c = Calendar.getInstance();
        Long currantTime = c.getTimeInMillis();
        return currantTime;
    }

    /**
     * 获取当前设备imei、imsi、设备名、芯片厂商、Mac地址组成的字符串
     *
     * @param context 当前context
     * @return 组合的md5值
     */
    public static String getDid(Context context) {
        String did = getDeviceId(context) + getIMSI(context) + getDeviceName() + getBrandName()
                + getMacAddress(context);
        return CommonUtils.MD5Encode(did);
    }

    /**
     * 获取当前设备唯一号
     *
     * @param context 当前context
     * @return 去处空格的字符串
     */
    public static String getDeviceId(Context context) {
        try {
            String deviceId = ((TelephonyManager) context.getSystemService(
                    Context.TELEPHONY_SERVICE)).getDeviceId();

            if (TextUtils.isEmpty(deviceId)) {
                return "";
            } else {
                return deviceId.replace(" ", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取当前设备IMSI号
     *
     * @param context 当前context
     * @return 去处空格的字符串
     */
    public static String getIMSI(Context context) {
        if (context == null) {
            return "";
        }

        String subscriberId = null;
        try {
            subscriberId = ((TelephonyManager) context.getSystemService(
                    Context.TELEPHONY_SERVICE)).getSubscriberId();

            if (null == subscriberId || subscriberId.length() <= 0) {
                subscriberId = generate_DeviceId(context);
            } else {
                subscriberId.replace(" ", "");
                if (TextUtils.isEmpty(subscriberId)) {
                    subscriberId = generate_DeviceId(context);
                }
            }

            return subscriberId;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return subscriberId;
        }
    }

    /**
     * 获取当前设备唯一号、设备名、芯片厂商、Mac地址组成的字符串，然后生成一个md5
     *
     * @return 生成一个md5值
     */
    public static String generate_DeviceId(Context context) {
        String str = getDeviceId(context) + getDeviceName() + getBrandName() + getMacAddress(
                context);

        return CommonUtils.MD5Encode(str);
    }

    /**
     * 获取当前设备名
     *
     * @return 返回设备名
     */
    public static String getDeviceName() {
        return CommonUtils.ensureStringValidate(android.os.Build.MODEL);
    }

    /**
     * 获取当前芯片厂商名
     *
     * @return 厂商名字符串
     */
    public static String getBrandName() {
        String brand = CommonUtils.ensureStringValidate(android.os.Build.BRAND);

        if (TextUtils.isEmpty(brand)) {
            return "";
        }

        return CommonUtils.getData(brand);
    }

    /**
     * 获取当前设备Mac地址
     *
     * @return 返回Mac地址
     */
    public static String getMacAddress(Context context) {
        WifiInfo wifiInfo = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
                .getConnectionInfo();

        if (wifiInfo != null) {
            return CommonUtils.ensureStringValidate(wifiInfo.getMacAddress());
        }

        return "";
    }
}
