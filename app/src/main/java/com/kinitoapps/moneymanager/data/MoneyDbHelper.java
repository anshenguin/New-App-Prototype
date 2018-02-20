package com.kinitoapps.moneymanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;

/**
 * Created by HP INDIA on 25-Dec-17.
 */

public class MoneyDbHelper extends SQLiteOpenHelper {

    /** Name of the database file */
    private static final String DATABASE_NAME = "money.db";
    private static final String FILE_DIR = "Money Manager";
    private static final String NESTED_DIR = "Database";
    private static final String FULL_DIR= System.getenv("EXTERNAL_STORAGE")
            +File.separator+FILE_DIR+File.separator+
            NESTED_DIR;


    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;
    public MoneyDbHelper(Context context) {
        super(context, FULL_DIR +File.separator+ DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase.openOrCreateDatabase(FULL_DIR +File.separator+ DATABASE_NAME,null);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_TABLE =  "CREATE TABLE " + MoneyContract.MoneyEntry.TABLE_NAME + " ("
                + MoneyContract.MoneyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE + " DOUBLE NOT NULL, "
                + MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS + " INTEGER NOT NULL DEFAULT 0, "
                + MoneyContract.MoneyEntry.COLUMN_MONEY_DESC + " TEXT, "
                + MoneyContract.MoneyEntry.COLUMN_MONEY_DATE + " TEXT, "
                + MoneyContract.MoneyEntry.COLUMN_MONEY_TIME + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String SQL_CREATE_TABLE =  "CREATE TABLE " + MoneyContract.MoneyEntry.TABLE_NAME + " ("
                + MoneyContract.MoneyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE + " DOUBLE NOT NULL, "
                + MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS + " INTEGER NOT NULL DEFAULT 0, "
                + MoneyContract.MoneyEntry.COLUMN_MONEY_DESC + " TEXT, "
                + MoneyContract.MoneyEntry.COLUMN_MONEY_DATE + " TEXT, "
                + MoneyContract.MoneyEntry.COLUMN_MONEY_TIME + " TEXT);";

        // Execute the SQL statement
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

}
