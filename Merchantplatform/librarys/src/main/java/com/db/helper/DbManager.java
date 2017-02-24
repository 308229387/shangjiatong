package com.db.helper;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.db.dao.gen.CallDetailDao;
import com.db.dao.gen.CallListDao;
import com.db.dao.gen.DaoMaster;
import com.db.dao.gen.DaoSession;
import com.db.dao.gen.IMMessageEntityDao;
import com.db.dao.gen.SystemNotificationDetialDao;

/**
 * Created by 58 on 2016/12/1.
 */

public class DbManager extends DaoMaster.OpenHelper {

    private static DbManager mDbManager;
    private static DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;
    private static Context mContext;


    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    /**
     * 储存通话信息的数据库
     */
    public static final String DATABASE_CALL = "merchantplatform.db";

    private DbManager(Context context, String name) {
        super(context, name);
    }

    public static void init(Context context) {
        mContext = context;
    }

    public static DbManager getInstance() {
        if (mDbManager == null) {
            synchronized (DbManager.class) {
                if (mDbManager == null) {
                    mDbManager = new DbManager(mContext, DATABASE_CALL);
                    mDaoMaster = new DaoMaster(mDbManager.getWritableDatabase());
                    mDaoSession = mDaoMaster.newSession();
                }
            }
        }
        return mDbManager;
    }

    /**
     * 覆盖原有的数据库版本更新方法，兼容老数据，每次添加表都要修改该表
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MigrationHelper.migrate(db, CallDetailDao.class, CallListDao.class, SystemNotificationDetialDao.class, IMMessageEntityDao.class);
    }
}