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
public class PayCalculateYiliaoResult implements Serializable {

    /**缴费单据号*/
    public String aae076;

    /**缴费基数*/
    public String aae180;

    /**缴费月数*/
    public String aae201;

    /**缴费合计*/
    public String yad001;

    /**基本医疗缴费金额*/
    public String yad001_jb;

    /**补充医疗缴费金额*/
    public String yad001_bc;

    /**利息*/
    public String lx;

    /**滞纳金*/
    public String znj;
}
