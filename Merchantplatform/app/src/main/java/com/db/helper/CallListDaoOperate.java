package com.db.helper;

import android.content.Context;

import com.db.dao.CallList;
import com.db.dao.gen.CallListDao;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;

/**
 * Created by 58 on 2016/12/10.
 */

public class CallListDaoOperate {

    /**
     * @desc 添加数据至数据库
     **/
    public static void insert(Context context, CallList callList) {
        DbManager.getDaoSession(context).getCallListDao().insert(callList);
    }

    /**
     * @desc 从数据库中删除
     **/
    public static void deleteData(Context context, CallList callList) {
        DbManager.getDaoSession(context).getCallListDao().delete(callList);
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
    public static ArrayList<CallList> queryByCondition(Context context, WhereCondition whereCondition, WhereCondition... condMore) {
        QueryBuilder<CallList> builder = DbManager.getDaoSession(context).getCallListDao().queryBuilder();
        return (ArrayList<CallList>) builder.where(whereCondition, condMore).list();
    }

    /**
     * @desc 按条件返回最新的限制条数结果集
     **/
    public static ArrayList<CallList> queryLimitDataByCondition(Context context, int limit, WhereCondition whereCondition, WhereCondition... condMore) {
        QueryBuilder<CallList> builder = DbManager.getDaoSession(context).getCallListDao().queryBuilder();
        return (ArrayList<CallList>) builder.where(whereCondition, condMore).orderDesc(CallListDao.Properties.CallTime).limit(limit).list();
    }

    /**
     * @desc 按条件返回最新的限制条数结果集（带偏移量）
     **/
    public static ArrayList<CallList> queryOffsetLimitDataByCondition(Context context, int offset, int limit, WhereCondition whereCondition, WhereCondition... condMore) {
        QueryBuilder<CallList> builder = DbManager.getDaoSession(context).getCallListDao().queryBuilder();
        return (ArrayList<CallList>) builder.where(whereCondition, condMore).orderDesc(CallListDao.Properties.CallTime).offset(offset).limit(limit).list();
    }
}