package org.kteam.palm.domain.repository.impl;

import android.content.Context;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.Where;

import org.apache.log4j.Logger;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.domain.repository.BaseDataRepository;
import org.kteam.palm.model.BaseData;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

/**
 * @Package org.kteam.palm.domain.repository.impl
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-13 22:16
 */
public class BaseDataRepositoryImpl extends BaseRepositoryImpl implements BaseDataRepository {
    private Logger logger = Logger.getLogger(getClass());

    private Dao<BaseData, Integer> mDao;

    public BaseDataRepositoryImpl(Context context) throws Exception {
        super(context);
        try {
            mDao = super.mHelper.getDao(BaseData.class);
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public void saveList(List<BaseData> list) {
        String pointName = "save_region_point";
        Savepoint savepoint = null;
        AndroidDatabaseConnection connection = new AndroidDatabaseConnection(mHelper.getWritableDatabase(), true);
        try {
            mDao.setAutoCommit(connection, false);
            savepoint = connection.setSavePoint(pointName);

            DeleteBuilder<BaseData, Integer> db = mDao.deleteBuilder();
            db.where().ne(BaseData.COLUMN_TYPE, "-1");
            db.delete();

            for (BaseData baseData : list) {
                mDao.createOrUpdate(baseData);
            }
            mDao.commit(connection);
        } catch (Exception e) {
            try {
                if (savepoint != null) {
                    connection.rollback(savepoint);
                }
            } catch (SQLException e1) {
                logger.error(e.getMessage(), e);
            }
            logger.error(e.getMessage(), e);
        }
    }

    public List<BaseData> getMonthList() {
        List<BaseData> resultList = null;
        try {
            Where<BaseData, Integer> where = mDao.queryBuilder().where().eq(BaseData.COLUMN_TYPE, Constants.PAY_MONTH_KEY);
            resultList = where.query();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return resultList;
    }

    public List<BaseData> getPayedLevel() {
        List<BaseData> resultList = null;
        try {
            Where<BaseData, Integer> where = mDao.queryBuilder().where().eq(BaseData.COLUMN_TYPE, Constants.PAY_LOW_LEVEL_KEY);
            resultList = where.query();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return resultList;
    }

    public List<BaseData> getPayedLevel(String levelKey) {
        List<BaseData> resultList = null;
        try {
            Where<BaseData, Integer> where = mDao.queryBuilder().where().eq(BaseData.COLUMN_TYPE, levelKey);
            resultList = where.query();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return resultList;
    }

    @Override
    public List<BaseData> getYiliaoInsuredPayedLevel() {
        List<BaseData> resultList = null;
        try {
            Where<BaseData, Integer> where = mDao.queryBuilder().where().eq(BaseData.COLUMN_TYPE, Constants.PAY_YILIAO_INSURCED_LEVEL_KEY);
            resultList = where.query();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return resultList;
    }

    @Override
    public List<BaseData> getNationList() {
        List<BaseData> resultList = null;
        try {
            Where<BaseData, Integer> where = mDao.queryBuilder().where().eq(BaseData.COLUMN_TYPE, Constants.PAY_NATION_KEY);
            resultList = where.query();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return resultList;
    }

    @Override
    public List<BaseData> getPayStatusList() {
        List<BaseData> resultList = null;
        try {
            Where<BaseData, Integer> where = mDao.queryBuilder().where().eq(BaseData.COLUMN_TYPE, Constants.PAY_TYPE_KEY);
            resultList = where.query();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return resultList;
    }

    @Override
    public List<BaseData> getPayBankList() {
        List<BaseData> resultList = null;
        try {
            Where<BaseData, Integer> where = mDao.queryBuilder().where().eq(BaseData.COLUMN_TYPE, Constants.PAY_BANK_KEY);
            resultList = where.query();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return resultList;
    }
}
