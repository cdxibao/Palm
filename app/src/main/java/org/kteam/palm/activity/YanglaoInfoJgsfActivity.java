package org.kteam.palm.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.common.view.ProgressHUD;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.MainActivity;
import org.kteam.palm.R;
import org.kteam.palm.adapter.YanglaoInfoJgsfAdapter;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.view.ArticleDividerItemDecoration;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.YanglaoInfoJgsfResponse;

import java.util.HashMap;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description 职工医疗保险历年缴费情况
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-12 18:13
 */
public class YanglaoInfoJgsfActivity extends BaseActivity implements DialogInterface.OnCancelListener{
    private final Logger mLogger = Logger.getLogger(getClass());

    private TextView mTvUserName;
    private TextView mTvIdCard;

    private ProgressHUD mProgressHUD;

    private YanglaoInfoJgsfAdapter mYanglaoInfoJgsfAdapter;

    private int mItem1Width;
    private int mItem2Width;
    private int mItem3Width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yanglao_info_jgsf);
        initToolBar();
        initView();
        if (mUser == null) {
            finish();
            return;
        }
        setTitleText(getString(R.string.jgsyylbxyljffjl));
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressHUD != null) {
            mProgressHUD.dismiss();
        }
    }

    private void initView() {
        mProgressHUD = new ProgressHUD(this);

        mTvUserName = findView(R.id.tv_user_name);
        mTvUserName.setText(getString(R.string.order_username, mUser.name));
        mTvIdCard = findView(R.id.tv_id_card);
        mTvIdCard.setText(getString(R.string.order_idcard, mUser.idcard));

        RecyclerView recyclerView = findView(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new ArticleDividerItemDecoration(this));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        mYanglaoInfoJgsfAdapter = new YanglaoInfoJgsfAdapter();
        recyclerView.setAdapter(mYanglaoInfoJgsfAdapter);

        DisplayMetrics metrics = ViewUtils.getScreenInfo(this);
        mItem1Width = (int) (metrics.widthPixels * 0.30);
        mItem2Width = (int) (metrics.widthPixels * 0.30);
        mItem3Width = (int) (metrics.widthPixels * 0.40);
        mYanglaoInfoJgsfAdapter.setItemWidth(mItem1Width, mItem2Width, mItem3Width);

        LinearLayout layout1 = findView(R.id.layout1);
        LinearLayout layout2 = findView(R.id.layout2);
        LinearLayout layout3 = findView(R.id.layout3);

        layout1.getLayoutParams().width = mItem1Width;
        layout2.getLayoutParams().width = mItem2Width;
        layout3.getLayoutParams().width = mItem3Width;
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

    private void loadData() {
        mProgressHUD.show(getString(R.string.loading), true, true, this);

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("aac002", String.valueOf(mUser.idcard));
        paramMap.put("aac003", String.valueOf(mUser.name));
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_YANGLAO_INFO_JGSF);
        paramMap.put("token", token);

        RequestClient<YanglaoInfoJgsfResponse> requestClient = new RequestClient<YanglaoInfoJgsfResponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<YanglaoInfoJgsfResponse>() {
            @Override
            public void onLoadComplete(YanglaoInfoJgsfResponse response) {
                mProgressHUD.dismiss();
                if (response.code == 0) {
                    if (response.body != null && response.body.size() > 0) {
                        mYanglaoInfoJgsfAdapter.appendDataList(response.body);
                        mYanglaoInfoJgsfAdapter.notifyDataSetChanged();
                    }
                } else {
                    ViewUtils.showToast(YanglaoInfoJgsfActivity.this, response.msg);
                    finish();
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                ViewUtils.showToast(YanglaoInfoJgsfActivity.this, R.string.network_error);
                mProgressHUD.dismiss();
                finish();
            }
        });
        requestClient.executePost(this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_YANGLAO_INFO_JGSF,
                YanglaoInfoJgsfResponse.class,
                paramMap);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        finish();
    }
}
