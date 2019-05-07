package org.kteam.palm.common.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.kteam.palm.R;

/**
 * @Package org.kteam.palm.common.utils
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-13 22:02
 */
public class PalmSqliteHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "Ticketing";
    private static final int DATABASE_VERSION = 4;
    private Context mContext;

    public PalmSqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
        try {
            String[] classNames = mContext.getResources().getStringArray(
                    R.array.sqlite_table);
            for (String className : classNames) {
                TableUtils.createTable(arg1, Class.forName(className));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2,
                          int arg3) {
        try {
            String[] classNames = mContext.getResources().getStringArray(
                    R.array.sqlite_table);
            for (String className : classNames) {
                TableUtils.dropTable(arg1, Class.forName(className), true);
            }
            onCreate(arg0, arg1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void clearTable(Class clazz) throws java.sql.SQLException {
        TableUtils.clearTable(connectionSource, clazz);
    }

}