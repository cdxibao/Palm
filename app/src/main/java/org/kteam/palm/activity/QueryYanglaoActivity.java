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
public class QueryYanglaoActivity extends BaseActivity implements View.OnClickListener {
    private final Logger mLogger = Logger.getLogger(getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_yanglao);
        initToolBar();
        setTitleText(getString(R.string.yanglao_query));

        initView();
    }

    private void initView() {
        findView(R.id.rl_query_yl_gtjfjl).setOnClickListener(this);
        findView(R.id.rl_query_yl_grzhqk).setOnClickListener(this);
        findView(R.id.rl_query_yl_yljqk).setOnClickListener(this);
        final ImageView ivYlGtjfjl = findView(R.id.iv_query_yl_gtjfjl);
        final ImageView ivYlGrzhqk = findView(R.id.iv_query_yl_grzhqk);
        final ImageView ivYlYljqk = findView(R.id.iv_query_yl_yljqk);
        ivYlGtjfjl.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = ivYlGtjfjl.getWidth();
                int height = (int) (width * 0.33333);
                ivYlGtjfjl.getLayoutParams().height = height;
                ivYlGrzhqk.getLayoutParams().height = height;
                ivYlYljqk.getLayoutParams().height = height;
                ivYlGtjfjl.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!checkUserLogin()) return;
        switch (v.getId()) {
            case R.id.rl_query_yl_gtjfjl:
                startActivity(new Intent(QueryYanglaoActivity.this, PersonalPayInfoYanglaoActivity.class));
                break;
            case R.id.rl_query_yl_grzhqk:
                startActivity(new Intent(QueryYanglaoActivity.this, YanglaoAccountActivity.class));
                break;
            case R.id.rl_query_yl_yljqk:
                startActivity(new Intent(QueryYanglaoActivity.this, PensionInfoActivity.class));
            default:
                break;
        }
    }

}
