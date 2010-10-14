package com.geotagging.geotagger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.geotagging.geotagger.R.id;

public class LoggingActivity extends Activity {
	public static final String TAG = "Logging";
	public static final long TIME_INTERVAL = 2000; // update interval in ms
	public static final float MIN_DISTANCE = 0; // update interval in m

	private LocationManager locationManager;
	private LocationListener locationListener;
	static int numOfLocations = 0;

	private DBAdapter db;
	long dataSetId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "ENTER onCreate()");
		Log.v(TAG, "Activity State: onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logging);

		/* TODO: move text into constant */
		Toast.makeText(LoggingActivity.this, "GeoTagger Started...",
				Toast.LENGTH_SHORT).show();

		// TODO: obtain string from the input field by intent?
		String dataTitle = "test-1234";
		db = new DBAdapter(this).open();
		dataSetId = db.insertDataSet(dataTitle);

		Log.d(TAG, "Inserted " + dataTitle + " with result=" + dataSetId);

		startLocationListener();

		Button buttonStart = (Button) findViewById(R.id.button_stop);
		buttonStart.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				db.close();
				Toast.makeText(LoggingActivity.this, "GeoTagger Stopped...",
						Toast.LENGTH_SHORT).show();
				launchSummary();
			}
		});
	}

	protected void startLocationListener() {
		Log.i(TAG, "ENTER startLocationListener()");
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		TextView tv = (TextView) findViewById(id.nmeatext);
		tv.setText("No NMEA received...");

		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				TextView tv = (TextView) findViewById(id.nmeatext);
				tv.setText("[" + numOfLocations + "] " + "Latitude:\t"
						+ location.getLatitude() + "\n" + "Longitude:\t"
						+ location.getLongitude() + "\n" + "Altitude:\t"
						+ location.getAltitude() + "\n" + "Bearing:\t"
						+ location.getBearing() + "\n" + tv.getText());

				// TBD: should we use transactions instead so it's not
				// necessary to write constantly towards the db?
				db.insertPosition(dataSetId, location);
				numOfLocations++;
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
				/* TODO: move text into constant */
				Toast.makeText(LoggingActivity.this,
						"Location is now available...", Toast.LENGTH_SHORT)
						.show();
			}

			public void onProviderDisabled(String provider) {
				/* TODO: move text into constant */
				Toast.makeText(LoggingActivity.this,
						"Location no longer available...", Toast.LENGTH_SHORT)
						.show();
			}
		};

		// Register the listener with the Location Manager to receive location
		// updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				TIME_INTERVAL, MIN_DISTANCE, locationListener);
	}

	protected void stopLocationListener() {
		Log.i(TAG, "ENTER stopLocationListener()");

		// Remove the listener you previously added
		locationManager.removeUpdates(locationListener);
	}

	protected void launchSummary() {
		Log.i(TAG, "ENTER launchSummary()");

		// Intent i = new Intent(this, Logging.class);
		// startActivity(i);
		stopLocationListener();

		Intent i = new Intent(this, GeoTagger.class);
		startActivity(i);
	}
}
