package org.kteam.palm.network.response;

import org.kteam.common.network.volleyext.BaseResponse;
import org.kteam.palm.model.UserInfo;

import java.util.List;

/**
 * @Package org.kteam.palm.network.response
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-13 14:44
 */
public class UserInfoResponse extends BaseResponse {
    public List<UserInfo> body;
}