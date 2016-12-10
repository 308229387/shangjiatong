package com.db.helper;

import android.content.Context;

import com.db.dao.CallList;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

/**
 * Created by 58 on 2016/12/10.
 */

public class CallListDaoOperate {
    /**
     * @desc 添加数据至数据库，如果存在，将原来的数据覆盖
     **/
    public static void saveData(Context context, CallList callList) {
        DbManager.getDaoSession(context).getCallListDao().save(callList);
    }

    /**
     * @desc 从数据库中删除
     **/
    public static void deleteData(Context context, CallList callList) {
        DbManager.getDaoSession(context).getCallListDao().delete(callList);
    }

    /**
     * @desc 根据主键删除
     **/
    public static void deleteByKey(Context context, long key) {
        DbManager.getDaoSession(context).getCallListDao().deleteByKey(key);
    }

    /**
     * @desc 根据条件删除
     **/
    public static void deleteByCondition(Context context, WhereCondition condition) {
        DbManager.getDaoSession(context).getCallListDao().queryBuilder().where(condition).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * @desc 修改数据
     **/
    public static void updateData(Context context, CallList callList) {
        DbManager.getDaoSession(context).getCallListDao().update(callList);
    }

    /**
     * @desc 按条件返回结果集
     **/
    public static List<CallList> queryByCondition(Context context, WhereCondition whereCondition) {
        QueryBuilder<CallList> builder = DbManager.getDaoSession(context).getCallListDao().queryBuilder();
        return builder.where(whereCondition).build().list();
    }

    /**
     * @desc 查询限制条数的数据
     **/
    public static List<CallList> queryLimitData(Context context, int limit) {
        QueryBuilder<CallList> builder = DbManager.getDaoSession(context).getCallListDao().queryBuilder().limit(limit);
        return builder.build().list();
    }
}