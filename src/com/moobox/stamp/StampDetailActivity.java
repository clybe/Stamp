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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.moobox.stamp.utils.FileUtils;
import com.moobox.stamp.utils.L;

public class StampDetailActivity extends Activity implements OnClickListener {

	private ImageView mImageStamp;
	private ProgressBar mImageProgress;

	private DownloadTask mDownloadTask;
	private boolean mIsDownloading = true;
	private int mProgress = 0;// 0-100
	private String downloadurl = "";

	private static final int Connection_Timeout = 10000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stamp_detail);

		mImageStamp = (ImageView) findViewById(R.id.image_stamp);
		mImageProgress = (ProgressBar) findViewById(R.id.image_progress);

		mDownloadTask = new DownloadTask();
		mDownloadTask.execute("http://www.zhuliang.name/photo/bianhao/N7.jpg");

		mImageStamp.setOnClickListener(this);

		// <key>000编号</key>
		// <string>纪2</string>
		// <key>001版别</key>
		// <string>雕刻版(无背胶)</string>
		// <key>002名称</key>
		// <string>中国人民政治协商会议纪念</string>
		// <key>003全套枚数</key>
		// <string>4</string>
		// <key>004发行日期</key>
		// <string>1950-2-1(原版) 1955-1-10(再版)</string>
		// <key>005全套面值</key>
		// <string>950圆(旧币)</string>
		// <key>006全套售价</key>
		// <string>950圆(旧币)</string>
		// <key>007发行机构</key>
		// <string>华北邮政管理总局(原版)邮电部(再版)</string>
		// <key>008印制机构</key>
		// <string>原版：(1)(2)大东书局上海印刷厂(3)(4)北京中国人民印刷厂 再版：北京人民印刷厂</string>
		// <key>009雕刻者</key>
		// <string>贾炳昆、高品璋、贾志谦、孙鸿年、刘国桐</string>
		// <key>010设计者</key>
		// <string>张仃、钟灵</string>
		// <key>011整版枚数</key>
		// <string>(4-1)(4-2)50(10×5)(4-3)(4-4)50(5×10)</string>
		// <key>012备注</key>
		// <string>下面的发行量为原版票发行量，再版票发行量不详。再版票的旧票非常罕见。</string>
		// <key>100图片</key>
		// <string>photo/laoji/c002.jpg</string>

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String fileName = extras.getString("file_name");
			L.e("fileName:" + fileName);
			try {
				InputStream inputStream = getAssets().open(fileName);
				String allData = FileUtils.InputStreamToString(inputStream);
				L.e(allData);
				JSONObject jsonObject = new JSONObject(allData);
				JSONArray stampArray = jsonObject.getJSONArray("stamps");
				ArrayList<String> sortKeys = new ArrayList<String>();
				for (int i = 0; i < stampArray.length(); i++) {
					L.e(i + "-----------------------------");
					sortKeys.clear();
					JSONObject jsObject = stampArray.getJSONObject(i);
					for (Iterator<String> keys = jsObject.keys(); keys
							.hasNext();) {
						sortKeys.add(keys.next());
					}
					Collections.sort(sortKeys);

					for (String key : sortKeys) {
						Object object = jsObject.get(key);
						if (object instanceof String) {
							L.e(key + ":" + (String) object);
						}
					}
				}
			} catch (Exception e) {
				L.e("exception:" + e.getMessage());
			}

		}

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

		default:
			break;
		}

	}

}
