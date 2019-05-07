package org.kteam.palm.common.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.R;

import java.util.Random;

/**
 * @Project Palm
 * @Packate org.kteam.common.view
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2016-01-05 15:59
 * @Version 1.0
 */
public class ModuleButton extends RelativeLayout {

    private TextView mTextView;
    private ImageView mImageView;
    private ImageView mIvNewMsg;
    private int mPosition;
    private int mState = 0; // 按下
    private float mLastY = 0;
    public int mFingerHeight;

    private OnModuleButtonClickListener mOnModuleButtonClickListener;

    public ModuleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    public ModuleButton(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mTextView = new TextView(context);
        mTextView.setTextSize(18);
        mTextView.setTextColor(Color.WHITE);
        LayoutParams tvParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tvParams.addRule(CENTER_IN_PARENT);
        addView(mTextView, tvParams);

        LayoutParams ivParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        ivParams.addRule(CENTER_IN_PARENT);
        mImageView = new ImageView(context);
        mImageView.setVisibility(View.GONE);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(mImageView, ivParams);

        int ivMsgWidth = ViewUtils.dip2px(getContext(), 18);
        LayoutParams ivMsgParams = new LayoutParams(ivMsgWidth, ivMsgWidth);
        ivMsgParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        ivMsgParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        ivMsgParams.setMargins(10, 10, 10, 10);
        mIvNewMsg = new ImageView(context);
        mIvNewMsg.setImageResource(R.mipmap.newmsg);
        addView(mIvNewMsg, ivMsgParams);
    }

    public void setText(String text, int textColorResId) {
        setBackgroundColor(getResources().getColor(textColorResId));
        setBackgroundResource(textColorResId);
        mTextView.setText(text);
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setMsgVisiblie(int visibility) {
        mIvNewMsg.setVisibility(visibility);
    }

    public void setOnModuleButtonClickListener(OnModuleButtonClickListener listener) {
        mOnModuleButtonClickListener = listener;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
//        if (getHeight() + 10 <= getWidth() * 0.5) {
//            mImageView.getLayoutParams().width = getHeight();
//            mImageView.getLayoutParams().height = getHeight();
//        } else {
//            int height = (int) (getHeight() * 0.5);
//            mImageView.getLayoutParams().width = height ;
//            mImageView.getLayoutParams().height = height;
//        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float start = 1.0f;
        float end = 0.95f;
        final Animation scaleAnimation = new ScaleAnimation(start, end, start, end,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        final Animation endAnimation = new ScaleAnimation(end, start, end, start,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        endAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mImageView.setVisibility(View.GONE);
                if (mState == 1 && mOnModuleButtonClickListener != null) {
                    mOnModuleButtonClickListener.onModuleButtonClick(ModuleButton.this);
                }
                mState = 0;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        scaleAnimation.setDuration(200);
        scaleAnimation.setFillAfter(true);
        endAnimation.setDuration(200);
        endAnimation.setFillAfter(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = event.getRawY();
                mState = 1;
                updateFinger();
                mImageView.setVisibility(View.VISIBLE);
                startAnimation(scaleAnimation);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = event.getRawY() - mLastY;
                mLastY = event.getRawY();
                if (Math.abs(deltaY) >= 10) {
                    mState = 0;
                    this.startAnimation(endAnimation);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                this.startAnimation(endAnimation);
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                mState = 0;
                this.startAnimation(endAnimation);
                invalidate();
                break;
        }
        return true;
    }

    private void updateFinger() {
        int resId = R.mipmap.fingerprint0;
        switch (new Random().nextInt(4)) {
            case 0:
                resId = R.mipmap.fingerprint0;
                break;
            case 1:
                resId = R.mipmap.fingerprint1;
                break;
            case 2:
                resId = R.mipmap.fingerprint2;
                break;
            case 3:
                resId = R.mipmap.fingerprint3;
                break;
            default:
                break;
        }
        mImageView.setImageResource(resId);
        mImageView.getLayoutParams().width = getFingerHeight() ;
        mImageView.getLayoutParams().height = mImageView.getLayoutParams().width;
    }

    public int getFingerHeight() {
        return (int)(150 * Math.random() + 150);
    }

    public interface OnModuleButtonClickListener {
       void onModuleButtonClick(ModuleButton button);
    }
}
