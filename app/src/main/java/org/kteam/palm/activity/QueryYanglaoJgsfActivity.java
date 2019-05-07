package org.kteam.palm.activity;

import android.app.AlertDialog;
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
public class QueryYanglaoJgsfActivity extends BaseActivity implements View.OnClickListener {
    private final Logger mLogger = Logger.getLogger(getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_yanglao_jgsf);
        initToolBar();
        setTitleText(getString(R.string.yanglao_query));

        initView();
    }

    private void initView() {
        findView(R.id.rl_query_yl_grzhqk).setOnClickListener(this);
        findView(R.id.rl_query_yl_yljqk).setOnClickListener(this);
        final ImageView ivYlGrzhqk = findView(R.id.iv_query_yl_grzhqk);
        final ImageView ivYlYljqk = findView(R.id.iv_query_yl_yljqk);
        ivYlGrzhqk.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = ivYlGrzhqk.getWidth();
                int height = (int) (width * 0.33333);
                ivYlGrzhqk.getLayoutParams().height = height;
                ivYlGrzhqk.getLayoutParams().height = height;
                ivYlYljqk.getLayoutParams().height = height;
                ivYlGrzhqk.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!checkUserLogin()) return;
        switch (v.getId()) {
            case R.id.rl_query_yl_grzhqk:
                startActivity(new Intent(QueryYanglaoJgsfActivity.this, QueryYanglaoAccountInfoActivity.class));
                break;
            case R.id.rl_query_yl_yljqk:
                startActivity(new Intent(QueryYanglaoJgsfActivity.this, YanglaoInfoJgsfActivity.class));
            default:
                break;
        }
    }

}
