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
public class LeftMenu {

    public int iconResId;
    public int titleResId;
    public int type;

    /**未登录时的菜单*/
    public static List<LeftMenu> getLevel0Menu() {
        List<LeftMenu> menuList = new ArrayList<LeftMenu>();

        LeftMenu unknownMenu = new LeftMenu();
        unknownMenu.type = LeftMenuType.UNKNOWN;
        menuList.add(unknownMenu);

        LeftMenu newInsuredMenu = new LeftMenu();
        newInsuredMenu.iconResId = R.mipmap.left_new_insurance;
        newInsuredMenu.titleResId = R.string.personal_new_insured;
        newInsuredMenu.type = LeftMenuType.NEW_INSURED;
        menuList.add(newInsuredMenu);

        menuList.add(unknownMenu);

        LeftMenu idcardBindMenu = new LeftMenu();
        idcardBindMenu.iconResId = R.mipmap.left_idcard_bind;
        idcardBindMenu.titleResId = R.string.bind_idcard;
        idcardBindMenu.type = LeftMenuType.IDCARD_BIND;
        menuList.add(idcardBindMenu);

        addUnLoginedSettingMenus(menuList);

        return menuList;
    }

    public static List<LeftMenu> getLevel1Data() {
        List<LeftMenu> menuList = new ArrayList<LeftMenu>();

        LeftMenu unknownMenu = new LeftMenu();
        unknownMenu.type = LeftMenuType.UNKNOWN;
        menuList.add(unknownMenu);

        LeftMenu newInsuredMenu = new LeftMenu();
        newInsuredMenu.iconResId = R.mipmap.left_new_insurance;
        newInsuredMenu.titleResId = R.string.personal_new_insured;
        newInsuredMenu.type = LeftMenuType.NEW_INSURED;
        menuList.add(newInsuredMenu);

        menuList.add(unknownMenu);

        LeftMenu idcardBindMenu = new LeftMenu();
        idcardBindMenu.iconResId = R.mipmap.left_idcard_bind;
        idcardBindMenu.titleResId = R.string.bind_idcard;
        idcardBindMenu.type = LeftMenuType.IDCARD_BIND;
        menuList.add(idcardBindMenu);

        addUpadatePwdAndSwitchUserMenu(menuList);

        addLoginedSettingMenus(menuList);

        return menuList;
    }

    public static List<LeftMenu> getLevel2Data() {
        List<LeftMenu> menuList = new ArrayList<LeftMenu>();

        LeftMenu unknownMenu = new LeftMenu();
        unknownMenu.type = LeftMenuType.UNKNOWN;
        menuList.add(unknownMenu);

        LeftMenu userInfoMenu = new LeftMenu();
        userInfoMenu.iconResId = R.mipmap.left_user_info;
        userInfoMenu.titleResId = R.string.user_info;
        userInfoMenu.type = LeftMenuType.USER_INFO;
        menuList.add(userInfoMenu);

        LeftMenu orderMenu = new LeftMenu();
        orderMenu.iconResId = R.mipmap.order;
        orderMenu.titleResId = R.string.order;
        orderMenu.type = LeftMenuType.ORDER;
        menuList.add(orderMenu);

        menuList.add(unknownMenu);

        LeftMenu idcardBindMenu = new LeftMenu();
        idcardBindMenu.iconResId = R.mipmap.left_idcard_bind;
        idcardBindMenu.titleResId = R.string.bind_idcard;
        idcardBindMenu.type = LeftMenuType.IDCARD_BIND;
        menuList.add(idcardBindMenu);

        LeftMenu idcardUNBindMenu = new LeftMenu();
        idcardUNBindMenu.iconResId = R.mipmap.left_idcard_unbind;
        idcardUNBindMenu.titleResId = R.string.unbind_idcard;
        idcardUNBindMenu.type = LeftMenuType.IDCARD_UNBIND;
        menuList.add(idcardUNBindMenu);

        addUpadatePwdAndSwitchUserMenu(menuList);

        addLoginedSettingMenus(menuList);

        return menuList;
    }

    public static List<LeftMenu> getLevel3Data() {
        List<LeftMenu> menuList = new ArrayList<LeftMenu>();
        LeftMenu unknownMenu = new LeftMenu();
        unknownMenu.type = LeftMenuType.UNKNOWN;
        menuList.add(unknownMenu);

        LeftMenu userInfoMenu = new LeftMenu();
        userInfoMenu.iconResId = R.mipmap.left_user_info;
        userInfoMenu.titleResId = R.string.user_info;
        userInfoMenu.type = LeftMenuType.USER_INFO;
        menuList.add(userInfoMenu);

        LeftMenu orderMenu = new LeftMenu();
        orderMenu.iconResId = R.mipmap.order;
        orderMenu.titleResId = R.string.order;
        orderMenu.type = LeftMenuType.ORDER;
        menuList.add(orderMenu);

        menuList.add(unknownMenu);

        LeftMenu idcardUNBindMenu = new LeftMenu();
        idcardUNBindMenu.iconResId = R.mipmap.left_idcard_unbind;
        idcardUNBindMenu.titleResId = R.string.unbind_idcard;
        idcardUNBindMenu.type = LeftMenuType.IDCARD_UNBIND;
        menuList.add(idcardUNBindMenu);

        addUpadatePwdAndSwitchUserMenu(menuList);

        addLoginedSettingMenus(menuList);
        return menuList;
    }

    /**未登录时的设置相关菜单*/
    private static void addUnLoginedSettingMenus(List<LeftMenu> menuList) {
        LeftMenu unknownMenu = new LeftMenu();
        unknownMenu.type = LeftMenuType.UNKNOWN;
        menuList.add(unknownMenu);

        LeftMenu optHelpMenu = new LeftMenu();
        optHelpMenu.iconResId = R.mipmap.opt_help;
        optHelpMenu.titleResId = R.string.opt_help;
        optHelpMenu.type = LeftMenuType.OPT_HELP;
        menuList.add(optHelpMenu);

        LeftMenu versionCheckMenu = new LeftMenu();
        versionCheckMenu.iconResId = R.mipmap.version_check;
        versionCheckMenu.titleResId = R.string.version_check;
        versionCheckMenu.type = LeftMenuType.CHECK_VERSION;
        menuList.add(versionCheckMenu);

        LeftMenu aboutMenu = new LeftMenu();
        aboutMenu.iconResId = R.mipmap.about;
        aboutMenu.titleResId = R.string.about;
        aboutMenu.type = LeftMenuType.ABOUNT;
        menuList.add(aboutMenu);

        LeftMenu exitMenu = new LeftMenu();
        exitMenu.iconResId = R.mipmap.exit;
        exitMenu.titleResId = R.string.exit;
        exitMenu.type = LeftMenuType.EXIT;
        menuList.add(exitMenu);
    }

    /**已登录时的设置相关菜单*/
    private static void addLoginedSettingMenus(List<LeftMenu> menuList) {
        LeftMenu unknownMenu = new LeftMenu();
        unknownMenu.type = LeftMenuType.UNKNOWN;
        menuList.add(unknownMenu);

        LeftMenu optHelpMenu = new LeftMenu();
        optHelpMenu.iconResId = R.mipmap.opt_help;
        optHelpMenu.titleResId = R.string.opt_help;
        optHelpMenu.type = LeftMenuType.OPT_HELP;
        menuList.add(optHelpMenu);

        LeftMenu versionCheckMenu = new LeftMenu();
        versionCheckMenu.iconResId = R.mipmap.version_check;
        versionCheckMenu.titleResId = R.string.version_check;
        versionCheckMenu.type = LeftMenuType.CHECK_VERSION;
        menuList.add(versionCheckMenu);

        LeftMenu aboutMenu = new LeftMenu();
        aboutMenu.iconResId = R.mipmap.about;
        aboutMenu.titleResId = R.string.about;
        aboutMenu.type = LeftMenuType.ABOUNT;
        menuList.add(aboutMenu);

        LeftMenu exitMenu = new LeftMenu();
        exitMenu.iconResId = R.mipmap.exit;
        exitMenu.titleResId = R.string.exit;
        exitMenu.type = LeftMenuType.EXIT;
        menuList.add(exitMenu);
    }

    private static void addUpadatePwdAndSwitchUserMenu(List<LeftMenu> menuList) {
        LeftMenu updatePwdMenu = new LeftMenu();
        updatePwdMenu.iconResId = R.mipmap.update_pwd;
        updatePwdMenu.titleResId = R.string.update_pwd;
        updatePwdMenu.type = LeftMenuType.UPDATE_PASSWD;
        menuList.add(updatePwdMenu);

        LeftMenu switchUserMenu = new LeftMenu();
        switchUserMenu.iconResId = R.mipmap.switch_user;
        switchUserMenu.titleResId = R.string.switch_user;
        switchUserMenu.type = LeftMenuType.SWICH_USER;
        menuList.add(switchUserMenu);
    }

    public static List<LeftMenu> getMenuList(int level) {
        List<LeftMenu> list = null;
        switch (level) {
            case 1:
                list = getLevel1Data();
                break;
            case 2:
                list = getLevel2Data();
                break;
            case 3:
                list = getLevel3Data();
                break;
            default:
                list = getLevel1Data();
                break;
        }
        return list;
    }

    public static class LeftMenuType {

        /**未知*/
        public static final int UNKNOWN = 0;

        /**个人信息*/
        public static final int USER_INFO = 1;

        /**交易记录查询*/
        public static final int ORDER = 2;

        /**新参保*/
        public static final int NEW_INSURED = 3;

        /**操作指南*/
        public static final int OPT_HELP = 4;

        /**关于*/
        public static final int ABOUNT = 6;

        /**身份证绑定*/
        public static final int IDCARD_BIND = 7;

        /**身份证解除绑定*/
        public static final int IDCARD_UNBIND = 8;

        /**修改密码*/
        public static final int UPDATE_PASSWD = 9;

        /**版本检测*/
        public static final int CHECK_VERSION = 10;

        /**切换用户*/
        public static final int SWICH_USER = 11;

        /**退出*/
        public static final int EXIT = 12;

    }
}
