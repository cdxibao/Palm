package org.kteam.common.utils;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.apache.log4j.Logger;
import org.kteam.palm.BaseActivity;

/**
 * @Description 全局Crash捕获处理
 * @Author Micky Liu
 * @Email sglazelhw@126.com
 * @Date 2015-04-03 下午 1:43
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler  {
    public final Logger mLogger = Logger.getLogger(getClass());
    private static CrashHandler INSTANCE = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultUEH;
    private Context mContext;

    private CrashHandler() {
        mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context ctx) {
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = ctx;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        mLogger.error(thread.getName(), ex);
        Log.e("ddd", ex.getMessage(), ex);
//        AppManager.getInstance().restartApp(mContext);
        ViewUtils.exitApp(mContext);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
        mDefaultUEH.uncaughtException(thread, ex);
    }

}
