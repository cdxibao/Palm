package org.kteam.palm.network.response;

import org.kteam.common.network.volleyext.BaseResponse;
import org.kteam.palm.model.BaseData;

import java.util.List;

/**
 * @Package org.kteam.palm.network.response
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-13 22:46
 */
public class BaseDataResponse extends BaseResponse {
    public List<BaseData> body;
}
