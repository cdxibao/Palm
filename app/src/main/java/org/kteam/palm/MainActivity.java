package org.kteam.palm;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import org.apache.log4j.Logger;
import org.kteam.common.utils.ViewUtils;
import org.kteam.common.view.ProgressHUD;
import org.kteam.palm.activity.ArticleActivity;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.palm.activity.ArticleListActivity;
import org.kteam.palm.activity.ArticleTypeActivity;
import org.kteam.palm.activity.BmServiceActivity;
import org.kteam.palm.activity.IDCardBindActivity;
import org.kteam.palm.activity.IDCardUnBindActivity;
import org.kteam.palm.activity.LoginActivity;
import org.kteam.palm.activity.NewInsuredTypeActivity;
import org.kteam.palm.activity.OrderListActivity;
import org.kteam.palm.activity.PersonalUserPayTypeActivity;
import org.kteam.palm.activity.QueryTypeActivity;
import org.kteam.palm.activity.UpdatePasswdActivity;
import org.kteam.palm.activity.UserInfoActivity;
import org.kteam.palm.adapter.BaseListAdapter;
import org.kteam.palm.adapter.LeftMenuAdapter;
import org.kteam.palm.common.utils.APKUtils;
import org.kteam.palm.common.utils.ShareSDK;
import org.kteam.palm.common.utils.UserStateUtils;
import org.kteam.palm.common.view.ArticleDividerItemDecoration;
import org.kteam.palm.common.view.ConfirmDialog;
import org.kteam.palm.common.view.DownloadingDialog;
import org.kteam.palm.common.view.ModuleItemDecoration;
import org.kteam.palm.common.view.OnRVItemClickListener;
import org.kteam.palm.domain.manager.AccessTimeManager;
import org.kteam.palm.model.AccessTime;
import org.kteam.palm.model.LeftMenu;
import org.kteam.palm.model.Module;
import org.kteam.palm.model.UserInfo;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.adapter.ModuleAdapter;
import org.kteam.palm.network.response.AccessResponse;
import org.kteam.palm.network.response.AdvertisementReponse;
import org.kteam.palm.network.response.ModuleReponse;
import org.kteam.palm.network.response.UserInfoResponse;
import org.kteam.palm.network.response.VersionInfo;
import org.kteam.palm.network.response.VersionInfoResponse;


import java.io.File;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements View.OnClickListener, OnRVItemClickListener, DialogInterface.OnCancelListener, EasyPermissions.PermissionCallbacks {

    public static final String FLAG_HEADER = "-1999998";
    private static final int HANDLER_MSG_EXIT = 9999;
    private static final int PERMISSION_CODE_SDCARD = 1111;

    private final Logger mLogger = Logger.getLogger(getClass());

    private DrawerLayout mDrawerLayout;
    private View mLeftLayout;
    private View mLayoutName;
    private View mLayoutPhone;
    private View mLayoutLogin;
    private TextView mTvName;
    private TextView mTvPhone;
    private TextView mTvAccessNum;
    private ImageView mIvSetting;
    private TextView mTvBindTip;
    private ProgressHUD mProgressHUD;
    private ConfirmDialog<String> mConfirmDialog;
    private DownloadingDialog mDownProcessDialog;
    private APKUtils mApkUtils;

    private ModuleAdapter mModuleAdapter;
    private LeftMenuAdapter mLeftMenuAdapter;
    private boolean mLoadUserFailed;

    private boolean mExit;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_MSG_EXIT:
                    mExit = false;
                    break;
                case APKUtils.HANDLER_MSG_PD_UPDATE:
                    mDownProcessDialog.setProgress(msg.arg1);
                    break;
                case APKUtils.HANDLER_MSG_DOWNLOAD_COMPLET:
                    mDownProcessDialog.cancel();
                    String packagePath = (String) msg.obj;
                    mApkUtils.installApp(packagePath);
                    break;
                case APKUtils.HANDLER_MSG_DOWNLOAD_FAILED:
                    ViewUtils.showToast(MainActivity.this, R.string.failed_download_apk);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        super.initToolBar(false);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.head);
        ab.setDisplayHomeAsUpEnabled(true);

        setTitleRes(R.string.app_name);
        initView();

        String[] perms = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            onLoadData();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_desc), PERMISSION_CODE_SDCARD, perms);
        }
    }

    private void onLoadData() {
        checkVersion(false);

        mApkUtils = new APKUtils(this);

        loadUserData();
        mProgressHUD.show(getString(R.string.loading), true, true, this);
        loadAdData();
        access();
    }


    @Override
    protected void onResume() {
        super.onResume();
        resetUserView();
        loadModuleData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressHUD != null) {
            mProgressHUD.dismiss();
        }
    }

    private void resetUserView() {
        mLeftLayout.setVisibility(View.VISIBLE);
        mLayoutName.setVisibility(View.INVISIBLE);
        mLayoutPhone.setVisibility(View.INVISIBLE);
        mLayoutLogin.setVisibility(View.GONE);

        mTvBindTip.setVisibility(View.GONE);
        if (mUser != null && !mLoadUserFailed) {
            if (!TextUtils.isEmpty(mUser.name)) {
                mLayoutName.setVisibility(View.VISIBLE);
                mTvName.setText(mUser.name);
            }

            if (!TextUtils.isEmpty(mUser.phone)) {
                mLayoutPhone.setVisibility(View.VISIBLE);
                mTvPhone.setText(mUser.phone);
            }
            if (mUser.level == 2) {
                mTvBindTip.setVisibility(View.VISIBLE);
            }

            mLeftMenuAdapter.setData(LeftMenu.getMenuList(mUser.level));
            mLeftMenuAdapter.notifyDataSetChanged();
        } else {
            mLeftMenuAdapter.setData(LeftMenu.getLevel0Menu());
            mLeftMenuAdapter.notifyDataSetChanged();
            mLayoutLogin.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        mLeftLayout = findView(R.id.layout_main_left);
        mLeftLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mLayoutName = findView(R.id.layout_name);
        mLayoutPhone = findView(R.id.layout_phone);
        mLayoutPhone.setOnClickListener(this);
        mLayoutLogin = findViewById(R.id.layout_login);
        mLayoutLogin.setOnClickListener(this);
        mTvName = findView(R.id.tv_name);
        mTvPhone = findView(R.id.tv_phone);
        mIvSetting = findView(R.id.iv_setting);
        mIvSetting.setOnClickListener(this);
        mTvBindTip = findView(R.id.tv_bind_tip);
        mTvAccessNum = findView(R.id.tv_access_num);
        mTvAccessNum.setOnClickListener(this);

        RecyclerView rvLeftMenu = findView(R.id.rv_left_menu);
        rvLeftMenu.setHasFixedSize(true);
        rvLeftMenu.addItemDecoration(new ArticleDividerItemDecoration(this));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvLeftMenu.setLayoutManager(linearLayoutManager);

        mLeftMenuAdapter = new LeftMenuAdapter();
        rvLeftMenu.setAdapter(mLeftMenuAdapter);
        mLeftMenuAdapter.setOnItemClickListener(this);

        RecyclerView rvModule = findView(R.id.recyclerView);
        rvModule.setHasFixedSize(true);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        rvModule.setLayoutManager(gridLayoutManager);
        rvModule.addItemDecoration(new ModuleItemDecoration(ViewUtils.dip2px(this, 1)));

        mModuleAdapter = new ModuleAdapter();
        mModuleAdapter.setOnItemCLickListener(this);
        rvModule.setAdapter(mModuleAdapter);

        mProgressHUD = new ProgressHUD(this);

        setTitleRes(R.string.app_name);
    }

    private void loadUserData() {
        if (mUser == null || mUser.level <= 1) return;
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("aac001", mUser.social_security_card);
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_GET_USER_INFO);
        paramMap.put("token", token);

        RequestClient<UserInfoResponse> requestClient = new RequestClient<UserInfoResponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<UserInfoResponse>() {
            @Override
            public void onLoadComplete(UserInfoResponse response) {
                if (response.code == 0) {
                    if (response.body != null && response.body.size() > 0) {
                        UserInfo userInfo = response.body.get(0);
                        if (!TextUtils.isEmpty(userInfo.aac002)) {
                            mUser.idcard = userInfo.aac002;
                        }
                        if (!TextUtils.isEmpty(userInfo.aac003)) {
                            mUser.name = userInfo.aac003;
                        }
                        if (!TextUtils.isEmpty(userInfo.aab001)) {
                            mUser.companyId = userInfo.aab001;
                        }
                        if (!TextUtils.isEmpty(userInfo.aab004)) {
                            mUser.companyName = userInfo.aab004;
                        }
                        if (!TextUtils.isEmpty(userInfo.aab300)) {
                            mUser.insuranceOrg = userInfo.aab300;
                        }
                        if (!TextUtils.isEmpty(userInfo.aac010)) {
                            mUser.address = userInfo.aac010;
                        }
                        UserStateUtils.getInstance().setUser(mUser);
                    }
                } else {
                    mLoadUserFailed = true;
                    resetUserView();
                    startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), LoginActivity.REGIST_REQUEST_CODE);
                    ViewUtils.showToast(MainActivity.this, R.string.load_user_failed);
                }
            }

            @Override
            public void onNetworkError() {
                mLoadUserFailed = true;
                resetUserView();
                startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), LoginActivity.REGIST_REQUEST_CODE);
                ViewUtils.showToast(MainActivity.this, R.string.load_user_failed);

            }
        });
        requestClient.executePost(this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_GET_USER_INFO,
                UserInfoResponse.class,
                paramMap);
    }

    private void loadAdData() {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_GET_AD);
        paramMap.put("token", token);

        RequestClient<AdvertisementReponse> requestClient = new RequestClient<AdvertisementReponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<AdvertisementReponse>() {
            @Override
            public void onLoadComplete(AdvertisementReponse response) {
                if (response.code == 0 && response.body != null && response.body.size() > 0) {
                    mModuleAdapter.setHeaderData(response.body);
                } else {
                    ViewUtils.showToast(BaseApplication.getContext(), response.msg);
                }
                loadModuleData();
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                mProgressHUD.hide();
            }
        });
        requestClient.executePost(this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_GET_AD,
                AdvertisementReponse.class,
                paramMap);
    }

    private void loadModuleData() {
        final AccessTimeManager accessTimeManager = new AccessTimeManager();
        final AccessTime accessTime = accessTimeManager.getAccessTime("0");

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("categoryPid", "0");
        paramMap.put("lastTime", accessTime.time);
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_GET_MODULE);
        paramMap.put("token", token);

        RequestClient<ModuleReponse> requestClient = new RequestClient<ModuleReponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<ModuleReponse>() {
            @Override
            public void onLoadComplete(ModuleReponse response) {
                if (response.code == 0) {
                    accessTimeManager.updateAccessTime(accessTime);
                    mModuleAdapter.clearData();

                    //广告,用于占位
                    Module adModule = new Module();
                    adModule.sys_flag = FLAG_HEADER;
                    mModuleAdapter.appendData(adModule);


                    //新参保
                    Module newInsured = new Module();
                    newInsured.name = getString(R.string.new_insured);
                    newInsured.sys_flag = Module.ModuleType.NEW_INSURED;
                    mModuleAdapter.appendData(newInsured);

                    //缴费
                    Module payModule = new Module();
                    payModule.name = getString(R.string.pay);
                    payModule.sys_flag = Module.ModuleType.PAY;
                    mModuleAdapter.appendData(payModule);

                    //查询
                    Module queryModule = new Module();
                    queryModule.name = getString(R.string.query);
                    queryModule.sys_flag = Module.ModuleType.QUERY;
                    mModuleAdapter.appendData(queryModule);

                    mModuleAdapter.appendDataList(response.body);

                    //便民服务
                    Module bmServiceModule = new Module();
                    bmServiceModule.name = getString(R.string.bm_service);
                    bmServiceModule.sys_flag = Module.ModuleType.BM_SERVICE;
                    mModuleAdapter.appendData(bmServiceModule);

                    //个人中心
                    Module personalCenterModule = new Module();
                    personalCenterModule.name = getString(R.string.personal_center);
                    personalCenterModule.sys_flag = Module.ModuleType.PERSONAL_CENTER;
                    mModuleAdapter.appendData(personalCenterModule);

                    if (mModuleAdapter.getItemCount() == 7 || mModuleAdapter.getItemCount() == 8) {
                        DisplayMetrics displayMetrics = ViewUtils.getScreenInfo(MainActivity.this);
                        double allItemHeight = displayMetrics.heightPixels - displayMetrics.widthPixels * 0.4 - mToolBar.getHeight() - ViewUtils.getStatusHeight(MainActivity.this);
                        int itemHeight = (int) (allItemHeight * 0.25);
                        mModuleAdapter.setItemHeight(itemHeight);
                    } else {
                        mModuleAdapter.setItemHeight((int) getResources().getDimension(R.dimen.module_item_height));
                    }
                    mModuleAdapter.notifyDataSetChanged();
                } else {
                    ViewUtils.showToast(BaseApplication.getContext(), response.msg);
                }
                mProgressHUD.hide();
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                mProgressHUD.hide();
            }
        });
        requestClient.executePost(this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_GET_MODULE,
                ModuleReponse.class,
                paramMap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (ViewUtils.isFastClick()) return super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.action_share) {
            ShareSDK shareSDK = new ShareSDK(this);
            shareSDK.openShareBoard();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mLoadUserFailed = false;
            if (requestCode == LoginActivity.REGIST_REQUEST_CODE) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        }
    }

    @Override
    public void onItemClick(BaseListAdapter adapter, int position) {
        if (ViewUtils.isFastClick()) return;
        if (adapter instanceof LeftMenuAdapter) {
            LeftMenu leftMenu = mLeftMenuAdapter.getItem(position);
            onLeftMenuItemClick(leftMenu);
        } else if (adapter instanceof ModuleAdapter) {
            if (position == 0) return;
            Module module = mModuleAdapter.getItem(position);
            onModuleItemClick(module);
        }
    }

    private void onLeftMenuItemClick(LeftMenu leftMenu) {
        switch (leftMenu.type) {
            case LeftMenu.LeftMenuType.USER_INFO:
                if (!checkUserLogin()) return;
                startActivity(new Intent(this, UserInfoActivity.class));
                break;
            case LeftMenu.LeftMenuType.ORDER:
                if (!checkUserLogin()) return;
                startActivity(new Intent(this, OrderListActivity.class));
                break;
            case LeftMenu.LeftMenuType.NEW_INSURED:
                if (!checkUserLogin()) return;
                startActivity(new Intent(this, NewInsuredTypeActivity.class));
                break;
            case LeftMenu.LeftMenuType.IDCARD_BIND:
                if (!checkUserLogin()) return;
                startActivity(new Intent(this, IDCardBindActivity.class));
                break;

            case LeftMenu.LeftMenuType.OPT_HELP:
                Intent intent = new Intent(this, ArticleActivity.class);
                intent.putExtra("from", "left_menu");
                intent.putExtra("articleId", "2");
                startActivity(intent);
                break;
            case LeftMenu.LeftMenuType.ABOUNT:
                intent = new Intent(this, ArticleActivity.class);
                intent.putExtra("from", "left_menu");
                intent.putExtra("articleId", "3");
                startActivity(intent);
                break;
            case LeftMenu.LeftMenuType.IDCARD_UNBIND:
                if (!checkUserLogin()) return;
                startActivity(new Intent(this, IDCardUnBindActivity.class));
                break;
            case LeftMenu.LeftMenuType.UPDATE_PASSWD:
                if (!checkUserLogin()) return;
                startActivity(new Intent(this, UpdatePasswdActivity.class));
                break;
            case LeftMenu.LeftMenuType.CHECK_VERSION:
                checkVersion(true);
                break;
            case LeftMenu.LeftMenuType.SWICH_USER:
                if (!checkUserLogin()) return;
                UserStateUtils.getInstance().deleteUser();
                startActivityForResult(new Intent(this, LoginActivity.class), LoginActivity.REGIST_REQUEST_CODE);
                break;
            case LeftMenu.LeftMenuType.EXIT:
                ViewUtils.exitApp(this);
                break;
            default:
                break;
        }
    }

    private void onModuleItemClick(Module module) {
        if (Module.ModuleType.NEW_INSURED.equals(module.sys_flag)) {
            if (!checkUserLogin()) return;
            startActivity(new Intent(this, NewInsuredTypeActivity.class));
        } else if (Module.ModuleType.QUERY.equals(module.sys_flag)) {
            if (!checkUserLogin()) return;
            if (mUser.level <= 2) {
                ViewUtils.showToast(this, R.string.no_right);
                return;
            }
            Intent intent = new Intent(this, QueryTypeActivity.class);
            startActivity(intent);
        } else if (Module.ModuleType.PAY.equals(module.sys_flag)) {
            if (!checkUserLogin()) return;
            if (mUser.level <= 1) {
                ViewUtils.showToast(this, R.string.no_right);
                return;
            }
            startActivity(new Intent(this, PersonalUserPayTypeActivity.class));
        } else if (Module.ModuleType.PERSONAL_CENTER.equals(module.sys_flag)) {
            if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        } else if (Module.ModuleType.BM_SERVICE.equals(module.sys_flag)) {
            startActivity(new Intent(this, BmServiceActivity.class));
        } else if (module.wz_flag > 0) {
            Intent intent = new Intent(this, ArticleTypeActivity.class);
            intent.putExtra("title", module.name);
            intent.putExtra("categoryId", String.valueOf(module.id));
            startActivity(intent);
        } else if (module.wz_flag == 0) {
            Intent intent = new Intent(this, ArticleListActivity.class);
            intent.putExtra("title", module.name);
            intent.putExtra("categoryId", String.valueOf(module.id));
            startActivity(intent);
        }
    }

    private void closeDrawer() {
        mLeftLayout.setVisibility(View.INVISIBLE);
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onClick(View v) {
        if (ViewUtils.isFastClick()) return;
        switch (v.getId()) {
            case R.id.layout_login:
                startActivityForResult(new Intent(this, LoginActivity.class), LoginActivity.REGIST_REQUEST_CODE);
                break;
            case R.id.tv_access_num:
                mTvAccessNum.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    public void exit() {
        if (!mExit) {
            mExit = true;
            ViewUtils.showToast(this, R.string.exit_click);
            mHandler.sendEmptyMessageDelayed(HANDLER_MSG_EXIT, 2000);
        } else {
            ViewUtils.exitApp(this);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        finish();
    }

    private void checkVersion(final boolean showToast) {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("apkName", Constants.APK_NAME);
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_CHECK_VERSION);
        paramMap.put("token", token);

        RequestClient<VersionInfoResponse> requestClient = new RequestClient<VersionInfoResponse>();
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<VersionInfoResponse>() {
            @Override
            public void onLoadComplete(VersionInfoResponse response) {
                if (response.code == 0 && response.body != null) {
                    try {
                        if (!TextUtils.isEmpty(response.body.versionCode)) {
                            APKUtils.Version localVersion = mApkUtils.getLocalVersionCode();
                            if (localVersion != null) {
                                VersionInfo serverVersion = response.body;
                                int serverVersionCode = Integer.parseInt(serverVersion.versionCode);
                                boolean forceUpdate = serverVersion.versionForced == 1;
                                if (!TextUtils.isEmpty(response.body.downUrl) && localVersion.versionCode < serverVersionCode) { //提示更新apk
                                    if (mConfirmDialog == null) {
                                        mConfirmDialog = new ConfirmDialog<String>(MainActivity.this, R.style.ConfirmDialog);
                                        int resId = forceUpdate ? R.string.find_new_version_force : R.string.find_new_version;
                                        mConfirmDialog.setContent(getString(resId, serverVersion.versionName));
                                        mConfirmDialog.setOnDialogListener(new OnDownloadConfirmDialogListener(!forceUpdate));
                                        mConfirmDialog.setCancelable(!forceUpdate);
                                        mConfirmDialog.setAbleBackKey(!forceUpdate);
                                        mConfirmDialog.setTag(response.body.downUrl);
                                        WindowManager windowManager = (WindowManager) MainActivity.this.getSystemService(Context.WINDOW_SERVICE);
                                        Display display = windowManager.getDefaultDisplay();
                                        WindowManager.LayoutParams lp = mConfirmDialog.getWindow().getAttributes();
                                        lp.width = (display.getWidth() * 80) / 100; //设置宽度
                                    }
                                    mConfirmDialog.show();
                                } else {
                                    if (showToast) {
                                        ViewUtils.showToast(MainActivity.this, R.string.latest_version);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        mLogger.error(e.getMessage(), e);
                    }
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                ViewUtils.showToast(MainActivity.this, R.string.network_error);
            }
        });
        requestClient.executePost(this,
                getString(R.string.checking_version),
                Constants.BASE_URL + Constants.URL_CHECK_VERSION,
                VersionInfoResponse.class,
                paramMap);
    }

    private void access() {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("type", Constants.FIRST_SOURCE);
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_ACCESS);
        paramMap.put("token", token);

        RequestClient<AccessResponse> requestClient = new RequestClient<AccessResponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<AccessResponse>() {
            @Override
            public void onLoadComplete(AccessResponse response) {
                if (response.code == 0 && response.body != null) {
                    String countStr = response.body.count;
                    if (!TextUtils.isEmpty(countStr)) {
                        mTvAccessNum.setVisibility(View.VISIBLE);
                        mTvAccessNum.setText(getString(R.string.access_num, countStr));
                    } else {
                        mTvAccessNum.setVisibility(View.GONE);
                    }
                } else {
                    ViewUtils.showToast(BaseApplication.getContext(), response.msg);
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                mProgressHUD.hide();
            }
        });
        requestClient.executePost(this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_ACCESS,
                AccessResponse.class,
                paramMap);
    }

    class OnDownloadConfirmDialogListener implements ConfirmDialog.OnDialogListener<String> {
        private boolean ableClosed = true;

        public boolean isAbleClosed() {
            return ableClosed;
        }

        public OnDownloadConfirmDialogListener(boolean b) {
            ableClosed = b;
        }

        @Override
        public void onOK(String url) {
            mConfirmDialog.hide();
            if (mConfirmDialog != null) {
                mDownProcessDialog = new DownloadingDialog(MainActivity.this, R.style.DownLoadDialog);
                mDownProcessDialog.setCancelable(ableClosed);
                WindowManager windowManager = (WindowManager) MainActivity.this.getSystemService(Context.WINDOW_SERVICE);
                Display display = windowManager.getDefaultDisplay();
                WindowManager.LayoutParams lp = mDownProcessDialog.getWindow().getAttributes();
                lp.width = (display.getWidth() * 80) / 100; //设置宽度
            }
            mDownProcessDialog.show();
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                ViewUtils.showToast(MainActivity.this, R.string.check_sdcard);
                return;
            }
            String filePath = Environment.getExternalStorageDirectory() + File.separator + Constants.APK_LOCAL_PATH;
            mApkUtils.downFile(MainActivity.this, url, filePath, mHandler);
        }

        @Override
        public void onCancle(String url) {
            mConfirmDialog.hide();
            if (!ableClosed) {
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        onLoadData();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        if (requestCode == PERMISSION_CODE_SDCARD) {
            showPermissionDialog();
        }
    }

    private void showPermissionDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(R.string.permission_open)
                .setPositiveButton(R.string.go_setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        APKUtils.toAppDetailSettingIntent(MainActivity.this);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create();
        dialog.setCancelable(false);
        dialog.show();
    }

}
