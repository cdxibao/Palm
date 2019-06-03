package org.kteam.palm.domain.repository;

import org.kteam.palm.model.BaseData;

import java.util.List;

/**
 * @Package org.kteam.palm.domain.repository
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-13 22:14
 */
public interface BaseDataRepository extends BaseRepository {

    /**保存数据*/
    void saveList(List<BaseData> list);

    /**月份列表*/
    List<BaseData> getMonthList();

    /**缴费档次*/
    List<BaseData> getPayedLevel();

    /**
     * 缴费档次
     * @param levelKey 当获取的开始月度小于等于4时，去获取档次信息就传ZAC100；当获取的开始月度大于等于5时，去获取档次信息就传ZAC200
     */
    List<BaseData> getPayedLevel(String levelKey);

    /**医疗保险新参保缴费档次*/
    List<BaseData> getYiliaoInsuredPayedLevel();

    /**民族列表*/
    List<BaseData> getNationList();

    /**支付状态列表*/
    List<BaseData> getPayStatusList();

    /**查询银行列表*/
    List<BaseData> getPayBankList();

}
