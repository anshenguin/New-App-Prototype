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
import android.os.Handler;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager;
import android.content.Context;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
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
public class ThisYearFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    // TODO: Rename parameter arguments, choose names that match
    private static final int MONEY_LOADER = 0;
    private static ArrayList<Long> mSelectedItemIds;
    MoneyCursorAdapter mCursorAdapter;
    private AdView adView;
    private NonScrollListView moneyListView;
    private boolean isActionModeOn = false;
    private android.view.ActionMode mActionMode;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView curDate;
    AppCompatTextView sum_spent,sum_received, sum_total;
    NestedScrollView pieChart;
    Animation slide;
    ImageView nextDate,previousDate;
    PieGraph pg;
    Boolean toEnd;
    int currentYear;
    View emptyView;
    private String currentDate;

    private OnFragmentInteractionListener mListener;

    public ThisYearFragment() {
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
    public static ThisYearFragment newInstance(String param1, String param2) {
        ThisYearFragment fragment = new ThisYearFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }



    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_this_year, container, false);
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        currentYear = Integer.parseInt(currentDate.substring(6));
        curDate = root.findViewById(R.id.currentDate);
        curDate.setText(String.valueOf(currentYear));
        mSelectedItemIds = new ArrayList<>();
        pieChart = root.findViewById(R.id.pie_graph);
        moneyListView = root.findViewById(R.id.list);
        nextDate = root.findViewById(R.id.imageViewRight);
        previousDate = root.findViewById(R.id.imageViewLeft);
        pg = root.findViewById(R.id.graph);
        sum_total = root.findViewById(R.id.total);
        emptyView = root.findViewById(R.id.empty_view);
        sum_spent = root.findViewById(R.id.sum_spent);
        sum_received = root.findViewById(R.id.sum_received);
        ConstraintLayout coordinatorLayout = root.findViewById(R.id.topbardates);
        ViewCompat.setElevation(coordinatorLayout,2);
        try {
            startMainThread();
        }finally {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    try {
                        if (getActivity() != null) {
                            adView = new AdView(getActivity(), "174139459886109_174180053215383", AdSize.BANNER_HEIGHT_50);
                            // Find the Ad Container
                            // Add the ad view to your activity layout
                            LinearLayout adContainer = root.findViewById(R.id.banner_container);
                            adContainer.addView(adView);

                            // Request an ad
                            adView.loadAd();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, 1500);



        }
        nextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incDecYear(1);
            }
        });

        previousDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incDecYear(-1);
            }
        });


        curDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Year");
                View v = inflater.inflate(R.layout.number_picker,null);
                builder.setView(v);
                final NumberPicker picker = v.findViewById(R.id.pickpick);

                picker.setMinValue(1970);
                picker.setMaxValue(2200);
                picker.setValue(currentYear);
                picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        currentYear = picker.getValue();
                        startMainThread();
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setCancelable(true);
                builder.show();

            }
        });
        emptyView = root.findViewById(R.id.empty_view);
        if(noEntriesExist()) {
            pieChart.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            pieChart.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
        return root;

    }

    private void incDecYear(int i) {
        pg.cancelAnimating();
        pg.setDuration(0);
        moneyListView.setVisibility(View.INVISIBLE);
        if(i<0) {
            if (currentYear > 1970)
                currentYear--;
        }
        else {
            if (currentYear < 2200)
                currentYear++;
        }

        startMainThread();
    }

    private boolean doesContainThisItem(long l, ArrayList<Long> mSelectedItemIds) {
        for(Long p: mSelectedItemIds){
            if(p==l)
                return true;
        }
        return false;
    }


    private void startMainThread() {
        moneyListView.setVisibility(View.GONE);
        curDate.setText(String.valueOf(currentYear));
        pg.removeSlices();
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
        sum_spent.setText("0");
        sum_received.setText("0");
        sum_total.setText("0");
//        pg.setInterpolator(new DecelerateInterpolator());
        pg.setDuration(1000);//default if unspecified is 300 ms
        slide = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);
        slide.setDuration(700);
        toEnd = true;
        pg.animateToGoalValues();
        pg.setAnimationListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if(toEnd) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            moneyListView.setVisibility(View.VISIBLE);
                            moneyListView.startAnimation(slide);
                        }
                    }, 50);
                }






            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                toEnd = false;
            }

        });

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

//        moneyListView.setEmptyView(emptyView);
        if(noEntriesExist()) {
            pieChart.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            pieChart.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }



        mCursorAdapter = new MoneyCursorAdapter(getActivity(),null);
        moneyListView.setAdapter(mCursorAdapter);
        mCursorAdapter.notifyDataSetChanged();
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
        getLoaderManager().restartLoader(MONEY_LOADER,null,this);

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

        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" LIKE?";
        String[] ARGS = {"%"+currentYear};
        String[] projection = {
                MoneyContract.MoneyEntry._ID,
                MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE,
                MoneyContract.MoneyEntry.COLUMN_MONEY_DESC,
                MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS,
                MoneyContract.MoneyEntry.COLUMN_MONEY_TIME,
                MoneyContract.MoneyEntry.COLUMN_MONEY_DATE,
                "CASE WHEN LENGTH(time)=8 THEN time ELSE '0'||time END AS TWELVEHR",
                "substr(date,7,4)||'-'||substr(date,4,2)||'-'||substr(date,1,2) AS sortabledate"


        };

        String sortorder = "sortabledate desc, case when substr(TWELVEHR,1,2)='12' then substr(TWELVEHR,7)||'00'||substr(TWELVEHR,3) else substr(TWELVEHR,7)||substr(TWELVEHR, 1, 5) end DESC";
        return new CursorLoader(getActivity(),
                MoneyContract.MoneyEntry.CONTENT_URI,
                projection,
                SELECTION,
                ARGS,
                sortorder);
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
//        String date = String.valueOf(Integer.parseInt(currentDate.substring(0,2))-1);
        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" LIKE? AND "+ MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS+" =?";
        String[] ARGS = {"%"+currentYear,"1"};
        String str = "";
        MoneyDbHelper mDbHelper = new MoneyDbHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] PROJECTION = {
                "SUM(value)"
        };
        Cursor cur = db.query(MoneyContract.MoneyEntry.TABLE_NAME,
                PROJECTION,
                SELECTION,
                ARGS,
                null,null,null);
//        Cursor cur = db.rawQuery("SELECT SUM(value) FROM today WHERE status = 1 AND date LIKE %"+year, null);
        if(cur.moveToFirst())
        {
            str = String.valueOf(cur.getDouble(0));
            cur.close();
        }
        return str;
    }

    public String getSumReceived() {
//        String date = String.valueOf(Integer.parseInt(currentDate.substring(0,2))-1);
        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" LIKE? AND "+ MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS+" =?";
        String[] ARGS = {"%"+currentYear,"2"};
        String sumReceived = "";
        MoneyDbHelper mDbHelper = new MoneyDbHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
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
                        + "\""+MoneyContract.MoneyEntry.COLUMN_MONEY_DESC +"\""+ " TEXT, "
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
//        Cursor cur = db.rawQuery("SELECT SUM(value) FROM today WHERE status = 2 AND date LIKE %"+year, null);
        if(cur.moveToFirst())
        {
            sumReceived = String.valueOf(cur.getDouble(0));
            cur.close();
        }
        return sumReceived;
    }

    public boolean noEntriesExist(){
//        String date = String.valueOf(Integer.parseInt(currentDate.substring(0,2))-1);
        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" LIKE?";
        String[] ARGS = {"%"+currentYear};
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

        androidx.fragment.app.Fragment fragment = null;
        Class fragmentClass = null;

        fragmentClass = ThisYearFragment.class;
        try {
            fragment = (androidx.fragment.app.Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left).replace(R.id.flContent, fragment).commit();
        // Close the activity
    }

    private void showDeleteConfirmationDialog() {
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
    @Override
    public void onResume() {
        super.onResume();
        startMainThread();
    }
}
