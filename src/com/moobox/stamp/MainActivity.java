package com.moobox.stamp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.codehaus.jackson.map.ObjectMapper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.moobox.stamp.adapter.CatalogAdapter;
import com.moobox.stamp.data.Catalog;
import com.moobox.stamp.data.CatalogList;
import com.moobox.stamp.utils.FileUtils;
import com.moobox.stamp.utils.L;

public class MainActivity extends Activity implements OnItemClickListener {

	private ArrayList<Catalog> mDataset;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {
			InputStream allInputStream = getAssets().open("all.json");
			String allData = FileUtils.InputStreamToString(allInputStream);
			ObjectMapper mapper = new ObjectMapper();
			CatalogList list = mapper.readValue(allData, CatalogList.class);
			for (Catalog catalog : list.list) {
				L.e(catalog.name + ":" + catalog.label);
			}
			mDataset = list.list;
			CatalogAdapter adapter = new CatalogAdapter(mDataset);
			ListView listView = (ListView) findViewById(R.id.list_view);
			listView.setAdapter(adapter);

			listView.setOnItemClickListener(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(MainActivity.this,
				SecondCatalogActivity.class);
		intent.putExtra("file_name", mDataset.get(arg2).label + ".json");
		startActivity(intent);

	}

}
