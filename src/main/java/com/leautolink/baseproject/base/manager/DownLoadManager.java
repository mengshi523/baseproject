package com.leautolink.baseproject.base.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.leautolink.baseproject.base.modelInterface.DownLoadModel;
import com.leautolink.baseproject.base.service.DownLoaderTask;
import com.leautolink.baseproject.base.storage.DownLoadDb;
import com.leautolink.baseproject.utils.CommonUtils;
import com.leautolink.baseproject.utils.FirmwareUtil;
import com.leautolink.baseproject.utils.LogUtil;

import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * File description
 * Created by @author${shimeng}  on @date14/3/9.
 */

public class DownLoadManager implements DownLoadModel {

    private final static String TAG = DownLoadManager.class.getSimpleName();

    public Context mContext;
    private List<DownLoaderTask> mTasks = null;
    public static DownLoadManager mInstance = null;
    public static ExecutorService mDownloadThreadPool = Executors.newFixedThreadPool(5);

    @Override
    public void init(Context context) {
        getInstance(context);
    }

    public static DownLoadManager getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DownLoadManager(ctx);
        }
        return mInstance;
    }

    public DownLoadManager(Context ctx) {
        mContext = ctx;
        loadAll();
        if (mTasks == null) {
            mTasks = new ArrayList<>();
        }
    }

    public synchronized void add(DownLoaderTask task) {
        if (mTasks == null) {
            mTasks = new ArrayList<>();
        }
        /*同一个任务不添加两次*/
        if (task == null || mTasks.contains(task)) {
            LogUtil.d(TAG, "--->this task already exist");
            return;
        }
        if (mTasks.size() > 0) {
            for (DownLoaderTask t : mTasks) {
                String url = t.getUrl();
                if (url != null && url.equals(task.getUrl())) {
                    LogUtil.d(TAG, "--->this task already exist , state: " + t.getState());
                    if (t.getState() == DownLoaderTask.State.STATE_DOWNLOAD_FAIL
                            || t.getState() == DownLoaderTask.State.STATE_UNZIP_FAIL
                            || !t.isFileExist()) {
                        task.execute();
                    }
                    return;
                }
            }
        }
        mTasks.add(task);
        DownLoadDb db = DownLoadDb.getInstance(mContext);
        db.add(task);
        task.execute();
    }

    @Override
    public void addTask(String firmware, String url, String name, String md5, String version) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(version)) {
            return;
        }
        List<DownLoaderTask> tasks = getTaskByType(firmware);
        DownLoaderTask task = null;
        for (DownLoaderTask t : tasks) {
            if (url.equals(t.getUrl())) {
                task = t;
                break;
            }
        }
        if (task == null) {
            task = new DownLoaderTask(mContext, firmware, url, name, md5, version);
            add(task);
        } else {
        }
    }

    @Override
    public void removeTask(DownLoaderTask task) {
        if (task == null || CommonUtils.isEmpty(task.getUrl())) {
            return;
        }
        DownLoadDb db = DownLoadDb.getInstance(mContext);
        db.delete(task);
        task.remove();
        mTasks.remove(task);
    }

    @Override
    public void cancelTask(DownLoaderTask task) {
        if (task == null) {
            return;
        }
        LogUtil.d(TAG, "--->cancel task: " + task.hashCode());
        task.cancel();
    }

    public void loadAll() {
        LogUtil.d(TAG, "--->loadAll");
        DownLoadDb db = DownLoadDb.getInstance(mContext);
        mTasks = db.loadTasks();
        if (mTasks == null) {
            return;
        }
        for (DownLoaderTask task : mTasks) {
            LogUtil.d(TAG, "--->task: " + task.toString());
            // 删除已经安装的apk文件
            if (DownLoaderTask.Type.FIRMWARE_APP.equals(task.getType())
                    && mContext.getPackageName().equals(task.getPackage())) {
                try {
                    PackageInfo info = mContext.getPackageManager().getPackageInfo(
                            mContext.getPackageName(),
                            PackageManager.GET_CONFIGURATIONS);
                    if (info.versionCode >= Long.parseLong(task.getVersion())) {
                        removeTask(task);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    LogUtil.d(TAG, "--->not found package:" + mContext.getPackageName());
                }
            }
        }

    }

    public void decompressNative() {
        LogUtil.d(TAG, "--->decompressNative");
        DownLoaderTask ota = null;
        if (mTasks != null) {
            for (DownLoaderTask task : mTasks) {
                LogUtil.d(TAG, "--->task: " + task.toString());
                if (DownLoaderTask.Type.FIRMWARE_OTA.equals(task.getType())
                        && task.getFlag() == DownLoaderTask.Flag.FLAG_UPGRADE_NATIVE) {
                    ota = task;
                }
            }
        }

        try {
            /*对比app version*/
            PackageInfo info = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            if (ota == null || ota.getAPPVersion() < info.versionCode) {
                LogUtil.d(TAG, "--->create native task");
                removeTask(ota);
                DownLoaderTask task = new DownLoaderTask(mContext, DownLoaderTask.Type.FIRMWARE_OTA,
                        DownLoaderTask.OTA_DEFAULT_URL, "", "", DownLoaderTask.OTA_VERSION,
                        DownLoaderTask.Flag.FLAG_UPGRADE_NATIVE);
                add(task);
            }
        } catch (Exception e) {
            LogUtil.d(TAG, "--->e: " + e.getMessage());
        }
    }

    public void startTask(DownLoaderTask task) {
        if (task.getState() == DownLoaderTask.State.STATE_READY && !task.checkFile()) {
            task.reset();
            task.execute();
        } else if ((task.getState() != DownLoaderTask.State.STATE_READY)
                && (task.getState() != DownLoaderTask.State.STATE_DOWNLOADING)) {
            task.execute();
        }
    }

    /**
     * 只启动Native OTA的解压工作,后续继续完善，把下载任务处理
     */
    public void startAll() {
        LogUtil.d(TAG, "--->startAll");
        decompressNative();
        if (mTasks == null) {
            return;
        }
        for (DownLoaderTask task : mTasks) {
            if (DownLoaderTask.Type.FIRMWARE_OTA.equals(task.getType())
                    && DownLoaderTask.Flag.FLAG_UPGRADE_NATIVE == task.getFlag()) {
                continue;
            }
            if (task.getState() == DownLoaderTask.State.STATE_DOWNLOAD_FAIL ||
                    task.getState() == DownLoaderTask.State.STATE_UNZIP_FAIL) {
                task.execute();
            } else if (task.getTotalSize() != task.getSize() || !task.isFileExist()) {
                task.execute();
            }
        }
    }

    public List<DownLoaderTask> getAllTask() {
        return mTasks;
    }

    public DownLoaderTask getTask(int type) {
        for (DownLoaderTask task : mTasks) {
            if (task.getFlag() == type) {
                return task;
            }
        }
        return null;
    }

    public String getMaxVersion() {
        String verCode = null;
        for (DownLoaderTask task : mTasks) {
            if (verCode == null) {
                verCode = task.getVersion();
            } else if (FirmwareUtil.versionGreater(task.getVersion(), verCode)) {
                verCode = task.getVersion();
            }
        }
        return verCode;
    }

    public List<DownLoaderTask> getTaskByType(String type) {
        List<DownLoaderTask> tasks = new ArrayList<DownLoaderTask>();

        if (type == null) {
            return tasks;
        }
        for (DownLoaderTask task : mTasks) {
            if (type.equals(task.getType())) {
                tasks.add(task);
            }
        }
        return tasks;
    }
}
