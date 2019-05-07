package org.kteam.palm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.kteam.common.utils.StringUtils;
import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.MainActivity;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.IDCardUtils;
import org.kteam.palm.common.utils.ShareSDK;


/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 *
 * @Description 选择医疗缴费还是养老缴费
 *
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-01-24 22:39
 */
public class PersonalPayTypeActivity extends BaseActivity implements View.OnClickListener {

    private int mPayType = 0;
    private String mUserName = "";
    private String mUserCard = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_pay_type);
        initToolBar();
        setTitleText(getString(R.string.personal_pay));
        initView();

        mPayType = getIntent().getIntExtra("payType", 0);
        mUserName = getIntent().getStringExtra("userName");
        mUserCard = getIntent().getStringExtra("userCard");
    }

    private void initView() {
        findViewById(R.id.layout_pay_yanglao).setOnClickListener(this);
        findViewById(R.id.layout_pay_yiliao).setOnClickListener(this);
        findViewById(R.id.scrollView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewUtils.hideInputMethod(PersonalPayTypeActivity.this);
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
            case R.id.layout_pay_yanglao:
                Intent intent = new Intent(this, PersonalPayYanglaoActivity.class);
                intent.putExtra("payType", mPayType);
                if (mPayType == 1) {
                    intent.putExtra("userName", mUserName);
                    intent.putExtra("userCard", mUserCard);
                }
                startActivity(intent);
                break;
            case R.id.layout_pay_yiliao:
                intent = new Intent(this, PersonalPayYiliaoActivity.class);
                intent.putExtra("payType", mPayType);
                if (mPayType == 1) {
                    intent.putExtra("userName", mUserName);
                    intent.putExtra("userCard", mUserCard);
                }
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
