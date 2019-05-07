package org.kteam.palm.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.MD5;
import org.kteam.common.utils.StringUtils;
import org.kteam.common.utils.ViewUtils;
import org.kteam.common.view.ProgressHUD;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.AppLogUtils;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.utils.SharedPreferencesUtils;
import org.kteam.palm.common.utils.UserStateUtils;
import org.kteam.palm.common.view.ConfirmDialog;
import org.kteam.palm.common.view.SwitchButton;
import org.kteam.palm.model.User;
import org.kteam.palm.model.UserInfo;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.UserInfoResponse;
import org.kteam.palm.network.response.UserResponse;

import java.util.HashMap;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 *
 * @Description 登录
 *
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-05 23:52
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, DialogInterface.OnCancelListener, TextWatcher {
    public static final int REGIST_REQUEST_CODE = 1001;
    private final Logger mLogger = Logger.getLogger(getClass());

    private EditText mEtUserName;
    private EditText mEtPassword;
    private Button mBtnLogin;
    private ProgressHUD mProgressHUD;
    private ConfirmDialog<String> mConfirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initToolBar();
        setTitleText(getString(R.string.login));

        mEtUserName = findView(R.id.et_phone);
        mEtPassword = findView(R.id.et_passwd);

        SwitchButton switchButton = findView(R.id.sb_pwd);
        switchButton.setOnSwitchStateListener(new SwitchButton.OnSwitchListener() {
            @Override
            public void onSwitched(boolean switched) {
                int type = switched ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                mEtPassword.setInputType(type);
                mEtPassword.setSelection(mEtPassword.getText().length());
            }
        });

        User user = UserStateUtils.getInstance().getUser();
        if (user != null) {
            mEtUserName.setText(user.phone);
            mEtPassword.setText(user.passwd);
            switchButton.setVisibility(View.INVISIBLE);
        }

        mEtUserName.addTextChangedListener(this);
        mEtPassword.addTextChangedListener(this);

        mBtnLogin = findView(R.id.btn_login);
        mBtnLogin.setOnClickListener(this);
        findView(R.id.tv_refind_passwd).setOnClickListener(this);

        mProgressHUD = new ProgressHUD(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setLoginButtonBg();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REGIST_REQUEST_CODE) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (ViewUtils.isFastClick()) return true;

        switch (item.getItemId()) {
            case R.id.action_register:
                startActivityForResult(new Intent(this, RegisterStep1Activity.class), REGIST_REQUEST_CODE);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressHUD != null) {
            mProgressHUD.dismiss();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        setLoginButtonBg();
    }

    private void setLoginButtonBg() {
        int alpha = mEtPassword.getText().length() > 0 && mEtUserName.getText().length() > 0 ?  255 : Constants.BUTTON_UNABLE_ALPHA;
        mBtnLogin.getBackground().setAlpha(alpha);
        mBtnLogin.setClickable(alpha == 255);
    }

    private void login() {
        ViewUtils.hideInputMethod(this);
        final String userName = mEtUserName.getText().toString();
        final String password = mEtPassword.getText().toString();
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            ViewUtils.showToast(this, R.string.input_tip_name_passwd);
            return;
        }
        if (!StringUtils.isValidPhone(userName)) {
            ViewUtils.showToast(this, R.string.failed_login_user_or_passswd);
            return;
        }
        if (password.length() < 6) {
            ViewUtils.showToast(this, R.string.failed_login_user_or_passswd);
            return;
        }
        mProgressHUD.show(getString(R.string.logining), true, true, this);
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("phone", userName);
        paramMap.put("password", MD5.encode(password));
        paramMap.put("lastSource", Constants.FIRST_SOURCE);
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_USER_LOGIN);
        paramMap.put("token", token);

        RequestClient<UserResponse> requestClient = new RequestClient<UserResponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<UserResponse>() {
            @Override
            public void onLoadComplete(UserResponse response) {
                if (response.code == 0 && response.body != null) {
                    AppLogUtils.userLog(AppLogUtils.YWLX_DL);
                    mUser = response.body;
                    mUser.phone = userName;
                    mUser.passwd = password;
//                    SharedPreferencesUtils.getInstance().updateLoginTime();
                    UserStateUtils.getInstance().setUser(response.body);
                    if (mUser.level > 1) {
                        loadUserData();
                    } else {
                        mProgressHUD.hide();
                        if (mConfirmDialog == null) {
                            mConfirmDialog = new ConfirmDialog<String>(LoginActivity.this, R.style.ConfirmDialog);
                            mConfirmDialog.setContent(R.string.pls_bind_idcard);
                            mConfirmDialog.setOnDialogListener(new ConfirmDialog.OnDialogListener<String>() {
                                @Override
                                public void onOK(String s) {
                                    setResult(RESULT_OK);
                                    finish();
                                }

                                @Override
                                public void onCancle(String s) {
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            });
                            mConfirmDialog.setCanceledOnTouchOutside(false);
                            WindowManager windowManager =  (WindowManager)LoginActivity.this.getSystemService(Context.WINDOW_SERVICE);
                            Display display = windowManager.getDefaultDisplay();
                            WindowManager.LayoutParams lp = mConfirmDialog.getWindow().getAttributes();
                            lp.width = (display.getWidth() * 80) / 100; //设置宽度
                        }
                        mConfirmDialog.show();
                    }
                } else {
                    ViewUtils.showToast(LoginActivity.this, response.msg);
                    mProgressHUD.hide();
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                ViewUtils.showToast(LoginActivity.this, R.string.network_error);
            }
        });
        requestClient.executePost(this,
                getString(R.string.logining),
                Constants.BASE_URL + Constants.URL_USER_LOGIN,
                UserResponse.class,
                paramMap);
    }

    private void loadUserData() {
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
                            mUser.name= userInfo.aac003;
                        }
                        if (!TextUtils.isEmpty(userInfo.aab001)) {
                            mUser.companyId = userInfo.aab001;
                        }
                        if (!TextUtils.isEmpty(userInfo.aab004)) {
                            mUser.companyName = userInfo.aab004;
                        }
                        if (!TextUtils.isEmpty(userInfo.aac010)) {
                            mUser.address = userInfo.aac010;
                        }
                        if (!TextUtils.isEmpty(userInfo.aab300)) {
                            mUser.insuranceOrg = userInfo.aab300;
                        }
                    }
                }
                mProgressHUD.hide();
                setResult(RESULT_OK);
                finish();
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

    @Override
    public void onClick(View v) {
        if (ViewUtils.isFastClick()) return;
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_refind_passwd:
                startActivity(new Intent(this, RefindPasswdStep1Activity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        finish();
    }
}
