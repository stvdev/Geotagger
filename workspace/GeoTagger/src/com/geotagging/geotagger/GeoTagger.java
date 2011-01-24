package com.geotagging.geotagger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class GeoTagger extends Activity {
	private int delay = 4000;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // (NEW)
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // (NEW)
		
		

		setContentView(R.layout.splash);
		
		

		 Handler handler = new Handler();
		 handler.postDelayed(new Runnable() {
		 public void run() {
		 Intent i = new Intent(GeoTagger.this, Main.class);
		 startActivity(i);
		 finish();
		 }
		 }, delay);
	}
}