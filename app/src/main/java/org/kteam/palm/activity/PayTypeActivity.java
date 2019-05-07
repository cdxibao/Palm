package org.kteam.palm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.BaseApplication;
import org.kteam.palm.MainActivity;
import org.kteam.palm.R;
import org.kteam.palm.adapter.BaseListAdapter;
import org.kteam.palm.adapter.PayBankAdapter;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.view.ArticleDividerItemDecoration;
import org.kteam.palm.common.view.OnRVItemClickListener;
import org.kteam.palm.domain.manager.BaseDataManager;
import org.kteam.palm.model.BaseData;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.GetPayUrlResponse;
import org.kteam.palm.network.response.PayCalculateYanglaoResult;
import org.kteam.palm.network.response.PayCalculateYiliaoResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-03-05 18:23
 */
public class PayTypeActivity extends BaseActivity implements View.OnClickListener,OnRVItemClickListener {

    private final Logger mLogger = Logger.getLogger(getClass());


    private PayCalculateYanglaoResult mPayCalculateYanglaoResult;
    private PayCalculateYiliaoResult mPayCalculateYiliaoResult;
    private PayBankAdapter mPayBankAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_type);
        initToolBar();
        setTitleText(getString(R.string.pay_type));

        mPayCalculateYanglaoResult = (PayCalculateYanglaoResult) getIntent().getSerializableExtra("payCalculateYanglaoResult");
        mPayCalculateYiliaoResult = (PayCalculateYiliaoResult) getIntent().getSerializableExtra("payCalculateYiliaoResult");
        if (mPayCalculateYanglaoResult == null && mPayCalculateYiliaoResult == null) {
            finish();
            return;
        }

        initView();
        initData();
    }

    private void initView() {
        TextView tvDesc = findView(R.id.tv_desc);
        tvDesc.setText(getString(R.string.order_desc, mPayCalculateYanglaoResult == null ? mPayCalculateYiliaoResult.yad001 : mPayCalculateYanglaoResult.aab180));
        findViewById(R.id.btn_ok).setOnClickListener(this);

        RecyclerView recyclerView = findView(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new ArticleDividerItemDecoration(this));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        mPayBankAdapter = new PayBankAdapter();
        recyclerView.setAdapter(mPayBankAdapter);
        mPayBankAdapter.setOnItemClickListener(this);
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                final List<BaseData> payBankList = new BaseDataManager().getPayBankList();
                if (payBankList != null && payBankList.size() > 0) {
                    final List<BankData> mPayBankList = new ArrayList<BankData>();
                    for (BaseData baseData : payBankList) {
                        if ("2".equals(baseData.value)) {
                            continue;
                        }
                        BankData bankData = new BankData();
                        bankData.id = baseData.id;
                        bankData.label = baseData.label;
                        bankData.value = baseData.value;
                        if (baseData.description != null) {
                            String[] arr = baseData.description.split(",");
                            if (arr.length >= 2) {
                                bankData.bankId = arr[0];
                                bankData.iconName = arr[1];
                            }
                        }
                        mPayBankList.add(bankData);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPayBankAdapter.setData(mPayBankList);
                            mPayBankAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }.start();

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
    public void onItemClick(BaseListAdapter adapter, int position) {
        BankData bankData = mPayBankAdapter.getItem(position);
        mPayBankAdapter.setCheckedBankData(bankData);
        mPayBankAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (ViewUtils.isFastClick()) return;
        switch (v.getId()) {
            case R.id.btn_ok:
                submit();
                break;
            default:
                break;
        }
    }

    private void submit() {
        if (mPayBankAdapter.getCheckedBankData() == null) {
            ViewUtils.showToast(this, R.string.pls_pay_type);
            return;
        }
        HashMap<String, String> paramMap = new HashMap<String, String>();
        String url = "";
        if (mPayCalculateYiliaoResult != null) {
            paramMap.put("aadjno", mPayCalculateYiliaoResult.aae076);
            paramMap.put("yad001", mPayCalculateYiliaoResult.yad001);
            url = Constants.URL_GET_PAY_URL_YILIAO;
        } else {
            paramMap.put("aadjno", mPayCalculateYanglaoResult.aadjno);
            paramMap.put("aab180", mPayCalculateYanglaoResult.aab180);
            url = Constants.URL_GET_PAY_URL_YANGLAO;
        }

        paramMap.put("jfqd", mPayBankAdapter.getCheckedBankData().bankId);
        String token = NetworkUtils.getToken(this, paramMap, url);
        paramMap.put("token", token);

        RequestClient<GetPayUrlResponse> requestClient = new RequestClient<GetPayUrlResponse>();
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<GetPayUrlResponse>() {
            @Override
            public void onLoadComplete(GetPayUrlResponse response) {
                if (response.code == 0) {
                    if ("5".equals(mPayBankAdapter.getCheckedBankData().bankId)) { //中国工商银行
                        Intent intent = new Intent(PayTypeActivity.this, ICBCPayActivity.class);
                        HashMap<String, String> jsonMap = new GsonBuilder().create().fromJson(response.body.payUrl, new TypeToken<HashMap<String, String>>() {}.getType());
                        intent.putExtra("startB2CParams", jsonMap);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(PayTypeActivity.this, PayActivity.class);
                        intent.putExtra("payUrl", response.body.payUrl);
                        startActivity(intent);
                    }
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
                getString(R.string.submiting),
                Constants.BASE_URL + url,
                GetPayUrlResponse.class,
                paramMap);
    }

    public static class BankData extends BaseData {
        public String bankId;
        public String iconName;
    }
}
