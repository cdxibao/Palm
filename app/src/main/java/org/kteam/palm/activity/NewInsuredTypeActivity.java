package org.kteam.palm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.MainActivity;
import org.kteam.palm.R;


/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 *
 * @Description 新参保类型：养老参保、医疗参保
 *
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-01-24 22:39
 */
public class NewInsuredTypeActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_NEW_INSURED = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_insured_type);
        initToolBar();
        setTitleText(getString(R.string.personal_new_insured));
        initView();
    }

    private void initView() {
        findViewById(R.id.layout_pay_yanglao).setOnClickListener(this);
        findViewById(R.id.layout_pay_yiliao).setOnClickListener(this);
        findViewById(R.id.scrollView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewUtils.hideInputMethod(NewInsuredTypeActivity.this);
                return true;
            }
        });

        final ImageView ivPayYanglao = findView(R.id.iv_pay_yanglao);
        final ImageView ivPayYiliao = findView(R.id.iv_pay_yiliao);
        ivPayYiliao.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int width = ivPayYiliao.getWidth();
                    int height = (int) (width * 0.33333);
                    ivPayYiliao.getLayoutParams().height = height;
                    ivPayYanglao.getLayoutParams().height = height;
                    ivPayYiliao.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_NEW_INSURED) {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (ViewUtils.isFastClick()) return;
        switch (v.getId()) {
            case R.id.layout_pay_yanglao:
                Intent intent = new Intent(this, NewYanglaoInsuredActivity.class);
                startActivityForResult(intent, REQUEST_CODE_NEW_INSURED);
                break;
            case R.id.layout_pay_yiliao:
                intent = new Intent(this, NewYiliaoInsuredActivity.class);
                startActivityForResult(intent, REQUEST_CODE_NEW_INSURED);
                break;
            default:
                break;
        }
    }
}
