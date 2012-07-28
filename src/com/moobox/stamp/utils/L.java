package com.moobox.stamp.utils;

import com.moobox.stamp.BuildConfig;

import android.util.Log;

public class L {

	public final static String TAG = "GoogleIO";

	public static void e(String msg) {
		if (BuildConfig.DEBUG) {
			Log.e(TAG, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (BuildConfig.DEBUG) {
			Log.e(tag, msg);
		}
	}

	public static void d(String msg) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (BuildConfig.DEBUG) {
			Log.d(tag, msg);
		}
	}
}