package com.kinitoapps.moneymanager.data;

/**
 * Created by HP INDIA on 25-Dec-17.
 */
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * {@link ContentProvider} for Pets app.
 */
public class MoneyProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = MoneyContract.MoneyEntry.class.getSimpleName();
    private static final int MONEY = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int MONEY_ID = 101;
    /**
     * Initialize the provider and the database helper object.
     */

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);



    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.pets/pets" will map to the
        // integer code {@link #MONEY}. This URI is used to provide access to MULTIPLE rows
        // of the pets table.
        sUriMatcher.addURI(MoneyContract.CONTENT_AUTHORITY, MoneyContract.PATH_MONEY, MONEY);

        // The content URI of the form "content://com.example.android.pets/pets/#" will map to the
        // integer code {@link #MONEY_ID}. This URI is used to provide access to ONE single row
        // of the pets table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.pets/pets/3" matches, but
        // "content://com.example.android.pets/pets" (without a number at the end) doesn't match.
        sUriMatcher.addURI(MoneyContract.CONTENT_AUTHORITY, MoneyContract.PATH_MONEY + "/#", MONEY_ID);
    }

    /** Database helper object */
    private MoneyDbHelper mDbHelper;

    @Override
    public boolean onCreate() {



        // TODO: Create and initialize a PetDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.

//            mDbHelper = new MoneyDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        mDbHelper = new MoneyDbHelper(getContext());
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MONEY:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(MoneyContract.MoneyEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MONEY_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = MoneyContract.MoneyEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(MoneyContract.MoneyEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MONEY:
                return insertMoney(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertMoney(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(MoneyContract.MoneyEntry.COLUMN_MONEY_DESC);
        if (name == null) {
            throw new IllegalArgumentException("Requires some description");
        }

        // Check that the gender is valid
        Integer status = values.getAsInteger(MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS);
        if (status == null || !MoneyContract.MoneyEntry.isValidStatus(status)) {
            throw new IllegalArgumentException("Requires a valid status");
        }

        // If the weight is provided, check that it's greater than or equal to 0 kg
        Integer value = values.getAsInteger(MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE);
        if (value != null && value < 0) {
            throw new IllegalArgumentException("Requires a positive value");
        }

        // No need to check the breed, any value is valid (including null).

        // Get writeable database
        mDbHelper = new MoneyDbHelper(getContext());
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(MoneyContract.MoneyEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MONEY:
                return updateMoney(uri, contentValues, selection, selectionArgs);
            case MONEY_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = MoneyContract.MoneyEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateMoney(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateMoney(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(MoneyContract.MoneyEntry.COLUMN_MONEY_DESC)) {
            String name = values.getAsString(MoneyContract.MoneyEntry.COLUMN_MONEY_DESC);
            if (name == null) {
                throw new IllegalArgumentException("Requires Description");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS)) {
            Integer status = values.getAsInteger(MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS);
            if (status == null || !MoneyContract.MoneyEntry.isValidStatus(status)) {
                throw new IllegalArgumentException("Requires a valid status");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer value = values.getAsInteger(MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE);
            if (value != null && value < 0) {
                throw new IllegalArgumentException("Requires a positive value");
            }
        }

        // No need to check the breed, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(MoneyContract.MoneyEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        mDbHelper = new MoneyDbHelper(getContext());

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MONEY:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(MoneyContract.MoneyEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MONEY_ID:
                // Delete a single row given by the ID in the URI
                selection = MoneyContract.MoneyEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(MoneyContract.MoneyEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MONEY:
                return MoneyContract.MoneyEntry.CONTENT_LIST_TYPE;
            case MONEY_ID:
                return MoneyContract.MoneyEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }




}
