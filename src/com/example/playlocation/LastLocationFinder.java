package com.example.playlocation;

import java.util.List;

import android.app.PendingIntent;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class LastLocationFinder {

	protected static String SINGLE_LOCATION_UPDATE_ACTION = "com.huiqun.SINGLE_LOCATION_UPDATE_ACTION";

	protected PendingIntent singleUpatePI;
	protected LocationListener locationListener;
	protected LocationManager locationManager;
	protected Context context;
	protected Criteria criteria;

	/**
	 * Construct a new Last Location Finder.
	 * 
	 * @param context
	 *            Context
	 */
	public LastLocationFinder(Context context, LocationListener listener) {
		this.context = context;
		this.locationListener = listener;
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

	}

	/**
	 * Returns the most accurate and timely previously detected location. Where
	 * the last result is beyond the specified maximum distance or latency a
	 * one-off location update is returned via the {@link LocationListener}
	 * specified in {@link setChangedLocationListener}.
	 * 
	 * @param minDistance
	 *            Minimum distance - meters - before we require a location
	 *            update.
	 * @param minTime
	 *            Minimum time - miniseconds - required between location
	 *            updates.
	 * @return The most accurate and / or timely previously detected location.
	 */
	public Location getLastBestLocation(int minDistance, long minTime) {
		Location bestResult = null;
		float bestAccuracy = Float.MAX_VALUE;
		long bestTime = Long.MIN_VALUE;

		// Iterate through all the providers on the system, keeping
		// note of the most accurate result within the acceptable time limit.
		// If no result is found within maxTime, return the newest Location.
		List<String> matchingProviders = locationManager.getAllProviders();
		if (matchingProviders.size() <= 0) {
			return null;
		}

		for (String provider : matchingProviders) {
			Location location = locationManager.getLastKnownLocation(provider);
			if (location != null) {
				float accuracy = location.getAccuracy();
				long time = location.getTime();

				if ((time > minTime && accuracy < bestAccuracy)) {
					bestResult = location;
					bestAccuracy = accuracy;
					bestTime = time;
				} else if (time < minTime && bestAccuracy == Float.MAX_VALUE
						&& time > bestTime) {
					bestResult = location;
					bestTime = time;
				}
			}
		}

		// If the best result is beyond the allowed time limit, or the accuracy
		// of the
		// best result is wider than the acceptable maximum distance, request a
		// single update.
		// This check simply implements the same conditions we set when
		// requesting regular
		// location updates every [minTime] and [minDistance].
		if (locationListener != null
				&& (bestTime < minTime || bestAccuracy > minDistance)) {
			if (matchingProviders.contains(LocationManager.NETWORK_PROVIDER)) {

				locationManager.requestSingleUpdate(
						LocationManager.NETWORK_PROVIDER, locationListener,
						null);
			}

			if (matchingProviders.contains(LocationManager.GPS_PROVIDER)) {

				locationManager.requestSingleUpdate(
						LocationManager.GPS_PROVIDER, locationListener, null);
			}
		}

		return bestResult;
	}

	/**
	 * set the LocationListener;
	 * 
	 * @param the
	 *            LocationListener to pass in;
	 */
	public void setChangedLocationListener(LocationListener l) {
		locationListener = l;
	}

	/**
	 * Unregister the location listener;
	 */
	public void cancel() {
		locationManager.removeUpdates(locationListener);
	}

	private static final int TWO_MINUTES = 1000 * 60 * 2;

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

}
