package org.kteam.palm.common.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.apache.log4j.Logger;
import org.kteam.palm.BaseApplication;
import org.kteam.palm.model.User;

import java.util.Random;
import java.util.UUID;

/**
 * @Package org.kteam.common.utils
 * @Project Palm
 * @Description 偏好管理
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-12 21:19
 */
public class SharedPreferencesUtils {
    private final Logger mLogger = Logger.getLogger(getClass());

    private static SharedPreferencesUtils instance = null;
    private SharedPreferences mSharedPreferences;
    private String mUUID;

    private SharedPreferencesUtils() {
        mSharedPreferences = BaseApplication.getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferencesUtils getInstance() {
        if (instance == null) {
            instance = new SharedPreferencesUtils();
        }
        return instance;
    }

    public void updateLoginTime() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong("loginTime", System.currentTimeMillis());
        editor.commit();
    }

    public void updateAccessTime() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("accessTime", DateUtils.getDateStr(System.currentTimeMillis()));
        editor.commit();
    }

    public String getAccessTime() {
        String accessTime = mSharedPreferences.getString("accessTime", "1970-12-12 11:11:11");
        accessTime = "1970-12-12 22:22:22";
        return accessTime;
    }

    public void checkValidUser() {
        long lastLoginTime =  mSharedPreferences.getLong("loginTime", 0);
        if (System.currentTimeMillis() - lastLoginTime > Constants.CACHE_LOGIN_STATE_DAY) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("user", "");
            editor.commit();
        }
    }

    public void saveUser(User user) {
        String userStr = ObjectUtil.objectToString(user);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("user", userStr);
        editor.commit();
    }

    public User getUser() {
        try {
//            checkValidUser();
            String userStr = mSharedPreferences.getString("user", "");
            if (TextUtils.isEmpty(userStr)) {
                return null;
            }
            User user = (User) ObjectUtil.stringToObject(userStr);
            return user;
        } catch (Exception e) {
            mLogger.error(e.getMessage(), e);
        }
        return null;
    }

    public void deleteUser() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("user", "");
        editor.commit();
    }

    public String getUUID() {
        if (!TextUtils.isEmpty(mUUID)) return mUUID;
        mUUID = mSharedPreferences.getString("uuid", "");
        if (!TextUtils.isEmpty(mUUID)) return mUUID;
        try {
            mUUID = UUID.randomUUID().toString().replaceAll("-", "");
        } catch (Exception e) {

        }
        if (TextUtils.isEmpty(mUUID)) {
            int rand = new Random().nextInt(90000) + 10000;
            mUUID = String.valueOf(System.currentTimeMillis())  + rand;
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("uuid", mUUID);
        editor.commit();
        return mUUID;
    }
}
