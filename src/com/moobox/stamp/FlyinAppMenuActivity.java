package com.moobox.stamp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.codehaus.jackson.map.ObjectMapper;

import com.moobox.stamp.adapter.MainCatalogAdapter;
import com.moobox.stamp.data.Catalog;
import com.moobox.stamp.data.CatalogList;
import com.moobox.stamp.utils.FileUtils;
import com.moobox.stamp.utils.L;
import com.moobox.stamp.widgets.FlyinAppMenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;

public class FlyinAppMenuActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	private FlyinAppMenu mFlyinAppMenu;
	private ArrayList<Catalog> mDataset;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.flyin_app_menu);
		findViewById(R.id.btnSwitch).setOnClickListener(this);
		mFlyinAppMenu = (FlyinAppMenu) findViewById(R.id.root_view);

		try {
			InputStream allInputStream = getAssets().open("all.json");
			String allData = FileUtils.InputStreamToString(allInputStream);
			ObjectMapper mapper = new ObjectMapper();
			CatalogList list = mapper.readValue(allData, CatalogList.class);
			for (Catalog catalog : list.list) {
				L.e(catalog.name + ":" + catalog.label);
			}
			mDataset = list.list;
			MainCatalogAdapter adapter = new MainCatalogAdapter(mDataset);
			ListView flyinView = (ListView) findViewById(R.id.flyin_view);
			ListView listView = (ListView) findViewById(R.id.list_view);
			flyinView.setAdapter(adapter);
			listView.setAdapter(adapter);

			flyinView.setOnItemClickListener(this);
			listView.setOnItemClickListener(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				SecondCatalogActivity.class);
		intent.putExtra("file_name", mDataset.get(arg2).label + ".json");
		startActivity(intent);
	}

}
