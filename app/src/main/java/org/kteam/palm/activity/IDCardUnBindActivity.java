package org.kteam.palm.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.BaseResponse;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.common.view.ProgressHUD;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.BaseApplication;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.utils.IDCardUtils;
import org.kteam.palm.common.utils.SharedPreferencesUtils;
import org.kteam.palm.common.utils.UserStateUtils;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.UserResponse;

import java.util.HashMap;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-18 23:35
 */
public class IDCardUnBindActivity extends BaseActivity implements View.OnClickListener, DialogInterface.OnCancelListener  {
    private final Logger mLogger = Logger.getLogger(getClass());
    private boolean mIsNetworking;
    private ProgressHUD mProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcard_unbind);
        initToolBar();
        setTitleText(getString(R.string.unbind_idcard));
        initView();
    }

    private void initView() {

        findView(R.id.btn_submit).setOnClickListener(this);
        TextView tvIdCard = findView(R.id.tv_idcard);
        tvIdCard.setText(mUser.idcard);
        TextView tvName = findView(R.id.tv_username);
        tvName.setText(mUser.name);

        mProgressHUD = new ProgressHUD(this);
    }

    private void submit() {
        if (mIsNetworking) return;

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("phone", mUser.phone);

        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_UNBIND_IDCARD);
        paramMap.put("token", token);

        mIsNetworking = true;
        mProgressHUD.show(getString(R.string.submit), true, true, this);
        RequestClient<UserResponse> requestClient = new RequestClient<UserResponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<UserResponse>() {
            @Override
            public void onLoadComplete(UserResponse response) {
                if (response.code == 0) {
                    loadUserInfo();
                } else {
                    ViewUtils.showToast(BaseApplication.getContext(), response.msg);
                }
                mIsNetworking = false;
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                mIsNetworking = false;
            }
        });
        requestClient.executePost(this,
                getString(R.string.submiting),
                Constants.BASE_URL + Constants.URL_UNBIND_IDCARD,
                UserResponse.class,
                paramMap);
    }

    private void loadUserInfo() {
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
                    mUser.level = response.body.level;
                    mUser.name = response.body.name;
                    mUser.idcard = response.body.idcard;
                    mUser.social_security_card = response.body.social_security_card;
                    UserStateUtils.getInstance().setUser(mUser);
                }
                mProgressHUD.hide();
                mIsNetworking = false;
                finish();
            }

            @Override
            public void onNetworkError() {
                mIsNetworking = false;
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
    public void onCancel(DialogInterface dialog) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                 submit();
            break;
            default:
                break;
        }
    }
}
