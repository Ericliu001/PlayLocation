package com.example.playlocation;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity implements LocationListener {
	private LastLocationFinder finder;
	private static final String ACTION_UPDATE_LOCATION = "com.example.playlocation.StartRepeating";
	private TextView tvLong;
	private TextView tvLat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tvLong = (TextView) findViewById(R.id.tvLong);
		tvLat = (TextView) findViewById(R.id.tvLat);

		finder = new LastLocationFinder(this, this);
		Location location = finder.getLastBestLocation(1000, 1000);

		if (location != null) {
			tvLong.setText("Longitude: " + location.getLongitude());
			tvLat.setText("Latitude: " + location.getLatitude());

		}

		Intent intentB = new Intent();
		intentB.setAction(ACTION_UPDATE_LOCATION);
		sendBroadcast(intentB);

		Intent intentS = new Intent(this, LocationService.class);
		startService(intentS);
	}

	public void onButtonClicked(View v) {
		switch (v.getId()) {
		case R.id.btNotify:
			startNotification();
			break;

		default:
			break;
		}
	}

	private void startNotification() {
		Location location = finder.getLastBestLocation(1000, 1000);
		Notification nf = new Notification.Builder(this)
				.setContentTitle("New Location.")
				.setContentText(
						"Longitude: " + location.getLongitude() + "; "
								+ "Latitude: " + location.getLatitude())
				.setSmallIcon(R.drawable.ic_launcher).build();

		NotificationManager notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notifyManager.notify(0, nf);
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
