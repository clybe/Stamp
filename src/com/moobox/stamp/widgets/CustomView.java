package com.moobox.stamp.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.moobox.stamp.utils.L;

public class CustomView extends View {

	public CustomView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			L.e("action down");
			return true;
		case MotionEvent.ACTION_MOVE:
			L.e("action move");
			return false;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			L.e("action up");
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}

}
