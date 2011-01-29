package com.geotagging.geotagger;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class DataActivity extends ListActivity {
	private DBAdapter db;
	private Cursor c;
	protected SimpleCursorAdapter adapter;
	public static final String TAG = "DataActivity";

	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "Activity State: onCreate()");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.data);

		// Get all of the notes from the database and create the item list
		try {
			db = new DBAdapter(this).open();
			c = db.getAllDataSets();
			startManagingCursor(c);
			adapter = new SimpleCursorAdapter(this,
					android.R.layout.two_line_list_item, c, new String[] {
							DBAdapter.KEY_DATASETID, DBAdapter.KEY_NAME },
					new int[] { android.R.id.text2, android.R.id.text1 });
			setListAdapter(adapter);
		} catch (Exception e) {
			Log.e(TAG, "argh cursor failure! " + e.fillInStackTrace());
		}

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				displayAction(id, position);
			}
		});
	}

	protected Cursor getPositions(long id) {
		Cursor positions = db.getPositionByDataSet((int) id);
		return positions;
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
			return true;
		case R.id.datasets:
			intent = new Intent().setClass(this, DataActivity.class);
			startActivity(intent);
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
				"\nCombitech GPS Logger 2.0\n\nIt is twice as good as v1.0."
						+ "\n\nCreated by:\nSteven Bergstedt &\nJohan Wennberg")
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

	private void displayAction(final long dsid, final int dsposition) {
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setMessage("Your choice.")
				.setCancelable(true)
				.setPositiveButton("Export",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Action for 'Yes' Button
								exportDS(dsid, dsposition);
								dialog.cancel();
							}
						});
		alt_bld.setNeutralButton("Delete",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// Action for 'Yes' Button
						displayConfirmation(dsid, dsposition);
						dialog.cancel();
					}
				});
		alt_bld.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// Action for 'Yes' Button
						dialog.cancel();
					}
				});
		AlertDialog alert = alt_bld.create();
		// Title for AlertDialog
		alert.setTitle("Action");
		// Icon for AlertDialog
		alert.setIcon(R.drawable.ic_menu_help);
		alert.show();
	}

	public void delDS(long id, int position) {
		Cursor foo = (Cursor) adapter.getItem(position);
		String name = foo.getString(foo.getColumnIndex(DBAdapter.KEY_NAME));
		Toast.makeText(DataActivity.this, "Deleting " + name,
				Toast.LENGTH_SHORT).show();
		try {
			FileHandler fh = new FileHandler();
			if (!fh.deleteGpxFile(name)) {
				Toast.makeText(DataActivity.this, "Unable to delete file!",
						Toast.LENGTH_LONG).show();
			}
			if (!db.deleteDataSet((int) id)) {
				Toast.makeText(DataActivity.this, "Unable to delete db elem!",
						Toast.LENGTH_LONG).show();
			}

			// update cursor with latest data
			c.requery();

		} catch (Exception e) {
			Log.e(TAG, "argh deletion failure! " + e.fillInStackTrace());
		}
	}

	private void exportDS(long id, int position) {
		Cursor foo = (Cursor) adapter.getItem(position);
		String name = foo.getString(foo.getColumnIndex(DBAdapter.KEY_NAME));
		Toast.makeText(DataActivity.this, "Exporting " + name,
				Toast.LENGTH_SHORT).show();
		Cursor dataSetPositions = getPositions(id);
		try {
			FileHandler fh = new FileHandler();
			if (!fh.WriteFile(1, dataSetPositions, name)) {
				Toast.makeText(DataActivity.this,
						"Unable to export! Check your storage.",
						Toast.LENGTH_LONG).show();
			}
			else {
			    String fileName = "CGL_" + name + ".gpx";
			    String path = Environment.DIRECTORY_DOWNLOADS;
				Toast.makeText(DataActivity.this, "Exported to file: " + path + "\\" + fileName,
						Toast.LENGTH_LONG).show();
			}

		} catch (Exception e) {
			Log.e(TAG, "argh file failure! " + e.fillInStackTrace());
		}
	}

	private void displayConfirmation(final long dsid, final int dsposition) {
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setMessage("This can not be undone!")
				.setCancelable(true)
				.setPositiveButton("Delete",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Action for 'Yes' Button
								delDS(dsid, dsposition);
								dialog.cancel();
							}
						});
		alt_bld.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// Action for 'Yes' Button
						dialog.cancel();
					}
				});
		AlertDialog alert = alt_bld.create();
		// Title for AlertDialog
		alert.setTitle("Are you sure?");
		// Icon for AlertDialog
		alert.setIcon(R.drawable.ic_menu_help);
		alert.show();
	}

}
