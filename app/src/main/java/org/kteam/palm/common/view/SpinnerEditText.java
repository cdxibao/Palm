package org.kteam.palm.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.kteam.palm.R;
import org.w3c.dom.Text;

/**
 * @Project Palm
 * @Packate org.kteam.palm.common.view
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2015-12-16 16:33
 * @Version 0.1
 */
public class SpinnerEditText extends LinearLayout {

    private TextView mTextView;

    public SpinnerEditText(Context context) {
        super(context);
        init(context);
    }

    public SpinnerEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_spinner_edit, this);
        mTextView = (TextView) findViewById(R.id.tv);
    }

    public void setTextWidth(int width) {
        mTextView.getLayoutParams().width = width;
        mTextView.requestLayout();
        requestLayout();
        invalidate();
    }

    public TextView getTextView() {
        return mTextView;
    }

    public void setText(String text) {
        mTextView.setText(text);
    }
}
