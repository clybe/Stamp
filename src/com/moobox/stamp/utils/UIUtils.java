package com.moobox.stamp.utils;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class UIUtils {

	private static final int SECOND_MILLIS = 1000;
	private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
	private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
	private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

	//找临界值可以尝试此方法，一堆if弱爆了
	//whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1)); 
	
	public static String getTimeAgo(long time, Context ctx) {
		if (time < 1000000000000L) {
			// if timestamp given in seconds, convert to millis
			time *= 1000;
		}

		long now = System.currentTimeMillis();
		if (time > now || time <= 0) {
			return "刚刚";
		}

		// TODO: localize
		final long diff = now - time;
		if (diff < MINUTE_MILLIS) {
			return "刚刚";
		} else if (diff < 60 * MINUTE_MILLIS) {
			return diff / MINUTE_MILLIS + " 分钟前";
		} else if (diff < 24 * HOUR_MILLIS) {
			return diff / HOUR_MILLIS + " 小时前";
		} else if (diff < 48 * HOUR_MILLIS) {
			return "昨天";
		} else {
			return diff / DAY_MILLIS + " 天前";
		}
	}

	/**
	 * Populate the given {@link TextView} with the requested text, formatting
	 * through {@link Html#fromHtml(String)} when applicable. Also sets
	 * {@link TextView#setMovementMethod} so inline links are handled.
	 */
	public static void setTextMaybeHtml(TextView view, String text) {
		if (TextUtils.isEmpty(text)) {
			view.setText("");
			return;
		}
		if (text.contains("<") && text.contains(">")) {
			view.setText(Html.fromHtml(text));
			view.setMovementMethod(LinkMovementMethod.getInstance());
		} else {
			view.setText(text);
		}
	}

}
