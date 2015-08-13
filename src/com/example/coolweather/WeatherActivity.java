package com.example.coolweather;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity {
	
	private LinearLayout weatherInfoLayout;
	
	private TextView cityNameText;
	
	private TextView publishText;
	
	private TextView weatherDespText;
	
	private TextView temp1Text;
	private TextView temp2Text;
	
	private TextView currentDateText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		weatherInfoLayout = (LinearLayout)findViewById(R.id.weather_info_layout);
		cityNameText = (TextView)findViewById(R.id.city_name);
		publishText = (TextView)findViewById(R.id.publish_text);
		weatherDespText = (TextView)findViewById(R.id.weather_desp);
		temp1Text = (TextView)findViewById(R.id.temp1);
		temp2Text = (TextView)findViewById(R.id.temp2);
		currentDateText = (TextView)findViewById(R.id.current_date);
		
		String countyCode = getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode)) {
			// 有县级代号就去查询天气
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}else {
			showWeather();
		}
		
	}

	// 从sharedpreferences文件中读取存储的天气信息，并显示到界面上
	private void showWeather() {
		// TODO Auto-generated method stub
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(preferences.getString("city_name", ""));
		temp1Text.setText(preferences.getString("temp1", ""));
		temp2Text.setText(preferences.getString("temp2", ""));
		weatherDespText.setText(preferences.getString("weatherDesp", ""));
		publishText.setText("今天" + preferences.getString("publishTime", "") + "发布");
		currentDateText.setText(preferences.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}

	private void queryWeatherCode(String countyCode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}
	
	private void queryWeatherInfo(String weatherCode){
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}

	private void queryFromServer(String address, final String type) {
		// TODO Auto-generated method stub
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						publishText.setText("同步失败");
					}
				});
			}
			
			@Override
			public void OnFinished(String response) {
				// TODO Auto-generated method stub
				if ("countyCode".equals(type)) {
						if (!TextUtils.isEmpty(response)) {
							// 从服务器返回的数据中解析出天气代号
							String[] array = response.split("\\|");
							if (array != null && array.length == 2) {
								String weatherCode = array[1];
								queryWeatherInfo(weatherCode);
							}
						}
				}else  if ("weatherCode".equals(type)) {
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						public void run() {
							showWeather();
						}
					});
				}
			}
		});
	}
	
	

}
