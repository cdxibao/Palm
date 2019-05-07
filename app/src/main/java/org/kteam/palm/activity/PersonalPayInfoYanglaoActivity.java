package org.kteam.palm.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.common.view.ProgressHUD;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.MainActivity;
import org.kteam.palm.R;
import org.kteam.palm.adapter.PayYanglaoInfoAdapter;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.view.HorizontalItemDecoration;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.PayDisposable;
import org.kteam.palm.network.response.PayDisposableResponse;
import org.kteam.palm.network.response.PersonalYanglaoPayInfo;
import org.kteam.palm.network.response.PersonalYanglaoPayResponse;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description  职工养老保险个体缴费记录
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-12 18:13
 */
public class PersonalPayInfoYanglaoActivity extends BaseActivity implements DialogInterface.OnCancelListener {
    private final Logger mLogger = Logger.getLogger(getClass());

    private View mLayoutContent;
    private View mLayoutEmpty;

    private PayYanglaoInfoAdapter mPayYanglaoInfoAdapter;

    private int mItem1Width;
    private int mItem2Width;
    private int mItem3Width;
    private int mItem4Width;

    private View mLayoutBzd;
    private TextView mTvBzdPayMonth;
    private TextView mTvBzdPayMoney;

    private ProgressHUD mProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_pay_info_yanglao);
        initToolBar();
        if (mUser == null) {
            finish();
            return;
        }
        setTitleText(getString(R.string.personal_pay_yanglao_info_title, mUser.name));

        initView();
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressHUD != null) {
            mProgressHUD.dismiss();
        }
    }

    private void initView() {
        mProgressHUD = new ProgressHUD(this);

        mLayoutContent = findView(R.id.layout_content);
        mLayoutEmpty = findView(R.id.layout_empty);

        RecyclerView recyclerView = findView(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new HorizontalItemDecoration(this));

        mPayYanglaoInfoAdapter = new PayYanglaoInfoAdapter();
        recyclerView.setAdapter(mPayYanglaoInfoAdapter);

        DisplayMetrics metrics = ViewUtils.getScreenInfo(this);
        mItem1Width = (int) (metrics.widthPixels * 0.25);
        mItem2Width = (int) (metrics.widthPixels * 0.25);
        mItem3Width = (int) (metrics.widthPixels * 0.25);
        mItem4Width = (int) (metrics.widthPixels * 0.25);
        mPayYanglaoInfoAdapter.setItemWidth(mItem1Width, mItem2Width, mItem3Width, mItem4Width);

        LinearLayout layout1 = findView(R.id.layout1);
        LinearLayout layout2 = findView(R.id.layout2);
        LinearLayout layout3 = findView(R.id.layout3);
        LinearLayout layout4 = findView(R.id.layout4);

        layout1.getLayoutParams().width = mItem1Width;
        layout2.getLayoutParams().width = mItem2Width;
        layout3.getLayoutParams().width = mItem3Width;
        layout4.getLayoutParams().width = mItem4Width;

        mLayoutBzd = findView(R.id.layout_bzd);
        mTvBzdPayMonth = findView(R.id.tv_bzd_pay_month);
        mTvBzdPayMoney = findView(R.id.tv_bzd_pay_money);
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

    private void loadData() {
        mProgressHUD.show(getString(R.string.loading), true, true, this);
        mLayoutContent.setVisibility(View.GONE);
        mLayoutEmpty.setVisibility(View.GONE);

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("aac001", mUser.social_security_card);
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_GET_PERSONAL_PAY_YANGLAO);
        paramMap.put("token", token);

        RequestClient<PersonalYanglaoPayResponse> requestClient = new RequestClient<PersonalYanglaoPayResponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<PersonalYanglaoPayResponse>() {
            @Override
            public void onLoadComplete(PersonalYanglaoPayResponse response) {
                if (response.code == 0) {
                    if (response.body != null && response.body.size() > 0) {
                        Collections.sort(response.body, new Comparator<PersonalYanglaoPayInfo>() {
                            @Override
                            public int compare(PersonalYanglaoPayInfo lhs, PersonalYanglaoPayInfo rhs) {
                                if (TextUtils.isEmpty(lhs.aae034) || TextUtils.isEmpty(rhs.aae034)) return 0;
                                return rhs.aae034.compareTo(lhs.aae034);
                            }
                        });
                        mLayoutContent.setVisibility(View.VISIBLE);
                        mLayoutEmpty.setVisibility(View.GONE);
                        mPayYanglaoInfoAdapter.setData(response.body);
                        mPayYanglaoInfoAdapter.notifyDataSetChanged();
                    } else {
                        mLayoutContent.setVisibility(View.GONE);
                        mLayoutEmpty.setVisibility(View.VISIBLE);
                    }
                    loadDisposableData();
                } else {
                    if (!TextUtils.isEmpty(response.msg)) {
                        ViewUtils.showToast(PersonalPayInfoYanglaoActivity.this, response.msg);
                    } else {
                        ViewUtils.showToast(PersonalPayInfoYanglaoActivity.this, R.string.failed_load_pay_info);
                    }
                    finish();
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                ViewUtils.showToast(PersonalPayInfoYanglaoActivity.this, R.string.network_error);
                finish();
            }
        });
        requestClient.executePost(this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_GET_PERSONAL_PAY_YANGLAO,
                PersonalYanglaoPayResponse.class,
                paramMap);
    }

    private void loadDisposableData() {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("aac001", String.valueOf(mUser.social_security_card));
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_PAY_DISPOSABLE);
        paramMap.put("token", token);

        RequestClient<PayDisposableResponse> requestClient = new RequestClient<PayDisposableResponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<PayDisposableResponse>() {
            @Override
            public void onLoadComplete(PayDisposableResponse response) {
                if (response.code == 0) {
                    if (response.body != null && response.body.size() > 0) {
                        PayDisposable payDisposable = response.body.get(0);
                        if (!TextUtils.isEmpty(payDisposable.zdys)) {
                            mLayoutBzd.setVisibility(View.VISIBLE);
                            mTvBzdPayMonth.setText(getString(R.string.bzd_pay_month, payDisposable.zdys));
                            mTvBzdPayMoney.setText(getString(R.string.order_pay_money, payDisposable.zdje));
                        } else {
                            mLayoutBzd.setVisibility(View.GONE);
                        }
                    }
                    mProgressHUD.dismiss();
                } else {
                    ViewUtils.showToast(PersonalPayInfoYanglaoActivity.this, response.msg);
                    mProgressHUD.dismiss();
                    finish();
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                ViewUtils.showToast(PersonalPayInfoYanglaoActivity.this, R.string.network_error);
                mProgressHUD.dismiss();
                finish();
            }
        });
        requestClient.executePost(this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_PAY_DISPOSABLE,
                PayDisposableResponse.class,
                paramMap);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        finish();
    }
}
