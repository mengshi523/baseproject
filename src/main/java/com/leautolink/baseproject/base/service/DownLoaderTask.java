package com.leautolink.baseproject.base.service;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.leautolink.baseproject.R;
import com.leautolink.baseproject.base.manager.DownLoadManager;
import com.leautolink.baseproject.base.storage.DownLoadDb;
import com.leautolink.baseproject.utils.CommonUtils;
import com.leautolink.baseproject.utils.LogUtil;
import com.leautolink.baseproject.utils.SdCardUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


/**
 * File description
 * Created by @author${shimeng}  on @date14/3/9.
 */

public class DownLoaderTask {
    private final String TAG = "DownLoaderTask";

    public final static String OTA_PACKAGE_NAME = "AmbaSysFW.bin";
    public final static String OTA_PROPERTY_NAME = "system/build.prop";
    public final static String OTA_ZIP_NAME = "AmbaFW.zip";
    /*下载的OTA包路径*/
    public final static String OTA_PACKAGE_DIR = "/ota/";
    /*自带的OTA包路径*/
    public final static String OTA_NATIVE_DIR = "/native/";
    /*APK路径*/
    public final static String APK_DIR = "/";

    /*自带OTA包版本号*/
    public static final String OTA_VERSION = "V5201RCN01C161020B15521S";
    /*0817及更低的固件才支持强推*/
    public static final String OTA_THRESHOLD_VERSION = "V5201RCN01C160817B19051S";
    /**
     * APP内置OTA包的URL
     */
    public static final String OTA_DEFAULT_URL = "file:///force_upgrade_ota";


    private URL mUrl;
    private File mFile;
    private FileOutputStream mOutputStream;
    private Context mContext;
    private String mRoot;
    private String mName;
    private String mMD5="";
    private int mFlag;
    private String mVersion;
    private int mState;
    private int mPercentage;
    private long mTotalSize;
    private long mSize;
    private int mAPPVersion;
    private String mPackage;
    private String mType;
    private boolean mRunnig;
    HttpURLConnection connection = null;

    public DownLoaderTask(Context context) {
        super();
        this.mContext = context;
        mPercentage = 0;
        mTotalSize = 0;
        mSize = 0;
        mPackage = context.getPackageName();
        mRoot = SdCardUtils.getSDCardRootPath(mContext) + "/";
        mType = Type.FIRMWARE_OTA;
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            mAPPVersion = info.versionCode;
        } catch (Exception e) {

        }
        setFlag(Flag.FLAG_UPGRADE_NORMAL);
    }

    /**
     * 下载任务
     * @param context
     * @param url	下载任务的链接
     * @param MD5	MD5
     * @param verCode	目标版本号
     * @param flag		属性标记：强制类型和普通类型，也可自行扩展
     */
    public DownLoaderTask(Context context, String type, String url, String name, String MD5, String verCode, int flag){
        this(context);
        this.mMD5 = MD5;
        setUrl(url);
        setFlag(flag);
        setType(type);
        setVersion(verCode);
        setFile(name);
        if (flag == Flag.FLAG_UPGRADE_NATIVE) {
            setState(State.STATE_DOWNLOAD_SUCCESS);
        } else {
            setState(State.STATE_QUEUED);
        }
    }

    /**
     * 创建普通下载任务
     * @param context 	上下文
     * @param type		下载类型 @see DownLoaderTask.Type
     * @param url		下载链接
     * @param MD5		MD5校验值
     * @param verCode	目标版本号
     */
    public DownLoaderTask(Context context, String type, String url, String name, String MD5, String verCode){
        this(context, type, url, name, MD5, verCode, Flag.FLAG_UPGRADE_NORMAL);
    }

    /**
     * 下载任务状态重置
     */
    public void reset() {
        mPercentage = 0;
        mTotalSize = 0;
        mSize = 0;
        remove();
        mState = State.STATE_QUEUED;
    }
    public void execute() {
        if (mRunnig) {
            return;
        }
        mRunnig = true;
        mPercentage = 0;
        LogUtil.d(TAG, "--->onPreExecute: " + mRunnig);
        DownLoadManager.mDownloadThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                doInBackground();
            }
        });
    }

    public void cancel() {
        mRunnig = false;
        LogUtil.d(TAG, "--->cancel: " + mRunnig);
    }

    private long doInBackground() {

        if (mFlag == Flag.FLAG_UPGRADE_NATIVE && Type.FIRMWARE_OTA.equals(getType())) {
            return doUnzip();
        }
        LogUtil.d(TAG, "--->start downloading");
        long size = download();

        if (mState == State.STATE_DOWNLOAD_FAIL) {
            mPercentage = 0;
            mRunnig = false;
            return 0L;
        }
        // 如果服务端没有配置md5，则客户端下载完后不需要校验
        if (TextUtils.isEmpty(mMD5)) {
            setState(State.STATE_READY);
            mRunnig = false;
            return size;
        }
        LogUtil.d(TAG, "--->download finish, checking");
        if (!check()) {
			/*如果校验失败表示下载的包有问题，标记为下载失败*/
            setState(State.STATE_DOWNLOAD_FAIL);
            LogUtil.d(TAG, "--->md5 check fail");
            mRunnig = false;
            return 0L;
        }
		/*只有我们的ota包需要解压*/
        if (!Type.FIRMWARE_OTA.equals(mType)) {
            setState(State.STATE_READY);
            mRunnig = false;
            return size;
        }
        LogUtil.d(TAG, "--->start upzip");

        return doZipExtractorWork();
    }

    /**
     * 下载
     * @return
     */
    private long download(){

        long bytesCopied = 0;
        try {
            if (mUrl == null) {
                LogUtil.d(TAG, "--->url is emtpy");
            }
            connection = (HttpURLConnection) mUrl.openConnection();
            if (connection == null) {
                LogUtil.e(TAG, "--->open url fail: " + mUrl.toString());
                return 0L;
            }
			/*如果是断点下载，需要回滚到下载位置*/
            rollBackIfneed();
            LogUtil.d(TAG, "--->filesize= " + mFile.length() + ", mSize=" + mSize);
            int length = 0;
            if(connection!=null) {
                length = connection.getContentLength();
            }else{
                LogUtil.e(TAG, "--->connection: " + connection);
                return 0L;
            }

            LogUtil.d(TAG, "--->mTotalSize = " + mTotalSize);
            if (length <= 0) {
                setState(State.STATE_DOWNLOAD_FAIL);
                return 0L;
            }

            setTotalSize(length+mSize);
            if(mState >= State.STATE_DOWNLOAD_SUCCESS && mFile.exists() && mTotalSize == mFile.length()){
                LogUtil.d(TAG, "--->file " + mFile.getName() + " already exits!!");
				/*如果文件已经存在直接置状态为Download Success*/
                setState(State.STATE_DOWNLOAD_SUCCESS);
                connection.disconnect();
                return getTotalSize();
            }
            setState(State.STATE_DOWNLOADING);
            LogUtil.d(TAG, "--->file = " + mFile.getAbsolutePath());
            // mOutputStream = new FileOutputStream(mFile);
            RandomAccessFile out = new RandomAccessFile(mFile, "rw");
            out.seek(mSize);
            bytesCopied = copy(connection.getInputStream(), out);
            LogUtil.d(TAG, "--->byteCopied= " + bytesCopied);
            if(bytesCopied != mTotalSize && length != -1){
                LogUtil.e(TAG, "--->Download incomplete bytesCopied=" + bytesCopied + ", length" + length);
                setState(State.STATE_DOWNLOAD_FAIL);
            } else {
				/*下载结束，且长度一致表示下载成功*/
                mSize = bytesCopied;
                setState(State.STATE_DOWNLOAD_SUCCESS);
            }
            // mOutputStream.close();
        } catch (IOException e) {
            LogUtil.d(TAG, "--->" + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return bytesCopied;
    }

    /**
     * MD5校验
     * @return
     */
    private boolean check() {
        File f = new File(mRoot, OTA_ZIP_NAME);
        if (f != null && f.exists()) {
            String md5String = null;
            try {
                md5String = CommonUtils.getMd5ByFile(new File(mRoot+this.getFileName()));
            } catch (Exception e) {
                LogUtil.d(TAG, "--->check err: " + e.getMessage());
            }
            LogUtil.d(TAG, "--->md5: " + md5String + ",  mMd5=" + mMD5);
            return mMD5 == null || mMD5.equalsIgnoreCase("") || (md5String != null
                    && md5String.equalsIgnoreCase(mMD5));
        } else {
            LogUtil.d(TAG, "--->downloaded file not exist");
        }
        return false;
    }

    /**
     * 解压
     * @return
     */
    public long doZipExtractorWork() {
        long extractedSize = 0L;
        Enumeration<ZipEntry> entries;
        File input = new File(mRoot	+ this.getFileName());
        File output = new File(mRoot);
        if (!output.exists()) {
            if (!output.mkdirs()) {
                LogUtil.e(TAG, "Failed to make directories:" + output.getAbsolutePath());
            }
        }
        setState(State.STATE_UNZIPING);
        ZipFile zip = null;
        try {
            zip = new ZipFile(input);

            entries = (Enumeration<ZipEntry>) zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                File destination = new File(output, entry.getName());
                if (!destination.getParentFile().exists()) {
                    LogUtil.e(TAG, "--->make dir=" + destination.getParentFile().getAbsolutePath());
                    destination.getParentFile().mkdirs();
                }
                LogUtil.d(TAG, "--->unzip to: " + destination.getAbsolutePath());
                // FileOutputStream outStream = new FileOutputStream(destination);
                extractedSize += copy(zip.getInputStream(entry), new RandomAccessFile(destination, "rw"));
                // outStream.close();
            }
            input.delete();
            setState(State.STATE_READY);
        } catch (Exception e) {
            setState(State.STATE_UNZIP_FAIL);
            LogUtil.e(TAG, "--->unzip err: " + e.getMessage());
        } finally {
            try {
                if (null != zip) {
                    zip.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mRunnig = false;
        return extractedSize;
    }

    private long doUnzip() {
        long size=0;
        ZipEntry entry = null;
        ZipInputStream zip = null;
        setState(State.STATE_UNZIPING);
        InputStream input = mContext.getResources().openRawResource(R.raw.native_ota);
        try {
            zip = new ZipInputStream(input);
            while ((entry = zip.getNextEntry()) != null) {
                LogUtil.d(TAG, "--->entry: " + entry.getName());
                if (entry.isDirectory()) {
                    continue;
                }
                File destination = new File(mRoot, entry.getName());
                if (!destination.getParentFile().exists()) {
                    LogUtil.e(TAG, "--->make=" + destination.getParentFile().getAbsolutePath());
                    destination.getParentFile().mkdirs();
                }
                /*强制删除已存在的升级文件*/
                if (destination.exists()) {
                    destination.delete();
                    destination.createNewFile();
                }
                LogUtil.d(TAG, "--->copy to: " + destination);
                // size += copy(zip, new FileOutputStream(destination));
                size += copy(zip, new RandomAccessFile(destination, "rw"));
            }
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != zip) {
                    zip.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mRunnig = false;
        setState(State.STATE_READY);
        return size;
    }

    private long copy(InputStream input, RandomAccessFile output){
        byte[] buffer = new byte[1024*8];
        BufferedInputStream in = new BufferedInputStream(input, 1024*8);
        // BufferedOutputStream out  = new BufferedOutputStream(output, 1024*8);
        int n=0;
        try {
            while((n=in.read(buffer, 0, 1024*8))!=-1 && mRunnig){
                output.write(buffer, 0, n);
                mSize += n;
                if (mTotalSize > 0) {
                    mPercentage = (int) (mSize*100 / mTotalSize);
                }
                DownLoadDb db = DownLoadDb.getInstance(mContext);
                db.updateTask(this);
            }
            output.close();
        } catch (Exception e) {
            setState(State.STATE_DOWNLOAD_FAIL);
            LogUtil.d(TAG, "--->download err: " + e.getMessage());
        }finally{
            try {
                if(output != null) {
                    output.close();
                }
            } catch (IOException e) {
                LogUtil.d(TAG, "--->close err: " + e.getMessage());
            }
        }
        return mSize;
    }

    /**
     * 回滚，处理断点续传
     */
    private void rollBackIfneed() {
        try {
            if (mSize != mFile.length()) {
                LogUtil.d(TAG, "--->delete file: " + mFile.getAbsolutePath());
                mFile.delete();
                setSize(0);
            }
            if (mSize <= 0 || mSize > mFile.length()) {
                return;
            }
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent","NetFox");
            connection.setRequestProperty("range","bytes=" + mSize + "-" + mTotalSize);
        } catch( Exception e) {

        }
    }

    public void remove() {
        if (mFile != null) {
            mFile.delete();
        }
    }

    /**
     * 检查文件是否有效，文件是否存在&文件大小是否与totalsize一致
     * @return
     */
    public boolean checkFile() {
        if (mFile == null) {
            return false;
        }
        return mFile.exists() && mFile.length() == mTotalSize;
    }

    public boolean isFileExist() {
        if (Type.FIRMWARE_OTA.equals(mType)) {
            return isOtaExist();
        }
        if (mFile == null) {
            return false;
        }
        return mFile.exists();
    }

    private boolean isOtaExist() {
        File bin = new File(mRoot, OTA_PACKAGE_NAME);
        File build = new File(mRoot, OTA_PROPERTY_NAME);
        return bin.exists() && build.exists();
    }

    public String getFile() {
        return mFile.getAbsolutePath();
    }

    public String getFileName(){
        if (mFile == null) {
            return "";
        }
        LogUtil.d(TAG, "--->filename= " + mFile.getName());
        return mFile.getName();
    }

    public void setFile(String name) {
        if (TextUtils.isEmpty(name)) {
            return;
        }
        mName = name;
        mFile = new File(mRoot, name);
    }

    public String getUrl() {
        if (mUrl == null) {
            return null;
        } else if (mFlag == Flag.FLAG_UPGRADE_NATIVE) {
            return OTA_DEFAULT_URL;
        }
        return mUrl.toString();
    }

    public void setUrl(String url) {
        LogUtil.d(TAG, "--->url=" + url);
        if (url == null) {
            return;
        }
        try {
            mUrl = new URL(url);
        } catch (MalformedURLException e) {
            LogUtil.d(TAG, "--->create DownloadTask err: " + e.getMessage());
        }
        // Logger.d(TAG, "--->url=" + mUrl.toString());
    }

    public String getMD5() {
        return mMD5;
    }

    public void setMD5(String MD5) {
        mMD5 = MD5;
    }

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

    public String getRoot() {
        return mRoot;
    }

    public void setRoot(String root) {
        mRoot = root;
        File path = new File(mRoot);
        if (!path.exists()) {
            path.mkdirs();
        }
        if (mName != null) {
            mFile = new File(mRoot, mName);
        }
    }


    public int getState() {
        return mState;
    }

    public void setState(int state) {
        mState = state;
        if (state == State.STATE_DOWNLOAD_FAIL) {
            // mFile.delete();
        }
        DownLoadDb db = DownLoadDb.getInstance(mContext);
        db.updateTask(this);
    }

    public long getTotalSize() {
        return mTotalSize;
    }

    public void setTotalSize(long totalSize) {
        mTotalSize = totalSize;
        DownLoadDb db = DownLoadDb.getInstance(mContext);
        db.updateTask(this);
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long size) {
        mSize = size;
    }

    public int getPercentage() {
        if (mPercentage > 100) {
            mPercentage = 100;
        }
        return mPercentage;
    }

    public void setFlag(int flag) {
        mFlag = flag;
        if (Type.FIRMWARE_APP.equals(mType)) {
            return;
        }
        if (mFlag != Flag.FLAG_UPGRADE_NATIVE) {
            setRoot(SdCardUtils.getSDCardRootPath(mContext) + OTA_PACKAGE_DIR);
        } else {
            setRoot(SdCardUtils.getSDCardRootPath(mContext) + OTA_NATIVE_DIR);
			/*app内置的ota不需要下载，直接进入解压流程*/
            if (mState < State.STATE_DOWNLOAD_SUCCESS) {
                setState(State.STATE_DOWNLOAD_SUCCESS);
            }
        }
    }

    public int getFlag() {
        return mFlag;
    }

    public int getAPPVersion() {
        return mAPPVersion;
    }

    public void setAPPVersion(int version) {
        mAPPVersion = version;
    }

    public static String getOtaDir(Context ctx, int type) {
        if (type == Flag.FLAG_UPGRADE_NATIVE) {
            return SdCardUtils.getSDCardRootPath(ctx) + OTA_NATIVE_DIR;
        } else {
            return SdCardUtils.getSDCardRootPath(ctx) + OTA_PACKAGE_DIR;
        }
    }

    public void setType(String type) {
        mType = type;
        if (Type.FIRMWARE_APP.equals(mType)) {
            setRoot(SdCardUtils.getSDCardRootPath(mContext) + APK_DIR);
        }
    }

    public String getType() {
        return mType;
    }

    public String getPackage() {
        return mPackage;
    }

    public void setPackage(String aPackage) {
        mPackage = aPackage;
    }

    @Override
    public String toString() {
        return "url = " + getUrl() + "\n file=" + mRoot + "\n state: " + mState + " MD5: " + mMD5 + " version: " + mVersion;
    }

    public class State {
        public static final int STATE_QUEUED = 0;
        public static final int STATE_DOWNLOADING = 1;
        public static final int STATE_DOWNLOAD_FAIL = 2;
        public static final int STATE_DOWNLOAD_SUCCESS = 3;
        public static final int STATE_UNZIPING = 4;
        public static final int STATE_UNZIP_FAIL = 5;
        public static final int STATE_READY = 6;
    }

    public static class Flag {
        /**
         * 没有更新
         */
        public static final int FLAG_UPGRADE_NO = 0;
        /**
         * 普通升级包
         */
        public static final int FLAG_UPGRADE_NORMAL = 1;
        /**
         * 强制升级包
         */
        public static final int FLAG_UPGRADE_FORCE = 2;

        /**
         * Native强制升级，打包到apk不需要下载
         */
        public static final int FLAG_UPGRADE_NATIVE = 255;

    }

    public static class Type {
        /**
         * APP类型的下载任务
         */
        public static final String FIRMWARE_APP = "app";
        /**
         * OTA包下载任务
         */
        public static final String FIRMWARE_OTA = "ota";

    }

}

