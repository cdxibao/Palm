package org.kteam.palm.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.BaseResponse;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.MD5;
import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.BaseApplication;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.utils.SharedPreferencesUtils;
import org.kteam.palm.common.utils.UserStateUtils;
import org.kteam.palm.common.view.SwitchButton;
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
public class UpdatePasswdActivity extends BaseActivity implements View.OnClickListener {
    private final Logger mLogger = Logger.getLogger(getClass());

    private EditText mEtOldPasswd;
    private EditText mEtNewPasswd;
    private Button mBtnOk;

    private boolean mNetworking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_passwd);
        initToolBar();
        setTitleText(getString(R.string.update_passwd));

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setOkButtonBg();
    }

    private void initView() {
        mEtOldPasswd = findView(R.id.et_old_passwd);
        mEtNewPasswd = findView(R.id.et_new_passwd);

        mBtnOk = findView(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);

        SwitchButton switchButton = findView(R.id.sb_pwd);
        switchButton.setOnSwitchStateListener(new SwitchButton.OnSwitchListener() {
            @Override
            public void onSwitched(boolean switched) {
                int type = switched ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                mEtNewPasswd.setInputType(type);
                mEtNewPasswd.setSelection(mEtNewPasswd.getText().length());
            }
        });

        MyTextWatcher myTextWatcher = new MyTextWatcher();
        mEtOldPasswd.addTextChangedListener(myTextWatcher);
        mEtNewPasswd.addTextChangedListener(myTextWatcher);
    }

    private void setOkButtonBg() {
        int alpha = mEtOldPasswd.getText().length() > 0 && mEtNewPasswd.getText().length() > 0 ?  255 : Constants.BUTTON_UNABLE_ALPHA;
        mBtnOk.getBackground().setAlpha(alpha);
        mBtnOk.setClickable(alpha == 255);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                if (mNetworking) return;
                String oldPasswd = mEtOldPasswd.getText().toString();
                if (TextUtils.isEmpty(oldPasswd)) {
                    ViewUtils.showToast(this, R.string.input_tip_old_passwd);
                    return;
                }
                String newPasswd = mEtNewPasswd.getText().toString();
                if (TextUtils.isEmpty(newPasswd)) {
                    ViewUtils.showToast(this, R.string.input_tip_new_passwd);
                    return;
                }
                mNetworking = true;
                HashMap<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("oldPassword", MD5.encode(oldPasswd));
                paramMap.put("newPassword", MD5.encode(newPasswd));
                paramMap.put("phone", mUser.phone);
                String token = NetworkUtils.getToken(this, paramMap, Constants.URL_UPDATE_PASSWD);
                paramMap.put("token", token);

                RequestClient<BaseResponse> requestClient = new RequestClient<BaseResponse>();
                requestClient.setOnLoadListener(new RequestClient.OnLoadListener<BaseResponse>() {
                    @Override
                    public void onLoadComplete(BaseResponse response) {
                        if (response.code == 0) {
                            finish();
                        } else {
                            ViewUtils.showToast(BaseApplication.getContext(), response.msg);
                        }
                        mNetworking = false;
                    }

                    @Override
                    public void onNetworkError() {
                        ViewUtils.showToast(UpdatePasswdActivity.this, R.string.network_error);
                        mLogger.error(getString(R.string.network_error));
                        mNetworking = false;
                    }
                });
                requestClient.executePost(this,
                        getString(R.string.submiting),
                        Constants.BASE_URL + Constants.URL_UPDATE_PASSWD,
                        BaseResponse.class,
                        paramMap);
                break;
            default:
                break;
        }
    }

    class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            setOkButtonBg();
        }
    }
}
