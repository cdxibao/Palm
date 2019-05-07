package org.kteam.palm.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.R;

/**
 * @Project Palm
 * @Packate org.kteam.palm.common.view
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2015-12-28 11:47
 * @Version 0.1
 */
public class TipTextView extends TextView {

    private int mLeftIconResId;

    public TipTextView(Context context) {
        super(context);
    }

    public TipTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TipTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        try {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TipTextView);
            if (typedArray == null) return;

            mLeftIconResId = typedArray.getResourceId(R.styleable.TipTextView_leftIcon, 0);
            setLeftIcon();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTipText() {
        setLeftIcon();
    }

    public void setLeftIcon() {
        String text = getText().toString();
        if (TextUtils.isEmpty(text)) {
            return;
        }
        SpannableStringBuilder ssb = new SpannableStringBuilder("icon");
        ssb.append(text);
        int wdith = ViewUtils.dip2px(getContext(), 18);
        Drawable drawable = getContext().getResources().getDrawable(mLeftIconResId);
        drawable.setBounds(0, 0, wdith, wdith);
        CenterImageSpan imageSpan = new CenterImageSpan(drawable);
        ssb.setSpan(imageSpan, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(ssb);
    }
}
