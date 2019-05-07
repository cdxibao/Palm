package org.kteam.palm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.MainActivity;
import org.kteam.palm.R;
import org.kteam.palm.network.response.PayCalculateYanglaoResult;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description 养老缴费计算结果信息
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-12 16:31
 */
public class PayYanglaoCalResultActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvPayBase;
    private TextView mTvPayMonths;
    private TextView mTvPaySum;
    private TextView mPersonalPayPrincipal;
    private TextView mPersonalPayInterest;
    private TextView mPersonalPayListNo;
    private TextView mTvPayExponent;

    private PayCalculateYanglaoResult mPayCalculateResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_yanglao_cal_result);
        initToolBar();
        setTitleText(getString(R.string.pay));

        mPayCalculateResult = (PayCalculateYanglaoResult) getIntent().getSerializableExtra("payCalculateResult");
        if (mPayCalculateResult == null) {
            finish();
            return;
        }
        initView();
        loadData();
    }

    private void initView() {
        mTvPayBase = findView(R.id.tv_pay_base);
        mTvPayMonths = findView(R.id.tv_pay_months);
        mTvPaySum = findView(R.id.tv_pay_sum);
        mPersonalPayPrincipal = findView(R.id.tv_personal_pay_principal);
        mPersonalPayInterest = findView(R.id.tv_personal_pay_interest);
        mPersonalPayListNo = findView(R.id.tv_personal_pay_list_no);
        mTvPayExponent = findView(R.id.tv_pay_exponent);
        findViewById(R.id.btn_ok).setOnClickListener(this);
    }

    private void loadData() {
        if (!TextUtils.isEmpty(mPayCalculateResult.aac040)) {
            mTvPayBase.setText(getString(R.string.yuan_per_month, mPayCalculateResult.aac040));
        }
        mTvPayMonths.setText(mPayCalculateResult.cab182);
        if (!TextUtils.isEmpty(mPayCalculateResult.aab180)) {
            mTvPaySum.setText(getString(R.string.yuan, mPayCalculateResult.aab180));
        } else {
            mTvPaySum.setText(getString(R.string.yuan, 0));
        }
        if (!TextUtils.isEmpty(mPayCalculateResult.aab181)) {
            mPersonalPayPrincipal.setText(getString(R.string.yuan, mPayCalculateResult.aab181));
        } else {
            mPersonalPayPrincipal.setText(getString(R.string.yuan, 0));
        }
        if (!TextUtils.isEmpty(mPayCalculateResult.aab182)) {
            mPersonalPayInterest.setText(getString(R.string.yuan, mPayCalculateResult.aab182));
        } else {
            mPersonalPayInterest.setText(getString(R.string.yuan, 0));
        }
        mTvPayExponent.setText(mPayCalculateResult.aic110);
        mPersonalPayListNo.setText(mPayCalculateResult.aadjno);
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
        Intent intent = new Intent(this, PayTypeActivity.class);
        intent.putExtra("payCalculateYanglaoResult", mPayCalculateResult);
        startActivity(intent);
    }
}