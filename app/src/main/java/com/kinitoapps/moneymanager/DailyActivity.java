package com.kinitoapps.moneymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kinitoapps.moneymanager.data.MoneyContract;
import com.kinitoapps.moneymanager.data.MoneyDbHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DailyActivity extends AppCompatActivity {
    ImageView nextDate,previousDate,dropDown;
    TextView curDate;
    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);
        nextDate = findViewById(R.id.imageViewRight);
        previousDate = findViewById(R.id.imageViewLeft);
        curDate = findViewById(R.id.currentDate);
        curDate.setText(currentDate);
        currentDate = curDate.getText().toString();
        nextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incDecDate(1);
            }
        });
        previousDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incDecDate(-1);
            }
        });
    }

    private void incDecDate(int i) {
        Date date;
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            date = df.parse(currentDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE,i);
            currentDate = df.format(c.getTime());
            Toast.makeText(this, "current date: "+currentDate, Toast.LENGTH_SHORT).show();

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public String getSumReceived() {
        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" =? AND "+ MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS+" =?";
        String[] ARGS = {currentDate,"2"};
        MoneyDbHelper mDbHelper = new MoneyDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sumReceived = "";
        String[] PROJECTION = {
                "SUM(value)"
        };
        Cursor cur=null;
        try {
            cur = db.query(MoneyContract.MoneyEntry.TABLE_NAME,
                    PROJECTION,
                    SELECTION,
                    ARGS,
                    null, null, null);
        }catch (SQLiteException e){
            if (e.getMessage().contains("no such table")){
                String SQL_CREATE_TABLE =  "CREATE TABLE " + MoneyContract.MoneyEntry.TABLE_NAME + " ("
                        + MoneyContract.MoneyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE + " DOUBLE NOT NULL, "
                        + MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS + " INTEGER NOT NULL DEFAULT 0, "
                        + "\""+MoneyContract.MoneyEntry.COLUMN_MONEY_DESC +"\""+ " TEXT, "
                        + MoneyContract.MoneyEntry.COLUMN_MONEY_DATE + " TEXT, "
                        + MoneyContract.MoneyEntry.COLUMN_MONEY_TIME + " TEXT);";

                // Execute the SQL statement
                db.execSQL(SQL_CREATE_TABLE);
                cur = db.query(MoneyContract.MoneyEntry.TABLE_NAME,
                        PROJECTION,
                        SELECTION,
                        ARGS,
                        null, null, null);
                // create table
                // re-run query, etc.

            }
        }
//        Cursor cur = db.rawQuery("SELECT SUM(value) FROM today WHERE status = 2 AND date = "+currentDate, null);
        if(cur.moveToFirst())
        {
            sumReceived = String.valueOf(cur.getDouble(0));
            cur.close();
        }
        return sumReceived;
    }
    public String getSumSpent(){
        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" =? AND "+ MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS+" =?";

        String[] ARGS = {currentDate,"1"};
        MoneyDbHelper mDbHelper = new MoneyDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String str = "";
        String[] PROJECTION = {
                "SUM(value)"
        };
        Cursor cur = db.query(MoneyContract.MoneyEntry.TABLE_NAME,
                PROJECTION,
                SELECTION,
                ARGS,
                null,null,null);
//        Cursor cur = db.rawQuery("SELECT SUM(value) FROM today WHERE status = 1 AND date = "+currentDate, null);
        if(cur.moveToFirst())
        {
            str = String.valueOf(cur.getDouble(0));
            cur.close();
        }
        return str;
    }


}
