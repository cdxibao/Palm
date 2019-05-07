package org.kteam.palm.network.response;

import org.kteam.common.network.volleyext.BaseResponse;
import org.kteam.palm.model.Ad;

import java.util.List;

/**
 * @Package org.kteam.palm.network.response
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-05 16:26
 */
public class AdvertisementReponse extends BaseResponse {
    public List<Ad> body;
}
