package org.kteam.palm.domain.repository.impl;

import android.content.Context;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.kteam.palm.common.utils.PalmSqliteHelper;
import org.kteam.palm.domain.repository.BaseRepository;

import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * @Package org.kteam.palm.domain.repository.impl
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-13 22:12
 */
public class BaseRepositoryImpl implements BaseRepository {
    protected PalmSqliteHelper mHelper;

    public BaseRepositoryImpl(Context context) {
        mHelper = OpenHelperManager.getHelper(context, PalmSqliteHelper.class);

    }

    public void close() {
        OpenHelperManager.releaseHelper();
        mHelper = null;
    }

    protected AndroidDatabaseConnection connection = null;

    protected Savepoint traslationBegin(String pointName) throws SQLException {
        Savepoint savepoint = null;
        connection = new AndroidDatabaseConnection(
                mHelper.getWritableDatabase(), true);
        return savepoint = connection.setSavePoint(pointName);
    }

    protected void traslaionEnd(Savepoint savepoint) throws SQLException {
        connection.commit(savepoint);
    }

    protected void traslationRollBack(Savepoint savepoint) {
        try {
            connection.rollback(savepoint);
        } catch (SQLException e) {
            // debug rollback fail
        }
    }

    protected void clearTable(Class clazz) throws SQLException {
        mHelper.clearTable(clazz);
    }

}