package org.kteam.palm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.R;
import org.kteam.palm.adapter.BaseListAdapter;
import org.kteam.palm.adapter.SettingMenuAdapter;
import org.kteam.palm.common.utils.APKUtils;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.utils.UserStateUtils;
import org.kteam.palm.common.view.ArticleDividerItemDecoration;
import org.kteam.palm.common.view.ConfirmDialog;
import org.kteam.palm.common.view.DownloadingDialog;
import org.kteam.palm.common.view.OnRVItemClickListener;
import org.kteam.palm.model.SettingMenu;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.VersionInfoResponse;

import java.io.File;
import java.util.HashMap;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description 设置界面
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-12 18:13
 */
public class SettingActivity extends BaseActivity implements OnRVItemClickListener, ConfirmDialog.OnDialogListener<String> {
    private final Logger mLogger = Logger.getLogger(getClass());

    private ConfirmDialog<String> mConfirmDialog;
    private DownloadingDialog mDownProcessDialog;
    private SettingMenuAdapter mSettingMenuAdapter;
    private APKUtils mApkUtils;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case APKUtils.HANDLER_MSG_PD_UPDATE:
                    mDownProcessDialog.setProgress(msg.arg1);
                    break;
                case APKUtils.HANDLER_MSG_DOWNLOAD_COMPLET:
                    mDownProcessDialog.cancel();
                    String packagePath = (String)msg.obj;
                    mApkUtils.installApp(packagePath);
                    break;
                case APKUtils.HANDLER_MSG_DOWNLOAD_FAILED:
                    ViewUtils.showToast(SettingActivity.this, R.string.failed_download_apk);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initToolBar();
        setTitleText(getString(R.string.setting));

        initView();
        mApkUtils = new APKUtils(this);
    }

    private void initView() {
        RecyclerView rvSettingMenu = findView(R.id.rv_setting);
        rvSettingMenu.setHasFixedSize(true);
        rvSettingMenu.addItemDecoration(new ArticleDividerItemDecoration(this));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSettingMenu.setLayoutManager(linearLayoutManager);

        mSettingMenuAdapter = new SettingMenuAdapter();
        rvSettingMenu.setAdapter(mSettingMenuAdapter);
        mSettingMenuAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mUser == null) {
            mSettingMenuAdapter.setData(SettingMenu.getUnLoginSettingData());
        } else {
            mSettingMenuAdapter.setData(SettingMenu.getLoginedSettingData());
        }
        mSettingMenuAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConfirmDialog != null) {
            mConfirmDialog.dismiss();
        }
    }

    private void checkVersion(final SettingMenu menu) {
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
                                int serverVersionCode = Integer.parseInt(response.body.versionCode);
                                if (!TextUtils.isEmpty(response.body.downUrl) && localVersion.versionCode < serverVersionCode) { //提示更新apk
                                    if (mConfirmDialog == null) {
                                        mConfirmDialog = new ConfirmDialog<String>(SettingActivity.this, R.style.ConfirmDialog);
                                        mConfirmDialog.setContent(getString(R.string.find_new_version, response.body.versionName));
                                        mConfirmDialog.setOnDialogListener(SettingActivity.this);
                                        mConfirmDialog.setCanceledOnTouchOutside(false);
                                        mConfirmDialog.setTag(response.body.downUrl);
                                        WindowManager windowManager =  (WindowManager)SettingActivity.this.getSystemService(Context.WINDOW_SERVICE);
                                        Display display = windowManager.getDefaultDisplay();
                                        WindowManager.LayoutParams lp = mConfirmDialog.getWindow().getAttributes();
                                        lp.width = (display.getWidth() * 80) / 100; //设置宽度
                                        mConfirmDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                                    }
                                    mConfirmDialog.show();
                                } else {
                                    menu.subTitle = getString(R.string.latest_version);
                                    mSettingMenuAdapter.notifyDataSetChanged();
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
                ViewUtils.showToast(SettingActivity.this, R.string.network_error);
            }
        });
        requestClient.executePost(this,
                getString(R.string.checking_version),
                Constants.BASE_URL + Constants.URL_CHECK_VERSION,
                VersionInfoResponse.class,
                paramMap);
    }

    @Override
    public void onItemClick(BaseListAdapter adapter, int position) {
        if (ViewUtils.isFastClick()) return;
        SettingMenu menu = mSettingMenuAdapter.getItem(position);
        switch (menu.type) {
            case SettingMenu.SettingMenuType.UPDATE_PASSWD:
                if (!checkUserLogin()) return;
                startActivity(new Intent(this, UpdatePasswdActivity.class));
                break;
            case SettingMenu.SettingMenuType.CHECK_VERSION:
                checkVersion(menu);
                break;
            case SettingMenu.SettingMenuType.SWICH_USER:
                UserStateUtils.getInstance().deleteUser();
                ViewUtils.restartApp(this);
                break;
            case SettingMenu.SettingMenuType.EXIT:
                UserStateUtils.getInstance().deleteUser();
                ViewUtils.exitApp(this);
                break;
        }
    }

    @Override
    public void onOK(String url) {
        mConfirmDialog.hide();
        if (mDownProcessDialog == null) {
            mDownProcessDialog = new DownloadingDialog(this, R.style.DownLoadDialog);
            mDownProcessDialog.setCanceledOnTouchOutside(false);
            mDownProcessDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        mDownProcessDialog.show();
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ViewUtils.showToast(this, R.string.check_sdcard);
            return;
        }
        String filePath = Environment.getExternalStorageDirectory() + File.separator + Constants.APK_LOCAL_PATH;
        mApkUtils.downFile(this, url, filePath, mHandler);
    }

    @Override
    public void onCancle(String url) {
        mConfirmDialog.hide();
    }
}
