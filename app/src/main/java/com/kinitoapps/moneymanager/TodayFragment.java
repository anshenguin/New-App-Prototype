package com.kinitoapps.moneymanager;

import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.kinitoapps.moneymanager.data.MoneyContract;
import com.kinitoapps.moneymanager.data.MoneyDbHelper;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TodayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TodayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodayFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    // TODO: Rename parameter arguments, choose names that match
    private static final int MONEY_LOADER = 0;
    MoneyCursorAdapter mCursorAdapter;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TodayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TodayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TodayFragment newInstance(String param1, String param2) {
        TodayFragment fragment = new TodayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        // Inflate the layout for this fragment
//        MoneyDbHelper mDbHelper = new MoneyDbHelper(getActivity());
//
//        // Create and/or open a database to read from it
//
//        // Perform this raw SQL query "SELECT * FROM pets"
//        // to get a Cursor that contains all rows from the pets table.
////        Cursor cursor = db.rawQuery("SELECT * FROM " + MoneyContract.MoneyEntry.TABLE_NAME, null);
//
////        String[] projection = {
////                MoneyContract.MoneyEntry._ID,
////                MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE,
////                MoneyContract.MoneyEntry.COLUMN_MONEY_DESC,
////                MoneyContract.MoneyEntry.COLUMN_MONEY_DATE,
////                MoneyContract.MoneyEntry.COLUMN_MONEY_TIME,
////                MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS
////        };
////
////        Cursor cursor = db.query(
////                MoneyContract.MoneyEntry.TABLE_NAME,
////                projection,
////                null,
////                null,
////                null,
////                null,
////                null
////        );
//        Toast.makeText(getActivity(),"onResume",Toast.LENGTH_LONG).show();
//        ListView moneyListView = getView().findViewById(R.id.list);
//        View emptyView = getView().findViewById(R.id.empty_view);
//        moneyListView.setEmptyView(emptyView);
////        MoneyCursorAdapter adapter = new MoneyCursorAdapter(getActivity(), cursor);
////        moneyListView.setAdapter(adapter);
//////        TextView displayView = root.findViewById(R.id.root);
//////
//////        try {
//////            // Create a header in the Text View that looks like this:
//////            //
//////            // The pets table contains <number of rows in Cursor> pets.
//////            // _id - name - breed - gender - weight
//////            //
//////            // In the while loop below, iterate through the rows of the cursor and display
//////            // the information from each column in this order.
//////            displayView.setText("The pets table contains " + cursor.getCount() + " pets.\n\n");
//////            displayView.append(MoneyContract.MoneyEntry._ID + " - " +
//////                                        MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE + " - " +
//////                                        MoneyContract.MoneyEntry.COLUMN_MONEY_DESC + " - " +
//////                                        MoneyContract.MoneyEntry.COLUMN_MONEY_DATE + " - " +
//////                                        MoneyContract.MoneyEntry.COLUMN_MONEY_TIME + " - " +
//////                                        MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS + "\n");
//////
//////            // Figure out the index of each column
//////            int idColumnIndex = cursor.getColumnIndex(MoneyContract.MoneyEntry._ID);
//////                        int valueColumnIndex = cursor.getColumnIndex(MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE);
//////                        int descColumnIndex = cursor.getColumnIndex(MoneyContract.MoneyEntry.COLUMN_MONEY_DESC);
//////                        int dateColumnIndex = cursor.getColumnIndex(MoneyContract.MoneyEntry.COLUMN_MONEY_DATE);
//////                        int timeColumnIndex = cursor.getColumnIndex(MoneyContract.MoneyEntry.COLUMN_MONEY_TIME);
//////                        int statusColumnIndex = cursor.getColumnIndex(MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS);
//////
//////
//////            // Iterate through all the returned rows in the cursor
//////            while (cursor.moveToNext()) {
//////                                // Use that index to extract the String or Int value of the word
//////                               // at the current row the cursor is on.
//////                               int currentID = cursor.getInt(idColumnIndex);
//////                                int currentValue = cursor.getInt(valueColumnIndex);
//////                                String currentDesc = cursor.getString(descColumnIndex);
//////                                String currentDate = cursor.getString(dateColumnIndex);
//////                                String currentTime = cursor.getString(timeColumnIndex);
//////                                String currentStatus = cursor.getString(statusColumnIndex);
//////
//////                // Display the values from each column of the current row in the cursor in the TextView
//////                                displayView.append(("\n" + currentID + " - " +
//////                              currentValue + " - " +
//////                                        currentDesc + " - " +
//////                                        currentDate + " - " +
//////                                        currentTime + " - " +
//////                                        currentStatus));
//////                            }
//////        } finally {
//////            // Always close the cursor when you're done reading from it. This releases all its
//////            // resources and makes it invalid.0
//////            cursor.close();
//////        }
//        mCursorAdapter = new MoneyCursorAdapter(getActivity(),null);
//        moneyListView.setAdapter(mCursorAdapter);
//        getLoaderManager().initLoader(MONEY_LOADER,null,this);
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_today, container, false);
        // Inflate the layout for this fragment
        MoneyDbHelper mDbHelper = new MoneyDbHelper(getActivity());

        // Create and/or open a database to read from it

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
//        Cursor cursor = db.rawQuery("SELECT * FROM " + MoneyContract.MoneyEntry.TABLE_NAME, null);

//        String[] projection = {
//                MoneyContract.MoneyEntry._ID,
//                MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE,
//                MoneyContract.MoneyEntry.COLUMN_MONEY_DESC,
//                MoneyContract.MoneyEntry.COLUMN_MONEY_DATE,
//                MoneyContract.MoneyEntry.COLUMN_MONEY_TIME,
//                MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS
//        };
//
//        Cursor cursor = db.query(
//                MoneyContract.MoneyEntry.TABLE_NAME,
//                projection,
//                null,
//                null,
//                null,
//                null,
//                null
//        );

        ListView moneyListView = root.findViewById(R.id.list);
        TextView sum_spent = root.findViewById(R.id.sum_spent);
        sum_spent.setText(getSumSpent());
        TextView sum_received = root.findViewById(R.id.sum_received);
        sum_received.setText(getSumReceived());

        View emptyView = root.findViewById(R.id.empty_view);
        moneyListView.setEmptyView(emptyView);
//        MoneyCursorAdapter adapter = new MoneyCursorAdapter(getActivity(), cursor);
//        moneyListView.setAdapter(adapter);
////        TextView displayView = root.findViewById(R.id.root);
////
////        try {
////            // Create a header in the Text View that looks like this:
////            //
////            // The pets table contains <number of rows in Cursor> pets.
////            // _id - name - breed - gender - weight
////            //
////            // In the while loop below, iterate through the rows of the cursor and display
////            // the information from each column in this order.
////            displayView.setText("The pets table contains " + cursor.getCount() + " pets.\n\n");
////            displayView.append(MoneyContract.MoneyEntry._ID + " - " +
////                                        MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE + " - " +
////                                        MoneyContract.MoneyEntry.COLUMN_MONEY_DESC + " - " +
////                                        MoneyContract.MoneyEntry.COLUMN_MONEY_DATE + " - " +
////                                        MoneyContract.MoneyEntry.COLUMN_MONEY_TIME + " - " +
////                                        MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS + "\n");
////
////            // Figure out the index of each column
////            int idColumnIndex = cursor.getColumnIndex(MoneyContract.MoneyEntry._ID);
////                        int valueColumnIndex = cursor.getColumnIndex(MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE);
////                        int descColumnIndex = cursor.getColumnIndex(MoneyContract.MoneyEntry.COLUMN_MONEY_DESC);
////                        int dateColumnIndex = cursor.getColumnIndex(MoneyContract.MoneyEntry.COLUMN_MONEY_DATE);
////                        int timeColumnIndex = cursor.getColumnIndex(MoneyContract.MoneyEntry.COLUMN_MONEY_TIME);
////                        int statusColumnIndex = cursor.getColumnIndex(MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS);
////
////
////            // Iterate through all the returned rows in the cursor
////            while (cursor.moveToNext()) {
////                                // Use that index to extract the String or Int value of the word
////                               // at the current row the cursor is on.
////                               int currentID = cursor.getInt(idColumnIndex);
////                                int currentValue = cursor.getInt(valueColumnIndex);
////                                String currentDesc = cursor.getString(descColumnIndex);
////                                String currentDate = cursor.getString(dateColumnIndex);
////                                String currentTime = cursor.getString(timeColumnIndex);
////                                String currentStatus = cursor.getString(statusColumnIndex);
////
////                // Display the values from each column of the current row in the cursor in the TextView
////                                displayView.append(("\n" + currentID + " - " +
////                              currentValue + " - " +
////                                        currentDesc + " - " +
////                                        currentDate + " - " +
////                                        currentTime + " - " +
////                                        currentStatus));
////                            }
////        } finally {
////            // Always close the cursor when you're done reading from it. This releases all its
////            // resources and makes it invalid.0
////            cursor.close();
////        }
        mCursorAdapter = new MoneyCursorAdapter(getActivity(),null);
        moneyListView.setAdapter(mCursorAdapter);
        getLoaderManager().initLoader(MONEY_LOADER,null,this);
        return root;

    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                MoneyContract.MoneyEntry._ID,
                MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE,
                MoneyContract.MoneyEntry.COLUMN_MONEY_DESC,
                MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS,
                MoneyContract.MoneyEntry.COLUMN_MONEY_TIME,
                MoneyContract.MoneyEntry.COLUMN_MONEY_DATE
        };

        return new CursorLoader(getActivity(),
                MoneyContract.MoneyEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public String getSumSpent(){
        MoneyDbHelper mDbHelper = new MoneyDbHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String str = "";
        Cursor cur = db.rawQuery("SELECT SUM(value) FROM today WHERE status = 1", null);
        if(cur.moveToFirst())
        {
            str = String.valueOf(cur.getInt(0));
            cur.close();
        }
        return str;
    }

    public String getSumReceived() {
        String sumReceived = "";
        MoneyDbHelper mDbHelper = new MoneyDbHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT SUM(value) FROM today WHERE status = 2", null);
        if(cur.moveToFirst())
        {
            sumReceived = String.valueOf(cur.getInt(0));
            cur.close();
        }
        return sumReceived;
    }
}
