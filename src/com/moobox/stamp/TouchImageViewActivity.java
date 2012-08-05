package com.moobox.stamp;

import java.io.IOException;

import com.moobox.stamp.utils.FileUtils;
import com.moobox.stamp.utils.L;
import com.moobox.stamp.widgets.TouchImageView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

/**
 * zoom in/out image
 * 
 * @author clybe
 * 
 */
public class TouchImageViewActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TouchImageView img = new TouchImageView(this);
		Bitmap snoop;
		try {
			snoop = BitmapFactory.decodeFile(FileUtils.getPicFile()
					.getAbsolutePath());
			img.setImageBitmap(snoop);
			img.setMaxZoom(4f);
			setContentView(img);
		} catch (IOException e) {
			L.e(e.getMessage());
		}
	}

}
