package com.kolocoda.debtormanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class DebtorManagerDataSource {

	public static final String LOG_TAG = "mydebts";
	SQLiteOpenHelper dbHelper;
	SQLiteDatabase database;
	Context context;

	public DebtorManagerDataSource(Context c) {

        dbHelper = new DebtorManagerDBOpenHelper(c);
        context = c;
	}
	
	public void open() {
		Log.i(LOG_TAG, "Database opened.");
		database = dbHelper.getWritableDatabase();	
	}
	
	public void close() {
		Log.i(LOG_TAG, "Database closed.");
		dbHelper.close();
	}
	
	public Uri insert(ContentValues values) {
        Uri uri = context.getContentResolver().insert(DebtorManagerContentProvider.CONTENT_URI, values);
		return uri;
	}
	
	public boolean removeDebtor(Uri uri) {
		return (context.getContentResolver().delete(uri, null, null) == 1);
	}

	public boolean updateDebtor(ContentValues values, long id) {
        Uri uri = Uri.parse(DebtorManagerContentProvider.CONTENT_URI + "/" + id);
        int result = context.getContentResolver().update(uri, values, null, null);
		return (result == 1);
	}

}
