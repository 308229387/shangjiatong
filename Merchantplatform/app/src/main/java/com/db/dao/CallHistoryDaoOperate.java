package com.db.dao;

import android.content.Context;

import com.db.helper.DbManager;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by 58 on 2016/12/5.
 */

public class CallHistoryDaoOperate {
    /**
     * @desc 添加数据至数据库
     **/
    public static void insertData(Context context, CallHistory callHistory) {
        DbManager.getDaoSession(context).getCallHistoryDao().insert(callHistory);
    }

    /**
     * @desc 将数据实体通过事务添加至数据库
     **/
    public static void insertData(Context context, List<CallHistory> list) {
        if (null == list || list.size() <= 0) {
            return;
        }
        DbManager.getDaoSession(context).getCallHistoryDao().insertInTx(list);
    }

    /**
     * @desc 添加数据至数据库，如果存在，将原来的数据覆盖
     **/
    public static void saveData(Context context, CallHistory callHistory) {
        DbManager.getDaoSession(context).getCallHistoryDao().save(callHistory);
    }

    /**
     * @desc 查询所有数据
     **/
    public static List<CallHistory> queryAll(Context context) {
        QueryBuilder<CallHistory> builder = DbManager.getDaoSession(context).getCallHistoryDao().queryBuilder();
        return builder.build().list();
    }
}