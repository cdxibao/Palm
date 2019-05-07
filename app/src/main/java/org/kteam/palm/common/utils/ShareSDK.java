package org.kteam.palm.common.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.ShareContent;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.editorpage.ShareActivity;
import com.umeng.socialize.handler.SmsHandler;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMediaObject;
import com.umeng.socialize.media.UMusic;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.BaseApplication;
import org.kteam.palm.R;

/**
 * @Package org.kteam.palm.common.utils
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-03-27 21:28
 */
public class ShareSDK {

    private Activity mActivity;

    public ShareSDK(Activity activity) {
        mActivity = activity;
    }

    public void openShareBoard() {
        final UMImage image = new UMImage(mActivity, R.mipmap.ic_launcher);
        final String url = "http://sclz.lss.gov.cn/lzzhsb/download.html";

        ShareAction action = new ShareAction(mActivity);
        action.setDisplayList(
                SHARE_MEDIA.QQ,
                SHARE_MEDIA.QZONE,
                SHARE_MEDIA.WEIXIN,
                SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.SINA,
                SHARE_MEDIA.SMS).setShareboardclickCallback(new ShareBoardlistener() {
            @Override
            public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                if (share_media.equals(SHARE_MEDIA.SMS)) {
                    new ShareAction(mActivity)
                            .setPlatform(share_media)
                            .withText(mActivity.getString(R.string.app_name) + "邀请您使用,请打开链接下载 " + url)
                            .setCallback(umShareListener).share();
                } else {
                    new ShareAction(mActivity)
                            .setPlatform(share_media)
                            .withText(mActivity.getString(R.string.app_name) + "邀请您使用")
                            .withMedia(image)
                            .withTargetUrl(url)
                            .setCallback(umShareListener).share();
                }
            }
        }).open();
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            ViewUtils.showToast(mActivity, "分享失败,请稍候重试");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
        }
    };
}
