package org.kteam.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DateUtils {

    public static final int MAX_DAY_OF_MONTH = 100001;

    public static Date constructDate(int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        return calendar.getTime();
    }

    /** 根据生日计算年龄 */
    public static int getAge(Date birthDay) {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthDay)) {
            return 0;
        }

        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH)+1;
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                age--;
            }
        }

        return age;
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
                .format(new Date());
    }

    public static String getCurrentLongTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                .format(new Date());
    }

    /**
     * 格式化时间
     * @param time
     * @return
     */
    public static String getTodayOrYestodayStr(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if(time == null || "".equals(time)){
            return getCurrentTime();
        }
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            return getCurrentTime();
        }

        Calendar current = Calendar.getInstance();

        Calendar today = Calendar.getInstance();	//今天

        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH));
        //  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
        today.set( Calendar.HOUR_OF_DAY, 0);
        today.set( Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Calendar yesterday = Calendar.getInstance();	//昨天

        yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
        yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
        yesterday.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH)-1);
        yesterday.set( Calendar.HOUR_OF_DAY, 0);
        yesterday.set( Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);

        current.setTime(date);

        if(current.after(today)){
            return "今天 "+ time.split(" ")[1];
        }else if(current.before(today) && current.after(yesterday)){

            return "昨天 "+ time.split(" ")[1];
        }else{
            int index = time.indexOf("-")+1;
            return time.substring(index, time.length());
        }
    }

    /**
     * 获取年、月、日、时、分、秒、星期 的map集合
     * @return
     */
    public static Map<Integer, Integer> getTimeMap(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);//获取年份
        int month = cal.get(Calendar.MONTH) + 1;//获取月份
        int day = cal.get(Calendar.DATE);//获取日
        int hour = cal.get(Calendar.HOUR_OF_DAY);//小时
        int minute = cal.get(Calendar.MINUTE);//分           
        int second = cal.get(Calendar.SECOND);//秒
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);//一周的第几天
        int maxDayOfMonth = cal.getActualMaximum(Calendar.DATE); //一个月有多少天

        Map<Integer, Integer> timeMap = new HashMap<Integer, Integer>();
        timeMap.put(Calendar.YEAR, year);
        timeMap.put(Calendar.MONTH, month);
        timeMap.put(Calendar.DATE, day);
        timeMap.put(Calendar.HOUR_OF_DAY, hour);
        timeMap.put(Calendar.MINUTE, minute);
        timeMap.put(Calendar.SECOND, second);
        timeMap.put(Calendar.DAY_OF_WEEK, dayOfWeek);
        timeMap.put(MAX_DAY_OF_MONTH, maxDayOfMonth);

        return timeMap;
    }
}
