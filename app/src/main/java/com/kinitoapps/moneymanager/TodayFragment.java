package com.kinitoapps.moneymanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kinitoapps.moneymanager.data.MoneyContract;
import com.kinitoapps.moneymanager.data.MoneyDbHelper;
import com.kinitoapps.moneymanager.piechart.PieGraph;
import com.kinitoapps.moneymanager.piechart.PieSlice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


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
    private String currentDate;

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

//
//    @Override
//    public void onResume() {
//        super.onResume();
//        Toast.makeText(getActivity(),"onResume",Toast.LENGTH_LONG).show();
//
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        Toast.makeText(getActivity(),"onPause",Toast.LENGTH_LONG).show();
//
//    }
//    @Override
//    public void onStart(){
//        super.onStart();
//        Toast.makeText(getActivity(),"onStart",Toast.LENGTH_LONG).show();
//
//    }
//
//    @Override
//    public void onStop(){
//        super.onStop();
//        Toast.makeText(getActivity(),"onStop",Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onDestroyView(){
//        super.onDestroyView();
//        Toast.makeText(getActivity(),"onDestroyView",Toast.LENGTH_LONG).show();
//
//    }


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
        final NonScrollListView moneyListView = root.findViewById(R.id.list);
        final PieGraph pg = root.findViewById(R.id.graph);
        pg.setInnerCircleRatio(140);
        boolean purpleValueGreater = false;
        PieSlice slice;
        slice = new PieSlice();
        slice.setColor(Color.parseColor("#FFBB33"));

        slice.setValue(Double.parseDouble(getSumReceived())>Double.parseDouble(getSumSpent())? (float) (Double.parseDouble(getSumReceived()) + Double.parseDouble(getSumSpent())) :0);
        if(slice.getValue()==0)
            purpleValueGreater = true;
        slice.setGoalValue((float) Double.parseDouble(getSumSpent()));
        pg.addSlice(slice);
        slice = new PieSlice();
        slice.setColor(Color.parseColor("#AA66CC"));
        slice.setValue(purpleValueGreater? (float) (Double.parseDouble(getSumReceived()) + Double.parseDouble(getSumSpent())) :0);
        slice.setGoalValue((float) Double.parseDouble(getSumReceived()));
        pg.addSlice(slice);
        final TextView sum_spent = root.findViewById(R.id.sum_spent);
        sum_spent.setText("0");
        final TextView sum_received = root.findViewById(R.id.sum_received);
        sum_received.setText("0");
        final TextView sum_total = root.findViewById(R.id.total);
        sum_total.setText("0");
//        pg.setInterpolator(new DecelerateInterpolator());
        pg.setDuration(1000);//default if unspecified is 300 ms
        pg.setAnimationListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        moneyListView.setVisibility(View.VISIBLE);
                        moneyListView.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.enter_from_left));
                    }
                }, 50);

            }


        });

        pg.animateToGoalValues();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, (float) Double.parseDouble(getSumSpent()));
        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                sum_spent.setText(valueAnimator.getAnimatedValue().toString());
            }
        });
        valueAnimator.start();
        ValueAnimator valueAnimator_two = ValueAnimator.ofFloat(0, (float)Double.parseDouble(getSumReceived()));
        valueAnimator_two.setDuration(1000);
        valueAnimator_two.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                sum_received.setText(valueAnimator.getAnimatedValue().toString());
            }
        });

        valueAnimator_two.start();
        ValueAnimator valueAnimator_three = ValueAnimator.ofFloat(0,(float)
                -Double.parseDouble(getSumSpent())+(float)Double.parseDouble(getSumReceived()));
        valueAnimator_three.setDuration(1000);
        valueAnimator_three.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                sum_total.setText(valueAnimator.getAnimatedValue().toString());
            }
        });
        valueAnimator_three.start();
        View emptyView = root.findViewById(R.id.empty_view);
//        moneyListView.setEmptyView(emptyView);
        LinearLayout pieChart = root.findViewById(R.id.pie_chart);
        if(noEntriesExist()) {
            pieChart.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            pieChart.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }



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
//        setListViewHeightBasedOnChildren(moneyListView);
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
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" =?";
        String[] ARGS = {currentDate};
        Log.v("date",MoneyContract.MoneyEntry.COLUMN_MONEY_DATE);
        Log.v("date",currentDate);
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
                SELECTION,
                ARGS,
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
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" =? AND "+ MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS+" =?";

        String[] ARGS = {currentDate,"1"};
        MoneyDbHelper mDbHelper = new MoneyDbHelper(getActivity());
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

    public String getSumReceived() {
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" =? AND "+ MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS+" =?";

        String[] ARGS = {currentDate,"2"};
        MoneyDbHelper mDbHelper = new MoneyDbHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sumReceived = "";
        String[] PROJECTION = {
                "SUM(value)"
        };
        Cursor cur = db.query(MoneyContract.MoneyEntry.TABLE_NAME,
                PROJECTION,
                SELECTION,
                ARGS,
                null,null,null);
//        Cursor cur = db.rawQuery("SELECT SUM(value) FROM today WHERE status = 2 AND date = "+currentDate, null);
        if(cur.moveToFirst())
        {
            sumReceived = String.valueOf(cur.getDouble(0));
            cur.close();
        }
        return sumReceived;
    }

    public boolean noEntriesExist(){
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" =?";
        String[] ARGS = {currentDate};
        MoneyDbHelper mDbHelper = new MoneyDbHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int numRows = (int)DatabaseUtils.queryNumEntries(db, MoneyContract.MoneyEntry.TABLE_NAME,SELECTION,ARGS);
        if(numRows == 0)
        return true;
        else return false;
    }
}
