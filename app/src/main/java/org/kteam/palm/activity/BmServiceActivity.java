package org.kteam.palm.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.StringUtils;
import org.kteam.common.utils.ViewUtils;
import org.kteam.common.view.ProgressHUD;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.CcbFaceResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description 便民服务
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-18 23:35
 */
public class BmServiceActivity extends BaseActivity implements View.OnClickListener, DialogInterface.OnCancelListener, OnBannerListener {
    private final Logger mLogger = Logger.getLogger(getClass());
    private ProgressHUD mProgressHUD;
    private Banner mBanner;
    private int mScreenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bm_service);
        initToolBar();
        setTitleText(getString(R.string.bm_service));
        mScreenWidth = getIntent().getIntExtra("screenWidth", 1080);
        initView();
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
        mBanner = findView(R.id.banner);
        findViewById(R.id.ll_jh_bm_service).setOnClickListener(this);
        SimpleDraweeView sdv = findView(R.id.sdv);
        Uri uri = Uri.parse("http://www.sclzsi.cn/WechatIMG50.jpeg");
        sdv.setImageURI(uri);
        sdv.setOnClickListener(this);
        initBanner();
    }

    private void initBanner() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mBanner.getLayoutParams();
        params.width = mScreenWidth;
        params.height = (int) (mScreenWidth * 0.3462);
        mBanner.setLayoutParams(params);

        List<Integer> imgs = new ArrayList<>();
        imgs.add(R.mipmap.banner_bm1);
        imgs.add(R.mipmap.banner_bm2);
        imgs.add(R.mipmap.banner_bm3);
        imgs.add(R.mipmap.banner_bm4);

//        List<String> titleList = new ArrayList<>();
//        titleList.add("梦想储蓄");
//        titleList.add("大额存单");
//        titleList.add("拼团存款");
//        titleList.add("旺财");

        //设置内置样式，共有六种可以点入方法内逐一体验使用。
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器，图片加载器在下方
        mBanner.setImageLoader(new MyLoader());
        //设置图片网址或地址的集合
        mBanner.setImages(imgs);
        //设置轮播的动画效果，内含多种特效，可点入方法内查找后内逐一体验
        mBanner.setBannerAnimation(Transformer.Default);
        //设置轮播图的标题集合
//        mBanner.setBannerTitles(titleList);
        //设置轮播间隔时间
        mBanner.setDelayTime(3000);
        //设置是否为自动轮播，默认是“是”。
        mBanner.isAutoPlay(true);
        //设置指示器的位置，小点点，左中右。
        mBanner.setIndicatorGravity(BannerConfig.CENTER)
                .setOnBannerListener(this)
                .start();
    }

//    public static String getStringFromDrawableRes(Context context, int id) {
//        Resources resources = context.getResources();
//        String path = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(id) + "/" + resources.getResourceTypeName(id) + "/" + resources.getResourceEntryName(id);
//        return path;
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_jh_bm_service:
                ccbFace();
                break;
            case R.id.sdv:
                startActivity(new Intent(BmServiceActivity.this, BmIntroActivity.class));
                break;
            default:
                break;
        }
    }

    public void ccbFace() {
        mProgressHUD.show(getString(R.string.loading), true, true, this);
        HashMap<String, String> paramMap = new HashMap<String, String>();
        String mUserId = mUser == null ? StringUtils.getRandomKey() : String.valueOf(mUser.id);
        paramMap.put("userid", mUserId);
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_CCBFACE_URL);
        paramMap.put("token", token);

        RequestClient<CcbFaceResponse> requestClient = new RequestClient<CcbFaceResponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<CcbFaceResponse>() {
            @Override
            public void onLoadComplete(CcbFaceResponse response) {
                mProgressHUD.dismiss();
                if (response.code == 0) {
                    if (response.body != null && response.body.containsKey("url") && !TextUtils.isEmpty(response.body.get("url"))) {
                        Intent intent = new Intent(BmServiceActivity.this, CcbFaceActivity.class);
                        intent.putExtra("url", response.body.get("url"));
                        startActivity(intent);
                    }
                } else {
                    ViewUtils.showToast(BmServiceActivity.this, response.msg);
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                ViewUtils.showToast(BmServiceActivity.this, R.string.network_error);
                mProgressHUD.dismiss();
            }
        });
        requestClient.executePost(this,
                getString(R.string.jumping),
                Constants.BASE_URL + Constants.URL_CCBFACE_URL,
                CcbFaceResponse.class,
                paramMap);
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    @Override
    public void OnBannerClick(int position) {
        Intent intent = new Intent(this, AdDetailActivity.class);
        switch (position) {
            case 0:
                intent.putExtra("title", "梦想储蓄");
                intent.putExtra("url", "https://www.ccbjrfw.com/cciapp/cmpn_main/mxcx");
                startActivity(intent);
                break;
            case 1:
                intent.putExtra("title", "大额存单");
                intent.putExtra("url", "https://www.ccbjrfw.com/cciapp/cmpn_main/decd");
                startActivity(intent);
                break;
            case 2:
                intent.putExtra("title", "拼团存款");
                intent.putExtra("url", "http://www.ccb.com/cn/html1/office/ebank/subject/19/jumpPg/index.html?funcid=01991001&businessid=gr00002020012201");
                startActivity(intent);
                break;
            case 3:
                intent.putExtra("title", "旺财");
                intent.putExtra("url", "http://www.ccb.com/cn/html1/office/ebank/subject/19/jumpPg/index.html?funcid=01991001&businessid=wl00002020011705");
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    private class MyLoader extends ImageLoader {

        @Override
        public ImageView createImageView(Context context) {
            SimpleDraweeView simpleDraweeView = new SimpleDraweeView(context);
            return simpleDraweeView;
        }

        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Uri uri = Uri.parse("res://mipmap/" + path);
            imageView.setImageURI(uri);
        }
    }
}
