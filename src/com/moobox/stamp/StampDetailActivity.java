package com.moobox.stamp;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moobox.stamp.utils.FileUtils;
import com.moobox.stamp.utils.L;

public class StampDetailActivity extends Activity implements OnClickListener {

	private TextView mTextTitle;
	private ImageView mImageStamp;
	private ProgressBar mImageProgress;

	private DownloadTask mDownloadTask;
	private boolean mIsDownloading = true;
	private int mProgress = 0;// 0-100
	private LinearLayout mRootContent1;
	private LinearLayout mRootContent2;

	private static final int Connection_Timeout = 10000;

	private int color;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stamp_detail);

		findViewById(R.id.btn_back).setOnClickListener(this);
		mTextTitle = (TextView) findViewById(R.id.text_title);
		mImageStamp = (ImageView) findViewById(R.id.image_stamp);
		mImageProgress = (ProgressBar) findViewById(R.id.image_progress);
		mRootContent1 = (LinearLayout) findViewById(R.id.root_content_1);
		mRootContent2 = (LinearLayout) findViewById(R.id.root_content_2);
		color= Color.parseColor("#523B0C");
		mImageStamp.setOnClickListener(this);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String data = extras.getString("data");
			String title = extras.getString("title");
			mTextTitle.setText(title);

			try {
				JSONObject jsObject = new JSONObject(data);
				ArrayList<String> sortKeys = new ArrayList<String>();
				for (@SuppressWarnings("unchecked")
				Iterator<String> keys = jsObject.keys(); keys.hasNext();) {
					sortKeys.add(keys.next());
				}
				Collections.sort(sortKeys);

				for (String key : sortKeys) {
					Object object = jsObject.get(key);
					if (object instanceof String) {
						L.e(key + ":" + (String) object);
						if (!key.equals("100图片")) {
							addContent(key.substring(3), (String) object);
						}
					} else if (object instanceof JSONArray) {
						ArrayList<String> detailSortKeys = new ArrayList<String>();
						JSONArray jsArray = (JSONArray) object;
						if (jsArray.length() > 0) {
							JSONObject jsObj = jsArray.getJSONObject(0);
							for (@SuppressWarnings("unchecked")
							Iterator<String> keys = jsObj.keys(); keys
									.hasNext();) {
								detailSortKeys.add(keys.next());
							}
							Collections.sort(detailSortKeys);
							addContent(null, detailSortKeys);

							for (int i = 0; i < jsArray.length(); i++) {
								addContent(jsArray.getJSONObject(i),
										detailSortKeys);
							}
						}
					}
				}

				mDownloadTask = new DownloadTask();
				mDownloadTask.execute("http://www.zhuliang.name/"
						+ jsObject.optString("100图片"));

			} catch (Exception e) {
				L.e("exception:" + e.getMessage());
			}
		}
	}

	private void addContent(String key, String value) {
		View view = LayoutInflater.from(this).inflate(
				R.layout.list_item_detail_1, mRootContent1, false);
		TextView textTitle = (TextView) view.findViewById(R.id.text_title);
		TextView textContent = (TextView) view.findViewById(R.id.text_content);
		textTitle.setText(key);
		textContent.setText(value);
		mRootContent1.addView(view);
	}

	private void addContent(JSONObject jsonObject, ArrayList<String> titles) {
		View view = LayoutInflater.from(this).inflate(
				R.layout.list_item_detail_2, mRootContent2, false);
		TextView text1 = (TextView) view.findViewById(R.id.text_1);
		TextView text2 = (TextView) view.findViewById(R.id.text_2);
		TextView text3 = (TextView) view.findViewById(R.id.text_3);
		TextView text4 = (TextView) view.findViewById(R.id.text_4);
		TextView text5 = (TextView) view.findViewById(R.id.text_5);
		TextView text6 = (TextView) view.findViewById(R.id.text_6);

		try {
			if (jsonObject == null) {
				text1.setTypeface(null, Typeface.BOLD);
				text2.setTypeface(null, Typeface.BOLD);
				text3.setTypeface(null, Typeface.BOLD);
				text4.setTypeface(null, Typeface.BOLD);
				text5.setTypeface(null, Typeface.BOLD);
				text6.setTypeface(null, Typeface.BOLD);
				text1.setTextColor(color);
				text2.setTextColor(color);
				text3.setTextColor(color);
				text4.setTextColor(color);
				text5.setTextColor(color);
				text6.setTextColor(color);
				text1.setText(titles.get(0).substring(1));
				text2.setText(titles.get(1).substring(1));
				text3.setText(titles.get(2).substring(1));
				text4.setText(titles.get(3).substring(1));
				text5.setText(titles.get(4).substring(1));
				text6.setText(titles.get(5).substring(1));

			} else {
				text1.setText(jsonObject.optString(titles.get(0)));
				text2.setText(jsonObject.optString(titles.get(1)));
				text3.setText(jsonObject.optString(titles.get(2)));
				text4.setText(jsonObject.optString(titles.get(3)));
				text5.setText(jsonObject.optString(titles.get(4)));
				text6.setText(jsonObject.optString(titles.get(5)));
			}

		} catch (Exception e) {
		}
		mRootContent2.addView(view);
	}

	private class DownloadTask extends AsyncTask<String, Integer, String> {

		private int contentLength = 0;
		private HttpURLConnection con;
		private InputStream is = null;
		private Bitmap mBitmap;

		@Override
		protected void onPreExecute() {
			mIsDownloading = true;
		}

		@Override
		protected String doInBackground(String... params) {
			URL url;
			try {
				L.e("download url is " + params[0]);
				url = new URL(params[0]);
				con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				con.setConnectTimeout(Connection_Timeout);
				con.setReadTimeout(Connection_Timeout);

				InputStream is = con.getInputStream();
				int responseCode = con.getResponseCode();
				L.e("responseCode is " + responseCode);

				contentLength = con.getContentLength();

				ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len;
				int sum = 0;

				while ((len = is.read(buffer)) > 0) {
					bytestream.write(buffer, 0, len);
					sum += len;
					publishProgress((sum * 100) / contentLength);
				}

				byte imgdata[] = bytestream.toByteArray();

				bytestream.close();
				File file = FileUtils.getPicFile();
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
						new FileOutputStream(file, true), 8 * 1024);
				bufferedOutputStream.write(imgdata);

				bufferedOutputStream.close();

				mBitmap = BitmapFactory.decodeByteArray(imgdata, 0,
						imgdata.length);
				imgdata = null;

				L.e("responseCode is " + responseCode);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (ProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			mProgress = values[0];
			mImageProgress.setProgress(mProgress);
		}

		@Override
		protected void onPostExecute(String res) {
			mImageStamp.setImageBitmap(mBitmap);
			mImageProgress.setVisibility(View.GONE);
			mIsDownloading = false;
		}

		@Override
		protected void onCancelled() {
			try {

				if (con != null) {
					con.disconnect();
					con = null;
				}
				if (is != null) {
					is.close();
				}

			} catch (Exception e) {
				L.e("io close exception " + e.getMessage());
			}

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.image_stamp:
			if (!mIsDownloading) {
				startActivity(new Intent(StampDetailActivity.this,
						TouchImageViewActivity.class));
			}
			break;
		case R.id.btn_back:
			finish();
			break;

		default:
			break;
		}

	}

}
