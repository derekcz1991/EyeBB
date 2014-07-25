package com.twinly.eyebb.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.eyebb.R;
import com.twinly.eyebb.activity.BeepDialog;
import com.twinly.eyebb.activity.ChildrenListActivity;
import com.twinly.eyebb.activity.SchoolBusTrackingActivity;
import com.twinly.eyebb.utils.CommonUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class IndoorLocatorFragment extends Fragment {
	private ListView listView;

	//public static IndoorLocatorFragment newInstance(Hash)
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_indoor_locator, container,
				false);
		listView = (ListView) v.findViewById(R.id.listView);
		setUpListener(v);
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (SharePrefsUtils.isUpdateIndoorLocator(getActivity())) {
			new UpdateView().execute();
		}
	}

	class UpdateView extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

	}

	private void setUpListener(View v) {
		v.findViewById(R.id.btn_beepall).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (CommonUtils.isFastDoubleClick()) {
							return;
						} else {
							Intent intent = new Intent(getActivity(),
									BeepDialog.class);
							startActivity(intent);
						}

					}
				});

		v.findViewById(R.id.btn_shcool_bus).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								SchoolBusTrackingActivity.class);
						startActivity(intent);

					}
				});

		v.findViewById(R.id.btn_kidslist).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								ChildrenListActivity.class);
						//startActivity(intent);
					}
				});
	}

}
