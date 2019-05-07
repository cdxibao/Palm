package org.kteam.palm.common.utils;

import android.graphics.Bitmap;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @PACKAGE_NAME org.kteam.palm.common.utils
 * @Project Palm
 *
 * @Description
 *
 * @Author Micky Liu
 * @Date 2015-11-22 15:35
 * @Version 1.0.0
 */
public class Constants {
    /**
     * 是否启用调试
     */
    public static final boolean DEBUG = false;

    public static final String SHARED_PREFERENCES_FILE_NAME = "Palm";
    public static final int CACHE_LOGIN_STATE_DAY  = 7 * 24 * 60 * 60 * 1000;

    //缓存配置
    public static int DISK_IMAGECACHE_SIZE = 1024*1024*10;
    public static Bitmap.CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    public static int DISK_IMAGECACHE_QUALITY = 100;  //PNG is lossless so quality is ignored but must be provided

    public static final String BASE_FILE_PATH = "Palm";
    public static final String LOG_PATH = BASE_FILE_PATH + File.separator + "log";
    public static final String LOG_FILE = BASE_FILE_PATH + ".log";

    public static final String PACKAGE_NAME = "org.kteam.palm";
    public static final String APK_NAME = "Palm.apk";
    public static final String APK_LOCAL_PATH = BASE_FILE_PATH + File.separator + APK_NAME;

    public static final int PAGE_SIZE = 10;

    public static ExecutorService executorService = Executors.newFixedThreadPool(5);

    public static final int AUTH_CODE_TIME = 120; //验证码倒计时

    public static final int BUTTON_UNABLE_ALPHA = 100;

    //网络访问URL
    public static final String BASE_URL = "http://api.sclzsi.cn/"; //"http://api.kteam.cn/";//http://192.168.0.102:8080/";
//        public static final String BASE_URL = "http://pengqi.51vip.biz:20035/";
//    public static final String BASE_URL = "http://testapi.sclzsi.com/";
    public static final String URL_GET_DICT = "api/base/get/increment/dict";
    public static final String URL_GET_AD = "api/info/get/indexImg";
    public static final String URL_GET_MODULE = "api/info/get/category";
    public static final String URL_GET_ARTICLE_LIST = "api/info/get/article";
    public static final String URL_GET_ARTICLE = "api/info/get/article/id";
    public static final String URL_SEND_CODE = "api/message/send/sendVCode";
    public static final String URL_USER_REGISTER = "api/user/register";
    public static final String URL_UPDATE_PHONE = "api/user/upd/phone";
    public static final String URL_USER_LOGIN = "api/user/login";
    public static final String URL_REFIND_PWD = "api/user/retrieve/password";
    public static final String URL_BIND_IDCARD = "api/user/phone/bind/identity";
    public static final String URL_UNBIND_IDCARD = "api/user/remove/bind/phone";
    public static final String URL_GET_USER_INFO = "api/query/userinfo";
    public static final String URL_GET_ACCOUNT = "api/query/accountInfo";
    public static final String URL_GET_PERSONAL_PAY_YANGLAO = "api/query/payQuery";
    public static final String URL_GET_PERSONAL_PAY_YILIAO = "api/query/payQuery/yb";
    public static final String URL_GET_PENSION_GRANT = "api/query/pension/grant";
    public static final String URL_PAY_INFO_YANGLAO = "api/payment/mention/userinfo";
    public static final String URL_PAY_INFO_YILIAO = "api/payment/mention/userinfo/yb";
    public static final String URL_PAY_CALCULATION_YANGLAO = "api/payment/calculation";
    public static final String URL_PAY_CALCULATION_YILIAO = "api/payment/calculation/yb";
    public static final String URL_SUBMIT_PAY = "api/payment/accounted";
    public static final String URL_PENSION_FORM = "api/query/pension/constitute";
    public static final String URL_PENSION_ADJUST_WAGES = "api/query/overyear/wage";
    public static final String URL_NEW_INSURANCE_YANGLAO = "api/user/phone/new/insurance";
    public static final String URL_NEW_INSURANCE_YILIAO = "api/user/phone/new/insurance/yb";
    public static final String URL_UPDATE_PASSWD = "api/user/upd/password";
    public static final String URL_CHECK_VERSION = "api/apk/update";
    public static final String URL_ACCOUNT_YEAR_INFO = "api/query/accountInfo/month";
    public static final String URL_LOAD_USER_INFO = "api/user/get/userinfo/phone";
    public static final String URL_GET_PAY_URL_YANGLAO = "api/payment/ccb/url";
    public static final String URL_GET_PAY_URL_YILIAO = "api/payment/ccb/url/yb";
    public static final String URL_GET_ORDER = "api/user/my/order";
    public static final String URL_ACCESS = "api/info/get/access";
    public static final String URL_PAY_DISPOSABLE = "api/query/getPayDisposable";
    public static final String URL_PAYED_INFO = "api/user/my/order/aadjno";
    public static final String URL_YL_PAY_HISTORY = "api/query/getYlbxlnjfqk";
    public static final String URL_YL_PAY_HISTORY_YUE = "api/query/getYlbxlnjfqkYue";
    public static final String URL_YL_PAY_HISTORY_MX = "api/query/getYlbxgrzhszmx";
    public static final String URL_YANGLAO_INFO_JGSF = "api/query/getJgryyljffjl";
    /**机关人员养老保险个人缴费情况*/
    public static final String URL_YANGLAO_ACCOUT_PAY_INFO = "api/query/getJgylbxjfqk";
    /**机关人员职业年金个人缴费情况*/
    public static final String URL_YANGLAO_ACCOUT_NJ_INFO = "api/query/getJgzynjjfqk";
    public static final String URL_USER_LOGGER = "/api/log/login";
    /**建行人脸识别*/
    public static final String URL_CCBFACE_URL = "api/base/get/ccb/url";

    public static final String FIRST_SOURCE = "0002";

    public static final String PAY_MONTH_KEY = "CSA001";
    public static final String PAY_LEVEL_KEY = "ZAC100";
    public static final String PAY_YILIAO_INSURCED_LEVEL_KEY = "AAZ289";
    public static final String PAY_NATION_KEY = "AAC005";
    public static final String PAY_TYPE_KEY = "cust_order_pay_status";
    public static final String PAY_BANK_KEY = "pay_bank";

    public static final int CODE_TYPE_BIND = 1;
    public static final int CODE_TYPE_UPDATE = 2;
    public static final int CODE_TYPE_REGISTER = 3;
    public static final int CODE_TYPE_RPERSON_INSURANCE = 4;
    public static final int CODE_TYPE_REFIND_PWD = 5;

}
