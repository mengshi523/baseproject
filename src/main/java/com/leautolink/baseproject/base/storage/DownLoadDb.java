package com.leautolink.baseproject.base.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.leautolink.baseproject.base.data.BaseDbHelper;
import com.leautolink.baseproject.base.data.DownLoadSchema;
import com.leautolink.baseproject.base.service.DownLoaderTask;
import com.leautolink.baseproject.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;



/**
 * File description
 * Created by @author${shimeng}  on @date14/3/9.
 */

public class DownLoadDb {
    private static final String TAG = DownLoadDb.class.getSimpleName();

    private Context mContext;
    private BaseDbHelper mHelper = null;
    private SQLiteDatabase mDb;
    public static DownLoadDb mInstance = null;

    public static DownLoadDb getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DownLoadDb(ctx);
        }
        return mInstance;
    }

    public DownLoadDb(Context ctx) {
        mContext = ctx;
        mHelper = new BaseDbHelper(mContext);
        mDb = mHelper.getWritableDatabase();
    }

    public void add(DownLoaderTask task) {
        if (task == null) {
            return;
        }
        /*同类的下载任务只保留一个记录*/
        if (DownLoaderTask.Type.FIRMWARE_APP.equals(task.getType())) {
            mDb.delete(DownLoadSchema.TABLE_TASK, DownLoadSchema._TYPE + " = ?",
                    new String[]{String.valueOf(task.getType())});
        } else if (DownLoaderTask.Type.FIRMWARE_OTA.equals(task.getType())) {
            mDb.delete(DownLoadSchema.TABLE_TASK, DownLoadSchema._FLAG + " = ?",
                    new String[]{String.valueOf(task.getFlag())});
        }

        ContentValues values = new ContentValues();
        values.put(DownLoadSchema._URL, task.getUrl());
        values.put(DownLoadSchema._MD5, task.getMD5());
        values.put(DownLoadSchema._VERSION, task.getVersion());
        values.put(DownLoadSchema._TYPE, task.getType());
        values.put(DownLoadSchema._FLAG, task.getFlag());
        values.put(DownLoadSchema._STATE, task.getState());
        values.put(DownLoadSchema._PATH, task.getRoot());
        values.put(DownLoadSchema._NAME, task.getFileName());
        values.put(DownLoadSchema._TOTALSIZE, task.getTotalSize());
        values.put(DownLoadSchema._CURRENT, task.getSize());
        values.put(DownLoadSchema._PACKAGENAME, task.getPackage());
        values.put(DownLoadSchema._APPVERSION, task.getAPPVersion());
        LogUtil.d(TAG, "--->insert task: " + task.getUrl());
        long id = mDb.insert(DownLoadSchema.TABLE_TASK, null, values);
        if (id == -1) {
            LogUtil.d(TAG, "--->insert to DB fail");
        } else {
            LogUtil.d(TAG, "--->insert to DB ok");
        }
    }

    public void updateTask(DownLoaderTask task) {
        ContentValues values = new ContentValues();
        values.put(DownLoadSchema._URL, task.getUrl());
        values.put(DownLoadSchema._MD5, task.getMD5());
        values.put(DownLoadSchema._VERSION, task.getVersion());
        values.put(DownLoadSchema._TYPE, task.getType());
        values.put(DownLoadSchema._FLAG, task.getFlag());
        values.put(DownLoadSchema._STATE, task.getState());
        values.put(DownLoadSchema._PATH, task.getRoot());
        values.put(DownLoadSchema._NAME, task.getFileName());
        values.put(DownLoadSchema._TOTALSIZE, task.getTotalSize());
        values.put(DownLoadSchema._CURRENT, task.getSize());
        values.put(DownLoadSchema._PACKAGENAME, task.getPackage());
        values.put(DownLoadSchema._APPVERSION, task.getAPPVersion());
        mDb.update(DownLoadSchema.TABLE_TASK, values, DownLoadSchema._URL + " = ?",
                new String[]{task.getUrl()});
    }

    public List<DownLoaderTask> loadTasks() {
        Cursor cursor = mDb.query(DownLoadSchema.TABLE_TASK, null, null, null, null, null, null);

        if (cursor == null || cursor.getCount() <= 0) {
            LogUtil.d(TAG, "--->no task now");
            return null;
        }
        List<DownLoaderTask> list = new ArrayList<DownLoaderTask>();
        while (cursor.moveToNext()) {
            DownLoaderTask task = new DownLoaderTask(mContext);
            task.setUrl(cursor.getString(cursor.getColumnIndex(DownLoadSchema._URL)));
            task.setMD5(cursor.getString(cursor.getColumnIndex(DownLoadSchema._MD5)));
            task.setFlag(cursor.getInt(cursor.getColumnIndex(DownLoadSchema._FLAG)));
            task.setVersion(cursor.getString(cursor.getColumnIndex(DownLoadSchema._VERSION)));
            task.setType(cursor.getString(cursor.getColumnIndex(DownLoadSchema._TYPE)));
            task.setState(cursor.getInt(cursor.getColumnIndex(DownLoadSchema._STATE)));
            task.setRoot(cursor.getString(cursor.getColumnIndex(DownLoadSchema._PATH)));
            task.setFile(cursor.getString(cursor.getColumnIndex(DownLoadSchema._NAME)));
            task.setTotalSize(cursor.getLong(cursor.getColumnIndex(DownLoadSchema._TOTALSIZE)));
            task.setSize(cursor.getLong(cursor.getColumnIndex(DownLoadSchema._CURRENT)));
            task.setPackage(cursor.getString(cursor.getColumnIndex(DownLoadSchema._PACKAGENAME)));
            task.setAPPVersion(cursor.getInt(cursor.getColumnIndex(DownLoadSchema._APPVERSION)));
            list.add(task);
        }
        return list;
    }

    public void delete(DownLoaderTask task) {
        if (task == null) {
            return;
        }
        mDb.delete(DownLoadSchema.TABLE_TASK, DownLoadSchema._URL + " = ?",
                new String[]{task.getUrl()});
    }
}