package org.kteam.palm.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.BaseResponse;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.StringUtils;
import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.BaseApplication;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.utils.UserStateUtils;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.UserResponse;

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
 * @Date 2016-01-24 22:00
 */
public class UpdateUserInfoActivity extends BaseActivity implements View.OnClickListener{
    private final Logger mLogger = Logger.getLogger(getClass());

    public static final int FLAG_COUNTING = 1001;
    public static final int FLAG_COUNT_FINISH = 1002;

    private EditText mEtPhone;
    private EditText mEtNewPhone;
    private EditText mEtMsgCode;
    private EditText mEtContactAddr;
    private View mLayoutAddress;
    private View mLineAdrress;
    private Button mBtnCode;
    private Button mBtnOk;
    private boolean mUpdating;

    private Timer mTimer;
    private boolean mIsSendingCode = false;
    private int mTimeCount = Constants.AUTH_CODE_TIME;
    private boolean mIsPause;

    private String mNewPhone;

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
        setContentView(R.layout.activity_update_userinfo);
        initToolBar();
        setTitleText(getString(R.string.userinfo_update));

        initView();
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
            mBtnCode.setText(getString(R.string.code_sent, mTimeCount));
            mBtnCode.setTextColor(getResources().getColor(R.color.code_unable_get));
        } else {
            mBtnCode.setText(R.string.get_msg_code);
            mBtnCode.setTextColor(getResources().getColor(R.color.article_content));
        }
        setOkButtonBg();
    }

    private void initView() {

        mEtPhone = findView(R.id.et_phone);
        mEtPhone.setText(mUser.phone);
        mEtPhone.setEnabled(false);
        mEtNewPhone = findView(R.id.et_new_phone);
        mEtMsgCode = findView(R.id.et_msg_code);
        mEtContactAddr = findView(R.id.et_new_address);
        mEtContactAddr.setText(mUser.address);

        mBtnCode = findView(R.id.btn_msg_code);
        mBtnCode.setOnClickListener(this);

        mBtnOk = findView(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);

        MyTextWatcher myTextWatcher = new MyTextWatcher();
        mEtPhone.addTextChangedListener(myTextWatcher);
        mEtNewPhone.addTextChangedListener(myTextWatcher);
        mEtMsgCode.addTextChangedListener(myTextWatcher);

        mLineAdrress = findView(R.id.line_address);
        mLayoutAddress = findView(R.id.layout_address);
        if (mUser.level <= 2) {
            mLayoutAddress.setVisibility(View.GONE);
            mLineAdrress.setVisibility(View.GONE);
            setTitleText(getString(R.string.update_phone));
        }
    }

    private void setOkButtonBg() {
        int alpha = mEtPhone.getText().length() > 0 && mEtNewPhone.getText().length() > 0 && mEtMsgCode.getText().length() > 0 ?  255 : Constants.BUTTON_UNABLE_ALPHA;
        mBtnOk.getBackground().setAlpha(alpha);
        mBtnOk.setClickable(alpha == 255);
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

    private void update() {
        String phone = mEtPhone.getText().toString();
        final String newPhobne = mEtNewPhone.getText().toString();
        String code = mEtMsgCode.getText().toString();

        if (mUpdating) {
            return;
        }

        if (!checkData()) {
            return;
        }

        if (TextUtils.isEmpty(code)) {
            ViewUtils.showToast(this, R.string.input_tip_msg_code);
            return;
        }

        if (!newPhobne.equals(mNewPhone)) {
            ViewUtils.showToast(this, R.string.pls_reget_msg_code);
            return;
        }

        mUpdating = true;
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("oldPhone", phone);
        paramMap.put("newPhone",newPhobne);
        paramMap.put("address", mEtContactAddr.getText().toString().trim());
        paramMap.put("vCode", code);
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_UPDATE_PHONE);
        paramMap.put("token", token);

        RequestClient<UserResponse> requestClient = new RequestClient<UserResponse>();
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<UserResponse>() {
            @Override
            public void onLoadComplete(UserResponse response) {
                if (response.code == 0) {
                    mUser.phone = newPhobne;
                    UserStateUtils.getInstance().setUser(mUser);
                    finish();
                } else {
                    if (!TextUtils.isEmpty(response.msg)) {
                        ViewUtils.showToast(BaseApplication.getContext(), response.msg);
                    } else {
                        ViewUtils.showToast(BaseApplication.getContext(), R.string.failed_update_msg);
                    }
                }
                mUpdating = false;
            }

            @Override
            public void onNetworkError() {
                ViewUtils.showToast(UpdateUserInfoActivity.this, R.string.network_error);
                mLogger.error(getString(R.string.network_error));
                mUpdating = false;
            }
        });
        requestClient.executePost(this,
                getString(R.string.submiting),
                Constants.BASE_URL + Constants.URL_UPDATE_PHONE,
                UserResponse.class,
                paramMap);
    }

    private boolean checkData() {
        String phone = mEtPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ViewUtils.showToast(this, R.string.input_tip_phone);
            return false;
        }

        if (!StringUtils.isValidPhone(phone)) {
            ViewUtils.showToast(this, R.string.input_tip_tel_length_error);
            return false;
        }

        String newPhone = mEtNewPhone.getText().toString();
        if (TextUtils.isEmpty(newPhone)) {
            ViewUtils.showToast(this, R.string.input_tip_phone_new);
            return false;
        }

        if (!StringUtils.isValidPhone(newPhone)) {
            ViewUtils.showToast(this, R.string.input_tip_new_tel_length_error);
            return false;
        }
        if (mUser.level <= 2 && phone.equals(newPhone)) {
            ViewUtils.showToast(this, R.string.phone_newphone_same);
            return false;
        }
        return true;
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

                if (!checkData()) {
                    return;
                }
                mIsSendingCode = true;
                mBtnCode.setText(getString(R.string.code_sent, mTimeCount));
                mBtnCode.setTextColor(getResources().getColor(R.color.code_unable_get));
                startCount();
                mNewPhone = mEtNewPhone.getText().toString();
                sendMsgCode(mNewPhone);
                break;
            case R.id.btn_ok:
                update();
                break;
            default:
                break;
        }
    }

    private void sendMsgCode(String phone) {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("phone", phone);
        paramMap.put("type", String.valueOf(Constants.CODE_TYPE_UPDATE));
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
