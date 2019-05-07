package org.kteam.palm.common.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.R;
import org.kteam.palm.activity.AdDetailActivity;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.model.Ad;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Package org.kteam.palm.common.view
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-06 00:45
 */
public class AdView extends LinearLayout {

    private ViewPager mViewPager;
    private LinearLayout mLayoutIndicator;

    private List<ImageView> mAdIvList;
    private List<ImageView> mIndicatorIvList;
    private List<Ad> mAdList;

    private AtomicInteger mAtomicInteger = new AtomicInteger(0);
    private boolean mContinue = true;

    private final Handler viewHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            mViewPager.setCurrentItem(msg.what);
            super.handleMessage(msg);
        }
    };

    public AdView(Context context) {
        super(context);
        init(context);
    }

    public AdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_ad, this);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        DisplayMetrics dm = ViewUtils.getScreenInfo(context);
        mViewPager.getLayoutParams().width = dm.widthPixels;
        mViewPager.getLayoutParams().height = (int)(dm.widthPixels * 0.4);
        mViewPager.addOnPageChangeListener(new OnMyPageChangeListener());

        mLayoutIndicator = (LinearLayout) view.findViewById(R.id.layout_indicator);
    }

    public Ad getSelectedAd() {
        if (mAdList != null) {
            return mAdList.get(mViewPager.getCurrentItem());
        }
        return null;
    }

    public void setAdList(List<Ad> list) {
        mAdList = list;
        if (list != null && list.size() > 0) {
            initPagerView(list);
            if (list.size() >= 2) {
                initIndicator(list);
            }
            mViewPager.setCurrentItem(mAdIvList.size() >= 2 ? 1 : 0);
        }

        if (list.size() > 1) {
            Constants.executorService.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (mContinue) {
                            viewHandler.sendEmptyMessage(mAtomicInteger.get());
                            atomicOption();
                        }
                    }
                }
            });
        }
    }

    private void initPagerView(List<Ad> list) {
        mAdIvList = new ArrayList<ImageView>();
        for (Ad ad : list) {
            SimpleDraweeView iv = new SimpleDraweeView(getContext());
            iv.setImageURI(Uri.parse(ad.img_url));
            iv.setTag(ad);
            mAdIvList.add(iv);
        }
        if (list.size() >= 2) {
            SimpleDraweeView ivFirst = new SimpleDraweeView(getContext());
            ivFirst.setImageURI(Uri.parse(list.get(list.size() - 1).img_url));
            mAdIvList.add(0, ivFirst);

            SimpleDraweeView ivLast = new SimpleDraweeView(getContext());
            ivFirst.setImageURI(Uri.parse(list.get(1).img_url));
            mAdIvList.add(ivLast);
        }
        AdPageAdapter adapter = new AdPageAdapter(mAdIvList);
        mViewPager.setAdapter(adapter);
    }

    private void initIndicator(List<Ad> list) {
        mLayoutIndicator.removeAllViews();
        mIndicatorIvList = new ArrayList<ImageView>();
        ImageView ivIndicator;
        for (int i = 0; i < list.size() + 2; i++) {
            ivIndicator = new ImageView(getContext());
            int width = ViewUtils.dip2px(getContext(), 7);
            LayoutParams params = new LayoutParams(width, width);
            params.leftMargin = 10;
            ivIndicator.setLayoutParams(params);
            mIndicatorIvList.add(ivIndicator);

            if (i == 0) {
                ivIndicator.setBackgroundResource(R.drawable.circle_white);
            } else {
                ivIndicator.setBackgroundResource(R.drawable.circle_gray);
            }
            if (list.size() >= 2) {
                if (i == 0 || i == mAdIvList.size() - 1) {
                    ivIndicator.setVisibility(View.INVISIBLE);
                }
            }
            //将小圆点放入到布局中
            mLayoutIndicator.addView(ivIndicator);
        }
    }

    private void atomicOption() {
        mAtomicInteger.incrementAndGet();
        if (mIndicatorIvList != null && mAtomicInteger.get() > mIndicatorIvList.size() - 1) {
            mAtomicInteger.getAndAdd(-5);
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {

        }
    }

    class OnMyPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position < 1) { //首位之前，跳转到末尾（N）
                position = mAdIvList.size() - 2;
            } else if (position > mAdIvList.size() - 2) { //末位之后，跳转到首位（1）
                position = 1;
            }
            mViewPager.setCurrentItem(position, false);//false:不显示跳转过程的动画

            mAtomicInteger.getAndSet(position);

            if (mIndicatorIvList != null && mIndicatorIvList.size() >= 2) {
                for (int i = 0; i < mIndicatorIvList.size(); i++) {
                    mIndicatorIvList.get(position).setBackgroundResource(R.drawable.circle_white);
                    if (position != i) {
                        mIndicatorIvList.get(i).setBackgroundResource(R.drawable.circle_gray);
                    }
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private final class AdPageAdapter extends PagerAdapter {
        private List<ImageView> views = null;

        public AdPageAdapter(List<ImageView> views) {
            this.views = views;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position));
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public Object instantiateItem(View container, int position) {
            View view = views.get(position);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Ad ad = (Ad) v.getTag();
                    if (!TextUtils.isEmpty(ad.action_url)) {
                        Intent intent = new Intent(getContext(), AdDetailActivity.class);
                        intent.putExtra("title", ad.description);
                        intent.putExtra("url", ad.action_url);
                        getContext().startActivity(intent);
                    }
                }
            });
            ((ViewPager) container).addView(view);
            return views.get(position);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
