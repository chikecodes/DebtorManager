package com.kolocoda.debtormanager.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.kolocoda.debtormanager.db.DebtorManagerContract.DebtsEntry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class DebtorManagerContentProvider extends ContentProvider {

    // database
    private DebtorManagerDBOpenHelper dbHelper;

    // Uri Matcher
    private static final int DEBTORS = 10;
    private static final int DEBTOR_WITH_ID = 20;

    public static final String CONTENT_AUTHORITY = "com.kolocoda.debtormanager";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MY_DEBTS = "debtors";

    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MY_DEBTS).build();

    private static HashMap<String, String> MYDEBTS_PROJECTION_MAP;

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/debtors";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/debtors";

    static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        sURIMatcher.addURI(CONTENT_AUTHORITY, "debtors", DEBTORS);
        sURIMatcher.addURI(CONTENT_AUTHORITY, "debtors/#", DEBTOR_WITH_ID);
    }

    public DebtorManagerContentProvider() {
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DebtorManagerDBOpenHelper(getContext());
        return false;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case DEBTOR_WITH_ID:
                String id = uri.getLastPathSegment();
                if(TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(DebtsEntry.TABLE_NAME, DebtsEntry._ID + "=" +id, null);
                } else {
                    rowsDeleted = db.delete(DebtsEntry.TABLE_NAME, DebtsEntry._ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        switch(sURIMatcher.match(uri)) {
            case DEBTORS:
                return CONTENT_TYPE;
            case DEBTOR_WITH_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId;
        switch(uriType) {
            case DEBTORS:
                rowId = db.insert(DebtsEntry.TABLE_NAME, null, values );
                if(rowId>0) {
                    Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowId);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(PATH_MY_DEBTS + "/" + rowId);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        // set the table
        queryBuilder.setTables(DebtsEntry.TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case DEBTORS:
                break;
            case DEBTOR_WITH_ID:
                queryBuilder.appendWhere(DebtsEntry._ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case DEBTORS:
                rowsUpdated = db.update(DebtsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case DEBTOR_WITH_ID:
                String id = uri.getLastPathSegment();
                if(TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(DebtsEntry.TABLE_NAME, values, DebtsEntry._ID + "=" + id, null );
                    Log.i("error", "id is " + DebtsEntry._ID + " " + id);
                } else {
                   rowsUpdated = db.update(DebtsEntry.TABLE_NAME, values, DebtsEntry._ID + "=" + id + " and " + selection, selectionArgs);
                }
            break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(DebtsEntry.getAllColumns()));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
