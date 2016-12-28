package com.db.helper;

import android.content.Context;

import com.db.dao.CallDetail;
import com.db.dao.SystemNotificationDetial;
import com.db.dao.gen.CallDetailDao;
import com.db.dao.gen.SystemNotificationDetialDao;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;

/**
 * Created by SongYongmeng on 2016/12/21.
 */

public class SystemNotificationOperate {
    /**
     * @desc 添加数据至数据库，如果存在，将原来的数据覆盖
     **/
    public static void insertOrReplace(Context context, SystemNotificationDetial callDetail) {
        DbManager.getDaoSession(context).getSystemNotificationDetialDao().insertOrReplace(callDetail);
    }

    /**
     * @desc 按条件返回按时间排序的结果集
     **/
    public static ArrayList<SystemNotificationDetial> queryByCondition(Context context, WhereCondition whereCondition, WhereCondition... condMore) {
        QueryBuilder<SystemNotificationDetial> builder = DbManager.getDaoSession(context).getSystemNotificationDetialDao().queryBuilder();
        return (ArrayList<SystemNotificationDetial>) builder.where(whereCondition, condMore).orderDesc(SystemNotificationDetialDao.Properties.SortId).list();
    }

    /**
     * @desc 按条件返回按时间排序的结果集
     **/
    public static ArrayList<SystemNotificationDetial> queryAll(Context context) {
        QueryBuilder<SystemNotificationDetial> builder = DbManager.getDaoSession(context).getSystemNotificationDetialDao().queryBuilder();
        return (ArrayList<SystemNotificationDetial>) builder.orderDesc(SystemNotificationDetialDao.Properties.SortId).list();
    }


    /**
     * @desc 按条件返回按时间排序的结果集
     **/
    public static ArrayList<SystemNotificationDetial> checkReaded(Context context) {
        QueryBuilder<SystemNotificationDetial> builder = DbManager.getDaoSession(context).getSystemNotificationDetialDao().queryBuilder();
        return (ArrayList<SystemNotificationDetial>) builder.where(SystemNotificationDetialDao.Properties.IsReaded.eq(0)).list();
    }

    /**
     * @desc 全部标记为以读
     **/
    public static void updateData(Context context) {
        ArrayList<SystemNotificationDetial> unReaded = checkReaded(context);
        for (SystemNotificationDetial temp : unReaded) {
            temp.setIsReaded(1);
        }

        for (SystemNotificationDetial a : unReaded) {
            DbManager.getDaoSession(context).getSystemNotificationDetialDao().update(a);
        }
    }


}
