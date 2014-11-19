package com.twinly.eyebb.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.eyebb.R;
import com.twinly.eyebb.activity.KindergartenListActivity.GetKindergartenList;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.BleDeviceConstants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SystemUtils;

public class MatchingDeviceActivity extends Activity {
	private ListView listView;
	private ArrayList<Map<String, String>> mapList;
	
	// sharedPreferences
	private SharedPreferences SandVpreferences;
	private SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_matching_device);
		setTitle(getString(R.string.text_matching_device));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);
		
		// sharedPreferences for signup
		SandVpreferences = getSharedPreferences("signup", MODE_PRIVATE);
		editor = SandVpreferences.edit();
		
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent data = new Intent();
				Map<String, String> map = mapList.get(position);

				data.putExtra("kindergartenId",
						Integer.parseInt(map.get("kindergartenId")));
				data.putExtra("nameEn", map.get("nameEn"));
				data.putExtra("nameTc", map.get("nameTc"));
				data.putExtra("nameSc", map.get("nameSc"));
				data.putExtra("displayName", map.get("displayName"));
				setResult(ActivityConstants.RESULT_RESULT_OK, data);
				
				editor.putInt("kindergartenId", Integer.parseInt(map.get("kindergartenId")));
				editor.putString("nameEns",  map.get("nameEn"));
				System.out.println("kindergartenId" + Integer.parseInt(map.get("kindergartenId").toString()));
				editor.commit();
				
				Intent intent = new Intent(MatchingDeviceActivity.this,MatchingVerificationActivity.class);
				startActivity(intent);
				
				finish();
			}
		});
		new GetKindergartenList().execute();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	class GetKindergartenList extends AsyncTask<Void, Void, String> {
		Dialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = LoadingDialog.createLoadingDialog(
					MatchingDeviceActivity.this,
					getString(R.string.toast_signup));
			dialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			dialog.dismiss();
			return HttpRequestUtils.get(HttpConstants.GET_KINDERGARTEN_LIST,
					null);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			System.out.println("result = " + result);
			try {
				JSONObject json = new JSONObject(result);
				int size = json.getInt("size");
				if (size > 0) {
					mapList = new ArrayList<Map<String, String>>();

					JSONArray list = json
							.getJSONArray(HttpConstants.JSON_KEY_AREAS_INFO);
					for (int i = 0; i < list.length(); i++) {
						JSONObject object = (JSONObject) list.get(i);

						Map<String, String> map = new HashMap<String, String>();
						map.put("kindergartenId",
								object.getString(HttpConstants.JSON_KEY_AREAS_id));
						map.put("nameEn",
								object.getString(HttpConstants.JSON_KEY_KINDERGARTEN_NAME_EN));
						map.put("nameTc",
								object.getString(HttpConstants.JSON_KEY_KINDERGARTEN_NAME_TC));
						map.put("nameSc",
								object.getString(HttpConstants.JSON_KEY_KINDERGARTEN_NAME_SC));
						int locale = SystemUtils
								.getLocale(MatchingDeviceActivity.this);
						switch (locale) {
						case BleDeviceConstants.LOCALE_CN:
							map.put("displayName", map.get("nameSc"));
							break;
						case BleDeviceConstants.LOCALE_TW:
							map.put("displayName", map.get("nameTc"));
							break;
						case BleDeviceConstants.LOCALE_HK:
							map.put("displayName", map.get("nameTc"));
							break;
						default:
							map.put("displayName", map.get("nameEn"));
							break;
						}
						mapList.add(map);
					}

					System.out.println(mapList);
					SimpleAdapter adapter = new SimpleAdapter(
							MatchingDeviceActivity.this, mapList,
							R.layout.list_item_kindergarten,
							new String[] { "displayName" },
							new int[] { R.id.name });
					listView.setAdapter(adapter);
				}
			} catch (JSONException e) {
				System.out.println("get kindergarden list ---->> "
						+ e.getMessage());
			}

		}
	}
}
