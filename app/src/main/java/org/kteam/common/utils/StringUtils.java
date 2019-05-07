package org.kteam.common.utils;

import android.text.TextUtils;

import java.util.Random;
import java.util.UUID;

/**
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2015-07-10 13:58
 */
public class StringUtils {

    /**
     * 获取UUID
     *
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取随机唯一值
     *
     * @return
     */
    public static String getRandomKey() {
        String uuid = getUUID();
        if (uuid == null || "".equals(uuid)) {
            return String.valueOf(System.currentTimeMillis()) + new Random(1000);
        }
        return uuid.replace("-", "").toLowerCase();
    }

    public static boolean isNullOrBlank(String str) {
        if (str == null || str.trim().equals("")) {
            return true;
        }
        return false;
    }

    public static boolean isAsciiNumeric(final char ch) {
        return ch >= '0' && ch <= '9';
    }

    public static boolean isNumeric(final CharSequence cs) {
        if (cs == null || cs.length() == 0) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidPhone(String phone) {
//		Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
//		Matcher matcher = pattern.matcher(phone);
        if (!TextUtils.isEmpty(phone) && phone.startsWith("1") && phone.length() == 11) {
            return true;
        }
        return false;
    }
}
