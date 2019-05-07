package org.kteam.palm.common.utils.ccbface.controller;

import android.content.Context;

import com.lidroid.xutils.http.callback.RequestCallBack;

import org.apache.http.client.CookieStore;
import org.kteam.palm.common.utils.ccbface.HttpXutils;
import org.kteam.palm.common.utils.ccbface.LoadingDialogUtils;
import org.kteam.palm.common.utils.ccbface.constant.HostAddress;
import org.kteam.palm.common.utils.ccbface.entity.BaseReq;
import org.kteam.palm.common.utils.ccbface.entity.FileUploadEntity;
import org.kteam.palm.common.utils.ccbface.entity.SecurityReqBody;

import java.io.File;


public class MainController {
   private static MainController controller;

    public static MainController getInstance(){
        if(controller == null){
            controller = new MainController();
        }
        return controller;
    }
    public void get(Context context, BaseReq params, RequestCallBack callBack) {
        LoadingDialogUtils.getInstance().showLoading(context);
        HttpXutils.getInstance().get(context, HostAddress.host,params, callBack);
    }

    public void postSecurity(Context context, BaseReq params, SecurityReqBody securityReqBody, RequestCallBack callBack){
        LoadingDialogUtils.getInstance().showLoading(context);
        HttpXutils.getInstance().postEncode(context,HostAddress.host, params,securityReqBody,callBack);
    }

    public void post(Context context, BaseReq params, RequestCallBack callBack) {
        LoadingDialogUtils.getInstance().showLoading(context);
        HttpXutils.getInstance().post(context,HostAddress.host, params,callBack);
    }

    public void uploadFiles(CookieStore cookieStore, File imageFile, BaseReq params, RequestCallBack requestCallBack){
        String url = HostAddress.pichost+((FileUploadEntity)params).ACTION;
        HttpXutils.getInstance().uploadFiles(cookieStore,url,imageFile, params,requestCallBack);
    }
}
