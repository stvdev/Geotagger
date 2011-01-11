package com.geotagging.geotagger;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

public class DataActivity extends ListActivity {
	private DBAdapter db;

	public static final String TAG = "DataActivity";

	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "Activity State: onCreate()");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_tab);

		// Get all of the notes from the database and create the item list
		try {
			db = new DBAdapter(this).open();
			Cursor c = db.getAllDataSets();
			startManagingCursor(c);
			ListAdapter adapter = new SimpleCursorAdapter(
					this, 
					android.R.layout.two_line_list_item, 
					c, 
					new String[] { DBAdapter.KEY_DATASETID, DBAdapter.KEY_NAME }, 
					new int[] { android.R.id.text1, android.R.id.text2 });
			setListAdapter(adapter);
			db.close();

		} catch (Exception e) {
			Log.e(TAG, "argh cursor failure! " + e.fillInStackTrace());
		}

	}
}
