package com.db.helper;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.db.dao.gen.CallDetailDao;
import com.db.dao.gen.CallListDao;
import com.db.dao.gen.DaoMaster;
import com.db.dao.gen.DaoSession;
import com.db.dao.gen.SystemNotificationDetialDao;
import com.utils.Constant;

/**
 * Created by 58 on 2016/12/1.
 */

public class DbManager extends DaoMaster.OpenHelper {

    private static DbManager mDbManager;
    private static DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;

    private DbManager(Context context, String name) {
        super(context, name);
    }

    public static DbManager getInstance(Context context) {
        if (mDbManager == null) {
            synchronized (DbManager.class) {
                if (mDbManager == null) {
                    mDbManager = new DbManager(context, Constant.DATABASE_CALL);
                    getDaoMaster(context);
                    getDaoSession(context);
                }
            }
        }
        return mDbManager;
    }

    /**
     * @desc 获取DaoMaster
     **/
    public static DaoMaster getDaoMaster(Context context) {
        if (null == mDaoMaster) {
            synchronized (DbManager.class) {
                if (null == mDaoMaster) {
                    mDaoMaster = new DaoMaster(getWritableDatabase(context));
                }
            }
        }
        return mDaoMaster;
    }

    /**
     * @desc 获取DaoSession
     **/
    public static DaoSession getDaoSession(Context context) {
        if (null == mDaoSession) {
            synchronized (DbManager.class) {
                mDaoSession = getDaoMaster(context).newSession();
            }
        }
        return mDaoSession;
    }

    /**
     * @desc 获取可写数据库
     **/
    public static SQLiteDatabase getWritableDatabase(Context context) {
        if (mDbManager == null) {
            getInstance(context);
        }
        return mDbManager.getWritableDatabase();
    }

    /**
     * @desc 获取可读数据库
     **/
    public static SQLiteDatabase getReadableDatabase(Context context) {
        if (null == mDbManager) {
            getInstance(context);
        }
        return mDbManager.getReadableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MigrationHelper.migrate(db, CallDetailDao.class, CallListDao.class, SystemNotificationDetialDao.class);
    }
}