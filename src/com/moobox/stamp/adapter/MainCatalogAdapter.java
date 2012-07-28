package com.moobox.stamp.adapter;

import java.util.ArrayList;

import com.moobox.stamp.R;
import com.moobox.stamp.data.Catalog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MainCatalogAdapter extends BaseAdapter {

	private ArrayList<Catalog> mDataset;
	private LayoutInflater mInflater;

	public MainCatalogAdapter(ArrayList<Catalog> dataset) {
		mDataset = dataset;
	}

	@Override
	public int getCount() {
		return mDataset == null ? 0 : mDataset.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textCatalog;
		if (convertView == null) {
			if (mInflater == null) {
				mInflater = LayoutInflater.from(parent.getContext());
			}
			textCatalog = (TextView) mInflater.inflate(R.layout.list_item_catalog_main, parent, false);
		} else {
			textCatalog = (TextView) convertView;
		}
		textCatalog.setText(mDataset.get(position).name);
		return textCatalog;
	}
}
