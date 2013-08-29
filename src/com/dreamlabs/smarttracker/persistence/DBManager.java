package com.dreamlabs.smarttracker.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager extends SQLiteOpenHelper {

	public DBManager(Context applicationcontext) {
		super(applicationcontext, "smarttracker.db", null, 1);
	}


	@Override
	public void onCreate(SQLiteDatabase database) {

		String query = "CREATE TABLE UserPreferences (id  INTEGER PRIMARY KEY, EMR_DialType	TEXT, "
				+ "EMR_Contact TEXT, EMR_SMS1_Contact TEXT, "
				+ "EMR_SMS2_Contact TEXT, "
				+ "DefaultLocation TEXT, "
				+ "NotificationChannel TEXT, " 
				+ "NotificationChannelType TEXT, "+
				"Broadcastalways boolean)";
				database.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}

	public void saveDialType(String type) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("EMR_DialType", type);
		database.insert("UserPreferences", null, values);
		database.close();
	}
	
	public void saveEmergencyContact(String contact) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("EMR_Contact", contact);
		database.insert("UserPreferences", null, values);
		database.close();
	}
	
	public void saveEmergencySMS1(String contact) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("EMR_SMS2_Contact", contact);
		database.insert("UserPreferences", null, values);
		database.close();
	}
	
	public void saveEmergencySMS2(String contact) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("EMR_SMS1_Contact", contact);
		database.insert("UserPreferences", null, values);
		database.close();
	}
	
	public void saveDefaultLocation(String location) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("DefaultLocation", location);
		database.insert("UserPreferences", null, values);
		database.close();
	}
	
	public void saveNotificationChannel(String location) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("DefaultLocation", location);
		database.insert("UserPreferences", null, values);
		database.close();
	}
	
	public void saveNotificationChannelType(String location) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("DefaultLocation", location);
		database.insert("UserPreferences", null, values);
		database.close();
	}
	
	
	/*public HashMap<String, String> getAnimalInfo(String id) {
		  HashMap<String, String> wordList = new HashMap<String, String>();
		         
		  return wordList;
		} */
	
	public String getDialType() {
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM UserPreferences";
		 Cursor cursor = database.rawQuery(selectQuery, null);
		 String value = null;
		  if (cursor.moveToFirst()) {
			  value = cursor.getString(1);
		  }
		return value;   
	}
	
	public String getPMFKey() {
		return "sample";
	}

}
