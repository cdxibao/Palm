package org.kteam.palm.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.BaseResponse;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.common.view.ProgressHUD;
import org.kteam.common.view.pickerview.OptionsPickerView;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.BaseApplication;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.utils.IDCardUtils;
import org.kteam.palm.common.utils.UserStateUtils;
import org.kteam.palm.common.view.SpinnerEditText;
import org.kteam.palm.domain.manager.BaseDataManager;
import org.kteam.palm.model.BaseData;
import org.kteam.palm.model.UserInfo;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.UserInfoResponse;
import org.kteam.palm.network.response.UserResponse;

import java.util.ArrayList;
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
 * @Date 2015-12-12 18:27
 */
public class NewYanglaoInsuredActivity extends BaseActivity implements View.OnClickListener, DialogInterface.OnCancelListener {

    private final Logger mLogger = Logger.getLogger(getClass());

    public static final int FLAG_COUNTING = 1001;
    public static final int FLAG_COUNT_FINISH = 1002;

    private EditText mEtIdCard;
    private EditText mEtUserName;
    private EditText mEtRegisteredResidence;
    private EditText mEtMsgCode;
    private EditText mEtPhone;
//    private SpinnerEditText mSetNation;
    private Button mBtnCode;
    private OptionsPickerView<BaseData> mPvOptionsNation;
    private ProgressHUD mProgressHUD;
    private Button mBtnSubmit;

    private Timer mTimer;
    private boolean mIsSendingCode = false;
    private int mTimeCount = Constants.AUTH_CODE_TIME;
    private boolean mIsPause;

    private boolean mNetworking;

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
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_insured_yanglao);
        initToolBar();
        setTitleText(getString(R.string.yanglao_new_insured));

        initView();
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsPause = false;
        setOkButtonBg();
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
        if (mPvOptionsNation != null) {
            mPvOptionsNation.dismiss();
        }
        if (mProgressHUD != null) {
            mProgressHUD.dismiss();
        }
        clearCounter();
    }

    private void initView() {
        mEtIdCard = findView(R.id.et_idcard);
        mEtUserName = findView(R.id.et_user_name);
        mEtRegisteredResidence = findView(R.id.et_registered_residence);
        mEtMsgCode = findView(R.id.et_msg_code);
        mEtPhone = findView(R.id.et_phone);
//        mSetNation = findView(R.id.set_pay_nation);
//        mSetNation.setOnClickListener(this);

        MyTextWatcher myTextWatcher = new MyTextWatcher();
        mEtMsgCode.addTextChangedListener(myTextWatcher);
        mEtUserName.addTextChangedListener(myTextWatcher);
        mEtIdCard.addTextChangedListener(myTextWatcher);

        mBtnCode = findView(R.id.btn_msg_code);
        mBtnCode.setOnClickListener(this);

        mBtnSubmit = findView(R.id.btn_submit);
        mBtnSubmit.setOnClickListener(this);

        if (mUser != null && !TextUtils.isEmpty(mUser.phone)) {
            mEtPhone.setText(mUser.phone);
        }
//        mPvOptionsNation = new OptionsPickerView<BaseData>(this);
//        ArrayList<BaseData> nationList = (ArrayList<BaseData>) new BaseDataManager().getNationList();
//        if (nationList != null && nationList.size() > 0) {
//            mPvOptionsNation.setPicker(nationList);
//            mPvOptionsNation.setSelectOptions(0);
//            BaseData nationBaseData = nationList.get(0);
//            mSetNation.setText(nationBaseData.label);
//            mSetNation.setTag(nationBaseData);
//        }
//        mPvOptionsNation.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
//            @Override
//            public void onOptionsSelect(int options1, int option2, int options3) {
//                BaseData baseData = mPvOptionsNation.getmOptions1Items().get(options1);
//                mSetNation.setTag(baseData);
//                mSetNation.setText(baseData.label);
//            }
//        });
        mProgressHUD = new ProgressHUD(this);
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

    @Override
    public void onCancel(DialogInterface dialog) {
        finish();
    }

    @Override
    public void onClick(View v) {
        if (ViewUtils.isFastClick()) return;
        if (!checkUserLogin()) return;
        switch (v.getId()) {
            case R.id.btn_msg_code:
                ViewUtils.hideInputMethod(this);
                if (mIsSendingCode) {
                    return;
                }

                mIsSendingCode = true;
                mBtnCode.setText(getString(R.string.code_sent, mTimeCount));
                mBtnCode.setTextColor(getResources().getColor(R.color.code_unable_get));
                startCount();

                sendMsgCode(mUser.phone);
                break;
//            case R.id.set_pay_nation:
//                if (mPvOptionsNation != null && mPvOptionsNation.getmOptions1Items() != null && mPvOptionsNation.getmOptions1Items().size() > 0) {
//                    if (mSetNation.getTag() != null && mSetNation.getTag() instanceof BaseData) {
//                        BaseData baseData = (BaseData) mSetNation.getTag();
//                        int index = mPvOptionsNation.getmOptions1Items().indexOf(baseData);
//                        if (index != -1) {
//                            mPvOptionsNation.setSelectOptions(index);
//                        }
//                    }
//                    mPvOptionsNation.show();
//                }
//                break;
            case R.id.btn_submit:
                submit();
                break;
            default:
                break;
        }
    }

    private void sendMsgCode(String phone) {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("phone", phone);
        paramMap.put("type", String.valueOf(Constants.CODE_TYPE_RPERSON_INSURANCE));
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

    private void submit() {
        if (mNetworking) return;
        String idCard = mEtIdCard.getText().toString();
        String username = mEtUserName.getText().toString();
//        String registeredResidence = mEtRegisteredResidence.getText().toString();
        String code = mEtMsgCode.getText().toString();
//        BaseData baseData = (BaseData) mSetNation.getTag();

        if (TextUtils.isEmpty(idCard) || !IDCardUtils.isValid(idCard)) {
            ViewUtils.showToast(this, R.string.invalid_idcard);
            return;
        }

        String groupid = idCard.substring(0, 6);
        String gender = IDCardUtils.GENDER_MALE.equals(IDCardUtils.getGenderByIdCard(idCard)) ? "1" : "2";
        String birthday = IDCardUtils.getBirthByIdCard(idCard);

        if (TextUtils.isEmpty(username)) {
            ViewUtils.showToast(this, R.string.input_tip_user_name);
            return;
        }

//        if (TextUtils.isEmpty(registeredResidence)) {
//            ViewUtils.showToast(this, R.string.input_tip_registered_residence);
//            return;
//        }

        if (TextUtils.isEmpty(code)) {
            ViewUtils.showToast(this, R.string.input_tip_msg_code);
            return;
        }

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("aac002", idCard);
        paramMap.put("aac003", username);
        paramMap.put("aac004", gender);
        paramMap.put("aac005", "01");
        paramMap.put("aac006", birthday);
        paramMap.put("aac010", "");
        paramMap.put("aae005", mUser.phone);
        paramMap.put("groupid", groupid);
        paramMap.put("djdk", Constants.FIRST_SOURCE);
        paramMap.put("openid", "");
        paramMap.put("vCode", code);
        paramMap.put("password", "");
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_NEW_INSURANCE_YANGLAO);
        paramMap.put("token", token);

        mProgressHUD.show(getString(R.string.submiting), true, true, this);
        RequestClient<UserResponse> requestClient = new RequestClient<UserResponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<UserResponse>() {
            @Override
            public void onLoadComplete(UserResponse response) {
                if (response.code == 0) {
                    loadUserInfo();
                } else {
                    mProgressHUD.hide();
                    ViewUtils.showToast(BaseApplication.getContext(), response.msg);
                    mNetworking = false;
                }
            }

            @Override
            public void onNetworkError() {
                mProgressHUD.hide();
                mLogger.error(getString(R.string.network_error));
                mNetworking = false;
            }
        });
        requestClient.executePost(this,
                getString(R.string.submiting),
                Constants.BASE_URL + Constants.URL_NEW_INSURANCE_YANGLAO,
                UserResponse.class,
                paramMap);
    }

    private void loadUserInfo() {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("phone", mUser.phone);

        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_LOAD_USER_INFO);
        paramMap.put("token", token);

        RequestClient<UserResponse> requestClient = new RequestClient<UserResponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<UserResponse>() {
            @Override
            public void onLoadComplete(UserResponse response) {
                if (response.code == 0 && response.body != null) {
                    mUser.level = response.body.level;
                    mUser.name = response.body.name;
                    mUser.idcard = response.body.idcard;
                    mUser.social_security_card = response.body.social_security_card;
                    UserStateUtils.getInstance().setUser(mUser);
                }
                mProgressHUD.hide();
                mNetworking = false;
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onNetworkError() {
                mNetworking = false;
                mProgressHUD.hide();
                setResult(RESULT_OK);
                finish();
            }
        });
        requestClient.executePost(this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_LOAD_USER_INFO,
                UserResponse.class,
                paramMap);
    }

    private void loadData() {
        if (mUser.level <= 1) {
            return;
        }
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
                        mEtUserName.setText(userInfo.aac003);
                        mEtIdCard.setText(userInfo.aac002);
                        mEtUserName.setEnabled(false);
                        mEtIdCard.setEnabled(false);
                    }
                } else {
                    if (!TextUtils.isEmpty(response.msg)) {
                        ViewUtils.showToast(NewYanglaoInsuredActivity.this, response.msg);
                    } else {
                        ViewUtils.showToast(NewYanglaoInsuredActivity.this, R.string.load_user_info_failed);
                    }
                    finish();
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                ViewUtils.showToast(NewYanglaoInsuredActivity.this, R.string.network_error);
                finish();
            }
        });
        requestClient.executePost(this,
                getString(R.string.loading_user_info),
                Constants.BASE_URL + Constants.URL_GET_USER_INFO,
                UserInfoResponse.class,
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

    private void setOkButtonBg() {
        int alpha = mEtIdCard.getText().length() > 0 && mEtUserName.getText().length() > 0 && mEtUserName.getText().length() > 0 && mEtMsgCode.getText().length() > 0 ? 255 : Constants.BUTTON_UNABLE_ALPHA;
        mBtnSubmit.getBackground().setAlpha(alpha);
        mBtnSubmit.setClickable(alpha == 255);
    }
}