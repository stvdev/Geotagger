package com.geotagging.geotagger;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class LoggingActivity extends MapActivity {
	public static final String TAG = "Logging";

	/* location listener */
	public static final long TIME_INTERVAL = 10000; // update interval in ms
	public static final float MIN_DISTANCE = 50; 	// update interval in m
	private LocationManager locationManager;
	private LocationListener locationListener;
	static int numOfLocations = 0;

	/* database specific */
	private DBAdapter db;
	private long dataSetId = 0;

	/* map specific */
	private MapView mapView;
	private ArrayList<GeoPoint> gpList = new ArrayList<GeoPoint>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		String dataTitle;
		Bundle b;

		Log.v(TAG, "Activity State: onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logging);

		Toast.makeText(this, "Logging Started...", Toast.LENGTH_SHORT).show();

		// obtain title from bundle
		b = getIntent().getExtras();
		dataTitle = b.getString("title");

		if (dataTitle == "") {
			// if no title have been submitted, set default title to current
			// time
			dataTitle = "" + System.currentTimeMillis();
		}

		try {
			db = new DBAdapter(this).open();
			dataSetId = db.insertDataSet(dataTitle);
			Log.d(TAG, "Inserted " + dataTitle + " with result=" + dataSetId);
		} catch (SQLException e) {
			Log.e(TAG, "SQL Exception:\n" + e.fillInStackTrace());
			Toast.makeText(this, "CGL unable to store in database...",
					Toast.LENGTH_SHORT).show();
			return;
		}

		mapView = (MapView) findViewById(R.id.mapview);

		startLocationListener();

		Button buttonStart = (Button) findViewById(R.id.button_stop);
		buttonStart.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				db.close();
				Toast.makeText(LoggingActivity.this, "Logging Stopped...",
						Toast.LENGTH_SHORT).show();
				launchSummary();
			}
		});
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	protected void startLocationListener() {
		Log.i(TAG, "ENTER startLocationListener()");
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		mapView.setClickable(true);
		mapView.setEnabled(true);
		mapView.setSatellite(false);
		mapView.setTraffic(false);
		mapView.setStreetView(false);
		mapView.setBuiltInZoomControls(true);
		mapView.getController().setZoom(13);

		locationListener = new LocationListener() {
			List<Overlay> mapOverlays = mapView.getOverlays();
			Drawable drawable = getResources().getDrawable(R.drawable.dot);

			RouteOverlay itemizedoverlay = new RouteOverlay(drawable);

			public void onLocationChanged(Location loc) {
				int latitude = (int) (loc.getLatitude() * 1E6);
				int longitude = (int) (loc.getLongitude() * 1E6);

				/* Add position to data set in db */
				db.insertPosition(dataSetId, loc);

				/* TODO: fix recorded path by interconnected plots */
				GeoPoint gp = new GeoPoint(latitude, longitude);
				gpList.add(gp);
				mapView.getController().animateTo(gp);
				OverlayItem overlayitem = new OverlayItem(gp, "omg", "im here!");
				itemizedoverlay.addOverlay(overlayitem);
				mapOverlays.add(itemizedoverlay);
				numOfLocations++;
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {

			}

			public void onProviderEnabled(String provider) {
				Toast.makeText(LoggingActivity.this,
						"Location is now available...", Toast.LENGTH_SHORT)
						.show();
			}

			public void onProviderDisabled(String provider) {
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
		/*
		 * TODO: do we need to handle on pause/resume?
		 */
		// Remove the listener you previously added
		locationManager.removeUpdates(locationListener);
	}

	protected void launchSummary() {
		Log.i(TAG, "ENTER launchSummary()");

		stopLocationListener();
		Intent i = new Intent(this, DataActivity.class);
		startActivity(i);

		Log.i(TAG, "RETURN launchSummary()");
	}
}
