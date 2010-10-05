package com.geotagging.geotagger;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class GeoTagger extends TabActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabs);

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, MainActivity.class);
		spec = tabHost.newTabSpec("main").setIndicator("Main",
				res.getDrawable(R.drawable.ic_tab_main)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, DataActivity.class);
		spec = tabHost.newTabSpec("data").setIndicator("Data",
				res.getDrawable(R.drawable.ic_tab_data)).setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
	}
}