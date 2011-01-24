package com.geotagging.geotagger;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class DataActivity extends ListActivity {
	private DBAdapter db;
	protected SimpleCursorAdapter adapter;
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
			adapter = new SimpleCursorAdapter(this,
					android.R.layout.two_line_list_item, c, new String[] {
							DBAdapter.KEY_DATASETID, DBAdapter.KEY_NAME },
					new int[] { android.R.id.text2, android.R.id.text1 });
			setListAdapter(adapter);
			db.close();

		} catch (Exception e) {
			Log.e(TAG, "argh cursor failure! " + e.fillInStackTrace());
		}

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Cursor foo = (Cursor) adapter.getItem(position);
				String name = foo.getString(foo
						.getColumnIndex(DBAdapter.KEY_NAME));
				Toast.makeText(DataActivity.this, "Exporting..." + name,
						Toast.LENGTH_SHORT).show();
				Cursor dataSetPositions = getPositions(id);
				try {
					FileHandler fh = new FileHandler();
					if (!fh.WriteFile(1, dataSetPositions, name)) {
						Toast.makeText(DataActivity.this,
								"Unable to export! Check your storage.",
								Toast.LENGTH_LONG).show();

					}

				} catch (Exception e) {
					Log.e(TAG, "argh file failure! " + e.fillInStackTrace());
				}
			}
		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Cursor foo = (Cursor) adapter.getItem(position);
				String name = foo.getString(foo
						.getColumnIndex(DBAdapter.KEY_NAME));
				Toast.makeText(
						DataActivity.this,
						"Deleting dataset... Hope that's what you intended :-)"
								+ name, Toast.LENGTH_SHORT).show();
				try {
					FileHandler fh = new FileHandler();
					db.open();
					if (!fh.deleteGpxFile(name) && !db.deleteDataSet((int) id)) {
						Toast.makeText(DataActivity.this, "Unable to delete!",
								Toast.LENGTH_LONG).show();

					}
					else
					{
						// force update of view adapter
						adapter.notifyDataSetChanged();
					}
					db.close();
				} catch (Exception e) {
					Log
							.e(TAG, "argh deletion failure! "
									+ e.fillInStackTrace());
				}
				return false;
			}
		});
	}

	protected Cursor getPositions(long id) {

		db.open();
		Cursor positions = db.getPositionByDataSet((int) id);
		db.close();
		return positions;
	}
}
