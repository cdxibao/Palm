package org.kteam.palm.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import org.kteam.palm.common.view.SwitchButton;
import org.kteam.palm.network.NetworkUtils;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-10 22:57
 */
public class RefindPasswdStep2Activity extends BaseActivity implements View.OnClickListener {
    private final Logger mLogger = Logger.getLogger(getClass());

    private TextView mTvPhone;
    private EditText mEtPasswd;
    private EditText mEtAuthCode;
    private Button mBtnCode;

    private Timer mTimer;
    private boolean mIsSendingCode = false;
    private int mTimeCount = Constants.AUTH_CODE_TIME;
    private boolean mIsPause;
    private String mPhone = "";
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
                    mBtnCode.setText(getString(R.string.code_sent, mTimeCount));
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

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refind_passwd_step2);
        super.initToolBar(true);
        setTitleRes(R.string.refind_passwd);
        mPhone = getIntent().getStringExtra("phone");
        if (TextUtils.isEmpty(mPhone)) {
            finish();
            return;
        }
        initView();
        mIsSendingCode = true;
        mBtnCode.setText(getString(R.string.code_sent, mTimeCount));
        mBtnCode.setTextColor(getResources().getColor(R.color.code_unable_get));
        startCount();
    }

    private void initView() {
        mTvPhone = (TextView) findViewById(R.id.tv_phone);
        mEtPasswd = (EditText) findViewById(R.id.et_passwd);
        mEtAuthCode = (EditText) findViewById(R.id.et_code);
        mBtnCode = (Button) findViewById(R.id.btn_code);
        mBtnCode.setOnClickListener(this);
        findViewById(R.id.btn_ok).setOnClickListener(this);

        SwitchButton switchButton = findView(R.id.sb_pwd);
        switchButton.setOnSwitchStateListener(new SwitchButton.OnSwitchListener() {
            @Override
            public void onSwitched(boolean switched) {
                int type = switched ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                mEtPasswd.setInputType(type);
                mEtPasswd.setSelection(mEtPasswd.getText().length());
            }
        });
        mTvPhone.setText(mPhone);
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsPause = false;
        if (mIsSendingCode) {
            mBtnCode.setText(getString(R.string.code_sent, mTimeCount));
            mBtnCode.setTextColor(getResources().getColor(R.color.code_unable_get));
        } else {
            mBtnCode.setText(R.string.get_msg_code);
            mBtnCode.setTextColor(getResources().getColor(R.color.article_content));
        }
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

    private boolean checkData() {
        if (TextUtils.isEmpty(mPhone)) {
            ViewUtils.showToast(this, R.string.input_tip_phone);
            return false;
        }

        if (!StringUtils.isValidPhone(mPhone)) {
            ViewUtils.showToast(this, R.string.input_tip_tel_length_error);
            return false;
        }

        if (TextUtils.isEmpty(mEtAuthCode.getText())) {
            ViewUtils.showToast(this, R.string.tip_msg_code);
            return false;
        }
        if (TextUtils.isEmpty(mEtPasswd.getText())) {
            ViewUtils.showToast(this, R.string.tip_passwd);
            return false;
        }
        if (mEtPasswd.getText().length() < 6) {
            ViewUtils.showToast(this, R.string.input_tip_tel_pwd_length_error);
            return false;
        }
        return true;
    }

    private void submit() {
        if (mIsNetworking || !checkData()) {
            return;
        }
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("phone", mPhone);
        paramMap.put("password", MD5.encode(mEtPasswd.getText().toString()));
        paramMap.put("vCode", mEtAuthCode.getText().toString());
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_REFIND_PWD);
        paramMap.put("token", token);

        mIsNetworking = true;
        RequestClient<BaseResponse> requestClient = new RequestClient<BaseResponse>();
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<BaseResponse>() {
            @Override
            public void onLoadComplete(BaseResponse response) {
                if (response.code == 0) {
                    setResult(Activity.RESULT_OK, getIntent());
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
                Constants.BASE_URL + Constants.URL_REFIND_PWD,
                BaseResponse.class,
                paramMap);
    }

    private void sendMsgCode(String phone) {
        if (mIsNetworking) return;
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("phone", phone);
        paramMap.put("type", String.valueOf(Constants.CODE_TYPE_REFIND_PWD));
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_SEND_CODE);
        paramMap.put("token", token);

        mIsNetworking = true;
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
                mIsNetworking = false;
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                mIsNetworking = false;
                clearCounter();
            }
        });
        requestClient.executePost(this,
                getString(R.string.sending_msg_code),
                Constants.BASE_URL + Constants.URL_SEND_CODE,
                BaseResponse.class,
                paramMap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_code:
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
                mIsSendingCode = true;
                mBtnCode.setText(getString(R.string.code_sent, mTimeCount));
                mBtnCode.setTextColor(getResources().getColor(R.color.code_unable_get));
                startCount();
                sendMsgCode(mPhone);
                break;
            case R.id.btn_ok:
                submit();
                break;
            default:
                break;
        }
    }
}