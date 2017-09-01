package com.taovo.rjp.stretchscrollview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * @author Gimpo create on 2017/9/1 10:54
 * @email : jimbo922@163.com
 */

public class StretchContainer extends FrameLayout {

    /**
     * 弹性系数  越小说明这个view越难被拉动
     */
    private float ratio;
    /**
     * view作出反应最小滑动距离
     */
    private int mTouchSlop;
    /**
     * 这个scroller可以让滑动更加的顺滑 不是很突兀的那种  写法固定
     */
    private Scroller mScroller;
    /**
     * 允许最大的下拉距离
     */
    private float maxY = 600;
    /**
     * 包裹的滚动view 可以是所有的view 这个view最好能滚动
     */
    private StretchScrollView mScrollView;
    /**
     * 最大滑动距离
     */
    private float mLastY;
    /**
     * 当前view被拖拽
     */
    private boolean mIsDraging;
    /**
     * 回弹时间
     */
    private int duration = 500;

    public StretchContainer(@NonNull Context context) {
        super(context);
        initView(context, null);
    }

    public StretchContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public StretchContainer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public void initView(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StretchContainer);
            ratio = a.getFloat(R.styleable.StretchContainer_ratio, 1.0f);
        }
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        DecelerateInterpolator interpolator = new DecelerateInterpolator();
        mScroller = new Scroller(context, interpolator);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mScrollView = (StretchScrollView) getChildAt(0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float currentY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = currentY;
                break;
            case MotionEvent.ACTION_MOVE:
                float distanceY = currentY - mLastY;
                if (mIsDraging) {
                    if (distanceY <= 0) {
                        if(mScrollView.isScrolledToTop()) {
                            scrollTo(0, 0);
                            mIsDraging = false;
                            return super.dispatchTouchEvent(ev);
                        }
                    }else{
                        if(mScrollView.isScrolledToBottom()){
                            scrollTo(0, 0);
                            mIsDraging = false;
                            return super.dispatchTouchEvent(ev);
                        }
                    }
                    scrollTo(0, (int) (-distanceY * ratio));
                    return true;
                } else {
                    if (Math.abs(distanceY) > mTouchSlop) {
                        if (distanceY > 0) {  // 向下
                            if (mScrollView.isScrolledToTop()) {
                                mLastY = currentY;
                                mIsDraging = true;
                            }
                        }else{
                            if(mScrollView.isScrolledToBottom()){
                                mLastY = currentY;
                                mIsDraging = true;
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsDraging) {
                    int scrollY = getScrollY();
                    mScroller.startScroll(0, scrollY, 0, -scrollY, duration);
                    mIsDraging = false;
                    invalidate();
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        //判断是否还在滚动，还在滚动为true
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            //更新界面
            postInvalidate();
        }
        super.computeScroll();
    }
}
