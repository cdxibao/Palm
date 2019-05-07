package org.kteam.palm.common.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.kteam.palm.R;
import org.kteam.palm.model.BaseData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Package org.kteam.palm.common.view
 * @Project Palm
 *
 * @Description
 *
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-13 18:43
 */
public class SpinnerDialog extends Dialog implements View.OnClickListener {

    private TextView mTvTitle;
    private WheelView mWheelView;
    private OnSpinnerDialogClickListener mOnSpinnerDilalogClickListener;
    private BaseData mSelectedData;
    private List<BaseData> mDataList;


    public SpinnerDialog(Context context) {
        super(context);
        init(context);
    }

    public SpinnerDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    public SpinnerDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    public void setTitle(int resId) {
        mTvTitle.setText(getContext().getString(resId));
    }

    public void setData(List<BaseData> list) {
        if (list == null && list.size() > 0) {
            return;
        }
        Collections.reverse(list);
        mWheelView.setOffset(2);
        mDataList = list;
        mWheelView.setSeletion(0);
        List<String> strList = new ArrayList<String>(list.size());
        for (BaseData baseData : list) {
            strList.add(baseData.label);
        }

        mWheelView.setItems(strList);
        mWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                if (selectedIndex > mDataList.size() - 1) return;
                mSelectedData = mDataList.get(selectedIndex);
            }
        });
    }

    public void setOnSpinnerDialogClickListener(OnSpinnerDialogClickListener onSpinnerDilalogClickListener) {
        mOnSpinnerDilalogClickListener = onSpinnerDilalogClickListener;
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_wheel_dialog, null);
        setContentView(view);
        view.findViewById(R.id.btn_ok).setOnClickListener(this);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mWheelView = (WheelView) view.findViewById(R.id.wheel_view_wv);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                hide();
                if (mOnSpinnerDilalogClickListener != null) {
                    mOnSpinnerDilalogClickListener.onOKClicked(mSelectedData);
                }
                break;
            default:
                break;
        }
    }

    public interface OnSpinnerDialogClickListener {
        void onOKClicked(BaseData data);
    }
}
