package com.kinitoapps.moneymanager.data;

/**
 * Created by HP INDIA on 25-Dec-17.
 */
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Pets app.
 */
public final class MoneyContract{

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private MoneyContract() {}
    public static final String CONTENT_AUTHORITY = "com.kinitoapps.moneymanager";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MONEY = "money_table";
    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class MoneyEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MONEY);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MONEY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MONEY;

        /** Name of database table for pets */
        public final static String TABLE_NAME = "money_table";

        /**
         * Unique ID number for the pet (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the pet.
         *
         * Type: TEXT
         */
        public final static String COLUMN_MONEY_VALUE ="value";

        /**
         * Breed of the pet.
         *
         * Type: TEXT
         */
        public final static String COLUMN_MONEY_DESC = "desc";

        /**
         * Gender of the pet.
         *
         * The only possible values are {@link #STATUS_UNKNOWN}, {@link #STATUS_SPENT},
         * or {@link #STATUS_RECEIVED}.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_MONEY_DATE = "date";

        /**
         * Weight of the pet.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_MONEY_TIME = "time";
        public final static String COLUMN_MONEY_STATUS = "status";

        /**
         * Possible values for the gender of the pet.
         */
        public static final int STATUS_UNKNOWN = 0;
        public static final int STATUS_SPENT = 1;
        public static final int STATUS_RECEIVED = 2;

        public static boolean isValidStatus(int status) {
            if (status == STATUS_UNKNOWN || status == STATUS_RECEIVED || status == STATUS_SPENT) {
                return true;
            }
            return false;
        }
    }

}
