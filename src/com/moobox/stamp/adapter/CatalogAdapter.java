package com.moobox.stamp.adapter;

import java.util.ArrayList;

import com.moobox.stamp.R;
import com.moobox.stamp.data.Catalog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CatalogAdapter extends BaseAdapter {

	private ArrayList<Catalog> mDataset;
	private LayoutInflater mInflater;
	private int mSelected = 0;

	public CatalogAdapter(ArrayList<Catalog> dataset) {
		mDataset = dataset;
	}

	@Override
	public int getCount() {
		return mDataset == null ? 0 : mDataset.size();
	}

	public void setSelected(int selected) {
		this.mSelected = selected;
		notifyDataSetChanged();
	}

	public int getSelected() {
		return mSelected;
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

			convertView = mInflater.inflate(R.layout.list_item_catalog, parent,
					false);
		}

		textCatalog = (TextView) convertView;

		textCatalog.setText(mDataset.get(position).name);
		if (mSelected == position) {
			textCatalog.setTextColor(parent.getContext().getResources()
					.getColor(R.color.color_catalog_selected));
			convertView.setBackgroundResource(R.drawable.bg_menu_selected);
		} else {
			textCatalog.setTextColor(parent.getContext().getResources()
					.getColor(R.color.color_catalog));
			convertView.setBackgroundResource(R.drawable.bg_menu);
		}
		return convertView;
	}
}
