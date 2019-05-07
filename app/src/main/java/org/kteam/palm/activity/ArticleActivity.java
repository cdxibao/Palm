package org.kteam.palm.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.BaseApplication;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.model.Article;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.ArticleListResponse;
import org.kteam.palm.network.response.ArticleResponse;

import java.util.HashMap;

/**
 * @Package org.kteam.activity
 * @Project Palm
 * @Description 文章
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-05 18:36
 */
public class ArticleActivity extends BaseActivity {
    private final Logger mLogger = Logger.getLogger(getClass());
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        initToolBar();

        String title = getIntent().getStringExtra("title");
        setTitleText(title);

        initView();

        if ("article_list".equals(getIntent().getStringExtra("from")) || "left_menu".equals(getIntent().getStringExtra("from"))) {
            String articleId = getIntent().getStringExtra("articleId");
            loadArticle(articleId);
        } else {
            String categoryId = getIntent().getStringExtra("categoryId");
            loadCategory(categoryId);
        }
    }

    private void initView() {
        mTvTitle.setPadding(0, 0, ViewUtils.dip2px(this, 15), 0);
        mWebView = findView(R.id.web_view);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("mailto:") || url.startsWith("geo:") || url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                mWebView.setVisibility(View.GONE);
            }
        });

        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setVerticalScrollbarOverlay(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setHorizontalScrollbarOverlay(false);
        mWebView.setScrollContainer(false);
        mWebView.setFocusable(false);
        mWebView.setFocusableInTouchMode(false);

        mWebView.getSettings().setDefaultTextEncodingName("UTF-8") ;
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.setWebChromeClient(new WebChromeClient());
    }

    private void loadCategory(String categoryId) {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("categoryId", categoryId);
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_GET_ARTICLE_LIST);
        paramMap.put("token", token);

        RequestClient<ArticleListResponse> requestClient = new RequestClient<ArticleListResponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<ArticleListResponse>() {
            @Override
            public void onLoadComplete(ArticleListResponse response) {
                if (response.code == 0 && response.body != null && response.body.size() > 0 && response.body.get(0) != null) {
                    loadArticle(String.valueOf(response.body.get(0).id));
                } else {
                    if (!TextUtils.isEmpty(response.msg)) {
                        ViewUtils.showToast(BaseApplication.getContext(), response.msg);
                    }
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

    private void loadArticle(String articleId) {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("articleId", articleId);
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_GET_ARTICLE);
        paramMap.put("token", token);

        RequestClient<ArticleResponse> requestClient = new RequestClient<ArticleResponse>();
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<ArticleResponse>() {
            @Override
            public void onLoadComplete(ArticleResponse response) {
                if (response.code == 0) {
                   if (response.body != null) {
                       setTitleText(response.body.title);
                       mWebView.loadDataWithBaseURL(null, response.body.content, "text/html", "UTF-8", null);
                   }
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
                Constants.BASE_URL + Constants.URL_GET_ARTICLE,
                ArticleResponse.class,
                paramMap);
    }
}
