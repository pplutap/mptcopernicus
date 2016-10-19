package com.be4code.mptcopernicus.json;

import org.json.JSONException;
import org.json.JSONObject;


public class JSonParse {

	private static String road;
	private static String number;
	private static String county;

	public static String getRoad(JSONObject jsonItems) {
		for(int i=0; i < jsonItems.length(); i++) {
			try {
				JSONObject array = jsonItems.getJSONObject("address");
				road = array.getString("road");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return road;
	}
	
	public static String getNumber(JSONObject jsonItems) {
		for(int i=0; i < jsonItems.length(); i++) {
			try {
				JSONObject array = jsonItems.getJSONObject("address");
				number = array.getString("house_number");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return number;
	}

	public static CharSequence getCounty(JSONObject jsonItems) {
		for(int i=0; i < jsonItems.length(); i++) {
			try {
				JSONObject array = jsonItems.getJSONObject("address");
				county = array.getString("city");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return county;
	}
	
}
