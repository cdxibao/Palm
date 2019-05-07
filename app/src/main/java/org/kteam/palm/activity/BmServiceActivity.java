package org.kteam.palm.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.StringUtils;
import org.kteam.common.utils.ViewUtils;
import org.kteam.common.view.ProgressHUD;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.CcbFaceResponse;

import java.util.HashMap;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description 便民服务
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-18 23:35
 */
public class BmServiceActivity extends BaseActivity implements View.OnClickListener, DialogInterface.OnCancelListener {
    private final Logger mLogger = Logger.getLogger(getClass());
    private ProgressHUD mProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bm_service);
        initToolBar();
        setTitleText(getString(R.string.bm_service));
        initView();
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
        findViewById(R.id.ll_jh_bm_service).setOnClickListener(this);
        SimpleDraweeView sdv = findView(R.id.sdv);
        Uri uri = Uri.parse("http://www.sclzsi.cn/WechatIMG50.jpeg");
        sdv.setImageURI(uri);
        sdv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_jh_bm_service:
                ccbFace();
                break;
            case R.id.sdv:
                startActivity(new Intent(BmServiceActivity.this, BmIntroActivity.class));
                break;
            default:
                break;
        }
    }

    public void ccbFace() {
        mProgressHUD.show(getString(R.string.loading), true, true, this);
        HashMap<String, String> paramMap = new HashMap<String, String>();
        String mUserId = mUser == null ? StringUtils.getRandomKey() : String.valueOf(mUser.id);
        paramMap.put("userid", mUserId);
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_CCBFACE_URL);
        paramMap.put("token", token);

        RequestClient<CcbFaceResponse> requestClient = new RequestClient<CcbFaceResponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<CcbFaceResponse>() {
            @Override
            public void onLoadComplete(CcbFaceResponse response) {
                mProgressHUD.dismiss();
                if (response.code == 0) {
                    if (response.body != null && response.body.containsKey("url") && !TextUtils.isEmpty(response.body.get("url"))) {
                        Intent intent = new Intent(BmServiceActivity.this, CcbFaceActivity.class);
                        intent.putExtra("url", response.body.get("url"));
                        startActivity(intent);
                    }
                } else {
                    ViewUtils.showToast(BmServiceActivity.this, response.msg);
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                ViewUtils.showToast(BmServiceActivity.this, R.string.network_error);
                mProgressHUD.dismiss();
            }
        });
        requestClient.executePost(this,
                getString(R.string.jumping),
                Constants.BASE_URL + Constants.URL_CCBFACE_URL,
                CcbFaceResponse.class,
                paramMap);
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }
}
