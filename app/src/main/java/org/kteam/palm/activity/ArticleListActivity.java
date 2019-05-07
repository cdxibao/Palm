package org.kteam.palm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.BaseApplication;
import org.kteam.palm.R;
import org.kteam.palm.adapter.ArticleAdapter;
import org.kteam.palm.adapter.BaseListAdapter;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.view.OnRVItemClickListener;
import org.kteam.palm.model.Article;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.ArticleListResponse;
import org.kteam.palm.common.view.ArticleDividerItemDecoration;

import java.util.HashMap;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description 文章列表
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-05 20:41
 */
public class ArticleListActivity extends BaseActivity implements OnRVItemClickListener {
    private final Logger mLogger = Logger.getLogger(getClass());

    private ArticleAdapter mArticleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        initToolBar();

        String title = getIntent().getStringExtra("title");
        setTitleText(title);

        initView();

        String categoryId = getIntent().getStringExtra("categoryId");
        loadData(categoryId);
    }

    private void initView() {
        RecyclerView recyclerView = findView(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new ArticleDividerItemDecoration(this));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        mArticleAdapter = new ArticleAdapter();
        recyclerView.setAdapter(mArticleAdapter);
        mArticleAdapter.setOnItemClickListener(this);
    }

    private void loadData(String categoryId) {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("categoryId",categoryId);
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_GET_ARTICLE_LIST);
        paramMap.put("token", token);

        RequestClient<ArticleListResponse> requestClient = new RequestClient<ArticleListResponse>();
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<ArticleListResponse>() {
            @Override
            public void onLoadComplete(ArticleListResponse response) {
                if (response.code == 0) {
                    mArticleAdapter.setData(response.body);
                    mArticleAdapter.notifyDataSetChanged();
                } else {
                    ViewUtils.showToast(BaseApplication.getContext(), response.msg);
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
            }
        });
        requestClient.executePost(this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_GET_ARTICLE_LIST,
                ArticleListResponse.class,
                paramMap);
    }

    @Override
    public void onItemClick(BaseListAdapter adapter, int position) {
        Article article = (Article) adapter.getItem(position);
        Intent intent = new Intent(this, ArticleActivity.class);
        intent.putExtra("from", "article_list");
        intent.putExtra("title", article.title);
        intent.putExtra("articleId", String.valueOf(article.id));
        startActivity(intent);
    }
}
