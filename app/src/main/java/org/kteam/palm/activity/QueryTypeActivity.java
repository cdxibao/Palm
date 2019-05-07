package org.kteam.palm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.common.view.ProgressHUD;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.model.UserInfo;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.UserInfoResponse;

import java.util.HashMap;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-03-02 23:23
 */
public class QueryTypeActivity extends BaseActivity implements View.OnClickListener {
    private final Logger mLogger = Logger.getLogger(getClass());
    private ProgressHUD mProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_type);
        initToolBar();
        setTitleText(getString(R.string.query));

        initView();
    }

    private void initView() {
        findView(R.id.rl_yanglao).setOnClickListener(this);
        findView(R.id.rl_yiliao).setOnClickListener(this);
        final ImageView ivYanglao = findView(R.id.iv_yanglao_pay_info);
        final ImageView ivYiliao = findView(R.id.iv_yiliao_pay_info);
        ivYiliao.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = ivYiliao.getWidth();
                int height = (int) (width * 0.33333);
                ivYiliao.getLayoutParams().height = height;
                ivYanglao.getLayoutParams().height = height;
                ivYiliao.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        mProgressHUD = new ProgressHUD(this);
    }

    @Override
    public void onClick(View v) {
        if (!checkUserLogin()) return;
        switch (v.getId()) {
            case R.id.rl_yanglao:
                loadUserData();
                break;
            case R.id.rl_yiliao:
                startActivity(new Intent(this, QueryYiliaoActivity.class));
                break;
            default:
                break;
        }
    }

    private void loadUserData() {
        mProgressHUD.show(getString(R.string.jumping), true, true, null);
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
                        if (userInfo != null && !TextUtils.isEmpty(userInfo.jgsf)) {
                            switch (userInfo.jgsf) {
                                case "1":
                                    startActivity(new Intent(QueryTypeActivity.this, QueryYanglaoJgsfActivity.class));
                                    break;
                                case "2":
                                    startActivity(new Intent(QueryTypeActivity.this, QueryYanglaoActivity.class));
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
                mProgressHUD.hide();
            }

            @Override
            public void onNetworkError() {
                finish();
                mProgressHUD.hide();
            }
        });
        requestClient.executePost(this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_GET_USER_INFO,
                UserInfoResponse.class,
                paramMap);
    }
}
