package com.db.helper;

import android.content.Context;

import com.db.dao.CallDetail;
import com.db.dao.gen.CallDetailDao;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 58 on 2016/12/9.
 */

public class CallDetailDaoOperate {

    /**
     * @desc 添加数据至数据库，如果存在，将原来的数据覆盖
     **/
    public static void saveData(Context context, CallDetail callDetail) {
        DbManager.getDaoSession(context).getCallDetailDao().save(callDetail);
    }

    /**
     * @desc 从数据库中删除
     **/
    public static void deleteData(Context context, CallDetail callDetail) {
        DbManager.getDaoSession(context).getCallDetailDao().delete(callDetail);
    }

    /**
     * @desc 根据主键删除
     **/
    public static void deleteByKey(Context context, long key) {
        DbManager.getDaoSession(context).getCallDetailDao().deleteByKey(key);
    }

    /**
     * @desc 根据条件删除
     **/
    public static void deleteByCondition(Context context, WhereCondition condition) {
        DbManager.getDaoSession(context).getCallDetailDao().queryBuilder().where(condition).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * @desc 修改数据
     **/
    public static void updateData(Context context, CallDetail callDetail) {
        DbManager.getDaoSession(context).getCallDetailDao().update(callDetail);
    }

    /**
     * @desc 按条件返回结果集
     **/
    public static List<CallDetail> queryByCondition(Context context, WhereCondition whereCondition) {
        QueryBuilder<CallDetail> builder = DbManager.getDaoSession(context).getCallDetailDao().queryBuilder();
        return builder.where(whereCondition).build().list();
    }

    /**
     * @desc 查询限制条数的数据
     **/
    public static List<CallDetail> queryLimitData(Context context, int limit) {
        QueryBuilder<CallDetail> builder = DbManager.getDaoSession(context).getCallDetailDao().queryBuilder().limit(limit);
        return builder.build().list();
    }

    /**
     * @desc 查询最大BackTime
     **/
    public static long queryMaxBackTime(Context context) {
        QueryBuilder<CallDetail> builder = DbManager.getDaoSession(context).getCallDetailDao().queryBuilder();
        List<CallDetail> list = builder.orderDesc(CallDetailDao.Properties.BackTime).limit(1).list();
        if (list != null && list.size() > 0)
            return list.get(0).getBackTime();
        else
            return 0;
    }

    /**
     * @desc 查询最小BackTime
     **/
    public static long queryMinBackTime(Context context) {
        QueryBuilder<CallDetail> builder = DbManager.getDaoSession(context).getCallDetailDao().queryBuilder();
        List<CallDetail> list = builder.orderAsc(CallDetailDao.Properties.BackTime).limit(1).list();
        if (list != null && list.size() > 0)
            return list.get(0).getBackTime();
        else
            return 0;
    }
}