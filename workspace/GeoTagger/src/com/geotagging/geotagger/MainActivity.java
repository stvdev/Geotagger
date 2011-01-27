package com.geotagging.geotagger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final String TAG = "MainActivity";

	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "ENTER onCreate()");
		Log.v(TAG, "Activity State: onCreate()");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button buttonStart = (Button) findViewById(R.id.button_start);
		buttonStart.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchLogging();
			}
		});

		try {
			LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				enableGpsAlert();
			}
		} catch (Exception e) {
			Log.e(TAG, "argh locationManager failure! " + e.fillInStackTrace());
		}
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

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.button_menu, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.startlogging:
			intent = new Intent().setClass(this, MainActivity.class);
			startActivity(intent);
			finish();
			return true;
		case R.id.datasets:
			intent = new Intent().setClass(this, DataActivity.class);
			startActivity(intent);
			finish();
			return true;
		case R.id.help:
			displayHelp();
			return true;
		case R.id.about:
			displayAbout();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void displayHelp() {
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setMessage(
				"\nHelp!\n\nHelp!\n\nHelp!\n\nHelp!\n\nHelp!\n\nHelp!\n\nHelp!\n\nHelp!\n\nHelp!\n\nHelp!\n\nHelp!\n\nHelp!\n\nHelp!\n\nHelp!\n\nHelp!\n\nHelp!\n\nHelp!\n")
				.setCancelable(true)
				.setPositiveButton("Close",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Action for 'Yes' Button
								dialog.cancel();
							}
						});
		AlertDialog alert = alt_bld.create();
		// Title for AlertDialog
		alert.setTitle("Help");
		// Icon for AlertDialog
		alert.setIcon(R.drawable.ic_menu_help);
		alert.show();
	}

	private void displayAbout() {
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setMessage(
				"\nCombitech GPS Logger 2.0\n\nIt is twice as good as v1.0.")
				.setCancelable(true)
				.setPositiveButton("Close",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Action for 'Yes' Button
								dialog.cancel();
							}
						});
		AlertDialog alert = alt_bld.create();
		// Title for AlertDialog
		alert.setTitle("About CGLv2.0");
		// Icon for AlertDialog
		alert.setIcon(R.drawable.ic_menu_info_details);
		alert.show();
	}

	private void enableGpsAlert() {
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setMessage("Your GPS is currently disabled. Would you like to enable it?")
				.setCancelable(true)
				.setPositiveButton("Enable GPS",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent gpsOptionsIntent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(gpsOptionsIntent);
							}
						});
		
		alt_bld.setNegativeButton("Do nothing",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Toast.makeText(MainActivity.this, "This app suddently becomes less meaningfull...",
								Toast.LENGTH_LONG).show();
						dialog.cancel();
					}
				});
		AlertDialog alert = alt_bld.create();
		alert.setIcon(R.drawable.ic_menu_help);
		alert.show();
	}
}
