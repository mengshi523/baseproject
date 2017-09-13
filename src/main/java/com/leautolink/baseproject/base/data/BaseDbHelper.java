package com.leautolink.baseproject.base.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.leautolink.baseproject.utils.LogUtil;


/**
 * File description
 * Created by @author${shimeng}  on @date14/3/9.
 */

public class BaseDbHelper extends SQLiteOpenHelper {
    private static final String TAG = BaseDbHelper.class.getSimpleName();
    private static final String DBNAME = "leautolink.db";
    private static final int VERSION = 1;


    public BaseDbHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DownLoadSchema.createsql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVer, int newVer) {
        LogUtil.d(TAG,
                "onUpgrade happen---> current version:" + oldVer + ", target version:" + newVer);
        LogUtil.i("oldVersion", oldVer + "");
        LogUtil.i("newVersion", newVer + "");
        if (oldVer == 0 && newVer == 0) {
            sqLiteDatabase.execSQL(DownLoadSchema.dropsql);
            onCreate(sqLiteDatabase);
        }

        if (oldVer == 1) {
            sqLiteDatabase.execSQL(DownLoadSchema.createsql);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
