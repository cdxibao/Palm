package org.kteam.palm.activity;

import android.content.Intent;
import android.os.Bundle;
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

import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.MainActivity;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.IDCardUtils;


/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 *
 * @Description 选择本人缴费还是他人缴费
 *
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-01-24 22:39
 */
public class PersonalUserPayTypeActivity extends BaseActivity implements View.OnClickListener {
    private RadioGroup mRgPay;
    private CardView mCVUserInfo;
    private EditText mEtUserName;
    private EditText mEtCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_user_pay_type);
        initToolBar();
        setTitleText(getString(R.string.personal_pay));
        initView();
    }

    private void initView() {
        findViewById(R.id.layout_main).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewUtils.hideInputMethod(PersonalUserPayTypeActivity.this);
                return true;
            }
        });
        findViewById(R.id.btn_ok).setOnClickListener(this);
        mCVUserInfo = findView(R.id.cv_user_info);
        mRgPay = findView(R.id.rg_pay);
        mRgPay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int visibility = checkedId == R.id.rb_other_payed ? View.VISIBLE : View.GONE;
                mCVUserInfo.setVisibility(visibility);
            }
        });
        mEtUserName = findView(R.id.et_user_name);
        mEtCard = findView(R.id.et_card);
        mRgPay.check(R.id.rb_self_payed);
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
                Intent intent = new Intent(this, PersonalPayTypeActivity.class);
                int payType = mRgPay.getCheckedRadioButtonId() == R.id.rb_other_payed ? 1 : 0;
                intent.putExtra("payType", payType);
                if (payType == 1) {
                    String username = mEtUserName.getText().toString().trim();
                    String idcard = mEtCard.getText().toString().trim();
                    if (TextUtils.isEmpty(username)) {
                        ViewUtils.showToast(this, R.string.input_tip_user_name);
                        return;
                    }
                    if (TextUtils.isEmpty(idcard)) {
                        ViewUtils.showToast(this, R.string.input_tip_idcard);
                        return;
                    }
                    if (!IDCardUtils.isValid(idcard)) {
                        ViewUtils.showToast(this, R.string.invalid_idcard);
                        return;
                    }
                    if (idcard.equals(mUser.idcard) && username.equals(mUser.name)) {
                        ViewUtils.showToast(this, R.string.pls_select_own_pay);
                        return;
                    }
                    intent.putExtra("userName", username);
                    intent.putExtra("userCard", idcard);
                }
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
