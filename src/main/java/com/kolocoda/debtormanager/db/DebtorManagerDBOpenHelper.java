package com.kolocoda.debtormanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kolocoda.debtormanager.db.DebtorManagerContract.DebtsEntry;

public class DebtorManagerDBOpenHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "mydebts.db";
	private static final int DATABASE_VERSION = 1;

	public DebtorManagerDBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

        final String TABLE_CREATE = "CREATE TABLE " + DebtsEntry.TABLE_NAME + " (" +
                DebtsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DebtsEntry.COLUMN_NAME + " VARCHAR(32) NOT NULL, " + DebtsEntry.COLUMN_PHONE_NO + " VARCHAR(11) NOT NULL, " +
                DebtsEntry.COLUMN_STATUS + " INT(1) NOT NULL, " + DebtsEntry.COLUMN_AMOUNT + " VARCHAR(32) NOT NULL, " + DebtsEntry.COLUMN_DATE_ENTERED + " VARCHAR NOT NULL, " +
                DebtsEntry.COLUMN_DATE_DUE + " VARCHAR NOT NULL, " + DebtsEntry.COLUMN_NOTE + " TEXT NOT NULL" + ")";

		db.execSQL(TABLE_CREATE);
		Log.i("KOLO", "Table has been created.");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXIST " + DebtsEntry.TABLE_NAME);
		onCreate(db);

	}

}
