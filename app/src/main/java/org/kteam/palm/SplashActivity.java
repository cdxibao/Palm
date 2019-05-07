package org.kteam.palm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.ImageView;

import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.palm.common.utils.AppLogUtils;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.utils.IPUtils;
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
 * @Date 2015-12-26 16:29
 */
public class SplashActivity extends BaseActivity {

    private ImageView mIvMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        startService(new Intent(this, CheckVersionService.class));
        AppLogUtils.userLog(AppLogUtils.YWLX_FW);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 2000);
        mIvMain = (ImageView) findViewById(R.id.iv_main);
        Bitmap bm = BitmapFactory.decodeResource(this.getResources(), R.mipmap.loading);
        BitmapDrawable bd = new BitmapDrawable(this.getResources(), bm);
        mIvMain.setBackgroundDrawable(bd);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        BitmapDrawable bd = (BitmapDrawable) mIvMain.getBackground();
        mIvMain.setBackgroundResource(0);
        bd.setCallback(null);
        bd.getBitmap().recycle();
    }
}
