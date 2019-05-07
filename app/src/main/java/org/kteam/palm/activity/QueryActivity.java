package org.kteam.palm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import org.apache.log4j.Logger;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.R;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-03-02 23:23
 */
public class QueryActivity extends BaseActivity implements View.OnClickListener {
    private final Logger mLogger = Logger.getLogger(getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        initToolBar();
        setTitleText(getString(R.string.query));

        initView();
    }

    private void initView() {
        findView(R.id.btn_user_account).setOnClickListener(this);
        findView(R.id.btn_yanglao_pay_info).setOnClickListener(this);
        findView(R.id.btn_yiliao_pay_info).setOnClickListener(this);
        findView(R.id.btn_pension_info).setOnClickListener(this);

        final ImageView ivUserAccount = findView(R.id.iv_user_account);
        final ImageView ivYanglaoPayInfo = findView(R.id.iv_yanglao_pay_info);
        final ImageView ivYiliaoPayInfo = findView(R.id.iv_yiliao_pay_info);
        final ImageView ivPensionInfo = findView(R.id.iv_pension_info);
        ivPensionInfo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = ivPensionInfo.getWidth();
                int height = (int) (width * 0.33333);
                ivPensionInfo.getLayoutParams().height = height;
                ivUserAccount.getLayoutParams().height = height;
                ivYanglaoPayInfo.getLayoutParams().height = height;
                ivYiliaoPayInfo.getLayoutParams().height = height;
                ivPensionInfo.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!checkUserLogin()) return;
        switch (v.getId()) {
            case R.id.btn_user_account:
                startActivity(new Intent(this, YanglaoAccountActivity.class));
                break;
            case R.id.btn_yanglao_pay_info:
                startActivity(new Intent(this, PersonalPayInfoYanglaoActivity.class));
                break;
            case R.id.btn_yiliao_pay_info:
                startActivity(new Intent(this, PersonalPayInfoYiliaoActivity.class));
                break;
            case R.id.btn_pension_info:
                startActivity(new Intent(this, PensionInfoActivity.class));
                break;
            default:
                break;
        }
    }
}
