package org.kteam.palm.model;

import java.io.Serializable;

/**
 * @Package org.kteam.palm.model
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-06 00:02
 */
public class User implements Serializable {
    public int id;
    public String phone = "";
    public String passwd;
    public int sex;
    public int level;
    /**个人编码*/
    public String social_security_card = "";
    public String open_id = "";
    public String name = "";
    /**身份证*/
    public String idcard = "";
    public String address = "";
    public String companyId = "";
    public String companyName = "";
    public String insuranceOrg = "";
}
