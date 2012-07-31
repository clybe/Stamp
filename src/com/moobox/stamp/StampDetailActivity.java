package com.moobox.stamp;

import java.io.IOException;
import java.net.URLConnection;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class StampDetailActivity extends Activity {

	private ImageView mImageStamp;
	private ProgressBar mImageProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stamp_detail);

		mImageStamp = (ImageView) findViewById(R.id.image_stamp);
		mImageProgress = (ProgressBar) findViewById(R.id.image_progress);

	}

	private class ImageDownloader extends AsyncTask<String, Integer, Integer> {

		@Override
		protected Integer doInBackground(String... params) {
			return null;
		}

	}

}
