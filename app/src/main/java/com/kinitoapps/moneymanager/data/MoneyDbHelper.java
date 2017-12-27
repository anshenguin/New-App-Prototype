package com.kinitoapps.moneymanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by HP INDIA on 25-Dec-17.
 */

public class MoneyDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = MoneyDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "money.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;
    public MoneyDbHelper(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + MoneyContract.MoneyEntry.TABLE_NAME + " ("
                + MoneyContract.MoneyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE + " INTEGER NOT NULL, "
                + MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS + " INTEGER NOT NULL DEFAULT 0, "
                + MoneyContract.MoneyEntry.COLUMN_MONEY_DESC + " TEXT, "
                + MoneyContract.MoneyEntry.COLUMN_MONEY_DATE + " TEXT NOT NULL, "
                + MoneyContract.MoneyEntry.COLUMN_MONEY_TIME + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
