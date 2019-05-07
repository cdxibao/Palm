package org.kteam.palm.common.utils;

import org.kteam.palm.model.User;

/**
 * @Package org.kteam.palm.common.utils
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-06 00:01
 */
public class UserStateUtils {

    private static UserStateUtils instance = null;

    private User mUser = null;

    private UserStateUtils() {}

    public static UserStateUtils getInstance() {
        if (instance == null) {
            instance = new UserStateUtils();
        }
        return instance;
    }

    public User getUser() {
        if (mUser == null) {
            mUser = SharedPreferencesUtils.getInstance().getUser();
        }
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
        SharedPreferencesUtils.getInstance().saveUser(mUser);
    }

    public void deleteUser() {
        mUser = null;
        SharedPreferencesUtils.getInstance().deleteUser();
    }
}
