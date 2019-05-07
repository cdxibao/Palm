package org.kteam.palm.common.view;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.kteam.palm.R;

/**
 * @Project Palm
 * @Packate org.kteam.palm.common.view
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2015-12-28 15:59
 * @Version 1.0
 */
public class DownloadingDialog extends Dialog {


    private boolean mAbleBackKey;

    public void setAbleBackKey(boolean ableBackKey) {
        mAbleBackKey = ableBackKey;
    }

    private ProgressBar mProgressBar;

    public DownloadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    public DownloadingDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    public DownloadingDialog(Context context) {
        super(context);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.downloading_dialog, null);
        setContentView(view);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !mAbleBackKey) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setProgress(int progress) {
        mProgressBar.setProgress(progress);
    }
}
