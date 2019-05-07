package org.kteam.palm.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ccb.crypto.tp.tool.eSafeLib;
import com.ccb.js.CcbAndroidJsInterface;
import com.intsig.idcardscan.sdk.ISCardScanActivity;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.tendyron.facelib.impl.FacelibInterface;
import com.tendyron.ocrlib.impl.OcrlibInterface;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EncodingUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.APKUtils;
import org.kteam.palm.common.utils.ccbface.DateUtils;
import org.kteam.palm.common.utils.ccbface.DeviceUtils;
import org.kteam.palm.common.utils.ccbface.EsafeUtils;
import org.kteam.palm.common.utils.ccbface.FileUtils;
import org.kteam.palm.common.utils.ccbface.JsonUtils;
import org.kteam.palm.common.utils.ccbface.LoadingDialogUtils;
import org.kteam.palm.common.utils.ccbface.base.OcrlibInterfaceImpl;
import org.kteam.palm.common.utils.ccbface.constant.Global;
import org.kteam.palm.common.utils.ccbface.constant.HostAddress;
import org.kteam.palm.common.utils.ccbface.controller.MainController;
import org.kteam.palm.common.utils.ccbface.entity.BankEntity;
import org.kteam.palm.common.utils.ccbface.entity.FileUploadEntity;
import org.kteam.palm.common.utils.ccbface.entity.IDCardEntity;
import org.kteam.palm.common.utils.ccbface.entity.SecurityReq;
import org.kteam.palm.common.utils.ccbface.entity.SecurityReqBody;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2018/8/23 23:10
 * @Copyright: 2018-2020 www.cdxibao.com Inc. All rights reserved.
 */
public class CcbFaceActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {
    private final Logger mLogger = Logger.getLogger(getClass());

    Context mContext;
    private WebView myWebView = null;
    //是否使用本地的测试代码
    private boolean isUseLocal = false;
    //初始化JS键盘对象
    CcbAndroidJsInterface ccbAndroidJsInterface;
    //人脸识别object
    FacelibInterface face;
    //人脸识别保存图片
    String photo = "";
    //安全中心请求
    SecurityReq securityReq;
    //身份认证请求
    SecurityReqBody securityReqBody;
    //网络请求返回结果
    String result = "";
    //OCR扫描类对象
    OcrlibInterface ocr;
    /**
     * 启动身份证正面扫描请求码
     */
    public static final int REQUEST_CODE_IDCARD_SIDE_FRONT = 1;
    /**
     * 启动身份证背面扫描请求码
     */
    public static final int REQUEST_CODE_IDCARD_SIDE_BACK = 2;
    /**
     * 启动银行卡扫描请求码
     */
    public static final int REQUEST_CODE_BANK_CARD = 3;
    /**
     * 身份证数据
     */
    private IDCardEntity idCardEntity;
    /**
     * 银行卡数据
     */
    private BankEntity bankEntity;
    /**
     * 身份证数据
     */
    byte[] imageBytesFront;
    byte[] imageBytesBack;
    /**
     * 身份证头像
     */
    String cardFace = "";

    /**
     * 保存图片地址
     */
    JSONObject jsonPicPath = new JSONObject();
    /**
     * 文件上传参数
     */
    FileUploadEntity fileUploadEntity;
    /**
     * 文件名
     */
    String fileNm = "";
    /**
     * 保存WEBVIEW cookie
     */
    CookieStore cookieStore = new BasicCookieStore();
    //声明并初始化对象
    eSafeLib safe;
    public static final int WEBVIEW_ACTIVITY_CODE = 4;
    //读取手机状态请求码
    private static final int READ_PHONE_STATE_CODE = 5;
    //手机相机权限请求码
    private static final int OCR_CAMERA_REQUEST_CODE = 6;
    //手机相机权限请求码
    private static final int FACE_CAMERA_REQUEST_CODE = 7;

    private ProgressBar mProgressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        face = com.tendyron.facelib.impl.IBank.getInstance(this);

        ocr = OcrlibInterfaceImpl.getInstance(this);
        ocr.ocrService("", "a6a5b2979348acdea5fd004551-ppo");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_ccbface);
        mContext = this;
        myWebView = (WebView) findViewById(R.id.webView);
        //获取当前Activity的Layout
        LinearLayout mainActivityLayout = (LinearLayout) findViewById(R.id.mainActivity);
        //初始化JS键盘对象，传入当前Activity的this对象
        ccbAndroidJsInterface = new WebAppInterface(mContext.getApplicationContext(), mainActivityLayout, myWebView);
        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.ll_back).setOnClickListener(this);

        initWebView();
        String[] perms = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(getApplication(), perms)) {
            EasyPermissions.requestPermissions(CcbFaceActivity.this, getString(R.string.permission_ccb), READ_PHONE_STATE_CODE, perms);
        }
//        DeviceUtils.getSignature(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.getInstance().sync();
        } else {
            CookieManager.getInstance().flush();
        }
    }

    public void initWebView() {
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        // 得到设置属性的对象
        WebSettings webSettings = myWebView.getSettings();
        // 使能JavaScript
        webSettings.setJavaScriptEnabled(true);

        // 支持中文，否则页面中中文显示乱码
        // 设置字符集编码
        webSettings.setDefaultTextEncodingName("UTF-8");
        //支持js调用window.open方法
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //webview 支持打开多窗口
//        webSettings.setSupportMultipleWindows(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webSettings.setBuiltInZoomControls(true);// 设置缩放工具
        webSettings.setDisplayZoomControls(false);// 不显示webview缩放按钮
        webSettings.setSupportZoom(true);// 设置支持缩放
        webSettings.setDefaultFontSize(18);//设置字体大小


        // 网页自适应大小
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
//        webSettings.setLoadWithOverviewMode(true);

      /*  DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int mDensity = metrics.densityDpi;

        if (mDensity == 120) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        }else if (mDensity == 160) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        }else if (mDensity == 240) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        }*/

        // 禁用 file 协议；
        webSettings.setAllowFileAccess(false);
        webSettings.setAllowFileAccessFromFileURLs(false);
        webSettings.setAllowUniversalAccessFromFileURLs(false);
        //Http和Https混合问题
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }*/
        myWebView.setHorizontalScrollBarEnabled(false);//禁止水平滚动
        myWebView.setVerticalScrollBarEnabled(true);//允许垂直滚动

        // 限制在WebView中打开网页，而不用默认浏览器
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                mLogger.error("polling:shouldInterceptRequest request url:" + request.getUrl().toString());
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String cookiesString = CookieManager.getInstance().getCookie(url);
                if (null != cookiesString) {
                    mLogger.debug("polling " + cookiesString);
                    String[] cookies = cookiesString.split(";");
                    for (String cookie : cookies) {
                        String[] cook = cookie.split("=");
                        if (null != cook && cook.length > 1) {
                            Cookie cookie1 = new BasicClientCookie(cook[0], cook[1]);
                            cookieStore.addCookie(cookie1);
                        }
                    }
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mLogger.error("polling:shouldOverrideUrlLoading request url:" + url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                mLogger.error("polling:shouldOverrideUrlLoading request url:" + request.getUrl().toString());
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                mLogger.error("polling:onReceivedSslError");
                handler.proceed();
//                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                mLogger.error("polling:onReceivedError");
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });

        // 如果不设置这个，JS代码中的按钮会显示，但是按下去却不弹出对话框
        // Sets the chrome handler. This is an implementation of WebChromeClient
        // for use in handling JavaScript dialogs, favicons, titles, and the
        // progress. This will replace the current handler.
        myWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                // TODO Auto-generated method stub
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);

                }
                super.onProgressChanged(view, newProgress);
            }
        });


        myWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(myWebView, true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        // 用JavaScript调用Android函数：
        // 先建立桥梁类，将要调用的Android代码写入桥梁类的public函数
        // 绑定桥梁类和WebView中运行的JavaScript代码
        // 将一个对象起一个别名传入，在JS代码中用这个别名代替这个对象，可以调用这个对象的一些方法CcbAndroidJsInterface.CCB_JS_OBJECT
//        myWebView.addJavascriptInterface(ccbAndroidJsInterface,"identification");
        myWebView.addJavascriptInterface(ccbAndroidJsInterface, CcbAndroidJsInterface.CCB_JS_OBJECT);
        // 载入页面：本地html资源文件
        if (isUseLocal) {
            myWebView.loadUrl("file:///android_asset/www/index.html");
        } else {
            String url = getIntent().getStringExtra("url");
//            myWebView.loadUrl(HostAddress.postUrl);
            myWebView.postUrl(url, EncodingUtils.getBytes(HostAddress.params, Global.DEFAULT_ENCORD));
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {

        if (R.id.ll_back == v.getId()) {
            finish();
        } else if (R.id.button == v.getId()) {
            /*initESafe();
            initSecurityReq("{\n" +
                    "                    \"StrUsInd_2\":\"2\",\n" +
                    "                    \"Cst_Nm\":\"马零五\",\n" +
                    "                    \"Crdt_TpCd\":\"1010\",\n" +
                    "                    \"Crdt_No\":\"210250193710096711\",\n" +
                    "                    \"Cst_ID\":\"015190000103192241\",\n" +
                    "                    \"CHNL_CUST_NO\":\"210250193710096711\",\n" +
                    "                    \"Gnd_Cd\":\"\",\n" +
                    "                    \"Ethnct_Cd\":\"\",\n" +
                    "                    \"Brth_Dt\":\"\",\n" +
                    "                    \"AvlDt_Dt\":\"\",\n" +
                    "                    \"AvlDt_EdDt\":\"\",\n" +
                    "                    \"Inst_Chn_FullNm\":\"\",\n" +
                    "                    \"Dtl_Adr\":\"\",\n" +
                    "                    \"Sign_OrCd\":\"\",\n" +
                    "                    \"base64_Ecrp_Txn_Inf\":\"\",\n" +
                    "                    \"SYS_CODE\":\"0250\",\n" +
                    "                    \"BRANCHID\":\"211000000\",\n" +
                    "                    \"base64_Ecrp_Txn_Inf\":\"\"\n" +
                    "                }");
            sendSecurityReq();*/
        }


    }

    /**
     * 自定义的Android代码和JavaScript代码之间的桥梁类
     *
     * @author 1
     */
    public class WebAppInterface extends CcbAndroidJsInterface {

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context context, LinearLayout activityLayout, WebView webView) {
            super(context, EsafeUtils.eSafeKey, activityLayout, webView);
        }

        /**
         * Show a toast from the web page
         */
        // 如果target 大于等于API 17，则需要加上如下注解
        @JavascriptInterface
        public void showToast(String toast) {
            Toast tost = Toast.makeText(mContext, toast, Toast.LENGTH_LONG);
            tost.show();
        }

        /**
         * 初始化ESAFE
         */
        @JavascriptInterface
        public void createESafe(String json) {
            initESafe();
        }

        /**
         * S人脸识别
         */
        // 如果target 大于等于API 17，则需要加上如下注解
        @JavascriptInterface
        public void scanFace(String... toast) {
            String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (EasyPermissions.hasPermissions(getApplication(), perms)) {
                startfaceScan();
            } else {
                EasyPermissions.requestPermissions(CcbFaceActivity.this, getString(R.string.permission_camera_desc), FACE_CAMERA_REQUEST_CODE, perms);
            }
           /* if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED){
                startfaceScan();
            }else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 2);
            }*/
        }

        /* 发送网关安全请求1*/
        @JavascriptInterface
        public void requestFaceInfo(String json) {
            initSecurityReq(json);
            sendSecurityReq();
        }

        /* 发送网关安全请求02*/
        @JavascriptInterface
        public void requestFaceInfoZX02(String json) {
            initSecurityReq(json, "02");
            sendSecurityReq();
        }

        /* 返回人脸扫描结果*/
        @JavascriptInterface
        public void sendParams(final String params) {
            //网络请求参数
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myWebView.loadUrl("javascript:scanFaceResult('" + params + " success" + "')");
                }
            });
        }

        /* 扫描身份认证请求*/
        @JavascriptInterface
        public void scanIdCardFront() {
            String[] perms = {Manifest.permission.CAMERA};
            if (EasyPermissions.hasPermissions(getApplication(), perms)) {
                scanIDCard(REQUEST_CODE_IDCARD_SIDE_FRONT);
            } else {
                EasyPermissions.requestPermissions(CcbFaceActivity.this, getString(R.string.permission_camera_desc), REQUEST_CODE_IDCARD_SIDE_FRONT, perms);
            }
        }

        /* 扫描身份认证请求*/
        @JavascriptInterface
        public void scanIdCardBack() {
            String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (EasyPermissions.hasPermissions(getApplication(), perms)) {
                scanIDCard(REQUEST_CODE_IDCARD_SIDE_BACK);
            } else {
                EasyPermissions.requestPermissions(CcbFaceActivity.this, getString(R.string.permission_camera_desc), REQUEST_CODE_IDCARD_SIDE_BACK, perms);
            }
        }

        /* 扫描银行卡请求*/
        @JavascriptInterface
        public void scanBankCard() {
            String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (EasyPermissions.hasPermissions(getApplication(), perms)) {
                scanIDCard(REQUEST_CODE_BANK_CARD);
            } else {
                EasyPermissions.requestPermissions(CcbFaceActivity.this, getString(R.string.permission_camera_desc), REQUEST_CODE_BANK_CARD, perms);
            }
        }

        /* 保存身份证*/
        @JavascriptInterface
        public void createPicture(String json) {
            saveIdCardPic2Cache(json);
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void openWebView(String url) {
            Intent intent = new Intent(mContext, CcbWebViewActivity.class);
            intent.putExtra("url", url);
            startActivityForResult(intent, WEBVIEW_ACTIVITY_CODE);


        }

        /**
         * 复制到剪贴板
         */
        @JavascriptInterface
        public void copyToClipboard(String data) {
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", data);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
        }

        @JavascriptInterface
        public void forBidScreenCap(String data) {
//           MainActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    ((Activity)mContext).getWindow().getAttributes().flags = WindowManager.LayoutParams.FLAG_SECURE;
                    Window window = ((Activity) mContext).getWindow();
                    window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
       /*               window.getAttributes().flags = WindowManager.LayoutParams.FLAG_SECURE;
                    window.setAttributes(window.getAttributes());
                    window.getDecorView().invalidate();*/
//                    MainActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

                }
            });
        }
    }

    public void initESafe() {
        safe = EsafeUtils.getESafeLib(mContext);

        securityReq = new SecurityReq();
        securityReq.SYS_CODE = safe.getSYS_CODE();
        securityReq.APP_NAME = safe.getAPP_NAME();
        securityReq.MP_CODE = safe.getMP_CODE();
        securityReq.SEC_VERSION = safe.getVersion();
        securityReq.APP_IMEI = TextUtils.isEmpty(safe.getIMEI()) ? "" : safe.getIMEI();
        securityReq.GPS_INFO = TextUtils.isEmpty(safe.getGPS()) ? "" : safe.getGPS();

        final String json = JsonUtils.toJson(securityReq);
        //网络请求参数
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myWebView.loadUrl("javascript:createESafeResult('" + json + "')");
            }
        });
    }

    public void startfaceScan() {
        try {
            // 活体检测
            //自定义动作随机顺序
            //String order = FaceScanUtils.generatingActionOrder();
            //动作随机顺序选择,照片质量
//                    face.initLiveness(3, -1);
            //异步获取人脸识别图片
            face.startLivenessAsync(mContext, FacelibInterface.ACTION_BLINK,
                    new FacelibInterface.FaceCallback<FacelibInterface.faceInfo[]>() {
                        @Override
                        public void onResult(int errCode, FacelibInterface.faceInfo[] t) {
                            handleFaceResult(errCode, t);
                        }
                    });
        } catch (Exception e) {
            Log.i("MainActivity", "Exception:" + e.toString());
        }
    }

    /**
     * 处理人脸识别结果
     *
     * @param errCode int 结果码
     * @param t       faceInfo[] 人脸数据
     */
    private void handleFaceResult(final int errCode, FacelibInterface.faceInfo[] t) {
        Log.i("MainActivity", "handleFaceResult success");
        // 成功
        if (errCode == FacelibInterface.RESULT_OK) {
            photo = Base64.encodeToString(t[0].img, Base64.DEFAULT).replaceAll("\r|\n", "");
            final String returnResult = "{\"picture\":\"\"}";
            //网络请求参数
            //网络请求参数
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myWebView.loadUrl("javascript:scanFaceResult('" + returnResult + "')");
                }
            });
        } else {
            // 失败
            new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myWebView.loadUrl("javascript:errorHandle(" + errCode + "," + "'用户取消操作。'" + ")");
                        }
                    });
                }
            }.run();
        }
    }

    private void initSecurityReq(String json) {
        initSecurityReq(json, null);
    }

    private void initSecurityReq(String json, String type) {
        initSecurityReqBody(json);
        if (null == securityReq)
            initESafe();
        securityReq.BRANCHID = JsonUtils.getString(json, "BRANCHID", "");
        int faceType = JsonUtils.getInt(json, "FaceType", 2);
        if (null != type) {
            securityReq.TXCODE = HostAddress.TXCODE_ZX02;
        } else {
            securityReq.TXCODE = HostAddress.TXCODE;
        }

        if (faceType == 1 && null != idCardEntity) {
            securityReqBody.base64_Ecrp_Txn_Inf = cardFace;
        } else {
            securityReqBody.base64_Ecrp_Txn_Inf = photo;
        }
    }

    private void initSecurityReqBody(String json) {
        securityReqBody = JsonUtils.jsonToBean(json, SecurityReqBody.class);
        securityReqBody.SYSTEM_TIME = DateUtils.getCurrentDate(DateUtils.dateFormatTimeStamp);
        securityReqBody.HARDWARESN = DeviceUtils.getDeviceId(mContext);
//        securityReqBody.base64_Ecrp_Txn_Inf = photo;
    }

    private void sendSecurityReq() {
        MainController.getInstance().postSecurity(mContext, securityReq, securityReqBody, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                Log.i("Polling", "send security failure responString" + arg0.toString() + arg1);
                LoadingDialogUtils.getInstance().dismissLoading();
                new Runnable() {
                    @Override
                    public void run() {
                        //上传安全网关失败
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myWebView.loadUrl("javascript:errorHandle(" + null + "," + "'网络异常，请稍后尝试。'" + ")");
                            }
                        });
                    }
                }.run();
//				tv_welcome.setText(String.format(getResources().getString(R.string.welcome),"***"));
            }

            @Override
            public void onSuccess(ResponseInfo<String> responString) {
                Log.i("Polling", "xutils post security success." + responString.toString());
                LoadingDialogUtils.getInstance().dismissLoading();

                if (null != responString && !TextUtils.isEmpty(responString.result)) {
                    result = responString.result.toString();
                    Log.i("解密前Polling", "responString.result ：" + result);
                    try {
                        JSONObject json = new JSONObject(responString.result);
                        if ("000000".equals(json.getString("Res_Rtn_Code"))) {
                            result = safe.tranDecrypt(json.getString("Ret_Enc_Inf"));
                            result = TextUtils.isEmpty(result) ? "" : result;
                            Log.i("解密后Polling", "responString.result ：" + result);
                            //网络请求参数
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myWebView.loadUrl("javascript:requestFaceInfoResult('" + result + "')");
                                }
                            });
                        } else {
                            result = json.getString("Res_Rtn_Msg");
                            final String code = json.getString("Res_Rtn_Code");
//                            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                            new Runnable() {
                                @Override
                                public void run() {
                                    //安全网关请求失败
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            myWebView.loadUrl("javascript:errorHandle('" + code + "','" + result + "')");
                                        }
                                    });
                                }
                            }.run();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    new Runnable() {
                        @Override
                        public void run() {
                            //上传身份证结果
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myWebView.loadUrl("javascript:errorHandle(" + null + "," + "'网络异常，请稍后尝试。'" + ")");
                                }
                            });
                        }
                    }.run();
                }
            }
        });
    }

    public void uploadFiles(final int type) {
        String filePath = "";
        try {
            if (type == REQUEST_CODE_IDCARD_SIDE_FRONT) {
                filePath = jsonPicPath.getString("FrontPicPath");
                fileUploadEntity.File_Nm = fileNm + "_ZM.jpg";
            } else if (type == REQUEST_CODE_IDCARD_SIDE_BACK) {
                filePath = jsonPicPath.getString("BackPicPath");
                fileUploadEntity.File_Nm = fileNm + "_FM.jpg";

            }

            if (TextUtils.isEmpty(jsonPicPath.getString("FrontPicPath")))
                return;
            File imageFile = new File(filePath);

            MainController.getInstance().uploadFiles(cookieStore, imageFile, fileUploadEntity, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo responString) {
                    Log.i("Polling", "upload file success responString" + responString.result.toString());
                    LoadingDialogUtils.getInstance().dismissLoading();

                    boolean isSuccess = false;
                    if (null != responString.result) {
                        String result = responString.result.toString();
                        isSuccess = JsonUtils.getBoolean(result, "SUCCESS", false);
                    }

                    if (isSuccess && REQUEST_CODE_IDCARD_SIDE_FRONT == type) {
                        uploadFiles(REQUEST_CODE_IDCARD_SIDE_BACK);
                        try {
                            FileUtils.deleteFile(jsonPicPath.getString("FrontPicPath"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (isSuccess && REQUEST_CODE_IDCARD_SIDE_BACK == type) {
                        try {
                            FileUtils.deleteFile(jsonPicPath.getString("BackPicPath"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new Runnable() {
                            @Override
                            public void run() {
                                //上传身份证结果
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myWebView.loadUrl("javascript:createPictureResult()");
                                    }
                                });
                            }
                        }.run();
                    } else {
//                        Toast.makeText(mContext,"上传图片失败，请稍后尝试。",Toast.LENGTH_SHORT).show();
                        new Runnable() {
                            @Override
                            public void run() {
                                //上传身份证结果
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myWebView.loadUrl("javascript:errorHandle(" + null + "," + "'上传图片失败，请稍后尝试。'" + ")");
                                    }
                                });
                            }
                        }.run();
                    }
                }

                @Override
                public void onFailure(HttpException arg0, String arg1) {
                    Log.i("Polling", "upload file failure responString" + arg0.toString() + arg1);
                    LoadingDialogUtils.getInstance().dismissLoading();
                    new Runnable() {
                        @Override
                        public void run() {
                            //上传身份证结果
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myWebView.loadUrl("javascript:errorHandle(" + null + "," + "'网络异常，请稍后尝试。'" + ")");
                                }
                            });
                        }
                    }.run();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initFileUpload(String json) {
        fileUploadEntity = new FileUploadEntity();
        fileUploadEntity.USERID = JsonUtils.getString(json, "USERID", "");
        fileUploadEntity.BRANCHID = JsonUtils.getString(json, "BRANCHID", "");
        fileUploadEntity.TXCODE = JsonUtils.getString(json, "TXCODE", "");
        fileUploadEntity.File_Date = JsonUtils.getString(json, "File_Date", "");
        fileUploadEntity.CCB_IBSVersion = JsonUtils.getString(json, "CCB_IBSVersion", "");
        fileUploadEntity.ACTION = JsonUtils.getString(json, "ACTION", "");
        fileNm = JsonUtils.getString(json, "File_Nm", "");
    }

    private void scanIDCard(int code) {

        Intent scanIntent = new Intent(this, ISCardScanActivity.class);
        int requestCode = 1, type = 1;
        if (REQUEST_CODE_IDCARD_SIDE_FRONT == code) {
            requestCode = REQUEST_CODE_IDCARD_SIDE_FRONT;
            type = OcrlibInterface.REQUEST_IDCARD_SIDE_FRONT;
        } else {
            requestCode = REQUEST_CODE_IDCARD_SIDE_BACK;
            type = OcrlibInterface.REQUEST_IDCARD_SIDE_BACK;
        }

        ocr.startScanIDCardActivity(scanIntent, type, 80);
        startActivityForResult(scanIntent, requestCode);
    }


    private void scanBandCard() {
        Intent bankIntent = new Intent(this,
                com.intsig.ccrengine.ISCardScanActivity.class);
        ocr.startScanBankCardActivity(bankIntent);
        startActivityForResult(bankIntent, REQUEST_CODE_BANK_CARD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_IDCARD_SIDE_FRONT == requestCode) {
            ocr.ScanIDCardActivityResult(resultCode, data);
            if (idCardEntity == null)
                idCardEntity = new IDCardEntity();
            if (data != null) {
                idCardEntity.cardName = data.getStringExtra(OcrlibInterface.RESULT_IDCARD_NAME);
                idCardEntity.cardDate = data.getStringExtra(OcrlibInterface.RESULT_IDCARD_DATE);
                if (idCardEntity.cardDate != null) {
                    Calendar calendar = DateUtils.strToCalendar(idCardEntity.cardDate, DateUtils.dateFormatYMD3);
                    idCardEntity.cardYear = calendar.get(Calendar.YEAR) + "";
                    idCardEntity.cardMonth = DateUtils.fillZero(calendar.get(Calendar.MONTH) + 1);
                    idCardEntity.cardDay = DateUtils.fillZero(calendar.get(Calendar.DAY_OF_MONTH));
                }
                idCardEntity.cardSex = data.getStringExtra(OcrlibInterface.RESULT_IDCARD_SEX);
                idCardEntity.cardNation = data.getStringExtra(OcrlibInterface.RESULT_IDCARD_NATION);
                idCardEntity.cardAddress = data.getStringExtra(OcrlibInterface.RESULT_IDCARD_ADDRESS);
                idCardEntity.cardID = data.getStringExtra(OcrlibInterface.RESULT_IDCARD_ID);
//                idCardEntity.cardOrientation = data.getStringExtra(OcrlibInterface);

                imageBytesFront = data.getByteArrayExtra(OcrlibInterface.RESULT_IDCARD_PICTURE);
                if (imageBytesFront != null) {
                    Bitmap bm = BitmapFactory.decodeByteArray(imageBytesFront, 0,
                            imageBytesFront.length);
                    idCardEntity.cardImage = Base64.encodeToString(imageBytesFront, Base64.DEFAULT).replaceAll("\r|\n", "");
                }

//                Toast.makeText(this,idCardEntity.toString(),Toast.LENGTH_SHORT).show();

                byte[] avatarBytes = data
                        .getByteArrayExtra(OcrlibInterface.RESULT_IDCARD_AVATAR);
                if (avatarBytes != null) {
                    Bitmap bm = BitmapFactory.decodeByteArray(avatarBytes, 0,
                            avatarBytes.length);
                    cardFace = Base64.encodeToString(avatarBytes, Base64.DEFAULT).replaceAll("\r|\n", "");
                }
//                FileUtils.writeImage2Cache(mContext,avatarBytes,"front");
                new Runnable() {
                    @Override
                    public void run() {
                        final String json = JsonUtils.toJson(idCardEntity);
                        mLogger.debug(json);
                        //返回身份证查询结果
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myWebView.loadUrl("javascript:scanIdCardFrontResult('" + json + "')");
                            }
                        });
                    }
                }.run();
            }
        } else if (REQUEST_CODE_IDCARD_SIDE_BACK == requestCode) {
            ocr.ScanIDCardActivityResult(resultCode, data);
            if (idCardEntity == null)
                idCardEntity = new IDCardEntity();
            if (data != null) {
//                idCardEntity.cardOrientation = data.getStringExtra(OcrlibInterface);
                idCardEntity.cardAuthority = data.getStringExtra(OcrlibInterface.RESULT_IDCARD_AUTHORITY);
//                idCardEntity.cardValidity = data.getStringExtra(OcrlibInterface.RESULT_IDCARD_VALIDITY);
                idCardEntity.cardValidity = data.getStringExtra(OcrlibInterface.RESULT_IDCARD_VALIDITY).replace(".", "");

                imageBytesBack = data.getByteArrayExtra(OcrlibInterface.RESULT_IDCARD_PICTURE);
                if (imageBytesBack != null) {
                    Bitmap bm = BitmapFactory.decodeByteArray(imageBytesBack, 0,
                            imageBytesBack.length);
                    idCardEntity.cardImage = Base64.encodeToString(imageBytesBack, Base64.DEFAULT).replaceAll("\r|\n", "");
                }
//                FileUtils.writeImage2Cache(mContext,imageBytesBack,"back");
                new Runnable() {
                    @Override
                    public void run() {
                        final String json = JsonUtils.toJson(idCardEntity);
                        mLogger.debug(json);
                        //返回身份证查询结果
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myWebView.loadUrl("javascript:scanIdCardBackResult('" + json + "')");
                            }
                        });
                    }
                }.run();
            }
        } else if (REQUEST_CODE_BANK_CARD == requestCode) {
            if (bankEntity == null)
                bankEntity = new BankEntity();
            ocr.ScanBankCardActivityResult(resultCode, data);
            if (data != null) {
                bankEntity.bankcardNumber = data.getStringExtra(OcrlibInterface.RESULT_BANKCARD_NUMBER).replace(" ", "");
                bankEntity.bankcardBankName = data.getStringExtra(OcrlibInterface.RESULT_BANKCARD_BANKNAME);
                bankEntity.bankcardBandIdentificationNumber = data.getStringExtra(OcrlibInterface.RESULT_BANKCARD_BANKIDENTIFICATIONNUMBER);
                bankEntity.bankcardCardName = data.getStringExtra(OcrlibInterface.RESULT_BANKCARD_CARDNAME);
                bankEntity.bankcardCardType = data.getStringExtra(OcrlibInterface.RESULT_BANKCARD_CARDTYPE);

                byte[] imageBytes = data
                        .getByteArrayExtra(OcrlibInterface.RESULT_BANKCARD_BITMAP);
                if (imageBytes != null) {
                    Bitmap bm = BitmapFactory.decodeByteArray(imageBytes, 0,
                            imageBytes.length);
//                    bankEntity.bankcardBitmap = Base64.encodeToString(imageBytes,Base64.DEFAULT).replaceAll("\r|\n","");
                }
//                Toast.makeText(this,bankEntity.toString(),Toast.LENGTH_SHORT).show();
//                FileUtils.writeImage2Cache(mContext,imageBytes,"card");
                new Runnable() {

                    @Override
                    public void run() {
                        final String json = JsonUtils.toJson(bankEntity);
                        mLogger.debug(json);
                        //返回银行卡查询结果
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myWebView.loadUrl("javascript:scanBankCardResult('" + json + "')");
                            }
                        });
                    }
                }.run();
            }
        } else if (requestCode == WEBVIEW_ACTIVITY_CODE) {
            final String params = data.getStringExtra("PARAMS");
            //关闭webview回调
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myWebView.loadUrl("javascript:closeWebViewResult('" + params + "')");
                }
            });
        }
    }

    public void saveIdCardPic2Cache(String json) {
        String backPic = "", frontPic = "", name = "";
        try {
            if (isUseLocal)
                name = json;
            else
                name = JsonUtils.getString(json, "File_Nm", "");
            if (null != imageBytesFront) {
                imageBytesFront = FileUtils.compressBitmap(imageBytesFront, 200);
                frontPic = FileUtils.writeImage2Cache(mContext, imageBytesFront, name + "_ZM");
//                FileUtils.writeImage2Disk(mContext, imageBytesFront, name + "_ZM");
            }
            if (null != imageBytesBack) {
                imageBytesBack = FileUtils.compressBitmap(imageBytesBack, 200);
                backPic = FileUtils.writeImage2Cache(mContext, imageBytesBack, name + "_FM");
//                FileUtils.writeImage2Disk(mContext, imageBytesBack, name + "_FM");
            }
            jsonPicPath.put("FrontPicPath", frontPic);
            jsonPicPath.put("BackPicPath", backPic);
            initFileUpload(json);
            uploadFiles(REQUEST_CODE_IDCARD_SIDE_FRONT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (myWebView != null) {
            ViewGroup parent = (ViewGroup) myWebView.getParent();
            if (null != parent)
                parent.removeView(myWebView);
            myWebView.removeAllViews();
            myWebView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        switch (requestCode) {
            case READ_PHONE_STATE_CODE:
                break;
            case FACE_CAMERA_REQUEST_CODE:
                startfaceScan();
                break;
            case REQUEST_CODE_IDCARD_SIDE_FRONT:
                scanIDCard(REQUEST_CODE_IDCARD_SIDE_FRONT);
                break;
            case REQUEST_CODE_IDCARD_SIDE_BACK:
                scanIDCard(REQUEST_CODE_IDCARD_SIDE_BACK);
                break;
            case REQUEST_CODE_BANK_CARD:
                scanBandCard();
                break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        showPermissionDialog();
    }

    private void showPermissionDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(R.string.permission_camera_open)
                .setPositiveButton(R.string.go_setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        APKUtils.toAppDetailSettingIntent(CcbFaceActivity.this);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        dialog.setCancelable(false);
        dialog.show();
    }
}
