package org.kteam.palm.model;

import org.kteam.palm.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @Package org.kteam.palm.model
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-19 16:18
 */
public class SettingMenu {

    public int iconResId;
    public int titleResId;
    public String subTitle = "";
    public int type;

    public static List<SettingMenu> getLoginedSettingData() {
        List<SettingMenu> menuList = new ArrayList<SettingMenu>();

        SettingMenu unknownMenu = new SettingMenu();
        unknownMenu.type = SettingMenuType.UNKNOWN;
        menuList.add(unknownMenu);

        SettingMenu updatePwdMenu = new SettingMenu();
        updatePwdMenu.iconResId = R.mipmap.update_pwd;
        updatePwdMenu.titleResId = R.string.update_pwd;
        updatePwdMenu.type = SettingMenuType.UPDATE_PASSWD;
        menuList.add(updatePwdMenu);


        SettingMenu versionCheckMenu = new SettingMenu();
        versionCheckMenu.iconResId = R.mipmap.version_check;
        versionCheckMenu.titleResId = R.string.version_check;
        versionCheckMenu.type = SettingMenuType.CHECK_VERSION;
        menuList.add(versionCheckMenu);

        SettingMenu switchUserMenu = new SettingMenu();
        switchUserMenu.iconResId = R.mipmap.switch_user;
        switchUserMenu.titleResId = R.string.switch_user;
        switchUserMenu.type = SettingMenuType.SWICH_USER;
        menuList.add(switchUserMenu);

        SettingMenu exitMenu = new SettingMenu();
        exitMenu.iconResId = R.mipmap.exit;
        exitMenu.titleResId = R.string.exit;
        exitMenu.type = SettingMenuType.EXIT;
        menuList.add(exitMenu);
        return menuList;
    }

    public static List<SettingMenu> getUnLoginSettingData() {
        List<SettingMenu> menuList = new ArrayList<SettingMenu>();

        SettingMenu unknownMenu = new SettingMenu();
        unknownMenu.type = SettingMenuType.UNKNOWN;
        menuList.add(unknownMenu);

        SettingMenu versionCheckMenu = new SettingMenu();
        versionCheckMenu.iconResId = R.mipmap.version_check;
        versionCheckMenu.titleResId = R.string.version_check;
        versionCheckMenu.type = SettingMenuType.CHECK_VERSION;
        menuList.add(versionCheckMenu);

        SettingMenu exitMenu = new SettingMenu();
        exitMenu.iconResId = R.mipmap.exit;
        exitMenu.titleResId = R.string.exit;
        exitMenu.type = SettingMenuType.EXIT;
        menuList.add(exitMenu);
        return menuList;
    }

    public static class SettingMenuType {

        /**未知*/
        public static final int UNKNOWN = 0;

        /**修改密码*/
        public static final int UPDATE_PASSWD = 1;

        /**版本检测*/
        public static final int CHECK_VERSION = 2;

        /**切换用户*/
        public static final int SWICH_USER = 3;

        /**退出*/
        public static final int EXIT = 4;

    }
}
