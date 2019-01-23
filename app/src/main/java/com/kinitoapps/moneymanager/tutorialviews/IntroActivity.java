package com.kinitoapps.moneymanager.tutorialviews;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.kinitoapps.moneymanager.R;
import com.kinitoapps.moneymanager.home;

/**
 * Created by HP INDIA on 17-Mar-18.
 */
public class IntroActivity extends AppIntro {
    boolean openedFromSettings = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if(b!=null)
        {
            if(i.getStringExtra("openedFromSettings").equals("yes"))
                openedFromSettings = true;
        }
        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
//        addSlide(firstFragment);
//        addSlide(secondFragment);
//        addSlide(thirdFragment);
//        addSlide(fourthFragment);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("Welcome to Money Manager", "Manage all your expenses efficiently", R.drawable.ic_wallet_light, Color.parseColor("#6790A4")));
        addSlide(AppIntroFragment.newInstance("Shortcuts for adding entries quickly", "Add entries quickly using the home shortcut or the quick entry notification (Settings > Quick Entry Notification)", R.drawable.shortcut, Color.parseColor("#6790A4")));
        addSlide(AppIntroFragment.newInstance("Review expenses for any date", "You can view your expenses for any date in addition to reviewing your daily, monthly and yearly activity", R.drawable.ic_calendar, Color.parseColor("#6790A4")));
        addSlide(AppIntroFragment.newInstance("Set daily and monthly spending limits", "Set a limit on how much you want to spend everyday and/or every month so when you cross that limit, Money Manager will let you know", R.drawable.ic_limit, Color.parseColor("#6790A4")));
        setCustomTransformer(new ZoomOutPageTransformer());



        // OPTIONAL METHODS
        // Override bar/separator color.
//        setBarColor(Color.parseColor("#3F51B5"));
//        setSeparatorColor(Color.parseColor("#2196F3"));
        // Hide Skip/Done button.
        showSkipButton(false);
//        setProgressButtonEnabled(false);
        setDoneText("GET STARTED");
        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
//        setVibrate(true);
//        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        if(!openedFromSettings) {
            Intent intent = new Intent(this, home.class);
            startActivity(intent);
            finish();
        }
        else
            finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}