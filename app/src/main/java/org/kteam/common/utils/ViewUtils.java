package org.kteam.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.widget.NestedScrollView;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.log4j.Logger;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.R;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @Description
 * @Author Micky Liu
 * @Email sglazelhw@126.com
 * @Date 2015-03-24 下午 5:47
 */
public class ViewUtils {

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null && activity.getCurrentFocus().getWindowToken() != null) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void showKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null && activity.getCurrentFocus().getWindowToken() != null) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static DisplayMetrics getScreenInfo(Context context) {
        // 获取屏幕大小
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm;
    }

    public static int getStatusHeight(Context context){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch(Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }

    /**防止连续点击*/
    private static long lastClickTime;
    public static boolean isFastClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ( timeD > 0 && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 获取图片名称获取图片的资源id的方法
     * @param iconName
     * @return
     */
    public static int getResource(Context context, String iconName) {
        int resId = context.getResources().getIdentifier(iconName, "mipmap", context.getPackageName());
        return resId;
    }


    public static void showToast(Context context, String text) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.view_toast, null);
        TextView tv = (TextView) view.findViewById(R.id.tv);
        tv.setText(text);
        Toast toast = new Toast(context);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showToast(Context context, int resId) {
        try {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.view_toast, null);
            TextView tv = (TextView) view.findViewById(R.id.tv);
            tv.setText(resId);
            Toast toast = new Toast(context);
            toast.setView(view);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideInputMethod(Activity activity) {
        try {
            if(activity.getCurrentFocus() != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {

        }
    }

    public static float getTextViewLength(Paint paint, String text){
        float textLength = paint.measureText(text);
        return textLength;
    }

    public static void exitApp(Context context) {
        context.sendBroadcast(new Intent(BaseActivity.ACTION_EXIT_APP));
    }
    public static void restartApp(Context context) {
        exitApp(context);
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

}
