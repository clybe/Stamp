package com.moobox.stamp.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.moobox.stamp.R;
import com.moobox.stamp.utils.L;

public class FlyinAppMenu extends RelativeLayout {

	private final int ANIMATION_TIME = 400;

	private View mMainView;
	private View mFlyinView;

	private boolean mIsOpen = false;

	private Scroller mScroller;
	private int mTouchWidth;

	private VelocityTracker mVelocityTracker;
	private float mCurrentX;
	private int mOffset;
	private boolean mReceiveAction = false;

	private boolean mEnableClick = true;

	public FlyinAppMenu(Context context) {
		super(context, null);
	}

	public FlyinAppMenu(Context context, AttributeSet attrs) {
		super(context, attrs);

		mScroller = new Scroller(getContext(), new DecelerateInterpolator());
		mTouchWidth = (int) (context.getResources().getDisplayMetrics().density * 48);
		mOffset = (int) (context.getResources().getDisplayMetrics().density * 200);
		L.e("mTouchWidth is " + mTouchWidth);
	}

	public synchronized void setOpen(boolean state) {
		mIsOpen = state;
	}

	public synchronized boolean isOpen() {
		return mIsOpen;
	}

	public void changeState(int offset) {
		changeState(offset, isOpen());
	}

	public void changeState(int offset, boolean isOpen) {
		mScroller.abortAnimation();
		requestLayout();
		if (isOpen) {
			scrollClose();
		} else {
			scrollOpen();
		}
		invalidate();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		int action = ev.getAction();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			float currentX = ev.getX();
			float currentY = ev.getY();
			mEnableClick = true;

			if (currentY <= mTouchWidth) {
				mEnableClick = true;
				return super.onInterceptTouchEvent(ev);
			} else {
				mEnableClick = false;
			}

			L.d("currentX:" + currentX + ", scrollerX:"
					+ mMainView.getScrollX());
			if ((currentX > -mMainView.getScrollX()) && currentY > mTouchWidth
					&& currentX < (mTouchWidth - mMainView.getScrollX())
					&& mScroller.isFinished()) {
				mCurrentX = currentX;
				mReceiveAction = true;
				L.e("start scroll by position");
				mEnableClick = false;
				return false;
			} else {
				mEnableClick = true;
				mReceiveAction = false;
			}
			break;

		default:
			break;
		}

		if (mEnableClick) {
			return super.onTouchEvent(ev);
		} else {
			return true;
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		mMainView = findViewById(R.id.main_view);
		mFlyinView = findViewById(R.id.flyin_view);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		int action = ev.getAction();
		float currentX = ev.getX();
		float currentY = ev.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			L.e("onDown");

			if (mEnableClick) {
				return super.onTouchEvent(ev);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			L.e("onMove");
			if (mEnableClick) {
				return super.onTouchEvent(ev);
			}

			if (!mReceiveAction) {
				return false;
			}

			int deltaX = (int) (mCurrentX - currentX);
			mCurrentX = currentX;

			int leftValue = -mOffset - mMainView.getScrollX();
			int rightValue = -mMainView.getScrollX();

			if (deltaX > rightValue) {
				deltaX = rightValue;
			} else if (deltaX < leftValue) {
				deltaX = leftValue;
			}

			mMainView.scrollBy(deltaX, 0);

			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:

			L.e("onCacnel");
			if (mEnableClick || !mReceiveAction) {
				return super.onTouchEvent(ev);
			}

			// mReceiveAction = false;

			VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			int velocityX = (int) velocityTracker.getXVelocity();
			L.e("velocityX:" + velocityX + ",CurrentX:"
					+ mMainView.getScrollX());

			if (velocityX > 0) {
				scrollOpen();
			} else {
				scrollClose();
			}

			invalidate();
			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}

		default:
			break;
		}

		if (mEnableClick) {
			return super.onTouchEvent(ev);
		} else {
			return true;
		}
	}

	private void scrollOpen() {
		mScroller.startScroll(mMainView.getScrollX(), 0,
				(-mOffset - mMainView.getScrollX()), 0, ANIMATION_TIME);
		mIsOpen = true;
	}

	private void scrollClose() {
		mScroller.startScroll(mMainView.getScrollX(), 0,
				(-mMainView.getScrollX()), 0, ANIMATION_TIME);
		mIsOpen = false;
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mMainView == null) {
				mMainView = findViewById(R.id.main_view);
			}
			mMainView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			invalidate();
			requestLayout();
		}
	}
}
