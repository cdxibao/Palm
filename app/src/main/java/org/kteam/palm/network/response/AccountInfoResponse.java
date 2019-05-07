package org.kteam.palm.network.response;

import org.kteam.common.network.volleyext.BaseResponse;
import org.kteam.palm.model.AccountInfo;

import java.util.List;

/**
 * @Package org.kteam.palm.network.response
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-13 15:29
 */
public class AccountInfoResponse extends BaseResponse {
    public List<AccountInfo> body;
}
