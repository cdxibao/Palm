package org.kteam.palm.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.icbc.paysdk.view.IndicatorView;
import com.icbc.paysdk.webview.CustomWebView;
import com.icbc.paysdk.webview.ICBCWebViewClient;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.kteam.palm.MainActivity;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2017/11/28 3:08
 */
public class ICBCPayActivity extends Activity {

    private CustomWebView mWebView;
    private LinearLayout mLoadingIndicator;
    private LinearLayout mLoadingError;
    TextView error_msg;
    ImageView error_image;
    Button returnButton;
    String error_type = "";
    private HashMap<String, String> startB2CParams;
    private String startB2CType;
    private String startB2CURL = "http://122.19.125.55:10790/ICBCWAPBank/servlet/ICBCWAPEBizServlet";
    ICBCPayActivity.NativeWebViewCommonProxy nativeProxy;
    private Handler mHandler = new Handler();
    private ICBCWebViewClient webViewClient = new ICBCWebViewClient() {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return url.startsWith("mailto:") || url.startsWith("tel:");
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.i("paySDK", "WebView --- onPageStarted()");
            ICBCPayActivity.this.mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.i("paySDK", "WebView --- onPageFinished()");
            ICBCPayActivity.this.mLoadingIndicator.setVisibility(View.GONE);
        }

        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            ICBCPayActivity.this.mLoadingIndicator.setVisibility(View.GONE);
            ICBCPayActivity.this.mWebView.setVisibility(View.GONE);
            ICBCPayActivity.this.error_type = "SSLError";
            Toast.makeText(ICBCPayActivity.this, "支付失败，请稍后再试。", Toast.LENGTH_SHORT).show();
            ICBCPayActivity.this.back(ICBCPayActivity.this.error_type);
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            ICBCPayActivity.this.mLoadingIndicator.setVisibility(View.GONE);
            ICBCPayActivity.this.mWebView.setVisibility(View.GONE);
            ICBCPayActivity.this.error_type = "ReceivedError";
            Toast.makeText(ICBCPayActivity.this, "支付失败，请稍后再试。", Toast.LENGTH_SHORT).show();
            ICBCPayActivity.this.back(ICBCPayActivity.this.error_type);
        }

        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            ICBCPayActivity.this.mLoadingIndicator.setVisibility(View.GONE);
            ICBCPayActivity.this.mWebView.setVisibility(View.GONE);
            ICBCPayActivity.this.error_type = "HttpAuthRequestError";
            Toast.makeText(ICBCPayActivity.this, "支付失败，请稍后再试。", Toast.LENGTH_SHORT).show();
            ICBCPayActivity.this.back(ICBCPayActivity.this.error_type);
        }
    };
    private View.OnClickListener returnButtonOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            ICBCPayActivity.this.back(ICBCPayActivity.this.error_type);
        }
    };

    public ICBCPayActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initViewAndData();
        this.startFirstRequest();
    }

    private void startFirstRequest() {
        if(!this.checkB2CData()) {
            this.finish();
        } else {
            this.startB2CTransaction();
        }
    }

    private void initViewAndData() {
        LinearLayout linearLayout1 = new LinearLayout(this);
        LinearLayout.LayoutParams linearLayout1_params = new LinearLayout.LayoutParams(-1, -1);
        linearLayout1.setLayoutParams(linearLayout1_params);
        FrameLayout body = new FrameLayout(this);
        android.widget.FrameLayout.LayoutParams body_params = new android.widget.FrameLayout.LayoutParams(-1, -1);
        body_params.gravity = 112;
        body.setLayoutParams(body_params);
        body.setBackgroundColor(Color.parseColor("#00000000"));
        CustomWebView WebViewCore = new CustomWebView(this);
        android.view.ViewGroup.LayoutParams WebViewCore_param = new android.view.ViewGroup.LayoutParams(-1, -1);
        WebViewCore.setHorizontalScrollBarEnabled(false);
        WebViewCore.setVerticalScrollBarEnabled(false);
        WebViewCore.setLayoutParams(WebViewCore_param);
        LinearLayout LoadingIndicator = new LinearLayout(this);
        LinearLayout.LayoutParams LoadingIndicator_params = new LinearLayout.LayoutParams(-1, -1);
        LoadingIndicator_params.weight = 1.0F;
        LoadingIndicator.setLayoutParams(LoadingIndicator_params);
        LoadingIndicator.setBackgroundColor(Color.parseColor("#00000000"));
        LoadingIndicator.setClickable(true);
        LoadingIndicator.setFocusable(true);
        LoadingIndicator.setFocusableInTouchMode(true);
        LoadingIndicator.setGravity(17);
        LoadingIndicator.setOrientation(LinearLayout.VERTICAL);
        LoadingIndicator.setVisibility(View.VISIBLE);
        IndicatorView indicatorView = new IndicatorView(this);
        RelativeLayout indicator = indicatorView.getIndicatorView();
        LoadingIndicator.addView(indicator);
        LinearLayout LoadingError = new LinearLayout(this);
        LinearLayout.LayoutParams LoadingError_params = new LinearLayout.LayoutParams(-1, -1);
        LoadingError_params.weight = 1.0F;
        LoadingError.setLayoutParams(LoadingError_params);
        LoadingError.setBackgroundColor(Color.parseColor("#00000000"));
        LoadingError.setClickable(true);
        LoadingError.setFocusable(true);
        LoadingError.setFocusableInTouchMode(true);
        LoadingError.setGravity(17);
        LoadingError.setOrientation(LinearLayout.VERTICAL);
        LoadingError.setVisibility(View.GONE);
        body.addView(WebViewCore);
        body.addView(LoadingIndicator);
        body.addView(LoadingError);
        linearLayout1.addView(body);
        this.setContentView(linearLayout1);
        this.mWebView = WebViewCore;
        this.mLoadingIndicator = LoadingIndicator;
        this.nativeProxy = new ICBCPayActivity.NativeWebViewCommonProxy(this);
        WebChromeClient mWebChromeClient = new WebChromeClient();
        this.mWebView.setWebChromeClient(mWebChromeClient);
        this.mWebView.setWebViewClient(this.webViewClient);
        this.mWebView.addJavascriptInterface(this.nativeProxy, "RequestService");
        this.mWebView.addJavascriptInterface(this.nativeProxy, "Native");
        this.mWebView.addJavascriptInterface(this.nativeProxy, "PortalRequestService");
        this.mWebView.addJavascriptInterface(this.nativeProxy, "androidNativeProxy");
    }

    private void startB2CTransaction() {
        this.startB2CWebViewRequest();
    }

    private void startB2CWebViewRequest() {
        String params = this.makeParamsStringForPortal(this.startB2CParams);
        this.mWebView.postUrl(this.startB2CURL, EncodingUtils.getBytes(params, "BASE64"));
    }

    private boolean checkB2CData() {
        try {
            this.startB2CParams = (HashMap)this.getIntent().getSerializableExtra("startB2CParams");
            if (this.startB2CParams != null) {
                this.startB2CURL = this.startB2CParams.get("url");
            }
            this.startB2CType = this.getIntent().getStringExtra("startB2CType");
            if(this.startB2CParams != null && this.startB2CType != null && "normal".equals(this.startB2CType)) {
                String interfaceName = (String)this.startB2CParams.get("interfaceName");
                if(!"ICBC_WAPB_B2C".equals(interfaceName)) {
                    return false;
                }

                String interfaceVersion = (String)this.startB2CParams.get("interfaceVersion");
                if(!"1.0.0.6".equals(interfaceVersion) && !"1.0.0.7".equals(interfaceVersion)) {
                    return false;
                }

                String netType = (String)this.startB2CParams.get("netType");
                if(!"15".equals(netType) && !"56".equals(netType)) {
                    return false;
                }

                String token = (String)this.startB2CParams.get("token");
                if(!"NewB2C".equals(token)) {
                    return false;
                }
            }

            return true;
        } catch (Exception var5) {
            return false;
        }
    }

    public String makeParamsStringForPortal(HashMap<String, String> paramsMap) {
        String result = "";
        int i = 0;
        Iterator var5 = paramsMap.keySet().iterator();

        while(var5.hasNext()) {
            String key = (String)var5.next();

            try {
                result = result + key + "=" + URLEncoder.encode((String)paramsMap.get(key), "UTF-8");
            } catch (Exception var7) {
                result = result + key + "=" + (String)paramsMap.get(key);
            }

            ++i;
            if(i != paramsMap.size()) {
                result = result + "&";
            }
        }

        return result;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        if(keyCode == 4 && event.getRepeatCount() == 0) {
            this.back(this.error_type);
            return false;
        } else {
            return false;
        }
    }

    public void back(String errorType) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        super.finish();

    }

    class NativeWebViewCommonProxy {
        private Context context;

        public NativeWebViewCommonProxy(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void showIndicator() {
            Log.i("paySDK", "WebView --- showIndicator()");
            ICBCPayActivity.this.mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @JavascriptInterface
        public void hideIndicator() {
            Log.i("paySDK", "WebView --- hideIndicator()");
            ICBCPayActivity.this.mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @JavascriptInterface
        public void returnToMerchant(final String result) {
            Log.i("paySDK", "WebView --- returnToMerchant(String result) -- " + result);
            ICBCPayActivity.this.mHandler.post(new Runnable() {
                public void run() {
                    String tranCode = "";
                    String tranMsg = "";
                    String orderNo = "";

                    try {
                        JSONObject json = new JSONObject(result);
                        tranCode = json.getString("tranCode");
                        tranMsg = json.getString("tranMsg");
                        orderNo = json.getString("orderNo");
                        Log.i("paySDK", "--tranCode = " + tranCode + "--tranMsg = " + tranMsg + "--orderNo = " + orderNo);
                    } catch (JSONException var6) {
                        var6.printStackTrace();
                    }

                    Intent intent = new Intent();
                    intent.setAction("com.icbc.pay.PayResultHandler.SHOW_ACTIVITY");
                    intent.putExtra("type", "onResp");
                    intent.putExtra("tranCode", tranCode);
                    intent.putExtra("tranMsg", tranMsg);
                    intent.putExtra("orderNo", orderNo);
                    intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                    ICBCPayActivity.NativeWebViewCommonProxy.this.context.startActivity(intent);
                }
            });
        }
    }
}