package org.kteam.common.view.xlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import org.kteam.common.utils.DateUtils;
import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.R;


/**
 * @Description 可下拉ScrollView
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2015-06-16 18:22
 */
public class XScrollView extends ScrollView implements OnScrollListener {
//    private static final String TAG = "XScrollView";

    private float mLastY = -1; // save event y

    /**
     * 这个类封装了滚动操作。 滚动的持续时间可以通过构造函数传递，并且可以指定滚动动作的持续的最长时间。
     * 经过这段时间，滚动会自动定位到最终位置，并且通过computeScrollOffset()会得到的返回值为false，
     * 表明滚动动作已经结束。
     */
    private Scroller mScroller; // used for scroll back

    /**
     * 当屏幕停止滚动时为0；
     * 当屏幕滚动且用户使用的触碰或手指还在屏幕上时为1；
     * 由于用户的操作，屏幕产生惯性滑动时为2
     */
    private OnScrollListener mScrollListener; // user's scroll listener

    // the interface to trigger refresh and load more.
    private IXScrollViewListener mScorllViewListener;

    // -- header view
    private XListViewHeader mHeaderView;
    // header view content, use it to calculate the Header's height. And hide it
    // when disable pull refresh.
    private RelativeLayout mHeaderViewContent;
    private TextView mHeaderTimeView;
    private int mHeaderViewHeight; // header view's height
    private boolean mEnablePullRefresh = true;
    private boolean mPullRefreshing = false; // is refreashing.

    private LinearLayout mLayout;
    private LinearLayout mContentView;

    // -- footer view
    private XListViewFooter mFooterView;
    private boolean mEnablePullLoad;
    private boolean mPullLoading;

    // for mScroller, scroll back from header or footer.
    private int mScrollBack;
    private final static int SCROLLBACK_HEADER = 0;
    private final static int SCROLLBACK_FOOTER = 1;

    private final static int SCROLL_DURATION = 200; // scroll back duration
    private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
    // at bottom, trigger
    // load more.
    private final static float OFFSET_RADIO = 1.8f; // support iOS like pull

    private String mKey = "";
    private XSharedPreferenceUtils mSharedPreferenceUtils;

    public XScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    private void initWithContext(Context context) {
        mHeaderViewHeight = (int) (60 * ViewUtils.getScreenInfo(context).density);
        setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        mSharedPreferenceUtils = new XSharedPreferenceUtils(context);
        mLayout = (LinearLayout) View.inflate(context, R.layout.vw_xscrollview_layout, null);

        mContentView = (LinearLayout) mLayout.findViewById(R.id.xscroll_view_content);

        mScroller = new Scroller(context, new DecelerateInterpolator());
        // XListView need the scroll event, and it will dispatch the event to
        // user's listener (as a proxy).
        this.setOnScrollListener(this);

        // init header view
        mHeaderView = new XListViewHeader(context);
        mHeaderViewContent = (RelativeLayout) mHeaderView.findViewById(R.id
                .xlistview_header_content);
        mHeaderTimeView = (TextView) mHeaderView.findViewById(R.id.xlistview_header_time);
        addHeaderView(mHeaderView);

        // init footer view
        mFooterView = new XListViewFooter(context);
        addFooterView(mFooterView);

        // init header height
        /**
         * 有时候需要在onCreate方法中知道某个View组件的宽度和高度等信息，而直接调用View组件的
         * getWidth()、getHeight()、getMeasuredWidth()、getMeasuredHeight()、getTop()、getLeft()
         * 等方法是无法获取到真实值的，只会得到0。这是因为View组件布局要在onResume回调后完成。
         * 下面提供实现方法，onGlobalLayout回调会在布局完成时自动调用
         */
        ViewTreeObserver observer = mHeaderView.getViewTreeObserver();
        if (null != observer) {
            observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mHeaderViewHeight = mHeaderViewContent.getHeight();
                    ViewTreeObserver observer = getViewTreeObserver();
                    if (null != observer) {
                        observer.removeGlobalOnLayoutListener(this);
                    }
                }
            });
        }
        this.addView(mLayout);
    }

    private void addHeaderView(XListViewHeader mHeaderView) {
        if (mLayout == null) {
            return;
        }

        LinearLayout mHeadLayout = (LinearLayout) mLayout.findViewById(R.id
                .xscroll_view_header);
        mHeadLayout.addView(mHeaderView);
    }

    private void addFooterView(XListViewFooter mFooterView) {
        if (mLayout == null) {
            return;
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        LinearLayout footLayout = (LinearLayout) mLayout.findViewById(R.id
                .xscroll_view_footer);
        footLayout.addView(mFooterView, params);
    }

    /**
     * 设置ScrollView 中间容器的View;
     *
     * @param contentView 要添加的ViewGroup
     */
    public void setContentView(ViewGroup contentView) {
        if (mLayout == null) {
            return;
        }

        if (mContentView == null) {
            mContentView = (LinearLayout) mLayout.findViewById(R.id.xscroll_view_content);
        }

        if (mContentView.getChildCount() > 0) {
            mContentView.removeAllViews();
        }
        mContentView.addView(contentView);
    }

    /**
     * 设置ScrollView 中间容器的View;
     *
     * @param contentView 要添加的ViewGroup
     */
    public void setView(View contentView) {
        if (mLayout == null) {
            return;
        }

        if (mContentView == null) {
            mContentView = (LinearLayout) mLayout.findViewById(R.id.xscroll_view_content);
        }
        mContentView.addView(contentView);
    }

    /**
     * Auto call back refresh.
     */
    public void autoRefresh() {
        mHeaderView.setVisiableHeight(mHeaderViewHeight);

        if (mEnablePullRefresh && !mPullRefreshing) {
            // update the arrow image not refreshing
            if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                mHeaderView.setState(XListViewHeader.STATE_READY);
            } else {
                mHeaderView.setState(XListViewHeader.STATE_NORMAL);
            }
        }

        mPullRefreshing = true;
        mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
        saveRefreshTime();
        if (mScorllViewListener != null) {
            mScorllViewListener.onRefresh();
        }
        mHeaderView.invalidate();
    }

    /**
     * enable or disable pull down refresh feature.
     *
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;

        if (!mEnablePullRefresh) { // disable, hide the content
            mHeaderViewContent.setVisibility(View.INVISIBLE);
        } else {
            mHeaderViewContent.setVisibility(View.VISIBLE);
        }
    }

    /**
     * enable or disable pull down refresh feature.
     *
     * @param enable
     * @param type
     */
    public void setPullRefreshEnable(boolean enable, String type) {
        mEnablePullRefresh = enable;

        if (!mEnablePullRefresh) { // disable, hide the content
            mHeaderViewContent.setVisibility(View.INVISIBLE);
        } else {
            mHeaderViewContent.setVisibility(View.VISIBLE);
        }

        mHeaderView.setHeaderType(type);
    }

    /**
     * enable or disable pull up load more feature.
     *
     * @param enable
     */
    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;

        if (!mEnablePullLoad) {
            mFooterView.setBottomMargin(0);
            mFooterView.hide();
            mFooterView.setPadding(0, 0, 0, mFooterView.getHeight() * (-1));
            mFooterView.setOnClickListener(null);
        } else {
            mPullLoading = false;
            mFooterView.setPadding(0, 0, 0, 0);
            mFooterView.show();
            mFooterView.setState(XListViewFooter.STATE_NORMAL);
            // both "pull up" and "click" will invoke load more.
            mFooterView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    public boolean getPullLoadEnable() {
        return mEnablePullLoad;
    }

    public void setRefreshTimeKey(String key) {
        mKey = key;
        updateRefreshTime();
    }

    /**
     * stop refresh, reset header view.
     */
    public void stopRefresh() {
        if (mPullRefreshing) {
            mPullRefreshing = false;
            saveRefreshTime();
            resetHeaderHeight();
        }
    }

    /**
     * stop load more, reset footer view.
     */
    public void stopLoadMore() {
        if (mPullLoading) {
            mPullLoading = false;
            mFooterView.setState(XListViewFooter.STATE_NORMAL);
        }
    }

    /**
     * set last refresh time
     *
     * @param time
     */
    public void setRefreshTime(String time) {
        mHeaderTimeView.setText(time);
    }

    public void updateRefreshTime() {
        String time = mSharedPreferenceUtils.getRefereshTime(XSharedPreferenceUtils.TYPE_SCROLL_VIEW, mKey);
        time = DateUtils.getTodayOrYestodayStr(time);
        mHeaderTimeView.setText(time);
    }

    public void saveRefreshTime() {
        mSharedPreferenceUtils.setRefreshTime(XSharedPreferenceUtils.TYPE_SCROLL_VIEW, mKey);
    }

    private void invokeOnScrolling() {
        if (mScrollListener instanceof OnXScrollListener) {
            OnXScrollListener l = (OnXScrollListener) mScrollListener;
            l.onXScrolling(this);
        }
    }

    private void updateHeaderHeight(float delta) {
        mHeaderView.setVisiableHeight((int) delta + mHeaderView.getVisiableHeight());
        if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
            if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                mHeaderView.setState(XListViewHeader.STATE_READY);
            } else {
                mHeaderView.setState(XListViewHeader.STATE_NORMAL);
                updateRefreshTime();
            }
        }

        // scroll to top each time
//        post(new Runnable() {
//            @Override
//            public void run() {
//                XScrollView.this.fullScroll(ScrollView.FOCUS_UP);
//            }
//        });
    }

    /**
     * reset header view's height.
     */
    private void resetHeaderHeight() {
        int height = mHeaderView.getVisiableHeight();
        if (height == 0) // not visible.
            return;
        // refreshing and header isn't shown fully. do nothing.
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }
        int finalHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mPullRefreshing && height > mHeaderViewHeight) {
            finalHeight = mHeaderViewHeight;
        }
        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
        // trigger computeScroll
        invalidate();
        updateRefreshTime();
    }

    private void updateFooterHeight(float delta) {
        int height = mFooterView.getBottomMargin() + (int) delta;
        if (mEnablePullLoad && !mPullLoading) {
            if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
                // more.
                mFooterView.setState(XListViewFooter.STATE_READY);
            } else {
                mFooterView.setState(XListViewFooter.STATE_NORMAL);
            }
        }
        mFooterView.setBottomMargin(height);

        // scroll to bottom
        post(new Runnable() {
            @Override
            public void run() {
                XScrollView.this.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void resetFooterHeight() {
        int bottomMargin = mFooterView.getBottomMargin();
        if (bottomMargin > 0) {
            mScrollBack = SCROLLBACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
            invalidate();
        }
    }

    private void startLoadMore() {
        mPullLoading = true;
        mFooterView.setState(XListViewFooter.STATE_LOADING);
        if (mScorllViewListener != null) {
            mScorllViewListener.onLoadMore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            /**
             * getX()是表示Widget相对于自身左上角的x坐标,而getRawX()是表示相对于屏幕左上角的x坐标值
             * (注意:这个屏幕左上角是手机屏幕左上角,不管activity是否有titleBar或是否全屏幕)
             */
            mLastY = ev.getRawY();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                updateRefreshTime();
                mLastY = ev.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:

                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();

                if (isTop() && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
                    // the first item is showing, header has shown or pull down.
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                    invokeOnScrolling();

                } else if (isBottom() && (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
                    // last item, already pulled up or want to pull up.
                    updateFooterHeight(-deltaY / OFFSET_RADIO);

                }
                break;

            default:
                mLastY = -1; // reset
                resetHeaderOrBottom();
                break;
        }

        return super.onTouchEvent(ev);
    }

    private void resetHeaderOrBottom() {
        if (isTop()) {
            // invoke refresh
            if (mEnablePullRefresh && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                mPullRefreshing = true;
                mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
                if (mScorllViewListener != null) {
                    mScorllViewListener.onRefresh();
                }
            }
            resetHeaderHeight();

        } else if (isBottom()) {
            // invoke load more.
            if (mEnablePullLoad && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
                startLoadMore();
            }
            resetFooterHeight();
        }
//        else {
//            LogUtil.e(TAG, "Y -> " + getScrollY());
//            LogUtil.d(TAG, (getScrollY() + getHeight()) + " - " + computeVerticalScrollRange());
//        }
    }

    private boolean isTop() {
        // 为防止顶部pull后无法回弹的问题，这里做了一定的修正
        return getScrollY() <= 0 || mHeaderView.getVisiableHeight() > mHeaderViewHeight;
    }

    private boolean isBottom() {
        // 为防止底部pull后无法回弹的问题，这里做了一定的修正
        return Math.abs(getScrollY() + getHeight() - computeVerticalScrollRange()) <= 5 ||
                (getScrollY() > 0 && null != mFooterView && mFooterView.getBottomMargin() > 0);
    }

    /**
     * 由父视图调用用来请求子视图根据偏移值 mScrollX,mScrollY重新绘制
     */
    @Override
    public void computeScroll() {
        /**
         * 当startScroll执行过程中即在duration时间内，computeScrollOffset
         * 方法会一直返回false，但当动画执行完成后会返回返加true.
         */
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLLBACK_HEADER) {
                mHeaderView.setVisiableHeight(mScroller.getCurrY());
            } else {
                mFooterView.setBottomMargin(mScroller.getCurrY());
            }
            postInvalidate();
            invokeOnScrolling();
        }
        super.computeScroll();
    }

    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    /**
     * <p>
     * 正在滚动时回调，回调2-3次，手指没抛则回调2次。scrollState = 2的这次不回调
     * 回调顺序如下
     * 第1次：scrollState = SCROLL_STATE_TOUCH_SCROLL(1) 正在滚动
     * 第2次：scrollState = SCROLL_STATE_FLING(2) 手指做了抛的动作（手指离开屏幕前，用力滑了一下）
     * 第3次：scrollState = SCROLL_STATE_IDLE(0) 停止滚动
     * 当屏幕停止滚动时为0；当屏幕滚动且用户使用的触碰或手指还在屏幕上时为1；
     * 由于用户的操作，屏幕产生惯性滑动时为2
     * 当滚到最后一行且停止滚动时，执行加载
     * </p>
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    /**
     * 滚动时一直回调，直到停止滚动时才停止回调。单击时回调一次。
     *
     * @param view
     * @param firstVisibleItem 当前能看见的第一个列表项ID（从0开始）
     * @param visibleItemCount 当前能看见的列表项个数（小半个也算）
     * @param totalItemCount   列表项共数
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        // send to user's listener
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    /**
     * 设置事件监听器
     *
     * @param l
     */
    public void setIXScrollViewListener(IXScrollViewListener l) {
        mScorllViewListener = l;
    }

    /**
     * you can listen ListView.OnScrollListener or this one. it will invoke
     * onXScrolling when header/footer scroll back.
     */
    public interface OnXScrollListener extends OnScrollListener {
        public void onXScrolling(View view);
    }

    /**
     * implements this interface to get refresh/load more event.
     */
    public interface IXScrollViewListener {
        public void onRefresh();

        public void onLoadMore();
    }


    public void setRefreshTime() {

    }
}