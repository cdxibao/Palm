package org.kteam.common.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.kteam.palm.R;


/**
 * @Description 加载进度显示
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2015-06-16 11:30
 */
public class ProgressHUD extends Dialog {
    private ImageView mIvSpinner;
    private TextView mTvMessage;

    public ProgressHUD(Context context) {
        super(context, R.style.ProgressHUD);

        Window window = getWindow(); //得到对话框
        window.setWindowAnimations(R.style.ToastAnim); //设置窗口弹出动画

        setContentView(R.layout.view_progress_hud);
        mTvMessage = (TextView) findViewById(R.id.tv_messsage);
        mIvSpinner = (ImageView) findViewById(R.id.iv_spinner);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        AnimationDrawable spinner = (AnimationDrawable) mIvSpinner.getBackground();
        spinner.start();
    }

    public void setMessage(CharSequence message) {
        if (message != null && message.length() > 0) {
            mTvMessage.setVisibility(View.VISIBLE);
            mTvMessage.setText(message);
            mTvMessage.invalidate();
        }
    }

    public void show(CharSequence message, boolean indeterminate, boolean cancelable, OnCancelListener cancelListener) {
        if (TextUtils.isEmpty(message)) {
            mTvMessage.setVisibility(View.GONE);
        }
        mTvMessage.setText(message);
        setCancelable(cancelable);
        setCanceledOnTouchOutside(false);//触摸不消失
        setOnCancelListener(cancelListener);
        getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.2f;
        getWindow().setAttributes(lp);
        // dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        show();
    }
    
}
