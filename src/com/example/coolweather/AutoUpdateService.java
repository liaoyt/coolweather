package com.example.coolweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		// 服务开启时更新天气
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateWeather();
			}
		}).start();
		
		// 设置定时功能
		AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
		int anHour = 60 * 60 * 1000;
		long triggerAtTime = SystemClock.elapsedRealtime() + 3 * anHour;		// 每三小时更新
		Intent i = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
		
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void updateWeather() {
		// TODO Auto-generated method stub
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode = preferences.getString("weatherCode", "");
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				e.printStackTrace();
			}
			
			@Override
			public void OnFinished(String response) {
				// TODO Auto-generated method stub
				Utility.handleWeatherResponse(AutoUpdateService.this, response);
			}
		});
	}

}
