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

import com.sina.weibo.sdk.api.share.Base;

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
import org.kteam.palm.network.response.GetPayInfoYiliaoResponse;
import org.kteam.palm.network.response.PayCalculateYiliaoResult;
import org.kteam.palm.network.response.PayCalculateYiliaoResultResponse;
import org.kteam.palm.network.response.PayInfoYililao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description 医疗保险缴费
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-12 18:13
 */
public class PersonalPayYiliaoActivity extends BaseActivity implements View.OnClickListener {
    private final Logger mLogger = Logger.getLogger(getClass());

    private UserInfoView mUserInfoView;
    //    private TextView mTvInsuranceOrg;
    private TextView mTvPayYear;
    private TextView mTvPayBeginMonth;
    private SpinnerEditText mSetPayEndMonth;
    private OptionsPickerView<BaseData> mPvOptionsMonth;

    private BaseDataManager mBaseDataManager;
    private PayInfoYililao mPayInfo;
    private int mPayType = 0;
    private String mUserName = "";
    private String mUserCard = "";
    private BaseData mSelectedYear;
    private BaseData mSelectedMonth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_pay_yiliao);
        initToolBar();
        setTitleText(getString(R.string.pay_yiliao));
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
        SpannableString payBeiginMonthSpannable = new SpannableString(getString(R.string.pay_begin_year_month1));
        payBeiginMonthSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, payBeiginMonthSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        payBeiginMonthSpannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, payBeiginMonthSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLabelPayBeginMonth.setText(payBeiginMonthSpannable);

        TextView tvLabelPayEndMonth = findView(R.id.tv_pay_label_end_month);
        SpannableString payEndMonthSpannable = new SpannableString(getString(R.string.pay_end_year_month1));
        payEndMonthSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, payBeiginMonthSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        payEndMonthSpannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, payBeiginMonthSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLabelPayEndMonth.setText(payEndMonthSpannable);

        mSetPayEndMonth = findView(R.id.set_pay_end_month);
        mSetPayEndMonth.setTextWidth(ViewUtils.dip2px(this, 90));
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
        if (mPvOptionsMonth != null) {
            mPvOptionsMonth.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if (ViewUtils.isFastClick()) return;
        switch (v.getId()) {
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
        HashMap<String, String> paramMap = new HashMap<String, String>();
        if (mPayType == 1) {
            if (mPayInfo == null) return;
            paramMap.put("aagrid", mPayInfo.aagrid);
            paramMap.put("aac002", mUserCard);
            paramMap.put("aac003", mUserName);
        } else {
            paramMap.put("aagrid", mPayInfo.aagrid);
            paramMap.put("aac002", mUser.idcard);
            paramMap.put("aac003", mUser.name);
        }
        paramMap.put("currPhone", mUser.phone);
        paramMap.put("currAac001", mUser.social_security_card);
        paramMap.put("jfdk", Constants.FIRST_SOURCE);
        paramMap.put("yab003", String.valueOf(mPayInfo.yab003));
        paramMap.put("aae041", String.valueOf(mPayInfo.aae041));
        if (mSelectedYear != null && mSelectedMonth != null) {
            paramMap.put("aae042", mSelectedYear.value + "" + mSelectedMonth.value);
        }

        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_PAY_CALCULATION_YILIAO);
        paramMap.put("token", token);

        RequestClient<PayCalculateYiliaoResultResponse> requestClient = new RequestClient<PayCalculateYiliaoResultResponse>();
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<PayCalculateYiliaoResultResponse>() {
            @Override
            public void onLoadComplete(PayCalculateYiliaoResultResponse response) {
                if (response.code == 0 && response.body != null) {
                    PayCalculateYiliaoResult result = response.body;
                    Intent intent = new Intent(PersonalPayYiliaoActivity.this, PayYiliaoCalResultActivity.class);
                    intent.putExtra("payCalculateResult", result);
                    startActivity(intent);
                } else {
                    if (!TextUtils.isEmpty(response.msg)) {
                        ViewUtils.showToast(PersonalPayYiliaoActivity.this, response.msg);
                    } else {
                        ViewUtils.showToast(PersonalPayYiliaoActivity.this, R.string.failed_calculate_pay);
                    }
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                ViewUtils.showToast(PersonalPayYiliaoActivity.this, R.string.network_error);
            }
        });
        requestClient.executePost(this,
                getString(R.string.calculating),
                Constants.BASE_URL + Constants.URL_PAY_CALCULATION_YILIAO,
                PayCalculateYiliaoResultResponse.class,
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
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_PAY_INFO_YILIAO);
        paramMap.put("token", token);

        RequestClient<GetPayInfoYiliaoResponse> requestClient = new RequestClient<GetPayInfoYiliaoResponse>();
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<GetPayInfoYiliaoResponse>() {
            @Override
            public void onLoadComplete(GetPayInfoYiliaoResponse response) {
                if (response.code == 0 && response.body != null) {
                    mPayInfo = response.body;
                    String startMonth = "";
                    String startYear = "";
                    if (!TextUtils.isEmpty(mPayInfo.aae041)) {
                        startYear = mPayInfo.aae041.substring(0, 4);
                        startMonth = mPayInfo.aae041.substring(4, 6);
                        mTvPayBeginMonth.setText(getString(R.string.year_month, startYear, startMonth));
                    }

                    String endMonth = "12";
                    String endYear = "";
                    if (!TextUtils.isEmpty(mPayInfo.aae041)) {
                        endMonth = mPayInfo.aae042.substring(4, 6);
                        endYear = mPayInfo.aae042.substring(0, 4);
                    }

                    try {
                        mBaseDataManager = new BaseDataManager();
                        ArrayList<BaseData> monthList = (ArrayList) mBaseDataManager.getMonthList();
                        if (mPvOptionsMonth == null) {
                            mPvOptionsMonth = new OptionsPickerView<BaseData>(PersonalPayYiliaoActivity.this);
                        }

                        if (monthList != null && monthList.size() > 0) {

                            int startYearValue = Integer.parseInt(startYear);
                            int endYearValue = Integer.parseInt(endYear);

                            ArrayList<BaseData> yearDataList = new ArrayList<BaseData>();
                            ArrayList<ArrayList<BaseData>> monthDataList = new ArrayList<ArrayList<BaseData>>();

                            for (int i = startYearValue; i <= endYearValue; i++) {
                                BaseData baseData = new BaseData();
                                String year = String.valueOf(i);
                                baseData.label = getString(R.string.year, year);
                                baseData.value = year;
                                yearDataList.add(baseData);
                                if (i == startYearValue) {
                                    ArrayList<BaseData> monthListTemp = new ArrayList<BaseData>(monthList);
                                    for (int j = monthListTemp.size() - 1; j >= 0; j--) { //开始年，筛选月份
                                        if (monthListTemp.get(j).value.compareTo(startMonth) < 0) {
                                            monthListTemp.remove(j);
                                        }
                                    }
                                    monthDataList.add(monthListTemp);
                                } else if (i == endYearValue) {
                                    ArrayList<BaseData> monthListTemp = new ArrayList<BaseData>(monthList);
                                    for (int j = monthListTemp.size() - 1; j >= 0; j--) { //开始年，筛选月份
                                        if (monthListTemp.get(j).value.compareTo(endMonth) > 0) {
                                            monthListTemp.remove(j);
                                        }
                                    }
                                    monthDataList.add(monthListTemp);
                                } else {
                                    monthDataList.add(monthList);
                                }
                            }
                            mPvOptionsMonth.setPicker(yearDataList, monthDataList, true);

                            int yearIndex = yearDataList.size() - 1;
                            int monthIndex = monthDataList.get(yearIndex).size() - 1;
                            mSelectedYear = yearDataList.get(yearIndex);
                            mSelectedMonth = monthDataList.get(yearIndex).get(monthIndex);
                            String label = mSelectedYear.label + mSelectedMonth.label;
                            mSetPayEndMonth.setText(label);
                            mSetPayEndMonth.setTextWidth((int) ViewUtils.getTextViewLength(mSetPayEndMonth.getTextView().getPaint(), label) + ViewUtils.dip2px(PersonalPayYiliaoActivity.this, 15));

                            mPvOptionsMonth.setSelectOptions(yearIndex, monthIndex);
                        }
                        mPvOptionsMonth.setCyclic(false);
                        mPvOptionsMonth.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                            @Override
                            public void onOptionsSelect(int options1, int option2, int options3) {
                                BaseData yearData = mPvOptionsMonth.getmOptions1Items().get(options1);
                                BaseData monthData = mPvOptionsMonth.getmOptions2Items(options1).get(option2);
                                mSelectedYear = yearData;
                                mSelectedMonth = monthData;
                                String label = yearData.label + monthData.label;
                                mSetPayEndMonth.setTextWidth((int) ViewUtils.getTextViewLength(mSetPayEndMonth.getTextView().getPaint(), label) + ViewUtils.dip2px(PersonalPayYiliaoActivity.this, 15));
                                mSetPayEndMonth.setText(label);
                            }
                        });

                    } catch (Exception e) {
                        mLogger.error(e.getMessage(), e);
                    }

                } else {
                    ViewUtils.showToast(PersonalPayYiliaoActivity.this, response.msg);
                    finish();
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                ViewUtils.showToast(PersonalPayYiliaoActivity.this, R.string.network_error);
                finish();
            }
        });
        requestClient.executePost(this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_PAY_INFO_YILIAO,
                GetPayInfoYiliaoResponse.class,
                paramMap);
    }
}
