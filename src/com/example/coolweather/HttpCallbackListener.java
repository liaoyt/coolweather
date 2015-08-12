package com.example.coolweather;

import android.app.PendingIntent.OnFinished;

public interface HttpCallbackListener {
	
	void OnFinished(String response);
	
	void onError(Exception e);

}
