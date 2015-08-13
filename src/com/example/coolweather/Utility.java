package com.example.coolweather;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class Utility {

	// 解析和处理服务器返回的省级数据
	public synchronized static boolean handleProvincesResponse(
			CoolWeatherDB coolWeatherDB, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0) {
				for (String p : allProvinces) {
					String [] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceName(array[1]);
					province.setProvinceCode(array[0]);
					Log.i(province.getProvinceCode(), province.getProvinceName());
					
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	// 解析和处理服务器返回的市级数据
	public synchronized static boolean handleCitiesResponse(
			CoolWeatherDB coolWeatherDB, String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (String p : allCities) {
					String [] array = p.split("\\|");
					City city = new City();
					city.setCityName(array[1]);
					city.setCityCode(array[0]);
					city.setProvinceId(provinceId);
					
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	// 解析和处理服务器返回的县级数据
		public synchronized static boolean handleCountiesResponse(
				CoolWeatherDB coolWeatherDB, String response, int cityId) {
			if (!TextUtils.isEmpty(response)) {
				String[] allCounties = response.split(",");
				if (allCounties != null && allCounties.length > 0) {
					for (String p : allCounties) {
						String [] array = p.split("\\|");
						County county = new County();
						county.setCountyName(array[1]);
						county.setCountyCode(array[0]);
						county.setCityId(cityId);
						
						coolWeatherDB.saveCounty(county);
					}
					return true;
				}
			}
			return false;
		}
		
		// 解析服务器返回的json数据，并将解析出的数据存储到本地
		public static void handleWeatherResponse(Context context, String response) {
			try {
				JSONObject jsonObject = new JSONObject(response);
				JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
				String cityName = weatherInfo.getString("city");
				String weahterCode = weatherInfo.getString("cityid");
				String temp1 = weatherInfo.getString("temp1");
				String temp2 = weatherInfo.getString("temp2");
				String weatherDesp = weatherInfo.getString("weather");
				String publishTime = weatherInfo.getString("ptime");
				saveWeatherInfo(context, cityName, weahterCode, temp1, temp2, weatherDesp, publishTime);
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		private static void saveWeatherInfo(Context context, String cityName,
				String weahterCode, String temp1, String temp2,
				String weatherDesp, String publishTime) {
			// TODO Auto-generated method stub
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
			editor.putBoolean("city_selected", true);
			editor.putString("city_name", cityName);
			editor.putString("weahterCode", weahterCode);
			editor.putString("temp1", temp1);
			editor.putString("temp2", temp2);
			editor.putString("weatherDesp", weatherDesp);
			editor.putString("publishTime", publishTime);
			editor.putString("current_date", sdf.format(new Date()));
			editor.commit();
		}
}
