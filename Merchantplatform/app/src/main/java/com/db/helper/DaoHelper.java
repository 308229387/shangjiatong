package com.db.helper;

import android.content.Context;

import com.db.dao.gen.DaoSession;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.QueryBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by 58 on 2016/12/6.
 */
public class DaoHelper {
    /**
     * @desc 添加数据至数据库
     **/
    public static <T extends Object> void insertData(Context context, T t) {
        ((AbstractDao) reflectMethod(context, t)).insert(t);
    }

    /**
     * @desc 将数据实体通过事务添加至数据库
     **/
    public static <T extends Object> void insertData(Context context, List<T> list, T t) {
        if (null == list || list.size() <= 0) {
            return;
        }
        ((AbstractDao) reflectMethod(context, t)).insertInTx(list);
    }

    /**
     * @desc 添加数据至数据库，如果存在，将原来的数据覆盖
     **/
    public static <T extends Object> void saveData(Context context, T t) {
        ((AbstractDao) reflectMethod(context, t)).save(t);
    }

    /**
     * @desc 查询所有数据
     **/
    public static <T extends Object> List<T> queryAll(Context context, T t) {
        QueryBuilder<T> builder = ((AbstractDao) reflectMethod(context, t)).queryBuilder();
        return builder.build().list();
    }

    private static <T extends Object> Object reflectMethod(Context context, T t) {
        try {
            Method method = DaoSession.class.getDeclaredMethod("get" + t.getClass().getSimpleName() + "Dao", new Class[0]);
            return method.invoke(DbManager.getDaoSession(context), new Object[]{});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}