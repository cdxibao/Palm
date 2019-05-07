package org.kteam.palm.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.apache.log4j.Logger;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.DateUtils;
import org.kteam.palm.common.view.NoScrollViewPager;
import org.kteam.palm.domain.manager.BaseDataManager;
import org.kteam.palm.fragment.OtherPayOrderFragment;
import org.kteam.palm.fragment.OwnPayOrderFragment;
import org.kteam.palm.model.BaseData;
import org.kteam.palm.model.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-03-27 16:10
 */
public class OrderInfoActivity extends BaseActivity {
    private final Logger mLogger = Logger.getLogger(getClass());
    private Order mOrder = null;
    private boolean mPayedOther = false;
    private List<BaseData> mPayedStatusList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);
        if (mUser == null) {
            finish();
            return;
        }
        initToolBar();
        setTitleText(getString(R.string.order_info));

        mOrder = (Order) getIntent().getSerializableExtra("order");
        if (mOrder == null) {
            finish();
            return;
        }
        mPayedOther = getIntent().getBooleanExtra("payedOther", false);

        mPayedStatusList = new BaseDataManager().getPayStatusList();

        initView();
    }

    private void initView() {
        TextView tvOrderNo = findView(R.id.tv_order_no);
        TextView tvUserName = findView(R.id.tv_username);
        TextView tvIdCard = findView(R.id.tv_idcard);
        TextView tvPhone = findView(R.id.tv_phone);

        View lineUserName = findView(R.id.line_username);
        View lineIdCard = findView(R.id.line_idcard);
        View linePhone = findView(R.id.line_phone);
        View linePayWay = findView(R.id.line_pay_way);
        View lineBcyl = findView(R.id.line_bcyl);

        int visibility = mPayedOther ? View.VISIBLE : View.GONE;
        tvUserName.setVisibility(visibility);
        tvIdCard.setVisibility(visibility);
        tvPhone.setVisibility(visibility);
        lineUserName.setVisibility(visibility);
        lineIdCard.setVisibility(visibility);
        linePhone.setVisibility(visibility);

        TextView tvStartMonth = findView(R.id.tv_start_month);
        TextView tvEndMonth = findView(R.id.tv_end_month);
        TextView tvPayBase = findView(R.id.tv_pay_base);
//        TextView tvPayMoney = findView(R.id.tv_pay_money);
        TextView tvPayPrincipay = findView(R.id.tv_pay_principal);
        TextView tvPayBcyl = findView(R.id.tv_bcylbx);
        TextView tvInterest = findView(R.id.tv_pay_interest);
        TextView tvPayOverdue = findView(R.id.tv_pay_overdue);
        TextView tvPayWay = findView(R.id.tv_pay_way);
        TextView tvPayTotalMoney = findView(R.id.tv_pay_total_money);
        TextView tvPayStatus = findView(R.id.tv_pay_status);
        TextView tvPayTime = findView(R.id.tv_pay_time);
        View linePayTime = findView(R.id.line_pay_time);

        tvOrderNo.setText(getString(R.string.order_no, mOrder.aadjno));
        tvUserName.setText(getString(R.string.order_username, mOrder.aac003));
        tvIdCard.setText(getString(R.string.order_idcard, mOrder.aac002));
        tvPhone.setText(getString(R.string.order_phone, mOrder.phone));
        tvStartMonth.setText(getString(R.string.pay_begin_year_month2, mOrder.zac001, mOrder.zac002));
        tvEndMonth.setText(getString(R.string.pay_end_year_month2, mOrder.end_year, mOrder.zac003));
        tvPayBase.setText(getString(R.string.order_pay_base, mOrder.aac040));
//        tvPayMoney.setText(getString(R.string.order_payed_money, mOrder.aab180));

        tvInterest.setText(getString(R.string.order_pay_interest, mOrder.aab182));
        tvPayOverdue.setText(getString(R.string.order_pay_overdue, mOrder.aab183));

        if ("1".equals(mOrder.category)) {
            tvPayPrincipay.setText(getString(R.string.order_pay_principal, mOrder.aab181));
            tvPayBcyl.setVisibility(View.GONE);
            lineBcyl.setVisibility(View.GONE);
        } else {
            tvPayBcyl.setVisibility(View.VISIBLE);
            lineBcyl.setVisibility(View.VISIBLE);
            tvPayPrincipay.setText(getString(R.string.pay_yiliao_jb1, mOrder.aab181));
            tvPayBcyl.setText(getString(R.string.pay_yiliao_bc1, mOrder.yad001_bc));
        }

        if (!TextUtils.isEmpty(mOrder.pay_way)) {
            String payType = "";
            switch (Integer.parseInt(mOrder.pay_way)) {
                case 1:
                    payType = getString(R.string.ccbpay);
                    break;
                case 2:
                    payType = getString(R.string.unionpay);
                    break;
                case 3:
                    payType = getString(R.string.alipay);
                    break;
                case 4:
                    payType = getString(R.string.weixinpay);
                    break;
                default:
                    break;
            }
            if (!TextUtils.isEmpty(payType)) {
                tvPayWay.setText(getString(R.string.order_pay_way, payType));
                tvPayWay.setVisibility(View.VISIBLE);
                linePayWay.setVisibility(View.VISIBLE);
            } else {
                tvPayWay.setVisibility(View.GONE);
                linePayWay.setVisibility(View.GONE);
            }
        } else {
            tvPayWay.setVisibility(View.GONE);
            linePayWay.setVisibility(View.GONE);
        }
        tvPayTotalMoney.setText(getString(R.string.order_payed_money, mOrder.total_fee));
        tvInterest.setText(getString(R.string.order_pay_interest, mOrder.aab182));
        tvPayStatus.setText(getString(R.string.order_pay_status, getPayStatus(mOrder.pay_status)));
        try {
            if (!TextUtils.isEmpty(mOrder.pay_date)) {
                tvPayTime.setText(getString(R.string.order_payed_time, DateUtils.getOrderDateStr(Long.parseLong(mOrder.pay_date))));
                tvPayTime.setVisibility(View.VISIBLE);
                linePayTime.setVisibility(View.VISIBLE);
            } else {
                tvPayTime.setVisibility(View.GONE);
                linePayTime.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            tvPayTime.setVisibility(View.GONE);
            linePayTime.setVisibility(View.GONE);
        }
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
