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
import org.kteam.palm.adapter.AccountAdapter;
import org.kteam.palm.adapter.BaseListAdapter;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.view.ArticleDividerItemDecoration;
import org.kteam.palm.common.view.OnRVItemClickListener;
import org.kteam.palm.model.AccountInfo;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.AccountInfoResponse;
import org.kteam.palm.network.response.PayDisposable;
import org.kteam.palm.network.response.PayDisposableResponse;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description 个人账户
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-12 18:13
 */
public class YanglaoAccountActivity extends BaseActivity implements OnRVItemClickListener, DialogInterface.OnCancelListener{
    private final Logger mLogger = Logger.getLogger(getClass());

    private TextView mTvAccountTotal;
    private TextView mTvPayYears;
    private TextView mTvAccountPrincipal;
    private TextView mTvAccountBalance;
    private View mLayoutYcx;
    private View mLayoutBzd;
    private TextView mTvYcxPayMonth;
    private TextView mTvBzdPayMonth;
    private TextView mTvYcxPayMoney;
    private TextView mTvBzdPayMoney;
    private View mLayoutUserInfo;
    private ProgressHUD mProgressHUD;

    private AccountAdapter mAccountAdapter;

    private int mItem1Width;
    private int mItem2Width;
    private int mItem3Width;
    private int mItem4Width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        initToolBar();
        initView();
        if (mUser == null) {
            finish();
            return;
        }
        setTitleText(getString(R.string.personal_account_title, mUser.name));
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
        mLayoutUserInfo = findView(R.id.layout_user_info);
        mTvAccountTotal = findView(R.id.tv_account_total);
        mTvPayYears = findView(R.id.tv_pay_years);
        mTvAccountPrincipal = findView(R.id.tv_account_principal);
        mTvAccountBalance = findView(R.id.tv_account_balance);
        mLayoutYcx = findView(R.id.layout_ycx);
        mLayoutBzd = findView(R.id.layout_bzd);
        mTvYcxPayMonth = findView(R.id.tv_ycx_pay_month);
        mTvBzdPayMonth = findView(R.id.tv_bzd_pay_month);
        mTvYcxPayMoney = findView(R.id.tv_ycx_pay_money);
        mTvBzdPayMoney = findView(R.id.tv_bzd_pay_money);

        RecyclerView recyclerView = findView(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new ArticleDividerItemDecoration(this));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        mAccountAdapter = new AccountAdapter();
        mAccountAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mAccountAdapter);

        DisplayMetrics metrics = ViewUtils.getScreenInfo(this);
        mItem1Width = (int) (metrics.widthPixels * 0.25);
        mItem2Width = (int) (metrics.widthPixels * 0.25);
        mItem3Width = (int) (metrics.widthPixels * 0.25);
        mItem4Width = (int) (metrics.widthPixels * 0.25);
        mAccountAdapter.setItemWidth(mItem1Width, mItem2Width, mItem3Width, mItem4Width);

        LinearLayout layout1 = findView(R.id.layout1);
        LinearLayout layout2 = findView(R.id.layout2);
        LinearLayout layout3 = findView(R.id.layout3);
        LinearLayout layout4 = findView(R.id.layout4);

        layout1.getLayoutParams().width = mItem1Width;
        layout2.getLayoutParams().width = mItem2Width;
        layout3.getLayoutParams().width = mItem3Width;
        layout4.getLayoutParams().width = mItem4Width;
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

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("aac001", String.valueOf(mUser.social_security_card));
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_GET_ACCOUNT);
        paramMap.put("token", token);

        RequestClient<AccountInfoResponse> requestClient = new RequestClient<AccountInfoResponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<AccountInfoResponse>() {
            @Override
            public void onLoadComplete(AccountInfoResponse response) {
                if (response.code == 0) {
                    if (response.body != null && response.body.size() > 0) {
                        Collections.sort(response.body, new Comparator<AccountInfo>() {
                            @Override
                            public int compare(AccountInfo lhs, AccountInfo rhs) {
                                return rhs.aae001.compareTo(lhs.aae001);
                            }
                        });
                        mLayoutUserInfo.setVisibility(View.VISIBLE);
                        AccountInfo accountInfo = response.body.get(0);
                        if (!TextUtils.isEmpty(accountInfo.aic039)) {
                            mTvAccountTotal.setText(getString(R.string.account_total, accountInfo.aic039));
                        }
                        if (!TextUtils.isEmpty(accountInfo.aic048)) {
                            try {
                                int months = Integer.parseInt(accountInfo.aic048);
                                int year = months / 12;;
                                int month = months % 12;
                                if (year == 0) {
                                    mTvPayYears.setText(getString(R.string.account_pay_years_1, month));
                                } else {
                                    if (month == 0) {
                                        mTvPayYears.setText(getString(R.string.account_pay_years_2, year));
                                    } else {
                                        mTvPayYears.setText(getString(R.string.account_pay_years_3, year, month));
                                    }
                                }
                            } catch (Exception e) {
                                mLogger.error(e.getMessage(), e);
                            }
                        }
                        if (!TextUtils.isEmpty(accountInfo.aic090)) {
                            mTvAccountPrincipal.setText(getString(R.string.account_principal, accountInfo.aic090));
                        }
                        if (!TextUtils.isEmpty(accountInfo.aic094)) {
                            mTvAccountBalance.setText(getString(R.string.account_balance, accountInfo.aic094));
                        }
                        mAccountAdapter.appendDataList(response.body);
                        mAccountAdapter.notifyDataSetChanged();
                    }
                    loadDisposableData();
                } else {
                    ViewUtils.showToast(YanglaoAccountActivity.this, response.msg);
                    mProgressHUD.dismiss();
                    finish();
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                ViewUtils.showToast(YanglaoAccountActivity.this, R.string.network_error);
                mProgressHUD.dismiss();
                finish();
            }
        });
        requestClient.executePost(this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_GET_ACCOUNT,
                AccountInfoResponse.class,
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
                        if (!TextUtils.isEmpty(payDisposable.ycxys)) {
                            mLayoutYcx.setVisibility(View.VISIBLE);
                            mTvYcxPayMonth.setText(getString(R.string.ycx_pay_month, payDisposable.ycxys));
                            mTvYcxPayMoney.setText(getString(R.string.order_pay_money, payDisposable.ycxje));
                        } else {
                            mLayoutYcx.setVisibility(View.GONE);
                        }
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
                    ViewUtils.showToast(YanglaoAccountActivity.this, response.msg);
                    mProgressHUD.dismiss();
                    finish();
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                ViewUtils.showToast(YanglaoAccountActivity.this, R.string.network_error);
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
    public void onItemClick(BaseListAdapter adapter, int position) {
        if (ViewUtils.isFastClick()) return;
        AccountAdapter accountAdapter = (AccountAdapter) adapter;
        Intent intent = new Intent(this, AccountYearActivity.class);
        intent.putExtra("year", accountAdapter.getItem(position).aae001);
        startActivity(intent);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        finish();
    }
}
