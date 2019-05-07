package org.kteam.palm.common.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.kteam.palm.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @Package org.kteam.palm.common.view
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-13 18:43
 */
public class YearDialog extends Dialog implements View.OnClickListener {

    private TextView mTvTitle;
    private WheelView mWheelView;
    private OnYearDialogClickListener mOnYearDialogClickListener;
    private String mSelectYear;

    public YearDialog(Context context) {
        super(context);
        init(context);
    }

    public YearDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    public YearDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    public void setTitle(int resId) {
        mTvTitle.setText(getContext().getString(resId));
    }

    public void setOnYearDialogClickListener(OnYearDialogClickListener onYearDialogClickListener) {
        mOnYearDialogClickListener = onYearDialogClickListener;
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_wheel_dialog, null);
        setContentView(view);
        view.findViewById(R.id.btn_ok).setOnClickListener(this);

        List<String> yearList = new ArrayList<String>(50);
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        for (int i = year; i > year - 50; i--) {
            yearList.add(String.valueOf(i));
        }
        mSelectYear = String.valueOf(mSelectYear);

        mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        mWheelView = (WheelView) view.findViewById(R.id.wheel_view_wv);
        mWheelView.setOffset(2);
        mWheelView.setItems(yearList);
        mWheelView.setSeletion(0);
        mWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                mSelectYear = item;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                hide();
                if (mOnYearDialogClickListener != null) {
                    mOnYearDialogClickListener.onOKClicked(mSelectYear);
                }
                break;
            default:
                break;
        }
    }

    public interface OnYearDialogClickListener {
        void onOKClicked(String year);
    }
}
