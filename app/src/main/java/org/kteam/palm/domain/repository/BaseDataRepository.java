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

    /**医疗保险新参保缴费档次*/
    List<BaseData> getYiliaoInsuredPayedLevel();

    /**民族列表*/
    List<BaseData> getNationList();

    /**支付状态列表*/
    List<BaseData> getPayStatusList();

    /**查询银行列表*/
    List<BaseData> getPayBankList();

}
