package org.kteam.palm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.common.view.ProgressHUD;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.model.UserInfo;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.UserInfoResponse;

import java.util.HashMap;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-03-02 23:23
 */
public class QueryYiliaoActivity extends BaseActivity implements View.OnClickListener {
    private final Logger mLogger = Logger.getLogger(getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_yiliao);
        initToolBar();
        setTitleText(getString(R.string.yl_query));

        initView();
    }

    private void initView() {
        findView(R.id.rl_query_yl_gtjfjl).setOnClickListener(this);
        findView(R.id.rl_query_yl_lnjfjl).setOnClickListener(this);
        findView(R.id.rl_query_yl_zhszmx).setOnClickListener(this);
        final ImageView ivYlGtjfjl = findView(R.id.iv_query_yl_gtjfjl);
        final ImageView ivYlLnJfjl = findView(R.id.iv_query_yl_lnjfjl);
        final ImageView ivYlZhszmx = findView(R.id.iv_query_yl_zhszmx);
        ivYlGtjfjl.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = ivYlGtjfjl.getWidth();
                int height = (int) (width * 0.33333);
                ivYlGtjfjl.getLayoutParams().height = height;
                ivYlLnJfjl.getLayoutParams().height = height;
                ivYlZhszmx.getLayoutParams().height = height;
                ivYlGtjfjl.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!checkUserLogin()) return;
        switch (v.getId()) {
            case R.id.rl_query_yl_gtjfjl:
                startActivity(new Intent(QueryYiliaoActivity.this, PersonalPayInfoYiliaoActivity.class));
                break;
            case R.id.rl_query_yl_lnjfjl:
                startActivity(new Intent(QueryYiliaoActivity.this, YiliaoPayHistoryActivity.class));
                break;
            case R.id.rl_query_yl_zhszmx:
                startActivity(new Intent(QueryYiliaoActivity.this, YiliaoPayHistoryInfoActivity.class));
            default:
                break;
        }
    }

}
