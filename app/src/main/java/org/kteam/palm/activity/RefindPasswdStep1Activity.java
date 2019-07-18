package org.kteam.palm.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.BaseResponse;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.StringUtils;
import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.BaseApplication;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.utils.SharedPreferencesUtils;
import org.kteam.palm.common.utils.VCodeImageUtils;
import org.kteam.palm.network.NetworkUtils;

import java.util.HashMap;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-10 22:57
 */
public class RefindPasswdStep1Activity extends BaseActivity implements View.OnClickListener {

    public static final int REFIND_PASSWD_REQUEST_CODE = 1002;

    private final Logger mLogger = Logger.getLogger(getClass());

    private EditText mEtPhone;
    private Button mBtnSendCode;
    private EditText mEtImgCode;
    private SimpleDraweeView mSdv;
    private boolean mNetworking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refind_passwd_step1);
        initToolBar();
        setTitleText(getString(R.string.refind_passwd));

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCodeButtonBg();
    }

    private void initView() {
        mEtPhone = findView(R.id.et_phone);
        mEtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setCodeButtonBg();
            }
        });

        mBtnSendCode = findView(R.id.btn_send_code);
        mBtnSendCode.setOnClickListener(this);

        mEtImgCode = findView(R.id.et_img_code);
        mSdv = findView(R.id.sdv);
        mSdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImgCode(true);
            }
        });
        getImgCode(false);
    }

    private void getImgCode(boolean showLoadingDialog) {
        VCodeImageUtils.getVCodeImg(this, new VCodeImageUtils.VCodeImgCallback() {
            @Override
            public void onResult(String imgUrl) {
                Uri uri = Uri.parse(imgUrl);
                mSdv.setImageURI(uri);
            }

            @Override
            public void onError() {
                Uri uri = Uri.parse("err");
                mSdv.setImageURI(uri);
            }
        }, showLoadingDialog);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REFIND_PASSWD_REQUEST_CODE && resultCode == RESULT_OK) {
            finish();
        }
    }

    private void setCodeButtonBg() {
        int alpha = mEtPhone.getText().length() > 0 && mEtPhone.getText().length() > 0 ?  255 : Constants.BUTTON_UNABLE_ALPHA;
        mBtnSendCode.getBackground().setAlpha(alpha);
        mBtnSendCode.setClickable(alpha == 255);
    }

    @Override
    public void onClick(View v) {
        if (ViewUtils.isFastClick()) return;
        switch (v.getId()) {
            case R.id.btn_send_code:
                String phone = mEtPhone.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    ViewUtils.showToast(this, R.string.input_tip_phone);
                    return;
                }

                if (!StringUtils.isValidPhone(phone)) {
                    ViewUtils.showToast(this, R.string.input_tip_tel_length_error);
                    return;
                }

                String imgCode = mEtImgCode.getText().toString();
                if (TextUtils.isEmpty(imgCode)) {
                    ViewUtils.showToast(this, R.string.pls_input_img_code);
                    return;
                }

                if (mNetworking) return;
                sendMsgCode(phone);
            default:
                break;
        }
    }

    private void sendMsgCode(final String phone) {
        mNetworking = true;
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("phone", phone);
        paramMap.put("type", String.valueOf(Constants.CODE_TYPE_REFIND_PWD));
        paramMap.put("unique", SharedPreferencesUtils.getInstance().getUUID());
        paramMap.put("vcode", mEtImgCode.getText().toString());
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_SEND_CODE);
        paramMap.put("token", token);

        RequestClient<BaseResponse> requestClient = new RequestClient<BaseResponse>();
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<BaseResponse>() {
            @Override
            public void onLoadComplete(BaseResponse response) {
                if (response.code == 0) {
                    Intent intent = new Intent(RefindPasswdStep1Activity.this, RefindPasswdStep2Activity.class);
                    intent.putExtra("phone", phone);
                    startActivityForResult(intent, REFIND_PASSWD_REQUEST_CODE);
                } else {
                    ViewUtils.showToast(BaseApplication.getContext(), response.msg);
                }
                mNetworking = false;
            }

            @Override
            public void onNetworkError() {
                mNetworking = false;
                ViewUtils.showToast(RefindPasswdStep1Activity.this, R.string.network_error);
                mLogger.error(getString(R.string.network_error));
            }
        });
        requestClient.executePost(this,
                getString(R.string.sending_msg_code),
                Constants.BASE_URL + Constants.URL_SEND_CODE,
                BaseResponse.class,
                paramMap);
    }
}