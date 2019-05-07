package org.kteam.palm.activity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.view.ProgressHUD;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.utils.UserStateUtils;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.UserResponse;

import java.util.HashMap;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description 修改密码
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-12 16:31
 */
public class PayActivity extends BaseActivity implements DialogInterface.OnCancelListener {
    private final Logger mLogger = Logger.getLogger(getClass());

    private WebView mWebView;

    private ProgressHUD mProgressHUD;

    private String mPayUrl;
    private boolean mPayedSuccess;
    private String mAddJno = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        initToolBar();
        setTitleText(getString(R.string.pay));

        mPayUrl = getIntent().getStringExtra("payUrl");
        if (TextUtils.isEmpty(mPayUrl)) {
            finish();
            return;
        }
        initView();

        if (mPayUrl.contains("&ORDERID")) {
            String []arr = mPayUrl.split("&");
            for (String str : arr) {
                if (str.startsWith("ORDERID=") && str.contains("")) {
                    mAddJno = str.substring("ORDERID=".length());
                    break;
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressHUD != null) {
            mProgressHUD.dismiss();
        }
    }

    private void initView() {
        mProgressHUD = new ProgressHUD(this);
        mProgressHUD.setOnCancelListener(this);
        mWebView = findView(R.id.web_view);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.contains("pay_android.html?aadjno=")) {
                    String []arr = url.split("aadjno=");
                    if (arr.length == 2) {
                        mAddJno = arr[1];
                        mPayedSuccess = true;
                        loadUserInfo();
                    }
                }
                if (!isFinishing() && !mProgressHUD.isShowing()) {
                    mProgressHUD.show(getString(R.string.loading), true, true, PayActivity.this);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("SUCCESS")) {
                    mLogger.info("payed web back button clicked!");
                    if (url.contains("SUCCESS=Y")) {
                        mPayedSuccess = true;
                        mLogger.info("payed success!");
                        loadUserInfo();
                    } else {
                        mLogger.info("payed failed!");
                        mPayedSuccess = false;
                        toPayResultActivity();
                    }

                } else {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                Log.e("error", "onReceivedHttpError");
            }

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();  // 接受所有网站的证书
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e("error", "onReceivedError");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressHUD.hide();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                mProgressHUD.hide();
            }
        });
//
//        mWebView.setVerticalScrollBarEnabled(false);
//        mWebView.setVerticalScrollbarOverlay(false);
//        mWebView.setHorizontalScrollBarEnabled(false);
//        mWebView.setHorizontalScrollbarOverlay(false);
//        mWebView.setScrollContainer(false);
//        mWebView.setFocusable(true);
//        mWebView.setFocusableInTouchMode(true);

        mWebView.getSettings().setDefaultTextEncodingName("UTF-8") ;
        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.getSettings().setSupportZoom(false);
//        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        mWebView.getSettings().setAllowFileAccess(true);
//        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
//        mWebView.setWebChromeClient(new WebChromeClient());
//
//        HashMap<String, String> paramMap = new HashMap<String, String>();
//        paramMap.put("aadjno", mAadjno);
//        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_SUBMIT_PAY);
//        StringBuilder sb = new StringBuilder();
//        sb.append("aadjno=").append(mAadjno).append("&token=").append(token);

//        mWebView.postUrl(Constants.BASE_URL + Constants.URL_SUBMIT_PAY, EncodingUtils.getBytes(sb.toString(), "BASE64"));
        mWebView.loadUrl(mPayUrl);
    }

    private void loadUserInfo() {

        if (!isFinishing() && !mProgressHUD.isShowing()) {
            mProgressHUD.show(getString(R.string.handling_payed_info), true, true, PayActivity.this);
        }

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("phone", mUser.phone);

        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_LOAD_USER_INFO);
        paramMap.put("token", token);

        RequestClient<UserResponse> requestClient = new RequestClient<UserResponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<UserResponse>() {
            @Override
            public void onLoadComplete(UserResponse response) {
                if (response.code == 0 && response.body != null) {
                    mProgressHUD.hide();
                    mUser.level = response.body.level;
                    mUser.name = response.body.name;
                    mUser.idcard = response.body.idcard;
                    mUser.social_security_card = response.body.social_security_card;
                    UserStateUtils.getInstance().setUser(mUser);
                } else {
                    mProgressHUD.hide();
                }
                toPayResultActivity();
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                toPayResultActivity();
            }
        });
        requestClient.executePost(this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_LOAD_USER_INFO,
                UserResponse.class,
                paramMap);
    }

    private void loadUserInfo1() {

        if (!isFinishing() && !mProgressHUD.isShowing()) {
            mProgressHUD.show(getString(R.string.handling_payed_info), true, true, PayActivity.this);
        }

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("phone", mUser.phone);

        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_LOAD_USER_INFO);
        paramMap.put("token", token);

        RequestClient<UserResponse> requestClient = new RequestClient<UserResponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<UserResponse>() {
            @Override
            public void onLoadComplete(UserResponse response) {
                if (response.code == 0 && response.body != null) {
                    mProgressHUD.hide();
                    mUser.level = response.body.level;
                    mUser.name = response.body.name;
                    mUser.idcard = response.body.idcard;
                    mUser.social_security_card = response.body.social_security_card;
                    UserStateUtils.getInstance().setUser(mUser);
                } else {
                    mProgressHUD.hide();
                }
                finish();
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                finish();
            }
        });
        requestClient.executePost(this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_LOAD_USER_INFO,
                UserResponse.class,
                paramMap);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        loadUserInfo1();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        finish();
    }

    private void toPayResultActivity() {
        Intent intent = new Intent(this, PayResultActivity.class);
        intent.putExtra("aadjno", mAddJno);
        intent.putExtra("payedSuccess", mPayedSuccess);
        startActivity(intent);
    }
}