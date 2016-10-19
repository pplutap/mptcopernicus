package com.be4code.mptcopernicus.json;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class JSonGet {
	
	/**
	 * Metoda statyczna pobiera najnowsze wpisy z bazy danych i zwraca je w formacie JSONArray
	 * @param limit
	 * @return JSONArray
	 */
	public static JSONObject getLatestItems(String lat, String lon) {
		InputStream is = null;
		String result = "";
		JSONObject jArray = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			//http://nominatim.openstreetmap.org/reverse?format=json&lat=50.008089&lon=19.960429&zoom=18&addressdetails=1
			//http://nominatim.openstreetmap.org/reverse?format=json&lat=" + lat + "&lon=" + lon + "&zoom=18&addressdetails=1
			HttpPost httppost = new HttpPost("http://nominatim.openstreetmap.org/reverse?format=json&lat=" + lat + "&lon=" + lon + "&zoom=18&addressdetails=1");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (Exception e) {}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
		} catch (Exception e) {
		}
		try {
			jArray = new JSONObject(result);
		} catch (JSONException e) {
		}
		return jArray;
	}
}
