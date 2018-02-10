/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kinitoapps.moneymanager;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinitoapps.moneymanager.data.MoneyContract.MoneyEntry;
import com.kinitoapps.moneymanager.R;

/**
 * {@link MoneyCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class MoneyCursorAdapter extends CursorAdapter {



    /**
     * Constructs a new {@link MoneyCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public MoneyCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.value);
        TextView summaryTextView = view.findViewById(R.id.desc);
//        TextView statusTextView = view.findViewById(R.id.status);
        TextView timeTextView = view.findViewById(R.id.time);
        TextView dateTextView = view.findViewById(R.id.date);
        ImageView arrow_image = view.findViewById(R.id.arrow_image);

        // Find the columns of pet attributes that we're interested in
        int valueColumnIndex = cursor.getColumnIndex(MoneyEntry.COLUMN_MONEY_VALUE);
        int descColumnIndex = cursor.getColumnIndex(MoneyEntry.COLUMN_MONEY_DESC);
        int statusColumnIndex = cursor.getColumnIndex(MoneyEntry.COLUMN_MONEY_STATUS);
        int timeColumnIndex = cursor.getColumnIndex(MoneyEntry.COLUMN_MONEY_TIME);
        int dateColumnIndex = cursor.getColumnIndex(MoneyEntry.COLUMN_MONEY_DATE);

        // Read the pet attributes from the Cursor for the current pet
        double value = cursor.getDouble(valueColumnIndex);
        String desc = cursor.getString(descColumnIndex);
        int status = cursor.getInt(statusColumnIndex);
        String time = cursor.getString(timeColumnIndex);
        String date = cursor.getString(dateColumnIndex);
        String dateWithMonthName = date.substring(0,3)+getMonthName(date.substring(3,5))+date.substring(5);
        String str = String.valueOf(value);
        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(str);
        summaryTextView.setText(desc);
//        statusTextView.setText((status==1)?"Spent":"Received");
        arrow_image.setImageResource((status==1)?R.drawable.ic_action_down:R.drawable.ic_action_up);
        timeTextView.setText(time);
        dateTextView.setText(dateWithMonthName);
    }
    private String getMonthName(String month){
        if(month.equals("01"))
            return "Jan";
        else if(month.equals("02"))
            return "Feb";
        else if(month.equals("03"))
            return "Mar";
        else if(month.equals("04"))
            return "Apr";
        else if(month.equals("05"))
            return "May";
        else if(month.equals("06"))
            return "Jun";
        else if(month.equals("07"))
            return "Jul";
        else if(month.equals("08"))
            return "Aug";
        else if(month.equals("09"))
            return "Sep";
        else if(month.equals("10"))
            return "Oct";
        else if(month.equals("11"))
            return "Nov";
        return "Dec";
    }
}