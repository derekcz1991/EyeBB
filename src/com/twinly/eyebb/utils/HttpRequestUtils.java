package com.twinly.eyebb.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.http.protocol.HTTP;

import android.util.Log;

import com.twinly.eyebb.constant.HttpConstants;

public class HttpRequestUtils {
	private static String TAG = "HttpRequestUtils";

	private static String getParameters(Map<String, String> map) {
		if (map == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			sb.append(entry.getKey()).append("=").append(entry.getValue());
			sb.append("&");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	private static String getResponse(HttpURLConnection urlConn) {
		try {
			// receive data
			BufferedReader in = new BufferedReader(new InputStreamReader(
					urlConn.getInputStream(), HTTP.UTF_8));
			StringBuilder response = new StringBuilder();
			String inputLine;

			while ((inputLine = in.readLine()) != null)
				response.append(inputLine);

			in.close();
			return response.toString();
		} catch (IOException e) {
			System.out.println("error = " + e.getMessage());
			Log.e(TAG, e.getMessage());
			return HttpConstants.HTTP_POST_RESPONSE_EXCEPTION;
		}
	}

	public static String get(String action, Map<String, String> map) {
		String path = HttpConstants.SERVER_URL + action + "?"
				+ getParameters(map);
		URL url = null;
		try {
			url = new URL(path);
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			urlConn.setRequestMethod("GET");
			urlConn.setConnectTimeout(HttpConstants.CONNECT_TIMEOUT);
			urlConn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			urlConn.setRequestProperty("charset", HTTP.UTF_8);

			return getResponse(urlConn);
		} catch (IOException e) {
			System.out.println("error = " + e.getMessage());
			Log.e(TAG, e.getMessage());
			return HttpConstants.HTTP_POST_RESPONSE_EXCEPTION;
		}
	}

	public static String post(String action, Map<String, String> map) {
		String params = getParameters(map);

		String path = HttpConstants.SERVER_URL + action;
		URL url = null;
		try {
			url = new URL(path);
			System.out.println("url = " + url);
			// use HttpURLConnection open connection
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			urlConn.setRequestMethod("POST");
			urlConn.setConnectTimeout(HttpConstants.CONNECT_TIMEOUT);
			urlConn.setDoInput(true);
			urlConn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			urlConn.setRequestProperty("charset", HTTP.UTF_8);
			/*urlConn.setRequestProperty("Content-Length",
					String.valueOf(params.getBytes().length));*/

			OutputStream os = urlConn.getOutputStream();
			os.write(params.getBytes());

			return getResponse(urlConn);
		} catch (IOException e) {
			System.out.println("error = " + e.getMessage());
			Log.e(TAG, e.getMessage());
			return HttpConstants.HTTP_POST_RESPONSE_EXCEPTION;
		}
	}
}
