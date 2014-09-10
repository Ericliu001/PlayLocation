package com.example.playlocation;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends IntentService implements LocationListener {
	private LastLocationFinder finder;

	public LocationService() {
		super("LocationService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		
		String s = intent.getStringExtra("eric");
		Log.d("eric", "is from receiver? " + s);

		finder = new LastLocationFinder(this, this);
		Location location = finder.getLastBestLocation(1000, 1000);
		if (location != null) {
//			Toast.makeText(getApplicationContext(), "Longitude: " + location.getLongitude() + "; " + "Latitude: " + location.getLatitude(), Toast.LENGTH_SHORT).show();
			Log.d("eric", "Longitude: " + location.getLongitude() + "; " + "Latitude: " + location.getLatitude());
			
			
			
			Notification nf = new Notification.Builder(this)
					.setContentTitle("Location update from Service." )
					.setContentText("Longitude: " + location.getLongitude() + "; " + "Latitude: " + location.getLatitude()).build();

			NotificationManager notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			notifyManager.notify(0, nf);
		}
		
	}

	@Override
	public void onDestroy() {
		finder.cancel();
		super.onDestroy();
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

}
