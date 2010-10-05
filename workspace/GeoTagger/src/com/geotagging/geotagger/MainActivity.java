package com.geotagging.geotagger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	public static final String TAG = "MainActivity";

	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "ENTER onCreate()");
		Log.v(TAG, "Activity State: onCreate()");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab);
		Button buttonStart = (Button) findViewById(R.id.button_start);
		buttonStart.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on clicks
				// Toast.makeText(MainActivity.this, "GeoTagger Started...",
				// Toast.LENGTH_SHORT).show();
				launchLogging();
			}
		});

	}

	protected void launchLogging() {
		Log.i(TAG, "ENTER launchLogging()");
		Intent i = new Intent(this, LoggingActivity.class);
		startActivity(i);
	}
}