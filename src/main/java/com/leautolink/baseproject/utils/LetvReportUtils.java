package com.leautolink.baseproject.utils;

import android.content.Context;

import com.letv.tracker2.agnes.Agnes;
import com.letv.tracker2.agnes.App;
import com.letv.tracker2.agnes.Event;
import com.letv.tracker2.enums.EventType;
import com.letv.tracker2.msg.bean.Version;

import java.util.HashMap;
import java.util.Map;


/**
 *
 */
public class LetvReportUtils {
    private static Map<String, String> page_uuld = new HashMap<>();
    private static Context mContext;
    private static int mMajor = -1;
    private static int mMinor = -1;
    private static int mPatch = -1;

    public static final String APP_NAME = "Ecolink_android";//格式00:00:00
    public static final String UID = "uid";


    public static void init(Context context) {
        mContext = context;
        try {
            Agnes.getInstance().setContext(context);
        } catch (Exception e) {
            LogUtil.e("======", "LetvReportUtils init fail ：e=" + e);
        }
        Agnes.getInstance().getConfig().enableLog();
    }

    public static void reportMessage(boolean selected) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event;
        if (selected) {
            event = app.createEvent("select");
        } else {
            event = app.createEvent("unselect");
        }
        LogUtil.i("LetvReportUtils", ",设置");
        agnes.report(event);
    }

    public static void reportVoiceSearch(String searchTerm, String type) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent("voiceInput");
        event.addProp("searchTerm", searchTerm);
        event.addProp("searchType", type);
        agnes.report(event);
    }


    public static void reportUpgradeEvent(Context context, String version, boolean result,
            String failCause) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent(EventType.Upgrade);
        event.addProp("version", version);
        event.addProp("result", result + "");
        LogUtil.i("LetvReportUtils", ",个人中心:version=" + version);
        agnes.report(event);
    }

    public static void reportConnectStart(String phone_brand, String phone_brand_model,
            String phone_os, String phone_os_version
            , String province, String city, String OBU_screen_width, String OBU_screen_height,
            String OBU_os, String OBU_os_version
            , String OBU_id, String OBU_brand) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent("connect_start");
        event.addProp("phone_brand", phone_brand);
        event.addProp("phone_brand_model", phone_brand_model);
        event.addProp("phone_os", phone_os);
        event.addProp("phone_os_version", phone_os_version);
        event.addProp("province", province);
        event.addProp("city", city);
        event.addProp("OBU_screen_width", OBU_screen_width);
        event.addProp("OBU_screen_height", OBU_screen_height);
        event.addProp("OBU_os", OBU_os);
        event.addProp("OBU_os_version", OBU_os_version);
//        event.addProp("phone_id", phone_id);
//        event.addProp("ip", ip);
        event.addProp("OBU_id", OBU_id);
        event.addProp("OBU_brand", OBU_brand);
//        event.addProp("OBU_MAC", OBU_MAC);
        agnes.report(event);
    }

    public static void reportConnectEnd() {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent("connect_end");
        agnes.report(event);
    }


    public static void reportConnectStartGps(String longitude, String latitude) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent("connect_start_gps");
        event.addProp("longitude", longitude);
        event.addProp("latitude", latitude);
        agnes.report(event);
    }

    public static void reportConnectEndGps(String longitude, String latitude) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent("connect_end_gps");
        event.addProp("longitude", longitude);
        event.addProp("latitude", latitude);
        agnes.report(event);
    }

    public static void reportClick(String page_id) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent(EventType.Click);
        event.addProp("page_id", page_id);
        agnes.report(event);
    }

    public static void reportDownloadEvent(Context context, String version, String channleId) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event = app.createEvent(EventType.Download);
        event.addProp("version", version);
        event.addProp("channleId", channleId);
        agnes.report(event);
    }

    public static void reportLoginEvent(String userId, String action) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event event;
        if (action.equalsIgnoreCase("login")) {
            event = app.createEvent(action);
            event.addProp("sourceName", "button");
            event.addProp(UID, userId);
            LogUtil.i("LetvReportUtils", ",登陆页");
            agnes.report(event);
        } else if (action.equalsIgnoreCase("register")) {
            event = app.createEvent(action);
            event.addProp("sourceName", "tabbar");
            event.addProp(UID, userId);
            LogUtil.i("LetvReportUtils", ",登陆页:userId=" + userId);
            agnes.report(event);
        }
    }

    public static int getMajorVer() {
        if (mMajor >= 0) {
            return mMajor;
        }
        try {
            String versionName = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0).versionName;
            String[] digits = versionName.split("\\.");
            if (digits.length >= 1) {
                mMajor = Integer.parseInt(digits[0]);
            } else {
                mMajor = 0;
            }
        } catch (Exception e) {
            mMajor = 0;
            e.printStackTrace();
        }
        return mMajor;
    }

    public static int getMinorVer() {
        if (mMinor >= 0) {
            return mMinor;
        }
        try {
            String versionName = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0).versionName;
            String[] digits = versionName.split("\\.");
            if (digits.length >= 2) {
                mMinor = Integer.parseInt(digits[1]);
            } else {
                mMinor = 0;
            }
        } catch (Exception e) {
            mMinor = 0;
            e.printStackTrace();
        }
        return mMinor;
    }

    public static int getPatchVer() {
        if (mPatch >= 0) {
            return mPatch;
        }
        try {
            String versionName = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0).versionName;
            String[] digits = versionName.split("\\.");
            if (digits.length >= 3) {
                mPatch = Integer.parseInt(digits[2]);
            } else {
                mPatch = 0;
            }
        } catch (Exception e) {
            mPatch = 0;
            e.printStackTrace();
        }
        return mPatch;
    }

    private static void setAppVersion(App app) {
        Version appVersion = app.getVersion();
        appVersion.setVersion(getMajorVer(), getMinorVer(), getPatchVer());
    }

    public static void recordAppStart() {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event e = app.createEvent("run");
        Agnes.getInstance().report(e);
    }

    public static void recordActivityStart(String activityId) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Long currantTime = DeviceInformationUtils.getCurrantTime();
        String page_uuld_str = activityId + "_" + currantTime;
        page_uuld.put(activityId, page_uuld_str);
        Event e = app.createEvent(EventType.acStart);
        e.addProp("activityId", activityId);
        e.addProp("page_uuid", page_uuld_str);
        Agnes.getInstance().report(e);
    }

    Long currantTime = DeviceInformationUtils.getCurrantTime();

    public static void recordActivityEnd(String activityId) {
        Agnes agnes = Agnes.getInstance();
        App app = agnes.getApp(APP_NAME);
        setAppVersion(app);
        Event e = app.createEvent(EventType.acEnd);
        e.addProp("activityId", activityId);
        if (page_uuld.get(activityId) != null) {
            e.addProp("page_uuid", page_uuld.get(activityId));
            Agnes.getInstance().report(e);
            page_uuld.remove(activityId);
        }
    }


}