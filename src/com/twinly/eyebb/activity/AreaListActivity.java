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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.twinly.twinly.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.utils.HttpRequestUtils;
import com.twinly.eyebb.utils.SystemUtils;

/**
 * @author eyebb team
 * 
 * @category AreaListActivity
 * 
 *           this activity is used when you fill in the child`s information
 *           (during the sign-up time). you should finish 3 parts. The first is
 *           child`s name. The second is child`s birthday. The third is child`s
 *           kindergarten.
 * 
 */
public class AreaListActivity extends Activity {
	public static final String EXTRA_AREA_ID = "AREA_ID";
	public static final String EXTRA_AREA_TYPE = "AREA_TYPE";
	public static final String EXTRA_AREA_DISPLAY_NAME = "AREA_DISPLAY_NAME";

	private ListView listView;
	private ArrayList<Map<String, String>> mapList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.text_select_kid_kindergarten));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);
		
		setContentView(R.layout.activity_kindergarten);
		
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent data = new Intent();
				Map<String, String> map = mapList.get(position);
				
				data.putExtra(EXTRA_AREA_ID,
						Integer.parseInt(map.get(EXTRA_AREA_ID)));
				data.putExtra(EXTRA_AREA_TYPE, map.get(EXTRA_AREA_TYPE));
				data.putExtra(EXTRA_AREA_DISPLAY_NAME,
						map.get(EXTRA_AREA_DISPLAY_NAME));
				setResult(ActivityConstants.RESULT_RESULT_OK, data);
				finish();
			}
		});
		new GetAreaList().execute();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class GetAreaList extends AsyncTask<Void, Void, String> {
		Dialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = LoadingDialog.createLoadingDialog(AreaListActivity.this,
					getString(R.string.text_loading));
			dialog.show();
		}
		
		@Override
		protected String doInBackground(Void... params) {
			dialog.dismiss();
			return HttpRequestUtils.get(HttpConstants.GET_AREA_LIST, null);
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
						map.put(EXTRA_AREA_ID, object
								.getString(HttpConstants.JSON_KEY_AREAS_ID));
						map.put(EXTRA_AREA_TYPE, object
								.getString(HttpConstants.JSON_KEY_AREAS_TYPE));
						int locale = SystemUtils
								.getLocale(AreaListActivity.this);
						switch (locale) {
						case Constants.LOCALE_CN:
							map.put(EXTRA_AREA_DISPLAY_NAME,
									object.getString(HttpConstants.JSON_KEY_KINDERGARTEN_NAME_SC));
							break;
						case Constants.LOCALE_TW:
							map.put(EXTRA_AREA_DISPLAY_NAME,
									object.getString(HttpConstants.JSON_KEY_KINDERGARTEN_NAME_TC));
							break;
						case Constants.LOCALE_HK:
							map.put(EXTRA_AREA_DISPLAY_NAME,
									object.getString(HttpConstants.JSON_KEY_KINDERGARTEN_NAME_TC));
							break;
						default:
							map.put(EXTRA_AREA_DISPLAY_NAME,
									object.getString(HttpConstants.JSON_KEY_KINDERGARTEN_NAME_EN));
							break;
						}
						mapList.add(map);
					}

					SimpleAdapter adapter = new SimpleAdapter(
							AreaListActivity.this, mapList,
							R.layout.list_item_kindergarten,
							new String[] { EXTRA_AREA_DISPLAY_NAME },
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
