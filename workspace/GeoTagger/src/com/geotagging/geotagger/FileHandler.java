package com.geotagging.geotagger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

public class FileHandler {

	// XML Header
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    
    // GPX opening tag
    private static final String TAG_GPX = "<gpx"
            + " xmlns=\"http://www.topografix.com/GPX/1/1\""
            + " version=\"1.1\""
            + " creator=\"CGLv2.0\""
            + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd \">";
	
    // GPX metadata
    private static final String GPX_META = "<metadata>"
    		+ "<link href=\"http://www.combitech.se\">"
    		+ "<text>GeoTagger</text>"
    		+ "</link>"
    		+ "</metadata>";
    
    // Date format for a point timestamp.
    private static final SimpleDateFormat POINT_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
    public boolean WriteFile(int fileFormat, Cursor positions, String dataSetName){
   	if(!checkExternalStorage()){
    		return false;
    	}
		switch (fileFormat) {
	
		case 1: 
			return createGPX(positions, dataSetName);
	
		case 2:
//			return createKML(positions, dataSetName);
		}
	
		Log.i("yes", "excellent 5");
		return false;
    }
	
	// Check if external storage is available and writeable
	private boolean checkExternalStorage(){
		boolean extStoreWrite = false;
		String state = Environment.getExternalStorageState();
		
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    extStoreWrite = true;
		} else {
			extStoreWrite = false;
		}		
		return extStoreWrite;	
	}
	
	private boolean createGPX(Cursor pos, String fName){
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	    path.mkdirs();
		File file = new File(path, "CGL_" + fName + ".gpx");
	    try {
			FileWriter writer = new FileWriter(file);
			writer.write(XML_HEADER + "\n");
			writer.write(TAG_GPX + "\n");
			writer.write("\t" + GPX_META + "\n");
			writer.write("\t<trk>\n");
			writer.write("\t\t<name>" + fName + "</name>\n");
			writer.write("\t\t<trkseg>\n");
			
			pos.moveToFirst();
			while (!pos.isAfterLast() ) {
				writer.write("\t\t\t<trkpt lat=\"" + 
						pos.getDouble(pos.getColumnIndex(DBAdapter.KEY_LATITUDE)) + "\" lon=\"" + 
						pos.getDouble(pos.getColumnIndex(DBAdapter.KEY_LONGITUDE)) + "\">\n");
				writer.write("\t\t\t\t<ele>" + pos.getDouble(pos.getColumnIndex(DBAdapter.KEY_ALTITUDE)) + "</ele>\n");
				writer.write("\t\t\t\t<time>" + 
						POINT_DATE_FORMATTER.format(new Date(pos.getLong(pos.getColumnIndex(DBAdapter.KEY_TIME)))) + 
						"</time>\n");
				writer.write("\t\t\t</trkpt>\n");
	            pos.moveToNext();
	         }
			 writer.write("\t\t</trkseg>\n");
			 writer.write("\t</trk>\n");
			 writer.write("</gpx>\n");
			 writer.close();
			
	    } catch (IOException e) {
			e.printStackTrace();
			return false;
	    }		
		return true;
	}
	
	public boolean deleteGpxFile(String dataset) {
		try{
			File path = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_DOWNLOADS);
			File file = new File(path, "CGL_" + dataset + ".gpx");
			file.delete();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
	    }		
	    return true;
	}

	
}
