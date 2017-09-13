package com.leautolink.baseproject.base.data;

/**
 * File description
 * Created by @author${shimeng}  on @date14/3/9.
 */

public class DownLoadSchema {
    public static final String DB_NAME = "camera_db";
    public static final int DB_VER = 1;
    public static final String TABLE_TASK = "download_task";

    public static final String _URL = "url";                // 下载任务的url
    public static final String _MD5 = "md5";                // md5
    public static final String _PATH = "filepath";          // 绝对保存路径
    public static final String _NAME = "filename";          // 文件名
    public static final String _VERSION = "version";        // 版本号
    public static final String _FLAG = "flag";              // 类型：强制类型和普通类型,可自行定义
    public static final String _TYPE = "type";              // 固件类型：记录仪固件、APP
    public static final String _STATE = "state";            // 下载状态
    public static final String _TOTALSIZE = "total";        // 文件总大小
    public static final String _CURRENT = "current";        // 已下载大小
    public static final String _APPVERSION = "app_version"; // app version
    public static final String _PACKAGENAME = "packagename"; // 如果下载的是apk，这个字段表示包名；

    public static final String createsql = "CREATE TABLE " + "IF NOT EXISTS   "+ TABLE_TASK +
            "(" + _URL + " varchar(256) primary key, " +
            _MD5 + " varchar(64), " +
            _VERSION + " varchar(20)," +
            _TYPE + " varchar(32), " +
            _FLAG + " integer, " +
            _STATE + " integer, " +
            _PATH + " varchar(256), " +
            _NAME + " varchar(128), " +
            _TOTALSIZE + " integer, " +
            _CURRENT + " integer, " +
            _PACKAGENAME + " varchar(128), " +
            _APPVERSION + " integer)";

    public final static String dropsql = "DROP TABLE IF EXISTS " + TABLE_TASK;
}
