package org.kteam.palm.network.response;

import java.io.Serializable;

/**
 * @Project Palm
 * @Packate org.kteam.palm.network.response
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2015-12-16 18:01
 * @Version 0.1
 */
public class PayCalculateYanglaoResult implements Serializable {
    /**缴费基数*/
    public String aac040;

    /**缴费月数*/
    public String cab182;

    /**缴费金额*/
    public String aab180;

    /**个人缴费本金*/
    public String aab181;

    /**个人缴费利息*/
    public String aab182;

    /**个人缴费滞纳金*/
    public String aab183;

    /**个体缴费订单号*/
    public String aadjno;

    /**个体缴费单据号*/
    public String aadjid;

    /**缴费工资指数*/
    public String aic110 = "";

    public String payUrl;

}
