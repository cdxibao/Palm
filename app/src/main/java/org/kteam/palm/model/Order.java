package org.kteam.palm.model;

import java.io.Serializable;

/**
 * @Package org.kteam.palm.model
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-03-27 17:18
 */
public class Order implements Serializable {
    public Integer id;
    public String cust_user_id;
    public String aadjid;
    public String aadjno;
    public double aac040 = 0.00;
    public int cab182 = 1;
    public double aab180 = 0.00;
    public double aab181 = 0.00;
    public double aab182 = 0.00;
    public double aab183 = 0.00;
    public double total_fee = 0.00;
    public int pay_status = 0;
    public String pay_date;
    public String aac002;
    public String aac003;
    public String type = "0";
    public String jfdk;
    public String phone;
    public String zac001;
    public String zac002;
    public String zac003;
    public String pay_url;
    public String pay_way;
    public String create_date;
    public String category;
    public String yad001_bc;
    public String end_year;
}
