package org.kteam.palm.common.utils;

import android.content.Context;
import android.text.TextUtils;

import org.kteam.common.network.volleyext.BaseResponse;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.BaseApplication;
import org.kteam.palm.R;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.VCodeImgResponse;

import java.util.HashMap;

/**
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2019/7/18 23:32
 * @Copyright: 2018-2020 www.cdxibao.com Inc. All rights reserved.
 */
public class VCodeImageUtils {

    public static void getVCodeImg(Context context, final VCodeImgCallback callback, boolean showLoadingDialog) {
        int width = ViewUtils.dip2px(context, 80);
        int height = ViewUtils.dip2px(context, 30);

        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("unique", SharedPreferencesUtils.getInstance().getUUID());
        paramMap.put("width", String.valueOf(width));
        paramMap.put("height", String.valueOf(height));
        String token = NetworkUtils.getToken(context, paramMap, Constants.URL_VCODE_IMG);
        paramMap.put("token", token);

        RequestClient<VCodeImgResponse> requestClient = new RequestClient<VCodeImgResponse>();
        requestClient.setUseProgress(showLoadingDialog);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<VCodeImgResponse>() {
            @Override
            public void onLoadComplete(VCodeImgResponse response) {
                if (response.code == 0 && response.body != null && !TextUtils.isEmpty(response.body.get("vcode_src"))) {
                    String imgUrl = response.body.get("vcode_src");
                    if (callback != null) {
                        callback.onResult(imgUrl);
                        return;
                    }
                } else {
                    ViewUtils.showToast(BaseApplication.getContext(), response.msg);
                }
                callback.onError();
            }

            @Override
            public void onNetworkError() {
                ViewUtils.showToast(BaseApplication.getContext(), R.string.failed_get_img_code);
                callback.onError();
            }
        });
        requestClient.executePost(context,
                "",
                Constants.BASE_URL + Constants.URL_VCODE_IMG,
                VCodeImgResponse.class,
                paramMap);
    }

    public interface VCodeImgCallback {
        void onResult(String imgUrl);

        void onError();
    }
}
