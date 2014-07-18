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
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.eyebb.R;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.constant.HttpConstants;
import com.twinly.eyebb.customview.LoadingDialog;
import com.twinly.eyebb.model.Kindergarten;
import com.twinly.eyebb.utils.HttpRequestUtils;

public class KindergartenListActivity extends Activity {
	private ListView listView;
	private ArrayList<Kindergarten> kindergartendsList;
	private ArrayList<Map<String, String>> mapList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.text_select_kid_kindergarten));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);

		setContentView(R.layout.activity_kindergarten);

		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent data = new Intent();
				data.putExtra("kindergartenId", kindergartendsList
						.get(position).getId());
				data.putExtra("name", mapList.get(position).get("name"));
				setResult(Constants.RESULT_RESULT_OK, data);
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
					KindergartenListActivity.this,
					getString(R.string.toast_login));
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
					kindergartendsList = new ArrayList<Kindergarten>();
					mapList = new ArrayList<Map<String, String>>();

					JSONArray list = json.getJSONArray("allKindergartensInfo");
					for (int i = 0; i < list.length(); i++) {
						JSONObject object = (JSONObject) list.get(i);
						Kindergarten kindergarten = new Kindergarten();

						kindergarten.setId(object.getInt("kindergartenId"));
						kindergarten.setEnglishName(object.getString("name"));
						kindergarten.setTranditionalName(object
								.getString("nameTc"));
						kindergarten.setSimplifiedName(object
								.getString("nameSc"));
						kindergartendsList.add(kindergarten);

						Map<String, String> map = new HashMap<String, String>();
						Resources resources = getResources();
						Configuration config = resources.getConfiguration();
						if (config.locale.toString().equals("en_GB")
								|| config.locale.toString().equals("en")) {
							map.put("name", kindergarten.getEnglishName());
						} else if (config.locale.toString().equals("zh_TW")
								|| config.locale.toString().equals("zh")) {
							map.put("name", kindergarten.getTranditionalName());
						} else if (config.locale.toString().equals("zh_HK")
								|| config.locale.toString().equals("zh")) {
							map.put("name", kindergarten.getTranditionalName());
						} else if (config.locale.toString().equals("zh_CN")
								|| config.locale.toString().equals("zh")) {
							map.put("name", kindergarten.getTranditionalName());
						}
						mapList.add(map);
					}

					System.out.println(mapList);
					SimpleAdapter adapter = new SimpleAdapter(
							KindergartenListActivity.this, mapList,
							R.layout.list_item_kindergarten,
							new String[] { "name" }, new int[] { R.id.name });
					listView.setAdapter(adapter);
				}
			} catch (JSONException e) {
				System.out.println("get kindergarden list ---->> "
						+ e.getMessage());
			}

		}
	}
}
