package com.db.helper;

import android.content.Context;

import com.db.dao.CallDetail;
import com.db.dao.gen.CallDetailDao;
import com.utils.UserUtils;

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
    public static void insertOrReplace(Context context, CallDetail callDetail) {
        DbManager.getDaoSession(context).getCallDetailDao().insertOrReplace(callDetail);
    }

    /**
     * @desc 根据条件删除
     **/
    public static void deleteByCondition(Context context, WhereCondition condition, WhereCondition... condMore) {
        DbManager.getDaoSession(context).getCallDetailDao().queryBuilder().where(condition, condMore).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * @desc 修改数据
     **/
    public static void updateData(Context context, CallDetail callDetail) {
        DbManager.getDaoSession(context).getCallDetailDao().update(callDetail);
    }

    /**
     * @desc 按条件返回按时间排序的结果集
     **/
    public static ArrayList<CallDetail> queryByCondition(Context context, WhereCondition whereCondition, WhereCondition... condMore) {
        QueryBuilder<CallDetail> builder = DbManager.getDaoSession(context).getCallDetailDao().queryBuilder();
        return (ArrayList<CallDetail>) builder.where(whereCondition, condMore).orderDesc(CallDetailDao.Properties.CallTime).list();
    }

    /**
     * @desc 查询最大BackTime
     **/
    public static long queryMaxBackTime(Context context) {
        WhereCondition conditionUserId = CallDetailDao.Properties.UserId.eq(UserUtils.getUserId());
        QueryBuilder<CallDetail> builder = DbManager.getDaoSession(context).getCallDetailDao().queryBuilder();
        List<CallDetail> list = builder.where(new WhereCondition.StringCondition("IS_DELETED='0'"), conditionUserId).orderDesc(CallDetailDao.Properties.BackTime).limit(1).list();
        if (list != null && list.size() > 0)
            return list.get(0).getBackTime();
        else
            return 0;
    }

    /**
     * @desc 查询最小BackTime
     **/
    public static long queryMinBackTime(Context context) {
        WhereCondition conditionUserId = CallDetailDao.Properties.UserId.eq(UserUtils.getUserId());
        QueryBuilder<CallDetail> builder = DbManager.getDaoSession(context).getCallDetailDao().queryBuilder();
        List<CallDetail> list = builder.where(new WhereCondition.StringCondition("IS_DELETED='0'"), conditionUserId).orderAsc(CallDetailDao.Properties.BackTime).limit(1).list();
        if (list != null && list.size() > 0)
            return list.get(0).getBackTime();
        else
            return 0;
    }
}