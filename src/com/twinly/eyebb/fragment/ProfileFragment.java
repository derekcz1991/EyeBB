package com.twinly.eyebb.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.eyebb.R;
import com.twinly.eyebb.activity.FeedbackDialog;
import com.twinly.eyebb.activity.LancherActivity;
import com.twinly.eyebb.activity.SettingsActivity;
import com.twinly.eyebb.activity.WebViewActivity;
import com.twinly.eyebb.adapter.NotificationsListViewAdapter;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.database.DBNotifications;
import com.twinly.eyebb.model.Notifications;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class ProfileFragment extends Fragment {

	private TextView settingBtn;
	private View notificationDetailsBtn;
	private ArrayList<Notifications> list;
	private ListView listView;
	private NotificationsListViewAdapter adapter;
	//没用的东西
	int child;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_profile, container, false);
		listView = (ListView) v.findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(), WebViewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("from",
						ActivityConstants.FRAGMENT_PROFILE);
				bundle.putSerializable("notifications",
						(Notifications) adapter.getItem(position));
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		/**
		 * FORMAT nickname(username)
		 */
		((TextView) v.findViewById(R.id.username)).setText(SharePrefsUtils
				.getLoginAccount(getActivity()));

		settingBtn = (TextView) v.findViewById(R.id.options_btn);
		settingBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getActivity(),
						SettingsActivity.class);
				startActivityForResult(intent,
						ActivityConstants.REQUEST_GO_TO_SETTING_ACTIVITY);

			}
		});

		notificationDetailsBtn = v.findViewById(R.id.notification_details_btn);

		notificationDetailsBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(getActivity(), FeedbackDialog.class);

				startActivity(intent);
			}
		});
		updateView();
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		new UpdateView().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void updateView() {
		list = DBNotifications.getNotifications(getActivity());
		adapter = new NotificationsListViewAdapter(getActivity(), list);
		listView.setAdapter(adapter);
	}

	private class UpdateView extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String result = HttpRequestUtils.get(HttpConstants.GET_NOTICES,
					null);
			try {
				new JSONObject(result);
			} catch (JSONException e) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
					result = HttpRequestUtils.get(HttpConstants.GET_NOTICES,
							null);
				}
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println("notice = " + result);
			try {
				JSONObject json = new JSONObject(result);
				JSONArray array = json
						.getJSONArray(HttpConstants.JSON_KEY_NOTICES);
				DBNotifications.deleteTable(getActivity());
				for (int i = 0; i < array.length(); i++) {
					JSONObject JSONNotice = array.getJSONObject(i);
					Notifications notice = new Notifications();
					notice.setTitle(JSONNotice
							.getString(HttpConstants.JSON_KEY_NOTICES_TITLE));
					notice.setTitleTc(JSONNotice
							.getString(HttpConstants.JSON_KEY_NOTICES_TITLE_TC));
					notice.setTitleSc(JSONNotice
							.getString(HttpConstants.JSON_KEY_NOTICES_TITLE_SC));
					notice.setUrl(JSONNotice
							.getString(HttpConstants.JSON_KEY_NOTICES_NOTICE));
					notice.setUrlTc(JSONNotice
							.getString(HttpConstants.JSON_KEY_NOTICES_NOTICE_TC));
					notice.setUrlSc(JSONNotice
							.getString(HttpConstants.JSON_KEY_NOTICES_NOTICE_SC));
					notice.setIcon(JSONNotice
							.getString(HttpConstants.JSON_KEY_NOTICES_ICON));
					notice.setDate(JSONNotice
							.getString(HttpConstants.JSON_KEY_NOTICES_VALID_UNTIL));
					DBNotifications.insert(getActivity(), notice);
				}
				updateView();
			} catch (JSONException e) {
				System.out.println(HttpConstants.GET_NOTICES + e.getMessage());
			}
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ActivityConstants.REQUEST_GO_TO_SETTING_ACTIVITY) {
			if (resultCode == ActivityConstants.RESULT_LOGOUT) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), LancherActivity.class);
				startActivity(intent);
				getActivity().finish();
			}
		}
	}
}
