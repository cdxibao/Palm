package org.kteam.palm.activity;

import android.net.Uri;
import android.os.Bundle;

import com.facebook.drawee.view.SimpleDraweeView;

import org.kteam.palm.BaseActivity;
import org.kteam.palm.R;

/**
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2019/3/30 10:54
 * @Copyright: 2018-2020 www.cdxibao.com Inc. All rights reserved.
 */
public class BmIntroActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bm_intro);
        initToolBar();
        setTitleText(getString(R.string.jh_kd_intro));
        initView();
    }

    private void initView() {
        SimpleDraweeView sdv = findView(R.id.sdv);
        Uri uri = Uri.parse("http://www.sclzsi.cn/WechatIMG49.jpeg");
        sdv.setImageURI(uri);
    }
}
