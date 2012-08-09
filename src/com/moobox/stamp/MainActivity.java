package com.moobox.stamp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.moobox.stamp.adapter.CatalogAdapter;
import com.moobox.stamp.adapter.StampAdapter;
import com.moobox.stamp.data.Catalog;
import com.moobox.stamp.data.CatalogList;
import com.moobox.stamp.data.Stamp;
import com.moobox.stamp.utils.FileUtils;
import com.moobox.stamp.utils.L;
import com.moobox.stamp.utils.UIUtils;
import com.moobox.stamp.widgets.FlyinAppMenu;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends BaseActivity implements OnClickListener {

	private FlyinAppMenu mFlyinAppMenu;
	private ArrayList<Catalog> mCatalogDataset;

	private ArrayList<Stamp> mAllStampDataset;
	private ArrayList<Stamp> mStampDataset;
	private ArrayList<Stamp> mSearchDataset;

	private CatalogAdapter mCatalogAdapter;
	private StampAdapter mStampAdapter;
	private InitTask mInitTask;
	private LoadingTask mLoadingTask;

	private ListView mCatalogListView;
	private TextView mTextTitle;
	private View mImageLogo;
	private EditText mEditSearct;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.flyin_app_menu);
		findViewById(R.id.btn_switch).setOnClickListener(this);
		findViewById(R.id.title_view).setOnClickListener(this);
		findViewById(R.id.btn_search).setOnClickListener(this);
		findViewById(R.id.btn_about).setOnClickListener(this);
		mTextTitle = (TextView) findViewById(R.id.text_title);
		mImageLogo = findViewById(R.id.img_logo);
		mEditSearct = (EditText) findViewById(R.id.edit_search);
		mEditSearct.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

		mEditSearct.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					mEditSearct.setText("");
					performSearch();
					return true;
				}
				return false;
			}
		});

		mEditSearct.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s == null || s.length() == 0) {
					if (mCatalogAdapter.getSelected() == 0) {
						mStampAdapter.setDataset(mAllStampDataset);
					} else {
						mStampAdapter.setDataset(mStampDataset);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		mFlyinAppMenu = (FlyinAppMenu) findViewById(R.id.root_view);

		mAllStampDataset = new ArrayList<Stamp>();
		mStampDataset = new ArrayList<Stamp>();
		mSearchDataset = new ArrayList<Stamp>();

		try {
			InputStream allInputStream = getAssets().open("all.json");
			String allData = FileUtils.InputStreamToString(allInputStream);
			ObjectMapper mapper = new ObjectMapper();
			CatalogList list = mapper.readValue(allData, CatalogList.class);

			mCatalogDataset = list.list;
			mCatalogDataset.add(0, new Catalog("全部", "all"));
			mCatalogAdapter = new CatalogAdapter(mCatalogDataset);
			mCatalogListView = (ListView) findViewById(R.id.flyin_view);

			mCatalogListView.setAdapter(mCatalogAdapter);
			mCatalogListView
					.setOnItemClickListener(mCatalogOnItemClickListener);

		} catch (IOException e) {
			e.printStackTrace();
		}

		mStampAdapter = new StampAdapter(mAllStampDataset);
		ListView listView = (ListView) findViewById(R.id.list_view);
		listView.setAdapter(mStampAdapter);
		listView.setOnItemClickListener(mStampOnItemClickListener);

		mInitTask = new InitTask();
		mInitTask.execute();

		// for (int i = 1; i < mCatalogDataset.size(); i++) {
		// saveStamps(
		// parsonSimpleJson(mCatalogDataset.get(i).label + ".json"),
		// mCatalogDataset.get(i).label);
		// L.e(i + " success");
		// }
		// L.e("save finished");

		UmengUpdateAgent.update(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_switch:
			mFlyinAppMenu.changeState();
			break;
		case R.id.btn_search:
			performSearch();
			break;
		case R.id.btn_about:
			startActivity(new Intent(MainActivity.this, AboutActivity.class));
			break;
		default:
			break;
		}
	}

	private void performSearch() {
		String key = mEditSearct.getText().toString();
		mSearchDataset.clear();
		if (!TextUtils.isEmpty(key)) {

			if (mCatalogAdapter.getSelected() == 0) {
				for (Stamp stamp : mAllStampDataset) {
					if (stamp.name.contains(key)) {
						mSearchDataset.add(stamp);
					}
				}
			} else {
				for (Stamp stamp : mStampDataset) {
					if (stamp.name.contains(key)) {
						mSearchDataset.add(stamp);
					}
				}
			}

			if (mSearchDataset.size() > 0) {
				mStampAdapter.setDataset(mSearchDataset);
				Toast.makeText(this, "搜索到" + mSearchDataset.size() + "条结果",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "未找到和\"" + key + "\"匹配的搜索结果",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private OnItemClickListener mCatalogOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			mFlyinAppMenu.changeState();
			mCatalogAdapter.setSelected(arg2);
			mEditSearct.setText("");

			if (arg2 == 0) {
				mImageLogo.setVisibility(View.VISIBLE);
				mTextTitle.setVisibility(View.GONE);
				mStampAdapter.setDataset(mAllStampDataset);
			} else {
				mLoadingTask = new LoadingTask();
				mLoadingTask.execute(mCatalogDataset.get(arg2).label);
				mImageLogo.setVisibility(View.GONE);
				mTextTitle.setVisibility(View.VISIBLE);
				mTextTitle.setText(mCatalogDataset.get(arg2).name);
			}
		}
	};

	private OnItemClickListener mStampOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent intent = new Intent(MainActivity.this,
					StampDetailActivity.class);
			intent.putExtra("data", mStampAdapter.getItem(arg2).data);
			intent.putExtra("title", mStampAdapter.getItem(arg2).name);
			startActivity(intent);
		}
	};

	@SuppressWarnings("finally")
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
				JSONObject jsObject = stampArray.getJSONObject(i);
				Stamp stamp = new Stamp();
				stamp.name = jsObject.optString("002名称");
				stamp.identifier = jsObject.optString("000编号");
				stamp.publishDate = jsObject.optString("004发行日期");
				stamp.data = jsObject.toString();
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

	private class LoadingTask extends
			AsyncTask<String, Integer, ArrayList<Stamp>> {

		@Override
		protected ArrayList<Stamp> doInBackground(String... params) {
			ArrayList<Stamp> stamps = readStamps(params[0]);
			return stamps;
		}

		@Override
		protected void onPostExecute(ArrayList<Stamp> stamps) {
			super.onPostExecute(stamps);
			mStampDataset.clear();
			mStampDataset.addAll(stamps);
			mStampAdapter.setDataset(mStampDataset);
		}
	}

	private class InitTask extends AsyncTask<String, ArrayList<Stamp>, Void> {

		long start;

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(String... params) {
			ArrayList<Stamp> stamps = readStamps(mCatalogDataset.get(1).label);
			publishProgress(stamps);
			for (int i = 2; i < mCatalogDataset.size(); i++) {
				if (!isCancelled()) {
					ArrayList<Stamp> tempStamps = readStamps(mCatalogDataset
							.get(i).label);
					publishProgress(tempStamps);
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(ArrayList<Stamp>[] values) {
			mAllStampDataset.addAll(values[0]);
			if (mCatalogAdapter.getSelected() == 0) {
				mStampAdapter.setDataset(mAllStampDataset);
			}

		};

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			start = System.currentTimeMillis();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			L.e("task finished, cost:" + (System.currentTimeMillis() - start)
					+ "ms");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		UIUtils.closeTask(mInitTask);
		UIUtils.closeTask(mLoadingTask);

		mAllStampDataset.clear();
		mCatalogDataset.clear();
		mStampDataset.clear();
	}

	/**
	 * 将闯关列表缓存在本地
	 */
	private void saveStamps(ArrayList<Stamp> stamps, String fileName) {

		File extFile = Environment.getExternalStorageDirectory();
		File folder = new File(extFile, "clybe");
		if (!folder.exists()) {
			folder.mkdir();
		}

		File file = new File(folder, fileName);

		try {
			if (file.exists()) {
				file.delete();
			}

			file.createNewFile();
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					fileOutputStream);
			objectOutputStream.writeObject(stamps);
			objectOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取序列化的邮票数据
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<Stamp> readStamps(String fileName) {
		ArrayList<Stamp> stamps = null;
		try {
			InputStream inputStream = getAssets().open(fileName);
			ObjectInputStream objectInputStream = new ObjectInputStream(
					inputStream);
			stamps = (ArrayList<Stamp>) objectInputStream.readObject();
			objectInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return stamps;
	}
}
