package org.kteam.palm.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.BaseApplication;
import org.kteam.palm.R;
import org.kteam.palm.adapter.AccountYearInfoAdapter;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.view.ArticleDividerItemDecoration;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.AccountYearResponse;

import java.util.HashMap;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-05 20:41
 */
public class AccountYearActivity extends BaseActivity {
    private final Logger mLogger = Logger.getLogger(getClass());

    private AccountYearInfoAdapter mAccountYearInfoAdapter;

    private int mItem1Width;
    private int mItem2Width;
    private int mItem3Width;
    private int mItem4Width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_year_info);

        initToolBar();

        if (mUser == null) {
            finish();
            return;
        }
        String year = getIntent().getStringExtra("year");
        setTitleText(getString(R.string.pension_year_info, mUser.name, year));

        initView();

        loadData(year);
    }

    private void initView() {
        RecyclerView recyclerView = findView(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new ArticleDividerItemDecoration(this));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        mAccountYearInfoAdapter = new AccountYearInfoAdapter();
        recyclerView.setAdapter(mAccountYearInfoAdapter);

        DisplayMetrics metrics = ViewUtils.getScreenInfo(this);
        mItem1Width = (int) (metrics.widthPixels * 0.25);
        mItem2Width = (int) (metrics.widthPixels * 0.25);
        mItem3Width = (int) (metrics.widthPixels * 0.25);
        mItem4Width = (int) (metrics.widthPixels * 0.25);
        mAccountYearInfoAdapter.setItemWidth(mItem1Width, mItem2Width, mItem3Width, mItem4Width);

        LinearLayout layout1 = findView(R.id.layout1);
        LinearLayout layout2 = findView(R.id.layout2);
        LinearLayout layout3 = findView(R.id.layout3);
        LinearLayout layout4 = findView(R.id.layout4);

        layout1.getLayoutParams().width = mItem1Width;
        layout2.getLayoutParams().width = mItem2Width;
        layout3.getLayoutParams().width = mItem3Width;
        layout4.getLayoutParams().width = mItem4Width;
    }

    private void loadData(String year) {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("aac001", mUser.social_security_card);
        paramMap.put("year", year);
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_ACCOUNT_YEAR_INFO);
        paramMap.put("token", token);

        RequestClient<AccountYearResponse> requestClient = new RequestClient<AccountYearResponse>();
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<AccountYearResponse>() {
            @Override
            public void onLoadComplete(AccountYearResponse response) {
                if (response.code == 0) {
                    mAccountYearInfoAdapter.setData(response.body);
                    mAccountYearInfoAdapter.notifyDataSetChanged();
                } else {
                    ViewUtils.showToast(BaseApplication.getContext(), response.msg);
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
            }
        });
        requestClient.executePost(this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_ACCOUNT_YEAR_INFO,
                AccountYearResponse.class,
                paramMap);
    }
}
