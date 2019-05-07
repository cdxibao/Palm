package org.kteam.palm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.MainActivity;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.domain.manager.BaseDataManager;
import org.kteam.palm.model.BaseData;
import org.kteam.palm.model.Order;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.OrderResponse;

import java.util.HashMap;
import java.util.List;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-07-13 23:39
 */
public class PayResultActivity extends BaseActivity {

    private final Logger mLogger = Logger.getLogger(getClass());
    private String mAadjno;
    private boolean mPayedSuccess;

    private List<BaseData> mPayedStatusList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);
        initToolBar();
        setTitleText(getString(R.string.payed_result));
        mAadjno = getIntent().getStringExtra("aadjno");
        mPayedSuccess = getIntent().getBooleanExtra("payedSuccess", true);

        mPayedStatusList = new BaseDataManager().getPayStatusList();

        initView();
        loadData();
    }

    private void initView() {
        ImageView ivPayedState = findView(R.id.iv_payed_state);
        TextView tvPayedState = findView(R.id.tv_payed_state);
        if (mPayedSuccess) {
            ivPayedState.setImageResource(R.mipmap.success);
            tvPayedState.setText(R.string.payed_success_tip);
            tvPayedState.setTextColor(getResources().getColor(R.color.success));
        } else {
            ivPayedState.setImageResource(R.mipmap.failed);
            tvPayedState.setText(R.string.payed_failed_tip);
            tvPayedState.setTextColor(getResources().getColor(R.color.failed));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.go_home, menu);
        return true;
    }

    @Override
    public void finish() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (ViewUtils.isFastClick()) return super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == R.id.action_home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("aadjno", mAadjno);
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_PAYED_INFO);
        paramMap.put("token", token);

        RequestClient<OrderResponse> requestClient = new RequestClient<OrderResponse>();
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<OrderResponse>() {
            @Override
            public void onLoadComplete(OrderResponse response) {
                if (response.code == 0 && response.body != null) {
                    TextView tvOrderState = findView(R.id.tv_order_state);
                    TextView tvOrderNo = findView(R.id.tv_order_no);
                    TextView tvUsername = findView(R.id.tv_username);
                    TextView tvStartYearMonth = findView(R.id.tv_start_year_month);
                    TextView tvEndYearMonth = findView(R.id.tv_end_year_month);
                    TextView tvMoney = findView(R.id.tv_money);

                    Order order = response.body;
                    tvOrderNo.setText(getString(R.string.order_no, order.aadjno));
                    tvUsername.setText(getString(R.string.order_username, order.aac003));
                    tvStartYearMonth.setText(getString(R.string.pay_begin_year_month2, order.zac001, order.zac002));
                    tvEndYearMonth.setText(getString(R.string.pay_end_year_month2, order.end_year, order.zac003));
                    tvOrderState.setText(getString(R.string.order_pay_status, getPayStatus(order.pay_status)));
                    tvMoney.setText(getString(R.string.order_pay_money, order.total_fee));

                } else {
                    ViewUtils.showToast(PayResultActivity.this, response.msg);
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
            }
        });
        requestClient.executePost(PayResultActivity.this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_PAYED_INFO,
                OrderResponse.class,
                paramMap);
    }

    private String getPayStatus(int status) {
        if (mPayedStatusList != null) {
            for (BaseData baseData : mPayedStatusList) {
                if (String.valueOf(status).equals(baseData.value)) {
                    return baseData.label;
                }
            }
        }
        return "";
    }
}
