package com.moobox.stamp;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import com.moobox.stamp.adapter.CatalogAdapter;
import com.moobox.stamp.adapter.StampAdapter;
import com.moobox.stamp.data.Catalog;
import com.moobox.stamp.data.CatalogList;
import com.moobox.stamp.data.Stamp;
import com.moobox.stamp.utils.FileUtils;
import com.moobox.stamp.utils.L;
import com.moobox.stamp.widgets.FlyinAppMenu;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;

public class FlyinAppMenuActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	private FlyinAppMenu mFlyinAppMenu;
	private ArrayList<Catalog> mCatalogDataset;

	private ArrayList<Stamp> mAllStampDataset;
	private ArrayList<Stamp> mStampDataset;

	private StampAdapter mStampAdapter;
	private LoadingTask mLoadingTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.flyin_app_menu);
		findViewById(R.id.btnSwitch).setOnClickListener(this);
		mFlyinAppMenu = (FlyinAppMenu) findViewById(R.id.root_view);

		mAllStampDataset = new ArrayList<Stamp>();
		mStampDataset = new ArrayList<Stamp>();

		try {
			InputStream allInputStream = getAssets().open("all.json");
			String allData = FileUtils.InputStreamToString(allInputStream);
			ObjectMapper mapper = new ObjectMapper();
			CatalogList list = mapper.readValue(allData, CatalogList.class);

			// long startTime = System.currentTimeMillis();
			// for (Catalog catalog : list.list) {
			// L.e(catalog.name + ":" + catalog.label + "cost:"
			// + parsonSimpleJson(catalog.label + ".json"));
			// }
			// L.e("cost " + (System.currentTimeMillis() - startTime) + "ms");
			mCatalogDataset = list.list;
			CatalogAdapter adapter = new CatalogAdapter(mCatalogDataset);
			ListView flyinView = (ListView) findViewById(R.id.flyin_view);

			flyinView.setAdapter(adapter);
			flyinView.setOnItemClickListener(this);

		} catch (IOException e) {
			e.printStackTrace();
		}

		mStampAdapter = new StampAdapter(mAllStampDataset);
		ListView listView = (ListView) findViewById(R.id.list_view);
		listView.setAdapter(mStampAdapter);
		// listView.setOnItemClickListener(this);
		
		mLoadingTask = new LoadingTask();
		mLoadingTask.execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSwitch:
			mFlyinAppMenu.changeState(100);
			break;

		default:
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(FlyinAppMenuActivity.this,
				StampDetailActivity.class);
		intent.putExtra("file_name", mCatalogDataset.get(arg2).label + ".json");
		startActivity(intent);
	}

	private void parsonJson(String fileName) {
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
				for (Iterator<String> keys = jsObject.keys(); keys.hasNext();) {
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

	private ArrayList<Stamp> parsonSimpleJson(String fileName) {
		long start = System.currentTimeMillis();
		ArrayList<Stamp> stamps = new ArrayList<Stamp>();

		try {
			InputStream inputStream = getAssets().open(fileName);

			String allData = FileUtils.InputStreamToByte(inputStream);
			start = System.currentTimeMillis() - start;
			JSONObject jsonObject = new JSONObject(allData);
			JSONArray stampArray = jsonObject.getJSONArray("stamps");
			for (int i = 0; i < stampArray.length(); i++) {
				// L.e(i + "-----------------------------");
				JSONObject jsObject = stampArray.getJSONObject(i);
				// L.e("name:" + jsObject.optString("002名称"));
				Stamp stamp = new Stamp();
				stamp.name = jsObject.optString("002名称");
				stamp.identifier = jsObject.optString("000编号");
				stamp.publishDate = jsObject.optString("004发行日期");
				stamp.jsonObject = jsObject;
				stamps.add(stamp);
			}
			inputStream.close();
		} catch (Exception e) {
			L.e("exception:" + e.getMessage());
		} finally {
			L.e("parse cost " + start + "ms");
			return stamps;
		}
	}

	private class LoadingTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			mAllStampDataset
					.addAll(parsonSimpleJson(mCatalogDataset.get(0).label
							+ ".json"));

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mStampAdapter.setDataset(mAllStampDataset);
		}
	}
}
