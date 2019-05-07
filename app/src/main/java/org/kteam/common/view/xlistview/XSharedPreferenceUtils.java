package org.kteam.common.view.xlistview;

import android.content.Context;
import android.content.SharedPreferences;

import org.kteam.common.utils.DateUtils;

/**
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2015-07-08 10:54
 */
public class XSharedPreferenceUtils {
    public static final String KEY_AIR_COND_QUOTE_RECORD_LIST = "1";
    public static final String KEY_GROUP_HEATING_QUOTE_RECORD_LIST = "2";
    public static final String KEY_PARTAKE_ACTION_LIST = "3";

    public static final String TYPE_LIST_VIEW = "listView";
    public static final String TYPE_SCROLL_VIEW = "scrollView";

    private Context mContext;
    private SharedPreferences mSharedPreferences;

    public XSharedPreferenceUtils(Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences("PullRefreshView", Context.MODE_PRIVATE);
    }

    public void clearAll() {
        mSharedPreferences.edit().clear().commit();
    }

    public String getRefereshTime(String type, String key) {
        key = type + "-" + key;
        return mSharedPreferences.getString(key, DateUtils.getCurrentTime());
    }

    public void setRefreshTime(String type, String key) {
        key = type + "-" + key;
        mSharedPreferences.edit().putString(key, DateUtils.getCurrentTime()).commit();
    }


}
