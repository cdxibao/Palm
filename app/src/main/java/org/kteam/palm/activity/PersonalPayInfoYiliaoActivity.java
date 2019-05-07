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

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.common.view.ProgressHUD;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.MainActivity;
import org.kteam.palm.R;
import org.kteam.palm.adapter.PayYanglaoInfoAdapter;
import org.kteam.palm.adapter.PayYiliaoInfoAdapter;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.view.HorizontalItemDecoration;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.PersonalYanglaoPayInfo;
import org.kteam.palm.network.response.PersonalYanglaoPayResponse;
import org.kteam.palm.network.response.PersonalYiliaoPayInfo;
import org.kteam.palm.network.response.PersonalYiliaoPayResponse;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-12 18:13
 */
public class PersonalPayInfoYiliaoActivity extends BaseActivity implements DialogInterface.OnCancelListener {
    private final Logger mLogger = Logger.getLogger(getClass());

    private View mLayoutContent;
    private View mLayoutEmpty;

    private PayYiliaoInfoAdapter mPayYiliaoInfoAdapter;

    private int mItem1Width;
    private int mItem2Width;
    private int mItem3Width;
    private int mItem4Width;

    private ProgressHUD mProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_pay_info_yiliao);
        initToolBar();
        if (mUser == null) {
            finish();
            return;
        }
        setTitleText(getString(R.string.personal_pay_yiliao_info_title, mUser.name));
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

        mPayYiliaoInfoAdapter = new PayYiliaoInfoAdapter();
        recyclerView.setAdapter(mPayYiliaoInfoAdapter);

        DisplayMetrics metrics = ViewUtils.getScreenInfo(this);
        mItem1Width = (int) (metrics.widthPixels * 0.25);
        mItem2Width = (int) (metrics.widthPixels * 0.25);
        mItem3Width = (int) (metrics.widthPixels * 0.25);
        mItem4Width = (int) (metrics.widthPixels * 0.25);
        mPayYiliaoInfoAdapter.setItemWidth(mItem1Width, mItem2Width, mItem3Width, mItem4Width);

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
        mLayoutContent.setVisibility(View.GONE);
        mLayoutEmpty.setVisibility(View.GONE);

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("aac001", mUser.social_security_card);
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_GET_PERSONAL_PAY_YILIAO);
        paramMap.put("token", token);

        RequestClient<PersonalYiliaoPayResponse> requestClient = new RequestClient<PersonalYiliaoPayResponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<PersonalYiliaoPayResponse>() {
            @Override
            public void onLoadComplete(PersonalYiliaoPayResponse response) {
                if (response.code == 0) {
                    if (response.body != null && response.body.size() > 0) {
                        Collections.sort(response.body, new Comparator<PersonalYiliaoPayInfo>() {
                            @Override
                            public int compare(PersonalYiliaoPayInfo lhs, PersonalYiliaoPayInfo rhs) {
                                if (TextUtils.isEmpty(lhs.dzrq) || TextUtils.isEmpty(rhs.dzrq)) return 0;
                                return rhs.dzrq.compareTo(lhs.dzrq);
                            }
                        });
                        mLayoutContent.setVisibility(View.VISIBLE);
                        mLayoutEmpty.setVisibility(View.GONE);
                        mPayYiliaoInfoAdapter.setData(response.body);
                        mPayYiliaoInfoAdapter.notifyDataSetChanged();
                    } else {
                        mLayoutContent.setVisibility(View.GONE);
                        mLayoutEmpty.setVisibility(View.VISIBLE);
                    }
                    mProgressHUD.dismiss();
                } else {
                    if (!TextUtils.isEmpty(response.msg)) {
                        ViewUtils.showToast(PersonalPayInfoYiliaoActivity.this, response.msg);
                    } else {
                        ViewUtils.showToast(PersonalPayInfoYiliaoActivity.this, R.string.failed_load_pay_info);
                    }
                    finish();
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                ViewUtils.showToast(PersonalPayInfoYiliaoActivity.this, R.string.network_error);
                finish();
            }
        });
        requestClient.executePost(this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_GET_PERSONAL_PAY_YILIAO,
                PersonalYiliaoPayResponse.class,
                paramMap);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        finish();
    }
}
