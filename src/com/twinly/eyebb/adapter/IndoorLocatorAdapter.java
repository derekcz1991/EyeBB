package com.twinly.eyebb.adapter;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twinly.eyebb.customview.CircleImageView;
import com.twinly.eyebb.model.Child;

public class IndoorLocatorAdapter extends BaseAdapter {
	private Map<String, ArrayList<String>> data;
	private Map<String, Child> childMap;

	public final class ViewHolder {
		private CircleImageView avatar;
		public TextView icon;
		public TextView areaName;
		public TextView childrenNum;
		public ViewGroup avatarContainer;
	}

	public IndoorLocatorAdapter(Context context,
			Map<String, ArrayList<String>> data, Map<String, Child> childMap) {
		this.data = data;
		this.childMap = childMap;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		return null;
	}

}
