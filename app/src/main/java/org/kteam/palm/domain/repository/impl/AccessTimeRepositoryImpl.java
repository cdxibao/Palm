package org.kteam.palm.domain.repository.impl;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;

import org.apache.log4j.Logger;
import org.kteam.palm.domain.repository.AccessTimeRespository;
import org.kteam.palm.model.AccessTime;

import java.sql.SQLException;
import java.util.List;

/**
 * @Package org.kteam.palm.domain.repository.impl
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-03-06 16:55
 */
public class AccessTimeRepositoryImpl extends BaseRepositoryImpl implements AccessTimeRespository {

    private Logger mLogger = Logger.getLogger(getClass());

    private Dao<AccessTime, Integer> mDao;

    public AccessTimeRepositoryImpl(Context context) throws Exception {
        super(context);
        try {
            mDao = super.mHelper.getDao(AccessTime.class);
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public void saveOrUpdate(AccessTime accessTime) {
        try {
            mDao.createOrUpdate(accessTime);
        } catch (Exception e) {
            mLogger.error(e.getMessage(), e);
        }
    }

    @Override
    public AccessTime get(String categoryId) {
        AccessTime accessTime = null;
        try {
            Where<AccessTime, Integer> where = mDao.queryBuilder().where().eq(AccessTime.COLUMN_CATEGORY_ID, categoryId);
            List<AccessTime> resultList = where.query();
            if (resultList != null && resultList.size() > 0) {
                accessTime = resultList.get(0);
            }
        } catch (Exception e) {
            mLogger.error(e.getMessage(), e);
        }
        return accessTime;
    }
}
