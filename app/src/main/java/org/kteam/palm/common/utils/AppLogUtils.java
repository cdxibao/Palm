package org.kteam.palm.common.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.palm.BaseApplication;
import org.kteam.palm.R;
import org.kteam.palm.model.User;
import org.kteam.palm.model.UserInfo;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.UserInfoResponse;

import java.util.HashMap;

/**
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2017/8/28 23:46
 */
public class AppLogUtils {
    public static final String YWLX_FW = "1";
    public static final String YWLX_DL = "2";

    public static void userLog(String ywlx) {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        final User user = UserStateUtils.getInstance().getUser();
        if (user != null) {
            paramMap.put("userId", String.valueOf(user.id));
            paramMap.put("sfz", user.idcard);
            paramMap.put("xm", user.name);
        }
        Context context = BaseApplication.getContext();
        paramMap.put("openId", IPUtils.getIpAddress(context));
        paramMap.put("port", Constants.FIRST_SOURCE);
        paramMap.put("ywlx", ywlx);//1-访问 2-登录
        String token = NetworkUtils.getToken(context, paramMap, Constants.URL_USER_LOGGER);
        paramMap.put("token", token);

        RequestClient<UserInfoResponse> requestClient = new RequestClient<UserInfoResponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<UserInfoResponse>() {
            @Override
            public void onLoadComplete(UserInfoResponse response) {
                if (response.code == 0) {

                }
            }

            @Override
            public void onNetworkError() {

            }
        });
        requestClient.executePost(context,
                "",
                Constants.BASE_URL + Constants.URL_USER_LOGGER,
                UserInfoResponse.class,
                paramMap);
    }
}
