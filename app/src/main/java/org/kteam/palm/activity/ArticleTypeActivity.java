package org.kteam.palm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.BaseApplication;
import org.kteam.palm.R;
import org.kteam.palm.adapter.ArticleTypeAdapter;
import org.kteam.palm.adapter.BaseListAdapter;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.view.ArticleDividerItemDecoration;
import org.kteam.palm.common.view.OnRVItemClickListener;
import org.kteam.palm.domain.manager.AccessTimeManager;
import org.kteam.palm.model.AccessTime;
import org.kteam.palm.model.Module;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.ModuleReponse;

import java.util.HashMap;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-03-06 12:52
 */
public class ArticleTypeActivity extends BaseActivity implements OnRVItemClickListener {
    private final Logger mLogger = Logger.getLogger(getClass());

    private View mLineTop;
    private String mCatetoryId;

    private ArticleTypeAdapter mArticleTypeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_type);

        initToolBar();

        String title = getIntent().getStringExtra("title");
        setTitleText(title);

        initView();

        mCatetoryId = getIntent().getStringExtra("categoryId");
        loadData(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(false);
    }

    private void initView() {
        mLineTop = findViewById(R.id.line_top);

        RecyclerView recyclerView = findView(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new ArticleDividerItemDecoration(this));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        mArticleTypeAdapter = new ArticleTypeAdapter();
        recyclerView.setAdapter(mArticleTypeAdapter);
        mArticleTypeAdapter.setOnItemClickListener(this);
    }

    private void loadData(boolean isShowProgress) {
        final AccessTimeManager accessTimeManager = new AccessTimeManager();
        final AccessTime accessTime = accessTimeManager.getAccessTime(mCatetoryId);

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("categoryPid", mCatetoryId);
        paramMap.put("lastTime", accessTime.time);
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_GET_MODULE);
        paramMap.put("token", token);

        RequestClient<ModuleReponse> requestClient = new RequestClient<ModuleReponse>();
        requestClient.setUseProgress(isShowProgress);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<ModuleReponse>() {
            @Override
            public void onLoadComplete(ModuleReponse response) {

                if (response.code == 0) {
                    int visibility = response.body != null && response.body.size() > 0 ? View.VISIBLE : View.GONE;
                    mLineTop.setVisibility(visibility);
                    accessTimeManager.updateAccessTime(accessTime);
                    mArticleTypeAdapter.setData(response.body);
                    mArticleTypeAdapter.notifyDataSetChanged();
                } else {
                    ViewUtils.showToast(BaseApplication.getContext(), response.msg);
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                mLineTop.setVisibility(View.GONE);
            }
        });
        requestClient.executePost(this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_GET_MODULE,
                ModuleReponse.class,
                paramMap);
    }

    @Override
    public void onItemClick(BaseListAdapter adapter, int position) {
        Module module = (Module) adapter.getItem(position);
        if (module.wz_flag > 0) {
            mArticleTypeAdapter.clearData();
            mArticleTypeAdapter.notifyDataSetChanged();
            mCatetoryId = String.valueOf(module.id);
            loadData(true);
            setTitleText(module.name);
        } else if (module.wz_flag == 0) {
            Intent intent = new Intent(this, ArticleListActivity.class);
            intent.putExtra("title", module.name);
            intent.putExtra("categoryId", String.valueOf(module.id));
            startActivity(intent);
        }
    }
}