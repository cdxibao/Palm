package org.kteam.palm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.kteam.palm.activity.LoginActivity;
import org.kteam.palm.common.utils.UserStateUtils;
import org.kteam.palm.model.User;

/**
 * @PACKAGE_NAME org.kteam.palm.common.utils
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2015-11-22 15:56
 * @Version 1.0.0
 */
public class BaseActivity extends AppCompatActivity {
    public static final String ACTION_EXIT_APP = "org.kteam.palm.ExitApp";

    protected boolean mShowBackButton = true;
    protected Toolbar mToolBar;
    protected TextView mTvTitle;
    protected User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = UserStateUtils.getInstance().getUser();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_EXIT_APP);
        registerReceiver(mExitReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUser = UserStateUtils.getInstance().getUser();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mExitReceiver != null) {
            unregisterReceiver(mExitReceiver);
        }
        super.onDestroy();
    }

    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }

    protected void initToolBar() {
        setTitle("");
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        mTvTitle = (TextView) findViewById(R.id.tv_title);
        setToolBar();
        if (mShowBackButton) {
            mToolBar.setNavigationIcon(R.drawable.back_selector);
            mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    protected boolean checkUserLogin() {
        mUser = UserStateUtils.getInstance().getUser();
        if (mUser == null) {
//            ViewUtils.showToast(this, R.string.load_user_failed);
            startActivity(new Intent(this, LoginActivity.class));
        }
        return mUser != null;
    }

    protected void setNavigationIcon(int iconResId, View.OnClickListener onClickListener) {
        mToolBar.setNavigationIcon(iconResId);
        mToolBar.setNavigationOnClickListener(onClickListener);
    }

    protected void setNavigationGone() {
        mToolBar.setNavigationIcon(null);
        mToolBar.setNavigationOnClickListener(null);
    }

    protected void initToolBar(boolean showBackButton) {
        mShowBackButton = showBackButton;
        initToolBar();
    }

    private void setToolBar() {
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    protected void setTitleText(String title) {
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(title);
    }
    protected void setTitleRes(int resId) {
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(resId);
    }

    protected void hideTitle() {
        mTvTitle.setVisibility(View.GONE);
    }

    private BroadcastReceiver mExitReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_EXIT_APP.equals(intent.getAction())) {
                finish();
            }
        }
    };

}
