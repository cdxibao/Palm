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
import org.kteam.palm.network.response.PayCalculateYiliaoResult;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description 医疗缴费计算结果信息
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-12 16:31
 */
public class PayYiliaoCalResultActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvPayBase;
    private TextView mTvPayMonths;
    private TextView mTvPaySum;
    private TextView mTvPayYiliaoJb;
    private TextView mTvPayYiliaoBc;
    private TextView mTvPersonalPayInterest;
    private TextView mTvPersonalPayOverdue;
    private TextView mTvPersonalPayListNo;

    private PayCalculateYiliaoResult mPayCalculateResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_yiliao_cal_result);
        initToolBar();
        setTitleText(getString(R.string.pay));

        mPayCalculateResult = (PayCalculateYiliaoResult) getIntent().getSerializableExtra("payCalculateResult");
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
        mTvPersonalPayInterest = findView(R.id.tv_personal_pay_interest);
        mTvPersonalPayOverdue = findView(R.id.tv_pay_overdue);
        mTvPayYiliaoJb = findView(R.id.tv_pay_yiliao_jb);
        mTvPayYiliaoBc = findView(R.id.tv_pay_yiliao_bc);
        mTvPersonalPayListNo = findView(R.id.tv_personal_pay_list_no);
        findViewById(R.id.btn_ok).setOnClickListener(this);
    }

    private void loadData() {
        if (!TextUtils.isEmpty(mPayCalculateResult.aae180)) {
            mTvPayBase.setText(getString(R.string.yuan_per_month, mPayCalculateResult.aae180));
        }
        if (!TextUtils.isEmpty(mPayCalculateResult.aae201)) {
            mTvPayMonths.setText(mPayCalculateResult.aae201);
        }
        if (!TextUtils.isEmpty(mPayCalculateResult.yad001)) {
            mTvPaySum.setText(getString(R.string.yuan, mPayCalculateResult.yad001));
        } else {
            mTvPaySum.setText(getString(R.string.yuan, 0));
        }
        if (!TextUtils.isEmpty(mPayCalculateResult.lx)) {
            mTvPersonalPayInterest.setText(getString(R.string.yuan, mPayCalculateResult.lx));
        } else {
            mTvPersonalPayInterest.setText(getString(R.string.yuan, 0));
        }
        if (!TextUtils.isEmpty(mPayCalculateResult.yad001_jb)) {
            mTvPayYiliaoJb.setText(getString(R.string.yuan, mPayCalculateResult.yad001_jb));
        } else {
            mTvPayYiliaoJb.setText(getString(R.string.yuan, 0));
        }
        if (!TextUtils.isEmpty(mPayCalculateResult.yad001_bc)) {
            mTvPayYiliaoBc.setText(getString(R.string.yuan, mPayCalculateResult.yad001_bc));
        } else {
            mTvPayYiliaoBc.setText(getString(R.string.yuan, 0));
        }
        if (!TextUtils.isEmpty(mPayCalculateResult.znj)) {
            mTvPersonalPayOverdue.setText(getString(R.string.yuan, mPayCalculateResult.znj));
        } else {
            mTvPersonalPayOverdue.setText(getString(R.string.yuan, 0));
        }
        if (!TextUtils.isEmpty(mPayCalculateResult.aae076)) {
            mTvPersonalPayListNo.setText(mPayCalculateResult.aae076);
        }
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
        intent.putExtra("payCalculateYiliaoResult", mPayCalculateResult);
        startActivity(intent);
    }
}