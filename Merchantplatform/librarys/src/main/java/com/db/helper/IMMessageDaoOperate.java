package com.db.helper;

import android.content.Context;

import com.Utils.UserUtils;
import com.db.dao.IMMessageEntity;
import com.db.dao.gen.CallDetailDao;
import com.db.dao.gen.IMMessageEntityDao;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linyueyang on 17/2/17.
 */

public class IMMessageDaoOperate {

    /**
     * @desc 添加数据至数据库，如果存在，将原来的数据覆盖
     **/
    public static void insertOrReplace(IMMessageEntity iMMessageEntity) {
        DbManager.getInstance().getDaoSession().getIMMessageEntityDao().insertOrReplace(iMMessageEntity);
    }

    /**
     * @desc 根据条件删除
     **/
    public static void deleteByCondition(WhereCondition condition, WhereCondition... condMore) {
        DbManager.getInstance().getDaoSession().getIMMessageEntityDao().queryBuilder().where(condition, condMore).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * @desc 修改数据
     **/
    public static void updateData(IMMessageEntity iMMessageEntity) {
        DbManager.getInstance().getDaoSession().getIMMessageEntityDao().update(iMMessageEntity);
    }

    /**
     * @desc 按条件返回按时间排序的结果集
     **/
    public static ArrayList<IMMessageEntity> queryByCondition(WhereCondition whereCondition, WhereCondition... condMore) {
        QueryBuilder<IMMessageEntity> builder = DbManager.getInstance().getDaoSession().getIMMessageEntityDao().queryBuilder();
        return (ArrayList<IMMessageEntity>) builder.where(whereCondition, condMore).orderDesc(IMMessageEntityDao.Properties.Timestamp).list();
    }


    /**
     * @desc 获取最后一条消息
     **/
    public static IMMessageEntity getLastCustomMessage(String uid) {
        QueryBuilder<IMMessageEntity> builder = DbManager.getInstance().getDaoSession().getIMMessageEntityDao().queryBuilder();
        builder = builder.whereOr(IMMessageEntityDao.Properties.SenderId.eq(uid), IMMessageEntityDao.Properties.ReceiverId.eq(uid));
        List<IMMessageEntity> imMessageEntityList = builder.orderDesc(IMMessageEntityDao.Properties.Timestamp).limit(1).list();

        if (null != imMessageEntityList && imMessageEntityList.size() > 0) {
            return imMessageEntityList.get(0);
        }
        return null;
    }

    /**
     * @desc 分页获取消息
     **/
    public static List<IMMessageEntity> getCustomMessageByLastTime(long time, String uid) {
        QueryBuilder<IMMessageEntity> builder = DbManager.getInstance().getDaoSession().getIMMessageEntityDao().queryBuilder();
        if (time > 0)
            builder = builder.where(IMMessageEntityDao.Properties.Timestamp.lt(time));
        builder = builder.whereOr(IMMessageEntityDao.Properties.SenderId.eq(uid), IMMessageEntityDao.Properties.ReceiverId.eq(uid));
        List<IMMessageEntity> imMessageEntityList = builder.orderDesc(IMMessageEntityDao.Properties.Timestamp).limit(20).list();
        return imMessageEntityList;
    }

    /**
     * @desc 未读消息结果集
     **/
    public static List<IMMessageEntity> getUnReadList(String uid) {
        QueryBuilder<IMMessageEntity> builder = DbManager.getInstance().getDaoSession().getIMMessageEntityDao().queryBuilder();
        builder = builder.whereOr(IMMessageEntityDao.Properties.SenderId.eq(uid), IMMessageEntityDao.Properties.ReceiverId.eq(uid));
        return builder.where(IMMessageEntityDao.Properties.IsReaded.eq(false)).list();
    }

    public static long countUnRead(String uid) {
        QueryBuilder<IMMessageEntity> builder = DbManager.getInstance().getDaoSession().getIMMessageEntityDao().queryBuilder();
        builder = builder.whereOr(IMMessageEntityDao.Properties.SenderId.eq(uid), IMMessageEntityDao.Properties.ReceiverId.eq(uid));
        return builder.where(IMMessageEntityDao.Properties.IsReaded.eq(false)).count();
    }

    /**
     * @desc 全部标记为以读
     **/
    public static void updateDataRedDot(String uid) {
        List<IMMessageEntity> unReaded = getUnReadList(uid);
        for (IMMessageEntity temp : unReaded) {
            temp.setIsReaded(true);
        }
        for (IMMessageEntity a : unReaded) {
            DbManager.getInstance().getDaoSession().getIMMessageEntityDao().update(a);
        }
    }

}
