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
import org.kteam.palm.adapter.YiliaoPayHistoryAdapter;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.view.ArticleDividerItemDecoration;
import org.kteam.palm.common.view.OnRVItemClickListener;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.AccountInfoResponse;
import org.kteam.palm.network.response.PayDisposable;
import org.kteam.palm.network.response.PayDisposableResponse;
import org.kteam.palm.network.response.YiliaoPayHistory;
import org.kteam.palm.network.response.YiliaoPayHistoryContent;
import org.kteam.palm.network.response.YiliaoPayHistoryResponse;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description 职工医疗保险历年缴费情况
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-12 18:13
 */
public class YiliaoPayHistoryActivity extends BaseActivity implements OnRVItemClickListener, DialogInterface.OnCancelListener{
    private final Logger mLogger = Logger.getLogger(getClass());

    private TextView mTvUserName;
    private TextView mTvIdCard;
    private TextView mTvAccountBalance;
    private TextView mTvPayYears;

    private View mLayoutUserInfo;
    private ProgressHUD mProgressHUD;

    private YiliaoPayHistoryAdapter mYiliaoPayHistoryAdapter;

    private int mItem1Width;
    private int mItem2Width;
    private int mItem3Width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yiliao_pay_history);
        initToolBar();
        initView();
        if (mUser == null) {
            finish();
            return;
        }
        setTitleText(getString(R.string.zgylbxlnjfqk));
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

        mTvUserName = findView(R.id.tv_user_name);
        mTvUserName.setText(getString(R.string.order_username, mUser.name));
        mTvIdCard = findView(R.id.tv_id_card);
        mTvIdCard.setText(getString(R.string.order_idcard, mUser.idcard));
        mTvAccountBalance = findView(R.id.tv_account_balance);
        mTvPayYears = findView(R.id.tv_pay_years);

        RecyclerView recyclerView = findView(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new ArticleDividerItemDecoration(this));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        mYiliaoPayHistoryAdapter = new YiliaoPayHistoryAdapter();
        mYiliaoPayHistoryAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mYiliaoPayHistoryAdapter);

        DisplayMetrics metrics = ViewUtils.getScreenInfo(this);
        mItem1Width = (int) (metrics.widthPixels * 0.33);
        mItem2Width = (int) (metrics.widthPixels * 0.33);
        mItem3Width = (int) (metrics.widthPixels * 0.34);
        mYiliaoPayHistoryAdapter.setItemWidth(mItem1Width, mItem2Width, mItem3Width);

        LinearLayout layout1 = findView(R.id.layout1);
        LinearLayout layout2 = findView(R.id.layout2);
        LinearLayout layout3 = findView(R.id.layout3);

        layout1.getLayoutParams().width = mItem1Width;
        layout2.getLayoutParams().width = mItem2Width;
        layout3.getLayoutParams().width = mItem3Width;
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
        paramMap.put("aac002", String.valueOf(mUser.idcard));
        paramMap.put("aac003", String.valueOf(mUser.name));
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_YL_PAY_HISTORY);
        paramMap.put("token", token);

        RequestClient<YiliaoPayHistoryResponse> requestClient = new RequestClient<YiliaoPayHistoryResponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<YiliaoPayHistoryResponse>() {
            @Override
            public void onLoadComplete(YiliaoPayHistoryResponse response) {
                mProgressHUD.dismiss();
                if (response.code == 0) {
                    if (response.body != null) {
                        YiliaoPayHistory payHistory = response.body;
                        if (payHistory.ylbxlnjfqk != null) {
                            mLayoutUserInfo.setVisibility(View.VISIBLE);
                            mTvAccountBalance.setText(getString(R.string.grzhye, payHistory.ylbxlnjfqk.ye));
                            mTvPayYears.setText(getString(R.string.jfnx, payHistory.ylbxlnjfqk.sumMonth));
                            if (!TextUtils.isEmpty(payHistory.ylbxlnjfqk.sumMonth)) {
                                try {
                                    int months = Integer.parseInt(payHistory.ylbxlnjfqk.sumMonth);
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
                        }
                        if (payHistory.ylbxlnjfqkList != null && payHistory.ylbxlnjfqkList.size() > 0) {
                            Collections.sort(payHistory.ylbxlnjfqkList, new Comparator<YiliaoPayHistoryContent>() {
                                @Override
                                public int compare(YiliaoPayHistoryContent lhs, YiliaoPayHistoryContent rhs) {
                                    return rhs.aae001.compareTo(lhs.aae001);
                                }
                            });

                            mYiliaoPayHistoryAdapter.appendDataList(payHistory.ylbxlnjfqkList);
                            mYiliaoPayHistoryAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    ViewUtils.showToast(YiliaoPayHistoryActivity.this, response.msg);
                    finish();
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                ViewUtils.showToast(YiliaoPayHistoryActivity.this, R.string.network_error);
                mProgressHUD.dismiss();
                finish();
            }
        });
        requestClient.executePost(this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_YL_PAY_HISTORY,
                YiliaoPayHistoryResponse.class,
                paramMap);
    }


    @Override
    public void onItemClick(BaseListAdapter adapter, int position) {
        if (ViewUtils.isFastClick()) return;
        YiliaoPayHistoryAdapter historyAdapter = (YiliaoPayHistoryAdapter) adapter;
        Intent intent = new Intent(this, YiliaoPayHistoryInfoActivity.class);
        intent.putExtra("year", historyAdapter.getItem(position).aae001);
        startActivity(intent);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        finish();
    }
}
