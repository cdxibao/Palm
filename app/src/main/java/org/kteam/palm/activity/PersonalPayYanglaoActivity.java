package org.kteam.palm.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.common.view.pickerview.OptionsPickerView;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.MainActivity;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.view.SpinnerEditText;
import org.kteam.palm.common.view.UserInfoView;
import org.kteam.palm.domain.manager.BaseDataManager;
import org.kteam.palm.model.BaseData;
import org.kteam.palm.model.User;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.GetPayInfoYanglaoResponse;
import org.kteam.palm.network.response.PayCalculateYanglaoResult;
import org.kteam.palm.network.response.PayCalculateYanglaoResultResponse;
import org.kteam.palm.network.response.PayInfoYanglao;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description 设置界面
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-12 18:13
 */
public class PersonalPayYanglaoActivity extends BaseActivity implements View.OnClickListener {
    private final Logger mLogger = Logger.getLogger(getClass());

    private UserInfoView mUserInfoView;
//    private TextView mTvInsuranceOrg;
    private TextView mTvPayYear;
    private TextView mTvPayBeginMonth;
    private SpinnerEditText mSetPayLevel;
    private SpinnerEditText mSetPayEndMonth;
    private OptionsPickerView<BaseData> mPvOptionsMonth;
    private OptionsPickerView<BaseData> mPvOptionsLevel;

    private BaseDataManager mBaseDataManager;
    private PayInfoYanglao mPayInfo;
    private int mPayType = 0;
    private String mUserName = "";
    private String mUserCard = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_pay_yanglao);
        initToolBar();
        setTitleText(getString(R.string.pay_yanglao));
        mBaseDataManager = new BaseDataManager();
        mPayType = getIntent().getIntExtra("payType", 0);
        mUserName = getIntent().getStringExtra("userName");
        mUserCard = getIntent().getStringExtra("userCard");
        initView();
        loadData();
    }

    private void initView() {
        mUserInfoView = findView(R.id.uiv);
//        mTvInsuranceOrg = findView(R.id.tv_insurance_org);
        mTvPayYear = findView(R.id.tv_pay_year);
        mTvPayBeginMonth = findView(R.id.tv_pay_begin_month);

        TextView tvLabelPayBeginMonth = findView(R.id.tv_label_pay_begin_month);
        SpannableString payBeiginMonthSpannable = new SpannableString(getString(R.string.pay_begin_month));
        payBeiginMonthSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, payBeiginMonthSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        payBeiginMonthSpannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, payBeiginMonthSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLabelPayBeginMonth.setText(payBeiginMonthSpannable);

        TextView tvLabelPayEndMonth = findView(R.id.tv_pay_label_end_month);
        SpannableString payEndMonthSpannable = new SpannableString(getString(R.string.pay_end_month));
        payEndMonthSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, payBeiginMonthSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        payEndMonthSpannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, payBeiginMonthSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLabelPayEndMonth.setText(payEndMonthSpannable);

        mSetPayLevel = findView(R.id.set_pay_level);
        mSetPayLevel.setTextWidth(ViewUtils.dip2px(this, 70));
        mSetPayLevel.setOnClickListener(this);

        mSetPayEndMonth = findView(R.id.set_pay_end_month);
        mSetPayEndMonth.setTextWidth(ViewUtils.dip2px(this, 70));
        mSetPayEndMonth.setOnClickListener(this);

        if (mPayType == 1) {
            User user = new User();
            user.name = mUserName;
            user.idcard = mUserCard;
            mUserInfoView.setUser(user);
        } else {
            mUserInfoView.setUser(mUser);
        }
        mUserInfoView.hidePhoneAndAddress();

        findViewById(R.id.btn_pay_calculate).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.go_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (ViewUtils.isFastClick()) return super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == R.id.action_home){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPvOptionsLevel != null) {
            mPvOptionsLevel.dismiss();
        }

        if (mPvOptionsMonth != null) {
            mPvOptionsMonth.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if (ViewUtils.isFastClick()) return;
        switch (v.getId()) {
            case R.id.set_pay_level:
                if (mPvOptionsLevel != null && mPvOptionsLevel.getmOptions1Items() != null && mPvOptionsLevel.getmOptions1Items().size() > 0) {
                    if (mSetPayLevel.getTag() != null && mSetPayLevel.getTag() instanceof  BaseData) {
                        BaseData baseData = (BaseData) mSetPayLevel.getTag();
                        int index = mPvOptionsLevel.getmOptions1Items().indexOf(baseData);
                        if (index != -1) {
                            mPvOptionsLevel.setSelectOptions(index);
                        }
                    }
                    mPvOptionsLevel.show();
                }
                break;
            case R.id.set_pay_end_month:
                if (mPvOptionsMonth != null && mPvOptionsMonth.getmOptions1Items() != null && mPvOptionsMonth.getmOptions1Items().size() > 0) {
                    if (mSetPayEndMonth.getTag() != null && mSetPayEndMonth.getTag() instanceof  BaseData) {
                        BaseData baseData = (BaseData) mSetPayEndMonth.getTag();
                        int index = mPvOptionsMonth.getmOptions1Items().indexOf(baseData);
                        if (index != -1) {
                            mPvOptionsMonth.setSelectOptions(index);
                        }
                    }
                    mPvOptionsMonth.show();
                }
                break;
            case R.id.btn_pay_calculate:
                calculate();
                break;
            default:
                break;
        }
    }

    private void calculate() {
        BaseData endMonthData = (BaseData) mSetPayEndMonth.getTag();
        final BaseData levelData = (BaseData) mSetPayLevel.getTag();

        HashMap<String, String> paramMap = new HashMap<String, String>();
        if (mPayType == 1) {
            if (mPayInfo == null) return;
            paramMap.put("aac001", mPayInfo.aac001);
            paramMap.put("aac002", mUserCard);
            paramMap.put("aac003", mUserName);
        } else {
            paramMap.put("aac001", mPayInfo.aac001);
            paramMap.put("aac002", mUser.idcard);
            paramMap.put("aac003", mUser.name);
        }
        paramMap.put("currPhone", mUser.phone);
        paramMap.put("currAac001", mUser.social_security_card);
        paramMap.put("jfdk", Constants.FIRST_SOURCE);
        paramMap.put("groupid", String.valueOf(mPayInfo.groupid));
        paramMap.put("zac001", String.valueOf(mPayInfo.zac001));
        paramMap.put("zac002", String.valueOf(mPayInfo.zac002));
        if (endMonthData != null) {
            paramMap.put("zac003", String.valueOf(endMonthData.value));
        }
        if (levelData != null) {
            paramMap.put("zac100", String.valueOf(levelData.value));
        }

        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_PAY_CALCULATION_YANGLAO);
        paramMap.put("token", token);

        RequestClient<PayCalculateYanglaoResultResponse> requestClient = new RequestClient<PayCalculateYanglaoResultResponse>();
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<PayCalculateYanglaoResultResponse>() {
            @Override
            public void onLoadComplete(PayCalculateYanglaoResultResponse response) {
                if (response.code == 0 && response.body != null) {
                    PayCalculateYanglaoResult result = response.body;
                    Intent intent = new Intent(PersonalPayYanglaoActivity.this, PayYanglaoCalResultActivity.class);
                    intent.putExtra("payCalculateResult", result);
                    try {
                        if (!TextUtils.isEmpty(levelData.label) && levelData.label.indexOf(" ") != -1) {
                            String[] arr = levelData.label.split(" ");
                            if (arr.length >= 2 && arr[1].indexOf("%") != -1) {
                                String rateStr = arr[1].substring(0, arr[1].indexOf("%"));
                                int rateI = Integer.parseInt(rateStr);
                                float rateF = rateI * 0.01f;
                                String pattern =  rateI % 10 == 0 ? "#.#" : "#.##";
                                DecimalFormat df = new DecimalFormat(pattern);
                                result.aic110 = df.format(rateF);
                            }
                        }
                    } catch (Exception e) {
                        mLogger.error(e.getMessage());
                    }
                    startActivity(intent);
                } else {
                    if (!TextUtils.isEmpty(response.msg)) {
                        ViewUtils.showToast(PersonalPayYanglaoActivity.this, response.msg);
                    } else {
                        ViewUtils.showToast(PersonalPayYanglaoActivity.this, R.string.failed_calculate_pay);
                    }
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                ViewUtils.showToast(PersonalPayYanglaoActivity.this, R.string.network_error);
            }
        });
        requestClient.executePost(this,
                getString(R.string.calculating),
                Constants.BASE_URL + Constants.URL_PAY_CALCULATION_YANGLAO,
                PayCalculateYanglaoResultResponse.class,
                paramMap);
    }

    private void loadData() {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        if (mPayType == 1) {
            paramMap.put("aac001", "");
            paramMap.put("aac002", mUserCard);
            paramMap.put("aac003", mUserName);
        } else {
            paramMap.put("aac001", String.valueOf(mUser.social_security_card));
            paramMap.put("aac002", mUser.idcard);
            paramMap.put("aac003", mUser.name);
        }
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_PAY_INFO_YANGLAO);
        paramMap.put("token", token);

        RequestClient<GetPayInfoYanglaoResponse> requestClient = new RequestClient<GetPayInfoYanglaoResponse>();
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<GetPayInfoYanglaoResponse>() {
            @Override
            public void onLoadComplete(GetPayInfoYanglaoResponse response) {
                if (response.code == 0 && response.body != null) {
                    mPayInfo = response.body;
//                    mTvInsuranceOrg.setText(mPayInfo.groupid);
                    mTvPayYear.setText(getString(R.string.year, mPayInfo.zac001));
                    mTvPayBeginMonth.setText(getString(R.string.month, mPayInfo.zac002));

                    try {
                        List<BaseData> levelList = (ArrayList) mBaseDataManager.getPayedLevel(Integer.parseInt(mPayInfo.zac002) >= 5);
                        mPvOptionsLevel = new OptionsPickerView<BaseData>(PersonalPayYanglaoActivity.this);
                        if (levelList != null && levelList.size() > 0) {
                            if (levelList.size() >= 3) {
                                mPvOptionsLevel.setPicker(new ArrayList<BaseData>(levelList));
                                mPvOptionsLevel.setSelectOptions(2);
                                BaseData baseData = levelList.get(2);
                                mSetPayLevel.setTextWidth((int) ViewUtils.getTextViewLength(mSetPayLevel.getTextView().getPaint(), baseData.label) + ViewUtils.dip2px(PersonalPayYanglaoActivity.this, 15));
                                mSetPayLevel.setText(baseData.label);
                                mSetPayLevel.setTag(baseData);
                            } else if (levelList.size() > 0) {
                                mPvOptionsLevel.setPicker(new ArrayList<BaseData>(levelList));
                                mPvOptionsLevel.setSelectOptions(0);
                                BaseData baseData = levelList.get(0);
                                mSetPayLevel.setTextWidth((int) ViewUtils.getTextViewLength(mSetPayLevel.getTextView().getPaint(), baseData.label) + ViewUtils.dip2px(PersonalPayYanglaoActivity.this, 15));
                                mSetPayLevel.setText(baseData.label);
                                mSetPayLevel.setTag(baseData);
                            }
                        }

                        mPvOptionsLevel.setCyclic(false);
                        mPvOptionsLevel.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                            @Override
                            public void onOptionsSelect(int options1, int option2, int options3) {
                                BaseData baseData = mPvOptionsLevel.getmOptions1Items().get(options1);
                                mSetPayLevel.setText(baseData.label);
                                mSetPayLevel.setTag(baseData);
                            }
                        });
                    } catch (Exception e) {
                        mLogger.error(e.getMessage(), e);
                    }

                    try {
                        mBaseDataManager = new BaseDataManager();
                        ArrayList<BaseData> monthList = (ArrayList) mBaseDataManager.getMonthList(mPayInfo.zac002);
                        if (mPvOptionsMonth == null) {
                            mPvOptionsMonth = new OptionsPickerView<BaseData>(PersonalPayYanglaoActivity.this);
                        }
                        if (monthList != null && monthList.size() > 0) {
                            mPvOptionsMonth.setPicker(monthList);
                            for (int i = monthList.size() - 1; i >= 0; i--) {
                                if (monthList.get(i).value.compareTo(mPayInfo.zac002) < 0) {
                                    monthList.remove(i);
                                }
                            }
                            BaseData baseData = monthList.get(monthList.size() - 1);
                            mSetPayEndMonth.setText(baseData.label);
                            mSetPayEndMonth.setTextWidth((int) ViewUtils.getTextViewLength(mSetPayEndMonth.getTextView().getPaint(), baseData.label) + ViewUtils.dip2px(PersonalPayYanglaoActivity.this, 15));
                            mSetPayEndMonth.setTag(baseData);
                        }
                        mPvOptionsMonth.setCyclic(false);
                        mPvOptionsMonth.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                            @Override
                            public void onOptionsSelect(int options1, int option2, int options3) {
                                BaseData baseData = mPvOptionsMonth.getmOptions1Items().get(options1);
                                mSetPayEndMonth.setTextWidth((int) ViewUtils.getTextViewLength(mSetPayEndMonth.getTextView().getPaint(), baseData.label) + ViewUtils.dip2px(PersonalPayYanglaoActivity.this, 15));
                                mSetPayEndMonth.setText(baseData.label);
                                mSetPayEndMonth.setTag(baseData);
                            }
                        });

                    } catch (Exception e) {
                        mLogger.error(e.getMessage(), e);
                    }

                } else {
                    ViewUtils.showToast(PersonalPayYanglaoActivity.this, response.msg);
                    finish();
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                ViewUtils.showToast(PersonalPayYanglaoActivity.this, R.string.network_error);
                finish();
            }
        });
        requestClient.executePost(this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_PAY_INFO_YANGLAO,
                GetPayInfoYanglaoResponse.class,
                paramMap);
    }
}
