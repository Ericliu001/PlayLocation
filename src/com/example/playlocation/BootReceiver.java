package com.example.playlocation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent intent1 = new Intent(context, LocationService.class);
		intent1.putExtra("eric", "receiver");
    	PendingIntent pi = PendingIntent.getService(context.getApplicationContext()	, 0	, intent1, 0);
    	
    	
    	AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
    	
    	int alarmType = AlarmManager.RTC_WAKEUP;
    	long interval = 1000L*60*20;
    	long start = System.currentTimeMillis();
    	alarmManager.setRepeating(alarmType, start, 20*60*1000, pi);
	}

	
    	
}
