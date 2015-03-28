package com.kolocoda.debtormanager.db;

import android.provider.BaseColumns;

/**
 * Created by koloCoda on 20/03/2015.
 */
public class DebtorManagerContract {

    public static final class DebtsEntry implements BaseColumns {

        public static final String TABLE_NAME = "debts";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHONE_NO = "phone_no";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_DATE_DUE = "date_due";
        public static final String COLUMN_DATE_ENTERED = "date_entered";
        public static final String COLUMN_NOTE = "note";

        // returns all columns as array
        public static String[] getAllColumns() {
            String[] columns = {
                    _ID,
                    COLUMN_NAME,
                    COLUMN_PHONE_NO,
                    COLUMN_STATUS,
                    COLUMN_AMOUNT,
                    COLUMN_DATE_DUE,
                    COLUMN_DATE_ENTERED,
                    COLUMN_NOTE,
            };

            return columns;
        }
    }
}
