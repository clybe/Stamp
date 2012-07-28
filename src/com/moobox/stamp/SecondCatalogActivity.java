package com.moobox.stamp;

import java.io.InputStream;

import com.moobox.stamp.utils.FileUtils;
import com.moobox.stamp.utils.L;

import android.app.Activity;
import android.os.Bundle;

public class SecondCatalogActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String fileName = extras.getString("file_name");
			L.e("fileName:" + fileName);
			try {
				InputStream inputStream = getAssets().open(fileName);
				String allData = FileUtils.InputStreamToString(inputStream);
				L.e(allData);
			} catch (Exception e) {
				L.e("exception:" + e.getMessage());
			}

		}

	}

}
