package org.kteam.palm.model;


/**
 * @Package org.kteam.palm.model
 * @Project Palm
 * @Description 包含首页广告 和首页模块
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-05 16:20
 */
public class Module {

    public int id;
    public String sys_flag;
    public int tz_flag;
    public int wz_flag;
    public int sort;
    public String name;

    public static class ModuleType {
        public static final String PERSONAL_CENTER = "ModuleType_PERSONAL_CENTER";
        public static final String QUERY = "ModuleType_QUERY";
        public static final String PAY = "ModuleType_PAY";
        public static final String NEW_INSURED = "ModuleType_NEW_INSURED";
        public static final String BM_SERVICE = "ModuleType_BM_Service";
    }
}