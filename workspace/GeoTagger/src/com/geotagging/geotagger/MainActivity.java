package com.geotagging.geotagger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
				launchLogging();
			}
		});
		Log.i(TAG, "RETURN onCreate()");
	}

	protected void launchLogging() {
		Log.i(TAG, "ENTER launchLogging()");
		Intent i = new Intent(this, LoggingActivity.class);
		Bundle b = new Bundle();
		EditText et = (EditText) findViewById(R.id.entry);
		b.putString("title", et.getText().toString());
		i.putExtras(b);
		startActivity(i);
		finish();
		Log.i(TAG, "RETURN launchLogging()");
	}
}