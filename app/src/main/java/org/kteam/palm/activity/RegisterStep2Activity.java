package org.kteam.palm.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.BaseResponse;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.MD5;
import org.kteam.common.utils.StringUtils;
import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.BaseApplication;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.utils.IDCardUtils;
import org.kteam.palm.common.utils.SharedPreferencesUtils;
import org.kteam.palm.common.utils.UserStateUtils;
import org.kteam.palm.common.utils.VCodeImageUtils;
import org.kteam.palm.common.view.SwitchButton;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.UserResponse;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 *
 * @Description 注册
 *
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-05 23:52
 */
public class RegisterStep2Activity extends BaseActivity implements View.OnClickListener, TextWatcher {
    private final Logger mLogger = Logger.getLogger(getClass());

    public static final int FLAG_COUNTING = 1001;
    public static final int FLAG_COUNT_FINISH = 1002;

    private TextView mTvTel;
    private EditText mEtPasswd;
    private EditText mEtMsgCode;
    private EditText mEtUsername;
    private EditText mEtIdCard;
    private Button mBtnCode;
    private Button mBtnRegister;
    private EditText mEtImgCode;
    private SimpleDraweeView mSdv;
    private boolean mRegistinng;

    private Timer mTimer;
    private boolean mIsSendingCode = false;
    private int mTimeCount = Constants.AUTH_CODE_TIME;
    private boolean mIsPause;

    private String mPhone;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case FLAG_COUNTING:
                    if (mIsPause) {
                        return;
                    }
                    mIsSendingCode = true;
                    mBtnCode.setText(getString(R.string.code_sent, String.valueOf(mTimeCount)));
                    mBtnCode.setTextColor(getResources().getColor(R.color.code_unable_get));
                    break;
                case FLAG_COUNT_FINISH:
                    mIsSendingCode = false;
                    if (mIsPause) return;
                    mBtnCode.setText(R.string.get_msg_code);
                    mBtnCode.setTextColor(getResources().getColor(R.color.black));
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step2);
        initToolBar();
        setTitleText(getString(R.string.register));

        mPhone = getIntent().getStringExtra("phone");
        if (TextUtils.isEmpty(mPhone)) {
            finish();
            return;
        }
        initView();

        mIsSendingCode = true;
        mBtnCode.setText(getString(R.string.code_sent, String.valueOf(mTimeCount)));
        mBtnCode.setTextColor(getResources().getColor(R.color.code_unable_get));
        startCount();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsPause = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsPause = false;
        if (mIsSendingCode) {
            mBtnCode.setText(getString(R.string.code_sent, String.valueOf(mTimeCount)));
            mBtnCode.setTextColor(getResources().getColor(R.color.code_unable_get));
        } else {
            mBtnCode.setText(R.string.get_msg_code);
            mBtnCode.setTextColor(getResources().getColor(R.color.article_content));
        }
    }

    private void initView() {
        mTvTel = findView(R.id.tv_tel);
        mEtPasswd = findView(R.id.et_passswd);
        mEtPasswd.addTextChangedListener(this);
        mEtUsername = findView(R.id.et_user_name);
        mEtUsername.addTextChangedListener(this);
        mEtIdCard = findView(R.id.et_idcard);
        mEtIdCard.addTextChangedListener(this);
        mEtMsgCode = findView(R.id.et_msg_code);
        mEtMsgCode.addTextChangedListener(this);

        mBtnCode = findView(R.id.btn_msg_code);
        mBtnCode.setOnClickListener(this);

        mTvTel.setText(mPhone);

        mBtnRegister = findView(R.id.btn_register);
        mBtnRegister.setOnClickListener(this);

        SwitchButton switchButton = findView(R.id.sb_pwd);
        switchButton.setOnSwitchStateListener(new SwitchButton.OnSwitchListener() {
            @Override
            public void onSwitched(boolean switched) {
                int type = switched ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                mEtPasswd.setInputType(type);
                mEtPasswd.setSelection(mEtPasswd.getText().length());
            }
        });

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
    public void onDestroy() {
        super.onDestroy();
        clearCounter();
    }

    private void startCount() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        mTimeCount = Constants.AUTH_CODE_TIME;
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mTimeCount--;
                if (mTimeCount <= 0) {
                    mHandler.sendEmptyMessage(FLAG_COUNT_FINISH);
                    cancel();
                    mTimer = null;
                } else {
                    mHandler.sendEmptyMessage(FLAG_COUNTING);
                }
            }
        }, 0, 1000);
    }

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

    private void setCodeButtonBg() {
        int alpha = mTvTel.getText().length() > 0
                && mEtMsgCode.getText().length() > 0
                && mEtIdCard.getText().length() > 0
                && mEtUsername.getText().length() > 0
                && mEtPasswd.getText().length()> 0 ?  255 : Constants.BUTTON_UNABLE_ALPHA;
        mBtnRegister.getBackground().setAlpha(alpha);
        mBtnRegister.setClickable(alpha == 255);
    }

    private void clearCounter() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = null;
        mIsSendingCode = false;
        mTimeCount = Constants.AUTH_CODE_TIME;
        if (!mIsPause) {
            mBtnCode.setText(R.string.get_msg_code);
            mBtnCode.setTextColor(getResources().getColor(R.color.article_content));
        }
    }

    private void register() {
        String password = mEtPasswd.getText().toString();
        String code = mEtMsgCode.getText().toString();

        String username = mEtUsername.getText().toString();
        String idcard = mEtIdCard.getText().toString();

        if (mRegistinng) {
            return;
        }

        if (TextUtils.isEmpty(mPhone)) {
            ViewUtils.showToast(this, R.string.input_tip_phone);
            return;
        }

        if (!StringUtils.isValidPhone(mPhone)) {
            ViewUtils.showToast(this, R.string.input_tip_tel_length_error);
            return;
        }

        if (TextUtils.isEmpty(username)) {
            ViewUtils.showToast(this, R.string.input_tip_user_name);
            return;
        }
        if (TextUtils.isEmpty(idcard)) {
            ViewUtils.showToast(this, R.string.input_tip_idcard);
            return;
        }
        if (!IDCardUtils.isValid(idcard)) {
            ViewUtils.showToast(this, R.string.invalid_idcard);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ViewUtils.showToast(this, R.string.tip_passwd);
            return;
        }

        if (password.length() < 6) {
            ViewUtils.showToast(this, R.string.input_tip_tel_pwd_length_error);
            return;
        }

        if (TextUtils.isEmpty(code)) {
            ViewUtils.showToast(this, R.string.input_tip_msg_code);
            return;
        }

        mRegistinng = true;
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("phone", mPhone);
        paramMap.put("password", MD5.encode(password));
        paramMap.put("vCode", code);
        paramMap.put("name", username);
        paramMap.put("idcard", idcard);
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_USER_REGISTER);
        paramMap.put("token", token);

        RequestClient<UserResponse> requestClient = new RequestClient<UserResponse>();
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<UserResponse>() {
            @Override
            public void onLoadComplete(UserResponse response) {
                if (response.code == 0 && response.body != null) {
                    UserStateUtils.getInstance().setUser(response.body);
                    setResult(Activity.RESULT_OK, getIntent());
                    finish();
                } else if (response.code == 99) {
                    UserStateUtils.getInstance().setUser(response.body);
                    ViewUtils.showToast(BaseApplication.getContext(), response.msg);
                    setResult(Activity.RESULT_OK, getIntent());
                    finish();
                } else {
                    ViewUtils.showToast(BaseApplication.getContext(), response.msg);
                }
                mRegistinng = false;
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                mRegistinng = false;
            }
        });
        requestClient.executePost(this,
                getString(R.string.submiting),
                Constants.BASE_URL + Constants.URL_USER_REGISTER,
                UserResponse.class,
                paramMap);
    }

    @Override
    public void onClick(View v) {
        if (ViewUtils.isFastClick()) return;
        switch (v.getId()) {
            case R.id.btn_msg_code:
                ViewUtils.hideInputMethod(this);
                if (mIsSendingCode) {
                    return;
                }

                if (TextUtils.isEmpty(mPhone)) {
                    ViewUtils.showToast(this, R.string.input_tip_phone);
                    return;
                }

                if (!StringUtils.isValidPhone(mPhone)) {
                    ViewUtils.showToast(this, R.string.input_tip_tel_length_error);
                    return;
                }

                String imgCode = mEtImgCode.getText().toString();
                if (TextUtils.isEmpty(imgCode)) {
                    ViewUtils.showToast(this, R.string.pls_input_img_code);
                    return;
                }

                mIsSendingCode = true;
                mBtnCode.setText(getString(R.string.code_sent, String.valueOf(mTimeCount)));
                mBtnCode.setTextColor(getResources().getColor(R.color.code_unable_get));
                startCount();
                sendMsgCode(mPhone);
                break;
            case R.id.btn_register:
                register();
                break;
            default:
                break;
        }
    }

    private void sendMsgCode(String phone) {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("phone", phone);
        paramMap.put("type", String.valueOf(Constants.CODE_TYPE_REGISTER));
        paramMap.put("unique", SharedPreferencesUtils.getInstance().getUUID());
        paramMap.put("vcode", mEtImgCode.getText().toString());
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_SEND_CODE);
        paramMap.put("token", token);

        RequestClient<BaseResponse> requestClient = new RequestClient<BaseResponse>();
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<BaseResponse>() {
            @Override
            public void onLoadComplete(BaseResponse response) {
                if (response.code == 0) {
                    ViewUtils.showToast(BaseApplication.getContext(), R.string.success_send_msg);
                } else {
                    ViewUtils.showToast(BaseApplication.getContext(), response.msg);
                    clearCounter();
                }
            }

            @Override
            public void onNetworkError() {
                ViewUtils.showToast(RegisterStep2Activity.this, R.string.network_error);
                mLogger.error(getString(R.string.network_error));
                clearCounter();
            }
        });
        requestClient.executePost(this,
                getString(R.string.sending_msg_code),
                Constants.BASE_URL + Constants.URL_SEND_CODE,
                BaseResponse.class,
                paramMap);
    }
}