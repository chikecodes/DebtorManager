package com.kolocoda.debtormanager;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateFormat;

import com.kolocoda.debtormanager.db.DebtorManagerContentProvider;
import com.kolocoda.debtormanager.db.DebtorManagerContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by koloCoda on 24/03/2015.
 */
public class Utility {

    public static String convertDateToString(Date date) {
        String dateFormat = "EEE, dd MMM yyyy";
        String newDateFormat = DateFormat.format(dateFormat, date).toString();
        return newDateFormat;
    }

    public static Date convertStringToDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.US);
        try {
            Date d = sdf.parse(dateString);
            return d;
        } catch(ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Double getIOweTotal(Context c) {
        Cursor cursor = c.getContentResolver().query(DebtorManagerContentProvider.CONTENT_URI, new String[] {DebtorManagerContract.DebtsEntry.COLUMN_AMOUNT}, DebtorManagerContract.DebtsEntry.COLUMN_STATUS + "=" + 1, null, null);
        Double total = 0.0;
        while(cursor.moveToNext()){
            total = total + cursor.getDouble(cursor.getColumnIndex(DebtorManagerContract.DebtsEntry.COLUMN_AMOUNT));
        }
        cursor.close();
        return total;
    }

    public static Double getYouOweTotal(Context c) {
        Cursor cursor = c.getContentResolver().query(DebtorManagerContentProvider.CONTENT_URI, new String[] {DebtorManagerContract.DebtsEntry.COLUMN_AMOUNT}, DebtorManagerContract.DebtsEntry.COLUMN_STATUS + "=" + 0, null, null);
        Double total = 0.0;
        while(cursor.moveToNext()){
            total = total + cursor.getDouble(cursor.getColumnIndex(DebtorManagerContract.DebtsEntry.COLUMN_AMOUNT));
        }
        cursor.close();
        return total;
    }
}
