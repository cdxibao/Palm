package org.kteam.palm.common.utils.ccbface;

import android.content.Context;
import android.util.Base64;

import com.ccb.crypto.tp.tool.eSafeLib;
import com.lidroid.xutils.util.LogUtils;

import java.io.UnsupportedEncodingException;

/**
 * Created by wutw on 2018/1/4 0004.
 */
public class EsafeUtils {
    //E护航APPKEY 测试
    public static String eSafeKey = "Lgbdq3WUKhBnL1wa1KaX9mrS2J0Bs3rhREis1BJNfv33/a94emw0IBSq3LVhOJ7X9pz2ez7sakzeYuqWh1CZiMciVt6UlF5I12WkEpkJN/RJ22CXYFu4GmVs1tpCXyTt/E44g/HX4eecnzORG6NZADdYS0af/m1C8XN0Rmomr+taadaReSTQdC5XjaUb8tfdtvesA7P2C4+GJrCsEsrJYDOBwfW+Xsqo+ME/wGOlPRzfcmrz4nQOU5SJ1Y3vChek2IHJZwqcrUh4FqHimmil5kupVwBq/TOwVGqcHcDjAPD06J12pQWEb9Y9JaA1c31/DVr9PKImz2V8AovU9DwkuME6gWLw7XFYkmssehabPk6FbcEL3LyH2PkmX4dLKgxkrEmNg5ssCX24b+lgnW4l1lWmsKZKaPwGBWFIuKUSfQKeg+HKEUveriJfZK6gpQRqbXXmRaOZU+htNXHVf9tVYNz+6XnRD95Z7/eu6NHcHumUY1NHfPdzrsG1PwL9YT32LOeTO+ZfkFqfXBD2LrR5hbVBEN7i6GEnZcHq1EAV8VyKbKAHAFSl4bAfNOD1LuJ6fFGYN5Gwkp1dw5ofMAR6/FphknogGvL6aYxUj9YKdKtgkYDQUNiPco1lhb0g1X7gO9jtRc73XkQO9CAhMuVoOh1351bHhWITlfcmZKsAsR0rj8GEr7xU+OX+XtWZq9qA84HfUX0feSsFFuUq3OO+zfUZhpkQPYhojQ0hwVY1n9HeEw9zzX6qVQlEiqHy4J5lJltLyl0TAlrpbFIlp0htiAe1zA/A6pb6oeUba/B73OM9Bf6xTYNYTH62EiTtRVV03mqNwcLIR4rOLLw1wlLu09ng6VPQAhyz+TRnIu9rRLwTpku/itk3xl2Z4dLG5tS0FlFsswtEyjwfCmCHIR5vAYYde4s2LTrqTPLYsm3viwRZLpeeBoviKDreYxvHJKP9Jscut//lUCh6sjXrr1crzuvAIdINkQB5Pf+9Sa/7RuK4kWHUVPdzkcMari1EdQcV+TjN5rPK1tSJIHK94JBUxlExVKMgVthDriuC+Bd/LGzJBLMJn8nNp76Pycnq8E8ZI4qKMh6VBytH4GLbnV9U25BlMCvRHqZBhcWDJK5lImztKSiZS8VOvz3zt71I8L2MC9TjymhX3YEzpfSvhOh35mrayYha/K8w/OeNu82MeprIcJhl8V0OhGuIkbdcIuiNQErv6finGgeT7VwyA1AA74T6ynQZ5bap2Ys3ZcrmzIpjpdH+EY+fdJlqQEqkD+OZ9oei8MClfhtQaMi2eaXpi96iV9zrl1NFSwYyuUr7q/tGiIgx6DMo4TUPLmscRIddZVxQXEkJ7dbnCyjERYB4a6pjPMjgrnGlbMLN9UObq+A6Fy027MPdj48VOjQadaaaH1Mr+G97NaGkAsPqByJbN+eAONtdR9+L89WchV/W5x7O/ZYfN85xkePJ+25zBU+ydEHGR7svR0CTSqftfjQ+zlZQkkGykgrW/Tb60miYiPyOPzirNzDHHR/BIS1ev5PBN8rQXA4WSjoNAifLgAU2VvyYKJQvJczODB4eDQ1jSQXPLRN9mvY8obfL2Pn6LsTGZJm1P5100AlFLAY7kkydaa1eBPjsoYq7mtebn1uA4tYl+9rGda6CS2OM3ISKTf+KqtB6B8aHt+pZ+a1UWUAQrDAf7EEbsu1KANglZHiKFMnoHpOLRSYaaJIuHNsrRukvazqvZ3YhH06XPfYYBSHzKET1LunSnhKrU3dLrWu4TmQC4vJ/llLdLtldssE6tGNR";
    //声明并初始化对象
    private static eSafeLib meSafeLib;
    /**
     * 获取eSafeLib
     */
    public static eSafeLib getESafeLib(Context context){
        if(null==meSafeLib) {
            meSafeLib = new eSafeLib(context, eSafeKey);
            meSafeLib.verify();
            meSafeLib.initGpsService();
        }
        return meSafeLib;
    }
    /**
     * 密文=接口发送字段1=value1&接口发送字段2=value2&...&接口发送字段n=valuen&reqTime=value&SYS_CODE=value&MP_CODE=value&APP_NAME=value&SEC_VERSION=1.0.0.0
     */
    // 用E路护航加密处理发送的报文。
    public static String makeESafeData(Context context, String param) {
        if (param == null)
            return null;
        int flag = 0;
        // param = addESafeCommonPara(param);
        LogUtils.d("加密前参数：" + param);
        String retParams = new String();
        try {
            String COMMPKG = Base64.encodeToString(param.getBytes("utf-8"), Base64.DEFAULT);
            LogUtils.d("base64编码后参数： " +COMMPKG );
            COMMPKG = addESafeCommonPara(COMMPKG);
            LogUtils.d( "base64编码后，一路护航加密前参数：" + COMMPKG);

            //声明并初始化对象
            eSafeLib safe = getESafeLib(context);
            if(safe.verify()){
                //调用加密接口
                String cipher = safe.tranEncrypt(COMMPKG);
                retParams = cipher;
               /* LogUtils.i("e护航加密后参数："+cipher);
                retParams =  URLEncoder.encode(cipher,"utf-8");*/
            }

        } catch (UnsupportedEncodingException e) {
            if (LogUtils.allowI) {
                e.printStackTrace();
            }
        }
        return retParams;

    }

    /**
     * 给参数数据添加E路护航的参数
     *
     * @return reqTime=100203040&SYS_CODE=0130&MP_CODE=01&APP_NAME=网点导航&SEC_CODE=1.0.0.0 1、
     * 系统时间差(reqTime) 系统时间差=客户端时间—服务器时间 2、 业务系统编号(SYS_CODE) 网点服务管理系统 0030 CQSM 国际互联网网站系统 0060 CCB.COM
     * 家居银行系统 0111 TVBS 渠道分析系统 0120 CAS 网上银行系统 0130 EBS 呼叫中心系统 0140 CCC 电话支付系统 0141 ETB 自助业务运营控管系统
     * 0151 ATMS 本外币一体化商户收单系统 0160 IABS 企业级渠道服务整合系统 0250 ECTIP 总行外汇清算系统 0530 MPS 外汇业务信息管理系统 0540 FIMS
     * 海外核心系统 0550 OCBS 总行短信系统 0750 SMS 手机银行系统 0760 MBS 海外业务操作型数据管理系统 0820 ODAS CA发证管理系统 0970 CAMS
     * 电子商务平台 1100 ECP
     *
     * 3、 移动平台代码(MP_CODE) Android：01 IOS：02 WP：03 4、 客户端名称(APP_NAME) 调用开发平台提供方法，获取客户端名称。
     * IOS：bunldeName Android:(android:name) 5、安全模块版本号(SEC_VERSION) 使用E路护航提供的方法，获取E路护航版本号，参见E路护航安全组件使用方法文档。
     */
    private static String addESafeCommonPara(String initData, int flag) {
        if (initData == null)
            return initData;
        if (flag == 100) {
            initData = "SYS_CODE=0130&MP_CODE=01&APP_NAME=com.atm&SEC_VERSION=1.0&" + initData;
        }else {
            initData = "&TXCODE=&BRANCHID=&USERID=&COMMPKG=" + initData;
        }
        return initData;
    }

    private static String addESafeCommonPara(String initData) {
        if (initData == null)
            return initData;

        initData = "&TXCODE=&BRANCHID=&USERID=&COMMPKG=" + initData;
        return initData;
    }
}

