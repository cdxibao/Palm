package org.kteam.common.network.volleyext;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import org.kteam.common.network.volleyext.request.GsonRequest;
import org.kteam.common.network.volleyext.request.UploadRequest;
import org.kteam.common.utils.ViewUtils;
import org.kteam.common.view.ProgressHUD;
import org.kteam.palm.BaseApplication;
import org.kteam.palm.R;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;


/**
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2015-06-16 11:11
 */
public class RequestClient<Response> implements DialogInterface.OnCancelListener {
    private final Logger mLogger = Logger.getLogger(getClass());

    /**
     * 加载完成监听器.
     */
    private OnLoadListener<Response> mOnLoadListener;



    private final String DEFAULT_LOAD_MSG = "正在努力加载...";

    /**
     * 是否使用缓存
     */
    private boolean isUseCache = false;

    /**
     * 是否使用进度条， 默认使用
     */
    private boolean isUseProgress = true;
    private Context mContext;
    private boolean isShowNetworkErrorToast = true;
    private String url = "";
    private ProgressHUD mProgressHUD;

    public boolean isUseCache() {
        return isUseCache;
    }

    public void setUseCache(boolean isUseCache) {
        this.isUseCache = isUseCache;
    }

    public void setUseProgress(boolean isUseProgress) {
        this.isUseProgress = isUseProgress;
    }

    public void setShowNetworkErrorToast(boolean isShowNetworkErrorToast) {
        this.isShowNetworkErrorToast = isShowNetworkErrorToast;
    }

    /**
     * 执行网络请求
     */
    public void executePost(Context mContext,
                            String loadingMsg,
                            String url,
                            Class<Response> responceClass,
                            HashMap<String, String> params) {
        this.url = url;


        if (TextUtils.isEmpty(url))
            return;

        if (mOnLoadListener == null)
            throw new IllegalArgumentException("OnLoadListener is null. You must call setOnLoadListener before.");

        if (isUseProgress)
            showProgressDialog(mContext, loadingMsg);

        this.mContext = mContext;

        doPost(mContext, url, responceClass, params);
    }

    /**
     * 执行网络请求
     */
    public void executePost(Context mContext,
                            String loadingMsg,
                            String url,
                            Class<Response> responceClass,
                            HashMap<String, String> params, HashMap<String, String> headers) {
        this.url = url;


        if (TextUtils.isEmpty(url))
            return;

        if (mOnLoadListener == null)
            throw new IllegalArgumentException("OnLoadListener is null. You must call setOnLoadListener before.");

        if (isUseProgress)
            showProgressDialog(mContext, loadingMsg);

        this.mContext = mContext;

        doPost(mContext, url, responceClass, params, headers);
    }

    /**
     * 执行网络请求
     */
    public void executeGet(Context mContext,
                           String loadingMsg,
                           String url,
                           Class<Response> responceClass) {


        if (TextUtils.isEmpty(url))
            return;

        if (mOnLoadListener == null)
            throw new IllegalArgumentException("OnLoadListener is null. You must call setOnLoadListener before.");

        this.mContext = mContext;

        if (isUseProgress)
            showProgressDialog(mContext, loadingMsg);

        doGet(mContext, url, responceClass);
    }

    /**
     * 执行网络请求
     */
    public void executeFileUpload(Context mContext,
                                  String loadingMsg,
                                  String url,
                                  Class<Response> responceClass,
                                  File file,
                                  HashMap<String, String> params) {


        if (TextUtils.isEmpty(url))
            return;

        if (mOnLoadListener == null)
            throw new IllegalArgumentException("OnLoadListener is null. You must call setOnLoadListener before.");

        this.mContext = mContext;


        if (isUseProgress)
            showProgressDialog(mContext, loadingMsg);


        doPost(mContext, url, responceClass, file, params);
    }


    /**
     * 显示进度条
     *
     * @param mContext
     * @param loadingMsg
     */
    private void showProgressDialog(Context mContext, String loadingMsg) {
        if (mContext == null)
            return;
        if (null == mProgressHUD) {
            if (TextUtils.isEmpty(loadingMsg))
                loadingMsg = DEFAULT_LOAD_MSG;

            mProgressHUD = new ProgressHUD(mContext);
            mProgressHUD.show(loadingMsg, true, true, this);
        }
    }

    /**
     * 发送服务端数据请求,无Dialog模式
     * Post
     *
     * @param url           server url
     * @param responceClass class type of response
     * @param params        request paramter
     */
    public void doPost(Context mContext,
                       String url,
                       Class<Response> responceClass,
                       HashMap<String, String> params) {

        // 执行Http请求
        GsonRequest<Response> request = new GsonRequest<Response>(Method.POST,
                url,
                responceClass,
                params,
                successListener,
                errorListener);

        // 设置连接超时时间
        setRetryPolicy(request);

        RequestManager.addRequest(request, mContext);
    }

    /**
     * 发送服务端数据请求,无Dialog模式
     * Post
     *
     * @param url           server url
     * @param responceClass class type of response
     * @param params        request paramter
     */
    public void doPost(Context mContext,
                       String url,
                       Class<Response> responceClass,
                       HashMap<String, String> params,
                       HashMap<String, String> headers) {

        // 执行Http请求
        GsonRequest<Response> request = new GsonRequest<Response>(Method.POST,
                url,
                responceClass,
                params,
                headers,
                successListener,
                errorListener);

        // 设置连接超时时间
        setRetryPolicy(request);

        RequestManager.addRequest(request, mContext);
    }


    /**
     * 发送服务端数据请求,无Dialog模式
     * Get
     *
     * @param url           server url
     * @param responceClass class type of response
     */
    public void doGet(Context mContext,
                      String url,
                      Class<Response> responceClass) {


        // 执行Http请求
        GsonRequest<Response> request = new GsonRequest<Response>(Method.GET,
                url,
                responceClass,
                null,
                successListener,
                errorListener);

        // 设置连接超时时间
        setRetryPolicy(request);

        RequestManager.addRequest(request, mContext);
    }

    /**
     * 上传文件
     * Post
     *
     * @param url           server url
     * @param responceClass class type of response
     * @param params        request paramter
     */
    public void doPost(Context mContext,
                       String url,
                       Class<Response> responceClass,
                       File file,
                       HashMap<String, String> params) {

        // 执行Http请求
        UploadRequest request = new UploadRequest(url, responceClass, file, params, successListener, errorListener);

        // 设置连接超时时间
        setRetryPolicy(request);


        RequestManager.addRequest(request, mContext);
    }

    /**
     * 上传文件
     * Post
     *
     * @param url           server url
     * @param responceClass class type of response
     * @param params        request paramter
     */
    public void doPost(Context mContext,
                       String url,
                       Class<Response> responceClass,
                       File file,
                       HashMap<String, String> params, HashMap<String, String> headers) {

        // 执行Http请求
        UploadRequest request = new UploadRequest(url, responceClass, file, params, headers, successListener, errorListener);

        // 设置连接超时时间
        setRetryPolicy(request);


        RequestManager.addRequest(request, mContext);
    }


    /**
     * 设置连接重试和timeout
     *
     * @param <T>
     * @param request
     */
    private <T> void setRetryPolicy(Request<T> request) {
        request.setRetryPolicy(new DefaultRetryPolicy(30 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /**
     * 接收服务端响应成功
     */
    private com.android.volley.Response.Listener<Response> successListener = new com.android.volley.Response.Listener<Response>() {

        @Override
        public void onResponse(Response response) {
            if (response instanceof BaseResponse) {
                BaseResponse baseResponse = (BaseResponse) response;
                if (baseResponse.code == -15) { //如果15表示登录失败，则可以再这里处理重连
                    if (mOnLoadListener != null) {
                        mOnLoadListener.onNetworkError();

                        if (isShowNetworkErrorToast) {
                            ViewUtils.showToast(BaseApplication.getContext(), mContext.getResources().getString(R.string.network_error));
                        }
                        //reconnect code
                    }
                } else {
                    if (mOnLoadListener != null) {
                        mOnLoadListener.onLoadComplete(response);
                    }
                }
            } else {
                if (mOnLoadListener != null) {
                    mOnLoadListener.onLoadComplete(response);
                }
            }


            if (mProgressHUD != null) {
                mProgressHUD.dismiss();
            }
        }
    };

    /**
     * 服务端响应或者请求连接失败
     */
    private com.android.volley.Response.ErrorListener errorListener = new com.android.volley.Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {

            if (mOnLoadListener != null) {
                mOnLoadListener.onNetworkError();

                if (isShowNetworkErrorToast) {
                    ViewUtils.showToast(BaseApplication.getContext(), mContext.getResources().getString(R.string.network_error));
                }
                //reconnect code
            }

            if (mProgressHUD != null) {
                mProgressHUD.dismiss();
            }
            mLogger.error(error.getMessage(), error);
        }
    };

    /**
     * 数据加载完成回调监听事件
     *
     * @param <Response> the generic type
     */
    public interface OnLoadListener<Response> {

        /**
         * 数据加载完成
         *
         * @param response the response
         */
        void onLoadComplete(Response response);

        /**
         * 网络出错
         */
        void onNetworkError();
    }

    public OnLoadListener<Response> getOnLoadListener() {
        return mOnLoadListener;
    }

    public void setOnLoadListener(OnLoadListener<Response> onLoadListener) {
        mOnLoadListener = onLoadListener;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mContext != null && mContext instanceof Activity) {
            ((Activity) mContext).finish();
        }
    }
}
