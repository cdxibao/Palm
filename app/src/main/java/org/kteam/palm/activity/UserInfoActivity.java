package org.kteam.palm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.utils.SharedPreferencesUtils;
import org.kteam.palm.common.utils.UserStateUtils;
import org.kteam.palm.common.view.UserInfoView;
import org.kteam.palm.model.UserInfo;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.UserInfoResponse;
import org.w3c.dom.Text;

import java.util.HashMap;

/**
 * @Package org.kteam.palm.activity
 *
 * @Project Palm 用户信息
 *
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-05 23:53
 */
public class UserInfoActivity extends BaseActivity implements View.OnClickListener {
    private final Logger mLogger = Logger.getLogger(getClass());

    private UserInfoView mUserInfoView;
//    private TextView mTvCompanyId;
    private TextView mTvCompayName;
    private TextView mTvInsuranceOrg;
    private TextView mTvPensionState;
    private TextView mTvGsState;
    private TextView mTvSyState;
    private TextView mTvSybx;
    private TextView mTvJbyibx;
    private TextView mTvGwybz;
    private TextView mTvBcylbx;
    private View mLayoutGwybz;
    private View mLineGwybz;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initToolBar();
        setTitleText(getString(R.string.user_info));

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mUser == null) {
            return;
        }
        loadData();
    }

    private void initView() {
        mUserInfoView = findView(R.id.uiv);
//        mTvCompanyId = findView(R.id.tv_company_id);
        mTvCompayName = findView(R.id.tv_company_name);
        mTvInsuranceOrg = findView(R.id.tv_insurance_org);
        mTvPensionState = findView(R.id.tv_pension_state);
        mTvGsState = findView(R.id.tv_gs_state);
        mTvSyState = findView(R.id.tv_sy_state);
        mTvSybx = findView(R.id.tv_sybx);
        mTvJbyibx = findView(R.id.tv_jbylbx);
        mTvGwybz = findView(R.id.tv_gwybz);
        mTvBcylbx = findView(R.id.tv_bcylbx);
        mLayoutGwybz = findView(R.id.layout_gwybz);
        mLineGwybz = findView(R.id.line_gwybz);
        Button btnUpdate = findView(R.id.btn_update_info);
        btnUpdate.setOnClickListener(this);
        if (mUser.level <= 2) {
            btnUpdate.setText(R.string.update_phone);
        } else {
            btnUpdate.setText(R.string.userinfo_update);
        }

    }

    private void loadData() {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("aac001", mUser.social_security_card);
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_GET_USER_INFO);
        paramMap.put("token", token);

        RequestClient<UserInfoResponse> requestClient = new RequestClient<UserInfoResponse>();
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
                            mUser.name= userInfo.aac003;
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
//                        mTvCompanyId.setText(mUser.companyId);
                        mTvCompayName.setText(mUser.companyName);
                        if (!TextUtils.isEmpty(mUser.address)) {
                            UserStateUtils.getInstance().setUser(mUser);
                        }
                        mUserInfoView.setUser(mUser);

                        mTvPensionState.setText(userInfo.aac031_yl);
                        mTvGsState.setText(userInfo.aac031_gs);
                        mTvSyState.setText(userInfo.aac031_ps);
                        if (!TextUtils.isEmpty(userInfo.aab300)) {
                            mTvInsuranceOrg.setText(userInfo.aab300);
                        }
                        mTvSybx.setText(userInfo.aac031_sy);
                        mTvJbyibx.setText(userInfo.aac031_ml);
                        if ("未参保".equals(userInfo.aac031_gwy)) {
                            mLayoutGwybz.setVisibility(View.GONE);
                            mLineGwybz.setVisibility(View.GONE);
                        } else {
                            mLayoutGwybz.setVisibility(View.VISIBLE);
                            mLineGwybz.setVisibility(View.VISIBLE);
                            mTvGwybz.setText(userInfo.aac031_gwy);
                        }
                        mTvBcylbx.setText(userInfo.aac031_bc);
                    }
                } else {
                    if (!TextUtils.isEmpty(response.msg)) {
                        ViewUtils.showToast(UserInfoActivity.this, response.msg);
                    } else {
                        ViewUtils.showToast(UserInfoActivity.this, R.string.load_user_info_failed);
                    }
                    finish();
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                ViewUtils.showToast(UserInfoActivity.this, R.string.network_error);
                finish();
            }
        });
        requestClient.executePost(this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_GET_USER_INFO,
                UserInfoResponse.class,
                paramMap);
    }

    @Override
    public void onClick(View v) {
        if (ViewUtils.isFastClick()) return;
        startActivity(new Intent(this, UpdateUserInfoActivity.class));
    }
}
