package com.moobox.stamp.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.moobox.stamp.utils.L;

public class CustomViewGroup extends ViewGroup {

	public CustomViewGroup(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CustomViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			return true;
		case MotionEvent.ACTION_MOVE:
			return true;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			break;

		default:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			L.e("group action down");
			return true;
		case MotionEvent.ACTION_MOVE:
			L.e("group action move");
			return false;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			L.e("group action up");
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

	}

}
