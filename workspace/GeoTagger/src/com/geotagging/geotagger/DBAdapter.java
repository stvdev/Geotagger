package com.geotagging.geotagger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

public class DBAdapter {
	/*
	 * See CursorAdapter doc. for further explanation to renaming 
	 * columns to _id.
	 */
	public static final String KEY_DATASETID = "_id";
	public static final String KEY_POSID = "position_id";

	public static final String KEY_NAME = "name";
	public static final String KEY_TIME = "timestamp";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_ALTITUDE = "altitude";

	private static final String TAG = "DBAdapter";
	private static final String DB_NAME = "geotagger.db";
	private static final String DB_SETS_TABLE = "datasets";
	private static final String DB_POS_TABLE = "positions";
	private static final int DB_VER = 1;

	private static final String DB_CREATE_SETS_TABLE = "create table "
			+ DB_SETS_TABLE + " (" + KEY_DATASETID
			+ " integer primary key autoincrement, " + KEY_NAME
			+ " text not null);";

	private static final String DB_CREATE_POS_TABLE = "create table "
			+ DB_POS_TABLE + " (" + KEY_POSID
			+ " integer primary key autoincrement, " + KEY_DATASETID
			+ " integer, " + KEY_LATITUDE + " real, " + KEY_LONGITUDE
			+ " real, " + KEY_ALTITUDE + " real, " + KEY_TIME + " integer);";

	private final Context context;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VER);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_CREATE_SETS_TABLE);
			db.execSQL(DB_CREATE_POS_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO: Move text-strings to xml
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DB_POS_TABLE + ";");
			db.execSQL("DROP TABLE IF EXISTS " + DB_SETS_TABLE + ";");
			onCreate(db);
		}
	}

	// ---opens the database---
	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---closes the database---
	public void close() {
		DBHelper.close();
	}

	// ---insert a new dataset into the database---
	public long insertDataSet(String name) throws SQLException {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
		return db.insert(DB_SETS_TABLE, null, initialValues);
	}

	// ---insert a new position into the database---
	public long insertPosition(long dataset, Location loc) {
		Log.d(TAG,
				"Latitude:" + loc.getLatitude() + ", Longitude: "
						+ loc.getLongitude() + ", Altitude:"
						+ loc.getAltitude());

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_DATASETID, dataset);
		initialValues.put(KEY_LATITUDE, loc.getLatitude());
		initialValues.put(KEY_LONGITUDE, loc.getLongitude());
		initialValues.put(KEY_ALTITUDE, loc.getAltitude());
		initialValues.put(KEY_TIME, loc.getTime());
		return db.insert(DB_POS_TABLE, null, initialValues);
	}

	// ---deletes an entire dataset---
	public boolean deleteDataSet(int dataset) {
		if ((db.delete(DB_SETS_TABLE, KEY_DATASETID + "=" + dataset, null) > 0)
				&& (db.delete(DB_POS_TABLE, KEY_DATASETID + "=" + dataset, null) > 0)) {
			return true;
		}
		return false;
	}

	// ---retrieves all the datasets---
	public Cursor getAllDataSets() throws SQLException {
		return db.query(DB_SETS_TABLE,
				new String[] { KEY_DATASETID, KEY_NAME }, null, null, null,
				null, "_id DESC");
	}

	// ---retrieves all positions in a particular dataset---
	public Cursor getPositionByDataSet(int dataset) throws SQLException {
		Cursor mCursor = db.query(true, DB_POS_TABLE, new String[] {
				KEY_LATITUDE, KEY_LONGITUDE, KEY_ALTITUDE, KEY_TIME },
				KEY_DATASETID + "=" + dataset, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// ---updates a dataset---
	public boolean updateDataSet(int dataset, String name) {
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, name);
		return db.update(DB_SETS_TABLE, args, KEY_DATASETID + "=" + dataset,
				null) > 0;
	}
}
