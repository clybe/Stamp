package com.moobox.stamp;

import java.lang.reflect.Method;

import com.moobox.stamp.utils.L;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends Activity {

	private static final int DELAY = 1200;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == 1) {

					Intent intent = new Intent(SplashActivity.this,
							FlyinAppMenuActivity.class);
					startActivity(intent);

					try {
						Method m = Activity.class.getDeclaredMethod(
								"overridePendingTransition", Integer.TYPE,
								Integer.TYPE);
						m.invoke(SplashActivity.this,
								R.anim.splash_screen_fade,
								R.anim.splash_screen_hold);
					} catch (Exception e) {
						L.e("activity animation error " + e.getMessage());
					}

					SplashActivity.this.finish();
				}

			}

		}.sendEmptyMessageDelayed(1, DELAY);
	}
}
