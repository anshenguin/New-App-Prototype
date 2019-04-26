package com.kinitoapps.moneymanager;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatTextView;
import com.google.android.material.card.MaterialCardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.kinitoapps.moneymanager.data.MoneyContract;
import com.kinitoapps.moneymanager.data.MoneyDbHelper;
import com.kinitoapps.moneymanager.piechart.PieGraph;
import com.kinitoapps.moneymanager.piechart.PieSlice;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OverviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverviewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private NativeAd nativeAd;
    private CardView nativeAdContainer;
    private LinearLayout  adView;
    View root;

    private void showNativeAd() {
        nativeAd = new NativeAd(getActivity(), "174139459886109_174139646552757");
        nativeAd.setAdListener(new AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                // Ad error callback
            }

            @Override
            public void onAdLoaded(Ad ad) {

                if (nativeAd != null) {
                    nativeAd.unregisterView();
                }

                // Add the Ad view into the ad container.
                try {
                    if(getActivity()!=null) {
                        nativeAdContainer = (CardView) root.findViewById(R.id.native_ad_container);
                        LayoutInflater inflater = LayoutInflater.from(getActivity());
                        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
                        adView = (LinearLayout) inflater.inflate(R.layout.native_ad_card_layout, nativeAdContainer, false);

                        nativeAdContainer.addView(adView);

                        // Create native UI using the ad metadata.
                        ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.native_ad_icon);
                        TextView nativeAdTitle = (TextView) adView.findViewById(R.id.native_ad_title);
                        MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.native_ad_media);
                        TextView nativeAdSocialContext = (TextView) adView.findViewById(R.id.native_ad_social_context);
                        TextView nativeAdBody = (TextView) adView.findViewById(R.id.native_ad_body);
                        Button nativeAdCallToAction = (Button) adView.findViewById(R.id.native_ad_call_to_action);

                        // Set the Text.
                        nativeAdTitle.setText(nativeAd.getAdTitle());
                        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                        nativeAdBody.setText(nativeAd.getAdBody());
                        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());

                        // Download and display the ad icon.
                        NativeAd.Image adIcon = nativeAd.getAdIcon();
                        NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

                        // Download and display the cover image.
                        nativeAdMedia.setNativeAd(nativeAd);

                        // Add the AdChoices icon
                        LinearLayout adChoicesContainer = (LinearLayout) root.findViewById(R.id.ad_choices_container);
                        AdChoicesView adChoicesView = new AdChoicesView(getActivity(), nativeAd, true);
                        adChoicesContainer.addView(adChoicesView);

                        // Register the Title and CTA button to listen for clicks.
                        List<View> clickableViews = new ArrayList<>();
                        clickableViews.add(nativeAdTitle);
                        clickableViews.add(nativeAdCallToAction);
                        nativeAd.registerViewForInteraction(nativeAdContainer, clickableViews);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });

        // Request an ad
        nativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);
    }
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String currentDate;
    PieGraph pg;
    AppCompatTextView sum_received;
    AppCompatTextView sum_spent;
    AppCompatTextView sum_received_mon;
    AppCompatTextView sum_spent_year;
    AppCompatTextView sum_spent_mon;

    PieGraph pg_yes;
    PieSlice slice;
    AppCompatTextView sum_received_yes;
    AppCompatTextView sum_received_year;
    boolean purpleValueGreater = false;
    AppCompatTextView sum_spent_yes;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    public OverviewFragment() {
//         Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static OverviewFragment newInstance(String param1, String param2) {
        OverviewFragment fragment = new OverviewFragment();
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_overview, container, false);
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.bringToFront();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),EnterValueActivity.class));
            }
        });

        CardView card_today = root.findViewById(R.id.card_today);
        CardView card_yesterday = root.findViewById(R.id.card_yesterday);
        CardView card_this_month = root.findViewById(R.id.card_month);
        CardView card_this_year = root.findViewById(R.id.card_year);

        card_this_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((home)getActivity()).replaceFragmentToThisYear();


            }
        });

        card_this_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((home)getActivity()).replaceFragmentToThisMonth();

            }
        });

        card_yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((home)getActivity()).replaceFragmentToYesterday();

            }
        });

        card_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((home)getActivity()).replaceFragmentToToday();
            }
        });
        sum_spent = root.findViewById(R.id.sum_spent1);
        sum_received = root.findViewById(R.id.sum_received1);
        sum_spent.setText("0");
        sum_received.setText("0");
        sum_spent_yes = root.findViewById(R.id.sum_spent2);
        sum_received_yes = root.findViewById(R.id.sum_received2);
        sum_spent_yes.setText("0");
        sum_received_yes.setText("0");
        sum_spent_mon = root.findViewById(R.id.sum_spent3);
        sum_received_mon = root.findViewById(R.id.sum_received3);
        sum_spent_mon.setText("0");
        sum_received_mon.setText("0");
        sum_received_year = root.findViewById(R.id.sum_received4);
        sum_spent_year = root.findViewById(R.id.sum_spent4);
        sum_spent_year.setText("0");
        sum_received_year.setText("0");
        showNativeAd();

/**
 *
 * ANIMATION FOR TODAY CARD
 *
 *
 */
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, (float) Double.parseDouble(getSumDailySpent()));
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
        final ValueAnimator valueAnimator_two = ValueAnimator.ofFloat(0, (int)Double.parseDouble(getSumDailyReceived()));
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

        /**
         *
         * ANIMATION FOR YESTERDAY CARD
         *
         *
         */

        final ValueAnimator valueAnimator1 = ValueAnimator.ofFloat(0, (float) Double.parseDouble(getSumYesterdaySpent()));
        valueAnimator1.setDuration(1000);
        valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if(Float.parseFloat(valueAnimator1.getAnimatedValue().toString())>=0&&
                        Float.parseFloat(valueAnimator1.getAnimatedValue().toString())<1)
                    sum_spent_yes.setText("0"+new DecimalFormat("#.00").format(valueAnimator.getAnimatedValue()));
                else
                    sum_spent_yes.setText(new DecimalFormat("#.00").format(valueAnimator.getAnimatedValue()));

            }
        });
        valueAnimator1.start();
        final ValueAnimator valueAnimator_two1 = ValueAnimator.ofFloat(0, (int)Double.parseDouble(getSumYesterdayReceived()));
        valueAnimator_two1.setDuration(1000);
        valueAnimator_two1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if(Float.parseFloat(valueAnimator_two1.getAnimatedValue().toString())>=0&&
                        Float.parseFloat(valueAnimator_two1.getAnimatedValue().toString())<1)
                    sum_received_yes.setText("0"+new DecimalFormat("#.00").format(valueAnimator.getAnimatedValue()));
                else
                    sum_received_yes.setText(new DecimalFormat("#.00").format(valueAnimator.getAnimatedValue()));
            }
        });

        valueAnimator_two1.start();
//        final ValueAnimator valueAnimator_three1 = ValueAnimator.ofFloat(0,(float)
//                -Double.parseDouble(getSumYesterdaySpent())+(float)Double.parseDouble(getSumYesterdayReceived()));
//        valueAnimator_three1.setDuration(1000);
//        valueAnimator_three1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                if(Float.parseFloat(valueAnimator_three1.getAnimatedValue().toString())>=0&&
//                        Float.parseFloat(valueAnimator_three1.getAnimatedValue().toString())<1)
//                    sum_total_yes.setText("0"+new DecimalFormat("#.00").format(valueAnimator.getAnimatedValue()));
//                else
//                    sum_total_yes.setText(new DecimalFormat("#.00").format(valueAnimator.getAnimatedValue()));
//
//            }
//        });
//        valueAnimator_three1.start();

        /**
         *
         * ANIMATION FOR MONTHLY CARD
         *
         *
         */

        final ValueAnimator valueAnimator2 = ValueAnimator.ofFloat(0, (float) Double.parseDouble(getSumMonthlySpent()));
        valueAnimator2.setDuration(1000);
        valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if(Float.parseFloat(valueAnimator2.getAnimatedValue().toString())>=0&&
                        Float.parseFloat(valueAnimator2.getAnimatedValue().toString())<1)
                    sum_spent_mon.setText("0"+new DecimalFormat("#.00").format(valueAnimator.getAnimatedValue()));
                else
                    sum_spent_mon.setText(new DecimalFormat("#.00").format(valueAnimator.getAnimatedValue()));

            }
        });
        valueAnimator2.start();
        final ValueAnimator valueAnimator_two2 = ValueAnimator.ofFloat(0, (int)Double.parseDouble(getSumMonthlyReceived()));
        valueAnimator_two2.setDuration(1000);
        valueAnimator_two2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if(Float.parseFloat(valueAnimator_two2.getAnimatedValue().toString())>=0&&
                        Float.parseFloat(valueAnimator_two2.getAnimatedValue().toString())<1)
                    sum_received_mon.setText("0"+new DecimalFormat("#.00").format(valueAnimator.getAnimatedValue()));
                else
                    sum_received_mon.setText(new DecimalFormat("#.00").format(valueAnimator.getAnimatedValue()));
            }
        });

        valueAnimator_two2.start();

        /**
         *
         * ANIMATION FOR YEARLY CARD
         *
         *
         */

        final ValueAnimator valueAnimator3 = ValueAnimator.ofFloat(0, (float) Double.parseDouble(getSumYearSpent()));
        valueAnimator3.setDuration(1000);
        valueAnimator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if(Float.parseFloat(valueAnimator3.getAnimatedValue().toString())>=0&&
                        Float.parseFloat(valueAnimator3.getAnimatedValue().toString())<1)
                    sum_spent_year.setText("0"+new DecimalFormat("#.00").format(valueAnimator.getAnimatedValue()));
                else
                    sum_spent_year.setText(new DecimalFormat("#.00").format(valueAnimator.getAnimatedValue()));

            }
        });
        valueAnimator3.start();
        final ValueAnimator valueAnimator_two3 = ValueAnimator.ofFloat(0, (int)Double.parseDouble(getSumYearReceived()));
        valueAnimator_two3.setDuration(1000);
        valueAnimator_two3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if(Float.parseFloat(valueAnimator_two3.getAnimatedValue().toString())>=0&&
                        Float.parseFloat(valueAnimator_two3.getAnimatedValue().toString())<1)
                    sum_received_year.setText("0"+new DecimalFormat("#.00").format(valueAnimator.getAnimatedValue()));
                else
                    sum_received_year.setText(new DecimalFormat("#.00").format(valueAnimator.getAnimatedValue()));
            }
        });

        valueAnimator_two3.start();
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
    public String getSumDailySpent(){
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

    public String getSumDailyReceived() {
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" =? AND "+ MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS+" =?";

        String[] ARGS = {currentDate,"2"};
        MoneyDbHelper mDbHelper = new MoneyDbHelper(getActivity());
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
//                SharedPreferences databaseversion = getActivity().getSharedPreferences("DBVER", Context.MODE_PRIVATE);
//                int DATABASE_VERSION = databaseversion.getInt("DATABASE_VERSION",0);
//                ++DATABASE_VERSION;
//                databaseversion.edit().putInt("DATABASE_VERSION",DATABASE_VERSION).commit();
//                mDbHelper = new MoneyDbHelper(getActivity());
//                db = mDbHelper.getReadableDatabase();
                String SQL_CREATE_TABLE =  "CREATE TABLE " + MoneyContract.MoneyEntry.TABLE_NAME + " ("
                        + MoneyContract.MoneyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + MoneyContract.MoneyEntry.COLUMN_MONEY_VALUE + " DOUBLE NOT NULL, "
                        + MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS + " INTEGER NOT NULL DEFAULT 0, "
                        + MoneyContract.MoneyEntry.COLUMN_MONEY_DESC + " TEXT, "
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

    public boolean noEntriesExistToday(){
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" =?";
        String[] ARGS = {currentDate};
        MoneyDbHelper mDbHelper = new MoneyDbHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, MoneyContract.MoneyEntry.TABLE_NAME,SELECTION,ARGS);
        if(numRows == 0)
            return true;
        else return false;
    }

    public String getSumYesterdaySpent(){
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

    public String getSumYesterdayReceived() {
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

    public String getSumMonthlySpent(){
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
//        String date = String.valueOf(Integer.parseInt(currentDate.substring(0,2))-1);
        String monthAndYear = currentDate.substring(2);
        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" LIKE? AND "+ MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS+" =?";
        String[] ARGS = {"%"+monthAndYear,"1"};
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
//        Cursor cur = db.rawQuery("SELECT SUM(value) FROM today WHERE status = 1 AND date LIKE %"+monthAndYear, null);
        if(cur.moveToFirst())
        {
            str = String.valueOf(cur.getDouble(0));
            cur.close();
        }
        return str;
    }

    public String getSumMonthlyReceived() {
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
//        String date = String.valueOf(Integer.parseInt(currentDate.substring(0,2))-1);
        String monthAndYear = currentDate.substring(2);
        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" LIKE? AND "+ MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS+" =?";
        String[] ARGS = {"%"+monthAndYear,"2"};
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
//        Cursor cur = db.rawQuery("SELECT SUM(value) FROM today WHERE status = 2 AND date LIKE %"+monthAndYear, null);
        if(cur.moveToFirst())
        {
            sumReceived = String.valueOf(cur.getDouble(0));
            cur.close();
        }
        return sumReceived;
    }

    public boolean noEntriesExistMonth(){
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
//        String date = String.valueOf(Integer.parseInt(currentDate.substring(0,2))-1);
        String monthAndYear = currentDate.substring(2);
        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" LIKE?";
        String[] ARGS = {"%"+monthAndYear};
        MoneyDbHelper mDbHelper = new MoneyDbHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int numRows = (int)DatabaseUtils.queryNumEntries(db, MoneyContract.MoneyEntry.TABLE_NAME,SELECTION,ARGS);
        if(numRows == 0)
            return true;
        else return false;
    }

    public boolean noEntriesExistYesterday(){
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

    public String getSumYearSpent(){
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
//        String date = String.valueOf(Integer.parseInt(currentDate.substring(0,2))-1);
        String year = currentDate.substring(5);
        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" LIKE? AND "+ MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS+" =?";
        String[] ARGS = {"%"+year,"1"};
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

    public String getSumYearReceived() {
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
//        String date = String.valueOf(Integer.parseInt(currentDate.substring(0,2))-1);
        String year = currentDate.substring(5);
        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" LIKE? AND "+ MoneyContract.MoneyEntry.COLUMN_MONEY_STATUS+" =?";
        String[] ARGS = {"%"+year,"2"};
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
//        Cursor cur = db.rawQuery("SELECT SUM(value) FROM today WHERE status = 2 AND date LIKE %"+year, null);
        if(cur.moveToFirst())
        {
            sumReceived = String.valueOf(cur.getDouble(0));
            cur.close();
        }
        return sumReceived;
    }

    public boolean noEntriesExistYear(){
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
//        String date = String.valueOf(Integer.parseInt(currentDate.substring(0,2))-1);
        String year = currentDate.substring(5);
        String SELECTION = MoneyContract.MoneyEntry.COLUMN_MONEY_DATE+" LIKE?";
        String[] ARGS = {"%"+year};
        MoneyDbHelper mDbHelper = new MoneyDbHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int numRows = (int)DatabaseUtils.queryNumEntries(db, MoneyContract.MoneyEntry.TABLE_NAME,SELECTION,ARGS);
        if(numRows == 0)
            return true;
        else return false;
    }
}
