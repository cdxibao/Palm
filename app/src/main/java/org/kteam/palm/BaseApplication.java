package org.kteam.palm;

import android.app.Application;
import android.content.Context;


import com.facebook.drawee.backends.pipeline.Fresco;
import com.umeng.socialize.PlatformConfig;

import org.apache.log4j.Level;
import org.kteam.common.network.volleyext.RequestManager;
import org.kteam.common.utils.CrashHandler;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.utils.FileUtils;
import org.kteam.palm.domain.manager.BaseDataManager;

import java.io.File;
import java.util.Map;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2015-06-16 11:00
 */
public class BaseApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        RequestManager.init(this);
        CrashHandler.getInstance().init(this);
        Fresco.initialize(this);
        initLog4j();

        new BaseDataManager().loadBaseData();
    }

    //友盟分享:各个平台的配置，建议放在全局Application或者程序入口
    {
        //微信
        PlatformConfig.setWeixin("wx9c74b8e091d13683", "dec57f527470ef0feb2f76e9f843f7cf");
        //新浪微博
        PlatformConfig.setSinaWeibo("2303039109", "b246c7266dedfa858cda041538ea57e4");
        //QQ控件
        PlatformConfig.setQQZone("1105214427", "TMuFyFfd73XUKGmg");
    }

    public static Context getContext() {
        return mContext;
    }

    private void initLog4j() {
        final LogConfigurator logConfigurator = new LogConfigurator();

        String logPath = FileUtils.getLogFilePath();
        try {
            if (!"".equals(logPath)) {
                File file = new File(logPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                logPath += File.separator + Constants.LOG_FILE;
                file = new File(logPath);
                if (!file.exists()) {
                    file.createNewFile();
                }
                logConfigurator.setFileName(logPath);
            }
            Level level = Constants.DEBUG ? Level.ALL : Level.ERROR;
            logConfigurator.setRootLevel(level);
            logConfigurator.setLevel("org.apache", Level.ALL);
            logConfigurator.configure();
        } catch (Exception e) {
            android.util.Log.e("Application", e.getMessage(), e);
        }
    }

    /**
     * Checks the response headers for session cookie and saves it
     * if it finds it.
     *
     * @param headers Response Headers.
     */
    public static final void checkSessionCookie(Map<String, String> headers) {
//        if (headers.containsKey(SET_COOKIE_KEY)
//                && headers.get(SET_COOKIE_KEY).startsWith(SESSION_COOKIE)) {
//            String cookie = headers.get(SET_COOKIE_KEY);
//            if (cookie.length() > 0) {
//                String[] splitCookie = cookie.split(";");
//                String[] splitSessionId = splitCookie[0].split("=");
//                cookie = splitSessionId[1];
//
//                SharedPrefeUtil sharedPrefeUtil = new SharedPrefeUtil(applicationContext);
//                sharedPrefeUtil.saveString(SESSION_COOKIE, cookie);
//
//                //TODO 兼容老版本
//                sharedPrefeUtil.saveString("JSESSIONID", cookie);
//            }
//        }
    }

    /**
     * Adds session cookie to headers if exists.
     *
     * @param headers
     */
    public static final void addSessionCookie(Map<String, String> headers) {
//        SharedPrefeUtil sharedPrefeUtil = new SharedPrefeUtil(applicationContext);
//        String sessionId = sharedPrefeUtil.getString(SESSION_COOKIE);
//
//        if (sessionId.length() > 0) {
//            StringBuilder builder = new StringBuilder();
//            builder.append(SESSION_COOKIE);
//            builder.append("=");
//            builder.append(sessionId);
//            if (headers.containsKey(COOKIE_KEY)) {
//                builder.append("; ");
//                builder.append(headers.get(COOKIE_KEY));
//            }
//            headers.put(COOKIE_KEY, builder.toString());
//        }
    }
}
