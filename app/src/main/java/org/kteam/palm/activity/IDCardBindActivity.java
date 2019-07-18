package org.kteam.palm.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.BaseResponse;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.BaseApplication;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.utils.IDCardUtils;
import org.kteam.palm.common.utils.SharedPreferencesUtils;
import org.kteam.palm.common.utils.UserStateUtils;
import org.kteam.palm.common.utils.VCodeImageUtils;
import org.kteam.palm.model.User;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.UserResponse;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description 设置界面
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-12 18:13
 */
public class IDCardBindActivity extends BaseActivity implements View.OnClickListener {
    private final Logger mLogger = Logger.getLogger(getClass());

    private EditText mEtIDCard;
    private EditText mEtUserName;
    private EditText mEtAuthCode;
    private TextView mTvPhone;
    private Button mBtnCode;
    private Button mBtnOk;
    private EditText mEtImgCode;
    private SimpleDraweeView mSdv;

    private Timer mTimer;
    private boolean mIsSendingCode = false;
    private int mTimeCount = Constants.AUTH_CODE_TIME;
    private boolean mIsPause;
    private boolean mIsNetworking = false;

    public static final int FLAG_COUNTING = 1001;
    public static final int FLAG_COUNT_FINISH = 1002;

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
                    mBtnCode.setTextColor(getResources().getColor(R.color.article_content));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcard_bind);
        initToolBar();
        setTitleText(getString(R.string.bind_idcard));
        initView();
    }

    private void initView() {
        mTvPhone = findView(R.id.tv_phone);
        mEtIDCard = findView(R.id.et_idcard);
        mEtUserName = findView(R.id.et_user_name);
        mEtAuthCode = findView(R.id.et_code);
        mEtAuthCode.requestFocus();
        mEtAuthCode.setFocusable(true);
        mEtAuthCode.setFocusableInTouchMode(true);
        mBtnCode = findView(R.id.btn_code);
        mBtnCode.setOnClickListener(this);
        mBtnOk = findView(R.id.btn_submit);
        mBtnOk.setOnClickListener(this);

        if (mUser != null) {
            mTvPhone.setText(mUser.phone);
            if (mUser.level == 2) {
                mEtIDCard.setText(mUser.idcard);
                mEtUserName.setText(mUser.name);
            }
        }

        MyTextWatcher myTextWatcher = new MyTextWatcher();
        mTvPhone.addTextChangedListener(myTextWatcher);
        mEtIDCard.addTextChangedListener(myTextWatcher);
        mEtUserName.addTextChangedListener(myTextWatcher);
        mEtAuthCode.addTextChangedListener(myTextWatcher);

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
        setOkButtonBg();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsPause = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearCounter();
    }

    private void setOkButtonBg() {
        int alpha = mTvPhone.getText().length() > 0 && mEtIDCard.getText().length() > 0 && mEtUserName.getText().length() > 0 && mEtAuthCode.getText().length() > 0 ?  255 : Constants.BUTTON_UNABLE_ALPHA;
        mBtnOk.getBackground().setAlpha(alpha);
        mBtnOk.setClickable(alpha == 255);
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

    private void sendMsgCode() {
        String idCard = mEtIDCard.getText().toString();
        String username = mEtUserName.getText().toString();

        ViewUtils.hideInputMethod(this);

        if (mIsSendingCode) return;

        if (TextUtils.isEmpty(idCard)) {
            ViewUtils.showToast(this, R.string.input_tip_idcard);
            return;
        }
        if (!IDCardUtils.isValid(idCard)) {
            ViewUtils.showToast(this, R.string.invalid_idcard);
            return;
        }

        if (TextUtils.isEmpty(username)) {
            ViewUtils.showToast(this, R.string.input_tip_user_name);
            return;
        }

        String imgCode = mEtImgCode.getText().toString();
        if (TextUtils.isEmpty(imgCode)) {
            ViewUtils.showToast(this, R.string.pls_input_img_code);
            return;
        }

        startCount();

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("phone", mUser.phone);
        paramMap.put("type", String.valueOf(Constants.CODE_TYPE_BIND));
        paramMap.put("unique", SharedPreferencesUtils.getInstance().getUUID());
        paramMap.put("vcode", mEtImgCode.getText().toString());
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_SEND_CODE);
        paramMap.put("token", token);

        RequestClient<BaseResponse> requestClient = new RequestClient<BaseResponse>();
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<BaseResponse>() {
            @Override
            public void onLoadComplete(BaseResponse response) {
                if (response.code == 0) {
                    Toast.makeText(IDCardBindActivity.this, R.string.success_send_msg, Toast.LENGTH_LONG).show();
                    ViewUtils.showToast(BaseApplication.getContext(), R.string.success_send_msg);
                } else {
                    ViewUtils.showToast(BaseApplication.getContext(), response.msg);
                    clearCounter();
                }
            }

            @Override
            public void onNetworkError() {
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

    private void submit() {
        final String idCard = mEtIDCard.getText().toString();
        final String username = mEtUserName.getText().toString();
        String authCode = mEtAuthCode.getText().toString();

        if (TextUtils.isEmpty(idCard)) {
            ViewUtils.showToast(this, R.string.input_tip_idcard);
            return;
        }
        if (!IDCardUtils.isValid(idCard)) {
            ViewUtils.showToast(this, R.string.invalid_idcard);
            return;
        }

        if (TextUtils.isEmpty(username)) {
            ViewUtils.showToast(this, R.string.input_tip_user_name);
            return;
        }

        if (TextUtils.isEmpty(authCode)) {
            ViewUtils.showToast(this, R.string.input_tip_msg_code);
            return;
        }
        if (mIsNetworking) return;

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("aac002", idCard);
        paramMap.put("aac003", username);
        paramMap.put("aae005",  mUser.phone);
        paramMap.put("vCode", authCode);

        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_BIND_IDCARD);
        paramMap.put("token", token);

        mIsNetworking = true;
        RequestClient<UserResponse> requestClient = new RequestClient<UserResponse>();
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<UserResponse>() {
            @Override
            public void onLoadComplete(UserResponse response) {
                if (response.code == 0 && response.body != null) {
                    mUser.name = username;
                    mUser.id = response.body.id;
                    mUser.social_security_card = response.body.social_security_card;
                    mUser.level = response.body.level;
                    mUser.idcard = idCard;
                    UserStateUtils.getInstance().setUser(mUser);
                    finish();
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
                Constants.BASE_URL + Constants.URL_BIND_IDCARD,
                UserResponse.class,
                paramMap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_code:
                sendMsgCode();
                break;
            case R.id.btn_submit:
                submit();
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
