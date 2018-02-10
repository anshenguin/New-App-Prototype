package com.kinitoapps.moneymanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kinitoapps.moneymanager.data.MoneyContract;
import com.kinitoapps.moneymanager.data.MoneyDbHelper;
import com.kinitoapps.moneymanager.piechart.PieGraph;
import com.kinitoapps.moneymanager.piechart.PieSlice;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public class YesterdayFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    // TODO: Rename parameter arguments, choose names that match
    private static final int MONEY_LOADER = 0;
    private static ArrayList<Long> mSelectedItemIds;
    MoneyCursorAdapter mCursorAdapter;
    private NonScrollListView moneyListView;
    private boolean isActionModeOn = false;

    private android.view.ActionMode mActionMode;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String currentDate;

    private OnFragmentInteractionListener mListener;

    public YesterdayFragment() {
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
        View root = inflater.inflate(R.layout.fragment_yesterday, container, false);
        // Inflate the layout for this fragment
        // Create and/or open a database to read from it

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
//        Cursor cursor = db.rawQuery("SELECT * FROM " + MoneyContract.MoneyEntry.TABLE_NAME, null);
//
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
        moneyListView = root.findViewById(R.id.list);
        mSelectedItemIds = new ArrayList<>();
        final PieGraph pg = root.findViewById(R.id.graph);
        pg.setInnerCircleRatio(160);
        boolean purpleValueGreater = false;
        PieSlice slice;
        slice = new PieSlice();
        slice.setColor(Color.parseColor("#F55454"));
        slice.setValue(Double.parseDouble(getSumReceived())>Double.parseDouble(getSumSpent())? (float) (Double.parseDouble(getSumReceived()) + Double.parseDouble(getSumSpent())) :0);
        if(slice.getValue()==0)
            purpleValueGreater = true;
        slice.setGoalValue((float) Double.parseDouble(getSumSpent()));
        pg.addSlice(slice);
        slice = new PieSlice();
        slice.setColor(Color.parseColor("#43C443"));
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
        final Animation slide = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);
        pg.setAnimationListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        moneyListView.setVisibility(View.VISIBLE);
                        moneyListView.startAnimation(slide);
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
                if(Float.parseFloat(valueAnimator.getAnimatedValue().toString())>=0&&
                        Float.parseFloat(valueAnimator.getAnimatedValue().toString())<1)
                    sum_spent.setText("0"+new DecimalFormat("#.00").format(valueAnimator.getAnimatedValue()));
                else
                    sum_spent.setText(new DecimalFormat("#.00").format(valueAnimator.getAnimatedValue()));

            }
        });
        valueAnimator.start();
        final ValueAnimator valueAnimator_two = ValueAnimator.ofFloat(0, (int)Double.parseDouble(getSumReceived()));
        valueAnimator_two.setDuration(1000);
        valueAnimator_two.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if(Float.parseFloat(valueAnimator_two.getAnimatedValue().toString())>=0&&
                        Float.parseFloat(valueAnimator_two.getAnimatedValue().toString())<1)
                    sum_received.setText("0"+new DecimalFormat("#.00").format(valueAnimator.getAnimatedValue()));
                else
                    sum_received.setText(new DecimalFormat("#.00").format(valueAnimator.getAnimatedValue()));
            }
        });

        valueAnimator_two.start();
        final ValueAnimator valueAnimator_three = ValueAnimator.ofFloat(0,(float)
                -Double.parseDouble(getSumSpent())+(float)Double.parseDouble(getSumReceived()));
        valueAnimator_three.setDuration(1000);
        valueAnimator_three.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if(Float.parseFloat(valueAnimator_three.getAnimatedValue().toString())>=0&&
                        Float.parseFloat(valueAnimator_three.getAnimatedValue().toString())<1)
                    sum_total.setText("0"+new DecimalFormat("#.00").format(valueAnimator.getAnimatedValue()));
                else
                    sum_total.setText(new DecimalFormat("#.00").format(valueAnimator.getAnimatedValue()));

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
        moneyListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(!doesContainThisItem(id,mSelectedItemIds)) {
                    view.setBackgroundColor(0x9934B5E4);
                    mSelectedItemIds.add(id);
                    if(mSelectedItemIds.size()==1)
                        mActionMode = getActivity().startActionMode(new ActionBarCallBack());
                    else {
                        updateTitle(mActionMode);
                    }
                }
                else {
                    if (mSelectedItemIds.size() != 0) {
                        view.setBackgroundColor(Color.TRANSPARENT);
                        mSelectedItemIds.remove(id);
                        updateTitle(mActionMode);
                        if (mSelectedItemIds.size() == 0)
                            mActionMode.finish();
                    }
                }
//                else{
//                    mActionMode.finish();
//                }
//                if (moneyListView.isItemChecked(position)){moneyListView.setItemChecked(position,false);}
//                else{moneyListView.setItemChecked(position,true);}
                return true;
            }
        });



        moneyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                if (moneyListView.isItemChecked(position)){moneyListView.setItemChecked(position,false);}else{moneyListView.setItemChecked(position,true);}
                if(mSelectedItemIds.size()==0) {
                    Intent intent = new Intent(getActivity(),SingleValueDetails.class);
                    Uri currentEntryUri = ContentUris.withAppendedId(MoneyContract.MoneyEntry.CONTENT_URI,id);
                    intent.setData(currentEntryUri);
                    startActivity(intent);
                }
                else if(!doesContainThisItem(id,mSelectedItemIds)){
                    view.setBackgroundColor(0x9934B5E4);
                    mSelectedItemIds.add(id);
                    updateTitle(mActionMode);

                }
                else{
                    view.setBackgroundColor(Color.TRANSPARENT);
                    mSelectedItemIds.remove(id);
                    updateTitle(mActionMode);
                    if(mSelectedItemIds.size()==0)
                        mActionMode.finish();

                }
            }
        });
//        setListViewHeightBasedOnChildren(moneyListView);
        getLoaderManager().initLoader(MONEY_LOADER,null,this);

        return root;

    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        if(getView() == null){
//            return;
//        }
//
//        getView().setFocusableInTouchMode(true);
//        getView().requestFocus();
//        getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK && mSelectedItemIds.size()!=0){
//                    // handle back button's click listener
//                    for(int i = 0 ; i < moneyListView.getCount() ; i++){
//                        moneyListView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
//                    }
//                    mSelectedItemIds.clear();
//                    mActionMode.finish();
//
//                    return true;
//                }
//                return false;
//            }
//        });
//    }

    private boolean doesContainThisItem(long l, ArrayList<Long> mSelectedItemIds) {
        for(Long p: mSelectedItemIds){
            if(p==l)
                return true;
        }
        return false;
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
        String date;
        String year;
        String yesterdayDate;
        String month;
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        if(!currentDate.substring(0,2).equals("01")) {
            date = String.valueOf(Integer.parseInt(currentDate.substring(0, 2)) - 1);
            String monthAndYear = currentDate.substring(2);
            if(Integer.parseInt(date)/10<1)
                yesterdayDate = "0"+date+monthAndYear;
            else
                yesterdayDate = date+monthAndYear;
            Log.v("lagging","not wali");
        }
        else{//new month started
            Log.v("lagging","new month started");

            //last month was feb
            if(currentDate.substring(3,5).equals("03")){
                Log.v("lagging","feb");
                //check for leap year
                int year_int = Integer.parseInt(currentDate.substring(6));
                if(year_int%4==0){
                    if(year_int%100==0){
                        if(year_int%400==0)
                        {
                            //leap year
                            month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                            year = currentDate.substring(5);
                            yesterdayDate="29-0"+month+year;
                        }
                        else{
                            month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                            year = currentDate.substring(5);
                            yesterdayDate="28-0"+month+year;

                            //not leap year
                        }
                    }
                    else{
                        //leap year
                        month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                        year = currentDate.substring(5);
                        yesterdayDate="29-0"+month+year;

                    }
                }
                else{
                    month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                    year = currentDate.substring(5);
                    yesterdayDate="28-0"+month+year;

                    //not leap year
                }
            }
            //last month was jan,mar,may,jul,aug,oct,dec
            else if(currentDate.substring(3,5).equals("02")||
                    currentDate.substring(3,5).equals("04")||
                    currentDate.substring(3,5).equals("06")||
                    currentDate.substring(3,5).equals("08")||
                    currentDate.substring(3,5).equals("09")||
                    currentDate.substring(3,5).equals("11")){
                Log.v("lagging","jan");
                month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                year = currentDate.substring(5);
                if(Integer.parseInt(month)/10<1)
                    yesterdayDate = "31-0"+month+year;
                else
                    yesterdayDate = "31-"+month+year;

            }

            else{
                Log.v("lagging","mar");
                month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                year = currentDate.substring(5);
                if(Integer.parseInt(month)/10<1)
                    yesterdayDate = "30-0"+month+year;
                else
                    yesterdayDate = "30-"+month+year;

            }
        }
        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" =?";
        String[] ARGS = {yesterdayDate};
        Log.v("date",MoneyContract.MoneyEntry.COLUMN_MONEY_DATE);
        Log.v("date",yesterdayDate);
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
                "_id DESC");
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
        String date;
        String year;
        String yesterdayDate;
        String month;
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        if(!currentDate.substring(0,2).equals("01")) {
            date = String.valueOf(Integer.parseInt(currentDate.substring(0, 2)) - 1);
            String monthAndYear = currentDate.substring(2);
            if(Integer.parseInt(date)/10<1)
                yesterdayDate = "0"+date+monthAndYear;
            else
                yesterdayDate = date+monthAndYear;
            Log.v("lagging","not wali");
        }
        else{//new month started
            Log.v("lagging","new month started");

            //last month was feb
            if(currentDate.substring(3,5).equals("03")){
                Log.v("lagging","feb");
                //check for leap year
                int year_int = Integer.parseInt(currentDate.substring(6));
                if(year_int%4==0){
                    if(year_int%100==0){
                        if(year_int%400==0)
                        {
                            //leap year
                            month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                            year = currentDate.substring(5);
                            yesterdayDate="29-0"+month+year;
                        }
                        else{
                            month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                            year = currentDate.substring(5);
                            yesterdayDate="28-0"+month+year;

                            //not leap year
                        }
                    }
                    else{
                        //leap year
                        month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                        year = currentDate.substring(5);
                        yesterdayDate="29-0"+month+year;

                    }
                }
                else{
                    month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                    year = currentDate.substring(5);
                    yesterdayDate="28-0"+month+year;

                    //not leap year
                }
            }
            //last month was jan,mar,may,jul,aug,oct,dec
            else if(currentDate.substring(3,5).equals("02")||
                    currentDate.substring(3,5).equals("04")||
                    currentDate.substring(3,5).equals("06")||
                    currentDate.substring(3,5).equals("08")||
                    currentDate.substring(3,5).equals("09")||
                    currentDate.substring(3,5).equals("11")){
                Log.v("lagging","jan");
                month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                year = currentDate.substring(5);
                if(Integer.parseInt(month)/10<1)
                    yesterdayDate = "31-0"+month+year;
                else
                    yesterdayDate = "31-"+month+year;

            }

            else{
                Log.v("lagging","mar");
                month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                year = currentDate.substring(5);
                if(Integer.parseInt(month)/10<1)
                    yesterdayDate = "30-0"+month+year;
                else
                    yesterdayDate = "30-"+month+year;

            }
        }
        String str = "";
        MoneyDbHelper mDbHelper = new MoneyDbHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] PROJECTION = {
                "SUM(value)"
        };
        String[] ARGS = {yesterdayDate,"1"};

        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" =? AND "+ MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS+" =?";
        Cursor cur = db.query(MoneyContract.MoneyEntry.TABLE_NAME,
                PROJECTION,
                SELECTION,
                ARGS,
                null,null,null);
        if(cur.moveToFirst())
        {
            str = String.valueOf(cur.getDouble(0));
            cur.close();
        }
        return str;
    }

    //TODO: CHECKING FOR YESTERDAYS WONT WORK ON START OF A NEW MONTH, PLEASE WRITE BETTER CODE DUMBASS
    public String getSumReceived() {
        String date;
        String year;
        String yesterdayDate;
        String month;
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        if(!currentDate.substring(0,2).equals("01")) {
            date = String.valueOf(Integer.parseInt(currentDate.substring(0, 2)) - 1);
            String monthAndYear = currentDate.substring(2);
            if(Integer.parseInt(date)/10<1)
                yesterdayDate = "0"+date+monthAndYear;
            else
                yesterdayDate = date+monthAndYear;
            Log.v("lagging","not wali");
        }
        else{//new month started
            Log.v("lagging","new month started");

            //last month was feb
            if(currentDate.substring(3,5).equals("03")){
                Log.v("lagging","feb");
                //check for leap year
                int year_int = Integer.parseInt(currentDate.substring(6));
                if(year_int%4==0){
                    if(year_int%100==0){
                        if(year_int%400==0)
                        {
                            //leap year
                            month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                            year = currentDate.substring(5);
                            yesterdayDate="29-0"+month+year;
                        }
                        else{
                            month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                            year = currentDate.substring(5);
                            yesterdayDate="28-0"+month+year;

                            //not leap year
                        }
                    }
                    else{
                        //leap year
                        month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                        year = currentDate.substring(5);
                        yesterdayDate="29-0"+month+year;

                    }
                }
                else{
                    month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                    year = currentDate.substring(5);
                    yesterdayDate="28-0"+month+year;

                    //not leap year
                }
            }
            //last month was jan,mar,may,jul,aug,oct,dec
            else if(currentDate.substring(3,5).equals("02")||
                    currentDate.substring(3,5).equals("04")||
                    currentDate.substring(3,5).equals("06")||
                    currentDate.substring(3,5).equals("08")||
                    currentDate.substring(3,5).equals("09")||
                    currentDate.substring(3,5).equals("11")){
                Log.v("lagging","jan");
                month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                year = currentDate.substring(5);
                if(Integer.parseInt(month)/10<1)
                    yesterdayDate = "31-0"+month+year;
                else
                    yesterdayDate = "31-"+month+year;

            }

            else{
                Log.v("lagging","mar");
                month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                year = currentDate.substring(5);
                if(Integer.parseInt(month)/10<1)
                    yesterdayDate = "30-0"+month+year;
                else
                    yesterdayDate = "30-"+month+year;

            }
        }
        String sumReceived = "";
        MoneyDbHelper mDbHelper = new MoneyDbHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] PROJECTION = {
                "SUM(value)"
        };
        String[] ARGS = {yesterdayDate,"2"};

        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" =? AND "+ MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS+" =?";
        Cursor cur=null;
        try {
            cur = db.query(MoneyContract.MoneyEntry.TABLE_NAME,
                    PROJECTION,
                    SELECTION,
                    ARGS,
                    null, null, null);
        }catch (SQLiteException e){
            if (e.getMessage().contains("no such table")){
//                SharedPreferences databaseversion = getActivity().getSharedPreferences("DBVER", Context.MODE_PRIVATE);
//                int DATABASE_VERSION = databaseversion.getInt("DATABASE_VERSION",0);
//                ++DATABASE_VERSION;
//                databaseversion.edit().putInt("DATABASE_VERSION",DATABASE_VERSION).commit();
//                mDbHelper = new MoneyDbHelper(getActivity());
//                db = mDbHelper.getReadableDatabase();
                String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + MoneyContract.MoneyEntry.TABLE_NAME + " ("
                        + MoneyContract.MoneyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE + " DOUBLE NOT NULL, "
                        + MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS + " INTEGER NOT NULL DEFAULT 0, "
                        + MoneyContract.MoneyEntry.COLUMN_MONEY_DESC + " TEXT, "
                        + MoneyContract.MoneyEntry.COLUMN_MONEY_DATE + " TEXT, "
                        + MoneyContract.MoneyEntry.COLUMN_MONEY_TIME + " TEXT);";

                // Execute the SQL statement
                db.execSQL(SQL_CREATE_PETS_TABLE);
                cur = db.query(MoneyContract.MoneyEntry.TABLE_NAME,
                        PROJECTION,
                        SELECTION,
                        ARGS,
                        null, null, null);
                // create table
                // re-run query, etc.

            }
        }
        if(cur.moveToFirst())
        {
            sumReceived = String.valueOf(cur.getDouble(0));
            cur.close();
        }
        return sumReceived;
    }

    public boolean noEntriesExist(){
        String date;
        String year;
        String yesterdayDate;
        String month;
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        if(!currentDate.substring(0,2).equals("01")) {
            date = String.valueOf(Integer.parseInt(currentDate.substring(0, 2)) - 1);
            String monthAndYear = currentDate.substring(2);
            if(Integer.parseInt(date)/10<1)
                yesterdayDate = "0"+date+monthAndYear;
            else
                yesterdayDate = date+monthAndYear;
            Log.v("lagging","not wali");
        }
        else{//new month started
            Log.v("lagging","new month started");

            //last month was feb
            if(currentDate.substring(3,5).equals("03")){
                Log.v("lagging","feb");
                //check for leap year
                int year_int = Integer.parseInt(currentDate.substring(6));
                if(year_int%4==0){
                    if(year_int%100==0){
                        if(year_int%400==0)
                        {
                            //leap year
                            month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                            year = currentDate.substring(5);
                            yesterdayDate="29-0"+month+year;
                        }
                        else{
                            month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                            year = currentDate.substring(5);
                            yesterdayDate="28-0"+month+year;

                            //not leap year
                        }
                    }
                    else{
                        //leap year
                        month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                        year = currentDate.substring(5);
                        yesterdayDate="29-0"+month+year;

                    }
                }
                else{
                    month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                    year = currentDate.substring(5);
                    yesterdayDate="28-0"+month+year;

                    //not leap year
                }
            }
            //last month was jan,mar,may,jul,aug,oct,dec
            else if(currentDate.substring(3,5).equals("02")||
                    currentDate.substring(3,5).equals("04")||
                    currentDate.substring(3,5).equals("06")||
                    currentDate.substring(3,5).equals("08")||
                    currentDate.substring(3,5).equals("09")||
                    currentDate.substring(3,5).equals("11")){
                Log.v("lagging","jan");
                month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                year = currentDate.substring(5);
                if(Integer.parseInt(month)/10<1)
                    yesterdayDate = "31-0"+month+year;
                else
                    yesterdayDate = "31-"+month+year;

            }

            else{
                Log.v("lagging","mar");
                month = String.valueOf(Integer.parseInt(currentDate.substring(3,5))-1);
                year = currentDate.substring(5);
                if(Integer.parseInt(month)/10<1)
                    yesterdayDate = "30-0"+month+year;
                else
                    yesterdayDate = "30-"+month+year;

            }
        }
        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" =?";
        String[] ARGS = {yesterdayDate};
        MoneyDbHelper mDbHelper = new MoneyDbHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int numRows = (int)DatabaseUtils.queryNumEntries(db, MoneyContract.MoneyEntry.TABLE_NAME,SELECTION,ARGS);
        if(numRows == 0)
            return true;
        else return false;
    }
    private void deletePet() {
        int rowsDeleted = 0;
        for(Long id:mSelectedItemIds){
            Uri currentEntryUri = ContentUris.withAppendedId(MoneyContract.MoneyEntry.CONTENT_URI, id);
            Log.v("lag",String.valueOf(currentEntryUri));
            Log.v("lag",String.valueOf(id));
            rowsDeleted = getActivity().getApplicationContext().getContentResolver().delete(currentEntryUri, null, null);

        }


        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(getActivity(), "Deletion Failed",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(getActivity(), "Deleted Successfully",
                    Toast.LENGTH_SHORT).show();
        }

        mActionMode.finish();

        android.support.v4.app.Fragment fragment = null;
        Class fragmentClass = null;

        fragmentClass = YesterdayFragment.class;
        try {
            fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.flContent, fragment).commit();
        // Close the activity
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(mSelectedItemIds.size()>1)
            builder.setMessage("Do you want to delete these entries?");
        else
            builder.setMessage("Do you want to delete this entry?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    class ActionBarCallBack implements android.view.ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.toolbar_cab, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
            isActionModeOn = true;
            mode.setTitle(String.valueOf(mSelectedItemIds.size()));
            ((home) getActivity()).disableDrawer();
            return false;
        }

        @Override
        public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.action_delete:
                    showDeleteConfirmationDialog();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(android.view.ActionMode mode) {
            isActionModeOn = false;
            mSelectedItemIds.clear();
            makeAllItemsWhite();
            ((home) getActivity()).enableDrawer();

        }

    }
    public void updateTitle(android.view.ActionMode mode){
        mode.setTitle(String.valueOf(mSelectedItemIds.size()));
    }

    public void makeAllItemsWhite() {
        for(int i = 0 ; i < moneyListView.getCount() ; i++){
            moneyListView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(isActionModeOn)
            mActionMode.finish();
    }
}
