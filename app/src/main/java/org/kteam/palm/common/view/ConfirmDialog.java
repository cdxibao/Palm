package org.kteam.palm.common.view;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.kteam.palm.R;

/**
 * @Project Palm
 * @Packate org.kteam.palm.common.view
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2015-12-18 17:49
 * @Version 0.1
 */
public class ConfirmDialog<T> extends Dialog implements View.OnClickListener {

    private TextView mTvContent;

    private boolean mAbleBackKey;

    private OnDialogListener<T> mOnDialogListener;

    public void setAbleBackKey(boolean ableBackKey) {
        mAbleBackKey = ableBackKey;
    }

    public boolean isAbleBackKey() {
        return mAbleBackKey;
    }

    private T mT;


    public ConfirmDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    public ConfirmDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    public ConfirmDialog(Context context) {
        super(context);
        init();
    }

    public void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.confirm_dialog, null);
        setContentView(view);
        mTvContent = (TextView) view.findViewById(R.id.tv_content);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    public void setTag(T t) {
        mT = t;
    }

    public void setContent(String title) {
        mTvContent.setText(title);
    }

    public void setContent(int resId) {
        setContent(getContext().getString(resId));
    }

    public void setOnDialogListener(OnDialogListener<T> listener) {
        mOnDialogListener = listener;
    }

    public interface OnDialogListener<T> {
        void onOK(T t);
        void onCancle(T t);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                if (mOnDialogListener != null) {
                    mOnDialogListener.onOK(mT);
                }
                break;
            case R.id.btn_cancel:
                if (mOnDialogListener != null) {
                    mOnDialogListener.onCancle(mT);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !mAbleBackKey) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}