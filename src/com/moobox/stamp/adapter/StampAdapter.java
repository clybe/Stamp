package com.moobox.stamp.adapter;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.moobox.stamp.R;
import com.moobox.stamp.data.Stamp;

public class StampAdapter extends BaseAdapter {

	private ArrayList<Stamp> mDataset;
	private LayoutInflater mInflater;

	public StampAdapter(ArrayList<Stamp> dataset) {
		mDataset = dataset;
	}
	
	public void setDataset(ArrayList<Stamp> dataset){
		mDataset = dataset;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mDataset == null ? 0 : mDataset.size();
	}

	@Override
	public Stamp getItem(int position) {
		return mDataset.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			if (mInflater == null) {
				mInflater = LayoutInflater.from(parent.getContext());
			}

			convertView = mInflater.inflate(R.layout.list_item_stamp, parent,
					false);
			holder = new ViewHolder();
			holder.textIdentifier = (TextView) convertView
					.findViewById(R.id.text_identifier);
			holder.textName = (TextView) convertView
					.findViewById(R.id.text_name);
			holder.textPublish = (TextView) convertView
					.findViewById(R.id.text_publish);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.textIdentifier.setText(mDataset.get(position).identifier);
		holder.textName.setText(mDataset.get(position).name);
		holder.textPublish.setText(mDataset.get(position).publishDate);
		return convertView;
	}

	static class ViewHolder {
		TextView textIdentifier;
		TextView textName;
		TextView textPublish;
	}
}
